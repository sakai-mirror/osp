<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

    <h3><osp:message key="submit_cell_confirmation"/></h3>
   
      <span class="chefAlert"><fmt:message key="instructions_paragraph1"/></span>
      <br />

	<table class="listHier">
	   <tr>
	      <th><fmt:message key="table_header_title"/></th>
          <th><fmt:message key="label_description"/></th>
       </tr>
       <tr>
          <td><c:out value="${page.pageDefinition.title}"/></td>
          <td><c:out value="${page.pageDefinition.description}" escapeXml="false"/></td>
       </tr>
    </table>
<form>

   <p class="act">
      <input type="hidden" name="page_id" value="<c:out value="${page_id}" />">
      <input type="submit" name="submit" value="<osp:message key="button_submitCell"/>" class="active">
      <input type="submit" name="cancel" value="<osp:message key="button_cancel"/>">
   </p>
</form>