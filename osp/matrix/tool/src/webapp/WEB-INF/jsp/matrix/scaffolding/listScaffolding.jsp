<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%
  	String thisId = request.getParameter("panel");
  	if (thisId == null) 
  	{
    	thisId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
 		 }
%>
<script type="text/javascript">
	function resize(){
		mySetMainFrameHeight('<%= org.sakaiproject.util.Web.escapeJavascript(thisId)%>');
	}


function mySetMainFrameHeight(id)
{
	// run the script only if this window's name matches the id parameter
	// this tells us that the iframe in parent by the name of 'id' is the one who spawned us
	if (typeof window.name != "undefined" && id != window.name) return;

	var frame = parent.document.getElementById(id);
	if (frame)
	{

		var objToResize = (frame.style) ? frame.style : frame;
  
    // SAK-11014 revert           if ( false ) {

		var height; 		
		var offsetH = document.body.offsetHeight;
		var innerDocScrollH = null;

		if (typeof(frame.contentDocument) != 'undefined' || typeof(frame.contentWindow) != 'undefined')
		{
			// very special way to get the height from IE on Windows!
			// note that the above special way of testing for undefined variables is necessary for older browsers
			// (IE 5.5 Mac) to not choke on the undefined variables.
 			var innerDoc = (frame.contentDocument) ? frame.contentDocument : frame.contentWindow.document;
			innerDocScrollH = (innerDoc != null) ? innerDoc.body.scrollHeight : null;
		}
	
		if (document.all && innerDocScrollH != null)
		{
			// IE on Windows only
			height = innerDocScrollH;
		}
		else
		{
			// every other browser!
			height = offsetH;
		}
   // SAK-11014 revert		} 

   // SAK-11014 revert             var height = getFrameHeight(frame);

		// here we fudge to get a little bigger
		var newHeight = height + 40;

		// but not too big!
		if (newHeight > 32760) newHeight = 32760;

		// capture my current scroll position
		var scroll = findScroll();

		// resize parent frame (this resets the scroll as well)
		objToResize.height=newHeight + "px";

		// reset the scroll, unless it was y=0)
		if (scroll[1] > 0)
		{
			var position = findPosition(frame);
			parent.window.scrollTo(position[0]+scroll[0], position[1]+scroll[1]);
		}
	}
}

</script>


<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<c:set var="date_format">
	<osp:message key="dateFormat_list" />
