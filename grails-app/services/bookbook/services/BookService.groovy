package bookbook.services

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

import java.beans.java_awt_BorderLayout_PersistenceDelegate;

import groovyx.net.http.HTTPBuilder

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.ReturnableEvaluator
import org.neo4j.graphdb.StopEvaluator
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.TraversalPosition
import org.neo4j.graphdb.Traverser.Order
import org.neo4j.graphdb.index.*
import org.neo4j.kernel.EmbeddedGraphDatabase

import org.apache.commons.logging.LogFactory

import bookbook.domain.Book
import bookbook.domain.CheckIn
import bookbook.domain.GoogleBook
import bookbook.domain.User

class BookService {
	
	private static final log = LogFactory.getLog(this)

	// Google Books API Oauth2
    static transactional = false
	def NETWORK_NAME = "Google"
	def CLIENT_ID = "690667165235.apps.googleusercontent.com"
	def CLIENT_SECRET = "SnAOp_UYXQtrJECyYJJt8ET7"
	def REDIRECT_URI = "http://localhost:8080/TimeWorks/googlebooks/getBooks"
	def SCOPE = "https://www.googleapis.com/auth/books"
	def RESPONSE_TYPE = "code"
	def AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=${SCOPE}&response_type=${RESPONSE_TYPE}";
	def API_KEY = "AIzaSyCmCwkxWuUuSSOSneMPBA3vPF2UWNfwr_E"
	def PROTECTED_RESOURCE_URL = "https://www.googleapis.com/";
	
	//neo4j
	def graphDb
	def DB_PATH = "bookbook-neo4j-store"
	def USERNAME_KEY = "username"
	def CHECK_IN_KEY_COUNTER = "check_in_key_counter"
	
	def userService
	
	@PostConstruct
	def initialize() {
		log.debug "############### initialize() in BookService - Loading graphDb, shutdownHook."
		//graphDb = new EmbeddedGraphDatabase( DB_PATH );
		//registerShutdownHook();
		
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
		CHECK_IN
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
	
    def findGoogleBooksByTitle(query, pageNumber) {
		return findGoogleBooks("intitle", query, 0, 10)
    }
	def findGoogleBooksByAuthor(query, pageNumber) {
		return findGoogleBooks("inauthor", query, 0, 10)
	}

	def findAllBooks() {
		
		Node brefNode = getBooksReferenceNode()

		def trav = brefNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
			new ReturnableEvaluator()
			{
				public boolean isReturnableNode( TraversalPosition pos )
				{
					return !pos.isStartNode();
				}
			},
			RelTypes.BOOK, Direction.OUTGOING
		);
		
		def allBooks = []
		for(node in trav) {
			allBooks.push(new Book(node))
		}
		log.debug "got this number of books" + allBooks.size()
		return allBooks
	}
	
	
	def findBooksByProperty(property, value) {
		log.debug "in findBooksByProperty(), looking for property [ $property ] with value [ $value ]"
		
		def bookIndex = graphDb.index().forNodes('books')
		if(property.equals('id'))
		{
			IndexHits<Node> hits = bookIndex.get(property, value)
			if(hits.hasNext()) {
				Node bookNode = hits.next()
				hits.close()
				if(bookNode)
				{
					log.debug "### book found in index ###"
					def book = new Book(bookNode)
					return book
				}
			}
			
			//The comparator in the traversal below needs value to be a Long
			value = Long.valueOf(value);
			
		}
		
		Node brefNode = getBooksReferenceNode()
		def trav = brefNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
			new ReturnableEvaluator()
			{
				public boolean isReturnableNode( TraversalPosition pos )
				{
					log.debug "current value = " + pos.currentNode().getProperty(property, null)
					if(value instanceof java.lang.String && !property.toLowerCase().equals("isbn")) {
						return !pos.isStartNode() &&
							pos.currentNode().getProperty(property, null).toLowerCase().contains(value.toLowerCase())
					}
					def currentValue = pos.currentNode().getProperty(property, null) == null ? "" : pos.currentNode().getProperty(property, null)
					return !pos.isStartNode() && currentValue.equals(value)
				}
			},
			RelTypes.BOOK, Direction.OUTGOING
		);
		
