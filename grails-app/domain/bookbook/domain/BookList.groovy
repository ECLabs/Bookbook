package bookbook.domain

class BookList {

	Long bookListId
	String type // default-manual, default-auto, custom-manual, custom-auto
	String title
	String description
	String createDate
	String createdBy
	Long subscriberCount
	
	Book[] books
	
	
	
    static constraints = {
    }
	
	static transients = ["books"]
}