</c:set>

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
        	<a href="<osp:url value="sakai.siteassociation.siteAssoc.helper/showSiteAssocs"></osp:url>"
               title="<fmt:message key="association_title"/>"><fmt:message key="action_association"/></a>
        
        
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
	<c:set var="studentView" value="${!can.publish && !can.edit && !can.delete && !can.export}" />
	<div>
	<table class="listHier lines nolines" cellspacing="0"  border="0" summary="<fmt:message key="list_matrix_summary"/>">
	   <thead>
		  <tr>
			 <th scope="col">
				<c:if test="${sortBy == 'title' && sortAscending == true }">
		 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="title"/>
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               		<fmt:message key="table_header_name"/>
	               	</a>
	               	<img src="img/sortascending.gif"/>
				</c:if>
		 		<c:if test="${sortBy == 'title' && sortAscending == false }">
		 			<a href="<osp:url value="listScaffolding.osp">
	               		<osp:param name="sort" value="title"/>
	               		<osp:param name="ascending" value="true"/>
	               		</osp:url>">
	               		<fmt:message key="table_header_name"/>	             
	               	</a>
	               	<img src="img/sortdescending.gif"/>
		 		</c:if>
		 		<c:if test="${sortBy != 'title'}">
		 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="title"/>
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               		<fmt:message key="table_header_name"/>
	               	</a>
		 		</c:if>
			 </th>
			 <c:if test="${!studentView}">
			 	<th scope="col"></th>
			 </c:if>
			 <c:if test="${!studentView}">
			 	<th scope="col">
			 		<c:if test="${sortBy == 'owner' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="owner"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<fmt:message key="table_header_owner"/>            		              			
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'owner' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="owner"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<fmt:message key="table_header_owner"/>	               		
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'owner'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="owner"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<fmt:message key="table_header_owner"/>
	               		</a>
			 		</c:if>	
			 	</th>
			 </c:if>
			 <c:if test="${!studentView}">
				<th scope="col">
					<c:if test="${sortBy == 'published' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="published"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<fmt:message key="table_header_published"/>          		              			
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'published' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="published"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<fmt:message key="table_header_published"/>               		
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'published'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="published"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<fmt:message key="table_header_published"/>
	               		</a>
			 		</c:if>	
				</th>
			 </c:if>
			 <th scope="col">
		 		<c:if test="${sortBy == 'modified' && sortAscending == true }">
			 		<a href="<osp:url value="listScaffolding.osp">
               			<osp:param name="sort" value="modified"/>             		
               			<osp:param name="ascending" value="false"/>
               			</osp:url>">
               			<fmt:message key="table_header_modified"/>          		              			
               		</a>
               		<img src="img/sortascending.gif"/>
				</c:if>
		 		<c:if test="${sortBy == 'modified' && sortAscending == false }">
		 			<a href="<osp:url value="listScaffolding.osp">
	               		<osp:param name="sort" value="modified"/>
	               		<osp:param name="ascending" value="true"/>
	               		</osp:url>">
	               		<fmt:message key="table_header_modified"/>              		
	               	</a>
	               	<img src="img/sortdescending.gif"/>
		 		</c:if> 
		 		<c:if test="${sortBy != 'modified'}">
		 			<a href="<osp:url value="listScaffolding.osp">
               			<osp:param name="sort" value="modified"/>             		
               			<osp:param name="ascending" value="true"/>
               			</osp:url>">
               			<fmt:message key="table_header_modified"/>
               		</a>
		 		</c:if>			 
			 </th>
			 <c:if test="${myworkspace}">
			 	<th scope="col">
				 	<c:if test="${sortBy == 'worksite' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="worksite"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<fmt:message key="table_header_worksite"/>       		              			
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'worksite' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="worksite"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<fmt:message key="table_header_worksite"/>              		
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'worksite'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="worksite"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<fmt:message key="table_header_worksite"/>
	               		</a>
			 		</c:if>	
			   </th>
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
						<a name="viewDesc" id="viewDesc" class="show" href="#" onclick="$(this).next('.hide').toggle();$('div.toggle${scaffold.id.value}:first', $(this).parents('div:first')).slideToggle(resize);$(this).toggle();">
							<img  id="toggle<c:out value="${scaffold.id.value}" />"  src="/library/image/sakai/expand.gif" style="padding-top:4px;width:13px" title="<fmt:message key="hideshowdesc_toggle_show"/>">
						</a>
				
			
						<a name="hideDesc" id="hideDesc" class="hide" style="display:none" href="#" onclick="$(this).prev('.show').toggle(); $('div.toggle${scaffold.id.value}:first', $(this).parents('div:first')).slideToggle(resize);$(this).toggle();">
							<img  id="toggle<c:out value="${scaffold.id.value}" />"  src="/library/image/sakai/collapse.gif" style="padding-top:4px;width:13px" title="<fmt:message key="hideshowdesc_toggle_hide"/>">
						</a>				
					</c:if>
					<c:if test="${(empty scaffold.description)}">						
							<img  src="/library/image/sakai/s.gif" style="width:13px" />					
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
				<c:if test="${!studentView}">
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
				d				<c:if test="${isMaintainer && empty scaffold.exposedPageId}">
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
				 </c:if>
				 <c:if test="${!studentView}">
				 <td>
						<c:out value="${scaffold.owner.displayName}" />
				</td>
				</c:if>
				<c:if test="${!studentView}">
				  <td>
					 <c:if test="${scaffold.published}">
						<fmt:message key="scaffolding_published_true"/>
					 </c:if>
					 <c:if test="${scaffold.preview}">
						<fmt:message key="scaffolding_published_false"/>
					 </c:if>
					 <c:if test="${!scaffold.published && !scaffold.preview}">
						<fmt:message key="scaffolding_published_false"/>
					 </c:if>
				 </td>
				 </c:if>
				 <td>
				 	<fmt:formatDate
						value="${scaffold.modifiedDate}"
						pattern="${date_format}" />
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
					<td colspan="5">					
							<div class="toggle${scaffold.id.value} instruction indnt2 textPanelFooter" style="padding:0;margin:0;display:none">
								<c:out value="${scaffold.description}" escapeXml="false"/>
							</div>													
					</td>
				</tr>
			</c:if>	
	
		  </c:forEach>
		</tbody>
	</table>
	</div>
</c:if> 
<c:if test="${(empty scaffolding)}">
	<c:if test="${can.create}">
		<fmt:message key="table_empty_list_message_create" />
	</c:if>	
	<c:if test="${!(can.create)}">
		<fmt:message key="table_empty_list_message" />
	</c:if>
</c:if>