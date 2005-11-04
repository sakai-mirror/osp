<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<h3>Remove Association</h3>
   

Are you sure you want to remove this association?

<form method="POST">
	<!-- <input name="cell_id" type="hidden" value="<c:out value="${cell_id}"/>"/>
	<input name="selectedArtifacts" type="hidden" value="<c:out value="${selectedArtifacts}"/>"/> -->
	<p class="act">
		<input name="action" type="submit" class="active" value="Continue"/>
		<input name="action" type="submit" value="Cancel"/>
	</p>
</form>