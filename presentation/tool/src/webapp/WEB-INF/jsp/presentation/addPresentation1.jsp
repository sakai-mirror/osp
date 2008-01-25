<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

 <c:if test="${empty templates && empty publishedTemplates}" var="noTemplates" />

<c:if test="${empty presentation.name}">
    <c:set var="targetPrevious" value="_target0" />
    <c:set var="suppress_save" value="true"/>
</c:if>
<c:if test="${not empty presentation.name}">
    <c:set var="suppress_save" value="false" />
</c:if>

<c:set var="suppress_previous" value="true" />
<c:set var="begin_state" value="current_state"/>
<c:set var="design_state" value="next_state"/>
<c:set var="publish_state" value="next_state"/>
<c:set var="targetNext" value="_target2" />

<c:set var="step" value="1" />

 <%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>

 <div class="instruction">
   <fmt:message key="instructions_requiredFields"/>
 </div>


 <form method="post" name="wizardform" action="addPresentation.osp"
     onsubmit="return true;"><input type="hidden" name="direction"
     value="" />
     <osp:form />

     <spring:bind path="presentation.name">
             <c:if test="${status.error}">
                 <p class="shorttext validFail" >
             </c:if>
			 <c:if test="${!status.error}">
                 <p class="shorttext" >
             </c:if>
             <span class="reqStar">*</span>
             <label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_title"/></label>
             <input type="text"
                 name="<c:out value="${status.expression}"/>"
				  id="<c:out value="${status.expression}"/>-id"
                 value="<c:out value="${status.displayValue}"/>" />
				 <c:if test="${status.error}">
				 	<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>
         </p>
     </spring:bind>

     <spring:bind path="presentation.description">
             <c:if test="${status.error}">
                 <p class="longtext validFail" >
             </c:if>
			 <c:if test="${!status.error}">
                 <p class="longtext" >
             </c:if>
             <label class="block"  for="<c:out value="${status.expression}"/>">
			 	<fmt:message key="label_description"/>
				 <c:if test="${status.error}">
					 <span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				 </c:if>
			</label>

             <c:set var="descriptionID" value="${status.expression}" />
             <textarea id="<c:out value="${status.expression}"/>"
                 name="<c:out value="${status.expression}"/>"
                 cols="80" rows="5"><c:out
                 value="${status.displayValue}" /></textarea>
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

     <spring:bind path="presentation.allowComments">
            <div class="checkbox indnt1">
                <input type="checkbox" name="<c:out value="${status.expression}"/>" value="true"  id="<c:out value="${status.expression}"/>-id" 
        			<c:if test="${status.value}">checked="checked"</c:if> />
      
                <label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_allowComments"/></label>
                <span class="instruction"><fmt:message key="instruction_allowComments"/></span>
            </div>
     </spring:bind>

     <c:set var="suppress_submit" value="true" />
     <c:if test="${empty presentation.id}">
         <c:set var="suppress_save" value="true" />
     </c:if>
     <br />
     <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
 </form>
