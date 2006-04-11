<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<form  method="POST">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3><fmt:message key="title_importTemplate"/></h3>
    
    <spring:bind path="uploadForm.uploadedTemplate">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <label><fmt:message key="label_templateFile"/></label>
            <input type="text" id="name" disabled="true"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedTemplate" id="uploadedTemplate"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           <fmt:message key="instructions_pickFile"/> </a>
        </p>
    </spring:bind>
    
    <br/>
    
    <div class="act">
      <input type="submit" value="<fmt:message key="button_importTemplate"/>" alignment="center" class="active"> 
      <input type="button" value="<fmt:message key="button_cancel"/>" onclick="window.document.location='<osp:url value="listTemplate.osp"/>'">
    </div>

</form>