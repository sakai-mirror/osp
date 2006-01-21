<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<h3><fmt:message key="title_preview"><param><c:out value="${template.name}"/></param></fmt:message></h3>

<div style="padding-left:15px">
    <h4>Template Description</h4>
    <div style="padding-left:30px">
        <c:out value="${template.description}"/>
    </div>
    
    <h4><fmt:message key="table_header_owner"/></h4>
    <div style="padding-left:30px">
        <c:out value="${template.owner.displayName}"/>
    </div>
</div>

<h3><fmt:message key="table_header_content"/></h3>

<c:forEach var="itemDefinition" items="${template.itemDefinitions}" varStatus="loopCounter">
<div style="padding-left:15px">
   <h4><c:out value="${itemDefinition.title}"/></h4>
   <div style="padding-left:30px">
      <h5><fmt:message key="table_row_description"/></h5> <c:out value="${itemDefinition.description}"/>
      <h5><fmt:message key="table_row_type"/>       </h5> <c:out value="${itemDefinition.type}"/>
      <c:if test="${itemDefinition.hasMimeTypes}">
         <c:forEach var="mimeType" items="${itemDefinition.mimeTypes}" varStatus="loopCounter2">
            <c:if test="${loopCounter2.index == 0}">( </c:if>
            <c:if test="${loopCounter2.index > 0}">, </c:if>
            <c:out value="${mimeType}"/>
         </c:forEach>
         )
      </c:if>
   </div>
</div>
</c:forEach>