<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<script>
    function displayMimeTypeSelection(selectBox, divName)
    {
       var divElement = ospGetElementById(divName);
       var selectedIndex = selectBox.selectedIndex;
    
       //do nothing if nothing is selected
       if (selectedIndex == -1 || undefined == selectedIndex){
          return false;
       }
    
       var value = selectBox.options[selectedIndex].value;
    
       if (value == 'fileArtifact'){
          divElement.style.height="<c:out value="${mimeTypeListSize * 20}"/>px";
          divElement.style.visibility="visible";
       } else {
          divElement.style.height="0px";
          divElement.style.visibility="hidden";
       }
    
       resetHeight();
    }
</script>

<spring:bind path="template.item.id">
    <input type="hidden" name="<c:out value="${status.expression}"/>"
        value="<c:out value="${status.value}"/>" />
</spring:bind>

<input type="hidden" name="templateId" value="<c:out value="${template.id}"/>" />

<spring:bind path="template.item.action">
    <input type="hidden" id="<c:out value="${status.expression}"/>"
        name="<c:out value="${status.expression}"/>" value="" />
</spring:bind>

<spring:bind path="template.item.type">
    <c:if test="${status.error}">
        <div class="validation"><c:out value="${status.errorMessage}" /></div>
    </c:if>
    <p class="shorttext">
        <span class="reqStar">*</span>
        <label for="<c:out value="${status.expression}"/>"><fmt:message key="label_type"/></label>
        <select id="<c:out value="${status.expression}"/>"
                name="<c:out value="${status.expression}"/>"
                onchange='displayMimeTypeSelection(this,"mimeTypeSelection")'>
            <option value=""><fmt:message key="addItemDef_pleaseSelectaType"/></option>
            <option value="">- - - - - - - - - - - - - - - - - - - - -</option>
            <option
                <c:if test="${status.value == 'fileArtifact'}">selected</c:if>
                value="fileArtifact"><fmt:message key="addItemDef_uploadedFile"/></option>
            <c:forEach var="home" items="${homes}">
                <c:if test="${!home.value.type.systemOnly}">
                    <option
                        <c:if test="${status.value == home.value.type.id.value}">selected</c:if>
                        value="<c:out value="${home.value.type.id.value}"/>"><c:out
                        value="${home.value.type.description}" /></option>
                </c:if>
            </c:forEach>
        </select> 
    </p>
</spring:bind>

<spring:bind path="template.item.name">
    <c:if test="${status.error}">
        <div class="validation"><c:out value="${status.errorMessage}" /></div>
    </c:if>
    <p class="shorttext">
        <span class="reqStar">*</span>
        <label for="<c:out value="${status.expression}"/>"><fmt:message key="label_name"/></label>
        <input type="text"
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>">
    </p>
</spring:bind>

<spring:bind path="template.item.title">
    <c:if test="${status.error}">
        <div class="validation"><c:out value="${status.errorMessage}" /></div>
    </c:if>
    <p class="shorttext">
        <span class="reqStar">*</span>
        <label for="<c:out value="${status.expression}"/>"><fmt:message key="label_title"/></label>
        <input type="text"
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>">
    </p>
</spring:bind>

<spring:bind path="template.item.description">
    <c:if test="${status.error}">
        <div class="validation"><c:out value="${status.errorMessage}" /></div>
    </c:if>
    <p class="longtext">
        <label class="block" for="<c:out value="${status.expression}"/>"><fmt:message key="label_description"/></label>
        <table><tr>
        <td><textarea cols="80" rows="5" name="<c:out value="${status.expression}"/>"><c:out
            value="${status.value}" /></textarea></td>
        </tr></table>
    </p>
</spring:bind>

<spring:bind path="template.item.allowMultiple">
    <fieldset>
        <legend class="radio"><fmt:message key="legend_AllowMultipleSelection"/></legend>
        <div class="checkbox indnt1">
            <input type="radio" id="multiYes"
                name="<c:out value="${status.expression}"/>" value="true"
                <c:if test="${status.value == true}">checked</c:if> />
            <label for="multiYes"><fmt:message key="label_yes"/></label>
        </div>
        <div class="checkbox indnt1">
            <input type="radio" id="multiNo"
                name="<c:out value="${status.expression}"/>" value="false"
                <c:if test="${status.value == false}">checked</c:if> />
            <label for="multiNo"><fmt:message key="label_no"/></label>
        </div>
    </fieldset>
</spring:bind>


<spring:bind path="template.item.mimeTypes">

    <div style="visibility:hidden; height: 0px" id="mimeTypeSelection">
        <p class="shorttext">
                <label><fmt:message key="label_limitToTheseMimeTypes"/></label>
                <table cellspacing="0" cellpadding="0" border="0"><tr><td>
                <c:forEach var="mimeType" items="${mimeTypeList}">
                    <input type="checkbox"
                        name="<c:out value="${status.expression}"/>"
                        <c:forEach var="next" items="${template.item.mimeTypes}"><c:if test="${mimeType eq next.value}">checked</c:if></c:forEach>
                        value="<c:out value="${mimeType}"/>">
                    <c:out value="${mimeType}" />
                    <br />
                </c:forEach>
                </td></tr></table>
        </p>
    </div>
</spring:bind>


    <p class="act indnt5">
        <c:choose>
            <c:when test="${param.editItem}">
                <input type="submit" name="_target2" value="<fmt:message key="button_saveEdit"/>"
                    onclick="setElementValue(<spring:bind path="template.item.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addItem');return true;" />
            </c:when>
            <c:otherwise>
                <input type="submit" name="_target2" value="<fmt:message key="button_addToList"/>"
                    onclick="setElementValue(<spring:bind path="template.item.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addItem');return true;" />
            </c:otherwise>
        </c:choose>
    </p>

<spring:bind path="template.item.type">
    <script>
displayMimeTypeSelection(ospGetElementById("<c:out value="${status.expression}"/>"),"mimeTypeSelection");
</script>
</spring:bind>
