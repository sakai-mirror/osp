<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="suppress_next" value="true" />
<c:set var="show_progress" value="false" />
<c:if test="${reflect.usingWizard}">
    <c:set var="show_review" value="true" />
    <c:set var="show_progress" value="true" />
</c:if>
<c:if test="${!reflect.usingWizard}">
    <c:set var="suppress_previous" value="true" />
    <c:set var="suppress_title" value="true" />
</c:if>

<c:set var="wizardTitle" value="Reflection Summary" />
<%@ include file="/WEB-INF/jsp/matrix/reflection/wizardHeader.inc"%>

<form name="wizardform" method="post" onsubmit="return true;"
    action="<osp:url value="reflect.osp" />"><input type="hidden"
    name="direction" value="" /> <c:forEach var="expectation"
    items="${reflect.cell.scaffoldingCell.expectations}"
    varStatus="loopStatus">
    <c:set var="i" value="${loopStatus.index}" />
    <%@ include file="reflection.inc"%>
</c:forEach> <br />
<br />

<h4>
    <c:if test="${reflect.cell.scaffoldingCell.gradableReflection}">
        <span class="reqStar">*</span>
    </c:if>
    Intellectual Growth
</h4>
<spring:bind path="reflect.growthStatement">
    <c:if test="${status.error}">
       <div class="validation"><c:out value="${status.errorMessage}"/></div>
    </c:if>
    <p class="longtext">
            <table><tr>
            <td><textarea id="growthStatement" name="growthStatement"
                rows="15" cols="75"><c:out
                value="${reflect.growthStatement}" /></textarea></td>
            </tr></table>
    </p>
</spring:bind>

<c:set var="suppress_next" value="true" /> <c:if
    test="${!reflect.cell.scaffoldingCell.gradableReflection}">
    <c:set var="suppress_submit" value="true" />
</c:if> <%@ include
    file="/WEB-INF/jsp/matrix/reflection/wizardFooter.inc"%></form>

<%@ include file="reflectionHtmlArea.inc"%>