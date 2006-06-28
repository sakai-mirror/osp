<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<form method="POST" action="addLayout.osp">
    <osp:form />

    <input name="filePickerAction" id="filePickerAction" type="hidden" value="" />
    <input type="hidden" name="validate" value="true" />
    <spring:bind path="layout.id">
        <input type="hidden" name="layout_id" value="<c:out value="${status.value}"/>" />
    </spring:bind>
<c:choose>
    <c:when test="${empty layout.id}">
        <h3><fmt:message key="title_addLayout"/></h3>
    </c:when>
    <c:otherwise>
        <h3><fmt:message key="title_editLayout"/></h3>
    </c:otherwise>
</c:choose>
    <p class="instruction">
        <fmt:message key="instructions_addLayout"/>
        <fmt:message key="instructions_requiredFields"/>
    </p>
    
    
  <spring:bind path="layout.name">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
         <p class="shorttext">
            <span class="reqStar">*</span><label><fmt:message key="label_displayName"/></label>
            <input type="text" name="<c:out value="${status.expression}"/>" 
                     value="<c:out value="${status.value}"/>" 
                  size="25" maxlength="25" <c:out value="${disabledText}"/>>
         </p>
        </spring:bind>
      
      <spring:bind path="layout.description">
         <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
         </c:if>
      	<p class="longtext">
         	<label class="block"><fmt:message key="label_description"/></label>
                <table><tr>
         		   <td><textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80" 
                    <c:out value="${disabledText}"/>><c:out value="${status.value}"/></textarea></td>
                </tr></table>
         </p>
      </spring:bind>    

    <spring:bind path="layout.xhtmlFileId">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <span class="reqStar">*</span>
            <label><fmt:message key="label_XHTMLLayoutFile"/></label>
            
            <input type="text" id="xhtmlFileName" disabled="true"
                value="<c:out value="${xhtmlFileName}"/>" />
            <input type="hidden" name="xhtmlFileId" id="xhtmlFileId"
                value="<c:out value="${status.value}"/>" />
            <a href="javascript:document.forms[0].filePickerAction.value='<c:out value="${XHTML_FILE}"/>';document.forms[0].validate.value='false';document.forms[0].submit();">
            <fmt:message key="label_pickFile"/> </a>
    </spring:bind>

    <spring:bind path="layout.previewImageId">
        <p class="shorttext">
            <label><fmt:message key="label_previewImage"/></label>
            <input type="text" id="previewImageName" disabled="true"
                value="<c:out value="${previewImageName}"/>" />
            <input type="hidden" name="previewImageId" id="previewImageId"
                value="<c:out value="${status.value}"/>" />
            <a href="javascript:document.forms[0].filePickerAction.value='<c:out value="${PREVIEW_IMAGE}"/>';document.forms[0].validate.value='false';document.forms[0].submit();">
                <fmt:message key="label_pickFile"/> </a>
        </p>
    </spring:bind>

    
   <div class="act">

       <c:choose>
    <c:when test="${empty layout.id}">
        <input type="submit" name="save" class="active" value="<fmt:message key="button_submit"/>"
            onclick="javascript:document.forms[0].validate.value='true';"/>
    </c:when>
    <c:otherwise>
        <input type="submit" name="save" class="active" value="<fmt:message key="button_saveEdit"/>"
            onclick="javascript:document.forms[0].validate.value='true';"/>
    </c:otherwise>
</c:choose>

      <input type="button" name="cancel" value="<fmt:message key="button_cancel"/>"
            onclick="window.document.location='<osp:url value="listLayout.osp"/>'" />
   </div>

</form>


