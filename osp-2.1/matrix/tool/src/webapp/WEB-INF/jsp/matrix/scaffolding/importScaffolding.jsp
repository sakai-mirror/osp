<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<form  method="POST">
	<osp:form/>

	<h3><fmt:message key="title_importScaffolding"/></h3>
		
	<spring:bind path="uploadForm.uploadedScaffolding">
        <c:if test="${status.error}">
           <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
		<p class="shorttext">
            <label><fmt:message key="label_importFile"/></label>
			<input type="text" id="name" disabled="true" value="<c:out value="${uploadForm.scaffoldingFileName}"/>"/>
			<input type="hidden" name="uploadedScaffolding" id="uploadedScaffolding" value="<c:out value="${status.value}"/>" />
			<a href="<osp:url value="sakai.filepicker.helper/tool?panel=Main&session.sakaiproject.filepicker.attachLinks=true">
			</osp:url>"
				title="<fmt:message key="action_chooseFile_title"/>" >
				<fmt:message key="action_chooseFile"/>
			</a>
		</p>
	</spring:bind>
	

	<br/>

	<div class="act">
		<input class="active" type="submit" value="<osp:message key="button_importScaffolding"  />"> 
      <input type="button" value="<osp:message key="button_cancel"/>" onclick="window.document.location='<osp:url value="viewMatrix.osp"/>'">
	</div>

</form>