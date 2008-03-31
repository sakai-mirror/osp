<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

	<script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/jquery-1.2.1.js"></script>
	<script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/thickbox.js"></script>
	<link href="/osp-common-tool/css/thickbox.css" type="text/css" rel="stylesheet" media="all" />

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
		
	function hrefLinkCell(pageId) {
	  window.location="<osp:url value="linkScaffolding.osp?page_id="/>"+pageId;
	}

	function hrefCellInfo(pageId) {
	  window.location="<osp:url value="scaffoldingCellInfo.osp?page_id="/>"+pageId;
	}
			//-->

</script>

<h3>
<fmt:message key="matrix_links_title">
<fmt:param value="${currentActivity.title}" />
</fmt:message>
</h3>

<fmt:message key="matrix_links_desc" />

<form method="get" action="<osp:url value="linkScaffolding.osp"/>">
						<osp:form/>
	<fmt:message key="site_heading" />
	<select name="selectedSite"  id="selectedSite" onchange="this.form.submit()">
		<c:forEach var="site" items="${sites}">
			<option value="<c:out value="${site.id}"/>" <c:if test="${selectedSite == site.id}"> selected="selected" </c:if>>
				<c:out value="${site.title}"/>
			</option>
		</c:forEach>
	</select>
</form>

<div class="navPanel">
	<osp:url var="listUrl" value="linkScaffolding.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>

<table width="100%">
<c:forEach var="matrixContents" items="${grids}" varStatus="gridLoopStatus">

<tr><td>
	<h4 style="display:inline">
		<%-- if there is a description and user can create, show a toggle to open description, otherwise not--%>
		<a href="#"  onclick="toggle_visibility('<c:out value="${matrixContents.scaffolding.id.value}" />')"><img  id="toggle<c:out value="${matrixContents.scaffolding.id.value}" />"  src="/library/image/sakai/expand.gif" style="padding-top:4px;width:13px" title="<fmt:message key="hideshowdesc_toggle_show"/>"></a></span>

			<img  src="/library/image/sakai/s.gif" style="width:13px" />
			<c:out value="${matrixContents.scaffolding.title}" />
		</a>
	</h4>
	<div class="instruction indnt2 textPanelFooter" id="<c:out value="${matrixContents.scaffolding.id.value}" />" style="padding:0;margin:0;display:block">
		<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
		<table cellspacing="0" width="100%" summary="<fmt:message key="table_summary_matrixScaffolding"/>">
			<tr>
				<th class="matrix-row-heading" width="10%" scope="col">
               <osp-h:glossary link="true" hover="true">
   					<c:out value="${matrixContents.scaffolding.title}"/>
               </osp-h:glossary>
				</th>
				<c:forEach var="head" items="${columnHeading}">
					<th class="matrix-column-heading matriColumnDefault" width="10%" 
                  bgcolor="<c:out value="${head.color}"/>" 
                  style="color: <c:if test="${not empty head.textColor}" ><c:out value="${head.textColor}"/></c:if>" scope="col">
                  <osp-h:glossary link="true" hover="true">
                     <c:out value="${head.description}"/>
                  </osp-h:glossary>
					</th>
				</c:forEach>
			</tr>   
			<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
				<tr>
					<th class="matrix-row-heading matriRowDefault" bgcolor="<c:out value="${rowLabel.color}"/>" 
						style="color: <c:if test="${not empty rowLabel.textColor}" ><c:out value="${rowLabel.textColor}"/></c:if>">
                  <osp-h:glossary link="true" hover="true">
                        <c:out value="${rowLabel.description}"/>
                  </osp-h:glossary>
					</th>
	    			<c:forEach var="cell" items="${matrixContents.matrixContents[loopStatus.index]}">
						<td class="matrix-cell-border matrix-<c:out value="${cell.scaffoldingCell.initialStatus}"/>">
						 <p style="position: relative; height: 100%;">
						 <input style="position: absolute; top:0px; right:0px;" type="checkbox" name="linked" value="true"
		                     <c:if test="${not empty cell.link}">checked</c:if>
		                 onclick="hrefLinkCell('<c:out value="${cell.scaffoldingCell.id}"/>') "/>
						 <center>
						 <a class="thickbox" href="<osp:url value="scaffoldingCellInfo.osp" >
						 		<osp:param name="pageId" value="${cell.scaffoldingCell.id}"/>
						 	</osp:url>">
						 	<img alt="asdf" src="/library/image/sakai/information.png" border="0"/>
						 </a>
						 </center>
						 </p>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	</div>
	</td></tr>
</c:forEach>
</table>
<!-- *********** end linkable view -->

<!-- *********** begin viewonly links -->
<table class="listHier lines nolines" cellspacing="0"  border="0" summary="<fmt:message key="list_link_summary"/>">
   <thead>
	  <tr>
		 <th scope="col"><fmt:message key="table_header_name"/></th>
	  </tr>
   </thead>
   <tbody>
	<c:forEach var="linkedCell" items="${linkedCells}">
	  <tr>
		<td style="white-space: nowrap">
			<a class="thickbox" href="<osp:url value="scaffoldingCellInfo.osp" >
			 		<osp:param name="pageId" value="${linkedCell.scaffoldingCell.id}"/>
			 	</osp:url>">
			<c:out value="${linkedCell.scaffoldingCell.title}" />
			</a>
		</td>
	</tr>
	  </c:forEach>
	</tbody>

</table>
<script type="text/javascript">
<!--
iframeId = "<c:out value="${iframeId}" />";
//-->
</script>
