<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

 <c:if test="${empty templates && empty publishedTemplates}" var="noTemplates" />
 <c:set var="targetNext" value="_target1" />
 <c:set var="suppress_previous" value="true" />

 <h3><fmt:message key="title_addPresentation1"/></h3>

 <div class="instruction">
   <fmt:message key="instructions_requiredFields"/>
 </div>
 <%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>


 <form method="POST" name="wizardform" action="addPresentation.osp"
     onsubmit="return true;"><input type="hidden" name="direction"
     value="" />
     <osp:form />

     <spring:bind path="presentation.name">
         <p class="shorttext">
             <c:if test="${status.error}">
                 <div class="validation"><c:out value="${status.errorMessage}"/></div>
             </c:if>
             <span class="reqStar">*</span>
             <label><fmt:message key="label_title"/></label>
             <input type="text"
                 name="<c:out value="${status.expression}"/>"
                 value="<c:out value="${status.displayValue}"/>" />
         </p>
     </spring:bind>

     <spring:bind path="presentation.description">
         <p class="longtext">
             <c:if test="${status.error}">
                 <div class="validation"><c:out value="${status.errorMessage}"/></div>
             </c:if>
             <label><fmt:message key="label_description"/></label>
             <table><tr>
             <c:set var="descriptionID" value="${status.expression}" />
             <td><textarea id="<c:out value="${status.expression}"/>"
                 name="<c:out value="${status.expression}"/>"
                 cols="80" rows="5"><c:out
                 value="${status.displayValue}" /></textarea></td>
             </tr></table>
         </p>
     </spring:bind>

     <spring:bind path="presentation.expiresOn">
         <p class="shorttext">
             <c:if test="${status.error}">
                 <div class="validation"><c:out value="${status.errorMessage}"/></div>
             </c:if>
             <label><fmt:message key="label_expires"/></label>
             <osp-c:dateSelect daySelectId="expiresOnBean.day"
                 yearSelectId="expiresOnBean.year"
                 monthSelectId="expiresOnBean.month"
                 earliestYear="2004"
                 selectedDate="${presentation.expiresOn}" />
         </p>
     </spring:bind>

<fmt:message key="addPresentation1_createPortfolioUsing"/><br />
      <spring:bind path="presentation.presentationType">
      <label>
         <input type="radio" name="<c:out value="${status.expression}"/>"
               value="osp.presentation.type.freeForm"
               <c:if test="${status.value == 'osp.presentation.type.freeForm' || noTemplates}">
                  checked</c:if> />
         &nbsp; <fmt:message key="label_freeForm"/></label>
      <fmt:message key="addPresentation1_manageYourself"/>

      <label> <br />
      <br />
         <input type="radio" name="<c:out value="${status.expression}"/>"
               value="osp.presentation.type.template"
               <c:if test="${status.value == 'osp.presentation.type.template' && !noTemplates}">
                  checked</c:if>
               <c:if test="${noTemplates}">disabled</c:if> />
      <fmt:message key="label_usingTemplate"/></label>
      </spring:bind>


            <p class="shorttext">
              <spring:bind path="presentation.template.id">
                <c:if test="${status.error}">
                    <div class="validation"><c:out value="${status.errorMessage}"/></div>
                </c:if>
                <span class="reqStar">*</span>
                <select id="<c:out value="${status.expression}"/>"
                    onchange='closeFrame("previewFrame", "previewButton","closeButton")'
                    name="<c:out value="${status.expression}"/>"
                    <c:if test="${noTemplates}">disabled</c:if> >
                    <option value=""><fmt:message key="addPresentation1_selectTemplate"/></option>
                    <option value="">- - - - - - - - - - - - - - - - - -
                    - - -</option>
                    <c:forEach var="template"
                        items="${publishedTemplates}"
                        varStatus="templateStatus">
                        <option
                            <c:if test="${presentation.template.id.value == template.id.value }">selected</c:if>
                            value="<c:out value="${template.id.value}"/>"><c:out
                            value="${template.name}" /> <fmt:message key="addPresentation1_publishedTemplate"/>
                    </c:forEach>
                    <c:forEach var="template" items="${templates}"
                        varStatus="templateStatus">
                        <option
                            <c:if test="${presentation.template.id.value == template.id.value}">selected</c:if>
                            value="<c:out value="${template.id.value}"/>"><c:out
                            value="${template.name}" /> (Your Template)

                    </c:forEach>
                </select>
              </spring:bind>
            </p>

       <c:if test="${!noTemplates}">
          <spring:bind path="presentation.template.id">
            <p class="shorttext">
                <div style="visibility:visible" id="previewButton"><a
                    href="#"
                    onclick='showFrame("<c:out value="${status.expression}"/>","previewFrame", "previewButton","closeButton","<osp:url value="previewTemplate.osp"/>&panelId=previewFrame&id=" + getSelectedValue("<c:out value="${status.expression}"/>"),"<fmt:message key="alert_selectTemplate"/>")'>
                    <fmt:message key="addPresentation1_previewTemplate"/></a></div>
                <div style="visibility:hidden" id="closeButton"><a
                    href="#"
                    onclick='closeFrame("previewFrame", "previewButton","closeButton")'>
                    <fmt:message key="addPresentation_closePreview"/></a><br />
                    <iframe name="previewFrame" id="previewFrame" height="0"
                        width="650" frameborder="0" marginwidth="0"
                        marginheight="0" scrolling="auto"> </iframe>
                </div>
            </p>
          </spring:bind>
       </c:if>




     <c:set var="suppress_submit" value="true" />
     <c:if test="${empty presentation.id}">
         <c:set var="suppress_save" value="true" />
     </c:if>
     <br />
     <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
 </form>
