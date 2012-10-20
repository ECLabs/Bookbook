package bookbook.services

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Path
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.index.RelationshipIndex
import org.neo4j.graphdb.traversal.TraversalDescription
import org.neo4j.helpers.collection.IterableWrapper
import org.neo4j.kernel.Traversal

import bookbook.domain.Book
import bookbook.domain.CheckIn
import bookbook.domain.Opinion;
import bookbook.domain.User

class CheckinService {
	
	static transactional = false
	
	//neo4j
	def graphDb
	def DB_PATH = "bookbook-neo4j-store"
	def USERNAME_KEY = "username"
	def KEY_COUNTER = "check_in_key_counter"
	
	RelationshipIndex checkInIndex
	
	def userService
	def bookService
	def opinionService
	
	@PostConstruct
	def initialize() {
		log.debug "############### initialize() in CheckinService - Loading graphDb, shutdownHook."
		checkInIndex = graphDb.index().forRelationships("checkIns")

		
	}
	
	@PreDestroy
	def cleanUp() {
		log.debug "############### cleanUp()  - Shutting down graphDb."
	}
	
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		CHECK_INS,
		NEXT_CHECKIN_FOR_BOOK,
		NEXT_CHECKIN_FOR_USER,
		CHECKINS_REFERENCE
	}
	
	def addCheckin(data, bookId, userId) {
		log.debug "in addCbeckin()"
		
		CheckIn newCheckin = null
		Transaction tx = graphDb.beginTx()
		try {
			
			// get the book and user objects
			User u = userService.findUsersByProperty("id", userId)
			Book b = bookService.findBooksByProperty("id", bookId)
			
			log.debug "<<<< Creating check-in for userId:${u.userId} and bookId:${b.bookId} >>>>"
			
			// make sure we only have one book
			if(b instanceof java.util.List && b.size() > 0) {
				b = b.first
			}
					
			// create the new checkin node
			CheckinFactory factory = new CheckinFactory(graphDb, checkInIndex)
			newCheckin = factory.createCheckin().with {
				checkInDate = new Date().toString()
				createDate = new Date().toString()
				narrative = data.narrative
				venue = data.venue
				chapterOrSection = data.chapterOrSection
				latitude = data.latitude
				longitude = data.longitude
				index = this.checkInIndex
				//book = b
				user = u
				return it
			}
			
			def rel1 = addNodeToLinkedList(b.underlyingNode, newCheckin.underlyingNode, "CHECK_INS", "NEXT_CHECKIN_FOR_BOOK")
			def rel2 = addNodeToLinkedList(u.underlyingNode, newCheckin.underlyingNode, "CHECK_INS", "NEXT_CHECKIN_FOR_USER")
			

			checkInIndex.add(rel2, "userId", u.userId)
			checkInIndex.add(rel1, "bookId", b.bookId)
			
			newCheckin.opinion = opinionService.addOpinion(data.narrative, b, u, newCheckin)
			
			log.debug "Check-in date: ${newCheckin.checkInDate.toString()}"
			log.debug "SUCCESS -----> check-in created successfully!!"
			tx.success()
		}
		catch(e) {
			tx.failure()
			log.error e.toString()
			log.error e.printStackTrace()
			return false
		}
		finally {
			tx.finish()
		}
		return newCheckin
	}
	
	def addNodeToLinkedList(Node rootNode, Node newNode, String rootRelType, String nextRelType) {
		
		def rels = rootNode.getRelationships(RelTypes.valueOf(rootRelType), Direction.OUTGOING)
		if(rels.hasNext()) {
			
			// there's an existing root node, so we need to subordinate it to the new one
			def rootRel = rels.next();
			def firstNode = rootRel.getEndNode()
			
			// delete old relationship to the book
			rootRel.delete()
			
			// create relationship from the new node to the orig one with reltype = NEXT_OPINION_FOR_BOOK
			log.debug "about to create 1st relationship"
			newNode.createRelationshipTo(firstNode, RelTypes.valueOf(nextRelType))
		}

		// create relationship from book to new opinion node
		log.debug "about to create relationship from book to new opinion node"
		rootNode.createRelationshipTo(newNode, RelTypes.valueOf(rootRelType))
	}
	
	def findCheckInsByBookId(bookId) {
		log.debug "<< Looking for check-ins for bookId:${bookId}"
		HashSet<CheckIn> allCheckIns = new HashSet<CheckIn>();
		def b = bookService.findBooksByProperty("id", bookId)

		// get the first checkin and add to list
		CheckIn firstCheckin = null;
		def rels = b.underlyingNode.getRelationships(RelTypes.CHECK_INS, Direction.OUTGOING)
		if(!rels.hasNext()) {
			return [];
		}	
		firstCheckin = new CheckIn(rels.next().getEndNode(), (Book) b)
		allCheckIns.add(firstCheckin)	
				
		
		// create traverser and all all to the hashset	
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_CHECKIN_FOR_BOOK);
		
		Iterable<CheckIn> checkinIterator = new IterableWrapper<CheckIn, Path>(
                traversal.traverse( firstCheckin.underlyingNode ) ) {
				
            @Override
			protected CheckIn underlyingObjectToObject( Path path )
            {
				def rels2 = path.startNode().getRelationships(RelTypes.CHECK_INS, Direction.INCOMING)
				if(rels2.hasNext()) {
					return new CheckIn( path.endNode(),  new Book(rels2.next().getStartNode()));
				}
                return new CheckIn( path.endNode() );
            }
        };
	
		Iterator<CheckIn> it = checkinIterator.iterator()
		while(it.hasNext()) {
			allCheckIns.add(it.next())
		}
			
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
	
	def getUserCheckins(User user) {
		Node startNode = null;
		
		def rels = user.underlyingNode.getRelationships(RelTypes.CHECK_INS, Direction.OUTGOING)
		if(rels.hasNext()) {
			def rel = rels.next()
			startNode = rel.getEndNode()
		}
		else {
			return null
		}
	
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_CHECKIN_FOR_USER);
		
		Iterable<CheckIn> checkinIterator = new IterableWrapper<CheckIn, Path>(
				traversal.traverse( startNode ) ) {
				
			@Override
			protected CheckIn underlyingObjectToObject( Path path )
			{
				def rels2 = path.startNode().getRelationships(RelTypes.CHECK_INS, Direction.INCOMING)
				if(rels2.hasNext()) {
					return new CheckIn( path.endNode(),  new User(rels2.next().getStartNode()));
				}
				return new CheckIn( path.endNode() );
			}
		};
	
		return checkinIterator
		
	}
	
	def getBookCheckins(Book book) {
		Node startNode = null;
		
		def rels = book.underlyingNode.getRelationships(RelTypes.CHECK_INS, Direction.OUTGOING)
		if(rels.hasNext()) {
			def rel = rels.next()
			startNode = rel.getEndNode()
		}
		else {
			return null
		}
	
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_CHECKIN_FOR_BOOK);
		
		Iterable<CheckIn> checkinIterator = new IterableWrapper<CheckIn, Path>(
				traversal.traverse( startNode ) ) {
				
			@Override
			protected CheckIn underlyingObjectToObject( Path path )
			{
				def rels2 = path.startNode().getRelationships(RelTypes.CHECK_INS, Direction.INCOMING)
				if(rels2.hasNext()) {
					return new CheckIn( path.endNode(),  new Book(rels2.next().getStartNode()));
				}
				return new CheckIn( path.endNode() );
			}
		};
	
		return checkinIterator
		
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
