<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3>Remove <c:out value="${label}"/></h3>
   
<div class="validation">
  <fmt:message key="tekst_AreYouSureRemove">
    <fmt:param><c:out value="${label}"/></fmt:param>
    <fmt:param><c:out value="${displayText}"/></fmt:param>
  </fmt:message>
</div>

<form method="POST">
	<div class="act">
		<input name="continue" type="submit" value="<osp:message key="button_continue" bundle="${msgs}" />"/>
      <input name="cancel" type="submit" value="<osp:message key="button_cancel" bundle="${msgs}" />"/>
	</div>
</form>