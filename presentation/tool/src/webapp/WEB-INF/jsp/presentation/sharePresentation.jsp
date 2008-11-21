<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>
<script type="text/javascript" src="/library/js/jquery.js">
</script>
<script type="text/javascript">
    $(document).ready(function(){
		$("#hideUrl").hide();
		$("#urlText").hide();

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

<script  type ="text/javascript">
$(document).ready(function() {
	setupMessageListener("messageHolder", "messageInformation");
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

	jQuery('body').click(function(e) { 
			
		if ( e.target.className !='menuOpen' && e.target.className !='dropdn'  ){
			$('.makeMenuChild').fadeOut();
		}
			else
			{
				if( e.target.className =='dropdn' ){
			targetId=$(e.target).parent('li').attr('id');
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);

}
				else{
			targetId=e.target.id;
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);
			}}
	});
	});

</script>

<c:set var="pres_active_page" value="share" />
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>
 
<div class="tabNavPanel">

<c:if test="${actionSave}">
	<div class="messageInformation" id="messageHolder" style="width:20em">
    	<fmt:message key="confirm_save"/>
	</div>
</c:if>
<c:if test="${actionUndo}">
	<div class="messageInformation" id="messageHolder" style="width:20em">
    	<fmt:message key="confirm_undo"/>
	</div>
</c:if>

<form method="post" name="mainForm">

<p><fmt:message key="share_when_active"/></p>
		
   <c:choose>
     <c:when test="${empty shareList}">
       <h3 style="padding:0;margin:0"><fmt:message key="pres_share_none"/></h3>
         <p><a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"  class="addUsersSmall"><span><fmt:message key="pres_share_add"/></span></a></p>
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
	
   <h3>
      <fmt:message key="pres_share_this"/>
   </h3>

   <div class="checkbox">
      <input type="checkbox" name="pres_share_public" id="pres_share_public"
         <c:if test="${pres_share_public=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_public">
         <fmt:message key="pres_share_public"/>
      </label>
       <a id="showUrl" href="#" onclick="$('#hideUrl').show(); $('#showUrl').hide(); $('#urlText').show();"><fmt:message key="pres_share_showurl"/></a>
       <a id="hideUrl" href="#" onclick="$('#showUrl').show(); $('#hideUrl').hide(); $('#urlText').hide();"><fmt:message key="pres_share_hideurl"/></a>
       <input id="urlText" type="text" readonly="true" name="publicUrl" value="${publicUrl}" size="120"/>
   </div>	
	
   <div class="act">
      <input name="save" type="submit" value="<fmt:message key="button_saveEdit" />" class="active" accesskey="s" />
      <input name="undo" type="submit" value="<fmt:message key="button_undo" />"  accesskey="x" />
   </div>
   </div> <%--end of #tabNavPanel --%>
</form>
