<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h2>
  <fmt:message key="title_saveMatrix">
    <fmt:param><c:out value="${label}"/></fmt:param>
  </fmt:message>
</h2>
   
<fmt:message key="text_areYouSureEdit">
  <fmt:param><c:out value="${label}"/></fmt:param>
</fmt:message>




<form method="POST">

<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
<div class="chefButtonRow">
<input name="continue" type="submit" value="<osp:message key="button_continue"/>"/>
<input name="cancel" type="submit" value="<osp:message key="button_cancel"/>"/>
</div>
</form>