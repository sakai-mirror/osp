<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.presentation.bundle.Messages"/>

<c:set var="pres_active_page" value="summary" />
<c:set var="optionsAreNull" value="${presentation.template.propertyFormType != null and presentation.propertyForm == null}" />
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>

<script type="text/javascript">
$(document).ready(function() {
	$('.autoPost').click(function() {
		$.post('updatePresentation.osp', { id: osp.bag.presentationId, active: $(this).val() });
	});
});
</script>

<style type="text/css">
.quickLink { padding-top: 0.2em; text-align: center; }
.quickLinkInfo { margin-top: 0.1em; text-align: center; }
.presentation_menu_block {
	margin: auto;
	width: 300px;
	height: 100%;
}

.presentation_menu_header {
 	text-align: center;
	font-size: 1.4em;
	color: #555555;
	margin-bottom: 0.1em;
}

.presentation_menu_body {
	border: 1px solid #CCCCCC;
	background-color: #FCFCEE;
	padding: 0.2em;
 	text-align: center;
 }
 
 
</style>

<spring:nestedPath path="presentation">

<div class="tabNavPanel">
 <!-- temp separation; end of tabs -->

<form method="post" onsubmit="return true;">
<osp:form/>

<div class="presentationPanel">
<h3>
   <fmt:message key="pres_details"/>
</h3>

<table>
<tbody>
<%-- Description:  --%>
<spring:bind path="description">
	<tr id="presentationDescription">
		<td class="label"><fmt:message key="table_row_description"/></td>
		<td>
			<span class="editableText"><c:out value="${status.value}" /></span>
			<textarea class="inlineEdit" cols="40" rows="4" style="display:none;"></textarea>
			<a href="#" class="editLink"><fmt:message key="edit"/></a>
			<a href="#" class="saveLink" style="display: none;"><fmt:message key="button_saveEdit"/></a>
			<a href="#" class="undoLink" style="display: none;"><fmt:message key="button_undo"/></a>
		</td>		
	</tr>
</spring:bind>
<%-- Type of Portfolio: --%>
<spring:bind path="template.name">
	<tr>
		<td><fmt:message key="table_row_type"/></td>
		<td><c:out value="${status.value}" /></td>
	</tr>
</spring:bind>
<%-- Created On: --%>
<spring:bind path="created">
	<tr>
		<td><fmt:message key="table_row_created"/></td>
		<td><c:out value="${status.value}" /></td>
	</tr>
</spring:bind>
<%-- Modified On: --%>
<spring:bind path="modified">
	<tr>
		<td><fmt:message key="table_row_modified"/></td>
		<td><c:out value="${status.value}" /></td>
	</tr>
</spring:bind>
</tbody>
</table>
<table style="width: 100%;">
<tbody>
<tr>
<td>
	<div class="presentation_menu_block">
		<div class="presentation_menu_header">
			<fmt:message key="quick_start" />
		</div>
		<div class="presentation_menu_body">
			<p class="quickLink"><a href="<osp:url value="editContent.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_content"/></a></p>
			<p class="quickLinkInfo"><fmt:message key="pres_content_caption"/></p>
			<p class="quickLink"><a href="<osp:url value="editOptions.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_options"/></a></p>
			<p class="quickLinkInfo"><fmt:message key="pres_options_caption"/></p>
			<p class="quickLink"><a href="<osp:url value="sharePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share"/></a></p>
			<p class="quickLinkInfo"><fmt:message key="pres_share_caption"/></p>
		</div>
	</div>
</td>
<td>
	<div class="presentation_menu_block">
		<div class="presentation_menu_header">
			<fmt:message key="pres_status" />
		</div>
		<div class="presentation_menu_body">
			<p class="quickLink">
				<input class="autoPost" type="radio"
				       name="active" value="true"
				       <c:if test="${optionsAreNull}">disabled="disabled"</c:if>
				       <c:if test="${active}">selected="selected"</c:if> />
				<label><fmt:message key="button_active" /></label>
			</p>
			<p class="quickLinkInfo">
				<fmt:message key="active_caption" /> <a href="<osp:url value="sharePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="sharing"/></a>
			</p>
			
			<p class="quickLink">
				<input class="autoPost" type="radio"
				       name="active" value="false"
				       <c:if test="${optionsAreNull}">disabled="disabled"</c:if>
				       <c:if test="${not active}">selected="selected"</c:if> />
				<label><fmt:message key="button_inactive" /></label>
			</p>
			<p class="quickLinkInfo">
				<fmt:message key="inactive_caption" />
			</p>
			<c:if test="${optionsAreNull}">
			<p class="quickLinkInfo"><fmt:message key="inactive_hint"/></p>
			</c:if>
		</div>
	</div>
</td>
</tr>
</table>

</spring:nestedPath>
</div>
</form>
</div>
