<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- GUID=<c:out value="${newPresentationLayoutId}"/> -->

<osp-c:authZMap prefix="osp.presentation.layout." var="can" />

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addLayout.osp"/>" title="New..." >
          New...
          </a>
       </c:if>
       <c:if test="${isMaintainer}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
                <osp:param name="name" value="presentationLayout"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listLayoutRedirect"/>
                </osp:url>"
                title="Permissions..." >
          Permissions...
          </a>
       </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listLayout.osp"/>
<osp:listScroll listUrl="${listUrl}" className="chefToolBarWrap" />

<h3>Presentation Layout Manager</h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col">Name</th>
         <th scope="col">Description</th>
         <th scope="col">Owner</th>
         <th scope="col">Published?</th>
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
               <a href="<osp:url value="editLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />">Edit</a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete}">
             | <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />">Delete</a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.publish && layout.published == false}">
             | <a href="<osp:url value="publishLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />">Publish</a>
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
