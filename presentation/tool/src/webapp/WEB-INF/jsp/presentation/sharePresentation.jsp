<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>
<script type="text/javascript" src="/library/js/jquery.js">
</script>
<script type="text/javascript">
    $(document).ready(function(){
		if ($("#pres_share_select:checked").val())
	{
		$("#sharePanel").show();
	}
	else
	{
		$("#sharePanel").hide();
	}

	$('#pres_share_select').click(function(){
			if (this.checked) {
				$("#sharePanel").fadeIn();
				resizeFrame();
			}
			else
			{
			$("#sharePanel").fadeOut();
			resizeFrame('shrink');
			}
		});

	$(".multSelectHolder").each(function(){
		if ($(this).height() > 180) {
		$(this).addClass("oversize")
}
})

	$(".multSelectHolder input:checkbox").click( function() {
		if ($(this).attr('checked')) {
		$(this).parents("li").addClass("selected")
		}
		else
		{
		$(this).parents("li").removeClass("selected")
		}
})
					
					
});
</script>

<c:set var="pres_active_page" value="share" />
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>
 
<div class="tabNavPanel">
 <!-- temp separation; end of tabs -->

<h3>
   <fmt:message key="pres_share_this"/>
</h3>
<form method="post" name="mainForm">
   <div class="checkbox">
      <input type="checkbox" name="pres_share_public" id="pres_share_public"
         <c:if test="${pres_share_public=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_public">
         <fmt:message key="pres_share_public"/>
      </label>
      <a href="javascript:alert('${publicUrl}')"><fmt:message key="pres_share_here"/></a>
   </div>
      
   <div class="checkbox">
  		<%--desNote: if this input (identified via ID)  is checked, #sharePanel will show, otherwise it will hide --%>
      <input   type="checkbox" name="pres_share_select" id="pres_share_select"
         <c:if test="${pres_share_select=='true' && not empty shareList}">disabled="disabled"</c:if>
         <c:if test="${pres_share_select=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_select">
         <fmt:message key="pres_share_select"/>
      </label>
   </div>
   
   <%--desNote: following panel should display on enter under the following condition: 
   		portfolio has previously been set to "shared"
		or it has been set to shared via input above  now (this later case is scripted above) --%>
   <div class="addPanel" id="sharePanel">
   <c:choose>
     <c:when test="${empty shareList}">
        <p class="messageInstruction">
 	 	<fmt:message key="pres_share_none"/>
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_add"/></a>
		</p> 
     </c:when>
     
     <c:otherwise>
         <table width="80%"   style="margin-top:1em">
         <thead>
         <tr>
         <td><h3 style="padding:0;margin:0"><fmt:message key="pres_share_list"/></h3></td>
         <td align="right" class="specialLink">
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"  class="addUsersSmall"><span><fmt:message key="pres_share_more"/></span></a>
         </td>
         </tr>
         </thead>
         
<tbody>
	<tr>
		<td  colspan="2">
			<ul class="multSelectHolder">
				<c:forEach var="shareMember" items="${shareList}"  varStatus="loopCounter"> 
					<c:choose>
						<c:when test="${(loopCounter.index mod 2) == 0}">
							<li class="checkbox odd">
						</c:when>
						<c:when test="${(loopCounter.index mod 2) ==1}">
							<li class="checkbox even">
						</c:when>
					</c:choose>
					<label for="${shareMember.id.value}">
						<input type="checkbox" name="${shareMember.id.value}" id="${shareMember.id.value}" />
						<c:out value="${shareMember.displayName}" />
					</label>
					</li> 
				</c:forEach>
			</ul>
		</td>
	</tr>
</tbody>

         <tfoot>
         <tr>
         <td colspan="2" align="right" class="specialLink">
            <a href="javascript:document.mainForm.submit();"  class="removeSmall"><span><fmt:message key="pres_share_rem"/></span></a> 
         </td>
         </tr>
         </tfoot>
         </table>
     </c:otherwise>
   </c:choose>
</div> <%--end of #sharePanel --%>
   <div class="act">
      <input name="save" type="submit" value="<fmt:message key="button_saveEdit" />" class="active" accesskey="s" />
      <input name="undo" type="submit" value="<fmt:message key="button_undo" />"  accesskey="x" />
   </div>
   </div> <%--end of #tabNavPanel --%>
</form>
