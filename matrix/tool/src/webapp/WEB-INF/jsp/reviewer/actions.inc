   <!-- actions -->
   <c:set var="firstAction" value="true" />
  <div class="itemAction">
  <c:if test="${item.status != 'CHECKED_OUT'}" >
      <c:if test="${!firstAction}">|</c:if>
         <a href="javascript:document.forms[0].newStatus.value='CHECK_OUT';
            document.forms[0].reviewItem.value='<c:out value="${item.id}"/>';
            document.forms[0].submit();">
            Check Out
         </a>
      <c:set var="firstAction" value="false" />
  </c:if>

  <c:if test="${item.status == 'CHECKED_OUT' && item.reviewer == currentUser}" >
      <c:if test="${!firstAction}">|</c:if>
         <a href="javascript:document.forms[0].newStatus.value='CHECK_OUT';
            document.forms[0].reviewItem.value='<c:out value="${item.id}"/>';
            document.forms[0].submit();">
            Edit
         </a>
      <c:set var="firstAction" value="false" />
  </c:if>

  <c:if test="${item.status == 'CHECKED_OUT' && can.unlockReview || item.reviewer == currentUser}" >
      <c:if test="${!firstAction}">|</c:if>
         <a href="javascript:document.forms[0].newStatus.value='WAITING';
            document.forms[0].reviewItem.value='<c:out value="${item.id}"/>';
            document.forms[0].submit();">
            Unlock
         </a>
      <c:set var="firstAction" value="false" />
  </c:if>
  <div>