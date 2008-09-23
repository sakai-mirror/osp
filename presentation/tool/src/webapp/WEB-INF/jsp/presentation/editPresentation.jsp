<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.presentation.bundle.Messages"/>

<script type="text/javascript" src="http://jqueryjs.googlecode.com/files/jquery-1.2.6.min.js">//empty</script>
<script type="text/javascript">	
var presentationId = '<c:out value="${presentation.id.value}"/>';

	function showEdit() {
		$('#presentationName .inlineEdit').val($('#presentationName .text').text());
		$('#presentationName .saveLink').show();
		$('#presentationName .undoLink').show();
		$('#presentationName .editLink').hide();
		$('#presentationName .inlineEdit').show();
		$('#presentationName .text').hide();
	}
	
	function hideEdit() {
		$('#presentationName .saveLink').hide();
		$('#presentationName .undoLink').hide();
		$('#presentationName .editLink').show();
		$('#presentationName .inlineEdit').hide();
		$('#presentationName .text').show();
		$('#presentationName .inlineEdit').val('');		
	}

	$(document).ready(function() {
		$('#presentationName .editLink').click(function() {
			showEdit();
		});
		$('#presentationName .saveLink').click(function() {
			var name = $('#presentationName .inlineEdit').val();
			if (name != $('#presentationName .text').text()) {
				$('#presentationName .text').text(name);
				$.post("updatePresentation.osp", { id: presentationId, name: name });
			}
			hideEdit();
		});
		$('#presentationName .undoLink').click(function() {
			hideEdit();
		});
	});	
</script>


<form method="post" onsubmit="return true;">
<osp:form/>

<%-- The model for this JSP consists of:
  * A CreatePresentationCommandBean bound as "command"
  * A map containing two elements:
    1. availableTemplates, a List<PresentationTemplate> of templates available for use
    2. freeFormTemplateId, the singleton Id referring to the placeholder template for free-form portfolios
--%>
<spring:nestedPath path="presentation">
<spring:bind path="name">
	<div id="presentationName">
		<span class="text"><c:out value="${status.value}"/></span>
		<input class="inlineEdit" type="text" style="display:none;" size="80" />
		<a href="#" class="editLink">Edit</a>
		<a href="#" class="saveLink" style="display: none;">Save</a>
		<a href="#" class="undoLink" style="display: none;">Undo</a>
	</div>
</spring:bind>
<spring:bind path="description">
	<div id="presentationDescription">
		<c:out value="${status.value}" />
	</div>
</spring:bind>
</spring:nestedPath>
<br />
<input type="submit" />
</form>
