<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<!-- GUID=<c:out value="${newPresentationStyleId}"/> -->

<osp-c:authZMap prefix="osp.style." var="can" />

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addStyle.osp"/>" title="<fmt:message key="action_new_title"/>" >
          <fmt:message key="action_new"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
                 <fmt:param><c:out value="${tool.title}"/></fmt:param>
                <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
              </osp:param>
                <osp:param name="name" value="style"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listStyleRedirect"/>
                </osp:url>"
                title="<fmt:message key="action_permissions_title"/>" >
          <fmt:message key="action_permissions"/>
          </a>
       </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listStyle.osp"/>
<osp:listScroll listUrl="${listUrl}" className="chefToolBarWrap" />

<h3><fmt:message key="title_presentationStyleManager"/></h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col"><fmt:message key="table_header_name"/></th>
         <th scope="col"><fmt:message key="table_header_description"/></th>
         <th scope="col"><fmt:message key="table_header_owner"/></th>
         <th scope="col"><fmt:message key="table_header_published"/></th>
         <th scope="col"><fmt:message key="table_header_global_published"/></th>
      </tr>
   </thead>
   <tbody>
  <c:forEach var="style" items="${styles}">
    <osp-c:authZMap prefix="osp.style." qualifier="${style.id}" var="isAuthorizedTo" />
    <TR>
      <TD nowrap>
         <c:out value="${style.name}" />
         <div class="itemAction">
             <c:if test="${isAuthorizedTo.edit}">
               <a href="<osp:url value="editstyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_edit"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete}">
             | <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_delete"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.publish && style.siteState == 0}">
             | <a href="<osp:url value="publishStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_publish"/></a>
             </c:if>
             
             <c:if test="${isAuthorizedTo.golbalPublish && style.globalState == 0}">
             | <a href="<osp:url value="globalPublishStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_global_publish"/></a>
             </c:if>
             
             <c:if test="${selectableStyle == 'true'}">
             | <a href="<osp:url value="selectStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_select"/></a>
             </c:if>
         </div>
      </TD>
      <TD><c:out value="${style.description}" /></TD>
      <TD><c:out value="${style.owner.displayName}" /></TD>
      <td><c:out value="${style.siteState}" /></TD>
      <td><c:out value="${style.globalState}" /></TD>
    </TR>

  </c:forEach>
    </tbody>
  </table>
