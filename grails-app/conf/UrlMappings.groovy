class UrlMappings {

	static mappings = {
		
		/**
		 * BOOKS
		 */
		
		"/api/book"(controller:"book"){
			action = [GET:"findAll", POST:"add"]
		}
		
		"/api/book/$id"(controller:"book"){
			action = [GET:"find", PUT:"update", DELETE:"remove"]
		}
		
		"/api/book/external"(controller:"book"){
			action = [GET:"findExternal"]
		}
		
		"/api/book/all"(controller:"book"){
			action = [GET:"findAll"]
		}
		
		// TODO: remove when we have DELETE/PUT working
		"/api/book/delete/$id"(controller:"book"){
			action = [GET:"remove"]
		}
		// TODO: remove when we have DELETE/PUT working
		"/api/book/update/$id"(controller:"book"){
			action = [POST:"update"]
		}
		
		/**
		 * USERS MANAGEMENT
		 */
		
		"/api/user"(controller:"user"){
			action = [GET:"findAll", POST:"add"]
		}
		
		"/api/user/$userName"(controller:"user"){
			action = [GET:"findByUserName"]
		}
		
		"/api/user/userId-$userId"(controller:"user"){
			action = [GET:"findByUserId", PUT:"update", DELETE:"remove"]
		}
		
		"/api/user/sign-in"(controller:"user"){
			action = [GET:"signIn"]
		}
		
		"/api/user/sign-in-facebook"(controller:"user"){
			action = [POST:"signInFacebook"]
		}
		
		"/api/user/$userName/photo"(controller:"user"){
			action = [POST:"updatePhoto"]
		}
		
		"/api/user/$userName/followers"(controller:"user"){
			action = [GET:"findFollowers"]
		}
		
		"/api/user/$userName/following"(controller:"user"){
			action = [GET:"findFollowing"]
		}
		
		"/api/user/$userName/follow/"(controller:"user"){
			action = [GET:"follow"]
		}
		
		"/api/user/delete/$userName"(controller:"user") {
			action = [GET:"remove"]
		}
		"/api/user/update/userId-$userId"(controller:"user") {
			action = [POST:"update"]
		}
		
		/**
		 * CHECK INS
		 */
		
		"/api/book/$bookId/checkIn"(controller:"book") {
			action = [GET:"findCheckInsByBookId", POST:"establishCheckIn"]
		}
		
		"/api/user/$userName/checkIn"(controller:"user") {
			action = [GET:"findCheckInsByUserId", POST:"establishCheckIn"]
		}
		
		"/api/book/$id/checkInDummy"(controller:"book") {
			action = [GET:"getDummyCheckIn"]
		}
		
		/**
		 * LISTS (Likes, Read a while ago, Recently Read, Want to Read, custom)
		 */
		/*
		"/api/list"(controller:"list") {
			action = [GET:"findAllLists"]
		}
		*/
		"/api/list/userId-$userId"(controller:"list") {
			action = [GET:"findListsByUserId", POST:"addBookToList"]
		}
		
		"/api/list/bookId-$bookId"(controller:"list") {
			action = [GET:"findListsByBookId"]
		}
		/* Can't get queries by relationship type to work yet, so we'll have to get them all from the 
		 * above query
		 *
		"/api/list/userId-$userId/like"(controller:"list") {
			action = [GET:"findLikeByUserId"]
		}
		
		"/api/list/userId-$userId/have-read"(controller:"list") {
			action = [GET:"findHaveReadByUserId"]
		}
		
		"/api/list/$id"(controller:"list") {
			action = [GET:"findListById", PUT:"updateList", DELETE:"removeList"]
		}
		
		"/api/list/$id/book/$bookId"(controller:"list") {
			action = [POST:"addBookToList", DELETE:"removeBookFromList"]
		}
		*/
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		"/" 
		{
			controller = "index"	
		}
		"500"(view:'/error')
		
		
	}
}
