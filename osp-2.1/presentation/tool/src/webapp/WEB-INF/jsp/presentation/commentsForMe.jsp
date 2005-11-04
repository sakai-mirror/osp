<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="chefPortletContent"><c:set var="commentsCount" value="0"
    scope="request" /> <c:forEach var="comment" items="${comments}"
    varStatus="commentsStatus">
    <c:set var="commentsCount" value="${commentsStatus.count}"
        scope="request" />
</c:forEach>

<h3><c:out value="${commentsCount}" /> Comment<c:if
    test="${commentsCount != 1}">s</c:if> Written About My Presentations</h3>

<table class="listHier" cellspacing="0">
    <thead>
        <tr>
            <c:set var="sortDir" value="asc" />
            <th><c:if test="${sortByColumn == 'name'}">
                <c:if test="${direction == 'asc'}">
                    <c:set var="sortDir" value="desc" />
                </c:if>
            </c:if> <a
                href="<osp:url value="commentsForMe.osp"/>&sortByColumn=name&direction=<c:out value="${sortDir}" />">
            Presentation</a>&nbsp;</th>
            <c:set var="sortDir" value="asc" />
            <th><c:if test="${sortByColumn == 'title'}">
                <c:if test="${direction == 'asc'}">
                    <c:set var="sortDir" value="desc" />
                </c:if>
            </c:if> <a
                href="<osp:url value="commentsForMe.osp"/>&sortByColumn=title&direction=<c:out value="${sortDir}" />">
            Comment</a>&nbsp;</th>
            <c:set var="sortDir" value="asc" />
            <th><c:if test="${sortByColumn == 'created'}">
                <c:if test="${direction == 'asc'}">
                    <c:set var="sortDir" value="desc" />
                </c:if>
            </c:if> <a
                href="<osp:url value="commentsForMe.osp"/>&sortByColumn=created&direction=<c:out value="${sortDir}" />">
            Date</a>&nbsp;</th>
            <c:set var="sortDir" value="asc" />
            <th><c:if test="${sortByColumn == 'creator_id'}">
                <c:if test="${direction == 'asc'}">
                    <c:set var="sortDir" value="desc" />
                </c:if>
            </c:if> <a
                href="<osp:url value="commentsForMe.osp"/>&sortByColumn=creator_id&direction=<c:out value="${sortDir}" />">
            Comment Author</a>&nbsp;</th>
            <th><c:if test="${sortByColumn == 'visibility'}">
                <c:if test="${direction == 'asc'}">
                    <c:set var="sortDir" value="desc" />
                </c:if>
            </c:if> <a
                href="<osp:url value="commentsForMe.osp"/>&sortByColumn=visibility&direction=<c:out value="${sortDir}" />">
            Visibility</a>&nbsp;</th>
        </tr>
    </thead>
    <tbody>
        <c:set value="0" var="odd" />
        <c:forEach begin="0" items="${comments}" var="comment">
            <c:choose>
                <c:when test="${odd == 1}">
                    <c:set var="odd" value="0" />
                    <c:set value="#FAFAFA" var="color" />
                </c:when>
                <c:otherwise>
                    <c:set var="odd" value="1" />
                    <c:set value="" var="color" />
                </c:otherwise>
            </c:choose>
            <tr bgcolor="<c:out value="${color}" />">
                <td nowrap="nowrap">
                <p><a target="_blank" 
                    href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${comment.presentation.id.value}" />#comment<c:out value="${comment.id.value}" />">
                <c:out value="${comment.presentation.name}" />(<c:out
                    value="${comment.presentation.template.name}" />) </a>
                </p>
                </td>
                <td>
                <p><c:out value="${comment.title}" /></p>
                </td>
                <td class="ListData">
                <p><c:out value="${comment.created}" /></p>
                </td>
                <td>
                <P><c:out value="${comment.creator.displayName}" /></P>
                </td>
                <td>
                <P><c:if test="${comment.visibility == 1}">
               &nbsp;Private&nbsp;
            </c:if> <c:if test="${comment.visibility == 2}">
               &nbsp;Shared&nbsp;
            </c:if> <c:if test="${comment.visibility == 3}">
               &nbsp;Public&nbsp;
            </c:if></P>
                </td>
            </tr>
            <tr bgcolor="<c:out value="${color}" />">
                <td>&nbsp;</td>
                <td colspan="5">
                <p><c:out value="${comment.comment}" /></p>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>