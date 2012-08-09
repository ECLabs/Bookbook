<%@page import="java.awt.event.ItemEvent"%>
<html>
    <head>
        <title>BookUp Admin Console</title>
        <meta name="layout" content="main" />
        <style type="text/css" media="screen">

        #nav {
            margin-top:20px;
            margin-left:30px;
            width:228px;
            float:left;

        }
        .homePagePanel * {
            margin:0px;
        }
        .homePagePanel .panelBody ul {
            list-style-type:none;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody h1 {
            text-transform:uppercase;
            font-size:1.1em;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody {
            background: url(images/leftnav_midstretch.png) repeat-y top;
            margin:0px;
            padding:15px;
        }
        .homePagePanel .panelBtm {
            background: url(images/leftnav_btm.png) no-repeat top;
            height:20px;
            margin:0px;
        }

        .homePagePanel .panelTop {
            background: url(images/leftnav_top.png) no-repeat top;
            height:11px;
            margin:0px;
        }
        h2 {
            margin-top:15px;
            margin-bottom:15px;
            font-size:1.2em;
        }
        #pageBody {
            margin-left:30px;
            margin-right:20px;
        }
        </style>
        <g:javascript library="jquery-1.5.1.min" />
        <g:javascript>
        
        	function addBook() {
				var url = "api/book"
        		$.ajax({
        			url: url,
        			type: "POST",
        			data: {jsondata : $('#jsondata_addbook').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function addUser() {
				var url = "api/user"
        		$.ajax({
        			url: url,
        			type: "POST",
        			data: {jsondata : $('#jsondata_adduser').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function updateBook() {
				var url = "api/book"
				var id = $('#books_updatebook').val()
				url = url + "/update/" + id
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : $('#jsondata_updatebook').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function getBook() {
				var url = "api/book/"
				var id = $('#books_updatebook').val()
				url = url + id
				/*
        		$.ajax({
        			url: url,
        			type: 'GET'
        		}).done(function(msg) { $('#jsondata_updatebook').val(msg); });
        		*/
        		$.getJSON(url, function(data) {
        			$('#jsondata_updatebook').val(JSON.stringify(data));
        		});
        	}
        	
        	function getUser() {
				var url = "api/user/userId-"
				var id = $('#users_updateuser').val()
				url = url + id

        		$.getJSON(url, function(data) {
        			$('#jsondata_updateuser').val(JSON.stringify(data));
        		});
        	}
        	
        	function updateUser() {
				var url = "api/user"
				var id = $('#users_updateuser').val()
				url = url + "/update/userId-" + id
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : $('#jsondata_updateuser').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function deleteUser() {
				var url = "api/user/delete/"
				var id = $('#users_deleteuser').val()
				url = url + id
        		$.ajax({
        			url: url,
        			type: 'GET'
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function deleteBook() {
				var url = "api/book/delete/"
				var id = $('#books_deletebook').val()
				url = url + id
        		$.ajax({
        			url: url,
        			type: 'GET'
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function createCheckIn() {
				var url = "api/book";
				var bookId = $('#books_createcheckin').val();
				var userId = $('#users_createcheckin').val();
				var chapterOrSection = $('#chapterOrSection').val();
				var narrative = $('#narrative').val();
				url = url + "/" + bookId + "/checkIn";
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : "{'userId':"+userId+",'narrative':'"+narrative+"','chapterOrSection':'"+chapterOrSection+"','venue':null,'latitude':null,'longitude':null}" }
        		}).done(function(msg) { alert("data saved " + msg); });
        	}
        	function addToList() {
				var url = "api/list/";
				var bookId = $('#books').val();
				var userId = $('#users').val();
				var listType = $('#listTypes').val();
				var listTitle = $('#listTitle').val();
				var creationType = 'default-manual';
				url = url + 'userId-' + userId;
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : "{'bookId':"+bookId+",'listType':'" +listType+"','listTitle':'"+listTitle+"','creationType':'"+creationType+"'}"}
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        </g:javascript>
    </head>
    <body>
    	<!-- 
        <div id="nav">
            <div class="homePagePanel">
                <div class="panelTop"></div>
                <div class="panelBody">
                    <h1>Application Status</h1>
                    <ul>
                        <li>App version: <g:meta name="app.version"></g:meta></li>
                        <li>Grails version: <g:meta name="app.grails.version"></g:meta></li>
                        <li>Groovy version: ${org.codehaus.groovy.runtime.InvokerHelper.getVersion()}</li>
                        <li>JVM version: ${System.getProperty('java.version')}</li>
                        <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
                        <li>Domains: ${grailsApplication.domainClasses.size()}</li>
                        <li>Services: ${grailsApplication.serviceClasses.size()}</li>
                        <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
                    </ul>
                    <h1>Installed Plugins</h1>
                    <ul>
                        <g:set var="pluginManager"
                               value="${applicationContext.getBean('pluginManager')}"></g:set>

                        <g:each var="plugin" in="${pluginManager.allPlugins}">
                            <li>${plugin.name} - ${plugin.version}</li>
                        </g:each>

                    </ul>
                </div>
                <div class="panelBtm">
                
                </div>
            </div>
        </div>
        -->
        <div id="pageBody">
            <h1>BookUp REST API Console</h1>
            <p>Use this administrative console to learn how to use the API.  We've provided sample JSON for POST and PUT operations.  Actions taken via this console directly affect the online datasource.</p>
            
            <h1>Book Service</h1>
            	<h2>Add a book</h2>
            	<table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">Book JSON:</td>
						<td>
							<textarea id="jsondata_addbook" name="jsondata" style="width: 450px;">{ "author": "Harvey Karp", "creatorUserId": 187, "description": "A pediatrician and child development ...", "isbn10": "0553381466", "pubType": null, "smallThumbnailUrl": "http://bks7.books.google.com/books?id=NIo3RQmYLqQC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api", "source": "admin", "thumbnailUrl": "http://bks7.books.google.com/books?id=NIo3RQmYLqQC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "title": "The Happiest Baby on the Block"}</textarea>
	          			</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Add book" name="button" onclick="addBook();" />
						</td>
					</tr>
				</table>
                
            	<h2>Update a book</h2>
            	<table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">Book ID:</td>
						<td>
							<g:select name="books_updatebook"
					          from="${books}"
					          optionValue="${{it.title+':'+it.bookId}}"
					          optionKey="bookId" />
							<input type="submit" value="Get book JSON" name="button" onclick="getBook();" />					
	          			</td>
					</tr>
					<tr>
						<td width="120">Book JSON:</td>
						<td>
							<textarea id="jsondata_updatebook" name="jsondata" style="width: 450px;">Select a book</textarea>
	          			</td>
					</tr>
					
					<tr>
						<td colspan="2">
							<input type="submit" value="Update book" name="button" onclick="updateBook();" />
						</td>
					</tr>
				</table>
				
				<h2>Delete a book</h2>
            	<table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">Book ID:</td>
						<td>
							<g:select name="books_deletebook"
					          from="${books}"
					          optionValue="${{it.title+':'+it.bookId}}"
					          optionKey="bookId" />					
	          			</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Delete book" name="button" onclick="deleteBook();" />
						</td>
					</tr>
				</table>
				
				<h2>Create Check-In</h2>
            	<table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">Book ID:</td>
						<td>
							<g:select name="books_createcheckin"
					          from="${books}"
					          optionValue="${{it.title+':'+it.bookId}}"
					          optionKey="bookId" />					
	          			</td>
					</tr>
					<tr>
						<td width="120">User ID:</td>
						<td>
							<g:select name="users_createcheckin"
					          from="${users}"
					          optionValue="${{it.userName + ':'+ it.userId}}"
					          optionKey="userId" />					
	          			</td>
					</tr>
					<tr>
						<td width="120">ChapterOrSection:</td>
						<td>
							<input type="text" value="" name="text" id="chapterOrSection" />					
	          			</td>
					</tr>
					<tr>
						<td width="120">Narrative:</td>
						<td>
							<input type="text" value="" name="text" id="narrative" />					
	          			</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="createCheckIn" name="button" onclick="createCheckIn()" />
						</td>
					</tr>
					<!-- {"bookId":3,"checkInDate":"Sun Nov 13 22:51:42 EST 2011","class":"bookbook.DummyCheckIn","createDate":null,"id":123,"latitude":"12 North","longitude":"34 West","narrative":"this is what i think of this book!","userName":"evansro","venue":"Whole Food, Reston, VA","chapterOrSection":"Chapter 19"} -->
				</table>
				
				<h2>All Books</h2>
				<table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="bookId" title="${message(code: 'book.bookId.label', default: 'BookId')}" />
                        
                            <g:sortableColumn property="author" title="${message(code: 'book.author.label', default: 'Author')}" />
                        
                            <g:sortableColumn property="title" title="${message(code: 'book.title.label', default: 'Title')}" />
                        
                            <g:sortableColumn property="description" title="${message(code: 'book.description.label', default: 'Description')}" />
                        
                        	<g:sortableColumn property="isbn10" title="${message(code: 'book.isbn10.label', default: 'ISBN10')}" />
                        
                            <g:sortableColumn property="createDate" title="${message(code: 'book.createDate.label', default: 'Create Date')}" />
                    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${books}" status="i" var="bookInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="showbook" id="${bookInstance.bookId}">${fieldValue(bean: bookInstance, field: "bookId")}</g:link></td>
                        
                            <td>${fieldValue(bean: bookInstance, field: "author")}</td>
                        
                            <td>${fieldValue(bean: bookInstance, field: "title")}</td>
                        
                            <td>${fieldValue(bean: bookInstance, field: "description")}</td>
                        
                            <td>${fieldValue(bean: bookInstance, field: "isbn10")}</td>
                        
                            <td>${fieldValue(bean: bookInstance, field: "createDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
				
				<h2>All Check-ins</h2>
				<table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="checkInId" title="${message(code: 'checkIn.checkInId.label', default: 'CheckInId')}" />
                        
                            <g:sortableColumn property="bookId" title="${message(code: 'checkIn.bookId.label', default: 'BookId')}" />
                        
                            <g:sortableColumn property="userId" title="${message(code: 'checkIn.userId.label', default: 'UserId')}" />
                        
                            <g:sortableColumn property="narrative" title="${message(code: 'checkIn.narrative.label', default: 'Narrative')}" />
                        
                        	<g:sortableColumn property="chapterOrSection" title="${message(code: 'checkIn.chapterOrSection.label', default: 'ChapterOrSection')}" />
                        
                            <g:sortableColumn property="checkInDate" title="${message(code: 'checkIn.checkInDate.label', default: 'Check-In Date')}" />
                    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${checkIns}" status="i" var="ciInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="showcheckin" id="${ciInstance.id}">${fieldValue(bean: ciInstance, field: "checkInId")}</g:link></td>
                        
                            <td>${fieldValue(bean: ciInstance, field: "bookId")}</td>
                        
                            <td>${fieldValue(bean: ciInstance, field: "userId")}</td>
                        
                            <td>${fieldValue(bean: ciInstance, field: "narrative")}</td>
                        
                            <td>${fieldValue(bean: ciInstance, field: "chapterOrSection")}</td>
                        
                            <td>${fieldValue(bean: ciInstance, field: "checkInDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
                
                <h1>User Service</h1>
            	<h2>Add a user</h2>
                <table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">User JSON:</td>
						<td>
							<textarea id="jsondata_adduser" name="jsondata_adduser" style="width: 450px;">{"aboutMe":"","activationMethod":"native","createDate":"Sun Jul 01 16:30:30 EDT 2012","email":"test@test.com","endDate":null,"firstName":"false","lastLoginDate":null,"lastName":"null","middleName":"null","fullName":"Robert Houston","location":"Reston, VA","password":"test","photoUrl":"","updateDate":null,"userName":"test","userTypeCode":"user"}</textarea>
	          			</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Add user" name="button" onclick="addUser();" />
						</td>
					</tr>
				</table>
				
				<h2>Update a user</h2>
                <table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">UserName:</td>
						<td>
							<g:select name="users_updateuser"
					          from="${users}"
					          optionValue="${{it.userName+':'+it.userId}}"
					          optionKey="userId" />	
							<input type="submit" value="Get user JSON" name="button" onclick="getUser();" />					
	          			</td>
					</tr>
					<tr>
						<td width="120">User JSON:</td>
						<td>
							<textarea id="jsondata_updateuser" name="jsondata_updateuser" style="width: 450px;"></textarea>
	          			</td>
					</tr>
					
					<tr>
						<td colspan="2">
							<input type="submit" value="Update user" name="button" onclick="updateUser()" />
						</td>
					</tr>
				</table>
				
				<h2>Delete a user</h2>
                <table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">UserID:</td>
						<td>
							<g:select name="books_deleteuser"
					          from="${users}"
					          optionValue="${{it.userName+':'+it.userId}}"
					          optionKey="userId" />					
	          			</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Delete user" name="button" onclick="deleteUser()" />
						</td>
					</tr>
				</table>
				
        		<h2>Upload Photo:</h2>
        		<g:form action="updatePhoto" controller="user" method="post" enctype="multipart/form-data">
        		<table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">User ID:</td>
						<td>
							<g:select name="userId"
					          from="${users}"
					          optionValue="${{it.userName+':'+it.userId}}"
					          optionKey="userId" />							
	          			</td>
					</tr>
					<tr>
						<td width="120">Select image:</td>
						<td>
							<input type="file" name="myFile" />         			
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" />
						</td>
					</tr>
				</table>
				</g:form>
			    
			    <h2>All Users</h2>
				<table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'user.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="userTypeCode" title="${message(code: 'user.userTypeCode.label', default: 'Type')}" />
                        
                            <g:sortableColumn property="userName" title="${message(code: 'user.userName.label', default: 'UserName')}" />
                            
                            <g:sortableColumn property="password" title="${message(code: 'user.password.label', default: 'Password')}" />
                            
                            <g:sortableColumn property="email" title="${message(code: 'user.email.label', default: 'Email')}" />
                        
                        	<g:sortableColumn property="photoUrl" title="${message(code: 'user.photoUrl.label', default: 'PhotoURL')}" />
                        
                        	<g:sortableColumn property="createDate" title="${message(code: 'user.createDate.label', default: 'Created')}" />
                        
                        	<g:sortableColumn property="endDate" title="${message(code: 'user.endDate.label', default: 'End Date')}" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${users}" status="i" var="userInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="showuser" id="${userInstance.userId}">${fieldValue(bean: userInstance, field: "userId")}</g:link></td>
                        
                            <td>${fieldValue(bean: userInstance, field: "userTypeCode")}</td>
                        
                            <td>${fieldValue(bean: userInstance, field: "userName")}</td>   
                            
                            <td>${fieldValue(bean: userInstance, field: "password")}</td>                      
                        
                            <td>${fieldValue(bean: userInstance, field: "email")}</td>
                          	
                          	<td>${fieldValue(bean: userInstance, field: "photoUrl")}</td>
                          	
                          	<td>${fieldValue(bean: userInstance, field: "createDate")}</td>
                          	
                          	<td>${fieldValue(bean: userInstance, field: "endDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
    
    			<h1>List Service</h1>
    			<h2>Add a book to a list</h2>
				<table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">User:</td>
						<td>
							<g:select name="users"
					          from="${users}"
					          optionValue="${{it.userName + ':'+ it.userId}}"
					          optionKey="userId" />
	          			</td>
					</tr>
					<tr>
						<td>Book:</td>
						<td>
							<g:select name="books"
					          from="${books}"
					          optionValue="${{it.title+':'+it.bookId}}"
					          optionKey="bookId" />
	          			</td>
					</tr>
					<tr>
						<td>List Type:</td>
						<td>
							<g:select name="listTypes" from="${listTypes}"  />
							* USER_LIST is not yet supported
	          			</td>
					</tr>
					<tr>
						<td>List Title:</td>
						<td>
							<g:textField name="listTitle" value="Title" />
							* Title only applies to USER_LIST type
	          			</td>
					</tr>
					<tr>
						<td colspan=2>
							<input type="button" value="Add to List" name="button" onclick="addToList();" />
	          			</td>
					</tr>
				</table>   
				
				<h2>All Book Lists</h2>
				<table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="bookListId" title="BookListId" />
                            
                            <g:sortableColumn property="type" title="List Type" />
                        
                            <g:sortableColumn property="bookId" title="Book ID" />
                        
                            <g:sortableColumn property="userId" title="User ID" />
                                                
                            <g:sortableColumn property="createDate" title="Create Date" />
                    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${bookLists}" status="i" var="bl">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="showlist" id="${bl.bookListId}">${fieldValue(bean: bl, field: "bookListId")}</g:link></td>
                            
                            <td>${fieldValue(bean: bl, field: "type")}</td>
                        
                            <td>${fieldValue(bean: bl, field: "bookId")}</td>
                        
                            <td>${fieldValue(bean: bl, field: "userId")}</td>
                        
                            <td>${fieldValue(bean: bl, field: "createDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table> 		
	    </div>
    </body>
</html>
