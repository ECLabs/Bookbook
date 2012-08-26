<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>BookUP - Books Console</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<!-- Le styles -->
<link rel="stylesheet" href="${resource(dir:'css',file:'bootstrap.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'bootstrap-responsive.css')}" />
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
          <li class="active"><a href="/Bookbook/index/dashboard">Console</a></li>
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
      <li><a href="/Bookbook/index/dashboard">Dashboard</a></li>
      <li class="active"><a href="/Bookbook/book/books">Books</a></li>
<!--      <li><a href="#">Book Lists</a></li>
      <li><a href="#">Users</a></li>
      <li><a href="#">Check-Ins</a></li>
      <li><a href="#">Followers</a></li>
      <li class="dropdown"> <a class="dropdown-toggle" data-toggle="dropdown" href="#">Actions <b class="caret"></b> </a>
        <ul class="dropdown-menu">
          
        </ul>
      </li> -->
    </ul>
  </div>
  <div class="row-fluid">
    <h1 class="pull-left">Books</h1>
    <div class="alert pull-left hide" id="the-alert" style="margin-left:10px;">
        <button type="button" class="close" data-dismiss="alert">×</button>
        <span></span>
      </div>

    
    <div class="btn-toolbar" style="padding-bottom:30px; margin-top:0;">
      <!-- <div class="btn-group pull-right"> <a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#"> Re-index </a> </div> -->
      <div class="btn-group  pull-right" style="margin-right:5px;"> <a class="btn" href="#" onclick="launchAddBookModal()"> Create a Book</a>
      </div>
       
      <g:form action="books" controller="book" method="get" style="clear:none;margin:0;padding:0;">
      <div class="pull-right" style="margin-right:10px"> 
      	<span class="input-append">
        	<input class="span2" name="title" id="appendedInputButton" size="16" type="text" style="width:200px" value="${queryReturn}"><button class="btn" type="submit"><i class="icon-search"></i> Search</button><button class="btn" type="button" onclick="window.location='/Bookbook/book/books'">Reset</button>
        </span> 
      </div>
      </g:form>
      
       <!-- <span class="label pull-left" style="margin-top:14px;margin-left:15px;">Showing all ### books</span> -->
    </div>
    
    <br/>
  </div>
  <div class="row-fluid">
    <table class="table table-striped">
      <tr>
      	<th>ID</th>
        <th>Title</th>
        <th>Author</th>
        <th>Description</th>
        <th>Thumb</th>
        <th>ISBN</th>
        <th>Created</th>
        <th>Actions</th>
      </tr>
      <g:each in="${books}" status="i" var="bookInstance">
          <tr>  
          	  <td> 
          	  		<g:if test="${bookInstance.bookId == null}">
          	  			<span class="label label-success">G</span>
          	  		</g:if>
              		<g:else>
              			<span class="label label-important">${bookInstance.bookId}</span>
              		</g:else>
          	  </td>   
          	  <td class="firstRow">
          	  		<!-- <i class="icon-book"></i>  --> 
          	  		<a href="/Bookbook/api/book/${bookInstance.bookId}" id="book-${bookInstance.bookId}" onclick="showEditWindow(this); return false;">
          	  		${fieldValue(bean: bookInstance, field: "title")}
          	  		</a> 
          	  		
          	  </td>          	
              <td>${fieldValue(bean: bookInstance, field: "author")}</td>
              <td width="300"><g:truncate maxlength="200">${fieldValue(bean: bookInstance, field: "description")}</g:truncate></td>
              <td><img src="${bookInstance.smallThumbnailUrl}"/></td>
              <td>${fieldValue(bean: bookInstance, field: "isbn10")}</td>
              
              <td width="100">
              	<g:if test="${bookInstance.bookId == null}">
              		&nbsp;
              	</g:if>
              	<g:else>
              		${fieldValue(bean: bookInstance, field: "createDate")}
              	</g:else>
              </td>    
              <td width="100">
              	<g:if test="${bookInstance.bookId == null}">
              		<a id="jamil" class="btn btn-success" style="width:78px" href="#" onclick="addGoogleBook(this)">Add Book</a>
              		<span class="jsonForAdd" style="display:none">${jsonBookArray[i]}</span>
              	</g:if>
              	<g:else>
              		<a id="jamil" class="btn btn-info" style="width:78px" href="#" onclick="showEditWindow($('#book-${bookInstance.bookId}'));">Update Book</a>
              	</g:else>
              </td>
          </tr>
      </g:each>
    </table>
  </div>
  <div class="modal hide fade" id="myModal">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
      <h3><i class="icon-book"></i> <span id="popupTitle">Default</span></h3>
    </div>
    <div class="modal-body">
      <p>
      
      <div class="alert hide" id="main-alert">
        <button type="button" class="close" data-dismiss="alert">×</button>
        <span>Edit the JSON below and save.</span>
      </div>
      
      <h4>Edit JSON</h4>
      <textarea rows="19" id="jsonCode" style="width:520px; background-color:whiteSmoke"></textarea>
      </p>
    </div>
    <div class="modal-footer"> <span id="spinner"><img src="${resource(dir:'images',file:'spinner_popup.gif')}"/> Saving...</span>  
    <a href="#" class="btn" onclick="$('#myModal').modal('hide');">Close</a> 
    <a href="#" class="btn btn-primary" id="saveBtn" onclick="$('#spinner').show(); updateBook($('#popupBookId').val())">Save</a> 
    <a href="#" class="btn btn-danger pull-left" id="delBtn" onclick="$('#spinner').show(); deleteBook($('#popupBookId').val());">Delete this book</a> </div>
    <input type="hidden" id="popupBookId" />
  </div>
  
  <div class="modal hide fade" id="myModal2">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
      <h3><i class="icon-book"></i> <span id="popupTitle2">Default</span></h3>
    </div>
    <div class="modal-body">
      <p>
      
      <div class="alert hide" id="main-alert2">
        <button type="button" class="close" data-dismiss="alert">×</button>
        <span>Edit the JSON below and save.</span>
      </div>
      
      <h4>Type your JSON below</h4>
      <textarea rows="10" id="jsonCode2" style="width:520px; background-color:whiteSmoke"></textarea>
      </p>
    </div>
    <div class="modal-footer"> <span id="spinner2"><img src="${resource(dir:'images',file:'spinner_popup.gif')}"/> Saving...</span>  
    <a href="#" class="btn" onclick="$('#myModal2').modal('hide');">Close</a> 
    <a href="#" class="btn btn-primary" id="saveBtn2" onclick="$('#spinner2').show(); addBook($('#jsonCode2').val())">Save</a> </div>
  </div>
  

  <div  style="text-align:center">
  	<span class="label">Displaying ${books.size()} results</span>
	</div>
	

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
<g:javascript>
	var g_refreshPage = false;
	$('#spinner').hide();
	$('#spinner2').hide();
	
	$('#myModal').on('hidden', function () {
	  if(g_refreshPage) {
	  	
	  	document.location.reload();
	  }
	})
	
	$('#myModal2').on('hidden', function () {
	  if(g_refreshPage) {
	  	
	  	document.location.reload();
	  }
	})
	
	function showEditWindow(element) {
		var url = $(element).attr('href');
		var title = $(element).html();
		var bookId = $(element).attr('id');
		bookId = bookId.substr(5, bookId.length);
		$.ajax({
   			url: url,
   			type: "GET"
   		}).done(function(msg) { 
   			var resp = JSON.stringify(msg, undefined, 2); 
   			$('#popupTitle').text(title);
   			$('#jsonCode').val(resp);
   			$('#myModal').modal('show'); 
   			$('#popupBookId').val(bookId);
   		}).fail(function(jqXHR, textStatus) {
	  		alert( "Request failed: " + textStatus );
		});
		return false;
	}
	
	function updateBook(id) {
		var url = "/Bookbook/api/book"
		url = url + "/update/" + id
   		$.ajax({
   			url: url,
   			type: 'POST',
   			data: {jsondata : $('#jsonCode').val() }
   		}).done(function(msg) { 
   			$('#main-alert span').html('Book update successful!');
   			$('#main-alert').removeClass('alert-error').addClass('alert-success');
   			g_refreshPage = true;
   		}).fail(function(jqXHR, textStatus) {
		  	$('#main-alert span').html('Error updating book: ' + textStatus);
   			$('#main-alert').removeClass('alert-success').addClass('alert-error');
		}).always(function() {
			$('#spinner').hide();
			$('#main-alert').show().delay(3000).fadeOut('slow');
		});
	}
	function deleteBook(id) {
		var url = "/Bookbook/api/book/delete/";
		url = url + id;
		
		$('#jsonCode').attr('disabled',true);
		$('#saveBtn').attr('readonly',true);
		
   		$.ajax({
   			url: url,
   			type: 'GET'
   		}).done(function(msg) { 
   			$('#main-alert span').html('Book delete successful!');
   			$('#main-alert').removeClass('alert-error').addClass('alert-success');
   			g_refreshPage = true;
   			$('#myModal').modal('hide'); // hide the window after a delete  			
   		}).fail(function(jqXHR, textStatus) {
		  	$('#main-alert span').html('Error deleting book: ' + textStatus);
   			$('#main-alert').removeClass('alert-success').addClass('alert-error');
   			$('#jsonCode').attr('disabled',false);
			$('#saveBtn').attr('readonly',false);		
		}).always(function() {
			$('#spinner').hide();
			$('#main-alert').show().delay(3000).fadeOut('slow')
		});
		
    }	
	
	function addBook(json) {
		var url = "/Bookbook/api/book";
    	$.ajax({
    			url: url,
    			type: "POST",
    			statusCode: {
					409: function() {
  						var msg = "Attempt to add a duplicate book - another with the same ISBN already exists.";
  						$('#main-alert2 span').html(msg);
   						$('#main-alert2').removeClass('alert-success').addClass('alert-error');
   						$('#spinner2').hide();
						$('#main-alert2').show().delay(3000).fadeOut('slow');
					},
					500: function() {
  						var msg = "BookUp is having problems... see the application log for more details.";
  						$('#main-alert2 span').html(msg);
   						$('#main-alert2').removeClass('alert-success').addClass('alert-error');
   						$('#spinner2').hide();
						$('#main-alert2').show().delay(3000).fadeOut('slow');
					}
				},
    			data: {jsondata : json }
    	}).done(function(msg) { 
    		$('#main-alert2 span').html('Book add successful!');
   			$('#main-alert2').removeClass('alert-error').addClass('alert-success');
   			g_refreshPage = true;
   			$('#myModal2').modal('hide'); // hide the window after a delete 
   		}).fail(function(jqXHR, textStatus) {
		 	var msg = "BookUp is having problems... see the application log for more details.";
			$('#main-alert2 span').html(msg);
			$('#main-alert2').removeClass('alert-success').addClass('alert-error');
		}).always(function() {
			$('#spinner').hide();
			$('#main-alert2').show().delay(3000).fadeOut('slow');
		});
    }	
		
	function launchAddBookModal() {
		$('#myModal2').modal('show');
		$('#popupTitle2').text('Add a book');
		var json = '({"author":"","description":"","isbn10":"","pubType":"","smallThumbnailUrl":"","source":"","thumbnailUrl":"","title":""})';
		$('#jsonCode2').val(JSON.stringify(eval(json), undefined, 2));
	}	
	
	function addGoogleBook(addBtn) {
		var jsonSpan = $(addBtn).next('span');
		var json = $(jsonSpan).text();
		
		var url = "/Bookbook/api/book";
    	$.ajax({
    			url: url,
    			type: "POST",
    			statusCode: {
					409: function() {
  						var msg = "Attempt to add a duplicate book - another with the same ISBN already exists.";
  						$('#the-alert span').html(msg);
   						$('#the-alert').removeClass('alert-success').addClass('alert-error');
   						// $('#spinner2').hide();
						$('#the-alert').show().delay(3000).fadeOut('slow');
					},
					500: function() {
  						var msg = "BookUp is having problems... see the application log for more details.";
  						$('#the-alert span').html(msg);
   						$('#the-alert').removeClass('alert-success').addClass('alert-error');
   						//$('#spinner2').hide();
						$('#the-alert').show().delay(3000).fadeOut('slow');
					}
				},
    			data: {jsondata : json }
    	}).done(function(msg) { 
    		$('#the-alert span').html('Book added successfully.   Refresh the page to see the results.');
   			$('#the-alert').removeClass('alert-error').addClass('alert-success');
   			g_refreshPage = true;
   			$('#myModal2').modal('hide'); // hide the window after a delete 
   			document.location.reload();
   		}).fail(function(jqXHR, textStatus) {
		 	var msg = "BookUp is having problems... see the application log for more details.";
			$('#the-alert span').html(msg);
			$('#the-alert').removeClass('alert-success').addClass('alert-error');
		}).always(function() {
			//$('#spinner').hide();
			$('#the-alert').show().delay(3000).fadeOut('slow');
		});
	}		

	</g:javascript>
</body>
</html>
