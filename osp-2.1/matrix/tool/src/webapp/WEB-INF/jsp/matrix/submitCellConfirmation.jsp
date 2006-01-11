<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

    <h3><osp:message key="view_cell" bundle="${msgs}" /></h3>
    
   <osp-h:glossary link="true" hover="true">
      <table class="itemSummary">
         <tr><th><c:out value="${cell.scaffoldingCell.scaffolding.columnLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.level.description}"/></td></tr>
         <tr><th><c:out value="${cell.scaffoldingCell.scaffolding.rowLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.rootCriterion.description}"/></td></tr>
      </table>
   </osp-h:glossary>
   
      <p class="instruction">Submitting
        your cell will lock it and you will not be able to edit your materials.
      </p>
      <p class="instruction">Are
        you sure you want to submit?
      </p>
      <br />

<form>

   <p class="act">
      <input type="hidden" name="cell_id" value="<c:out value="${cell_id}" />">
      <input type="submit" name="submit" value="Submit for Review" class="active">
      <input type="submit" name="cancel" value="Cancel">
   </p>
</form>