<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<!-- GUID=<c:out value="${newScaffoldingId}"/> -->

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />


<c:if test="${can.create || isMaintainer}">
   <div class="navIntraTool">
        <c:if test="${can.create}">
            <a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><fmt:message key="action_create"/></a>
            
            <c:if test="${empty matrixContents.scaffolding}">
                <a href="<osp:url value="importScaffolding.osp"/>" title="<fmt:message key="action_import_title"/>" >
                   <fmt:message key="action_import"/>
                </a>
            </c:if>     
        </c:if> 
        <c:if test="${isMaintainer}">
             <a href="<osp:url value="osp.permissions.helper/editPermissions">
               <osp:param name="message"><fmt:message key="action_message_setPermission">
                <fmt:param><c:out value="${tool.title}"/></fmt:param>
               <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
             </osp:param>
               <osp:param name="name" value="scaffolding"/>
               <osp:param name="qualifier" value="${tool.id}"/>
               <osp:param name="returnView" value="listScaffoldingRedirect"/>
               </osp:url>"
               title="<fmt:message key="action_permissions_title"/>" >
            <fmt:message key="action_permissions"/>
             </a>
         </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listScaffolding.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3><fmt:message key="title_matrixManager"/></h3>

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
  <c:forEach var="scaffold" items="${scaffolding}">
    <osp-c:authZMap prefix="osp.matrix.scaffolding." qualifier="${scaffold.id}" var="isAuthorizedTo" />
    <TR>
      <TD nowrap>
         <c:out value="${scaffold.title}" />
         <div class="itemAction">
             <c:if test="${scaffold.published == true}">
               <a href="<osp:url value="viewMatrix.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="table_action_view"/></a>
             </c:if>
             
             <c:if test="${scaffold.owner == osp_agent}">
              | <a href="<osp:url value="viewScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="table_action_edit"/></a>
             </c:if>
    
             <c:if test="${scaffold.owner == osp_agent && scaffold.published == false}">
             | <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="table_action_delete"/></a>
             </c:if>
    
             <c:if test="${can.publish && scaffold.owner == osp_agent && scaffold.published == false}">
             | <a href="<osp:url value="publishScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="action_publish"/></a>
             </c:if>
             
             <c:if test="${can.create}">
              | <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=matrixManager&scaffoldingId=<c:out value="${scaffold.id.value}"/>/<c:out value="${scaffold.title}" />.zip">
                   <fmt:message key="table_action_export"/>
                </a>
            </c:if>
            
            <c:if test="${isMaintainer && empty scaffold.exposedPageId}">
              | <a href="<osp:url value="exposedScaffolding.osp"/>&expose=true&scaffolding_id=<c:out value="${scaffold.id.value}"/>">
                   <fmt:message key="table_action_expose"/>
                </a>
            </c:if>
            
            <c:if test="${isMaintainer && not empty scaffold.exposedPageId}">
              | <a href="<osp:url value="exposedScaffolding.osp"/>&expose=false&scaffolding_id=<c:out value="${scaffold.id.value}"/>">
                   <fmt:message key="table_action_unexpose"/>
                </a>
            </c:if>
            
             
         </div>
      </TD>
      <TD><c:out value="${scaffold.description}" escapeXml="false"/></TD>
      <TD><c:out value="${scaffold.owner.displayName}" /></TD>
      <td><c:out value="${scaffold.published}" /></TD>
    </TR>

  </c:forEach>
  
    </tbody>
  </table>