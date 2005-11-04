<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<c:forEach var="log" items="${presentationLogs}"
    varStatus="presentationStatus">
    <c:set var="count" value="${presentationStatus.count}"
        scope="request" />
    <c:set var="presentationName" value="${log.presentation.name}" />
</c:forEach>

<h3>Presentation Statistics</h3>

Presentation '<c:out value="${presentationName}" />'
                has <c:out value="${count}" /> View<c:if
                    test="${count > 1}">s</c:if>

<table class="listHier">
    <tr>
        <th>Viewer</th>
        <th>Date</th>
    </tr>


    <c:forEach var="log" items="${presentationLogs}">
        <TR>
            <TD width="200"><c:out
                value="${log.viewer.displayName}" />&nbsp;</TD>
            <TD width="200"><c:out value="${log.viewDate}" />&nbsp;</TD>
        </TR>
    </c:forEach>
</table>
