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
<!-- Note: These tabs should be disabled, so the user doesn't accidently move away without saving changes -->

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
   <fmt:message key="title_share_add"/>
</h3>

<c:if test="${isUpdated=='true'}">
	<div class="messageInformation" id="messageHolder" style="width:20em">
    	<fmt:message key="share_confirm"/>
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
	   <c:if test="${hasGroups=='true'}">
		  <li><a href="javascript:document.mainForm.shareBy.value='share_group';document.mainForm.submit();"><fmt:message key="share_group"/></a> </li>
	   </c:if>
	   <li><a href="javascript:document.mainForm.shareBy.value='share_search';document.mainForm.submit();"><fmt:message key="share_search"/></a> </li>
	   <c:if test="${guestEnabled=='true'}">
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
<c:if test="${shareBy=='share_group' && hasGroups=='true'}">
   <fmt:message key="share_group_filter"/> 
   <table width="auto" rules="groups">
   <thead>
   <tr>
   <td>
      <h3>
      <fmt:message key="share_group_filter"/> 
      </h3>
   </td>
   </tr>
   </thead>
         
   <!-- Gonzalo, could this be a scrolling list of checkboxes? visible size of about 10? I don't know how to do this -->
   <%-- it will be a scrollable  box of 180 px height if the content goes over 180px, if less, it will he as high as the contents--%>
   <tbody>
   <c:forEach var="group" items="${groupList}"> 
     <tr><td colspan="2">
     <div class="checkbox">
     <input type="checkbox" name="${group.id}" id="${group.id}" />
     <label for="${group.id}">
     <c:out value="${group.title}" />
     </label>
     </div>
     </td></tr>
   </c:forEach>
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
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_users"/></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_roles"/></a>
      </c:if>
   </td>
   <td style="text-align:right;padding-left:2em;white-space:nowrap">
      <span class="messageInstruction">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <fmt:message key="share_user_list"/> 
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
      <fmt:message key="share_role_list"/> 
      </c:if>
      </span>
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
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_users"/></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_roles"/></a>
      </c:if>
   </td>
   <td style="text-align:right;padding-left:2em;white-space:nowrap">
      <span class="messageInstruction"><fmt:message key="share_hint"/></span>
   </td>
   </tr>
   </tfoot>
   </table>
   </c:if>
</c:when>

<%-- enter new user to share with --%>
<c:when test="${shareBy=='share_search'}">
<p  class="longttext">
<label for="share_enter_userid" style="display:block;padding:.3em"><fmt:message key="share_enter_userid"/></label> 
   <input type="text" name="share_enter_userid" id="share_enter_userid" size="60" />
   <a href="javascript:document.mainForm.submit();"><fmt:message key="share_submit"/></a>
  </p> 
</c:when>

<%-- enter new user by email to share with --%>
<c:when test="${shareBy=='share_email'}">
   <p  class="longttext">
<label for="share_enter_email" style="display:block;padding:.3em"><fmt:message key="share_enter_email"/></label> 
   <input type="text" name="share_enter_email" id="share_enter_email" size="60" />
   <a href="javascript:document.mainForm.submit();"><fmt:message key="share_submit"/></a>
</p>
   </c:when>

</c:choose>

   <div class="act">
      <input name="back" type="submit" value="<fmt:message key="button_return" />" class="active" accesskey="b" />
   </div>
   
</form>
</div>
