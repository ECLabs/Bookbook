package bookbook.domain

import org.neo4j.graphdb.Node

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
	String source // google books, amazon, etc 
	String pubType // book, magazine, paper, short-form
	String bbRating // TODO
	Node underlyingNode
	
	Long haveReadCount // TODO
	Long wantToReadCount // TODO
	Long opinionCount // TODO
	String createdBy // TODO
	String creatorUserId // TODO
	CheckIn[] recentCheckIns // TODO
	CheckIn[] friendsLastCheckIns // TODO
	Opinion[] opinions // TODO
	
	Book(node) {
		this.underlyingNode = node	
	}
	public String getTitle() { underlyingNode.getProperty("title", null) }
	public String getAuthor() { underlyingNode.getProperty("author", null) }
	public String getDescription() { underlyingNode.getProperty("description", null) }
	public String getIsbn10() { underlyingNode.getProperty("isbn10", null) }
	public String getSmallThumbnailUrl() { underlyingNode.getProperty("smallThumbnailUrl", null) }
	public String getThumbnailUrl() { underlyingNode.getProperty("thumbnailUrl", null) }
	public String getCreateDate() { underlyingNode.getProperty("createDate", null) }
	public Long getBookId() { return underlyingNode.getProperty("id", null) == null ? 1234 : underlyingNode.getProperty("id", null) }
	
	public void setTitle(String value) {
		underlyingNode.setProperty("title", value)	
	}
	
	public void setAuthor(String value) {
		underlyingNode.setProperty("author", value)
	}
	public void setDescription(String value) {
		underlyingNode.setProperty("description", value)
	}
	public void setIsbn10(String value) {
		underlyingNode.setProperty("isbn10", value)
	}
	public void setSmallThumbnailUrl(String value) {
		underlyingNode.setProperty("smallThumbnailUrl", value)
	}
	public void setThumbnailUrl(String value) {
		underlyingNode.setProperty("thumbnailUrl", value)
	}
	public void setCreateDate(String value) {
		underlyingNode.setProperty("createDate", value)
	}
	public void setBookId(Long value) {
		underlyingNode.setProperty("id", value)
	}
	
    static constraints = {
    }
    
	static transients = [ "underlyingNode", "recentCheckIns", "friendsLastCheckIns", "opinions" ]
}
