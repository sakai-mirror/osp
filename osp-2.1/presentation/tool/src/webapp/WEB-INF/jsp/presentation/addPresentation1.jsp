<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:if test="${empty templates && empty publishedTemplates}">
<h3>No templates available</h3>
    Use the presentation template tool to create a template.
</c:if>

<c:if test="${!empty templates || !empty publishedTemplates}">
    <c:set var="targetNext" value="_target1" />
    <c:set var="suppress_previous" value="true" />
    
    <h3>Presentation Setup</h3>
    
    <div class="instruction">
    Required fields are noted with an <span class="reqStarInline">*</span>
    </div>
    <%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>


    <form method="POST" name="wizardform" action="addPresentation.osp"
        onsubmit="return true;"><input type="hidden" name="direction"
        value="" />
        <osp:form />

        <spring:bind path="presentation.name">
            <p class="shorttext">
                <c:if test="${status.error}">
                    <div class="validation"><c:out value="${status.errorMessage}"/></div>
                </c:if>
                <span class="reqStar">*</span>
                <label>Title</label>
                <input type="text"
                    name="<c:out value="${status.expression}"/>"
                    value="<c:out value="${status.displayValue}"/>" />
            </p>
        </spring:bind>

        <spring:bind path="presentation.description">
            <p class="longtext">
                <c:if test="${status.error}">
                    <div class="validation"><c:out value="${status.errorMessage}"/></div>
                </c:if>
                <label>Description</label>
                <table><tr>
                <c:set var="descriptionID" value="${status.expression}" />
                <td><textarea id="<c:out value="${status.expression}"/>"
                    name="<c:out value="${status.expression}"/>"
                    cols="80" rows="5"><c:out
                    value="${status.displayValue}" /></textarea></td>
                </tr></table>
            </p>
        </spring:bind>

        <spring:bind path="presentation.expiresOn">
            <p class="shorttext">
                <c:if test="${status.error}">
                    <div class="validation"><c:out value="${status.errorMessage}"/></div>
                </c:if>
                <label>Expires</label>
                <osp-c:dateSelect daySelectId="expiresOnBean.day"
                    yearSelectId="expiresOnBean.year"
                    monthSelectId="expiresOnBean.month"
                    earliestYear="2004"
                    selectedDate="${presentation.expiresOn}" />
            </p>
        </spring:bind>

        <spring:bind path="presentation.template.id">
            <p class="shorttext">
                <c:if test="${status.error}">
                    <div class="validation"><c:out value="${status.errorMessage}"/></div>
                </c:if>
                <span class="reqStar">*</span>
                <label>Template</label>
                <select id="<c:out value="${status.expression}"/>"
                    onchange='closeFrame("previewFrame", "previewButton","closeButton")'
                    name="<c:out value="${status.expression}"/>">
                    <option value="">Please select a template</option>
                    <option value="">- - - - - - - - - - - - - - - - - -
                    - - -</option>
                    <c:forEach var="template"
                        items="${publishedTemplates}"
                        varStatus="templateStatus">
                        <option
                            <c:if test="${presentation.template.id.value == template.id.value }">selected</c:if>
                            value="<c:out value="${template.id.value}"/>"><c:out
                            value="${template.name}" /> (Published
                        Template)
                    </c:forEach>
                    <c:forEach var="template" items="${templates}"
                        varStatus="templateStatus">
                        <option
                            <c:if test="${presentation.template.id.value == template.id.value}">selected</c:if>
                            value="<c:out value="${template.id.value}"/>"><c:out
                            value="${template.name}" /> (Your Template)
                        
                    </c:forEach>
                </select>
            </p>
            
            <p class="shorttext">
                <div style="visibility:visible" id="previewButton"><a
                    href="#"
                    onclick='showFrame("<c:out value="${status.expression}"/>","previewFrame", "previewButton","closeButton","<osp:url value="previewTemplate.osp"/>&panelId=previewFrame&id=" + getSelectedValue("<c:out value="${status.expression}"/>"),"please select a template first")'>
                    Preview template</a></div>
                <div style="visibility:hidden" id="closeButton"><a
                    href="#"
                    onclick='closeFrame("previewFrame", "previewButton","closeButton")'>
                    Close preview</a><br />
                    <iframe name="previewFrame" id="previewFrame" height="0"
                        width="650" frameborder="0" marginwidth="0"
                        marginheight="0" scrolling="auto"> </iframe>
                </div>
            </p>
        </spring:bind>


        <c:set var="suppress_submit" value="true" />
        <c:if test="${empty presentation.id}">
            <c:set var="suppress_save" value="true" />
        </c:if>
        <br />
        <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
    </form>
</c:if>
