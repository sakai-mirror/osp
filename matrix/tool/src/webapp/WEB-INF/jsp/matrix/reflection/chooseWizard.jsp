<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="show_progress" value="false" />
<c:set var="wizardTitle" value="Reflection Wizard Confirmation" />

<%@ include file="/WEB-INF/jsp/matrix/reflection/wizardHeader.inc" %>
<script>
function doSubmit(val)
{
	document.choose.usingWizard.value=val;
	document.choose.direction.value='next';
	document.choose.submit();
}
</script>

    <p>&nbsp;</p>
    <p class="instruction">Would you like to use the reflection wizard?</p>

    <form name="choose" method="post" action="<osp:url value="reflect.osp" />">
        <spring:bind path="reflect.usingWizard">
            <input type="hidden" name="usingWizard" value="">
            <input type="hidden" name="direction" value=""/>
		</spring:bind>
<!--           <input type="hidden" name="_target1" value="next"/> -->
        <p class="act">
            <input type="button" value="Yes" onclick="doSubmit(true)"/>
            <input type="button" value="No" onclick="doSubmit(false)"/>
        </p>
        
        <spring:bind path="reflect.cell.id">
			<input type="hidden" name="cell_id" value="<c:out value="${reflect.cell.id}"/>"/>
        </spring:bind>

    </form>


