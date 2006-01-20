<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<h3>Remove <c:out value="${label}"/></h3>
   
<div class="validation">
	Are you sure you want to remove <c:out value="${label}"/> '<c:out value="${displayText}"/>'?  Removing it will remove
	all file associations, reflections and reviews and CANNOT be undone.
</div>

<form method="POST">
	<!-- <input name="cell_id" type="hidden" value="<c:out value="${cell_id}"/>"/>
	<input name="selectedArtifacts" type="hidden" value="<c:out value="${selectedArtifacts}"/>"/> -->
	<div class="act">
		<input name="continue" type="submit" value="<osp:message key="continue" bundle="${msgs}" />"/>
      <input name="cancel" type="submit" value="<osp:message key="cancel" bundle="${msgs}" />"/>
	</div>
</form>