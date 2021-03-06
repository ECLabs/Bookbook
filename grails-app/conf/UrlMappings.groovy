class UrlMappings {

	static mappings = {
		
		/**
		 * BOOKS
		 */
		
		"/api/book"(controller:"book"){
			action = [GET:"find", POST:"add"]
		}
		
		"/api/book/$id"(controller:"book"){
			action = [GET:"findById", PUT:"update", DELETE:"remove"]
		}
		
		// TODO: remove this... just need to make sure its not called anywhere first
		"/api/book/all"(controller:"book"){
			action = [GET:"find"]
		}
		
		// TODO: remove when we have DELETE/PUT working
		"/api/book/delete/$id"(controller:"book"){
			action = [GET:"remove"]
		}
		// TODO: remove when we have DELETE/PUT working
		"/api/book/update/$id"(controller:"book"){
			action = [POST:"update"]
		}
		"/api/book/$id/activity-stream"(controller:"book") {
			action = [GET:"getActivityStream"]
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
		
		"/api/user/userId-$userId/photo"(controller:"user"){
			action = [POST:"updatePhoto"]
		}
		
		"/api/user/$userId/followers"(controller:"user"){
			action = [GET:"findFollowers"]
		}
		
		"/api/user/$userId/following"(controller:"user"){
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
		"/api/user/userId-$userId/activity-stream"(controller:"user") {
			action = [GET:"getActivityStream"]
		}
		
		"/api/user/$id/activity-stream"(controller:"user") {
			action = [GET:"getActivityStream"]
		}
		
		/**
		 * CHECK INS
		 */
		
		"/api/book/$bookId/checkIn"(controller:"book") {
			action = [GET:"findCheckInsByBookId", POST:"establishCheckIn"]
		}
		
		"/api/user/$userId/checkIn"(controller:"user") {
			action = [GET:"findCheckInsByUserId", POST:"establishCheckIn"]
		}
		
		"/api/book/$id/checkInDummy"(controller:"book") {
			action = [GET:"getDummyCheckIn"]
		}
		
		"/api/book/checkIn/delete-all"(controller:"book") {
			action = [GET:"removeAllCheckIns"]
		}
		
		"/api/book/$id/opinion"(controller:"book") { action = [GET:"findOpinions", POST:"addOpinion"] }
		
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
		
		"/api/list/delete/bookListId-$bookListId"(controller:"list") {
			action = [GET:"removeListEntry"]
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
		
		/**
		 * OPINIONS
		 */
		
		"/api/opinion/$id"(controller:"book") {
			action = [GET:"findOpinionById", DELETE:"deleteOpinion"]
		}
		"/api/opinion/$id/delete"(controller:"book") {
			action = [GET:"deleteOpinion"]
		}
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		"/" 
		{
			controller = "index"	
			action = "dashboard"
		}
		"/books"
		{
			controller = "book"
		}
		"/shutdown"
		{
			controller = "book"
			action = "shutdown"
		}
		
		"500"(view:'/error')
		
		
	}
}
