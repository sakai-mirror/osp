<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.presentation.bundle.Messages"/>

<form method="post" name="wizardform" action="createPresentation.osp" onsubmit="return true;">
<osp:form/>

<%-- The model for this JSP consists of:
  * A CreatePresentationCommandBean bound as "command"
  * A map containing two elements:
    1. availableTemplates, a List<PresentationTemplate> of templates available for use
    2. freeFormTemplateId, the singleton Id referring to the placeholder template for free-form portfolios
--%>
<h2>Editing Content</h2>
<input type="submit" />
</form>
