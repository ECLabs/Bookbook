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
            float:left;
        }
        h2 {
        	margin-top: 50px;
        }
        </style>
        <g:javascript library="jquery-1.5.1.min" />
        <g:javascript>
        	
        	function addToList() {
				var url = "/Bookbook/api/list/";
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
        		}).done(function(msg) { alert("data saved - refreshing page"); document.location.reload(); });
        	}
        	
        	function deleteListEntry() {
				var url = "/Bookbook/api/list/delete/bookListId-"
				var id = $('#lists_delete').val()
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
                        
                            <td>${fieldValue(bean: bl, field: "book.bookId")}</td>
                        
                            <td>${fieldValue(bean: bl, field: "user.userId")}</td>
                        
                            <td>${fieldValue(bean: bl, field: "createDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table> 	
                
                <h2>Delete a list entry</h2>
                <table style="width: 600px;border-collapse:collapse" border="1" bordercolor="gray">
					<tr>
						<td width="120">BookListId:</td>
						<td>
							
							<g:select name="lists_delete"
					          from="${bookLists}"
					          optionValue="${{'BookListId'+it?.bookListId+':BookId'+it?.book.bookId+':UserId'+it?.user.userId}}"
					          optionKey="bookListId" />	
	          			</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Delete list entry" name="button" onclick="deleteListEntry()" />
						</td>
					</tr>
				</table>
	    </div>
	    </div>
    </body>
</html>
