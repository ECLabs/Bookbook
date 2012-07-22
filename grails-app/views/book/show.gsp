
<%@ page import="bookbook.domain.Book" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'book.label', default: 'Book')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.author.label" default="Author" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "author")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.bbRating.label" default="Bb Rating" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "bbRating")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.bookId.label" default="Book Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "bookId")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.createDate.label" default="Create Date" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "createDate")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.createdBy.label" default="Created By" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "createdBy")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.creatorUserId.label" default="Creator User Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "creatorUserId")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.description.label" default="Description" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "description")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.haveReadCount.label" default="Have Read Count" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "haveReadCount")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.isbn10.label" default="Isbn10" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "isbn10")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.opinionCount.label" default="Opinion Count" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "opinionCount")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.pubType.label" default="Pub Type" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "pubType")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.smallThumbnailUrl.label" default="Small Thumbnail Url" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "smallThumbnailUrl")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.source.label" default="Source" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "source")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.thumbnailUrl.label" default="Thumbnail Url" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "thumbnailUrl")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.title.label" default="Title" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "title")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="book.wantToReadCount.label" default="Want To Read Count" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: bookInstance, field: "wantToReadCount")}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${bookInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
