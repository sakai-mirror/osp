<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<h3>Manage Cell Status</h3>
   
<div class="validation">
   Note - If you change the status for all matrix users, the status will be set to 
   '<c:out value="${newStatus}" />' regardless of the cell's current status.
</div>



<form method="POST">

   <fieldset>
      <legend class="radio">Change Status to <c:out value="${newStatus}" />: </legend>
      <div class="checkbox indnt1">
         <input type="radio" id="changeUserOnly" name="changeUserOnly" value="true"
         <label for="changeUserOnly">For this user only</label>
      </div>
      <div class="checkbox indnt1">
         <input type="radio" id="changeAll" name="changeAll" value="true"
         <label for="changeAll">For all matrix users</label>
      </div>
   </fieldset>
    
   <div class="act">
      <input name="continue" type="submit" value="<osp:message key="continue" bundle="${msgs}" />"/>
      <input name="cancel" type="submit" value="<osp:message key="cancel" bundle="${msgs}" />"/>
   </div>
</form>