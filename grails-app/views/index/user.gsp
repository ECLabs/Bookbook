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
            background: url(/Bookbook/images/leftnav_midstretch.png) repeat-y top;
            margin:0px;
            padding:15px;
        }
        .homePagePanel .panelBtm {
            background: url(/Bookbook/images/leftnav_btm.png) no-repeat top;
            height:20px;
            margin:0px;
        }

        .homePagePanel .panelTop {
            background: url(/Bookbook/images/leftnav_top.png) no-repeat top;
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
            margin-right:15px;
            width:600px;
            float:left;
        }
        h2 {
        	margin-top: 50px;
        }
        </style>
        <g:javascript library="jquery-1.5.1.min" />
        <g:javascript>
        
        	function addUser() {
				var url = "/Bookbook/api/user"
        		$.ajax({
        			url: url,
        			type: "POST",
        			data: {jsondata : $('#jsondata_adduser').val() }
        		}).done(function(msg) { alert("data saved.  Reloading page."); document.location.reload();});
        	}
        	
        	function getUser() {
				var url = "/Bookbook/api/user/userId-"
				var id = $('#users_updateuser').val()
				url = url + id

        		$.getJSON(url, function(data) {
        			$('#jsondata_updateuser').val(JSON.stringify(data));
        		});
        	}
        	
        	function updateUser() {
				var url = "/Bookbook/api/user"
				var id = $('#users_updateuser').val()
				url = url + "/update/userId-" + id
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : $('#jsondata_updateuser').val() }
        		}).done(function(msg) { alert("data saved.  Reloading page."); document.location.reload();});
        	}
        	
        	function deleteUser() {
				var url = "/Bookbook/api/user/delete/"
				var id = $('#users_deleteuser').val()
				url = url + id
        		$.ajax({
        			url: url,
        			type: 'GET'
        		}).done(function(msg) { alert("data saved.  Reloading page."); document.location.reload();});
        	}

        </g:javascript>
    </head>
    <body>
    	 
        <div id="nav">
            <div class="homePagePanel">
                <div class="panelTop"></div>
                <div class="panelBody">
                	<h1>Components</h1>
                    <ul>
                        <li><a href="/Bookbook/index">Books</a></li>                    
                        <li><a href="/Bookbook/index/list">Book Lists</a></li>
                        <li><a href="/Bookbook/index/user">Users</a></li>
                    </ul>
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
                </div>
                <div class="panelBtm">
                
                </div>
            </div>
        </div>
        
        <div id="pageBody">
            <h1>BookUp REST API Console</h1>
            <p>Use this administrative console to learn how to use the API.  We've provided sample JSON for POST and PUT operations.  Actions taken via this console directly affect the online datasource.</p>
                
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
                        
                            <td><g:link style="text-decoration:underline;"¬†action="showuser" id="${userInstance.userId}">${fieldValue(bean: userInstance, field: "userId")}</g:link></td>
                        
                            <td>${fieldValue(bean: userInstance, field: "userTypeCode")}</td>
                        
                            <td>${fieldValue(bean: userInstance, field: "userName")}</td>   
                            
                            <td>${fieldValue(bean: userInstance, field: "password")}</td>                      
                        
                            <td>${fieldValue(bean: userInstance, field: "email")}</td>
                          	
                          	<td><a style="text-decoration:underline;" href="${fieldValue(bean: userInstance, field: "photoUrl")}">${fieldValue(bean: userInstance, field: "photoUrl")}</a></td>
                          	
                          	<td>${fieldValue(bean: userInstance, field: "createDate")}</td>
                          	
                          	<td>${fieldValue(bean: userInstance, field: "endDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>

	    </div>
    </body>
</html>
