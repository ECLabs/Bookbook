package bookbook.services

import bookbook.domain.Book
import bookbook.domain.BookList
import bookbook.domain.User
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.TraversalPosition;

class ListService {

    static transactional = false

    //neo4j
	def graphDb
	def DB_PATH = "bookbook-neo4j-store"
	def KEY_COUNTER = "list_key_counter"
	def listsReferenceNode // this is only for the list key_counter
	def listIndex
	
	def bookService
	def userService
	
	@PostConstruct
	def initialize() {
		println "############### initialize() in ListService - Loading graphDb, shutdownHook."
		//graphDb = new EmbeddedGraphDatabase( DB_PATH );
		//registerShutdownHook();
		listIndex = graphDb.index().forNodes("lists")
		listsReferenceNode = this.getSubReferenceNode(RelTypes.LISTS_REFERENCE)
		
	}
	
	@PreDestroy
	def cleanUp() {
		println "############### cleanUp()  - Shutting down graphDb."
		//graphDb.shutdown();
	}
	
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		CHECK_IN,
		USER_LIST,
		LISTS_REFERENCE,
		LIKE,
		HAVE_READ,
		WANT_TO_READ
	}

	def shutdown()
	{
		graphDb.shutdown();
	}
	def registerShutdownHook()
	{
		// Registers a shutdown hook for the Neo4j and index service instances
		// so that it shuts down nicely when the VM exits (even if you
		// "Ctrl-C" the running example before it's completed)
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()	{
				shutdown();
			}
		} );
	}
	
	def addListEntry(userId, bookId, relType, title) {
		/**
		 * listTypes are: 
		 	USER_LIST,
			LIKE,
			HAVE_READ,
			WANT_TO_READ
		 */
		User u = userService.findUsersByProperty("id", userId)
		Book b = bookService.findBooksByProperty("id", bookId)
		BookList bl = null;
		
		//establish new relationship
		Transaction tx = graphDb.beginTx()
		try {
			
			// get new ID 
			Relationship rel = u.underlyingNode.createRelationshipTo(b.underlyingNode, RelTypes.valueOf(relType))
			bl = new BookList(rel).with {
				type = relType
				createDate = new Date().toString()
				title = title
				bookListId = getNextId() 
				return it
			}
			println "booklistobject:" + bl
			
			// add to index
			def listIndex = graphDb.index().forRelationships("lists")
			println "bookListId - ${bl.bookListId}"
			
			listIndex.add(bl.underlyingRel, "id", bl.bookListId)
			listIndex.add(bl.underlyingRel, "bookId", b.bookId)
			listIndex.add(bl.underlyingRel, "userId", u.userId)
			
			tx.success()
		}
		catch(Exception e) {
			println e.toString()
			println e.printStackTrace()
			tx.failure()
			
			return false
		}
		finally {
			tx.finish()
		}
		println "list creation successful."
		return bl
	}
	
	def findListsByBookId(bookId, listType) {
		def listIndex = graphDb.index().forRelationships("lists")
		def b = bookService.findBooksByProperty("id", bookId)
		def rels = listIndex.query(null, null, b.underlyingNode)
		def allLists = []
		for(rel in rels) {
			BookList bl = new BookList(rel)
			allLists.push(bl)
		}
		println "found [${allLists.size()}] lists for book [${b.title}] as End Node"
		
		/*
		def allLists2 = []
		def rels2 = b.underlyingNode.getRelationships(RelTypes.valueOf(listType), Direction.INCOMING)
		for (rel2 in rels2) {
			BookList bl = new BookList(rel2)
			allLists2.push(bl)
		}
		println "found [${allLists2.size()}] lists in graph for book with bookId [${b.bookId}] as Start Node"
		*/
		return allLists
	}
	
	def findListsByUserId(userId, listType) {
		def listIndex = graphDb.index().forRelationships("lists")
		def u = userService.findUsersByProperty("id", userId)
		def rels = listIndex.query(null, u.underlyingNode, null)
		Set allLists = new HashSet()
		for(rel in rels) {
			BookList bl = new BookList(rel)
			allLists.add(bl)
		}
		println "found [${allLists.size()}] lists for user [${u.userName}] as End Node"
		/* The index is working 
		def allLists2 = []
		def rels2 = u.underlyingNode.getRelationships(RelTypes.valueOf(listType), Direction.OUTGOING)
		for (rel2 in rels2) {
			BookList bl = new BookList(rel2)
			allLists2.push(bl)
		}
		println "found [${allLists2.size()}] lists in graph for user with userId [${u.userId}] as Start Node"
		
		allLists.addAll(allLists2)
		*/
		return allLists
	}
	
	def findBooksByListTypeForUser(userId, listType) {
		def allLists = this.findListsByUserId(userId, listType)
		def books = []
		for (myList in allLists) {
			Book b = new Book(myList.underlyingRel.getEndNode());
			books.push(b);
		}
		return books
		
	}
	
	def findUsersByBookAndListType(bookId, listType) {
		def allLists = this.findListsByBookId(bookId, listType)
		def users = []
		for (myList in allLists) {
			User u = new Book(myList.underlyingRel.getStartNode());
			users.push(u);
		}
		return users
	}
	
	
	def getNextId() {
		// get id
		def counter = null;
		try
		{
			counter = listsReferenceNode.getProperty( KEY_COUNTER );
		}
		catch ( e )
		{
			// Create a new counter
			counter = 0L;
		}
		listsReferenceNode.setProperty( KEY_COUNTER, new Long( counter + 1 ) );
		return counter;
	}
	
	private Node getSubReferenceNode(relType) {
		def rel = graphDb.getReferenceNode().getSingleRelationship(
			relType, Direction.OUTGOING );

		def subReferenceNode = null;
		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				subReferenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( subReferenceNode,
					relType );
				tx.success()
			}
			finally {
				tx.finish()
			}
		}
		else
		{
			subReferenceNode = rel.getEndNode();
		}
			
		return subReferenceNode;
	}
	
	private synchronized Long getNextId(keyCounter)
	{
		println "in getNextId()"
		def counter = null;
		try
		{
			counter = referenceNode.getProperty( keyCounter );
			println "counter: ${counter}"
		}
		catch ( e )
		{
			// Create a new counter
			counter = 0L;
			println "counter2: ${counter}"
		}
		
		referenceNode.setProperty( keyCounter, new Long( counter + 1 ) );
		return counter;
	}
}
