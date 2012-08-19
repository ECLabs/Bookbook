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
	
    def index = { 
		render "I'm alive!  The current time is " + new Date()
	}
	
	def findById = {
		println "in find"
		def results = ""

		if(params.id)
			results = bookService.findBooksByProperty("id", params.id)
		
		render results as JSON
		
	}
	
	def find = {
		println "in findAll()"
		/**
		 * Check to see if this is a query
		 */
		if(params.isbn10 || params.title || params.author) {
			println "still in findAll() - this is a search operation"
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
		println "in add(). Json Data is --> " + params['jsondata']

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
		println "in BookController.update(). Json Data is --> ${params.jsondata}. id is ${params.id}"
		
		def jsonBook = JSON.parse(params.jsondata)
		def googleBook = new GoogleBook(jsonBook)
		
		def updatedBook = bookService.updateBook(googleBook, Long.valueOf(params.id))
		if(updatedBook)
			render updatedBook as JSON
	}
	
	def remove = {
		println "in BookController.remove(). id is --> " + params.id
		
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
		println "in findAllSources(). Parameters are ${params.toString()}"
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
			if(combined[(book.isbn10)]) { // if the book is already in the map, see if we need to replace it
				if(book.getBookId() != null) { // replace with this one, prefer the one with an ID
					combined[(book.isbn10)] = book
				}
			}
			else {
				combined.put(book.isbn10, book)
			}
			
		}
		
		render combined.values() as JSON
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