		def allBooks = []
		
		Transaction tx = graphDb.beginTx()
		try {
			for(node in trav) {
				def book = new Book(node)
				allBooks.push(book)
				if(property.equals("id")) {
					log.debug "!!! adding book with id [${book.bookId}] to index !!!"
					bookIndex.add node, property, book.bookId		
				}
					
			}
			tx.success()
		}
		finally {
			tx.finish()
		}
		
		log.debug "found ${allBooks.size()}"
		return allBooks
	}
	
	def addBook(GoogleBook b) {
		log.debug "in addBook()"
		
		Transaction tx = graphDb.beginTx()
		try {
			// strip any leading 0's on the ISBN appropriately before saving and comparing
			b.isbn10 = b.isbn10?.replaceFirst("^0+", "")
			
			// check to see if the book was already added
			def matches = findBooksByProperty("isbn10", b.isbn10).size()
			if(matches > 0)
			{
				log.debug "Could not add book.  $matches Book(s) with isbn10 of ${b.isbn10} already exist"
				return -1
			}
			
			BookFactory bookFactory = new BookFactory(graphDb)
			Book bookbook = bookFactory.createBook().with {
				title = b.title
				author = b.author
				description = b.description
				isbn10 = b.isbn10
				smallThumbnailUrl = b.smallThumbnailUrl
				thumbnailUrl = b.thumbnailUrl
				pubType = b.pubType
				createDate = new Date().toString()
				creatorUserId = b.creatorUserId
				return it
			}
			tx.success()
			return bookbook
		}
		finally {
			tx.finish()
		}
	}
	
	def updateBook(GoogleBook b, id) {
		log.debug "in updateBook() - b.bookId = ${b.bookId} & id = ${id}"	
		
		Transaction tx = graphDb.beginTx()
		try {			
			// strip any leading 0's on the ISBN appropriately before saving and comparing
			b.isbn10 = b.isbn10?.replaceFirst("^0+", "")
			
			def bookId = b.bookId
			// make sure the id matches
			if(!id.equals(bookId)) {
				log.debug "Could not update.. id did not match"
				return null
			}
			
			// check to see if the book was already added
			def bookbook = findBooksByProperty("id", b.bookId)
			if(!bookbook)
			{
				log.debug "Could not update.. book not found"
				return null
			} 
			
			bookbook.with {
				title = b.title
				author = b.author
				description = b.description
				isbn10 = b.isbn10
				smallThumbnailUrl = b.smallThumbnailUrl
				thumbnailUrl = b.thumbnailUrl
				pubType = b.pubType
				// don't update createDate nor bookId
				return it
			}			

			// TODO: no need to updated the index yet, only ID is in index
			tx.success()
			return bookbook
		}
		finally {
			tx.finish()
		}
	}
	
	def deleteBook(bookId) {
		log.debug "in deleteBook() with id [ $bookId ] "
		Transaction tx = graphDb.beginTx()
		try {
			// get the book
			def bookbook = findBooksByProperty("id", bookId)
			if(!bookbook)
			{
				log.debug "Could not delete.. book not found"
				return null
			}
			
			// delete node
			log.debug "Deleting book from graph"
			bookbook.underlyingNode.delete()
			for(rel in bookbook.underlyingNode.getRelationships())
			{
				rel.delete()
			}
			
			// delete node from index
			log.debug "Deleting book from index"
			def bookIndex = graphDb.index().forNodes("books")
			bookIndex.remove(bookbook.underlyingNode)
			
			tx.success()
		}
		finally {
			tx.finish()
		}
	}
	
	def createCheckIn(data, bookId, userId) {
		
		log.debug "data-" + data.toString()
		CheckIn ci = null
		User u = userService.findUsersByProperty("id", userId)
		Book b = this.findBooksByProperty("id", bookId)
		
		// check for existing relationship
		/* allow more than one check-in for the same book/user combo
		def existingCheckIns = u.underlyingNode.getRelationships(RelTypes.CHECK_IN, Direction.OUTGOING)
		log.debug "existingCheckIns: ${existingCheckIns}"
		for(checkInRel in existingCheckIns) {
			
			log.debug "relationship bookId [${checkInRel.getEndNode()}]"
			if(checkInRel.getEndNode().equals(b.underlyingNode)) {
				// check-in already exists
				log.debug "check-in already exists between book:[${b.bookId}] and user:[${u.userId}]"
				return false
			}
		}
		*/
		Transaction tx = graphDb.beginTx()
		try {
			Relationship rel = u.underlyingNode.createRelationshipTo(b.underlyingNode, RelTypes.CHECK_IN)
			ci = new CheckIn(rel).with {
				checkInDate = new Date().toString()
				createDate = new Date().toString()
				narrative = data.narrative
				venue = data.venue
				chapterOrSection = data.chapterOrSection
				latitude = data.latitude
				longitude = data.longitude
				return it
			}
			log.debug "checkinobject:" + ci
			
			// get id
			def counter = null;
			try
			{
				counter = booksReferenceNode.getProperty( CHECK_IN_KEY_COUNTER );
			}
			catch ( e )
			{
				// Create a new counter
				counter = 0L;
			}
			booksReferenceNode.setProperty( CHECK_IN_KEY_COUNTER, new Long( counter + 1 ) );
			
			ci.checkInId = counter
			
			// add to index
			def checkInIndex = graphDb.index().forRelationships("checkIns")
			log.debug "adding checkin to index - checkInId: ${ci.checkInId}"
			checkInIndex.add(ci.underlyingRel, "id", counter)
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
		log.debug "checkin successful."
		return ci
	}
	
	def findCheckInsByUserName(userName) {
		def checkInIndex = graphDb.index().forRelationships("checkIns")
		def u = userService.findUsersByProperty("userName", userName)
		def rels = checkInIndex.query("type:check_in", u.underlyingNode, null)
		def allCheckIns = []
		for(rel in rels) {
			CheckIn ci = new CheckIn(rel)
			allCheckIns.push(ci)
		}
		log.debug "found [${allCheckIns.size()}] check-ins in index for user with userName [${u.userName}]"
		if(allCheckIns.size())
		{
			return allCheckIns
		}
		
		def allCheckIns2 = []
		def rels2 = u.underlyingNode.getRelationships(RelTypes.CHECK_IN, Direction.OUTGOING)
		for (rel2 in rels2) {
			CheckIn ci = new CheckIn(rel2)
			allCheckIns2.push(ci)
		}
		log.debug "found [${allCheckIns2.size()}] check-ins in graph for user with userName [${u.userName}]"
		
		return allCheckIns2
	}
	
	def findCheckInsByBookId(bookId) {
		def allCheckIns = []
		def checkInIndex = graphDb.index().forRelationships("checkIns")
		def b = findBooksByProperty("id", bookId)
		if(b instanceof ArrayList) return allCheckIns // TODO: clean this up
		def rels = checkInIndex.query(null, null, b.underlyingNode)
		
		for(rel in rels) {
			CheckIn ci = new CheckIn(rel)
			allCheckIns.push(ci)
		}
		log.debug "found [${allCheckIns.size()}] check-ins for book [${b.title}]"
		
		def allCheckIns2 = []
		def rels2 = b.underlyingNode.getRelationships(RelTypes.CHECK_IN, Direction.INCOMING)
		for (rel2 in rels2) {
			CheckIn ci = new CheckIn(rel2)
			allCheckIns2.push(ci) // .equals() doesn't allow duplicates
		}
		log.debug "found [${allCheckIns2.size()}] check-ins in graph for book with bookId [${b.bookId}]"
		
		return allCheckIns 
	}
	
	def deleteCheckIn(checkInId) {
		def checkInIndex = graphDb.index().forRelationships("checkIns")
		Relationship rel = checkInIndex.get("id", checkInId)
		Transaction tx = graphDb.beginTx()
		try {
			rel.delete()
			checkInIndex.remove(rel)
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
	
	def private findGoogleBooks(searchField, query, pageNumber, maxResults) {
		def http2 = new HTTPBuilder(PROTECTED_RESOURCE_URL)
		http2.get(	path:'books/v1/volumes',
					contentType:JSON,
					query:[q:searchField+":"+query, key:API_KEY, startIndex:pageNumber, maxResults:maxResults])
		{ resp2, bookData ->
			return buildBooks(bookData);
		}
	}
	def private buildBooks(bookData) {
		/**
		 * Filter out books that don't have a description, image, author or ISBN10
		 */
		def bookList = bookData.items;
		def filteredBookList = [];
		for(book in bookList)  {
			def existingBookId = null
			if(	book.volumeInfo.description &&
				book.volumeInfo.imageLinks &&
				book.volumeInfo.authors &&
				book.volumeInfo.industryIdentifiers) {
				def isbn = ""

				for(ident in book.volumeInfo.industryIdentifiers) {
					if(ident.type == 'ISBN_10') {
						isbn = ident.identifier.replaceFirst("^0+", "") // strip leading zeros
						break
					}
				}
				if(isbn) {
					Transaction tx = graphDb.beginTx()
					try {		
						filteredBookList.push new GoogleBook (
							description:book.volumeInfo.description,
							author:book.volumeInfo.authors[0],
							title:book.volumeInfo.title,
							isbn10:isbn,
							smallThumbnailUrl:book.volumeInfo.imageLinks.smallThumbnail,
							thumbnailUrl:book.volumeInfo.imageLinks.thumbnail,
							bookId:existingBookId,
							createDate:(new Date()).toString())
						tx.success()
					}
					finally {
						tx.finish()
					}
				}
			}
		}
		return filteredBookList;
	}
	
	private Node getBooksReferenceNode() {
		def rel = graphDb.getReferenceNode().getSingleRelationship(
			RelTypes.BOOKS_REFERENCE, Direction.OUTGOING );

		def booksReferenceNode
		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				booksReferenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( booksReferenceNode,
					RelTypes.BOOKS_REFERENCE );
				tx.success()
			}
			finally {
				tx.finish()
			}
		}
		else
		{
			booksReferenceNode = rel.getEndNode();
		}
			
		return booksReferenceNode;
	}
}

