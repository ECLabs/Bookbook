package bookbook.domain

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.RelationshipIndex
import org.neo4j.graphdb.Direction

/**
 * Adding a checkin - JSON
 * {"bookId":3,"checkInDate":"Sun Nov 13 22:51:42 EST 2011","class":"bookbook.DummyCheckIn","createDate":null,"id":123,"latitude":"12 North","longitude":"34 West","narrative":"this is what i think of this book!","userName":"evansro","venue":"Whole Food, Reston, VA","chapterOrSection":"Chapter 19"}
 * @author mindstate
 *
 */

class CheckIn {

	Long checkInId
	Long bookId
	Long userId
	String checkInDate
	String createDate
	String narrative
	String userName
	String venue
	String chapterOrSection
	String latitude
	String longitude
	Node underlyingNode
	Book book
	User user
	Opinion opinion
	RelationshipIndex index
	
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
	
    static constraints = { }
	static transients = [ "underlyingNode", "book", "user", "index" ]
	
	// Constructor
	CheckIn(Node cNode) {
		this.index = index
		this.underlyingNode = cNode
		
		// get the related book
		def bookRels = cNode.getRelationships(RelTypes.CHECK_IN, Direction.OUTGOING)
		if(bookRels.hasNext()) {
			book = new Book(bookRels.next().getEndNode())
		}
		
		// get the related user
		def userRels = cNode.getRelationships(RelTypes.CHECK_IN, Direction.INCOMING)
		if(userRels.hasNext()) {
			user = new User(userRels.next().getStartNode())		
		}
		
		// get the related opinion
		def oRels = cNode.getRelationships(RelTypes.CHECK_IN_OPINION, Direction.INCOMING)
		if(oRels.hasNext()) {
			opinion = new Opinion(oRels.next().getStartNode())
		}
		
	}
	
	// Constructor
	CheckIn(Node cNode, RelationshipIndex index) {
		this.index = index 
		this.underlyingNode = cNode
	}
	
	public Long getCheckInId() { 
		underlyingNode.getProperty("id", null) 
	}
	public Long getBookId() { if(book) book.getBookId() }
	public Long getUserId() { if(user) user.getUserId() }
	public String getCheckInDate() { underlyingNode.getProperty("checkInDate", null) }
	public String getCreateDate() { underlyingNode.getProperty("createDate", null) }
	public String getNarrative() { underlyingNode.getProperty("narrative", null) }
	public String getUserName() { user.getUserName() }
	public String getVenue() { underlyingNode.getProperty("venue", null) }
	public String getChapterOrSection() { underlyingNode.getProperty("chapterOrSection", null) }
	public String getLatitude() { underlyingNode.getProperty("latitude", null) }
	public String getLongitude() { underlyingNode.getProperty("longitude", null) }
	public Node getUnderlyingNode() { return underlyingNode }
	public Book getBook() { return book }
	public User getUser() { return user }
	public Opinion getOpinion() { return opinion }
	
	public void setCheckInId(Long checkInId) { 
		if(checkInId) 
			underlyingNode.setProperty("id", checkInId) 
	}
	public void setCheckInDate(String checkInDate) { if(checkInDate) underlyingNode.setProperty("checkInDate", checkInDate) }
	public void setCreateDate(String createDate) { if(createDate) underlyingNode.setProperty("createDate", createDate) }
	public void setNarrative(String narrative) { if(narrative) underlyingNode.setProperty("narrative", narrative) }
	public void setVenue(String venue) { if(venue) underlyingNode.setProperty("venue", venue) }
	public void setChapterOrSection(String chapterOrSection) { if(chapterOrSection) underlyingNode.setProperty("chapterOrSection", chapterOrSection) }
	public void setLatitude(String latitude) { if(latitude) underlyingNode.setProperty("latitude", latitude) }
	public void setLongitude(String longitude) { if(longitude) underlyingNode.setProperty("longitude", longitude) }
	public void setBook(Book book, RelationshipIndex index) { 
		if(book) {
			this.book = book
		}
	}
	public void setUser(User user, RelationshipIndex index) {
		if(user) {
			this.user = user
		}
	}
	public void setOpinion(Opinion opinion) { this.opinion = opinion }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checkInId == null) ? 0 : checkInId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CheckIn)) {
			return false;
		}
		CheckIn other = (CheckIn) obj;
		if (getCheckInId() == null) {
			if (other.getCheckInId() != null) {
				return false;
			}
		} else if (!getCheckInId().equals(other.getCheckInId())) {
			return false;
		}
		return true;
	}
	
	
	
}
