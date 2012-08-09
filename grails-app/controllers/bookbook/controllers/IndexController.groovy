package bookbook.controllers

import bookbook.domain.Book
import bookbook.services.BookService
import bookbook.services.ListService
import bookbook.services.UserService

class IndexController {
	UserService userService
	BookService bookService
	ListService listService
	List users
	List books
	List bookLists = []
	List listTypes = ['LIKE','HAVE_READ','WANT_TO_READ']
	List checkIns = []
    def index = { 
		
		users = userService.findAllUsers()	
		books = bookService.findAllBooks()
		

		for(Book b in books) {
			println "bookId - ${b.bookId}"
			checkIns.addAll(bookService.findCheckInsByBookId(b.bookId))
			bookLists.addAll(listService.findListsByBookId(b.bookId, ""))
			
		}

	}
	
	def showuser = {
		redirect(controller:"user", action: "findByUserId", params: [userId: params.id])
	}
	def showbook = {
		redirect(controller:"book", action: "find", params: [id: params.id])
	}
	def showcheckin = {
		redirect(controller:"book", action: "findCheckInById", params: [id: params.id])
	}
	def showlist = {
		redirect(controller:"list", action: "findListById", params: [id: params.id])
	}
}
