package bookbook.domain

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.RelationshipType
import org.ocpsoft.pretty.time.PrettyTime

class Opinion {

	Long opinionId
	String text
	String createDate
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
		CHECK_IN_OPINION,
		OPINION_REFERENCE
	}
	
	// Constructor
	Opinion(Node opinionNode) {
		this.underlyingNode = opinionNode
		
		// get the related user
		def userRels = opinionNode.getRelationships(RelTypes.OPINION_BY, Direction.INCOMING)
		if(userRels.hasNext()) {
			user = new User(userRels.next().getStartNode())
		}
	}
	
	// Constructor
	Opinion(Node oNode, Book book) {
		this.underlyingNode = oNode
		this.book = book
		
		// get the related user
		def userRels = oNode.getRelationships(RelTypes.OPINION_BY, Direction.INCOMING)
		while(userRels.hasNext()) {
			def r = userRels.next()
			if(r.getStartNode().getProperty("id", null)) {
				this.user = new User(r.getStartNode())
				break
			}
		}
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
		PrettyTime p = new PrettyTime();
		return p.format(new Date(underlyingNode.getProperty("createDate", null)))
	}
	public Book getBook() { return this.book  }
	public User getUser() { return this.user  }
	public CheckIn getCheckIn() { return this.checkIn  }
	
	
    static constraints = { }
	
	static transients =  ['underlyingNode']

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
