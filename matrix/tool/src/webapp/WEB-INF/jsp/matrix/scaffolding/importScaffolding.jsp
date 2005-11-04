<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<form  method="POST">
	<osp:form/>

	<h3>Import Scaffolding</h3>
		
	<spring:bind path="uploadForm.uploadedScaffolding">
        <c:if test="${status.error}">
           <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
		<p class="shorttext">
            <label>Import File</label>
			<input type="text" id="name" disabled="true" value="<c:out value="${uploadForm.scaffoldingFileName}"/>"/>
			<input type="hidden" name="uploadedScaffolding" id="uploadedScaffolding" value="<c:out value="${status.value}"/>" />
			<a href="<osp:url value="sakai.filepicker.helper/tool?panel=Main&sakaiproject.filepicker.attachLinks=true">
			</osp:url>"
				title="Choose File..." >
				Choose File...
			</a>
		</p>
	</spring:bind>
	

	<br/>

	<div class="act">
		<input class="active" type="submit" value="Import Scaffolding"> <input type="button" value="Cancel" onclick="window.document.location='<osp:url value="viewMatrix.osp"/>'">
	</div>

</form>