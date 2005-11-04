<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- GUID=<c:out value="${newPresentationTemplateId}"/> -->

<osp-c:authZMap prefix="osp.presentation.template." var="can" />

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addTemplate.osp"/>" title="New..." >
          New...
          </a>
          <a href="<osp:url value="importTemplate.osp"/>" title="Import..." >
          Import...
          </a>
       </c:if>
       <c:if test="${isMaintainer}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
                <osp:param name="name" value="presentationTemplate"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listTemplateRedirect"/>
                </osp:url>"
                title="Permissions..." >
          Permissions...
          </a>
       </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listTemplate.osp"/>
<osp:listScroll listUrl="${listUrl}" className="chefToolBarWrap" />

<h3>Presentation Template Manager</h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col">Name</th>
         <th scope="col">Description</th>
         <th scope="col">Include Header</th>
         <th scope="col">Owner</th>
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
             <a href="<osp:url value="copyTemplate.osp"/>&id=<c:out value="${template.id.value}" />">Copy</a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.edit}">
             | <a href="<osp:url value="editTemplate.osp"/>&id=<c:out value="${template.id.value}" />">Edit</a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete}">
             | <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteTemplate.osp"/>&id=<c:out value="${template.id.value}" />">Delete</a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.publish && template.published == false}">
             | <a href="<osp:url value="publishTemplate.osp"/>&id=<c:out value="${template.id.value}" />">Publish</a>
             </c:if>
             <c:if test="${isAuthorizedTo.export}">
             | <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=presentationManager&templateId=<c:out value="${template.id.value}"/>/<c:out value="${template.name}" />.zip">Export</a>
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
