package bookbook.domain

import org.neo4j.graphdb.Direction
import bookbook.utils.ActivityIF
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Path
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.index.RelationshipIndex
import org.neo4j.graphdb.traversal.TraversalDescription
import org.neo4j.helpers.collection.IterableWrapper
import org.neo4j.kernel.Traversal

/**
 * Adding a checkin - JSON
 * {"bookId":3,"checkInDate":"Sun Nov 13 22:51:42 EST 2011","class":"bookbook.DummyCheckIn","createDate":null,"id":123,"latitude":"12 North","longitude":"34 West","narrative":"this is what i think of this book!","userName":"evansro","venue":"Whole Food, Reston, VA","chapterOrSection":"Chapter 19"}
 * @author mindstate
 *
 */

class CheckIn extends ActivityIF {

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
		CHECK_INS,
		OPINION_ON,
		OPINION_BY,
		CHECK_IN_OPINION,
		OPINION_REFERENCE,
		NEXT_CHECKIN_FOR_BOOK,
		NEXT_CHECKIN_FOR_USER
	}
	
    static constraints = { }
	static transients = [ "underlyingNode", "book", "user", "index" ]
	
	// Constructor
	CheckIn(Node cNode) {
		this.underlyingNode = cNode
		//this.book = getBookForCheckIn(cNode)
		//this.user = getUserForCheckIn(cNode)
		//this.opinion = getOpinionForCheckIn(cNode)
		
	}
	
	// Constructor
	CheckIn(Node cNode, Book book) {
		this.underlyingNode = cNode
		this.book = book
		this.user = getUserForCheckIn(cNode)
		this.opinion = getOpinionForCheckIn(cNode)
	}
	
	// Constructor
	CheckIn(Node cNode, User user) {
		this.underlyingNode = cNode
		this.user = user
		this.book = getBookForCheckIn(cNode)
		this.opinion = getOpinionForCheckIn(cNode)
	}
	
	
	// Constructor
	CheckIn(Node cNode, RelationshipIndex index) {
		this.index = index 
		this.underlyingNode = cNode
	}
	
	public Long getCheckInId() { 
		return underlyingNode.getProperty("id", null) 
	}
	public Long getBookId() { 
		if(book) {	return book.getBookId() }	
	}
	public Long getUserId() { if(user) return user.getUserId() }
	public String getCheckInDate() { return underlyingNode.getProperty("checkInDate", null) }
	public String getCreateDate() { return underlyingNode.getProperty("createDate", null) }
	public String getNarrative() { return underlyingNode.getProperty("narrative", null) }
	public String getUserName() { return user.getUserName() }
	public String getVenue() { return underlyingNode.getProperty("venue", null) }
	public String getChapterOrSection() { return underlyingNode.getProperty("chapterOrSection", null) }
	public String getLatitude() { return underlyingNode.getProperty("latitude", null) }
	public String getLongitude() { return underlyingNode.getProperty("longitude", null) }
	public Node getUnderlyingNode() { return underlyingNode }
	public Book getBook() { return getBookForCheckIn(underlyingNode) }
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
	
	private Opinion getOpinionForCheckIn(cNode) {
		// get the related opinion
		def oRels = cNode.getRelationships(RelTypes.CHECK_IN_OPINION, Direction.INCOMING)
		if(oRels.hasNext()) {
			return new Opinion(oRels.next().getStartNode())
		}
		return null
	}
	
	private User getUserForCheckIn(Node cNode) {
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_CHECKIN_FOR_USER, Direction.INCOMING );
		
		Iterable<CheckIn> checkInIterator = new IterableWrapper<CheckIn, Path>(
				traversal.traverse( cNode ) ) {
				
			@Override
			protected CheckIn underlyingObjectToObject( Path path )
			{
				return new CheckIn( path.endNode());
			}
		};
	
		Iterator itr = checkInIterator.iterator()
		while(itr.hasNext()) {
			CheckIn last = itr.next()
			if(!itr.hasNext()) {
				def rel = last.underlyingNode.getRelationships(RelTypes.CHECK_INS, Direction.INCOMING)
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
	
	private Book getBookForCheckIn(Node cNode) {
		// create traverser and all all to the hashset
		TraversalDescription traversal = Traversal.description().
		depthFirst().
		relationships( RelTypes.NEXT_CHECKIN_FOR_BOOK, Direction.INCOMING );
		
		Iterable<CheckIn> checkInIterator = new IterableWrapper<CheckIn, Path>(
				traversal.traverse( cNode ) ) {
				
			@Override
			protected CheckIn underlyingObjectToObject( Path path )
			{
				return new CheckIn( path.endNode());
			}
		};
	
		Iterator itr = checkInIterator.iterator()
		while(itr.hasNext()) {
			CheckIn last = itr.next()
			if(!itr.hasNext()) {
				def rel = last.underlyingNode.getRelationships(RelTypes.CHECK_INS, Direction.INCOMING)
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
