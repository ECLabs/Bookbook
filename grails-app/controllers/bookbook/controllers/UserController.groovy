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
		println "in add(). Incoming json data is --> " + params['jsondata']

		// validate that no other user with the username already exists.
		def jsonUser = JSON.parse(params['jsondata'])
		if(validateAddUser(jsonUser) == false) {
			render "{field:'userName',error:'Username " + jsonUser.userName + " already exists. Please try another.'}"
			return
		}
		
		// all is good.. add the user
		println "validation checked out ok!"
		def addedUser = userService.addUser(jsonUser)
		if(addedUser) {
			render addedUser as JSON
		}				
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
		
		def f = request.getFile("myFile");
		  if(!f.empty) {
			  println "success getting file"
			flash.message = 'success'
			f.transferTo(new File( '/tmp/' + params.userName + '_photo.png'))
			render ""
		  }
		  else {
		   render 'file cannot be empty'
		  }
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
	
	def validateAddUser(user) {
		def userCount = userService.findNumberOfUsersByUserName(user.userName)
		println "Number of users with username $user.userName: $userCount"
		if(userCount > 0) {
			return false;
		}
		return true;
	}

}
