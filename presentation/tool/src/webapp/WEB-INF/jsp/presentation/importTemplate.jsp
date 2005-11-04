<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<form  method="POST">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3>Import Template</h3>
    
    <spring:bind path="uploadForm.uploadedTemplate">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <label>Template File</label>
            <input type="text" id="name" disabled="true"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedTemplate" id="uploadedTemplate"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           Pick file </a>
        </p>
    </spring:bind>
    
    <br/>
    
    <div class="act">
      <input type="submit" alignment="center" value="Import Template" class="active"> 
      <input type="button" value="Cancel" onclick="window.document.location='<osp:url value="listTemplate.osp"/>'">
    </div>

</form>