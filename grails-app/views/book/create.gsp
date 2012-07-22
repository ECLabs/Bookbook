

<%@ page import="bookbook.domain.Book" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'book.label', default: 'Book')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${bookInstance}">
            <div class="errors">
                <g:renderErrors bean="${bookInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="author"><g:message code="book.author.label" default="Author" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'author', 'errors')}">
                                    <g:textField name="author" value="${bookInstance?.author}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="bbRating"><g:message code="book.bbRating.label" default="Bb Rating" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'bbRating', 'errors')}">
                                    <g:textField name="bbRating" value="${bookInstance?.bbRating}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="bookId"><g:message code="book.bookId.label" default="Book Id" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'bookId', 'errors')}">
                                    <g:textField name="bookId" value="${fieldValue(bean: bookInstance, field: 'bookId')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="createDate"><g:message code="book.createDate.label" default="Create Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'createDate', 'errors')}">
                                    <g:textField name="createDate" value="${bookInstance?.createDate}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="createdBy"><g:message code="book.createdBy.label" default="Created By" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'createdBy', 'errors')}">
                                    <g:textField name="createdBy" value="${bookInstance?.createdBy}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="creatorUserId"><g:message code="book.creatorUserId.label" default="Creator User Id" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'creatorUserId', 'errors')}">
                                    <g:textField name="creatorUserId" value="${bookInstance?.creatorUserId}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="description"><g:message code="book.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'description', 'errors')}">
                                    <g:textField name="description" value="${bookInstance?.description}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="haveReadCount"><g:message code="book.haveReadCount.label" default="Have Read Count" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'haveReadCount', 'errors')}">
                                    <g:textField name="haveReadCount" value="${fieldValue(bean: bookInstance, field: 'haveReadCount')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="isbn10"><g:message code="book.isbn10.label" default="Isbn10" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'isbn10', 'errors')}">
                                    <g:textField name="isbn10" value="${bookInstance?.isbn10}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="opinionCount"><g:message code="book.opinionCount.label" default="Opinion Count" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'opinionCount', 'errors')}">
                                    <g:textField name="opinionCount" value="${fieldValue(bean: bookInstance, field: 'opinionCount')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="pubType"><g:message code="book.pubType.label" default="Pub Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'pubType', 'errors')}">
                                    <g:textField name="pubType" value="${bookInstance?.pubType}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="smallThumbnailUrl"><g:message code="book.smallThumbnailUrl.label" default="Small Thumbnail Url" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'smallThumbnailUrl', 'errors')}">
                                    <g:textField name="smallThumbnailUrl" value="${bookInstance?.smallThumbnailUrl}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="source"><g:message code="book.source.label" default="Source" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'source', 'errors')}">
                                    <g:textField name="source" value="${bookInstance?.source}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="thumbnailUrl"><g:message code="book.thumbnailUrl.label" default="Thumbnail Url" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'thumbnailUrl', 'errors')}">
                                    <g:textField name="thumbnailUrl" value="${bookInstance?.thumbnailUrl}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title"><g:message code="book.title.label" default="Title" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'title', 'errors')}">
                                    <g:textField name="title" value="${bookInstance?.title}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="wantToReadCount"><g:message code="book.wantToReadCount.label" default="Want To Read Count" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: bookInstance, field: 'wantToReadCount', 'errors')}">
                                    <g:textField name="wantToReadCount" value="${fieldValue(bean: bookInstance, field: 'wantToReadCount')}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
