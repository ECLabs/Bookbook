package bookbook.controllers

import grails.converters.JSON

class ListController {
	
	def listService

    def index = { }
	
	def findAllLists = { 
		render "not yet implemented"
	}
	
	// finds by user
	def findListsByUserId = {
		def results = listService.findListsByUserId(params.userId, '')
		render results as JSON
	}
	/*
	def findLikeByUserId = {
		def results = listService.findBooksByListTypeForUser(params.userId, 'LIKE')
		render results as JSON
	}
	def findHaveReadByUserId = {
		def results = listService.findListsByUserId(params.userId, 'HAVE_READ')
		render results as JSON
	}
	def findWantToReadByUserId = {
		def results = listService.findListsByUserId(params.userId, 'WANT_TO_READ')
		render results as JSON
	}
	*/
	
	// finds by book
	def findListsByBookId = {
		def results = listService.findListsByBookId(params.bookId, '')
		render results as JSON
	}
	
	/*
	def findLikeByBookId = {
		def results = listService.findListsByBookId(params.bookId, 'LIKE')
		render results as JSON
	}
	def findHaveReadByBookId = {
		def results = listService.findListsByBookId(params.bookId, 'HAVE_READ')
		render results as JSON
	}
	def findWantToReadByBookId = {
		def results = listService.findListsByBookId(params.bookId, 'WANT_TO_READ')
		render results as JSON
	}
	*/
	
	def addBookToList = {
		def l = JSON.parse(params['jsondata'])
		def userId = params['userId']
		
		if(false == listService.addListEntry(userId, l.bookId, l.listType, l.listTitle)) {
			response.sendError(javax.servlet.http.HttpServletResponse.SC_CONFLICT) // 409
		}
		render ""
	}
	
	def findListById = {
		render "Find list by ID not yet implemented"
	}
	
	def updateList = {
		render "not yet implemented"
	}
	
	def removeListEntry = {
		log.info "in removeListEntry(). Parameters are ${params.toString()}"
		
		listService.deleteListEntry(params.bookListId)
		render "book list deleted successfully!"
	}
	
	def removeBookFromList = {
		render "not yet implemented"
	}
}
