<spring:bind path="reflect.cell.id">
<input type="hidden" name="cell_id" value="<c:out value="${reflect.cell.id}"/>"/>
</spring:bind>

<input type="hidden" name="foo_id" value="<c:out value="${reflect.id.value}"/>"/>

<%--
<spring:bind path="${reflect.id.value}">
	<input name="id" type="hidden" value="<c:out value="${reflect.id.value}"/>"/>
</spring:bind>
--%>
<p class="act">
    <c:if test="${!suppress_previous}"> 
    	<input type="submit" onclick="submitWizardForm('previous'); return false;" value="Back">
    	&nbsp;
    </c:if>
    <c:if test="${!suppress_next}"> 
    	<input type="submit" onclick="submitWizardForm('next'); return false;" value="Continue">
    </c:if> 
    <c:if test="${!suppress_save}"> 
    	<input type="submit" onclick="submitWizardForm('save'); return false;" value="Save">
    </c:if> 
    <c:if test="${!suppress_submit}"> 
    	<input type="submit" onclick="submitWizardForm('submit'); return false;" value="Submit for Review">
    </c:if> 
    <c:if test="${!suppress_cancel}"> 
    	<input type="submit" onclick="submitWizardForm('cancel'); return false;" value="Cancel">
    </c:if> 
</p >
<!--
<table width="550" border="0" height="81">
  <tr>
    <td>&nbsp;</td>
    <c:if test="${!suppress_previous}"> 
    <td><input type="submit" onclick="submitWizardForm('previous'); return false;" value="Back"></td>
    </c:if>
    <td>&nbsp;</td>
    <c:if test="${!suppress_next}"> 
    <td><input type="submit" onclick="submitWizardForm('next'); return false;" value="Continue"></td>
    </c:if> 
    <c:if test="${!suppress_save}"> 
    <td><input type="submit" onclick="submitWizardForm('save'); return false;" value="Save"></td>
    </c:if> 
    <c:if test="${!suppress_submit}"> 
    <td><input type="submit" onclick="submitWizardForm('submit'); return false;" value="Submit for Review"></td>
    </c:if> 
    <c:if test="${!suppress_cancel}"> 
    <td><input type="submit" onclick="submitWizardForm('cancel'); return false;" value="Cancel"></td>
    </c:if> 
  </tr>
</table>
-->
