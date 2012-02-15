package bookbook.domain

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship

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
	Relationship underlyingRel
	Book book
	User user
	
    static constraints = { }
	static transients = [ "underlyingRel", "book", "user" ]
	
	// Constructor
	CheckIn(rel) { 
		this.underlyingRel = rel
		book = new Book(rel.getEndNode())
		user = new User(rel.getStartNode())
	}
	
	public Long getCheckInId() { underlyingRel.getProperty("id", null) }
	public Long getBookId() { book.getBookId() }
	public Long getUserId() { user.getUserId() }
	public String getCheckInDate() { underlyingRel.getProperty("checkInDate", null) }
	public String getCreateDate() { underlyingRel.getProperty("createDate", null) }
	public String getNarrative() { underlyingRel.getProperty("narrative", null) }
	public String getUserName() { user.getUserName() }
	public String getVenue() { underlyingRel.getProperty("venue", null) }
	public String getChapterOrSection() { underlyingRel.getProperty("chapterOrSection", null) }
	public String getLatitude() { underlyingRel.getProperty("latitude", null) }
	public String getLongitude() { underlyingRel.getProperty("longitude", null) }
	public Relationship getUnderlyingRel() { return underlyingRel }
	public Book getBook() { return book }
	public User getUser() { return user }
	
	public void setCheckInId(Long checkInId) { underlyingRel.setProperty("id", checkInId) }
	public void setCheckInDate(String checkInDate) { underlyingRel.setProperty("checkInDate", checkInDate) }
	public void setCreateDate(String createDate) { underlyingRel.setProperty("createDate", createDate) }
	public void setNarrative(String narrative) { underlyingRel.setProperty("narrative", narrative) }
	public void setVenue(String venue) { underlyingRel.setProperty("venue", venue) }
	public void setChapterOrSection(String chapterOrSection) { underlyingRel.setProperty("chapterOrSection", chapterOrSection) }
	public void setLatitude(String latitude) { underlyingRel.setProperty("latitude", latitude) }
	public void setLongitude(String longitude) { underlyingRel.setProperty("longitude", longitude) }
	
	//private setBookId() { book.setBookId() }
	//private setUserId() { user.setUserId() }
	
	//private setUserName() { user.setUserName() }
	//private setUnderlyingRel() { return underlyingRel }
	//private setBook() { return book }
	//private setUser() { return user }
	
}
