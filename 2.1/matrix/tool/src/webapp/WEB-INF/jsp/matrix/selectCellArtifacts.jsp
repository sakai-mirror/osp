<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="nodes" value="${model.nodes}"/>
<c:set var="cellId" value="${model.cellId}"/>

<c:forEach var="artifact" items="${nodes}">
	<a href='<osp:url value="manageArtifactAssociations.osp?cell_id="/><c:out value="${cellId}" />&node_id=<c:out value="${artifact.id}"/>'><c:out value="${artifact.name}"/></a><br/>
</c:forEach>
<form method="POST">
	<!--<input type="submit" name="action" value="attach"/>
	<input type="submit" name="action" value="browse"/> -->
	<input type="submit" name="action" value="cancel"/>
</form>
