package bookbook.domain

import org.neo4j.graphdb.Relationship;

class BookList {

	Long bookListId
//	Long bookId
//	Long userId
	String type
	String title
	String createDate
	
	Relationship underlyingRel
	Book book
	User user
	
	// Constructor
	BookList(rel) {
		this.underlyingRel = rel
		book = new Book(rel.getEndNode())
		user = new User(rel.getStartNode())
	}
	
    static constraints = {
    }
	
	static embedded = ['book', 'user']
	static transients = ["underlyingRel"]
	
	public Long getBookListId() { return underlyingRel.getProperty("id", null); }
//	public Long getUserId() { return user.getUserId(); }
//	public Long getBookId() { return book.getBookId(); }
	public Book getBook() { return book; }
	public User getUser() { return user; }
	public String getType() { return underlyingRel.getProperty("type", null); }
	public String getTitle() { return underlyingRel.getProperty("title", null); }
	public String getCreateDate() { return underlyingRel.getProperty("createDate", null); }
	
	public void setBookListId(Long bookListId) { if(value) underlyingRel.setProperty("id", bookListId); }
	public void setType(String type) { if(value) underlyingRel.setProperty("type", type); }
	public void setTitle(String title) { if(value) underlyingRel.setProperty("title", title); }
	public void setCreateDate(String createDate) { if(value) underlyingRel.setProperty("createDate", createDate); }
}
