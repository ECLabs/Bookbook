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
import org.neo4j.graphdb.index.IndexHits
import org.neo4j.graphdb.traversal.TraversalDescription
import org.neo4j.helpers.collection.IterableWrapper
import org.neo4j.kernel.Traversal

import bookbook.domain.Book
import bookbook.domain.CheckIn
import bookbook.domain.Opinion
import bookbook.domain.User

class OpinionService {
	static transactional = false
	
	//neo4j
	def graphDb
	def DB_PATH = "bookbook-neo4j-store"
	def KEY_COUNTER = "opinion_key_counter"
	
	def opinionIndex
	def bookService
	
	@PostConstruct
	def initialize() {
		log.debug "############### initialize() in OpinionService ##############"
		opinionIndex = graphDb.index().forNodes("opinions")
	}
	
	@PreDestroy
	def cleanUp() {
		log.debug "############### cleanUp() in OpinionService #################"
	}
	
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		CHECK_IN,
		OPINIONS,
		NEXT_OPINION_FOR_BOOK,
		NEXT_OPINION_FOR_USER,
		CHECK_IN_OPINION,
		OPINION_REFERENCE
	}
	
	def addOpinion(String opinionText, Book book, User user, CheckIn checkIn) {
		log.debug "in addOpinion() with opinionText ${opinionText}"
		
		Opinion newOpinion = null
		Transaction tx = graphDb.beginTx()
		try {
			
			OpinionFactory factory = new OpinionFactory(graphDb, opinionIndex)
			
			newOpinion = factory.createOpinion().with {
				createDate = new Date().toString()
				text = opinionText
				book = book
				user = user
				checkIn = checkIn
				return it
			}
			
			addNodeToLinkedList(book.underlyingNode, newOpinion.underlyingNode, "OPINIONS", "NEXT_OPINION_FOR_BOOK")
			addNodeToLinkedList(user.underlyingNode, newOpinion.underlyingNode, "OPINIONS", "NEXT_OPINION_FOR_USER")
			
			//user.underlyingNode.createRelationshipTo(newOpinion.underlyingNode, RelTypes.OPINION_BY)

			if(checkIn) {
				log.debug "about to create check-in relationship"
				newOpinion.underlyingNode.createRelationshipTo(checkIn.underlyingNode, RelTypes.CHECK_IN_OPINION)
			}
			
			opinionIndex.add(newOpinion.underlyingNode, "opinionId", newOpinion.opinionId)
			opinionIndex.add(newOpinion.underlyingNode, "bookId", book.bookId)
			
			log.debug "SUCCESS -----> opinion created successfully!!"
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
		return newOpinion
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
	
	def findByBookId(bookId) {
		log.debug "<< Looking for opinions for bookId:${bookId}"
		HashSet<Opinion> allOpinions = new HashSet<Opinion>();
		def b = bookService.findBooksByProperty("id", bookId)
		
		if(!b) {	
			return []
		}
		// get the first opinion and add to list
		Opinion firstOpinion = null;
		def rels = b.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.OUTGOING)
		if(!rels.hasNext()) {
			return [];
		}
		firstOpinion = new Opinion(rels.next().getEndNode(), (Book) b)
		allOpinions.add(firstOpinion)				
		
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_OPINION_FOR_BOOK);
		
		Iterable<Opinion> opinionIterator = new IterableWrapper<Opinion, Path>(
				traversal.traverse( firstOpinion.underlyingNode ) ) {
				
			@Override
			protected Opinion underlyingObjectToObject( Path path )
			{
				def rels2 = path.startNode().getRelationships(RelTypes.OPINIONS, Direction.INCOMING)
				if(rels2.hasNext()) {
					return new Opinion( path.endNode(),  new Book(rels2.next().getStartNode()));
				}
				return new Opinion( path.endNode() );
			}
		};
	
		Iterator<Opinion> it = opinionIterator.iterator()
		while(it.hasNext()) {
			allOpinions.add(it.next())
		}
			
		return allOpinions
		
	}
	
	def getUserOpinions(User user) {
		Node startNode = null;
		
		def rels = user.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.OUTGOING)
		if(rels.hasNext()) {
			def userRel = rels.next()
			startNode = userRel.getEndNode()
		}
		else {
			return null
		}
		
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_OPINION_FOR_USER);
		
		Iterable<Opinion> opinionIterator = new IterableWrapper<Opinion, Path>(
				traversal.traverse( startNode ) ) {
				
			@Override
			protected Opinion underlyingObjectToObject( Path path )
			{
				return new Opinion( path.endNode(), user );
			}
		};
		
	}
	
	def getBookOpinions(Book book) {
		Node startNode = null;
		
		def rels = book.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.OUTGOING)
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
		relationships( RelTypes.NEXT_OPINION_FOR_BOOK);
		
		Iterable<Opinion> opinionIterator = new IterableWrapper<Opinion, Path>(
				traversal.traverse( startNode ) ) {
				
			@Override
			protected Opinion underlyingObjectToObject( Path path )
			{
				def rels2 = path.startNode().getRelationships(RelTypes.OPINIONS, Direction.INCOMING)
				if(rels2.hasNext()) {
					return new Opinion( path.endNode(), book);
				}
				return new Opinion( path.endNode() );
			}
		};
	
		return opinionIterator
		
	}
	
	def findById(opinionId) {
		return findOpinionByProperty("opinionId", opinionId)
	}
	
	def findByCheckInId(checkInId) {
		return findOpinionByProperty("checkInId", checkInId)
	}
	
	def findOpinionByProperty(property, value) {
		log.debug "in findOpinionByProperty(), looking for property [ $property ] with value [ $value ]"
		
		def allOpinions = []
		if(property.equals('opinionId') || property.equals('checkInId') || property.equals('bookId')) {
			IndexHits<Node> hits = opinionIndex.get(property, value)
			while(hits.hasNext()) {
				Node node = hits.next()				
				if(node) {
					log.debug "### opinion found in index ###"
					def opinion = new Opinion(node)
					allOpinions.add(opinion)
				}
			}
			hits.close()
			
		}
		return allOpinions
	}
	
	def findAllOpinions() {
		HashSet<Opinion> allOpinions = new HashSet<Opinion>()
		def books = bookService.findAllBooks()
		for(b in books) {
			allOpinions.addAll(findByBookId(b.bookId))
		}
		return allOpinions		
	}
	
	def deleteOpinion(opinionId) {
		println "deleting opinion with opinionId = " + opinionId
		Transaction tx = graphDb.beginTx()
		try {
			ArrayList<Opinion> ops = findOpinionByProperty("opinionId", opinionId)
			Opinion op = ops[0]
			println "ID is " + op.opinionId
			
			def relsFromBook = op.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.INCOMING)
			def relsFromOpinions = op.underlyingNode.getRelationships(RelTypes.NEXT_OPINION_FOR_BOOK, Direction.INCOMING)
			def relsToOpinions = op.underlyingNode.getRelationships(RelTypes.NEXT_OPINION_FOR_BOOK, Direction.OUTGOING)
			def relsFromUser = op.underlyingNode.getRelationships(RelTypes.OPINION_BY, Direction.INCOMING)
			
			
			// Always delete the associate from user
			def relsZ = op.underlyingNode.getRelationships()
			while(relsZ.hasNext()) {
				Relationship z = relsZ.next()
				log.debug(" List all rels =====> " + z.getType()) 
			}
			
			// Scenario 1 - check for relationship from book, delete if exists
			// then associate book with the next opinion down the chain
			
			if(relsFromBook.hasNext()) {
				log.debug("scenario 1 !!!!")
				Relationship toOpinion = relsToOpinions.next()
				Relationship fromBook = relsFromBook.next()				
				Node bookNode = fromBook.getStartNode()
				Node nextOpinionNode = toOpinion.getEndNode()
				bookNode.createRelationshipTo(nextOpinionNode, RelTypes.NEXT_OPINION_FOR_BOOK)
				
				// delete the current rels
				toOpinion.delete()
				fromBook.delete()		
			}
			
			// Scenario 2 - the book is sandwiched between two opinions
			
			if(relsFromOpinions.hasNext() && relsToOpinions.hasNext()) {
				log.debug("scenario 2!!!!")
				Relationship toOpinion = relsToOpinions.next()
				Relationship fromOpinion = relsFromOpinions.next()
				Node startNode = fromOpinion.getStartNode()
				Node endNode = toOpinion.getEndNode();
				startNode.createRelationshipTo(endNode, RelTypes.NEXT_OPINION_FOR_BOOK)
				
				// delete the current rels
				toOpinion.delete()
				fromOpinion.delete()
			}
			
			// Scenario 3 - the book is at the end of the linked list but not associate with book directly
			
			if(relsFromOpinions.hasNext() && !relsToOpinions.hasNext()) {
				log.debug("scenario 3!!!!")
				Relationship fromOpinion = relsFromOpinions.next()
				fromOpinion.delete()
				
			}
			
			// Always delete the associate from user
			def rels = op.underlyingNode.getRelationships(RelTypes.OPINION_BY, Direction.INCOMING)
			if(rels.hasNext()) {
				log.debug("deleting user !!!!")
				Relationship userRel = rels.next()
				userRel.delete()
			}

			// delete from index
			opinionIndex.remove(op.underlyingNode)
			
			// always delete the node
			
			op.underlyingNode.delete()
			op.underlyingNode = null
			
		}
		
		catch(e) {
			tx.failure()
			println e
			return false
		}
		finally {
			tx.finish()
		}
	}
	
	private Node getReferenceNode() {
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
			RelTypes.OPINION_REFERENCE, Direction.OUTGOING );

		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				referenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( referenceNode,
					RelTypes.OPINION_REFERENCE );
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
	
}


class OpinionFactory {
	def KEY_COUNTER = "key_counter"
	def graphDb
	def opinionIndex
	def referenceNode
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		CHECK_IN,
		CHECKINS_REFERENCE,
		CHECK_IN_OPINION,
		OPINION_REFERENCE
	}
	
	OpinionFactory(graphDb, index) {
		this.graphDb = graphDb
		this.opinionIndex = index
		this.referenceNode = getReferenceNode()
	}
	
	def createOpinion() {
		Node node = graphDb.createNode()
		Long counter = getNextId()
		Opinion op = new Opinion(node).with {
			opinionId = counter
			return it
		}
		return op
	}
	
	private Node getReferenceNode() {
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
			RelTypes.OPINION_REFERENCE, Direction.OUTGOING );

		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				referenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( referenceNode,
					RelTypes.OPINION_REFERENCE );
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

