package bookbook.controllers

import grails.converters.JSON

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.RelationshipType
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

class UserController {

	def userService
	def bookService
	
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		FOLLOW
	}
	
	def add = { 
		
		println "in add(). Json Data is --> " + params['jsondata']

		def jsonUser = JSON.parse(params['jsondata'])
		def addedUser = userService.addUser(jsonUser)
		if(addedUser)
			render addedUser as JSON
		else
			render "user could not be added"
	}
	/**
	 * Example URIs:
	 * 
	 * /user/jgarland/follow?targetUserName=rjevans&follow-action=create
	 */
	def signIn = { 
		println "in signIn(). username --> " + params.username + " password --> " + params.password
		render userService.signIn(params.username, params.password)
	}
	
	def findAll = {
		//userService.deleteSubReferenceNodes(RelTypes.USERS_REFERENCE)
		//userService.deleteAllUsers()
		//userService.createTestUsers()
		//render "hello"
		render userService.findAllUsers() as JSON
	}
	
	def findByUserName = {
		render userService.findUsersByProperty("userName", params.userName) as JSON
	}
	
	def update = { 
		def jsonUser = JSON.parse(params.jsondata)
		println "userName to update - ${params.userName}"
		render userService.updateUser(jsonUser, params.userName) as JSON	
	} 
	
	def updatePhoto = {
		println "In updatePhoto()"
		println request.getClass()
		
		if(request instanceof MultipartHttpServletRequest)
		{
		  MultipartHttpServletRequest mpr = (MultipartHttpServletRequest)request;
		  CommonsMultipartFile f = (CommonsMultipartFile) mpr.getFile("myFile");
		  if(!f.empty) {
			  println "success getting file"
			flash.message = 'success'
			f.transferTo(new File( '/tmp/' + params.userName + '_photo.png'))
		  }
		  else
		   flash.message = 'file cannot be empty'
		}
		else
		  flash.message = 'request is not of type MultipartHttpServletRequest'
	}
	def remove = {
		render userService.deleteUser(params.userName)
	}
	
	def findFollowers = { 
		render userService.findFollowList(params.userName, Direction.INCOMING) as JSON
	}
	
	def findFollowing = { 
		render userService.findFollowList(params.userName, Direction.OUTGOING) as JSON
	}
	
	/**
	 * Example URIs:
	 * /user/jgarland/follow?targetUserName=rjevans&follow-action=create
	 * /user/jgarland/follow?targetUserName=rjevans&follow-action=delete
	 */
	
	def follow = { 
		if(params.targetUserName && params['follow-action'].equals("create")) {
			render userService.followUser(params.userName, params.targetUserName)
		}
		else if(params.targetUserName && params['follow-action'].equals("delete")) {
			render userService.unfollowUser(params.userName, params.targetUserName)
		}
	}
	
	def terminateFollow = { 
		userService.unfollowUser(params.userName, params.targetUserName)
	}
	
	def establishCheckIn = {
		def jsonCheckIn = JSON.parse(params.jsondata)
		println "username is ${params.userName}"
		render bookService.createCheckIn(jsonCheckIn, jsonCheckIn.bookId, params.userName)
	}
	
	def findCheckInsByUserId = {
		render bookService.findCheckInsByUserName(params.userName) as JSON
	}
	
	def createTestUsers = {
		userService.createTestUsers()
	}
	
	def deleteAllUsers = {
		userService.deleteAllUsers()
	}
	
	def list = {
		flash.test = "test"
	}

}
