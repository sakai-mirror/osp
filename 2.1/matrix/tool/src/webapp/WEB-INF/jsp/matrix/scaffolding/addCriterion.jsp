<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

	
	
	<h3>Editing Scaffolding Criterion</h3>
	
	<%--  Hiding Sub criteria for now
	<div class="chefToolBarWrapForm">
		<a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=<c:out value="${path}"/>.<c:out value="${index}"/>';document.forms[0].submit();">
			Add Sub Criterion...
		</a>
	</div>
	--%>
	
	<div class="instructions">
		Scaffolding Criterion
		(required fields are noted with an <span class="reqStarInline">*</span>)
	</div>
	
<form method="POST">
	<osp:form/> 
	<input type="hidden" name="params" value="" />
	<input type="hidden" name="submitAction" value="" />
	<input type="hidden" name="dest" value="" />
	<input type="hidden" name="validate" value="false" />
	
	<h4>Scaffolding Criterion</h4>
	
    <spring:bind path="criterion.description">
        <c:if test="${status.error}">
           <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
    	<p class="shorttext indnt2">
            <span class="reqStar">*</span><label>Name</label>
    		<input type="text" name="<c:out value="${status.expression}"/>" 
    				value="<c:out value="${status.displayValue}"/>"/>
    	</p>
    </spring:bind>
	<p class="shorttext indnt2">
		<spring:bind path="criterion.color">
			<label>Background Color</label>   
		
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
	<%--
	<!-- test sub criteria -->
	<h4>Sub Criteria</h4>
	
	<table class="itemSummary" cellspacing="0">
		<thead>
			<tr>
				<th scope="col">Name</th>
				<th scope="col">Actions</th>
			</tr>
		</thead>
		<tbody>
			<spring:bind path="criterion.criteria">
				<c:set var="rootIndex" value="-1"/>
				<c:forEach var="criterion" items="${criterion.criteria}" varStatus="itemLoopStatus">
					<c:set var="rootIndex" value="${rootIndex+1}"/>
					<tr>
						<td <c:if test="${not empty criterion.color}">bgcolor="<c:out value="${criterion.color}"/>"</c:if>>
							<c:out value="${criterion.description}"/>
						</td>
						<td>
							<a href="javascript:document.forms[0].dest.value='addCriterion';
							document.forms[0].submitAction.value='forward';
							document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>:path=<c:out value="${path}"/>';
							document.forms[0].submit();">
								Edit
							</a>
							<c:if test="${empty criterion.id}">
							| <a href="javascript:document.forms[0].dest.value='deleteCriterion';
							document.forms[0].submitAction.value='forward';
							document.forms[0].params.value='criterion_id=<c:out value="${criterion.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
							document.forms[0].submit();">
								Remove
							</a>
							</c:if>
							| <a href="javascript:document.forms[0].dest.value='moveCriterion';
							document.forms[0].submitAction.value='forward';
							document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>:current_root_index=<c:out value="${rootIndex}"/>:dest_root_index=<c:out value="${rootIndex-1}"/>';
							document.forms[0].submit();">
								Up
							</a>
							| <a href="javascript:document.forms[0].dest.value='moveCriterion';
							document.forms[0].submitAction.value='forward';
							document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>:current_root_index=<c:out value="${rootIndex}"/>:dest_root_index=<c:out value="${rootIndex+1}"/>';
							document.forms[0].submit();">
								Down
							</a>
						</td>
					</tr>
				</c:forEach>
				<span class="error_message"><c:out value="${status.errorMessage}"/></span>
			</spring:bind>
		</tbody>
	</table>	
	<!-- end test sub criteria -->
	--%>
	<div class="act">
	
		<spring:bind path="criterion.indent">
			<input type="hidden" name="<c:out value="${status.expression}"/>" 
				value="0"/>
		</spring:bind>
		<input type="submit" name="action" class="active" value="Update"/>
		<input type="button" name="action" value="Cancel" onclick="javascript:doCancel()"/>
	
	</div>

</form>
<form name="cancelForm" method="GET" action="<osp:url value="addScaffolding.osp" />">
	<osp:form/>
	<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
</form>
	
