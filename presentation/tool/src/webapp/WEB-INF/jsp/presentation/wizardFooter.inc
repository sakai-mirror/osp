<div class="act">
    <c:if test="${!suppress_previous}">
        <input type="submit" name="<c:out value="${targetPrevious}"/>" value="Back"/>
    </c:if>
    <c:if test="${!suppress_next}">
        <input type="submit" name="<c:out value="${targetNext}"/>" value="Continue"/>
    </c:if>
    <c:if test="${updateSeq}">
        <input type="submit" name="<c:out value="${target}"/>" value="Update"/>
    </c:if>
    <c:if test="${!suppress_save}">
        <input type="submit" name="_finish" value="Save"/>
    </c:if>
    <c:if test="${!suppress_submit}"> 
        <input type="submit" name="_finish_notify" value="Save and Notify"/>
    </c:if>
    <c:if test="${!suppress_cancel}"> 
        <input type="submit" name="_cancel" value="Cancel"/>
    </c:if>
</div>