class BookFactory {
	def KEY_COUNTER = "key_counter"
	def graphDb
	def bookIndex
	def booksReferenceNode
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK
	}
	
	BookFactory(graphDb) {
		this.graphDb = graphDb
		this.booksReferenceNode = getBooksReferenceNode()
		this.bookIndex = graphDb.index().forNodes("books")
	}
	
	def createBook() {
		Node bookNode = graphDb.createNode()
		booksReferenceNode.createRelationshipTo(bookNode, RelTypes.BOOK)
		
		def counter = getNextId()
		Book b = new Book(bookNode).with {
			bookId = getNextId()
			return it
		}
		
		// add to index
		bookIndex.add(bookNode, "id", counter)
		return b
	}
	
	private Node getBooksReferenceNode() {
		def rel = graphDb.getReferenceNode().getSingleRelationship(
			RelTypes.BOOKS_REFERENCE, Direction.OUTGOING );

		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				booksReferenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( booksReferenceNode,
					RelTypes.BOOKS_REFERENCE );
				tx.success()
			}
			finally {
				tx.finish()
			}
		}
		else
		{
			booksReferenceNode = rel.getEndNode();
		}
			
		return booksReferenceNode;
	}
	private synchronized long getNextId()
	{
		def counter = null;
		try
		{
			counter = booksReferenceNode.getProperty( KEY_COUNTER );
		}
		catch ( e )
		{
			// Create a new counter
			counter = 0L;
		}
		
		booksReferenceNode.setProperty( KEY_COUNTER, new Long( counter + 1 ) );
		return counter;
	}
	
}
