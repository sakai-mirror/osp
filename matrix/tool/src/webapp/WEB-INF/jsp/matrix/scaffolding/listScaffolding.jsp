<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<c:if test="${!myworkspace}">
  <osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" useSite="true"/>
  <osp-c:authZMap prefix="osp.matrix." var="matrixCan" useSite="true"/>
</c:if>

<script type="text/javascript">
	<!--
		function toggle_visibility(id) {
		   var e = document.getElementById(id);
		   var elabel = document.getElementById('toggle' + id);
		   if(e.style.display == 'block')
		   {
			  e.style.display = 'none';
			  elabel.src='/library/image/sakai/expand.gif'
			  elabel.title='<fmt:message key="hideshowdesc_toggle_show"/>'
			  resizeFrame('shrink');
			}
		   else
		   {
			  e.style.display = 'block';
			  elabel.src='/library/image/sakai/collapse.gif'
			  elabel.title='<fmt:message key="hideshowdesc_toggle_hide"/>'
			  resizeFrame();
			}  
		}
		function resizeFrame(updown) {
		  var frame = parent.document.getElementById( window.name );
		  if( frame ) {
			if(updown=='shrink')
			{
			var clientH = document.body.clientHeight + 30;
		  }
		  else
		  {
		  var clientH = document.body.clientHeight + 30;
		  }
			$( frame ).height( clientH );
		  } else {
			throw( "resizeFrame did not get the frame (using name=" + window.name + ")" );
		  }
		}
			//-->

</script>


<c:if test="${!myworkspace && (can.create || isMaintainer)}">
   <div class="navIntraTool">
        <c:if test="${can.create}">
            <a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><fmt:message key="action_create"/></a> 
            <c:if test="${empty matrixContents.scaffolding}">
             <a href="<osp:url value="importScaffolding.osp"/>" title="<fmt:message key="action_import_title"/>" ><fmt:message key="action_import"/></a> 
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


<div class="navPanel">
	<div class="viewNav">
		<c:if test="${can.create}">
			<h3><fmt:message key="title_matrixManager"/></h3>
		</c:if>	
		<c:if test="${!(can.create)}">
			<h3><fmt:message key="title_matrixUser"/></h3>
		</c:if>
	</div>
	<%--//gsilver: if list is less or equal to 10 omit the pager --%>
	<osp:url var="listUrl" value="listScaffolding.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>
