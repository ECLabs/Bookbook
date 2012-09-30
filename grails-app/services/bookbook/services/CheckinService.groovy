package bookbook.services

import bookbook.domain.Book
import bookbook.domain.CheckIn
import bookbook.domain.GoogleBook;
import bookbook.domain.User

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.index.RelationshipIndex
import org.neo4j.graphdb.Direction

class CheckinService {
	
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
	def KEY_COUNTER = "check_in_key_counter"
	
	RelationshipIndex checkInIndex
	
	def userService
	def bookService
	
	@PostConstruct
	def initialize() {
		log.debug "############### initialize() in CheckinService - Loading graphDb, shutdownHook."
		checkInIndex = graphDb.index().forRelationships("checkIns")

		
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

    def serviceMethod() {

    }
	
	def addCheckin(data, bookId, userId) {
		log.debug "in addCbeckin()"
		
		CheckIn c = null
		Transaction tx = graphDb.beginTx()
		try {
			
			User u = userService.findUsersByProperty("id", userId)
			Book b = bookService.findBooksByProperty("id", bookId)
			
			log.debug "<<<< Creating check-in for userId:${u.userId} and bookId:${b.bookId} >>>>"
			
			// make sure we only have one book
			if(b instanceof java.util.List && b.size() > 0) {
				b = b.first
			}
						
			CheckinFactory factory = new CheckinFactory(graphDb, checkInIndex)
			
			c = factory.createCheckin().with {
				checkInDate = new Date().toString()
				createDate = new Date().toString()
				narrative = data.narrative
				venue = data.venue
				chapterOrSection = data.chapterOrSection
				latitude = data.latitude
				longitude = data.longitude
				index = this.checkInIndex
				book = b
				user = u
				return it
			}
			
			log.debug "about to create 1st relationship"
			Relationship rel1 = c.getUnderlyingNode.createRelationshipTo(b.underlyingNode, RelTypes.CHECK_IN)
			log.debug "about to create 2nd relationship"
			Relationship rel2 = u.underlyingNode.createRelationshipTo(c.underlyingNode, RelTypes.CHECK_IN)
			log.debug "after creating 2nd relationship"
			
			checkInIndex.add(rel2, "userId", u.userId)
			checkInIndex.add(rel1, "bookId", b.bookId)
			
			log.debug "Check-in date: ${c.checkInDate.toString()}"
			log.debug "SUCCESS -----> check-in created successfully!!"
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
		return c
	}
	
	def findCheckInsByBookId(bookId) {
		log.debug "<< Looking for check-ins for bookId:${bookId}"
		HashSet<CheckIn> allCheckIns = new HashSet<CheckIn>();
		def b = bookService.findBooksByProperty("id", bookId)
		def rels = checkInIndex.query("bookId", bookId) // 4th param is end node of rel
		
		for(rel in rels) {
			CheckIn ci = new CheckIn(rel.getStartNode())
			allCheckIns.add(ci)
		}
		log.debug "found [${allCheckIns.size()}] check-ins for book [${b.title}]"
		
		def rels2 = b.underlyingNode.getRelationships(RelTypes.CHECK_IN, Direction.INCOMING)
		for (rel2 in rels2) {
			CheckIn ci = new CheckIn(rel2.getStartNode())
			allCheckIns.add(ci) // .equals() doesn't allow duplicates
		}
		log.debug "found [${allCheckIns.size()}] check-ins in graph for book with bookId [${b.title}]"
		
		return allCheckIns
	}
	
	def findCheckInsByUserId(userId) {
		log.debug "<< Looking for check-ins for userId:${userId}"
		def allCheckIns = []
		def u = userService.findUsersByProperty("id", userId)
		def rels = checkInIndex.query("userId", userId) // 4th param is end node of rel
		
		for(rel in rels) {
			CheckIn ci = new CheckIn(rel.getEndNode())
			allCheckIns.push(ci)
		}
		log.debug "found [${allCheckIns.size()}] check-ins for user [$u.userName}]"
		
		/*
		def allCheckIns2 = []
		def rels2 = u.underlyingNode.getRelationships(RelTypes.CHECK_IN, Direction.OUTGOING)
		for (rel2 in rels2) {
			CheckIn ci = new CheckIn(rel2.getEndNode())
			allCheckIns.push(ci) // .equals() doesn't allow duplicates
		}
		log.debug "found [${allCheckIns2.size()}] check-ins in graph for user with userId [${u.userId}]"
		*/
		return allCheckIns
	}
}

class CheckinFactory {
	def KEY_COUNTER = "key_counter"
	def graphDb
	def checkinIndex
	def referenceNode
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		CHECK_IN,
		CHECKINS_REFERENCE
	}
	
	CheckinFactory(graphDb, index) {
		this.graphDb = graphDb
		this.referenceNode = getReferenceNode()
		this.checkinIndex = index
	}
	
	def createCheckin() {
		Node node = graphDb.createNode()
		referenceNode.createRelationshipTo(node, RelTypes.CHECK_IN)
		
		Long counter = getNextId()
		CheckIn c = new CheckIn(node, checkinIndex).with {
			checkInId = counter
			return it
		}
		return c
	}
	
	private Node getReferenceNode() {
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
			RelTypes.CHECKINS_REFERENCE, Direction.OUTGOING );

		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				referenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( referenceNode,
					RelTypes.CHECKINS_REFERENCE );
				tx.success()
			}
			finally {
				tx.finish()
			}
		}
		else
		{
			referenceNode = rel.getEndNode();
		}
			
		return referenceNode;
	}
	private synchronized long getNextId()
	{
		def counter = null;
		try
		{
			counter = referenceNode.getProperty( KEY_COUNTER );
		}
		catch ( e )
		{
			// Create a new counter
			counter = 1L;
		}
		
		referenceNode.setProperty( KEY_COUNTER, new Long( counter + 1 ) );
		return counter;
	}
	
}
