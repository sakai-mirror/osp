<div class="act">
    <c:if test="${!suppress_next}">
        <input type="submit" name="<c:out value="${targetNext}"/>" class="active" value="<fmt:message key="button_continue"/>"/>
    </c:if>
    <c:if test="${!suppress_previous and suppress_submit and !suppress_next}">
        <input type="submit" name="<c:out value="${targetPrevious}"/>" value="<fmt:message key="button_back"/>"/>
    </c:if>
    <c:if test="${previewPres}">
        <input type="submit" name="<c:out value="${targetPreview}"/>" value="<fmt:message key="button_preview"/>" onClick="document.wizardform.preview.value = true;" />
    </c:if>
    <c:if test="${updateSeq}">
        <input type="submit" name="<c:out value="${target}"/>" value="<fmt:message key="button_update"/>"/>
    </c:if>
    <c:if test="${!suppress_save}">
        <input type="submit" name="_finish" value="<fmt:message key="button_save"/>"
        <c:if test="${suppress_next}">
            class="active"
        </c:if>
        />
    </c:if>
    <c:if test="${!suppress_submit}">
        <input type="submit" name="_finish_notify" value="<fmt:message key="button_saveAndNotify"/>" />

        <c:if test="${!suppress_previous}">
            <input type="submit" name="<c:out value="${targetPrevious}"/>" value="<fmt:message key="button_back"/>"/>
        </c:if>
    </c:if>
    <c:if test="${suppress_submit and !suppress_previous and suppress_next}">
            <input type="submit" name="<c:out value="${targetPrevious}"/>" value="<fmt:message key="button_back"/>"/>
        </c:if>
    <c:if test="${!suppress_cancel}"> 
        <input type="submit" name="_cancel" value="<fmt:message key="button_cancel"/>"/>
    </c:if>
</div>
