<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>BookUP - API Docs</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<!-- Le styles -->
<link rel="stylesheet" href="${resource(dir:'css',file:'bootstrap.css')}" />
<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<style type="text/css">
body {
	padding-top: 60px;
	padding-bottom: 40px;
}
.sidebar-nav {
	padding: 9px 0;
}
</style>
<link rel="stylesheet" href="${resource(dir:'css',file:'bootstrap-responsive.css')}" />

<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    
    <script>
    $(document).ready(function() {
    	  // Handler for .ready() called.
		$('ul.nav-tabs li').click(function() {
			var index = $(this).index();
			$('ul.nav li').removeClass('active');
			$(this).addClass('active');
			$('div.service-div').addClass('hide');
			$('div.service-div:eq(' + index + ')').removeClass('hide');
			return false;
		});
    });
		
    </script>

</head>
<body>
<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container-fluid"> <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span> </a> <a class="brand" href="/Bookbook/index/dashboard">BookUP</a>
      <div class="btn-group pull-right"> <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"> <i class="icon-user"></i> Username <span class="caret"></span> </a>
        <ul class="dropdown-menu">
          <li><a href="#">Profile</a></li>
          <li class="divider"></li>
          <li><a href="#">Sign Out</a></li>
        </ul>
      </div>
      <div class="nav-collapse">
        <ul class="nav">
          <li><g:link action="dashboard">Console</g:link></li>
          <li class="active"><g:link action="apidocs">API Docs</g:link></li>
        </ul>
      </div>
      <!--/.nav-collapse -->
    </div>
  </div>
