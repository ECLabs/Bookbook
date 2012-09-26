package bookbook.services

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.DateTimeComparator
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.ReturnableEvaluator
import org.neo4j.graphdb.StopEvaluator
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.TraversalPosition
import org.neo4j.graphdb.Traverser.Order
import org.neo4j.graphdb.index.IndexHits
import bookbook.domain.User
import bookbook.domain.User2
import org.apache.commons.logging.LogFactory

class UserService {
    static transactional = true
	
	//neo4j
	def graphDb
	def DB_PATH = "bookbook-neo4j-store"
	def USERNAME_KEY = "username"
	def userIndex

	@PostConstruct
	def initialize() {
		log.debug "############### initialize() in UserService- Loading graphDb, shutdownHook."
		//graphDb = new EmbeddedGraphDatabase( DB_PATH );
		userIndex = graphDb.index().forNodes("users")
		//registerShutdownHook();
	}
	
	@PreDestroy
	def cleanUp() {
		log.debug "############### cleanUp()  - Shutting down graphDb."
		//graphDb.shutdown();
	}
	
	def shutdown()
	{
		graphDb.shutdown();
		
	}
	def registerShutdownHook()
	{
		// Registers a shutdown hook for the Neo4j and index service instances
		// so that it shuts down nicely when the VM exits (even if you
		// "Ctrl-C" the running example before it's completed)
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()	{
				shutdown();
			}
		} );
	}
	
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK,
		FOLLOW
	}
		
	def addUser(userIn) {
		//log.debug "in AddUser - graphdb" + graphDb
		
		def userType = "user"
		// validation
		userIn.with {
			if(!activationMethod.equals("facebook") && (!userName || !password)) {
				log.debug "userName and password are required fields for non-facebook enabled accounts."
				return null
			}
			if(userTypeCode) {
				userType = userTypeCode
			}
			// TODO: strong password restrictions
			// TODO: only allow one user with a given username, email address, or facebookId
		}
		
		User u = null
		Transaction tx = graphDb.beginTx()
		try {
			UserFactory userFactory = new UserFactory(graphDb)
			u = userFactory.createUser().with {
				firstName = userIn.firstName == null ? "" : userIn.firstName == null
				middleName = userIn.middleName == null ? "" : userIn.middleName
				lastName  = userIn.lastName == null ? "" : userIn.lastName
				fullName  = userIn.fullName == null ? "" : userIn.fullName
				location  = userIn.location == null ? "" : userIn.location
				userName = userIn.userName
				email = userIn.email
				password = userIn.password == null ? "" : userIn.password
				photoUrl = userIn.photoUrl == null ? "" : userIn.photoUrl
				createDate = new Date().toString()
				userTypeCode = userType == null ? "" : userType// i.e. user, superuser, author, guest, auditor, placeholder (used for popular reviewers)
				aboutMe = userIn.aboutMe == null ? "" : userIn.aboutMe // 140 characters of text about the user
				activationMethod = userIn.activationMethod
				numberFollowing = 0
				numberOfFollowers = 0
				facebookId = userIn.facebookId == null ? "" : userIn.facebookId
				gender = userIn.gender == null ? "" : userIn.gender
				facebookUpdateTime = userIn.facebookUpdateTime == null ? "" : userIn.facebookUpdateTime
				return it
			}
			log.debug "just added this email... ${u.email}"
			userIndex = graphDb.index().forNodes('users')
			userIndex.add(u.underlyingNode, "userName", u.userName)
			userIndex.add(u.underlyingNode, "id", u.userId)
			userIndex.add(u.underlyingNode, "email", u.email)
			tx.success()
		}
		finally {
			tx.finish()
		}
		
		log.debug "about to send email to ${u.userName} with email address = ${u.email}"
		sendMail {
		  to u.email
		  subject "Welcome to BookUp!"
		  html "<b>Hello</b> ${u.userName}!<br/><br/>You've signed up for BookUp.  Thanks for giving BookUp a try.  Enjoy!"
		}
		
		return u
	}
	def updateUser(userIn, userId) {
		log.debug "in updateUser - userIn" + userIn.toString()
		Transaction tx = graphDb.beginTx()
		def u = null
		try {
			// make sure the id matches
			if(!userIn.activationMethod.equals("facebook") && (Long.valueOf(userId) != userIn.userId)) {
				log.debug "Could not update.. userId did not match"
				return null
			}
			
			// check to see if the user was already added
			u = findUsersByProperty("id", Long.valueOf(userId))
			if(!u)
			{
				log.debug "Could not update.. user not found"
				return false
			}
			
			u.with {
				firstName = userIn.firstName == null ? "" : userIn.firstName
				middleName = userIn.middleName == null ? "" : userIn.middleName
				lastName  = userIn.lastName == null ? "" : userIn.lastName
				fullName  = userIn.fullName == null ? "" : userIn.fullName
				location  = userIn.location == null ? "" : userIn.location
				userName = userIn.userName == null ? "" : userIn.userName
				email = userIn.email == null ? "" : userIn.email
				password = userIn.password == null ? "" : userIn.password
				photoUrl = userIn.photoUrl == null ? "" : userIn.photoUrl
				//createDate = userIn.createDate
				endDate = userIn.endDate == null ? "" : userIn.endDate
				updateDate = new Date().toString()
				//lastLoginDate = userIn.lastLoginDate
				userTypeCode = userIn.userTypeCode == null ? "" : userIn.userTypeCode // i.e. user, superuser, author, guest, auditor, placeholder (used for popular reviewers)
				aboutMe = userIn.aboutMe == null ? "" :  userIn.aboutMe// 140 characters of text about the user
				//activationMethod = userIn.activationMethod
				facebookId = userIn.facebookId == null ? "" : userIn.facebookId
				gender = userIn.gender == null ? "" : userIn.gender
				facebookUpdateTime = userIn.facebookUpdateTime == null ? "" : userIn.facebookUpdateTime
				return it
			}
			tx.success()
		}
		finally {
			tx.finish()
		}
		return u
	}
	
	def updateUserPhotoUrl(userId, photoUrl) {
		log.debug "in updateUserPhotoUrl - userId: ${userId}, photoUrl:${photoUrl}"
		Transaction tx = graphDb.beginTx()
		def u = null
		try {
			u = findUsersByProperty("id", userId)
			if(!u)
			{
				log.debug "Could not update.. user not found"
				return false
			}
			
			u.setPhotoUrl(photoUrl)
			tx.success()
		}
		finally {
			tx.finish()
		}
		return u
	}
	
	def findAllUsers() {
		Node refNode = getSubReferenceNode(RelTypes.USERS_REFERENCE)
		
		def trav = refNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
			new ReturnableEvaluator()
			{
				public boolean isReturnableNode( TraversalPosition pos )
				{
					return !pos.isStartNode();
				}
			},
			RelTypes.USER, Direction.OUTGOING,
		);
		
		def allUsers = []
		for(node in trav) {
			allUsers.push(new User(node))
		}
		
		return allUsers
	}
	
	def findNumberOfUsersByUserName(userName) {
		log.debug "in findNumberOfUsersByUserName(), looking for property [ $userName ]"
			
		userIndex = graphDb.index().forNodes('users')
		IndexHits<Node> hits = userIndex.get('userName', userName)
		def numUsers = hits.size()
		hits.close()
		return numUsers
	}
	
	def findUsersByProperty(property, value) {
		log.debug "in findUsersByProperty(), looking for property [ $property ] with value [ $value ]"
		
		if(property.equals("id")) {
			value = Long.valueOf(value)
		}
		userIndex = graphDb.index().forNodes('users')
		
		Node userNode = null
		if(property.equals('id') || property.equals('userName') || property.equals('email'))
		{
			IndexHits<Node> hits = userIndex.get(property, value)
			if(hits.hasNext()) {
				userNode = hits.next()
				hits.close()
				if(userNode)
				{
					log.debug "### user found in index ###"
					def u = new User(userNode)
					return u
				}
			}
		}
		
		Node urefNode = getSubReferenceNode(RelTypes.USERS_REFERENCE)
		def trav = urefNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
			new ReturnableEvaluator()
			{
				public boolean isReturnableNode( TraversalPosition pos )
				{
					return !pos.isStartNode() &&
						pos.currentNode().getProperty(property, null).equals(value)
				}
			},
			RelTypes.USER, Direction.OUTGOING
		);
		
		def allUsers = []
		
		Transaction tx = graphDb.beginTx()
		try {
			for(node in trav) {
				def u = new User(node)
				allUsers.push(u)
				if(property.equals("id") || property.equals("userName") || property.equals("email")) {
					log.debug "!!! adding user with property:[$property] and value:[${value}] to index !!!"
					userIndex.add node, property, value		
				}
					
			}
			tx.success()
		}
		finally {
			tx.finish()
		}
		
		log.debug "found ${allUsers.size()}"
		if(!allUsers)
			return null
		return allUsers
	}
	
	def deleteUser(userName) {
		log.debug "in deleteUser() with id [ $userName ] "
		Transaction tx = graphDb.beginTx()
		try {
			// get the user
			def u = findUsersByProperty("userName", userName)
			if(!u)
			{
				log.debug "Could not delete.. user not found"
				return false
			}
			
			// delete node
			log.debug "Deleting user from graph"
			u.underlyingNode.delete()
			for(rel in u.underlyingNode.getRelationships())
			{
				rel.delete()
			}
			
			// delete node from index
			log.debug "Deleting user from index"
			userIndex = graphDb.index().forNodes("users")
			userIndex.remove(u.underlyingNode)
			
			tx.success()
		}
		catch(e) {
			return false
		}
		finally {
			tx.finish()
		}
		return true
	}
	
	def signIn(userName, password) {
		def returnString = ""
		Transaction tx = graphDb.beginTx()
		try { 
			User u = findUsersByProperty("userName", userName)
			if(!u) {
				returnString = "Invalid username."
			}
			else if(!u.getPassword().equals(password)) {
				returnString = "Incorrect password!"
			}
			else {
				u.lastLoginDate = new Date().toString()
				returnString = u;
			}
			tx.success()
		}
		catch(e) {
			return "Server error..."
		}
		finally {
			tx.finish()
		}
		
		
		return returnString
	}
	
	def signInFacebook(fbUserIn) {
		log.debug("in signInFacebook()")
		
		
		def userId
		// check for existing user by email
		// if not exists, create user
		// if exists, update user
		// update last-login-date
		
		// TODO: Only update the bookup user if facebook's updatetime has changed
		fbUserIn.location = fbUserIn.location.name
		log.debug("looking for user with email address: " + fbUserIn.email)
		def u = findUsersByProperty("email", fbUserIn.email)
		if(!u) {
			log.debug("adding user")
			u = addUser(fbUserIn)
			userId = u.userId
		}
		else {
			log.debug("updating user. ID is ${u.userId}")
		
			boolean updateUser = false
			if(!u.facebookUpdateTime) {
				updateUser = true
			}
			else {
				DateTimeComparator dtc = DateTimeComparator.getInstance()
				log.debug "compare result - " + dtc.compare(fbUserIn.facebookUpdateTime,u.facebookUpdateTime)
				
				if(dtc.compare(fbUserIn.facebookUpdateTime,u.facebookUpdateTime) > 0) {
					updateUser = true
					log.debug "facebook update time is greater than the last recorded time in the bookup database"
				}
			}
			
			if(updateUser) {
				// only update the username if the account was activated via facebook initially.
				// we don't want to update the username if it was specifically set by the user upon
				// signup
				if(!u.activationMethod.equalsIgnoreCase('facebook')) {
					fbUserIn.userName = u.userName
				}
				userId = u.userId
				fbUserIn.aboutMe = u.aboutMe
				fbUserIn.fullName = u.fullName
				u = this.updateUser(fbUserIn, userId)
			}
			
			
		}

		return u
	}
	
	def signUp(userIn) { 
		addUser(userIn)
	}
	
	def findFollowList(userName, direction) {
		// direction: Direction.OUTGOING, Direction.INCOMING
		// friends are one way... more like followers
		User u = findUsersByProperty("userName", userName)
		
		def trav = u.underlyingNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
			new ReturnableEvaluator()
			{
				public boolean isReturnableNode( TraversalPosition pos )
				{
					// only get active users
					return !pos.isStartNode() &&
						pos.lastRelationshipTraversed().getEndNode().getProperty("endDate", null) == null
				}
			},
			RelTypes.FOLLOW, direction
		);
		
		def followingUsers = []
		
		Transaction tx = graphDb.beginTx()
		try {
			for(node in trav) {
				def u2 = new User(node)
				followingUsers.push(u2)
			}

			// update index
			if(direction.equals(Direction.OUTGOING)) {
				u.numberFollowing = followingUsers.size()
			} else if(direction.equals(Direction.INCOMING)) {
				u.numberOfFollowers = followingUsers.size()
			}
			tx.success()
		}
		finally {
			tx.finish()
		}

		log.debug "found ${followingUsers.size()}"
		return followingUsers
	}
	
	def followUser(userNameFollowing, userNameToFollow) {
		User u = findUsersByProperty("userName", userNameFollowing)
		User u2f = findUsersByProperty("userName", userNameToFollow)
		Transaction tx = graphDb.beginTx()
		
		// make sure there isn't already a FOLLOW relationship
		def following = findFollowList(userNameFollowing, Direction.OUTGOING) 
		for (User u3 in following) {
			if(u3.userName.equals(userNameToFollow)) {
				log.debug "this user already follows the target user."
				return false
			}
		}
		
		try {
			u.underlyingNode.createRelationshipTo( u2f.underlyingNode, RelTypes.FOLLOW );
			if(!u.numberFollowing) { 
				u.numberFollowing = 0 
			}
			if(!u2f.numberOfFollowers) { 
				u2f.numberOfFollowers = 0 
			}
			u.numberFollowing += 1
			u2f.numberOfFollowers += 1
			
			tx.success()
		}
		catch(e) {
			return false
		}
		finally {
			tx.finish()
		}
		return true
		log.debug "$userNameFollowing successfully followed ${userNameToFollow}"
	}
	
	def unfollowUser(userNameFollowing, userNameToUnfollow) {
		User u = findUsersByProperty("userName", userNameFollowing)
		User u2uf = findUsersByProperty("userName", userNameToUnfollow)
		Transaction tx = graphDb.beginTx()
		try {
			def rels = u.underlyingNode.getRelationships(Direction.OUTGOING, RelTypes.FOLLOW)
			for(rel in rels) {
				if(rel.getEndNode().getProperty("userName", userNameToUnfollow).equals(userNameToUnfollow)) {
					rel.delete()
					break
				}
			}
			u.numberFollowing -= 1
			u2uf.numberOfFollowers -= 1
			tx.success()
		}
		catch(e) {
			return false
		}
		finally {
			tx.finish()
		}
		
		log.debug "$userNameFollowing successfully unfollowed ${userNameToUnfollow}"
		return true
	}
	private Node getSubReferenceNode(relType) {
		def rel = graphDb.getReferenceNode().getSingleRelationship(
			relType, Direction.OUTGOING );

		def subReferenceNode = null;
		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				subReferenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( subReferenceNode,
					relType );
				tx.success()
			}
			finally {
				tx.finish()
			}
		}
		else
		{
			subReferenceNode = rel.getEndNode();
		}
			
		return subReferenceNode;
	}
	
	def deleteAllUsers() {
		def allUsers = findAllUsers()
		log.debug "number of users to be deleted ${allUsers.size()}"
		Transaction tx = graphDb.beginTx()
		for(u in allUsers) {
			// delete node
			log.debug "Deleting user from graph"
			u.underlyingNode.delete()
			for(rel in u.underlyingNode.getRelationships())
			{
				rel.delete()
			}
			
			// delete node from index
			log.debug "Deleting user from index"
			userIndex = graphDb.index().forNodes("users")
			userIndex.remove(u.underlyingNode)
		}
		tx.success()
		tx.finish()
	}
	
	def createTestUsers() {
		def u1 = new User2(
			userName:"rjevans",
			firstName:"Jamil",
			//middleName:null,
			lastName:"Evans",
			email:"jamil.evans@evanschambers.com",
			password:"apples",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			//endDate: null,
			//updateDate: null,
			//createDate: null,
			//lastLoginDate: null,
			userTypeCode:"user",
			//aboutMe:"",
			activationMethod:"native"
			//numberOfFollowers:null,
			//numberFollowing: null
		)
		def u2 = new User2(
			userName:"voraciousRDR",
			firstName:"Nicole",
			lastName:"Tripodi",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u3 = new User2(
			userName:"achambers1",
			firstName:"Andre",
			lastName:"Chambers",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u4 = new User2(
			userName:"boobyhatch",
			firstName:"Elissa",
			lastName:"Shefrin",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u5 = new User2(
			userName:"cerealKilla",
			firstName:"Lisa",
			lastName:"Molinelli",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u6 = new User2(
			userName:"javaDev102",
			firstName:"Vik",
			lastName:"David",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u7 = new User2(
			userName:"jfallon",
			firstName:"Jimmy",
			lastName:"Fallon",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u8 = new User2(
			userName:"Dreamer",
			firstName:"Steve",
			lastName:"Jobs",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u9 = new User2(
			userName:"MaxH",
			firstName:"Max",
			lastName:"Houston",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u10 = new User2(
			userName:"gtimberlake",
			firstName:"Guy",
			lastName:"Timberlake",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u11 = new User2(
			userName:"ColbertReport",
			firstName:"Stephen",
			lastName:"Colbert",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u12 = new User2(
			userName:"ectech",
			firstName:"Edward",
			lastName:"Carter",
			email:"ectech@evanschambers.com",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u13 = new User2(
			userName:"maxBuddy",
			firstName:"Julian",
			lastName:"Newbill",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u14 = new User2(
			userName:"mommy",
			firstName:"Lambeth",
			lastName:"Evans",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u15 = new User2(
			userName:"jgarland",
			firstName:"Jabari",
			lastName:"Garland",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u16 = new User2(
			userName:"bstorm",
			firstName:"Brain",
			lastName:"Storm",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u17 = new User2(
			userName:"onFirst11",
			firstName:"Charlie",
			lastName:"Chaplain",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u18 = new User2(
			userName:"pennstate",
			firstName:"Joe",
			lastName:"Paterno",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u19 = new User2(
			userName:"OverweightLover",
			firstName:"Heavy",
			lastName:"D",
			email:"",
			password:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			photoUrl:"",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u20 = new User2(
			userName:"Boriqua",
			firstName:"Fat",
			lastName:"Joe",
			email:"",
			password:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			photoUrl:"",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u21 = new User2(
			userName:"yujulie",
			firstName:"Julie",
			lastName:"Yu",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u22 = new User2(
			userName:"TegaCay12",
			firstName:"Amy",
			lastName:"Martinez",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u23 = new User2(
			userName:"999forthewin",
			firstName:"Herman",
			lastName:"Cain",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u24 = new User2(
			userName:"PMP4Ever",
			firstName:"Mary",
			lastName:"Sabatino",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u25 = new User2(
			userName:"lilwahoo",
			firstName:"Laura",
			lastName:"Schweitzer",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u26 = new User2(
			userName:"hess",
			firstName:"Deanna",
			lastName:"Hess",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u27 = new User2(
			userName:"austinsmom",
			firstName:"Wendy",
			lastName:"Sydnor",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u28 = new User2(
			userName:"newsman",
			firstName:"Anderson",
			lastName:"Cooper",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u29 = new User2(
			userName:"ooooops",
			firstName:"Rick",
			lastName:"Perry",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		def u30 = new User2(
			userName:"yeswecan",
			firstName:"Barack",
			lastName:"Obama",
			email:"",
			password:"",
			photoUrl:"http://localhost:8080/Bookbook/images/maxavatar.jpg",
			userTypeCode:"user",
			aboutMe:"",
			activationMethod:"native"
		)
		
		
		addUser(u1)
		addUser(u2)
		addUser(u3)
		addUser(u4)
		addUser(u5)
		addUser(u6)
		addUser(u7)
		addUser(u8)
		addUser(u9)
		addUser(u10)
		addUser(u11)
		addUser(u12)
		addUser(u13)
		addUser(u14)
		addUser(u15)
		addUser(u16)
		addUser(u17)
		addUser(u18)
		addUser(u19)
		addUser(u20)
		addUser(u21)
		addUser(u22)
		addUser(u23)
		addUser(u24)
		addUser(u25)
		addUser(u26)
		addUser(u27)
		addUser(u28)
		addUser(u29)
		addUser(u30)
		
	}
	
//	def deleteSubReferenceNodes(relType) {
//		log.debug "in deleteSubReferenceNodes()"
//		def rels = graphDb.getReferenceNode().getRelationships(
//			relType, Direction.OUTGOING );
//
//		def subReferenceNode = null;
//		//log.debug "number of relationships12: ${rels.size()}"
//		
//		//Integer numRels = rels.size()
//		//log.debug "numRels: ${numRels}"
//
//			log.debug "deleting relationships"
//			Transaction tx = graphDb.beginTx()
//			def i = 1
//			for(rel in rels) {
//				log.debug "testing"
//				def node = rel.getEndNode();
//				def childrel = node.getSingleRelationship(RelTypes.USER, Direction.OUTGOING )
//				if(childrel != null) {
//					def childnode = childrel.getEndNode()
//					childnode.delete()
//					childrel.delete()
//				}
//				node.delete()
//				rel.delete()
//				log.debug "deleted ${i}"
//				i++;
//			}
//			tx.success()
//			tx.finish()
//		
//		
//			
//		return true;
//	}
//	
	
}

class UserFactory {
	def KEY_COUNTER = "key_counter"
	def graphDb
	def userIndex
	def usersReferenceNode
	def enum RelTypes implements RelationshipType
	{
		USERS_REFERENCE,
		USER,
		BOOKS_REFERENCE,
		BOOK
	}
	
	UserFactory(graphDb) {
		this.graphDb = graphDb
		this.usersReferenceNode = getSubReferenceNode(RelTypes.USERS_REFERENCE)
		this.userIndex = graphDb.index().forNodes("users")
	}
	
	def createUser() {
		
		Node userNode = graphDb.createNode()
		usersReferenceNode.createRelationshipTo(userNode, RelTypes.USER)
		
		User u = new User(userNode).with {
			userId = getNextId()
			return it
		}

		log.debug "New user under creation with ID of " + u.getUserId()

		// add to index
		userIndex.add(userNode, "id", u.userId)
		return u
		
	}
	
	private Node getSubReferenceNode(relType) {
		def rel = graphDb.getReferenceNode().getSingleRelationship(
			relType, Direction.OUTGOING );

		def subReferenceNode = null;
		if ( rel == null )
		{
			Transaction tx = graphDb.beginTx()
			try {
				subReferenceNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo( subReferenceNode,
					relType );
				tx.success()
			}
			finally {
				tx.finish()
			}
		}
		else
		{
			subReferenceNode = rel.getEndNode();
		}
			
		return subReferenceNode;
	}
	private synchronized Long getNextId()
	{
		log.debug "in getNextId()"
		def counter = null;
		try
		{
			counter = usersReferenceNode.getProperty( KEY_COUNTER );
			log.debug "counter: ${counter}"
		}
		catch ( e )
		{
			// Create a new counter
			counter = 1L;
			log.debug "counter2: ${counter}"
		}
		
		usersReferenceNode.setProperty( KEY_COUNTER, new Long( counter + 1 ) );
		return counter;
	}
	
}
