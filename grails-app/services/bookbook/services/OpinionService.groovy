package bookbook.services

import bookbook.domain.Book
import bookbook.domain.CheckIn
import bookbook.domain.Opinion
import bookbook.domain.User
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.index.IndexHits
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction

class OpinionService {
	static transactional = false
	
	//neo4j
	def graphDb
	def DB_PATH = "bookbook-neo4j-store"
	def KEY_COUNTER = "opinion_key_counter"
	
	def opinionIndex
	
	
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
		OPINION_ON,
		OPINION_BY,
		CHECK_IN_OPINION,
		OPINION_REFERENCE
	}

	def addOpinion(String opinionText, CheckIn checkIn, Book book, User user) {
		log.debug "in addOpinion() with opinionText ${opinionText}"
		
		Opinion op = null
		Transaction tx = graphDb.beginTx()
		try {
			
			OpinionFactory factory = new OpinionFactory(graphDb, opinionIndex)
			
			op = factory.createOpinion().with {
				createDate = new Date().toString()
				text = opinionText
				checkIn = checkIn
				book = book
				user = user
				return it
			}
			
			log.debug "about to create check-in relationship"
			Relationship rel1 = op.underlyingNode.createRelationshipTo(checkIn.underlyingNode, RelTypes.CHECK_IN_OPINION)
			log.debug "about to create book relationship"
			Relationship rel2 = op.underlyingNode.createRelationshipTo(book.underlyingNode, RelTypes.OPINION_ON)
			log.debug "about to create user relationship"
			Relationship rel3 = user.underlyingNode.createRelationshipTo(op.underlyingNode, RelTypes.OPINION_BY)
			log.debug "after creating 3rd relationship"
			
			opinionIndex.add(op.underlyingNode, "opinionId", op.opinionId)
			opinionIndex.add(op.underlyingNode, "bookId", book.bookId)
			
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
		return op
	}
	
	def addOpinion(String opinionText, Book book, User user) {
		log.debug "in addOpinion() with opinionText ${opinionText}"
		
		Opinion op = null
		Transaction tx = graphDb.beginTx()
		try {
			
			OpinionFactory factory = new OpinionFactory(graphDb, opinionIndex)
			
			op = factory.createOpinion().with {
				createDate = new Date().toString()
				text = opinionText
				book = book
				user = user
				return it
			}
			
			log.debug "about to create 2nd relationship"
			Relationship rel2 = op.underlyingNode.createRelationshipTo(book.underlyingNode, RelTypes.OPINION_ON)
			log.debug "about to create 2nd relationship"
			Relationship rel3 = user.underlyingNode.createRelationshipTo(op.underlyingNode, RelTypes.OPINION_BY)
			log.debug "after creating 3rd relationship"
			
			opinionIndex.add(op.underlyingNode, "opinionId", op.opinionId)
			opinionIndex.add(op.underlyingNode, "bookId", book.bookId)
			
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
		return op
	}
	
	def findById(opinionId) {
		return findOpinionByProperty("id", opinionId)
	}
	
	def findByBookId(bookId) {
		return findOpinionByProperty("bookId", bookId)
	}
	
	def findByCheckInId(checkInId) {
		return findOpinionByProperty("checkInId", checkInId)
	}
	
	def findOpinionByProperty(property, value) {
		log.debug "in findOpinionByProperty(), looking for property [ $property ] with value [ $value ]"
		
		def allOpinions = []
		if(property.equals('id') || property.equals('checkInId') || property.equals('bookId')) {
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
		OPINION_ON,
		OPINION_BY,
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

