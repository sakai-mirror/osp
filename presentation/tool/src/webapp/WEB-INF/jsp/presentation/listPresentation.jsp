<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

   <osp-c:authZMap prefix="osp.presentation." var="can" />

<!-- GUID=<c:out value="${newPresentationId}"/> -->

<div class="navIntraTool">
    <c:if test="${can.create}">
        <a href="<osp:url value="addPresentation.osp"/>&resetForm=true"
            title="<fmt:message key="action_new_title"/>"> <fmt:message key="action_new"/> </a>
    </c:if>
    <a href="<osp:url value="myComments.osp">
               <osp:param name="sortByColumn" value="created"/>
               <osp:param name="direction" value="desc"/>
            </osp:url>"
        title="<fmt:message key="action_myComments_title"/>"> <fmt:message key="action_myComments"/> </a>
    <a href="<osp:url value="commentsForMe.osp">
               <osp:param name="sortByColumn" value="created"/>
               <osp:param name="direction" value="desc"/>
            </osp:url>"
        title="<fmt:message key="action_commentsOthers_title"/>"> <fmt:message key="action_commentsOthers"/> </a>

    <c:if test="${isMaintainer}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
	          <fmt:param><c:out value="${tool.title}"/></fmt:param>
		  <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message></osp:param>
                <osp:param name="name" value="presentation"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listPresentationRedirect"/>
                </osp:url>"
            title="<fmt:message key="action_permissions_title"/>"> <fmt:message key="action_permissions"/> </a>
    </c:if>
    
    <c:if test="${showHidden}">
		    <a href="<osp:url value="listPresentation.osp"/>&action=showHidden&showHiddenKey=false"><fmt:message key="action_remove_hidden"/></a>
	 </c:if>
	 <c:if test="${!showHidden}">
	    	<a href="<osp:url value="listPresentation.osp"/>&action=showHidden&showHiddenKey=true"><fmt:message key="action_show_all_presentations"/></a>
	 </c:if>
</div>

<c:forEach var="presentation" items="${presentations}" varStatus="presentationStatus">
   <c:set var="presentationCount" value="${presentationStatus.count}" scope="request" />
  </c:forEach>

<div class="navPanel">
	<div class="viewNav">
		<h3><fmt:message key="title_presentationManager"/></h3>
	</div>	
	<osp:url var="listUrl" value="listPresentation.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>	
<c:choose>
	<c:when  test="${empty presentations}">
		<fmt:message key="table_presentationManager_empty_list_message"/>
	</c:when>
	<c:otherwise>
		
	<table class="listHier lines nolines" cellspacing="0" cellpadding="0"  border="0" summary="<fmt:message key=" table_presentationManager_summary" />" >
	   <thead>
		  <tr>
			 <th scope="col"><fmt:message key="table_header_name"/></th>
			 <th scope="col"><fmt:message key="table_header_dateModified"/></th>
			 <th scope="col"><fmt:message key="table_header_template"/></th>
			 <th scope="col"><fmt:message key="table_header_owner"/></th>
			 <c:if test="${myworkspace}">
			   <th scope="col"><fmt:message key="table_header_worksite"/></th>
			 </c:if>
			 <th scope="col" class="attach"><fmt:message key="table_header_expired"/></th>
		  </tr>
	   </thead>
		<tbody>
	  <c:forEach var="presentation" items="${presentations}">
		<c:set var="isAuthorizedTo" value="${presentation.authz}" />
		<osp-c:authZMap prefix="osp.presentation." var="presCan" qualifier="${presentation.id}"/>
	
		<tr>
		  <td style="white-space:nowrap">
		  <h4>
		  <a <c:if test="${presentation.template.includeHeaderAndFooter == false}">target="_blank" title="
	<fmt:message key="table_presentationManager_new_window"/>"</c:if>
					href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">
			 <c:out value="${presentation.name}" />
		  </a>
		  </h4>
			 <div class="itemAction">
			 <c:set var="hasFirstAction" value="false" />
					
				 <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
						  <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="addPresentation.osp"/>&target=_target5&resetForm=true&id=<c:out value="${presentation.id.value}" />"  title="<fmt:message key="action_share"/> <c:out value="${presentation.name}" />"><fmt:message key="action_share"/></a>
				 
				 </c:if>
				 <c:if test="${isAuthorizedTo.edit}">
					 <input name="_page" type="hidden" value="1" />
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="addPresentation.osp"/>&target=_target1&resetForm=true&id=<c:out value="${presentation.id.value}" />"  title="<fmt:message key="action_edit"/> <c:out value="${presentation.name}" />"><fmt:message key="table_action_edit"/></a>
				 </c:if>
				 <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="PresentationStats.osp"/>&id=<c:out value="${presentation.id.value}" />" title="<fmt:message key="table_action_viewStats_title"/> <c:out value="${presentation.name}" />"><fmt:message key="table_action_viewStats"/></a>
				 </c:if>
	
				  <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
	
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=presentationManager&presentationId=<c:out value="${presentation.id.value}"/>/<c:out value="${presentation.name}" />.zip" title="<fmt:message key="table_action_download"/> <c:out value="${presentation.name}" />"> <fmt:message key="table_action_download"/></a>
	
	
				 </c:if>
				 
				 <c:if test="${isAuthorizedTo.delete}">
	
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a onclick="return confirmDeletion();" href="<osp:url value="deletePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />" title="<fmt:message key="table_action_delete"/> <c:out value="${presentation.name}" />"><fmt:message key="table_action_delete"/></a>
				 </c:if>
				 
					 <c:if test="${!presCan.hide}">
						
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="hidePresentation.osp"/>&hideAction=hide&id=<c:out value="${presentation.id.value}" />" title="<fmt:message key="table_action_hide"/> <c:out value="${presentation.name}" />"><fmt:message key="table_action_hide"/></a>
				 </c:if>
				 <c:if test="${presCan.hide}">
						<!-- This means it is already hidden because the "permission" is set to hide this id for this user -->
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="hidePresentation.osp"/>&hideAction=show&id=<c:out value="${presentation.id.value}" />" title="<fmt:message key="table_action_show"/> <c:out value="${presentation.name}" />"><fmt:message key="table_action_show"/></a>
				 </c:if>
			 </div>
	
		  </td>
		  <td><c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${presentation.modified}" pattern="${dateFormat}"/></td> 
		  <td><c:out value="${presentation.template.name}" /></td>
		  <td><c:out value="${presentation.owner.displayName}" /></td>
		  <c:if test="${myworkspace}">
			 <td><c:out value="${presentation.worksiteName}" /></td>
		  </c:if>
		  <td class="attach">
			 <c:if test="${presentation.expired}">
				<img alt="<fmt:message key="linktitle_presentationExpired"/>"  src="/library/image/sakai/checkon.gif" border="0"/>
			 </c:if>
		  </td>
		</tr>
	  </c:forEach>
	   </tbody>
	  </table>
	 </c:otherwise>
</c:choose> 