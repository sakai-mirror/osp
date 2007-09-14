<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3>
  <fmt:message key="title_saveMatrix">
    <fmt:param><c:out value="${label}"/></fmt:param>
  </fmt:message>
</h3>
  
<div class="alertMessage" >
<fmt:message key="text_areYouSureEdit">
  <fmt:param><c:out value="${label}"/></fmt:param>
</fmt:message>
</div>



<form method="post">

<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
<div class="act">
<input name="continue" type="submit" value="<osp:message key="button_continue"/>" class="active" accesskey="s" />
<input name="cancel" type="submit" value="<osp:message key="button_cancel"/>" accesskey="x" />
</div>
</form>