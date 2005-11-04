<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="targetPrevious" value="_target0" />
<c:set var="targetNext" value="_target2" />


<form method="POST" action="addTemplate.osp">
    <osp:form />
    <input type="hidden" name="pickerField" value="" />
    <input type="hidden" name="validate" value="true" />
    <spring:bind path="template.id">
        <c:set var="templateId" value="${status.value}" />
    </spring:bind>


    <h3>Build Template</h3>
    <%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>
    <p class="instruction">
        Select the files that define the structure of the template
        (required fields are noted with an <span class="reqStarInline">*</span>)
    </p>

    <spring:bind path="template.renderer">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <span class="reqStar">*</span>
            <label>Basic Template Outline</label>
            
            <input type="text" id="rendererName" disabled="true"
                value="<c:out value="${rendererName}"/>" />
            <input type="hidden" name="renderer" id="renderer"
                value="<c:out value="${status.value}"/>" />
            <input type="hidden" name="_target4" id="_target4" value="" />
            <input type="hidden" name="returnPage" id="returnPage"
                value="<c:out value="${currentPage-1}"/>" />
            <a href="javascript:document.forms[0].pickerField.value='<c:out value="${TEMPLATE_RENDERER}"/>';javascript:document.forms[0]._target4.value='picker';document.forms[0].validate.value='false';document.forms[0].submit();">
            Pick file </a>
    </spring:bind>

    <spring:bind path="template.propertyPage">
        <p class="shorttext">
            <label>Outline Options</label>
            <input type="text" id="propertyPageName" disabled="true"
                value="<c:out value="${propertyPageName}"/>" />
            <input type="hidden" name="propertyPage" id="propertyPage"
                value="<c:out value="${status.value}"/>" />
            <a href="javascript:document.forms[0].pickerField.value='<c:out value="${TEMPLATE_PROPERTYFILE}"/>';javascript:document.forms[0]._target4.value='picker';document.forms[0].validate.value='false';document.forms[0].submit();">
                Pick file </a>
        </p>
    </spring:bind>

    <spring:bind path="template.documentRoot">
        <p class="shorttext">
            <label>Outline Options File Element</label>
            <select name="<c:out value="${status.expression}" />"
                id="<c:out value="${status.expression}" />">
                <c:forEach var="element" items="${elements}"
                    varStatus="status">
                    <option value="<c:out value="${element}"/>"><c:out
                        value="${element}" /></option>
                </c:forEach>
            </select>
        </p>
    </spring:bind>


    <c:set var="suppress_submit" value="true" />
    <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
</form>


