package bookbook.domain

import org.neo4j.graphdb.Node
import org.ocpsoft.pretty.time.PrettyTime
import bookbook.domain.BookList

/**
 * Instantiate with...
 * 
def obj = new Book (
	graphDb.createNode() ).with {
		description = book.volumeInfo.description
		author=book.volumeInfo.authors[0]
		title='jsmil is great'
		isbn10=isbn
		smallThumbnailUrl=book.volumeInfo.imageLinks.smallThumbnail
		thumbnailUrl=book.volumeInfo.imageLinks.thumbnail
		createDate=(new Date()).toString()
		return it
	}
*/

class Book {

	Long bookId
	String title
	String author
	String description
	String isbn10
	String smallThumbnailUrl
	String thumbnailUrl
	String createDate
	Long creatorUserId
	String source // google books, amazon, etc 
	String pubType // book, magazine, paper, short-form
	String bbRating // TODO
	Node underlyingNode
	
	Long haveReadCount // TODO
	Long wantToReadCount // TODO
	Long opinionCount // TODO
	
	CheckIn[] recentCheckIns // TODO
	CheckIn[] friendsLastCheckIns // TODO
	Opinion[] opinions // TODO
	ArrayList<BookList> listsForUser
	
	Book(node) {
		this.underlyingNode = node	
	}
	public String getTitle() { underlyingNode.getProperty("title", null) }
	public String getAuthor() { underlyingNode.getProperty("author", null) }
	public String getDescription() { underlyingNode.getProperty("description", null) }
	public String getIsbn10() { underlyingNode.getProperty("isbn10", null) }
	public String getSmallThumbnailUrl() { underlyingNode.getProperty("smallThumbnailUrl", null) }
	public String getThumbnailUrl() { underlyingNode.getProperty("thumbnailUrl", null) }
	public Long getCreatorUserId() { underlyingNode.getProperty("creatorUserId", null) }
	public Long getBookId() { return underlyingNode.getProperty("id", null) == null ? 1234 : underlyingNode.getProperty("id", null) }
	public String getPubType() { return underlyingNode.getProperty("pubType", null) }
	public ArrayList<BookList>  getListsForUser() { return listsForUser }
	public String getCreateDate() { 
		PrettyTime p = new PrettyTime();
		return p.format(new Date(underlyingNode.getProperty("createDate", null)))
	}
	
	public void setTitle(String value) {
		if(value) underlyingNode.setProperty("title", value)	
	}	
	public void setAuthor(String value) {
		if(value) underlyingNode.setProperty("author", value)
	}
	public void setDescription(String value) {
		if(value) underlyingNode.setProperty("description", value)
	}
	public void setIsbn10(String value) {
		if(value) underlyingNode.setProperty("isbn10", value)
	}
	public void setSmallThumbnailUrl(String value) {
		if(value) underlyingNode.setProperty("smallThumbnailUrl", value)
	}
	public void setThumbnailUrl(String value) {
		underlyingNode.setProperty("thumbnailUrl", value)
	}
	public void setPubType(String value) {
		if(value) underlyingNode.setProperty("pubType", value)
	}
	public void setCreateDate(String value) {
		if(value) underlyingNode.setProperty("createDate", value)
	}
	public void setCreatorUserId(Long value) {
		if(value) underlyingNode.setProperty("creatorUserId", value)
	}
	public void setBookId(Long value) {
		if(value) underlyingNode.setProperty("id", value)
	}
	public void setListsForUser(ArrayList<BookList> listsForUser) {
		this.listsForUser = listsForUser
	}
	
    static constraints = {
    }
	
    
	static transients = [ "underlyingNode", "recentCheckIns", "friendsLastCheckIns", "opinions" ]
}
