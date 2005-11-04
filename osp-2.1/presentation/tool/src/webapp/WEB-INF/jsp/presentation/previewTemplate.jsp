<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h3>Preview: <c:out value="${template.name}"/></h3>

<div style="padding-left:15px">
    <h4>Template Description</h4>
    <div style="padding-left:30px">
        <c:out value="${template.description}"/>
    </div>
    
    <h4>Owner</h4>
    <div style="padding-left:30px">
        <c:out value="${template.owner.displayName}"/>
    </div>
</div>

<h3>Content</h3>

<c:forEach var="itemDefinition" items="${template.itemDefinitions}" varStatus="loopCounter">
<div style="padding-left:15px">
   <h4><c:out value="${itemDefinition.title}"/></h4>
   <div style="padding-left:30px">
      <h5>Description:</h5> <c:out value="${itemDefinition.description}"/>
      <h5>Type:</h5> <c:out value="${itemDefinition.type}"/>
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