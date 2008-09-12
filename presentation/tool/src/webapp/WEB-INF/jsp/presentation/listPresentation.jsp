<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<osp-c:authZMap prefix="osp.presentation." var="can" />

<div class="navIntraTool">
    <c:if test="${can.create}">
        <a href="<osp:url value="addPresentation.osp"/>&resetForm=true"
            title="<fmt:message key="action_new_portfolio"/>"> <fmt:message key="action_new_portfolio"/> </a>
    </c:if>
    
    <c:if test="${isMaintainer}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
	          <fmt:param><c:out value="${tool.title}"/></fmt:param>
		  <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message></osp:param>
                <osp:param name="name" value="presentation"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listPresentationRedirect"/>
                <osp:param name="session.${lastViewKey}" value="/listPresentation.osp"/>
                </osp:url>"
            title="<fmt:message key="action_permissions_title"/>"> <fmt:message key="action_permissions"/> </a>
    </c:if>
</div>

<br/> <!-- temp separation; start of tabs -->

<div class="navIntraTool">
    <c:choose>
      <c:when test="${filterList != 'all'}">
          <a href="<osp:url value="listPresentation.osp"/>&filterListKey=all"><fmt:message key="action_filter_all"/></a>
      </c:when>
      <c:otherwise>
        <fmt:message key="action_filter_all"/>
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${filterList != 'mine'}">
          <a href="<osp:url value="listPresentation.osp"/>&filterListKey=mine"><fmt:message key="action_filter_mine"/></a>
      </c:when>
      <c:otherwise><fmt:message key="action_filter_mine"/></c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${filterList != 'shared'}">
         <a href="<osp:url value="listPresentation.osp"/>&filterListKey=shared"><fmt:message key="action_filter_shared"/></a>
      </c:when>
      <c:otherwise>
        <fmt:message key="action_filter_shared"/>
      </c:otherwise>
    </c:choose>
</div>
    
<br/> <!-- temp separation; end of tabs -->

<div class="navIntraTool">
    <fmt:message key="title_show"/>
    <c:choose>
      <c:when test="${showHidden != 'all'}">
          <a href="<osp:url value="listPresentation.osp"/>&showHiddenKey=all"><fmt:message key="action_show_all"/></a>
      </c:when>
      <c:otherwise>
        <fmt:message key="action_show_all"/>
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${showHidden != 'hidden'}">
          <a href="<osp:url value="listPresentation.osp"/>&showHiddenKey=hidden"><fmt:message key="action_show_hidden"/></a>
      </c:when>
      <c:otherwise>
        <fmt:message key="action_show_hidden"/>
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${showHidden != 'visible'}">
         <a href="<osp:url value="listPresentation.osp"/>&showHiddenKey=visible"><fmt:message key="action_show_not_hidden"/></a>
      </c:when>
      <c:otherwise>
        <fmt:message key="action_show_not_hidden"/>
      </c:otherwise>
    </c:choose>
</div>

<div class="navPanel">
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
			 <th scope="col"><fmt:message key="table_header_owner"/></th>
			 <th scope="col"><fmt:message key="table_header_dateModified"/></th>
			 <th scope="col" class="attach"><fmt:message key="table_header_status"/></th>
			 <th scope="col" class="attach"><fmt:message key="table_header_shared"/></th>
			 <th scope="col" class="attach"><fmt:message key="table_header_comments"/></th>
			 <c:if test="${myworkspace}">
			   <th scope="col"><fmt:message key="table_header_worksite"/></th>
			 </c:if>
			 <th scope="col"><fmt:message key="table_header_actions"/></th>
		  </tr>
	   </thead>
		<tbody>
	  <c:forEach var="presentationBean" items="${presentations}">
		<c:set var="presentation" value="${presentationBean.presentation}" />
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
		  </td>
        
		  <td><c:out value="${presentation.owner.displayName}" /></td>
        
		  <td><c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${presentation.modified}" pattern="${dateFormat}"/></td> 
        
		  <td align="center">
			 <c:if test="${!presentation.expired}">
				<img alt="<fmt:message key="alt_image_yes"/>"  src="/library/image/sakai/checkon.gif" border="0"/>
			 </c:if>
		  </td>
        
		  <td align="center">
			 <c:if test="${presentationBean.shared}">
				<img alt="<fmt:message key="alt_image_yes"/>"  src="/library/image/sakai/checkon.gif" border="0"/>
			 </c:if>
		  </td>
        
		  <td align="center"><c:out value="${presentationBean.commentNumAsString}" /></td>
        
		  <c:if test="${myworkspace}">
			 <td><c:out value="${presentation.worksiteName}" /></td>
		  </c:if>
        
        <!-- selection of actions/options -->
		  <td>
           <form name="form${presentation.id.value}">
           <select name="action${presentation.id.value}" onchange="location.href=form${presentation.id.value}.action${presentation.id.value}.options[selectedIndex].value">
           
             <option selected><fmt:message key="table_action_select"/></option>
             
             <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
             <option
                value="<osp:url value="addPresentation.osp"/>&target=_target5&resetForm=true&id=<c:out value="${presentation.id.value}" />"><fmt:message key="action_share"/></option>
             </c:if>
             
             <c:if test="${isAuthorizedTo.edit}">
             <option
                value="<osp:url value="addPresentation.osp"/>&target=_target1&resetForm=true&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_edit"/></option>
             </c:if>
             
             <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
             <option
                value="<osp:url value="PresentationStats.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_viewStats"/></option>
             </c:if>
   
             <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
             <option
                value="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=presentationManager&presentationId=<c:out value="${presentation.id.value}"/>/<c:out value="${presentation.name}" />.zip"><fmt:message key="table_action_download"/></option>
             </c:if>
             
             <c:if test="${isAuthorizedTo.delete}">
             <option
                value="<osp:url value="deletePresentation.osp"/>&id=<c:out value="${presentation.id.value}"/>"><fmt:message key="table_action_delete"/></option>
             </c:if>
             
             <c:if test="${!presCan.hide}">
             <option
                value="<osp:url value="hidePresentation.osp"/>&hideAction=hide&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_hide"/></option>
             </c:if>
             
             <c:if test="${presCan.hide}">
             <option
                value="<osp:url value="hidePresentation.osp"/>&hideAction=show&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_show"/></option>
             </c:if>
           </select>
           </form>
        </td>
		</tr>
	  </c:forEach>
	   </tbody>
	  </table>
	 </c:otherwise>
</c:choose> 