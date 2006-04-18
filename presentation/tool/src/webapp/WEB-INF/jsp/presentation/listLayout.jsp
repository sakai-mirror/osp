<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<!-- GUID=<c:out value="${newPresentationLayoutId}"/> -->

<osp-c:authZMap prefix="osp.presentation.layout." useSite="true" var="can" />

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addLayout.osp"/>" title="<fmt:message key="action_new_title"/>" >
          <fmt:message key="action_new"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer && selectableLayout != 'true'}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
	              <fmt:param><c:out value="${tool.title}"/></fmt:param>
		          <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
		        </osp:param>
                <osp:param name="name" value="presentationLayout"/>
                <osp:param name="qualifier" value="${worksite.id}"/>
                <osp:param name="returnView" value="listLayoutRedirect"/>
                </osp:url>"
                title="<fmt:message key="action_permissions_title"/>" >
          <fmt:message key="action_permissions"/>
          </a>
       </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listLayout.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3><fmt:message key="title_presentationLayoutManager"/></h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col"></th>
         <th scope="col"><fmt:message key="table_header_name"/></th>
         <th scope="col"><fmt:message key="table_header_description"/></th>
         <th scope="col"><fmt:message key="table_header_owner"/></th>
         <th scope="col"><fmt:message key="table_header_published"/></th>
      </tr>
   </thead>
   <tbody>
  <c:forEach var="layout" items="${layouts}">
    <osp-c:authZMap prefix="osp.presentation.layout." qualifier="${layout.id}" var="isAuthorizedTo" />
    <TR>
      <td>&nbsp;
         <c:if test="${selectedLayout == layout.id}">
            <img src="<osp:url value="/img/arrowhere.gif"/>" title="<fmt:message key="table_image_title" />" />
         </c:if>
      </td>
      <TD nowrap>
         <c:out value="${layout.name}" />
         <div class="itemAction">
             <c:if test="${isAuthorizedTo.edit}">
               <a href="<osp:url value="editLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><fmt:message key="table_action_edit"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete}">
             | <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><fmt:message key="table_action_delete"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.publish && layout.globalState == 0 && isGlobal}">
             | <a href="<osp:url value="publishLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><fmt:message key="table_action_publish"/></a>
             </c:if>
             
             <c:if test="${isAuthorizedTo.suggestPublish && layout.globalState == 0 && selectableLayout != 'true'}">
             | <a href="<osp:url value="publishLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />&suggest=true"><fmt:message key="table_action_suggeset_publish"/></a>
             </c:if>
             
             <c:if test="${selectableLayout == 'true' and selectedLayout != layout.id.value}">
             | <a href="<osp:url value="selectLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />&selectAction=on"><fmt:message key="table_action_select"/></a>
             </c:if>
             
             <c:if test="${selectableLayout == 'true' and selectedLayout == layout.id.value}">
             | <a href="<osp:url value="selectLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />&selectAction=off"><fmt:message key="table_action_unselect"/></a>
             </c:if>
         </div>
      </TD>
      <TD><c:out value="${layout.description}" /></TD>
      <TD><c:out value="${layout.owner.displayName}" /></TD>
      <TD><fmt:message key="layout_published_status${layout.globalState}"/></TD>
    </TR>

  </c:forEach>
    </tbody>
  </table>
  
     <div class="act">
      <c:if test="${selectableLayout == 'true'}">
         <input type="button" name="goBack" class="active" value="<fmt:message key="button_goback"/>"
            onclick="window.document.location='<osp:url value="selectLayout.osp"/>'"/>
      </c:if>
   </div>
