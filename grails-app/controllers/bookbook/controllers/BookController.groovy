package bookbook.controllers

import bookbook.domain.GoogleBook
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

class BookController {

	def GOOGLE_BOOKS_MAX_RESULTS_PER_SEARCH = 10
	def bookService
	
    def index = { 
		render "I'm alive!  The current time is " + new Date()
	}
	
	def find = {
		println "in find"
		def results = ""

		if(params.id)
			results = bookService.findBooksByProperty("id", params.id)
		
		render results as JSON
		
	}
	
	def findExternal = {
		println "in findExternal(). Parameters are ${params.toString()}"
		def results
		if(params.isbn10)
			results = bookService.findBooksByProperty("isbn10", params.isbn10);
		else if(params.title)
			results = bookService.findGoogleBooksByTitle(params.title, params.page);
		else if(params.author)
			results = bookService.findGoogleBooksByAuthor(params.author, params.page);
		else {
			render "Invalid query"
			return;
		}
		render results as JSON
	}
	
	def findAll = {
		println "in findAll()"
		def results = bookService.findAllBooks()
		def conv = results as JSON
		conv.prettyPrint = true
		render conv.toString()
	}
	
	def add = {
		println "in add(). Json Data is --> " + params['jsondata']

		def jsonBook = JSON.parse(params['jsondata'])
		def newGoogleBook = new GoogleBook(jsonBook)
		def addedBook = bookService.addBook(newGoogleBook)
		if(addedBook)
			render "book added successfully!"
		else
			render "book could not be added"

		
	}
	
	def update = {
		println "in BookController.update(). Json Data is --> ${params.jsondata}. id is ${params.id}"
		
		def jsonBook = JSON.parse(params.jsondata)
		def googleBook = new GoogleBook(jsonBook)
		
		def updatedBook = bookService.updateBook(googleBook, params.id)
		if(updatedBook)
			render "book updated successfully!"
		else
			render "book could not be updated"
	}
	
	def remove = {
		println "in BookController.remove(). id is --> " + params.id
		
		bookService.deleteBook(params.id)
		render "book deleted successfully!"
	}
	
	def establishCheckIn = {
		def jsonCheckIn = JSON.parse(params.jsondata)
		render bookService.createCheckIn(jsonCheckIn, params.bookId, jsonCheckIn.userName) as JSON
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
