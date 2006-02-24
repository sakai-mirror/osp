<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="messages" var="msgs" />

<form  method="POST">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3><osp:message key="osp.help.glossary.importTitle" bundle="${msgs}" /></h3>
    
    
    <spring:bind path="uploadGlossary.uploadedGlossary">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <label><osp:message key="osp.help.glossary.importTheseFiles" bundle="${msgs}" /></label>
            <input type="text" id="name" disabled="true"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedGlossary" id="uploadedGlossary"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           <osp:message key="osp.help.glossary.PickFilesToImport" bundle="${msgs}" /></a>
        </p>
    </spring:bind>
        
    <spring:bind path="uploadGlossary.replaceExistingTerms">
        <osp:message key="osp.help.glossary.whenTermExists" bundle="${msgs}" />
        <p class="checkbox indnt1">
            <input type="radio" name="replaceExistingTerms" id="replaceTerm" value="true" 
                <c:if test="${status.value}">checked</c:if> />
            <label for="replaceTerm">
                    <osp:message key="osp.help.glossary.replaceExistingTerm" bundle="${msgs}" />
            </label>
        </p>
        <p class="checkbox indnt1">
            <input type="radio" name="replaceExistingTerms" id="ignoreTerm" value="false" 
                <c:if test="${status.value == false}">checked</c:if> />
            <label for="ignoreTerm">
                    <osp:message key="osp.help.glossary.ignoreExistingTerm" bundle="${msgs}" />
            </label>
        </p>
        
    </spring:bind>
    
    <br/>
    
    <div class="act">
      <input type="submit" value="<osp:message key="osp.help.glossary.importButton" bundle="${msgs}" />" alignment="center" class="active"> 
      <input type="button" value="<osp:message key="osp.help.glossary.cancelButton" bundle="${msgs}" />" onclick="window.document.location='<osp:url value="glossaryList.osp"/>'">
    </div>

</form>