</div>
<div class="container-fluid">
  <div class="row-fluid">
    <!--
          <div class="hero-unit">
            <h1>Console</h1>
          </div>
          -->
  </div>
  <div class="row-fluid">
    <ul class="nav nav-tabs">
      <li id="book_service_tab" class="active"><a href="#">Book Service</a></li>
      <li id="user_service_tab"><a href="#">User Service</a></li>
      <li><a href="#">List Service</a></li>
      <li><a href="#">Opinion Service</a></li>
      <li><a href="#">Checkin Service</a></li>
    </ul>
  </div>
  <div class="row-fluid">
    
    <div class="alert pull-left hide" id="the-alert" style="margin-left:10px;">
        <button type="button" class="close" data-dismiss="alert">×</button>
        <span></span>
      </div>

    <!-- 
    <div class="btn-toolbar" style="padding-bottom:30px; margin-top:0;">
       <div class="btn-group  pull-right" style="margin-right:5px;"> <a class="btn" href="#" onclick="launchAddBookModal()"> Create a Book</a>
      </div>
       
      <g:form action="books" controller="book" method="get" style="clear:none;margin:0;padding:0;">
      <div class="pull-right" style="margin-right:10px"> 
      	<span class="input-append">
        	<input class="span2" name="title" id="appendedInputButton" size="16" type="text" style="width:200px" value="${queryReturn}"><button class="btn" type="submit"><i class="icon-search"></i> Search</button><button class="btn" type="button" onclick="window.location='/Bookbook/book/books'">Reset</button>
        </span> 
      </div>
      </g:form>
      -->
      
       <!-- <span class="label pull-left" style="margin-top:14px;margin-left:15px;">Showing all ### books</span> -->
    
    <div class="service-div">
	<h1>Book Service</h1>
	<br/>
	<table class="table table-striped">
		<tr><th width="150">Resource</th><th width="100">Method</th><th width="200">Query Params</th><th width="375">Description</th><th width="200">Return Value</th><th>Comments</th></tr>
		<tr><td>/book</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find all books in the internal database</td><td>Array of Book Objects</td><td></td></tr>
		<tr><td>/book</td><td><span class="label label-success">GET</span></td><td>isbn10 |author | title </td><td>Find books from internal and external sources combined (currently the only external source is Google Books)</td><td>Array of Book  objects (BookUp books have more attributes)</td><td>This call returns the bookId and other bookup attributes if the book already exists in the bookup database.  Returns a 400 (Bad Request) HTTP status code on invalid query parameters.  Returns 500 for all other errors.  <br/><br/>Example request URI: <span class="label">/book/external?title=flowers</span></td></tr>
		<tr><td>/book/{bookid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Finds a specific book by the supplied BOOK_ID</td><td>Book Object</td><td></td></tr>
		<tr><td>/book</td><td><span class="label label-info">POST</span></td><td>none</td><td>Add a book</td><td>Book Object </td><td>"Returns a 409 (Conflict) HTTP status code on attempts to add a duplicate book.  Returns 500 for all other errors.  <br/><br/>Example request body: {"jsonData":{"description":"fantastic book", "author":"Stephen King", "title":"Angry Birds", "isbn10":"1234567890", "smallThumbnailUrl":"http://labs.evanschambers.com/small-cover.jpg",  "thumbnailUrl":"http://labs.evanschambers.com/large-cover.jpg", "source":"googlebooks", "pubType":"book"}}    </td></tr>
		<tr><td>/book/{bookid}</td><td><span class="label label-warning">PUT</span></td><td>none</td><td>Update a book</td><td>none</td><td>"This may not be working yet. <br/><br/>Example request body: {"jsonData":{"description":"fantastic book", "author":"Stephen King", "title":"Angry Birds", "isbn10":"1234567890", "smallThumbnailUrl":"http://labs.evanschambers.com/small-cover.jpg",  "thumbnailUrl":"http://labs.evanschambers.com/large-cover.jpg", "source":"googlebooks", "pubType":"book"}}      </td></tr>
		<tr><td>/book/{bookid}</td><td><span class="label label-important">DELETE</span></td><td>none</td><td>Delete a book</td><td>none</td><td>This may not be working yet.</td></tr>
		<tr><td>/book/{bookid}/checkIn</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find all checkins for a book by BOOK_ID</td><td>Array of CheckIn objects</td><td></td></tr>
		<tr><td>/book/{bookid}/checkIn</td><td><span class="label label-info">POST</span></td><td>none</td><td>Establish a checkin for a book with the check-in data in the request body.</td><td>CheckIn object</td><td>Example request body: {"jsonData":{"chapterOrSection":"1", "latitude":"null", "longitude":"null", "narrative":"This is awesome", "userId":34,"venue":"null"}}</td></tr>
		<tr><td>/book/delete/{bookid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Delete a book.  (This is a temporary resource until the <span class="label label-important">DELETE</span> method works.)</td><td>none</td><td>This will be deleted when <span class="label label-important">DELETE</span>/<span class="label label-warning">PUT</span> methods can be made to work</td></tr>
		<tr><td>/book/update/{bookid}</td><td><span class="label label-info">POST</span></td><td>none</td><td>Update a book.  (This is a temporary resource until the <span class="label label-warning">PUT</span> method works.)</td><td>Book Object</td><td>"This will be deleted when <span class="label label-important">DELETE</span>/<span class="label label-warning">PUT</span> methods can be made to work.  <br/><br/>Example request body: {"jsonData":{"description":"fantastic book", "author":"Stephen King", "title":"Angry Birds", "isbn10":"1234567890", "smallThumbnailUrl":"http://labs.evanschambers.com/small-cover.jpg",  "thumbnailUrl":"http://labs.evanschambers.com/large-cover.jpg", "source":"googlebooks", "pubType":"book"}}      </td></tr>
	</table>
	</div>
	
	<div class="service-div hide">
	<h1>User Service</h1>
	<br/>
	<table class="table table-striped">
		<tr><th width="150">Resource</th><th width="100">Method</th><th width="200">Query Params</th><th width="375">Description</th><th width="200">Return Value</th><th>Comments</th></tr>
		<tr><td>/user</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find all users</td><td>Array of User Objects</td><td></td></tr>
		<tr><td>/user</td><td><span class="label label-info">POST</span></td><td>none</td><td>Add a user</td><td>User Object</td><td>Example request body: {"jsonData":{"aboutMe":"", "activationMethod":"native", "email":"null", "endDate":null,"firstName":"false", "lastName":"Evans", "middleName":"null", "password":"jamil", "photoUrl":"null", "userName":"jamil", "userTypeCode":"user", "location":"Reston, VA",  "fullName":"Robert Jamil Evans"}}</td></tr>
		<tr><td>/user/sign-in</td><td><span class="label label-success">GET</span></td><td>username & password</td><td>Validates the username and password supplied.  Return value is…</td><td>User Object</td><td>Example request URI: <span class="label">/user/sign-in?username=jgarland&password=mysecret</span></td></tr>
		<tr><td>/user/sign-in-facebook</td><td><span class="label label-info">POST</span></td><td>none</td><td>"Adds or updates a user who logged in with Facebook credentials.  For adds, the facebook photo is also uploaded."</td><td>User Object</td><td>Example request URI: <span class="label">/user/sign-in?username=jgarland&password=mysecret</span>.  <br/><br/>Example request body: {jsonData:{"aboutMe":"", "activationMethod":"native", "email":"null", "endDate":null,"firstName":"false", "lastName":"Evans", "middleName":"null", "password":"jamil", "photoUrl":"null", "userName":"jamil", "userTypeCode":"user", "location":"Reston, VA",  "fullName":"Robert Jamil Evans",  "picture":"base64encoded-data-here."}}</td></tr>
		<tr><td>/user/{username}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find a single user by USERNAME</td><td>User Object</td><td>Example request URI: <span class="label">/user/ryan</span></td></tr>
		<tr><td>/user/userId-{userid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find a single user by USERID</td><td>User Object</td><td>Example request URI: <span class="label">/user/userId-22</span></td></tr>
		<tr><td>/user/userId-{userid}</td><td><span class="label label-warning">PUT</span></td><td>none</td><td>Update a single user</td><td>User Object</td><td>Example request URI: <span class="label">/user/userId-22</span>.  <br/><br/>Example request body: {"jsonData":{"aboutMe":"", "activationMethod":"native", "email":"null", "endDate":null,"firstName":"false", "lastName":"Evans", "middleName":"null", "password":"jamil", "photoUrl":"null", "userName":"jamil", "userTypeCode":"user", "location":"Reston, VA",  "fullName":"Robert Jamil Evans"}}</td></tr>
		<tr><td>/user/userId-{userid}</td><td><span class="label label-important">DELETE</span></td><td>none</td><td>Delete a single user</td><td>none</td><td>Example request URI: <span class="label">/user/userId-22</span></td></tr>
		<tr><td>/user/userId-{userid}/photo</td><td><span class="label label-info">POST</span></td><td>none</td><td>Upload a profile photo.</td><td>String - URL of the photo just added</td><td>The request must be sent as 'multipart/form-data' and the name attribute must be 'myFile'.</td></tr>
		<tr><td>/user/{username}/following</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find a list of users that being followed by USERNAME</td><td>Array of User Objects</td><td></td></tr>
		<tr><td>/user/{username}/following</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find a list of users that USERNAME is following</td><td>Array of User Objects</td><td></td></tr>
		<tr><td>/user/{username}/follow</td><td><span class="label label-success">GET</span></td><td>targetUsername &  follow-action</td><td>"Create or remove a user-to-user "follow"" relationship"</td><td>TBD</td><td>Example request URI: /user/jgarland/follow?targetUserName=rjevans&follow-action=create /user/jgarland/follow?targetUserName=rjevans&follow-action=delete</td></tr>
		<tr><td>/book/{username}/checkIn</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find all checkins for a user by USERNAME</td><td>Array of CheckIn objects</td><td></td></tr>
		<tr><td>/book/{username}/checkIn</td><td><span class="label label-info">POST</span></td><td>none</td><td>Establish a checkin for a user with the check-in data in the request body.</td><td>CheckIn object</td><td>Example request body: {"jsonData":{"bookId":5,"chapterOrSection":"1", "latitude":"null", "longitude":"null", "narrative":"This is awesome", "venue":"null"}}</td></tr>
		<tr><td>/user/delete/userId-{userid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Delete a single user</td><td>TBD</td><td></td></tr>
		<tr><td>/user/update/userId-{userid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Update a single user</td><td>TBD</td><td>Example request body: {"jsonData":{"aboutMe":"", "activationMethod":"native", "email":"null", "endDate":null,"firstName":"false", "lastName":"Evans", "middleName":"null", "password":"jamil", "photoUrl":"null", "userName":"jamil", "userTypeCode":"user", "location":"Reston, VA",  "fullName":"Robert Jamil Evans"}}</td></tr>
	</table>
	</div>
	
	<div class="service-div hide">
	<h1>List Service</h1>
	<br/>
	<table class="table table-striped">
		<tr><th width="150">Resource</th><th width="100">Method</th><th width="200">Query Params</th><th width="375">Description</th><th width="200">Return Value</th><th>Comments</th></tr>
		<tr><td>/list/userId-{userid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find books on lists for a user by {userid}</td><td>Array of BookList objects</td><td></td></tr>
		<tr><td>/list/userId-{userid}</td><td><span class="label label-info">POST</span></td><td>none</td><td>"Add a book to user's list by USERID.   listType options are ['HAVE_READ','LIKE','WANT_TO_READ']"</td><td>BookList object</td><td>Example request body: {"jsonData":{"bookId":3,"title":null,"listType":"HAVE_READ"}}}</td></tr>
		<tr><td>/list/bookId-{bookid}</td><td><span class="label label-success">GET</span></td><td>none</td><td>Find lists for a book by BOOK_ID</td><td>Array of BookList objects</td><td></td></tr>
	</table>
	</div>
	
	<div class="service-div hide">
	<h1>Opinion Service</h1>
	</div>
	
    <div class="service-div hide">
    <h1>Checkin Service</h1>
    </div>
    </div>
    
    <br/>
 


  <hr>
  <footer>
    <p>&copy; Evans & Chambers Technology, LLC 2012</p>
  </footer>
</div>
<!--/.fluid-container-->
<!-- Le javascript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<g:javascript library="bootstrap.min"></g:javascript>

</body>
</html>



