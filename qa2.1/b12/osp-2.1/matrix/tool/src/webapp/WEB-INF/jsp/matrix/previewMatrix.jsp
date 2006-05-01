<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<table cellspacing="0" width="100%">
  <tr>
    <th class="matrix-row-heading" width="400">
      <c:out value="${bean.matrixTool.scaffolding.title}"/>
    </th>
    <c:forEach var="head" items="${bean.matrixTool.scaffolding.levels}">
      <th class="matrix-column-heading" width="200">
        <c:out value="${head.description}"/>
      </th>
    </c:forEach>
  </tr>
  <c:forEach var="rowLabel" items="${bean.matrixTool.scaffolding.criteria}" varStatus="loopStatus" >
    <tr>
      <th class="matrix-row-heading" bgcolor="<c:out value="${rowLabel.color}"/>" >
        <c:out value="${rowLabel.description}"/>
      </th>
      <c:forEach var="rowLabel" items="${bean.matrixTool.scaffolding.levels}" varStatus="loopStatus" >
        <td class="matrix-READY" style="cursor:pointer">
          &nbsp;
        </td>
      </c:forEach>
    </tr>
  </c:forEach>
</table>

