<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="show_progress" value="true" />
<c:set var="wizardTitle" value="Expectation Reflection" />
<%@ include file="/WEB-INF/jsp/matrix/reflection/wizardHeader.inc" %>

<form name="wizardform" method="post" onsubmit="return true;" action="<osp:url value="reflect.osp" />">
    <input type="hidden" name="direction" value=""/>
    <c:set var="i" value="${reflect.currentVirtualPage}" />
    
    <%@ include file="reflection.inc" %>
    <c:set var="suppress_submit" value="true" />
    
    <%@ include file="/WEB-INF/jsp/matrix/reflection/wizardFooter.inc" %>

</form>
<%@ include file="reflectionHtmlArea.inc" %>
