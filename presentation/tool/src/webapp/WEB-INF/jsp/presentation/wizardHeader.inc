<!-- <div class="intraNavTool">
      <c:if test="${!suppress_previous}">
         <span name="previous" onclick="javascript:document.forms[0].elements['<c:out value="${targetPrevious}"/>'].click()" style="cursor:pointer" title="Previous">&lt;&lt;</span>
      </c:if>
      <i><font face="Verdana, Arial, Helvetica, sans-serif" size="2"> Step <c:out value="${currentPage}"/> of <c:out value="${totalPages}"/> </font></i>
      <c:if test="${!suppress_next}">
         <span name="next" onclick="javascript:document.forms[0].elements['<c:out value="${targetNext}"/>'].click()" style="cursor:pointer" title="Next">&gt;&gt;</span>
      </c:if>
</div>-->

<p class="step">
	Step <c:out value="${currentPage}"/> of <c:out value="${totalPages}"/>
</p>