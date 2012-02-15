class UrlMappings {

	static mappings = {
		
		/**
		 * BOOKS
		 */
		
		"/book"(controller:"book"){
			action = [GET:"findAll", POST:"add"]
		}
		
		"/book/$id"(controller:"book"){
			action = [GET:"find", PUT:"update", DELETE:"remove"]
		}
		
		"/book/external"(controller:"book"){
			action = [GET:"findExternal"]
		}
		
		"/book/all"(controller:"book"){
			action = [GET:"findAll"]
		}
		
		// TODO: remove when we have DELETE/PUT working
		"/book/delete/$id"(controller:"book"){
			action = [GET:"remove"]
		}
		// TODO: remove when we have DELETE/PUT working
		"/book/update/$id"(controller:"book"){
			action = [POST:"update"]
		}
		
		/**
		 * USERS MANAGEMENT
		 */
		
		"/user"(controller:"user"){
			action = [GET:"findAll", POST:"add"]
		}
		
		"/user/$userName"(controller:"user"){
			action = [GET:"findByUserName", PUT:"update", DELETE:"remove"]
		}
		
		"/user/sign-in"(controller:"user"){
			action = [GET:"signIn"]
		}
		
		"/user/$userName/followers"(controller:"user"){
			action = [GET:"findFollowers"]
		}
		
		"/user/$userName/following"(controller:"user"){
			action = [GET:"findFollowing"]
		}
		
		"/user/$userName/follow/"(controller:"user"){
			action = [GET:"follow"]
		}
		
		"/user/delete/$userName"(controller:"user") {
			action = [GET:"remove"]
		}
		"/user/update/$userName"(controller:"user") {
			action = [POST:"update"]
		}
		
		/**
		 * CHECK INS
		 */
		
		"/book/$bookId/checkIn"(controller:"book") {
			action = [GET:"findCheckInsByBookId", POST:"establishCheckIn"]
		}
		
		"/user/$userName/checkIn"(controller:"user") {
			action = [GET:"findCheckInsByUserId", POST:"establishCheckIn"]
		}
		
		"/book/$id/checkInDummy"(controller:"book") {
			action = [GET:"getDummyCheckIn"]
		}
		
		
		
		"/"(view:"/index")
		"500"(view:'/error')
		
		
	}
}
