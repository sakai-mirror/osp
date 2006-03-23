<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- GUID=<c:out value="${newPresentationTemplateId}"/> -->

<osp-c:authZMap prefix="osp.presentation.template." var="can" />

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addTemplate.osp"/>" title="<fmt:message key="action_new_title"/>" >
          <fmt:message key="action_new"/>
          </a>
          <a href="<osp:url value="importTemplate.osp"/>" title="<fmt:message key="action_import_title"/>" >
          <fmt:message key="action_import"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
	          <fmt:param><c:out value="${tool.title}"/></fmt:param>
		  <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message></osp:param>
                <osp:param name="name" value="presentationTemplate"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listTemplateRedirect"/>
                </osp:url>"
                title="<fmt:message key="action_permissions_title"/>" >
          <fmt:message key="action_permissions"/>
          </a>
       </c:if>
    </div>
</c:if>


<osp:url var="listUrl" value="listTemplate.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3><fmt:message key="title_listTemplate"/></h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col"><fmt:message key="table_header_name"/></th>
         <th scope="col"><fmt:message key="table_header_description"/></th>
         <th scope="col"><fmt:message key="table_header_includeHeader"/></th>
         <th scope="col"><fmt:message key="table_header_owner"/></th>
      </tr>
   </thead>
   <tbody>
  <c:forEach var="template" items="${templates}">
    <osp-c:authZMap prefix="osp.presentation.template." qualifier="${template.id}" var="isAuthorizedTo" />
    <TR>
      <TD nowrap>
         <c:out value="${template.name}" />
         <div class="itemAction">
             <c:if test="${isAuthorizedTo.copy}">
             <a href="<osp:url value="copyTemplate.osp"/>&id=<c:out value="${template.id.value}" />"><fmt:message key="table_action_copy"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.edit}">
             | <a href="<osp:url value="editTemplate.osp"/>&id=<c:out value="${template.id.value}" />"><fmt:message key="table_action_edit"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete}">
             | <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteTemplate.osp"/>&id=<c:out value="${template.id.value}" />"><fmt:message key="table_action_delete"/></a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.publish && template.published == false}">
             | <a href="<osp:url value="publishTemplate.osp"/>&id=<c:out value="${template.id.value}" />"><fmt:message key="table_action_publish"/></a>
             </c:if>
             <c:if test="${isAuthorizedTo.export}">
             | <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=presentationManager&templateId=<c:out value="${template.id.value}"/>/<c:out value="${template.name}" />.zip"><fmt:message key="table_action_export"/></a>
             </c:if>
         </div>
      </TD>
      <TD><c:out value="${template.description}" /></TD>
      <TD><c:out value="${template.includeHeaderAndFooter}" /></TD>
      <TD><c:out value="${template.owner.displayName}" /></TD>

    </TR>

  </c:forEach>
    </tbody>
  </table>
