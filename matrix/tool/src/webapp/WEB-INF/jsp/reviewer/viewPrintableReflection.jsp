<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set  var="cell" value="${reviewerItem.cell}"/>
<table cellpadding="5">
<c:forEach var="item" items="${cell.reflection.reflectionItems}" varStatus="loopStatus">
   <c:set var="reflectionItem" value="${cell.reflection.reflectionItems[loopStatus.index]}"/>
   <tr><td colspan="2">
   <b>Expectation <c:out value="${loopStatus.index+1}"/>:</b>
   </td>
   </tr>
   <tr>
   <td width="30"></td><td><p>
  <b>Evidence:</b>
   <c:out value="${reflectionItem.evidence}" escapeXml="false" /></p>
   </td>
   </tr>
   <tr>
   <td></td><td><p>
   <b>Connect:</b>
   <c:out value="${reflectionItem.connect}" escapeXml="false" /></p>
   </td>
</tr>   
</c:forEach>
<tr>
<td colspan="2">
<b>Intellectual Growth:</b>
</td>
</tr>
<tr>
<td></td><td width="550"><p>
<c:out value="${cell.reflection.growthStatement}" escapeXml="false" /></p>
</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
<td colspan="2">
<b>Reviewer Comments:</b>
</td>
</tr>
<tr>
<td></td><td width="550">
<spring:message code="${reviewRubrics[reviewerItem.grade].displayText}" text="${reviewRubrics[reviewerItem.grade].displayText}" />
</td>
</tr>
<tr>
   <td></td><td><p>
   <c:out value="${reviewerItem.comments}" escapeXml="false" /></p>
   </td>
</tr>   
</table>

<form method="POST">
<osp:form/>

<div class="chefButtonRow">
	<input type="button" value="Print" onclick="window.print()"/>
	<input type="button" value="Close" onclick="window.close()"/>
</div>
</form>