<c:if test="${!(empty scaffolding)}">
	<table class="listHier lines nolines" cellspacing="0"  border="0" summary="<fmt:message key="list_matrix_summary"/>">
	   <thead>
		  <tr>
			 <th scope="col"><fmt:message key="table_header_name"/></th>
			 <th scope="col"></th>
			 	<th scope="col"><fmt:message key="table_header_owner"/></th>
				<th scope="col"><fmt:message key="table_header_published"/></th>
			 <c:if test="${myworkspace}">
			   <th scope="col"><fmt:message key="table_header_worksite"/></th>
			</c:if>
		  </tr>
	   </thead>
	   <tbody>
		  <c:forEach var="scaffold" items="${scaffolding}">
			<c:if test="${myworkspace}">
			  <osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" qualifier="${scaffold.worksiteId}"/>
			  <osp-c:authZMap prefix="osp.matrix." var="matrixCan" qualifier="${scaffold.worksiteId}"/>
			</c:if>
			<tr>
				<td style="white-space: nowrap">
				<h4 style="display:inline">
					<%-- if there is a description and user can create, show a toggle to open description, otherwise not--%>
					<c:if test="${!(empty scaffold.description)}">
						<c:if test="${can.create}">
							<a href="#"  onclick="toggle_visibility('<c:out value="${scaffold.id.value}" />')"><img  id="toggle<c:out value="${scaffold.id.value}" />"  src="/library/image/sakai/expand.gif" style="padding-top:4px;width:13px" title="<fmt:message key="hideshowdesc_toggle_show"/>"></a></span>
						</c:if>	
					</c:if>
					<c:if test="${(empty scaffold.description)}">
						<c:if test="${can.create}">
							<img  src="/library/image/sakai/s.gif" style="width:13px" />
						</c:if>	
					</c:if>	
					<c:if test="${(scaffold.published || scaffold.preview) && (scaffold.owner == osp_agent || can.use || matrixCan.review || matrixCan.evaluate)}">
						<a href="<osp:url value="viewMatrix.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />" title="<fmt:message key="scaffolding_link_title">
							<fmt:param><c:out value="${scaffold.title}"/></fmt:param>
							</fmt:message>">
					</c:if>
					<c:out value="${scaffold.title}" />
					<c:if test="${(scaffold.published || scaffold.preview) && (scaffold.owner == osp_agent || can.use || matrixCan.review || matrixCan.evaluate)}">
						</a>
					</c:if>
				</h4>
				</td>
				<td>
					<c:set var="hasFirstAction" value="false" />
					<div class="itemAction">
						<c:if test="${can.publish && !scaffold.preview && !scaffold.published}">
							<c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="previewScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />" title="<fmt:message key="action_preview"/> <c:out value="${scaffold.title}" />" ><fmt:message key="action_preview"/></a>
						</c:if>
						 <c:if test="${can.publish && !scaffold.published && scaffold.preview}">
							<c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="publishScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"  title="<fmt:message key="action_publish"/> <c:out value="${scaffold.title}" />"><fmt:message key="action_publish"/></a>
						 </c:if>
						 <c:if test="${can.edit && !useExperimentalMatrix}">
							 <c:if test="${hasFirstAction}" > | </c:if>
							 <c:set var="hasFirstAction" value="true" />
							 <a href="<osp:url value="viewScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />" title="<fmt:message key="table_action_edit"/> <c:out value="${scaffold.title}" />"><fmt:message key="table_action_edit"/></a>
						 </c:if>
						 <c:if test="${can.edit && useExperimentalMatrix}">
							 <c:if test="${hasFirstAction}" > | </c:if>
							 <c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="prettyScaffolding.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />" title="<fmt:message key="table_action_edit"/> <c:out value="${scaffold.title}" />"><fmt:message key="table_action_edit"/></a>
						 </c:if>
						 <c:if test="${can.delete}">
							<c:if test="${hasFirstAction}" > | </c:if>
							<c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="deleteScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${scaffold.id.value}" />"  title="<fmt:message key="table_action_delete"/> <c:out value="${scaffold.title}" />"><fmt:message key="table_action_delete"/></a>
						 </c:if>
				
						 <c:if test="${can.export}">
							<c:if test="${hasFirstAction}" > | </c:if>
							<c:set var="hasFirstAction" value="true" />
							 <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=matrixManager&scaffoldingId=<c:out value="${scaffold.id.value}"/>/<c:out value="${scaffold.title}" />.zip" title="<fmt:message key="table_action_export"/> <c:out value="${scaffold.title}" />"><fmt:message key="table_action_export"/></a>
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
				 <td>
						<c:out value="${scaffold.owner.displayName}" />
				</td>
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
				<c:if test="${myworkspace}">
				 <td>
					<c:out value="${scaffold.worksiteName}" />
				 </td>
				</c:if>
			</tr>
			<%-- if there is a description and user can create,  description visibility can be toggled, if user cannot create, just show--%>
			<c:if test="${!(empty scaffold.description)}">
				<tr class="exclude">
					<td colspan="4">
						<c:if test="${can.create}">
							<div class="instruction indnt2 textPanelFooter" id="<c:out value="${scaffold.id.value}" />" style="padding:0;margin:0;display:none">
								<c:out value="${scaffold.description}" escapeXml="false"/>
							</div>
						</c:if>	
						<c:if test="${!(can.create)}">
							<div class="instruction textPanelFooter indnt3">
								<c:out value="${scaffold.description}" escapeXml="false"/>
							</div>			
						</c:if>
					</td>
				</tr>
			</c:if>	
		
		  </c:forEach>
		</tbody>
	</table>
</c:if> 
<c:if test="${(empty scaffolding)}">
	<c:if test="${can.create}">
		<fmt:message key="table_empty_list_message_create" />
	</c:if>	
	<c:if test="${!(can.create)}">
		<fmt:message key="table_empty_list_message" />
	</c:if>
</c:if>