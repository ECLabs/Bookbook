package bookbook.controllers

import bookbook.domain.User
import grails.converters.JSON

import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.RelationshipType
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.codehaus.groovy.grails.web.converters.ConverterUtil
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import javax.servlet.http.HttpServletRequest

class UserController {

	def userService
	def bookService
	def basePhotoPath = '/usr/bin/tomcat7/webapps/BookUpImages/'
	def basePhotoUrl = 'http://labs.evanschambers.com:8080/BookUpImages/'
	
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

		// we have to remove the return characters otherwise decodeBase64 won't work
		def jsonNoReturns = params['jsondata'].replaceAll("\r") { "" }
		println "updated json - " + jsonNoReturns
		def jsonUser = JSON.parse(jsonNoReturns)
		byte[] b = jsonUser.picture.decodeBase64()
		
		// validate that no other user with the username already exists.
		if(validateAddUser(jsonUser) == false) {
			render "{field:'userName',error:'Username " + jsonUser.userName + " already exists. Please try another.'}"
			return
		}
		
		// all is good.. add the user
		println "validation checked out ok!"
		def addedUser = userService.addUser(jsonUser)
		if(addedUser) {
			updatePhoto(addedUser.userId, b)
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
		def returnVal = userService.signIn(params.username, params.password)
		if(returnVal instanceof User) {
			render returnVal as JSON
		}
		else {
			render returnVal
		}
	}
	
	def signInFacebook = {
		println "in signInFacebook(). jsondata -->" + params['jsondata']
		
		// we have to remove the return characters otherwise decodeBase64 won't work
		def jsonNoReturns = params['jsondata'].replaceAll("\r") { "" }
		println "updated json - " + jsonNoReturns
		def jsonUser = JSON.parse(jsonNoReturns)
		byte[] b = jsonUser.picture.decodeBase64()
		def user = userService.signInFacebook(jsonUser)
		def now = new Date()
		
		if(user) {
			println "Login successful for user ${user.userName}:${user.userId}"
			updatePhoto(user.userId, b)
			render user as JSON
		} else {
			println "Login failed - sending 403 Forbidden"
			response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN)
		}
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
	
	def findByUserId = {
		def returnVal = userService.findUsersByProperty("id", Long.valueOf(params.userId))
		if(returnVal == null) {
			render "[]"
		}
		else {
			render userService.findUsersByProperty("id", Long.valueOf(params.userId)) as JSON
		}
					
	}
	
	def update = { 
		def jsonUser = JSON.parse(params.jsondata)
		println "userId to update - ${params.userId}"
		render userService.updateUser(jsonUser, params.userId) as JSON	
	} 
	
	def updatePhoto = {
		println "In updatePhoto()"
		println request.getClass()
		
		def f = request.getFile("myFile");
		if(!f.empty) {
			println "success getting file"
			flash.message = 'success'
			def suffix = params.userId + ".profilephoto.${new Date().getTime()}.png";
			def path = basePhotoPath + suffix
			def url =  basePhotoUrl + suffix
			f.transferTo(new File(path))
			
			// update the photoUrl on the user record
			userService.updateUserPhotoUrl(params.userId, url)
			render url
		}
		else {
			render ''
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
		def jsonCheckIn = params.jsondata
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
	
	def updatePhoto(userId, b) {
		// Write to a file
		def filename = userId + ".profilephoto.${new Date().getTime()}.png";
		def fos= new FileOutputStream(basePhotoPath + filename)
		fos.write(b);
		fos.close()
		
		// TODO: move this into the signInFacebook() method
		// Update user photoUrl
		def url = basePhotoUrl + filename
		userService.updateUserPhotoUrl(userId, url)
		return url
	}

}
