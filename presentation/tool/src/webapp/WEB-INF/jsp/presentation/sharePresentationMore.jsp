<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>
<script type="text/javascript" src="/library/js/jquery.js">
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
 <!-- temp separation; end of tabs -->

<h3>
   <fmt:message key="title_share_add"/>
</h3>

<c:if test="${isUpdated}">
	<div class="messageInformation" id="messageHolder" style="width:20em">
    	<fmt:message key="share_confirm"/>
	</div>
</c:if>
<c:if test="${not empty errMsg}">
	<div class="alertMessageInline">
     <c:out value="${errMsg}" />
	</div>
</c:if>
   
<form method="post" name="mainForm">
<input type="hidden" name="shareBy" value="${shareBy}"/>

<table>
<tr>
  <td>
     <!-- Gonzalo: this should be a pull-down list like the Actions in listPresentation.jsp, Thanks! -->
   <ul class=" inlineMenu" style="margin:0;display:block;border:none;">
   	<li id="0" class="menuOpen"><fmt:message key="share_by"/>
		<ul id="menu-0" class="makeMenuChild" style="display:none">
	   <li><a href="javascript:document.mainForm.shareBy.value='share_browse';document.mainForm.submit();"><fmt:message key="share_browse"/></a> </li>
	   <c:if test="${hasGroups}">
		  <li><a href="javascript:document.mainForm.shareBy.value='share_group';document.mainForm.submit();"><fmt:message key="share_group"/></a> </li>
	   </c:if>
	   <li><a href="javascript:document.mainForm.shareBy.value='share_search';document.mainForm.submit();"><fmt:message key="share_search"/></a> </li>
	   <c:if test="${guestEnabled}">
		  <li><a href="javascript:document.mainForm.shareBy.value='share_email';document.mainForm.submit();"><fmt:message key="share_email"/></a> </li>
	   </c:if>
	   <li><a href="javascript:document.mainForm.shareBy.value='share_role';document.mainForm.submit();"><fmt:message key="share_role"/></a> </li>
	   <li><a href="javascript:document.mainForm.shareBy.value='share_allrole';document.mainForm.submit();"><fmt:message key="share_allrole"/></a> </li>
   </ul>
   </li>
      </ul>
</td>
</tr>
</table>

<!-- select groups to to filter -->
<c:if test="${shareBy=='share_group' && hasGroups}">
<blockquote>
   <table width="auto" rules="groups">
   <thead>
   
   <tr>
   <td>
      <span class="messageInstruction"><fmt:message key="share_group_filter"/></span>
   </td>
   </thead>
         
   <%-- this is a scrollable  box of 180 px height if the content goes over 180px, if less, it will he as high as the contents--%>
   <tbody>
   <c:forEach var="group" items="${groupList}"> 
     <tr><td colspan="2">
     <div class="checkbox">
     <input type="radio" name="groups" id="groups" value="${group.id}"  
         <c:if test="${group.checked}"> checked="checked"</c:if>
         onchange="javascript:document.mainForm.submit();" />
     <label for="groups">
     <c:out value="${group.title}" />
     </label>
     </div>
     </td></tr>
   </c:forEach>
   </tbody>
   </table>
</blockquote>
</c:if>

<c:choose>

<%-- select new users or roles to share with --%>
<c:when test="${shareBy=='share_browse' || shareBy=='share_group' || shareBy=='share_role' || shareBy=='share_allrole'}">

   <c:if test="${empty availList}">
      <span class="messageInstruction">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <fmt:message key="share_no_users"/> 
      </c:if>
      <c:if test="${shareBy=='share_role'}">
      <fmt:message key="share_no_roles"/> 
      </c:if>
      <c:if test="${shareBy=='share_allrole'}">
      <fmt:message key="share_no_allroles"/> 
      </c:if>
      </span>
   </c:if>
   
   <c:if test="${not empty availList}">
   <table style="width:auto">
   <thead>
   <tr>
   <td>
      <span class="messageInstruction">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <fmt:message key="share_user_list"/> 
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
      <fmt:message key="share_role_list"/> 
      </c:if>
      </span>
   </td>
   <td style="text-align:right;padding-left:2em;white-space:nowrap">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_users"/></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_roles"/></a>
      </c:if>
   </td>
   </tr>
   </thead>
         
   <!-- Gonzalo, could this be a scrolling list of checkboxes? visible size of about 20? I don't know how to do this -->
   <%-- it will be ascrollable  box of 180 px height if the content goes over 180px, if less, it will he as high as the contents 
   	and will not have a scroillbar --%>
   <tbody>
   	<tr> 
		<td  colspan="2">
			<ul class="multSelectHolder">
   <c:forEach var="member" items="${availList}" varStatus="loopCounter">
   	<c:choose>
		<c:when test="${(loopCounter.index mod 2) == 0}">
			<li class="checkbox odd">
		</c:when>
		<c:when test="${(loopCounter.index mod 2) ==1}">
			<li class="checkbox even">
		</c:when>
	</c:choose>	
     <label for="${member.id.value}">
     	<input type="checkbox" name="${member.id.value}" id="${member.id.value}" />
     <c:out value="${member.displayName}" />
     </label>
     </li>

   </c:forEach>
     </ul>
         </td>
		 </tr>
		 </tbody>
   <tfoot>
   <tr>
   <td>
      <span class="messageInstruction"><fmt:message key="share_hint"/></span>
   </td>
   <td style="text-align:right;padding-left:2em;white-space:nowrap">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_users"/></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_roles"/></a>
      </c:if>
   </td>
   </tr>
   </tfoot>
   </table>
   </c:if>
</c:when>

<%-- enter new user or email to share with --%>
<c:when test="${shareBy=='share_search' || shareBy=='share_email'}">
<p class="longttext">
  <table style="width:auto">
  <tr>
  <td>
    <label for="share_enter_userid" style="display:block;padding:.3em">
      <c:if test="${shareBy=='share_search'}">
      <fmt:message key="share_enter_userid"/>
      </c:if>
      <c:if test="${shareBy=='share_email'}"> 
      <fmt:message key="share_enter_email"/>
      </c:if>
    </label> 
  </td>
  <td style="text-align:right;padding-left:2em;white-space:nowrap">
     <a href="javascript:document.mainForm.submit();"><fmt:message key="share_submit"/></a>
  </td>
  </tr>
  <tr><td colspan="2">
  <input type="text" name="share_user" id="share_user" size="60" />
  </td></tr>
</table>
</p> 
</c:when>

</c:choose>

   <div class="act">
      <input name="back" type="submit" value="<fmt:message key="button_return" />" class="active" accesskey="b" />
   </div>
   
</form>
</div>
