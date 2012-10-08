package bookbook.domain

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Direction

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
		
		// get the related book
		def bookRels = opinionNode.getRelationships(RelTypes.OPINION_ON, Direction.OUTGOING)
		if(bookRels.hasNext()) {
			book = new Book(bookRels.next().getEndNode())
		}
		
		// get the related user
		def userRels = opinionNode.getRelationships(RelTypes.OPINION_BY, Direction.INCOMING)
		if(userRels.hasNext()) {
			user = new User(userRels.next().getStartNode())
		}
		/*
		// get the related checkIn
		def checkInRels = opinionNode.getRelationships(RelTypes.CHECK_IN_OPINION, Direction.OUTGOING)
		if(checkInRels.hasNext()) {
			checkIn = new CheckIn(checkInRels.next().getEndNode())
		}
		*/

	}
	
	public void setOpinionId(Long value) { underlyingNode.setProperty("opinionId", value) }
	public void setText(String value) { underlyingNode.setProperty("text", value) }
	public void setCreateDate(String value) { underlyingNode.setProperty("createDate", value) }
	public void setBook(Book value) { this.book = value }
	public void setUser(User value) { this.user = value }
	public void setCheckIn(CheckIn value) { this.checkIn = value }
	
	public Long getOpinionId() { return underlyingNode.getProperty("opinionId", null)  }
	public String getText() { return underlyingNode.getProperty("text", null)   }
	public String getCreateDate() { return underlyingNode.getProperty("createDate", null)  }
	public Book getBook() { return this.book  }
	public User getUser() { return this.user  }
	public CheckIn getCheckIn() { return this.checkIn  }
	
	
    static constraints = { }
	
	static transients =  ['underlyingNode']
}
