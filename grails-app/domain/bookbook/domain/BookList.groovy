package bookbook.domain

import org.neo4j.graphdb.Relationship;

class BookList {

	Long bookListId
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
	
	public Long getBookListId() { 
		return underlyingRel.getProperty("id", null); 
	}
	public Book getBook() { return book; }
	public User getUser() { return user; }
	public String getType() { return underlyingRel.getProperty("type", null); }
	public String getTitle() { return underlyingRel.getProperty("title", null); }
	public String getCreateDate() { return underlyingRel.getProperty("createDate", null); }
	
	public void setBookListId(Long bookListId) { 
		if(bookListId) underlyingRel.setProperty("id", bookListId); 
	}
	public void setType(String type) { underlyingRel.setProperty("type", type); }
	public void setTitle(String title) { if(title) underlyingRel.setProperty("title", title); }
	public void setCreateDate(String createDate) { if(createDate) underlyingRel.setProperty("createDate", createDate); }
}
