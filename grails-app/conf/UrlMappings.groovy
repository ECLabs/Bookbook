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
			action = [GET:"findByUserName", PUT:"update", DELETE:"remove"]
		}
		
		"/api/user/sign-in"(controller:"user"){
			action = [GET:"signIn"]
		}
		
		"/api/user/$userName/photo"(controller:"user"){
			action = [POST:"updateUserPhoto"]
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
		"/api/user/update/$userName"(controller:"user") {
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
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		"/"(view:"/index")
		"500"(view:'/error')
		
		
	}
}
