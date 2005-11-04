<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="suppress_previous" value="true" />
<% /* <c:set var="suppress_title" value="true" /> */ %>
<c:set var="wizardTitle" value="Reflection Submission  Confirmation" />
<c:set var="suppress_next" value="true" />
<c:set var="suppress_save" value="true" />
<%@ include file="/WEB-INF/jsp/matrix/reflection/wizardHeader.inc" %>
      <p class="instruction">Submitting
        your reflection will lock the cell
        and you will not be able to edit your materials.
      </p>
      <p class="instruction">Are
        you sure you want to submit?
      </p>
      <br />

<form name="wizardform" method="post" onsubmit="return true;" action="<osp:url value="reflect.osp" />">
<input type="hidden" name="direction" value=""/>

<%@ include file="/WEB-INF/jsp/matrix/reflection/wizardFooter.inc" %>

</form>