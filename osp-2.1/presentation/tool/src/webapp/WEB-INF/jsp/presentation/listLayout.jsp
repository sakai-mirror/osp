<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<!-- GUID=<c:out value="${newPresentationLayoutId}"/> -->

<osp-c:authZMap prefix="osp.presentation.layout." var="can" />

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addLayout.osp"/>" title="<fmt:message key="action_new_title"/>" >
          <fmt:message key="action_new"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
	              <fmt:param><c:out value="${tool.title}"/></fmt:param>
		          <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
		        </osp:param>
                <osp:param name="name" value="presentationLayout"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listLayoutRedirect"/>
                </osp:url>"
                title="<fmt:message key="action_permissions_title"/>" >
          <fmt:message key="action_permissions"/>
          </a>
       </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listLayout.osp"/>
<osp:listScroll listUrl="${listUrl}" className="chefToolBarWrap" />

<h3><fmt:message key="title_presentationLayoutManager"/></h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
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
    
             <c:if test="${isAuthorizedTo.publish && layout.published == false}">
             | <a href="<osp:url value="publishLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><fmt:message key="table_action_publish"/></a>
             </c:if>
         </div>
      </TD>
      <TD><c:out value="${layout.description}" /></TD>
      <TD><c:out value="${layout.owner.displayName}" /></TD>
      <td><c:out value="${layout.published}" /></TD>
    </TR>

  </c:forEach>
    </tbody>
  </table>
