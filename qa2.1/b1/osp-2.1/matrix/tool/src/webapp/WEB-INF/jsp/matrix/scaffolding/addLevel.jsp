<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

	<h3><fmt:message key="title_edit_ScaffColumn"/></h3>
	
	
	<div class="instruction">
		<fmt:message key="instructions_scaffColumn"/>
		<fmt:message key="instructions_requiredFields"/>
	</div>
	
	<form method="POST">
		<osp:form/> 
        
		<h4><fmt:message key="title_scaffColumn"/></h4>
		
        <spring:bind path="level.description">
          <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
          </c:if>
		  <p class="shorttext indnt2">
				<span class="reqStar">*</span><label><fmt:message key="label_columnName"/></label> 
				<input type="text" name="<c:out value="${status.expression}"/>" 
					   value="<c:out value="${status.displayValue}"/>"/>
		    </p>
        </spring:bind>
        <p class="shorttext indnt2">
      <spring:bind path="level.color">
         <label><fmt:message key="label_bgColor"/></label>   
      
         <input type="Text" name="<c:out value="${status.expression}"/>" 
               value="<c:out value="${status.displayValue}"/>"/>
         <span class="error_message"><c:out value="${status.errorMessage}"/></span>
         <!--
            Put icon by the input control.
            Make it the link calling picker popup.
            Specify input object reference as first parameter to the function and palete selection as second.
         -->
         <a href="javascript:TCP.popup(document.forms[0].elements['<c:out value="${status.expression}"/>'])"><img width="15" height="13" border="0" alt="Click Here to Pick up the color" src="<osp:url value="/js/colorPicker/img/sel.gif"/>"></a>
      </spring:bind>
   </p>
           <p class="shorttext indnt2">
      <spring:bind path="level.textColor">
         <label><fmt:message key="label_fontColor"/></label>   
      
         <input type="Text" name="<c:out value="${status.expression}"/>" 
               value="<c:out value="${status.displayValue}"/>"/>
         <span class="error_message"><c:out value="${status.errorMessage}"/></span>
         <!--
            Put icon by the input control.
            Make it the link calling picker popup.
            Specify input object reference as first parameter to the function and palete selection as second.
         -->
         <a href="javascript:TCP.popup(document.forms[0].elements['<c:out value="${status.expression}"/>'])"><img width="15" height="13" border="0" alt="Click Here to Pick up the color" src="<osp:url value="/js/colorPicker/img/sel.gif"/>"></a>
      </spring:bind>
   </p>
		
		<div class="act">
			<spring:bind path="level.id">
				<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
				<span class="error_message"><c:out value="${status.errorMessage}"/></span>
			</spring:bind>
			<input type="submit" name="updateAction" class="active" value="<osp:message key="button_update" />"/>
			<input type="button" name="action" value="<osp:message key="button_cancel" />" onclick="javascript:doCancel()"/>
		</div>
		
	</form>
	<form name="cancelForm" method="GET" action="<osp:url value="addScaffolding.osp" />">
		<osp:form/>
		<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
	</form>
