<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>
	
	
	<h3><fmt:message key="title_edit_ScaffRow"/></h3>
	
	<div class="instructions">
		<fmt:message key="instructions_scaffRow"/>
		<fmt:message key="instructions_requiredFields"/>
	</div>
	
<form method="POST">
	<osp:form/> 
	<input type="hidden" name="params" value="" />
	<input type="hidden" name="dest" value="" />
	<input type="hidden" name="validate" value="false" />
	
	<h4><fmt:message key="title_scaffRow"/></h4>
	
    <spring:bind path="criterion.description">
        <c:if test="${status.error}">
           <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
    	<p class="shorttext indnt2">
            <span class="reqStar">*</span><label><fmt:message key="label_rowName"/></label>
    		<input type="text" name="<c:out value="${status.expression}"/>" 
    				value="<c:out value="${status.displayValue}"/>"/>
    	</p>
    </spring:bind>
	<p class="shorttext indnt2">
		<spring:bind path="criterion.color">
			<label><fmt:message key="label_bgColor"/></label>   
		   <input type="text" disabled="disabled" value="" size="2" 
                        name="<c:out value="${status.expression}"/>_sample"
                        class="matrixRowDefault"
                        style="background-color: <c:out value="${status.value}"/>" />   
                        
			<input type="Text" name="<c:out value="${status.expression}"/>" 
					value="<c:out value="${status.displayValue}"/>"
               onchange="document.forms[0].elements['<c:out value="${status.expression}"/>_sample'].style.backgroundColor='' + document.forms[0].elements['<c:out value="${status.expression}"/>'].value"/>
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
      <spring:bind path="criterion.textColor">
         <label><fmt:message key="label_fontColor"/></label>   
      
         <input type="text" disabled="disabled" value="" size="2" 
                        name="<c:out value="${status.expression}"/>_sample"
                        class="matrixRowFontAsBGDefault" 
                        style="background-color: <c:out value="${status.value}"/>" />
         <input type="Text" name="<c:out value="${status.expression}"/>" 
               value="<c:out value="${status.displayValue}"/>"
               onchange="document.forms[0].elements['<c:out value="${status.expression}"/>_sample'].style.backgroundColor='' + document.forms[0].elements['<c:out value="${status.expression}"/>'].value"/>
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
		<input type="submit" name="updateAction" class="active" value="<osp:message key="button_update"/>"/>
		<input type="button" name="action" value="<osp:message key="button_cancel"/>" onclick="javascript:doCancel()"/>
	</div>

</form>
<form name="cancelForm" method="GET" action="<osp:url value="addScaffolding.osp" />">
	<osp:form/>
	<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
</form>
	
