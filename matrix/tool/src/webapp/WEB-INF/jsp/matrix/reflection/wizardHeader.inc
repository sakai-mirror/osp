<SCRIPT LANGUAGE="JavaScript">
var regularInit = true;
function submitWizardForm(direction) {
//	alert(document.wizardform.onsubmit());
   document.wizardform.direction.value=direction;
   document.wizardform.onsubmit();
   document.wizardform.submit();
  // if (document.wizardform.onsubmit()) {
  //    document.wizardform.submit();
  // }
}


</SCRIPT>
<osp-h:glossary link="true" hover="true">
	<h3><c:out value="${wizardTitle}"/></h3>
	<c:if test="${show_progress}">
		<p class="step">
			Step <strong><c:out value="${reflect.effectivePage-1}"/> </strong>of <strong><c:out value="${reflect.totalPages+1}"/></strong>
		</p>
	</c:if>
	<table class="itemSummary">
   		<tr><th>Level: </th><td><c:out value="${reflect.cell.scaffoldingCell.level.description}"/></td></tr>
   		<tr><th>Criteria: </th><td><c:out value="${reflect.cell.scaffoldingCell.rootCriterion.description}"/></td></tr>
	</table>
</osp-h:glossary>