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
	def checkinService
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
		log.info "in add(). Parameters are ${params.toString()}. Incoming json data is --> " + params['jsondata']

		// we have to remove the return characters otherwise decodeBase64 won't work
		def jsonNoReturns = params['jsondata'].replaceAll("\r") { "" }
		log.debug "updated json - " + jsonNoReturns
		def jsonUser = JSON.parse(jsonNoReturns)
		byte[] b = jsonUser.picture.decodeBase64()
		
		// validate that no other user with the username already exists.
		if(validateAddUser(jsonUser) == false) {
			render "{field:'userName',error:'Username " + jsonUser.userName + " already exists. Please try another.'}"
			return
		}
		
		// all is good.. add the user
		log.debug "validation checked out ok!"
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
		log.info "in signIn(). Parameters are ${params.toString()}"
		def returnVal = userService.signIn(params.username, params.password)
		if(returnVal instanceof User) {
			render returnVal as JSON
		}
		else {
			render returnVal
		}
	}
	
	def signInFacebook = {
		log.info "in signInFacebook(). Parameters are ${params.toString()}. jsondata -->" + params['jsondata']
		
		// we have to remove the return characters otherwise decodeBase64 won't work
		def jsonNoReturns = params['jsondata'].replaceAll("\r") { "" }
		log.debug "updated json - " + jsonNoReturns
		def jsonUser = JSON.parse(jsonNoReturns)
		byte[] b = jsonUser.picture.decodeBase64()
		def user = userService.signInFacebook(jsonUser)
		def now = new Date()
		
		if(user) {
			log.info "Login successful for user ${user.userName}:${user.userId}"
			updatePhoto(user.userId, b)
			render user as JSON
		} else {
			log.info "Login failed - sending 403 Forbidden"
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
		log.info "In findByUserName(). Parameters are ${params.toString()}"
		render userService.findUsersByProperty("userName", params.userName) as JSON
	}
	
	def findByUserId = {
		log.info "In findByUserId(). Parameters are ${params.toString()}"
		def returnVal = userService.findUsersByProperty("id", Long.valueOf(params.userId))
		if(returnVal == null) {
			render "[]"
		}
		else {
			render userService.findUsersByProperty("id", Long.valueOf(params.userId)) as JSON
		}
					
	}
	
	def update = { 
		log.info "In update(). Parameters are ${params.toString()}"
		// we have to remove the return characters otherwise decodeBase64 won't work
		def jsonNoReturns = params['jsondata'].replaceAll("\r") { "" }
		log.debug "updated json - " + jsonNoReturns
		def jsonUser = JSON.parse(jsonNoReturns)
		
		if(jsonUser.picture) {
			byte[] b = jsonUser.picture.decodeBase64()
			jsonUser.photoUrl = updatePhoto(jsonUser.userId, b)
		}
		
		log.debug "userId to update - ${params.userId}"
		render userService.updateUser(jsonUser, params.userId) as JSON	
	} 
	
	def updatePhoto = {
		log.info "In updatePhoto(). Parameters are ${params.toString()}"
		
		def f = request.getFile("myFile");
		if(!f.empty) {
			log.info "success getting file"
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
		log.info "In remove(). Parameters are ${params.toString()}"
		render userService.deleteUser(params.userName)
	}
	
	def findFollowers = { 
		log.info "In findFollowers(). Parameters are ${params.toString()}"
		render userService.findFollowList(params.userName, Direction.INCOMING) as JSON
	}
	
	def findFollowing = { 
		log.info "In findFollowing(). Parameters are ${params.toString()}"
		render userService.findFollowList(params.userName, Direction.OUTGOING) as JSON
	}
	
	/**
	 * Example URIs:
	 * /user/jgarland/follow?targetUserName=rjevans&follow-action=create
	 * /user/jgarland/follow?targetUserName=rjevans&follow-action=delete
	 */
	
	def follow = { 
		log.info "In follow(). Parameters are ${params.toString()}"
		if(params.targetUserName && params['follow-action'].equals("create")) {
			render userService.followUser(params.userName, params.targetUserName)
		}
		else if(params.targetUserName && params['follow-action'].equals("delete")) {
			render userService.unfollowUser(params.userName, params.targetUserName)
		}
	}
	
	def terminateFollow = { 
		log.info "In terminateFollow(). Parameters are ${params.toString()}"
		userService.unfollowUser(params.userName, params.targetUserName)
	}
	
	def establishCheckIn = {
		log.info "In establishCheckIn(). Parameters are ${params.toString()}"
		def jsonCheckIn = JSON.parse(params.jsondata)
		def returnVal = checkinService.addCheckin(jsonCheckIn, params.userId, jsonCheckIn.bookId)
		if(returnVal == false) {
			log.error("Unable to establish check-in")
			response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR) // 500
		}
		else
			render returnVal as JSON
		
	}
	
	def findCheckInsByUserId = {
		log.info "In findCheckInsByUserId(). Parameters are ${params.toString()}"
		render checkinService.findCheckInsByUserId(params.userId) as JSON
	}
	
	def createTestUsers = {
		log.info "In createTestUsers(). Parameters are ${params.toString()}"
		userService.createTestUsers()
	}
	
	def deleteAllUsers = {
		log.info "In deleteAllUsers(). Parameters are ${params.toString()}"
		userService.deleteAllUsers()
	}
	
	def list = {
		flash.test = "test"
	}
	
	def validateAddUser(user) {
		log.info "In validateAddUser(). Parameters are ${params.toString()}"
		def userCount = userService.findNumberOfUsersByUserName(user.userName)
		log.debug "Number of users with username $user.userName: $userCount"
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
