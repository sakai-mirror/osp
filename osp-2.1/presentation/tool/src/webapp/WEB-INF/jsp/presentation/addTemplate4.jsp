<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="targetPrevious" value="_target2" />


<h3>Select supporting files</h3>

<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>

<form method="POST" action="addTemplate.osp"><osp:form /> <input
    type="hidden" name="templateId"
    value="<c:out value="${template.id}"/>" /> <input type="hidden"
    name="pickerField" value="" /> <input type="hidden" name="validate"
    value="true" />

    <spring:bind path="template.fileRef.action">
        <input type="hidden" id="<c:out value="${status.expression}"/>"
            name="<c:out value="${status.expression}"/>" value="" />
    </spring:bind>

    <div class="instruction">
        Add any supporting files needed for this
        template. These may include cascading Styles Sheets (css),
        pictures/images (jpg, gif, png), utilities or scripts (xml,jvs)
    </div>

    <spring:bind path="template.fileRef.usage">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <span class="reqStar">*</span>
            <label>Name (used in xpath)</label>
            <input type="text"
                name="<c:out value="${status.expression}"/>"
                value="<c:out value="${status.value}"/>">
        </p>
    </spring:bind>
    
    <spring:bind path="template.fileRef.fileId">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
    </spring:bind>
    <p class="shorttext">
        <span class="reqStar">*</span>
        <label>Choose File</label>
        <spring:bind path="template.fileRef.artifactName">
            <input type="text" id="fileName" disabled="true"
                value="<c:out value="${status.value}" />" />
        </spring:bind>
        <spring:bind path="template.fileRef.fileId">
            <input type="hidden"
                name="<c:out value="${status.expression}"/>"
                id="<c:out value="${status.expression}"/>"
                value="<c:out value="${status.value}" />" />
            <!--<input type="hidden" name="_target4" id="_target4" value="" /> -->
            <input type="hidden" name="returnPage" id="returnPage"
                value="<c:out value="${currentPage-1}"/>" />
            <a href="javascript:callPicker('<c:out value="${TEMPLATE_SUPPORTFILE}"/>');">
            Pick file </a>

            <script language="javascript">
                function callPicker(pickerField) {
                
                    //document.write("<input type='hidden' name='_target4' id='_target4' value='true' />");
                    document.getElementById('insertTarget').innerHTML="<input type='hidden' name='_target4' id='_target4' value='true' />"
                    document.forms[0].pickerField.value=pickerField;
                    document.forms[0].validate.value='false';
                    document.forms[0].submit()
                }
            </script>
        </spring:bind>
    </p>
        


    <p class="act">
        <c:choose>
            <c:when test="${param.editFile}">
                <input type="submit" name="_target3" value="Save Edit"
                    onclick="setElementValue(<spring:bind path="template.fileRef.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addFile');return true;" />
            </c:when>
            <c:otherwise>
                <input type="submit" name="_target3" value="Add To List"
                    onclick="setElementValue(<spring:bind path="template.fileRef.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addFile');return true;" />
            </c:otherwise>
        </c:choose>
    </p>


    <table class="listHier" cellspacing="0">
        <thead>
            <tr>
                <th scope="col">File Name</th>
                <th scope="col">Full Xpath</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${template.files['empty']}">
                <tr>
                    <td colspan="3" align="center"><b>There are no supporting files</b></td>
                </tr>
            </c:if>
            <c:if test="${not template.files['empty']}">
                <c:forEach var="file" items="${template.files}">
                    <tr>
                        <td><c:out value="${file.artifactName}" />
                            <div class="itemAction">
                                <a href="<osp:url value="editTemplateFile.osp"/>&id=<c:out value="${file.id.value}" />">Edit</a>
                                |
                                <a href="<osp:url value="deleteTemplateFile.osp"/>&id=<c:out value="${file.id.value}" />">Delete</a>
                            </div>
                        </td>
                        <td nowrap>/ospiPresentation/presentationFiles/<c:out
                            value="${file.usage}" /></td>
                    </tr>
                </c:forEach>
            </c:if>
        </tbody>
    </table>
<br /><br />
<c:set var="suppress_next" value="true" />
<c:set var="suppress_submit" value="true" />
<%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
<p id="insertTarget" />
</form>
