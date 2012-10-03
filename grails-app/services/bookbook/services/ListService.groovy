package bookbook.services

import bookbook.domain.Book
import bookbook.domain.BookList
import bookbook.domain.User
import grails.converters.JSON
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.index.IndexHits
import org.apache.commons.logging.LogFactory

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
		log.debug "############### initialize() in ListService - Loading graphDb, shutdownHook."
		//graphDb = new EmbeddedGraphDatabase( DB_PATH );
		//registerShutdownHook();
		listIndex = graphDb.index().forRelationships("lists")
		listsReferenceNode = this.getSubReferenceNode(RelTypes.LISTS_REFERENCE)
		
	}
	
	@PreDestroy
	def cleanUp() {
		log.debug "############### cleanUp()  - Shutting down graphDb."
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
		
		// validation - check to see if this book has already been added
		IndexHits<Relationship> hits = listIndex.query(null,null,u.underlyingNode, b.underlyingNode)
		while(hits.hasNext()) {
			Relationship rel = hits.next()			
			if(rel)
			{
				BookList hit = new BookList(rel)
				if(hit.type.equals(relType)) {
					hits.close()
					log.debug "### book & list & user combination already exists!  sending error back to controller ###"
					return false
				}
			}
		}
		hits.close()
		
		//establish new relationship
		Transaction tx = graphDb.beginTx()
		try {
			
			// get new ID 
			Relationship rel = u.underlyingNode.createRelationshipTo(b.underlyingNode, RelTypes.valueOf(relType))
			bl = new BookList(rel)
			bl.with {
				type = relType
				createDate = new Date().toString()
				title = title
				bookListId = getNextId() 
				return it
			}
			log.debug "booklistobject:" + bl
			
			// add to index
			//def listIndex = graphDb.index().forRelationships("lists")
			log.debug "bookListId - ${bl.bookListId}"
			log.debug "title - ${bl.title}"
			
			listIndex.add(bl.underlyingRel, "id", bl.bookListId)
			listIndex.add(bl.underlyingRel, "bookId", b.bookId)
			listIndex.add(bl.underlyingRel, "userId", u.userId)
			
			tx.success()
		}
		catch(Exception e) {
			log.debug e.toString()
			log.debug e.printStackTrace()
			tx.failure()
			
			return false
		}
		finally {
			tx.finish()
		}
		log.debug "list creation successful."
		return bl
	}
	
	def deleteListEntry(bookListId) {
		//def index = graphDb.index().forRelationships("lists")
		IndexHits<Relationship> rels = listIndex.get("id", bookListId)
		Relationship rel = rels.getSingle()
		
		Transaction tx = graphDb.beginTx()
		try {
			rel.delete()
			listIndex.remove(rel)
			tx.success()
		}
		catch(e) {
			tx.failure()
			log.error e.toString()
			return false
		}
		finally {
			tx.finish()
		}
		return true
	}
	
	def findListsByBookId(bookId, listType) {
		//def listIndex = graphDb.index().forRelationships("lists")
		def b = bookService.findBooksByProperty("id", bookId)
		def rels = listIndex.query(null, null, b.underlyingNode)
		def allLists = []
		def relsToDelete = []
		for(rel in rels) {
			BookList bl = new BookList(rel)
			
			if(!bl.bookListId) { // cleanup - delete invalid booklists
				log.error "Found an invalid book list.  Adding to queue for deletion."
				relsToDelete.push rel
			}
			else {
				allLists.push(bl)
			}
		}
		if(relsToDelete) {
			log.debug("###########################################")
			log.debug("#### cleaning up invalid booklists.... ####")
			for(rel in rels) {
				Transaction tx = graphDb.beginTx()
				try {
					rel.delete()
					log.debug("after deleting rel")
					listIndex.remove(rel)
					log.debug("after deleting index entry")
					tx.success()
				}
				catch(e) {
					tx.failure()
					log.error e.toString()
				}
				finally {
					tx.finish()
				}
			}
		}
		log.debug "found [${allLists.size()}] lists for book [${b.title}] as End Node"
		
		// rje: deleted some code that pulls from the actual graph - see git history
		
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
		log.debug "found [${allLists.size()}] lists for user [${u.userName}] as End Node"
		
		// rje: deleted some code that pulls from the actual graph - see git history
		
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
			counter = 1L;
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
		log.debug "in getNextId()"
		def counter = null;
		try
		{
			counter = referenceNode.getProperty( keyCounter );
			log.debug "counter: ${counter}"
		}
		catch ( e )
		{
			// Create a new counter
			counter = 1L; // RJE: not sure why, but adding the first list did not work until I changed this from 0L to 1L
			log.debug "counter2: ${counter}"
		}
		
		referenceNode.setProperty( keyCounter, new Long( counter + 1 ) );
		return counter;
	}
}
