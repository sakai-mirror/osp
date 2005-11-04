<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<SCRIPT LANGUAGE="JavaScript">

function checkForm() {
	if (document.wizardform.direction.value=='cancel') {
	   return true;
	}   
	
	if (typeof document.wizardform.selectedExpectations.length == "undefined") {
		if (document.wizardform.selectedExpectations.checked) { return true; }
	}
	else {
		<c:forEach var="expectation" items="${reflect.cell.scaffoldingCell.expectations}" varStatus="loopStatus">
		   if (document.wizardform.selectedExpectations.length><c:out value="${loopStatus.index}"/>) {
		   	alert(document.wizardform.selectedExpectations[<c:out value="${loopStatus.index}"/>].checked);
		      if (document.wizardform.selectedExpectations[<c:out value="${loopStatus.index}"/>].checked) { return true; }
		   }
		</c:forEach>
	}
   alert("Please select at least one item.");
   return false;
}

</SCRIPT>

<c:set var="wizardTitle" value="Expectation Selection" />
<%@ include file="/WEB-INF/jsp/matrix/reflection/wizardHeader.inc"%>

<!--<form name="wizardform" method="post" onsubmit="return true;"> -->
<form name="wizardform" method="post"
    action="<osp:url value="reflect.osp" />" onsubmit="return true;"><%-- <form name="wizardform" method="post" onsubmit="return checkForm();"> --%>
    <input type="hidden" name="direction" value="" />

    <osp-h:glossary link="true" hover="true">
        <c:out
            value="${reflect.cell.scaffoldingCell.expectationHeader}"
            escapeXml="false" />
    </osp-h:glossary>
    <br/><br/>
    <h4>Expectation Selection</h4>
    
    <p class="instruction">Please select all that apply.  
        At least one must be selected.</p>

    <osp-h:glossary hover="true" link="true">
         <spring:bind path="reflect.selectedExpectations">
                <c:forEach var="expectation"
                    items="${reflect.cell.scaffoldingCell.expectations}"
                    varStatus="loopStatus">
    
                    <p class="checkbox"><%--    <input type="hidden" name="_<c:out value="${status.expression}"/>"  value="visible" /> --%>
                        <input type="checkbox"
                            name="<c:out value="${status.expression}"/>"
                            <c:if test="${reflect.selectedExpectations[loopStatus.index]=='on'}">checked</c:if>
                            value="<c:out value="${loopStatus.index}"/>" />
    
                        <label> <c:out
                            value="${expectation.description}"
                            escapeXml="false" />
                        </label>
                    </p>
    
                </c:forEach>
        <span class="error_message"><c:out value="${status.errorMessage}" /></span>
        </spring:bind>
    </osp-h:glossary>
    <c:set var="suppress_submit" value="true" />
    <c:set var="suppress_save" value="true" />
    <c:set var="suppress_previous" value="true" />
    <%@ include file="/WEB-INF/jsp/matrix/reflection/wizardFooter.inc"%>
</form>
