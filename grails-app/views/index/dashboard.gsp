<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>BookUP - Dashboard</title>
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
          <li class="active"><a href="dashboard.html">Console</a></li>
          <li><a href="#">API Documentation</a></li>
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
      <li class="active"><a href="/Bookbook/index/dashboard">Dashboard</a></li>
      <li><a href="/Bookbook/book/books">Books</a></li>
      <li><a href="/Bookbook/book/comments">Comments</a></li>
      <li><a href="/Bookbook/book/activity">Book Activity</a></li>
      <li><a href="/Bookbook/user/activity">Friend Activity</a></li>
<!--       <li><a href="#">Book Lists</a></li>
      <li><a href="#">Users</a></li>
      <li><a href="#">Check-Ins</a></li>
      <li><a href="#">Followers</a></li>
      <li class="dropdown"> <a class="dropdown-toggle" data-toggle="dropdown" href="#">Dropdown <b class="caret"></b> </a>
        <ul class="dropdown-menu">
          
        </ul>
      </li> -->
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
    <div class="hero-unit">
	  <h1>BookUP API Console v2.0</h1>
	  <p>The BookUP API console is used by privileged administrators to manage books, users, search indexes, and other BookUP entities.  It can also be used as a test harness.</p>
	  <p>
	    <a class="btn btn-primary btn-large" href="/Bookbook/index">
	      Go to Older Version of the Console
	    </a>
	    <a class="btn btn-success btn-primary btn-large" href="/Bookbook/book/books">
	      Jump to Books
	    </a>
	  </p>
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
