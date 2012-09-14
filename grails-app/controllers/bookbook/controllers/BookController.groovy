package bookbook.controllers

import java.text.Normalizer.Form;

import bookbook.domain.GoogleBook
import bookbook.domain.Book
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import javax.servlet.http.HttpServletRequest

class BookController {

	def GOOGLE_BOOKS_MAX_RESULTS_PER_SEARCH = 10
	def bookService
	def queryReturn
	def youSearchedFor
	def jsonBookArray = []
	
	def books = {
		log.info "in books(). Parameters are ${params.toString()}"
		youSearchedFor = "12 Results for query \"${params.title}\""
		queryReturn = params.title
		if(params.title) {
			books = findAllSources2(params)
			for(b in books) {
				jsonBookArray.push(b as JSON)
			}
		}
		else {
			books = bookService.findAllBooks()
		}
		
	}
	
    def index = { 
		render "I'm alive!  The current time is " + new Date()
	}
	
	def findById = {
		log.info "in findById(). Parameters are ${params.toString()}"
		def results = ""

		if(params.id)
			results = bookService.findBooksByProperty("id", params.id)
		
		if(results instanceof ArrayList) {
			results = results[0]
		}
		render results as JSON
		
	}
	
	def find = {
		log.info "in find(). Parameters are ${params.toString()}"
		/**
		 * Check to see if this is a query
		 */
		if(params.size() > 2) { // action and controller are default params
			log.debug "still in findAll() - this is a search operation"
			def books = findAllSources(params)
			if(books && books == -1) { // invalid query
				response.sendError(javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST) // 400
			}
			else if(books) {
				render books as JSON
				return
			}
			else {
				// TODO: wrap this stuff in exception handlers so we don't have to include in every function
				response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR) // 400
			}
			
		}
		// otherwise, just return all books
		render bookService.findAllBooks() as JSON
	}
	
	def add = {
		log.info "in add(). . Parameters are ${params.toString()}. Json Data is --> " + params['jsondata'] + ". Parameters are ${params.toString()}"

		def jsonBook = JSON.parse(params['jsondata'])
		def newGoogleBook = new GoogleBook(jsonBook)
		def addedBook = bookService.addBook(newGoogleBook)
		if(addedBook == -1) // duplicate
			response.sendError(javax.servlet.http.HttpServletResponse.SC_CONFLICT) // 409
		else if (addedBook)
			render addedBook as JSON
		else
			response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR) // 500 
	}
	
	def update = {
		log.info "in BookController.update(). Json Data is --> ${params.jsondata}. id is ${params.id}"
		
		def jsonBook = JSON.parse(params.jsondata)
		def googleBook = new GoogleBook(jsonBook)
		
		def updatedBook = bookService.updateBook(googleBook, Long.valueOf(params.id))
		if(updatedBook)
			render updatedBook as JSON
	}
	
	def remove = {
		log.info "in BookController.remove(). Parameters are ${params.toString()}"
		
		bookService.deleteBook(params.id)
		render "book deleted successfully!"
	}
	
	def findCheckInById = {
		render "Find check-in by ID not yet implemented"
	}
	
	def establishCheckIn = {
		def jsonCheckIn = JSON.parse(params.jsondata)
		render bookService.createCheckIn(jsonCheckIn, params.bookId, jsonCheckIn.userId) as JSON
	}
	
	def findCheckInsByBookId = {
		render bookService.findCheckInsByBookId(params.bookId) as JSON
	}
	
	def getDummyCheckIn = {
		def dummy = new DummyCheckIn(
			id: 123,
			bookId:23,
			userId : 45,
			checkInDate : new Date().toString(),
			narrative : "this is what i think of this book!",
			venue : "Whole Food, Reston, VA",
			latitude : "12 North",
			longitude : "34 West")
		render dummy as JSON
	}
	def findAllSources(parameters) {
		log.info "in findAllSources(). Parameters are ${params.toString()}"
		
		def externals = []
		def internals = []
		def combined = [:]
		
		if(parameters.isbn10) {
			externals = bookService.findGoogleBooks("isbn", parameters.isbn10, 0, 10)
			internals = bookService.findBooksByProperty("isbn10", parameters.isbn10)
		}
		else if(parameters.title) {
			externals = bookService.findGoogleBooksByTitle(parameters.title, parameters.page)
			internals = bookService.findBooksByProperty("title", parameters.title)
		}
		else if(parameters.author) {
			externals = bookService.findGoogleBooksByAuthor(parameters.author, parameters.page)
			internals = bookService.findBooksByProperty("author", parameters.author)
		}
		else {
			return -1; // invalid query
		}
		
		// combine into one list
		externals.addAll(internals)
		
		// add the books into a Map so we can eliminate duplicates - preferences for books with BookUp IDs
		for(Book book in externals) {
			def isbn = book.isbn10.replaceFirst("^0+", "") // strip leading zeros from ISBN first - comes from Google with a leading zero
			//book.isbn10 = book.isbn10.replaceFirst("^0+", "") 
			if(combined[(isbn)]) { // if the book is already in the map, see if we need to replace it
				if(book.getBookId() != null) { // replace with this one, prefer the one with an ID
					combined[(isbn)] = book
				}
			}
			else {
				combined.put(isbn, book)
			}
			
		}
		
		render combined.values() as JSON
	}
	
	def findAllSources2(parameters) {
		log.info "in findAllSources(). Parameters are ${params.toString()}"
		
		def externals = []
		def internals = []
		def combined = [:]
		
		if(parameters.isbn10) {
			externals = bookService.findGoogleBooks("isbn", parameters.isbn10, 0, 10)
			internals = bookService.findBooksByProperty("isbn10", parameters.isbn10)
		}
		else if(parameters.title) {
			externals = bookService.findGoogleBooksByTitle(parameters.title, parameters.page)
			internals = bookService.findBooksByProperty("title", parameters.title)
		}
		else if(parameters.author) {
			externals = bookService.findGoogleBooksByAuthor(parameters.author, parameters.page)
			internals = bookService.findBooksByProperty("author", parameters.author)
		}
		else {
			return []; // invalid query
		}
		
		// combine into one list
		if(internals)
			externals.addAll(internals)
		
		// add the books into a Map so we can eliminate duplicates - preferences for books with BookUp IDs
		for(def book in externals) {
			/*
			Book book = new Book()
			if (b instanceof GoogleBook) {
				copyProperties(b, book)
			}
			else {
				book = b
			}
			*/
			def isbn = book.isbn10.replaceFirst("^0+", "") // strip leading zeros from ISBN first - comes from Google with a leading zero
			//book.isbn10 = book.isbn10.replaceFirst("^0+", "")
			if(combined[(isbn)]) { // if the book is already in the map, see if we need to replace it
				if(book.getBookId() != null) { // replace with this one, prefer the one with an ID
					combined[(isbn)] = book
				}
			}
			else {
				combined.put(isbn, book)
			}
			
		}
		
		return combined.values();
	}
	
	def copyProperties(def source, def target){
	   target.metaClass.properties.each{
	      if (source.metaClass.hasProperty(source, it.name) && 
			  it.name != 'metaClass' && 
			  it.name != 'class' && 
			  it.name != 'validationSkipMap' &&
			  it.name != 'gormPersistentEntity')
	         it.setProperty(target, source.metaClass.getProperty(source, it.name))
	   }
	}

	
}



class DummyCheckIn {
	def id
	def bookId
	def userId
	def checkInDate
	def createDate
	def narrative
	def userName
	def venue
	def latitude
	def longitude
}
