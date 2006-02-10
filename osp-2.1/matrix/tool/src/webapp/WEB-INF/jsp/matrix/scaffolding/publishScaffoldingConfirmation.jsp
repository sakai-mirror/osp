<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3><fmt:message key="title_publishScaffolding"/></h3>
   
<div class="validation">
	<fmt:message key="text_areYouSurePublish"/>
</div>

<form method="POST">

	<div class="act">
      <input name="continue" type="submit" value="<osp:message key="button_continue"/>"/>
      <input name="cancel" type="submit" value="<osp:message key="button_cancel"/>"/>
	</div>
</form>