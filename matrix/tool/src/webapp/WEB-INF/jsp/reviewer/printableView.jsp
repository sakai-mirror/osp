<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:forEach var="item" items="${cell.reflection.reflectionItems}" varStatus="loopStatus">
   <c:set var="reflectionItem" value="${cell.reflection.reflectionItems[loopStatus.index]}"/>
   Expectation <c:out value="${reflectionItem.expectation}"  escapeXml="false" />
   Evidence:
   <c:out value="${reflectionItem.evidence}"  escapeXml="false" />
   Connect:
   <c:out value="${reflectionItem.connect}"  escapeXml="false" />
   
</c:forEach>

Intellectual Growth:
<c:out value="${cell.reflection.growthStatement}"  escapeXml="false" />

