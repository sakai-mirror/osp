<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<h3>Publish Scaffolding</h3>
   
<div class="validation">
	Are you sure you want to publish this scaffolding?  Publishing it will lock 
   many properties and they CANNOT be edited.
</div>

<form method="POST">
	<!-- <input name="cell_id" type="hidden" value="<c:out value="${cell_id}"/>"/>
	<input name="selectedArtifacts" type="hidden" value="<c:out value="${selectedArtifacts}"/>"/> -->
	<div class="act">
		<input name="continue" type="submit" value="Continue"/>
		<input name="cancel" type="submit" value="Cancel"/>
	</div>
</form>