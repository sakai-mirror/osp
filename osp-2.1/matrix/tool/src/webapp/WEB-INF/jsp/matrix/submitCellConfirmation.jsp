<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

    <h3><osp:message key="submit_cell_confirmation"/></h3>
    
   <osp-h:glossary link="true" hover="true">
      <table class="itemSummary">
         <tr><th><c:out value="${cell.scaffoldingCell.scaffolding.columnLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.level.description}"/></td></tr>
         <tr><th><c:out value="${cell.scaffoldingCell.scaffolding.rowLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.rootCriterion.description}"/></td></tr>
      </table>
   </osp-h:glossary>
   
      <p class="instruction"><fmt:message key="instructions_paragraph1"/>
      </p>
      <p class="instruction"><fmt:message key="instructions_paragraph2"/>
      </p>
      <br />

<form>

   <p class="act">
      <input type="hidden" name="page_id" value="<c:out value="${page_id}" />">
      <input type="submit" name="submit" value="<osp:message key="button_submitCell"/>" class="active">
      <input type="submit" name="cancel" value="<osp:message key="button_cancel"/>">
   </p>
</form>