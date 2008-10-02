<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.presentation.bundle.Messages"/>

<c:if test="${empty templates && empty publishedTemplates && empty globalPublishedTemplates}" var="noTemplates"/>
<c:set var="targetNext" value="_target1"/>
<c:set var="suppress_previous" value="true"/>

<%-- <h3><fmt:message key="title_addPresentation1"/></h3> --%>

<form method="post" name="wizardform" action="addPresentation.osp" onsubmit="return true;">
<input type="hidden" name="direction" value=""/>
<osp:form/>

<spring:bind path="presentation.presentationType">
	<input id="presType" type="hidden" name="${status.expression}"/>
</spring:bind>

<div class="presentationTypeDialog">
	<h3><fmt:message key="heading_createPresentation"/></h3>
	<spring:bind path="presentation.template.id">
		<c:if test="${status.error}">
			<%-- FIXME: This needs an appropriate class for error msg --%>
			<div class="messageValidation">
				<c:out value="${status.errorMessage}"/>
			</div>
		</c:if>
		<c:if test="${!noTemplates}">
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

<%--
<p class="shorttext indnt3">
	<spring:bind path="presentation.template.id">
		<span class="reqStar">*</span>
		<select id="<c:out value="${status.expression}"/>"
			onchange='closeFrame("previewFrame", "previewButton","closeButton")'
			name="< c:out value="${status.expression}"/>"
			<c:if test="${noTemplates}">disabled</c:if> >
			<option value=""><fmt:message key="addPresentation1_selectTemplate"/></option>
			<c:forEach var="template"
				  items="${publishedTemplates}"
				  varStatus="templateStatus">
				  <option
				  <c:if test="${presentation.template.id.value == template.id.value }">selected="selected"</c:if>
					value="<c:out value="${template.id.value}"/>"><c:out
					value="${template.name}"/> <fmt:message key="addPresentation1_publishedTemplate"/>
			</c:forEach>
			<c:forEach var="template"
				  items="${globalPublishedTemplates}"
				  varStatus="templateStatus">
				<option
				<c:if test="${presentation.template.id.value == template.id.value }">selected="selected"</c:if>
				value="<c:out value="${template.id.value}"/>"><c:out
				value="${template.name}"/> <fmt:message key="addPresentation1_globalPublishedTemplate"/>
			</c:forEach>
			<c:forEach var="template" items="${templates}"
				varStatus="templateStatus">
				<option
				<c:if test="${presentation.template.id.value == template.id.value}">selected="selected"</c:if>
				value="<c:out value="${template.id.value}"/>"><c:out
				value="${template.name}"/> (Your Template)</option>
			</c:forEach>
		</select>
		<c:if test="${status.error}">
			<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
		</c:if>
	</spring:bind>
</p>
--%>

<%--
<c:if test="${!noTemplates}">
    <spring:bind path="presentation.template.id">
        <div class="indnt3">
            <div style="display:block" id="previewButton"><a
                    href="#"
                    onclick='showFrame("<c:out value="${status.expression}"/>","previewFrame", "previewButton","closeButton","<osp:url value="previewTemplate.osp"/>&panelId=previewFrame&id=" + getSelectedValue("<c:out value="${status.expression}"/>"),"<fmt:message key="alert_selectTemplate"/>")'>
                <fmt:message key="addPresentation1_previewTemplate"/></a></div>
            <div style="display:none" id="closeButton">
				<a href="#" onclick='closeFrame("previewFrame", "previewButton","closeButton")'>
                	<fmt:message key="addPresentation_closePreview"/>
				</a>
            </div>
        </div>
		<div class=" indnt3">
			<iframe name="previewFrame" id="previewFrame" style="display:none"
			width="80%" frameborder="0" marginwidth="0"
			marginheight="0" scrolling="auto"></iframe>
		</div>	

    </spring:bind>
</c:if>
--%>

<c:set var="suppress_submit" value="true"/>
<c:set var="suppress_save" value="true"/>
<br/>
<%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc" %>
</form>
