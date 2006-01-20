<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<h2>Save <c:out value="${label}"/></h2>
   

Are you sure you want to change this <c:out value="${label}"/>?  Users may have already 
added content to matrix cells or created/submitted reflections.

<form method="POST">
<!-- <input name="cell_id" type="hidden" value="<c:out value="${cell_id}"/>"/>
<input name="selectedArtifacts" type="hidden" value="<c:out value="${selectedArtifacts}"/>"/> -->
<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
<div class="chefButtonRow">
<input name="continue" type="submit" value="<osp:message key="continue" bundle="${msgs}" />"/>
<input name="cancel" type="submit" value="<osp:message key="cancel" bundle="${msgs}" />"/>
</div>
</form>