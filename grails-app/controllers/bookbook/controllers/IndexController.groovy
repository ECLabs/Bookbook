package bookbook.controllers

import bookbook.domain.Book
import bookbook.services.BookService
import bookbook.services.CheckinService
import bookbook.services.ListService
import bookbook.services.UserService

class IndexController {
	UserService userService
	BookService bookService
	ListService listService
	CheckinService checkinService
	List users
	List books
	List bookLists = []
	List listTypes = ['LIKE','HAVE_READ','WANT_TO_READ','HAVE_SKIMMED']
	List checkIns = []
	def graphDb
	
    def index = { 
		
		users = userService.findAllUsers()
		books = bookService.findAllBooks()
		
		for(Book b in books) {
			checkIns.addAll(checkinService.findCheckInsByBookId(b.bookId))
		}

		//log.info ("**** shutting down DB ****")
		//graphDb.shutdown()
	}
	
	def user = {
		users = userService.findAllUsers()
	}
	
	def list = {
		books = bookService.findAllBooks()
		users = userService.findAllUsers()
		for(Book b in books) {
			bookLists.addAll(listService.findListsByBookId(b.bookId, ""))
		}
	}
	
	def dashboard = {

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
