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

<spring:nestedPath path="command">
<spring:bind path="presentationType">
	<input id="presType" type="hidden" name="${status.expression}"/>
</spring:bind>

<div class="presentationTypeDialog">
	<h3><fmt:message key="heading_createPresentation"/></h3>
	<spring:bind path="*">
		<c:if test="${status.error}">
			<%-- FIXME: This needs an appropriate class for error msg --%>
			<div class="alert">
				<c:out value="${status.errorMessage}"/>
			</div>
		</c:if>
	</spring:bind>
	<spring:bind path="templateId">
		<c:if test="${not empty availableTemplates}">
			<div class="presentationTypeGroup">
				<c:forEach var="template"
					items="${availableTemplates}"
					varStatus="templateStatus">
					<div class="portfolioTypeOption">
						<input type="radio"
							id="${status.expression}-${templateStatus.count}"
							name="${status.expression}"
							value="<c:out value="${template.id.value}"/>"
							onclick="getElementById('presType').value = 'osp.presentation.type.template';" />
						<label for="${status.expression}-${templateStatus.count}"><c:out value="${template.name}"/></label>
						<p class="portfolioTypeDescription">
							<c:out value="${template.description}"/>
						</p>
						<div class="portfolioTypeMoreInfo">
							<a href="#" onclick="return false;">More Info</a> <%-- TODO: i18n --%>
						</div>
					</div>
				</c:forEach>
			</div>
		</c:if>
		
		<%-- Handle option to turn free-form off --%>
		<div class="presentationTypeGroup">
			<div class="portfolioTypeOption">
				<input type="radio"
					id="${status.expression}-freeForm"
					name="${status.expression}"
					value="${freeFormTemplateId.value}"
					onclick="getElementById('presType').value = 'osp.presentation.type.freeForm';" />
				<label for="${status.expression}-freeForm"><fmt:message key="label_freeForm"/></label>
				<p class="portfolioTypeDescription">
					<fmt:message key="addPresentation1_manageYourself"/>
				</p>
			</div>
		</div>
	</spring:bind>
</div>

<div class="act">
	<input type="submit" name="submit" value="Create" /> <input type="submit" name="cancel" value="Cancel" />
</div>
</spring:nestedPath>
</form>
