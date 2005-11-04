<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

	<h3>Editing Scaffolding Level</h3>
	
	
	<div class="instruction">
		Scaffolding Level
		(required fields are noted with an <span class="reqStarInline">*</span>)
	</div>
	
	<form method="POST">
		<osp:form/> 
		<input type="hidden" name="submitAction" value="Update" />
        
		<h4>Scaffolding Level</h4>
		
        <spring:bind path="level.description">
          <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
          </c:if>
		  <p class="shorttext indnt2">
				<span class="reqStar">*</span><label>Name</label> 
				<input type="text" name="<c:out value="${status.expression}"/>" 
					   value="<c:out value="${status.displayValue}"/>"/>
		    </p>
        </spring:bind>
		
		<div class="act">
			<spring:bind path="level.id">
				<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
				<span class="error_message"><c:out value="${status.errorMessage}"/></span>
			</spring:bind>
			<input type="submit" name="action" class="active" value="Update"/>
			<input type="button" name="action" value="Cancel" onclick="javascript:doCancel()"/>
		</div>
		
	</form>
	<form name="cancelForm" method="GET" action="<osp:url value="addScaffolding.osp" />">
		<osp:form/>
		<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
	</form>
