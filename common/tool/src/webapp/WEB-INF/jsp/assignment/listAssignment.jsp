<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<osp:url var="listUrl" value="listAssignment.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3><fmt:message key="assign.title.manage"/></h3>

<form method="POST" action="<osp:url value="listAssignment.osp"/>">

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col"></th>
         <th scope="col"><fmt:message key="assign.title"/></th>
         <th scope="col"><fmt:message key="assign.status"/></th>
         <th scope="col"><fmt:message key="assign.open"/></th>
         <th scope="col"><fmt:message key="assign.due"/></th>
      </tr>
   </thead>
   <tbody>
     <c:forEach var="bean" items="${assignments}">
        <tr>
         <td>
            <input type="checkbox" 
                   id="<c:out value='${bean.assignment.id}'/>" 
                   name="<c:out value='${bean.assignment.id}'/>" 
                   <c:if test="${bean.selected}">checked='checked'</c:if> 
                   />
         </td> <!-- checked -->
          <td><c:out value="${bean.assignment.title}" /></td>
          <td><c:out value="${bean.assignment.status}" /></td>
          <td><c:out value="${bean.assignment.openTimeString}" /></td>
          <td><c:out value="${bean.assignment.dueTimeString}" /></td> 
        </tr>
     </c:forEach>
    </tbody>
</table>

<div class="act">
      <input type="submit" name="_save" value="<fmt:message key="button_save"/>" />
      <input type="submit" name="_cancel" value="<fmt:message key="button_cancel"/>" />

</div>

</form>