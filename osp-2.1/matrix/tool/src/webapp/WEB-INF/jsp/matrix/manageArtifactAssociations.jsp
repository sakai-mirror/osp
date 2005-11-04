<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<form name="form" method="POST">
	<spring:bind path="matrix.action">
		<input type="hidden" name="action" value="submit">
	</spring:bind>
	<spring:bind path="matrix.cellId">
		<input type="hidden" name="cellId" value="<c:out value="${matrix.cellId}"/>">
	</spring:bind>
	<spring:bind path="matrix.nodeId">
		<input type="hidden" name="nodeId" value="<c:out value="${matrix.nodeId}"/>">
	</spring:bind>  
	
	<table class="header_table">
		<tr><td class="header_info_small">
			<strong><c:out value="${matrix.node.name}"/></strong> satisfies the following criteria (please select all that apply)
		</td></tr>
	</table>
	<br/>
	<spring:bind path="matrix.selectedCriteria">
		<table width="40%"  >
			<c:forEach var="criterion" items="${matrix.criteria}" varStatus="loopCount">
				<tr>
					<td>
						<c:forEach begin="0" end="${criterion[0].indent}" step="1" >
							&nbsp;&nbsp;&nbsp;
						</c:forEach>
						<input type="checkbox" name="<c:out value="${status.expression}"/>" 
							<c:if test="${criterion[1]}">checked</c:if>
							value="<c:out value="${criterion[0].id}"/>">
							<c:out value="${criterion[0].description}"/>
						</input>
					
					</td>
				</tr>
				<c:set var="itemName" value="${status.expression}"/>
				<c:set var="items" value="${loopCount.index+1}"/>
			</c:forEach>
		</table>
		<br/>
	</spring:bind>
	<input type="submit" value="Save">

</form>