<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<osp:url var="listUrl" value="listAssignment.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3><fmt:message key="assign.title.manage"/></h3>

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
     <c:forEach var="assign" items="${assignments}">
        <tr>
<%--
          <td><c:out value="${assign.id}" /></td>
--%>			
          <td></td>
          <td><c:out value="${assign.title}" /></td>
          <td><c:out value="${assign.status}" /></td>
          <td><c:out value="${assign.openTimeString}" /></td>
          <td><c:out value="${assign.dueTimeString}" /></td> 
        </tr>
     </c:forEach>
    </tbody>
</table>
  
<div class="act">
   <input type="button" name="goBack" class="active" value="<fmt:message key="button_goback"/>"
        onclick="window.document.location='<osp:url value="listAssignment.osp"/>&goBack=true'"/>
</div>