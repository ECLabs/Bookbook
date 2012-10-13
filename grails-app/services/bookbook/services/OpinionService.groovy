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
		OPINION_ON,
		OPINION_BY,
		OPINIONS,
		NEXT_OPINION_FOR_BOOK,
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
			
			// get the current associated root opinion
			Opinion originalOpinion = null;
			def rels = book.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.OUTGOING)
			if(rels.hasNext()) {
				
				// there's an existing root node, so we need to subordinate it to the new one
				def existingOpinionRel = rels.next();
				originalOpinion = new Opinion(existingOpinionRel.getEndNode())
				
				// delete old relationship to the book
				existingOpinionRel.delete()
				
				// create relationship from the new node to the orig one with reltype = NEXT_OPINION_FOR_BOOK
				log.debug "about to create 1st relationship"
				Relationship rel0 = newOpinion.underlyingNode.createRelationshipTo(originalOpinion.underlyingNode, RelTypes.NEXT_OPINION_FOR_BOOK)
			}

			// create relationship from book to new opinion node
			log.debug "about to create relationship from book to new opinion node"
			Relationship rel1 = book.underlyingNode.createRelationshipTo(newOpinion.underlyingNode, RelTypes.OPINIONS)

			
			log.debug "about to create 2nd relationship"
			Relationship rel3 = user.underlyingNode.createRelationshipTo(newOpinion.underlyingNode, RelTypes.OPINION_BY)
			log.debug "after creating 3rd relationship"
			
			Relationship rel2 = null
			if(checkIn) {
				log.debug "about to create check-in relationship"
				rel2 = newOpinion.underlyingNode.createRelationshipTo(checkIn.underlyingNode, RelTypes.CHECK_IN_OPINION)
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
	
	def findById(opinionId) {
		return findOpinionByProperty("id", opinionId)
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
	
	def findAllOpinions() {
		HashSet<Opinion> allOpinions = new HashSet<Opinion>()
		def books = bookService.findAllBooks()
		for(b in books) {
			allOpinions.addAll(findByBookId(b.bookId))
		}
		return allOpinions		
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

