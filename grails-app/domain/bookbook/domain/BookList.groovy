package bookbook.domain

import org.neo4j.graphdb.Relationship;

class BookList {

	Long bookListId
	Long bookId
	Long userId
	String type
	String title
	String createDate
	
	Relationship underlyingRel
	Book book
	User user
	
	Book[] books
	
	// Constructor
	BookList(rel) {
		this.underlyingRel = rel
		book = new Book(rel.getEndNode())
		user = new User(rel.getStartNode())
	}
	
    static constraints = {
    }
	
	static transients = ["underlyingRel", "user", "book","books"]
	
	public Long getBookListId() { return underlyingRel.getProperty("id", null); }
	public Long getUserId() { return user.getUserId(); }
	public Long getBookId() { return book.getBookId(); }
	public String getType() { return underlyingRel.getProperty("type", null); }
	public String getTitle() { return underlyingRel.getProperty("title", null); }
	public String getCreateDate() { return underlyingRel.getProperty("createDate", null); }
	
	public void setBookListId(Long bookListId) { underlyingRel.setProperty("id", bookListId); }
	public void setType(String type) { underlyingRel.setProperty("type", type); }
	public void setTitle(String title) { underlyingRel.setProperty("title", title); }
	public void setCreateDate(String createDate) { underlyingRel.setProperty("createDate", createDate); }
	
}
