<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<form method="POST" action="<osp:url value="reviewPostProcessor.osp"/>">
   <osp:form/>
   
   <h4>Matrix Progression</h4>
   <fieldset>
      <legend class="radio">Choose workflow option: </legend>
      <c:forEach var="option" items="${workflows}" varStatus="loopStatus">
         <div class="checkbox indnt1">
            <input type="radio" id="workflow_option_<c:out value="${loopStatus.index}" />" 
                  name="workflowId" 
                  value="<c:out value="${option.id}" />" />
            <label for="workflow_option_<c:out value="${loopStatus.index}" />">
               <c:out value="${option.title}" />
            </label>
         </div>
      </c:forEach>
   </fieldset>
   
   <p class="act">      
      <input type="submit" name="submit" class="active" value="<osp:message key="submit" bundle="${msgs}" />"/>
      <input type="hidden" name="objId" value="<c:out value="${obj_id}"/>"/>
      <input type="hidden" name="manager" value="<c:out value="${manager}"/>"/>
   </p>
</form>