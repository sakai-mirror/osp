<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

	
	<h3>Editing Cell Expectations</h3>
	
	
	<div class="instructions">
		Expectations (required fields are noted with an <span class="reqStarInline">*</span>)
	</div>
	
	<form method="POST">
		<osp:form/> 
		
		<h4>Expectation</h4>
        <spring:bind path="expectation.description">
            <c:if test="${status.error}">
               <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
    		<p class="longtext indnt2">
    			<span class="reqStar">*</span><label class="block">Name</label>
                <table><tr>
				<td><textarea name="<c:out value="${status.expression}"/>" 
                    id="<c:out value="${status.expression}"/>" 
                    rows="10" cols="75"><c:out value="${status.displayValue}"/></textarea></td>
                </tr></table>
		    </p>
        </spring:bind>
        
        
        <spring:bind path="expectation.required">
            <c:if test="${status.error}">
               <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
		    <p class="checkbox indnt2">
				<input type="checkbox" name="<c:out value="${status.expression}"/>_checkBox"
					value="true" id="<c:out value="${status.expression}"/>_checkBox"
					<c:if test="${status.value}">checked</c:if>
					onChange="form['<c:out value="${status.expression}"/>'].value=this.checked" 
				/>
				<label for="<c:out value="${status.expression}"/>_checkBox">Required</label>
				<input type="hidden" name="<c:out value="${status.expression}"/>"
					value="<c:out value="${status.value}"/>" />
		    </p>
        </spring:bind>

		<div class="act">
			<input type="submit" name="action" class="active" value="Update"/>
			<input type="button" name="action" value="Cancel" onclick="javascript:doCancel()"/>
		</div>
	
	</form>
	<form name="cancelForm" method="GET" action="<osp:url value="editScaffoldingCell.osp" />">
		<osp:form/>
		<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
	</form>
	
	<script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
	<script type="text/javascript" defer="1">chef_setupformattedtextarea('description');</script>
	<%--@ include file="/WEB-INF/jsp/htmlarea.inc" --%>