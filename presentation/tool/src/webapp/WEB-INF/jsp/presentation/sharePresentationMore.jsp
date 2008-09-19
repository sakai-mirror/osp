<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

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
   
<br/> <!-- temp separation; start of tabs --> 
<!-- Note: These tabs should be disabled, so the user doesn't accidently move away without saving changes -->
<div class="navIntraTool">
 [ 
 <fmt:message key="pres_summary"/>
 | 
 <fmt:message key="pres_content"/>
 | 
 <fmt:message key="pres_required"/>
 | 
 <fmt:message key="pres_share"/>
 ]
</div>
<br/> <!-- temp separation; end of tabs -->

<h3>
   <fmt:message key="title_share_add"/>
</h3>

<c:if test="${isUpdated=='true'}">
	<div class="information">
    	<fmt:message key="share_confirm"/>
	</div>
</c:if>
   
<form method="post" name="mainForm">
<input type="hidden" name="shareBy" value="${shareBy}"/>

<p>
   <!-- Gonzalo: this should be a pull-down list like the Actions in listPresentation.jsp, Thanks! -->
   <fmt:message key="share_by"/> 
   <ul>
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
</p>

<!-- select groups to to filter -->
<c:if test="${shareBy=='share_group' && hasGroups=='true'}">
   <fmt:message key="share_group_filter"/> 
   <table width="80%" rules="groups">
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
   </tbody>
   </table>
</c:if>

<c:choose>

<%-- select new users or roles to share with --%>
<c:when test="${shareBy=='share_browse' || shareBy=='share_group' || shareBy=='share_role' || shareBy=='share_allrole'}">

   <c:if test="${empty availList}">
      <i>
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <fmt:message key="share_no_users"/> 
      </c:if>
      <c:if test="${shareBy=='share_role'}">
      <fmt:message key="share_no_roles"/> 
      </c:if>
      <c:if test="${shareBy=='share_allrole'}">
      <fmt:message key="share_no_allroles"/> 
      </c:if>
      </i>
   </c:if>
   
   <c:if test="${not empty availList}">
   <table width="80%" rules="groups">
   <thead>
   <tr>
   <td align="left">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_users"/></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_roles"/></a>
      </c:if>
   </td>
   <td align="right">
      <i>
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <fmt:message key="share_user_list"/> 
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
      <fmt:message key="share_role_list"/> 
      </c:if>
      </i>
   </td>
   </tr>
   </thead>
         
   <!-- Gonzalo, could this be a scrolling list of checkboxes? visible size of about 20? I don't know how to do this -->
   <tbody>
   <c:forEach var="member" items="${availList}"> 
     <tr><td colspan="2">
     <div class="checkbox">
     <input type="checkbox" name="${member.id.value}" id="${member.id.value}" />
     <label for="${member.id.value}">
     <c:out value="${member.displayName}" />
     </label>
     </div>
     </td></tr>
   </c:forEach>
   </tbody>
         
   <tfoot>
   <tr>
   <td align="left">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_users"/></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();"><fmt:message key="share_add_roles"/></a>
      </c:if>
   </td>
   <td align="right">
      <i><fmt:message key="share_hint"/></i>
   </td>
   </tr>
   </tfoot>
   </table>
   </c:if>
</c:when>

<%-- enter new user to share with --%>
<c:when test="${shareBy=='share_search'}">
   <fmt:message key="share_enter_userid"/> 
   <input type="text" name="share_enter_userid" id="share_enter_userid" size="60" />
   <a href="javascript:document.mainForm.submit();"><fmt:message key="share_submit"/></a>
</c:when>

<%-- enter new user by email to share with --%>
<c:when test="${shareBy=='share_email'}">
   <fmt:message key="share_enter_email"/> 
   <input type="text" name="share_enter_email" id="share_enter_email" size="60" />
   <a href="javascript:document.mainForm.submit();"><fmt:message key="share_submit"/></a>
</c:when>

</c:choose>

</tr>
</table>

   <div class="act">
      <input name="back" type="submit" value="<fmt:message key="button_return" />" class="active" accesskey="b" />
   </div>
</form>
