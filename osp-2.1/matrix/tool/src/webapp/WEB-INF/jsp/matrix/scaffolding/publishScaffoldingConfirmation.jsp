<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<h3>Publish Scaffolding</h3>
   
<div class="validation">
	Are you sure you want to publish this scaffolding?  Publishing it will lock 
   many properties and they CANNOT be edited.
</div>

<form method="POST">

	<div class="act">
      <input name="continue" type="submit" value="<osp:message key="continue" bundle="${msgs}" />"/>
      <input name="cancel" type="submit" value="<osp:message key="cancel" bundle="${msgs}" />"/>
	</div>
</form>