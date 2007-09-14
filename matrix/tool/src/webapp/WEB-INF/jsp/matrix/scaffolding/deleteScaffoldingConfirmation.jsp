<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3>
   <fmt:message key="title_delete_matrix"/>
</h3>
   
<div class="alertMessage">
   <fmt:message key="text_AreYouSureDeleteMatrix"/>
	 <c:if test="${scaffolding_published}">
      <fmt:message key="text_CautionDeleteMatrix"/>
	</c:if>
</div>

<form method="post">
   <div class="act">
      <input name="continue" type="submit" value="<fmt:message key="button_continue" />" class="active" accesskey="s" />
      <input name="cancel" type="submit" value="<fmt:message key="button_cancel" />"  accesskey="x" />
   </div>
</form>
