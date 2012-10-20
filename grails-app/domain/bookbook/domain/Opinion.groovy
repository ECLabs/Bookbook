package bookbook.domain

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.traversal.TraversalDescription
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.kernel.Traversal
import org.ocpsoft.pretty.time.PrettyTime
import bookbook.utils.ActivityIF

class Opinion extends ActivityIF {

	Long opinionId
	String text
	String createDate
	boolean forCheckin
	Node underlyingNode	
	User user
	Book book
	CheckIn checkIn
	
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
		NEXT_OPINION_FOR_USER,
		NEXT_OPINION_FOR_BOOK,
		CHECK_IN_OPINION,
		OPINION_REFERENCE
	}
	
	// Constructor
	Opinion(Node oNode) {
		this.underlyingNode = oNode
	}
	
	// Constructor
	Opinion(Node oNode, Book book) {
		this.underlyingNode = oNode
		this.book = book
		this.user = getUserForOpinion(oNode)
	}
	
	// Constructor
	Opinion(Node oNode, User user) {
		this.underlyingNode = oNode
		this.user = user
		this.book = getBookForOpinion(oNode)
	}
	
	public void setOpinionId(Long value) { underlyingNode.setProperty("opinionId", value) }
	public void setText(String value) { underlyingNode.setProperty("text", value) }
	public void setCreateDate(String value) { underlyingNode.setProperty("createDate", value) }
	public void setBook(Book value) { this.book = value }
	public void setUser(User value) { this.user = value }
	public void setCheckIn(CheckIn value) { this.checkIn = value }
	
	public Long getOpinionId() { return underlyingNode.getProperty("opinionId", null)  }
	public String getText() { return underlyingNode.getProperty("text", null)   }
	public String getCreateDate() { 
		//PrettyTime p = new PrettyTime();
		//return p.format(new Date(underlyingNode.getProperty("createDate", null)))
		return underlyingNode.getProperty("createDate", null)
	}
	public Book getBook() { return this.book  }
	public User getUser() { return this.user  }
	public CheckIn getCheckIn() { return this.checkIn  }
	public boolean isForCheckin() {
		def oRels = underlyingNode.getRelationships(RelTypes.CHECK_IN_OPINION, Direction.OUTGOING)
		if(oRels.hasNext()) {
			return true
		}
		return false
	}
	
	
    static constraints = { }
	
	static transients =  ['underlyingNode']
	
	private Book getBookForOpinion(Node oNode) {
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_OPINION_FOR_BOOK, Direction.INCOMING );
		
		Iterable<Opinion> opinionIterator = new IterableWrapper<Opinion, Path>(
				traversal.traverse( oNode ) ) {
				
			@Override
			protected Opinion underlyingObjectToObject( Path path )
			{
				return new Opinion( path.endNode());
			}
		};
	
		Iterator itr = opinionIterator.iterator()
		while(itr.hasNext()) {
			Opinion lastOpinion = itr.next()
			if(!itr.hasNext()) {
				def rel = lastOpinion.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.INCOMING)
				while(rel.hasNext()) {
					def r = rel.next()
					if(r.getStartNode().getProperty("title", null)) {
						return new Book(r.getStartNode())
					}
				}
			}
		}
		return null
	}
	
	private User getUserForOpinion(Node oNode) {
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_OPINION_FOR_USER, Direction.INCOMING );
		
		Iterable<Opinion> opinionIterator = new IterableWrapper<Opinion, Path>(
				traversal.traverse( oNode ) ) {
				
			@Override
			protected Opinion underlyingObjectToObject( Path path )
			{
				return new Opinion( path.endNode());
			}
		};
	
		Iterator itr = opinionIterator.iterator()
		while(itr.hasNext()) {
			Opinion lastOpinion = itr.next()
			if(!itr.hasNext()) {
				def rel = lastOpinion.underlyingNode.getRelationships(RelTypes.OPINIONS, Direction.INCOMING)
				while(rel.hasNext()) {
					def r = rel.next()
					if(r.getStartNode().getProperty("userName", null)) {
						return new User(r.getStartNode())
					}
				}
			}
		}
		return null
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((opinionId == null) ? 0 : opinionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Opinion)) {
			return false;
		}
		Opinion other = (Opinion) obj;
		if (getOpinionId() == null) {
			if (other.getOpinionId() != null) {
				return false;
			}
		} else if (!getOpinionId().equals(other.getOpinionId())) {
			return false;
		}
		return true;
	}	
	
}
