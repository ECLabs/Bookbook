<html>
    <head>
        <title>Welcome to Grails</title>
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
            margin-left:280px;
            margin-right:20px;
        }
        </style>
        <g:javascript library="jquery-1.5.1.min" />
        <g:javascript>
        
        	function sendPost() {
				var url = "book"
        		$.ajax({
        			url: url,
        			type: "POST",
        			data: {jsondata : $('#jsondata').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function sendPostUpdate() {
				var url = "book"
				var id = $('#bookId').val()
				url = url + "/update/" + id
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : $('#jsondata').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function sendPostUser() {
				var url = "book"
				//var id = $('#bookId').val()
				url = url + "/update/" + id
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : $('#jsondata').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        	
        	function createCheckIn() {
				var url = "user"
				var userName = $('#bookId').val()
				url = url + "/" + userName + "/checkIn"
        		$.ajax({
        			url: url,
        			type: 'POST',
        			data: {jsondata : $('#jsondata').val() }
        		}).done(function(msg) { alert("data saved" + msg); });
        	}
        </g:javascript>
    </head>
    <body>
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
        <div id="pageBody">
            <h1>Welcome to Grails</h1>
            <p>Congratulations, you have successfully started your first Grails application! At the moment
            this is the default page, feel free to modify it to either redirect to a controller or display whatever
            content you may choose. Below is a list of controllers that are currently deployed in this application,
            click on each to execute its default action:</p>

            <div id="controllerList" class="dialog">
                <h2>Available Controllers:</h2>
                <ul>
                    <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
                        <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
                    </g:each>
                </ul>
            </div>
            
            <%--<form action="http://localhost:8080/Bookbook/book" method="post">
	                <input type="text" value="testing123" name="jsontext" />
	                <input type="submit" value="go!" name="submit" />
                </form>--%>
                <textarea id="jsondata" name="jsondata" style="width: 600px;">{"bookId":3,"checkInDate":"Sun Nov 13 22:51:42 EST 2011","class":"bookbook.DummyCheckIn","createDate":null,"id":123,"latitude":"12 North","longitude":"34 West","narrative":"this is what i think of this book!","userName":"evansro","venue":"Whole Food, Reston, VA","chapterOrSection":"Chapter 19"}              
                                            
                                </textarea><br/>
                <input type="text" value="evansro" name="text" id="bookId" />
                <input type="submit" value="Add book" name="button" onclick="sendPost('POST');" /> &nbsp;
                <input type="submit" value="Update book" name="button" onclick="sendPostUpdate();" />
        		<input type="submit" value="Update user" name="button" onclick="sendPostUser()" />
        		<input type="submit" value="createCheckIn" name="button" onclick="createCheckIn()" />
        </div>
    </body>
</html>
