package bookbook.domain

class Opinion {

	Long opinionId
	Long userId
	Long bookId
	Long rating // 1 to 5 stars?
	String text
	String date
	
	User user
	
	
    static constraints = {
    }
	
	static transients = ["user"]
}
