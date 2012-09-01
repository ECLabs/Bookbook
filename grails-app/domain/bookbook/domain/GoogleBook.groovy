package bookbook.domain

class GoogleBook {

	Long bookId
	Long creatorUserId
	String title
	String author
	String description
	String isbn10
	String smallThumbnailUrl
	String thumbnailUrl
	String createDate
	String pubType
	
    static constraints = {
    }
}
