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
<table width="100%">
   <tr>
      <td align="left">
          <h3><c:out value="${presentation.name}" /></h3>
      </td>
      <td align="right">
		  <a <c:if test="${presentation.template.includeHeaderAndFooter == false}">target="_blank" title="<fmt:message key="table_presentationManager_new_window"/>"</c:if>
					href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">
               <fmt:message key="pres_preview" />
		  </a>
	   </td>
   </tr>
</table>
 <!-- temp separation; start of tabs -->
<ul class="tabNav specialLink">

<li>
 <a href="#"><fmt:message key="pres_summary"/></a>
 </li>
 <li>
 <a href="#"><fmt:message key="pres_content"/></a>
 </li>
 <li>
 <a href="#"><fmt:message key="pres_required"/></a>
 </li>
 <li  class="selected"> 
 <span> <fmt:message key="pres_share"/></span>
 </li>
</ul>
<div class="tabNavPanel">
 <!-- temp separation; end of tabs -->

<h3>
   <fmt:message key="pres_share_this"/>
</h3>
<%--  desNote: if all the choices below are mutually exclusive an input  radio 
	group would be more appropriate if so and are changing to a radio set - please mantain the id of the input id="pres_share_select"
	--%>
<form method="post" name="mainForm">
   <div class="checkbox">
      <input type="checkbox" name="pres_share_public" id="pres_share_public"
         <c:if test="${pres_share_public=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_public">
         <fmt:message key="pres_share_public"/>
      </label>
    <%--  desNote: preview link (if this is what this is) has moved to top right
      <a href="${publicUrl}" target="_blank_"><fmt:message key="pres_share_here"/></a>--%>
   </div>
      
   <div class="checkbox">
   		<%--desNote: TODO if this input (identified via ID)  is checked, #sharePanel will show, otherwise it will hide
		 moreover - if the input is unchecked, all the values should be nulled? All people and groups unselected? probably--%>
      <input   type="checkbox" name="pres_share_select" id="pres_share_select" <%-- desNote: TODO comment for now so can display toggledisabled="disabled"--%>
         <c:if test="${pres_share_select=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_select">
         <fmt:message key="pres_share_select"/>
      </label>
   </div>
   
   <%--desNote: following panel should display on enter  under the following condition: 
   		portfolio has previously been set to "shared"
		or it has been set to shared via input above  now (this later case is scripted above) --%>
   <div class="addPanel" id="sharePanel">
   <c:choose>
     <c:when test="${empty shareList}">
        <p class="messageInstruction">
 	 	<fmt:message key="pres_share_none"/>
         <%-- OLD LINK
         <a href="<osp:url value="addPresentation.osp"/>&target=_target5&resetForm=true&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_add"/></a>
         --%>
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_add"/></a>
		</p> 
     </c:when>
     
     <c:otherwise>
         <table width="80%"   style="margin-top:1em">
         <thead>
         <tr>
         <td><h3 style="padding:0;margin:0"><fmt:message key="pres_share_list"/></h3></td>
         <td align="right">
         <%-- OLD LINK
         <a href="<osp:url value="addPresentation.osp"/>&target=_target5&resetForm=true&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_more"/></a>
         --%>
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_more"/></a>
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
         <td colspan="2" align="right">
            <a href="javascript:document.mainForm.submit();"><fmt:message key="pres_share_rem"/></a> 
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
