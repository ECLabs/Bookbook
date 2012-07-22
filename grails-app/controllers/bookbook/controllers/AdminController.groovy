package bookbook.controllers

class AdminController {

	def bookService
	
    def index = { 
		render "hello!"	
	}
	
	def findAllBooks = {
		println "in findAllBooks()"
		request['bookList'] = bookService.findAllBooks()
		return true
	}
}
