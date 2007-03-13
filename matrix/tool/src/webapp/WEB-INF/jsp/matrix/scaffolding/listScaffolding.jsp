<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<!-- GUID=<c:out value="${newScaffoldingId}"/> -->

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" useSite="true"/>
<osp-c:authZMap prefix="osp.matrix." var="matrixCan" useSite="true"/>

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
               <osp:param name="qualifier" value="${worksite.id}"/>
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
<table class="listHier lines nolines" cellspacing="0"  border="0" summary="<fmt:message key="list_matrix_summary"/>">
   <thead>
      <tr>
         <th scope="col"><fmt:message key="table_header_name"/></th>
         <th scope="col"><fmt:message key="table_header_owner"/></th>
         <th scope="col"><fmt:message key="table_header_published"/></th>
      </tr>
   </thead>
   <tbody>
  <c:forEach var="scaffold" items="${scaffolding}">
    <tr>
      <td style="white-space: nowrap">
         <c:if test="${(scaffold.published || scaffold.preview) && (scaffold.owner == osp_agent || can.use || matrixCan.review || matrixCan.evaluate)}">
            <a href="<osp:url value="viewMatrix.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />" title="<fmt:message key="scaffolding_link_title">
               <fmt:param><c:out value="${scaffold.title}"/></fmt:param>
               </fmt:message>">
         </c:if>
         <c:out value="${scaffold.title}" />
         <c:if test="${(scaffold.published || scaffold.preview) && (scaffold.owner == osp_agent || can.use || matrixCan.review || matrixCan.evaluate)}">
            </a>
         </c:if>
         <c:set var="hasFirstAction" value="false" />
         <div class="itemAction">
             <c:if test="${can.publish && !scaffold.preview && !scaffold.published}">
                <c:set var="hasFirstAction" value="true" />
                <a href="<osp:url value="previewScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="action_preview"/></a>
             </c:if>
				 
             <c:if test="${can.publish && !scaffold.published && scaffold.preview}">
                <c:set var="hasFirstAction" value="true" />
                <a href="<osp:url value="publishScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="action_publish"/></a>
             </c:if>
             
             <c:if test="${can.edit && !useExperimentalMatrix}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                <a href="<osp:url value="viewScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="table_action_edit"/></a>
             </c:if>
             <c:if test="${can.edit && useExperimentalMatrix}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                <a href="<osp:url value="prettyScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="table_action_edit"/></a>
             </c:if>
             
             <c:if test="${can.delete}">
                <c:if test="${hasFirstAction}" > | </c:if>
                <c:set var="hasFirstAction" value="true" />
             <a href="<osp:url value="deleteScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"><fmt:message key="table_action_delete"/></a>
             </c:if>
    
             <c:if test="${can.export}">
                <c:if test="${hasFirstAction}" > | </c:if>
                <c:set var="hasFirstAction" value="true" />
             <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=matrixManager&scaffoldingId=<c:out value="${scaffold.id.value}"/>/<c:out value="${scaffold.title}" />.zip"><fmt:message key="table_action_export"/></a>
            </c:if>
    <%--  Hiding this functionality as it hasn't gotten much testing
            <c:if test="${isMaintainer && empty scaffold.exposedPageId}">
                <c:if test="${hasFirstAction}" > | </c:if>
                <c:set var="hasFirstAction" value="true" />
                <a href="<osp:url value="exposedScaffolding.osp"/>&expose=true&scaffolding_id=<c:out value="${scaffold.id.value}"/>">
                   <fmt:message key="table_action_expose"/>
                </a>
            </c:if>
            
            <c:if test="${isMaintainer && not empty scaffold.exposedPageId}">
                <c:if test="${hasFirstAction}" > | </c:if>
                <c:set var="hasFirstAction" value="true" />
                <a href="<osp:url value="exposedScaffolding.osp"/>&expose=false&scaffolding_id=<c:out value="${scaffold.id.value}"/>">
                   <fmt:message key="table_action_unexpose"/>
                </a>
            </c:if>
       --%>     
             
         </div>
      </td>
     <td><c:out value="${scaffold.owner.displayName}" /></td>
      <td>
         <c:if test="${scaffold.published}">
            <fmt:message key="scaffolding_published_true"/>
         </c:if>
         <c:if test="${scaffold.preview}">
            <fmt:message key="scaffolding_published_preview"/>
         </c:if>
         <c:if test="${!scaffold.published && !scaffold.preview}">
            <fmt:message key="scaffolding_published_false"/>
         </c:if>
		</td>
    </tr>
	<tr class="exclude">
			
      <td colspan="4"><div class="textPanel indnt1 instruction"><c:out value="${scaffold.description}" escapeXml="false"/></div></td>
		

	</tr>

  </c:forEach>
  
    </tbody>
  </table>
