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
			checkIns.addAll(bookService.findCheckInsByBookId(b.bookId))
			bookLists.addAll(listService.findListsByBookId(b.bookId, ""))
			
		}

	}
}
