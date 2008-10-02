<%--
	NOTE: This include file expects that there a Presentation is bound as ${presentation} and
		  that the active page should be in ${pres_active_page}, (summary, content, share)
--%>
<script type="text/javascript" src="/library/js/jquery.js">//empty</script>
<script type="text/javascript" src="/osp-presentation-tool/js/osp.editor.js">//empty</script>
<script type="text/javascript">	
osp.bag.presentationId = '<c:out value="${presentation.id.value}"/>';
$(document).ready(function() {
	osp.bag.phEdit = osp.editor('#presentationName', 'name', 'updatePresentation.osp', { id: osp.bag.presentationId });
	osp.bag.pdEdit = osp.editor('#presentationDescription', 'description', 'updatePresentation.osp', { id: osp.bag.presentationId });
});
</script>

<table width="100%" style="margin-top:1em">
	<tr>
		<td  style="vertical-align:middle;text-align:left">
			<spring:bind path="presentation.name">
				<div id="presentationName" class="shorttext">
					<span class="editableText portfolio_name"><c:out value="${status.value}"/></span>
					<input class="inlineEdit" type="text" style="display:none;margin:0;" size="40" />
					<span class="itemAction">
						<a href="#" class="editLink"><fmt:message key="edit"/></a>
						<a href="#" class="saveLink" style="display: none;"><fmt:message key="button_saveEdit"/></a>
						<a href="#" class="undoLink" style="display: none;"><fmt:message key="button_undo"/></a>
					</span>
				</div>
			</spring:bind>
		</td>
		<td   style="vertical-align:middle; text-align:right">
			<a href="<osp:url value="listPresentation.osp" />"><fmt:message key="return_to_list" /></a> | 
			<a <c:if test="${presentation.template.includeHeaderAndFooter == false}">target="_blank" title="<fmt:message key="table_presentationManager_new_window"/>"</c:if>
				href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">
				<fmt:message key="pres_preview" />
			</a>
		</td>
	</tr>
</table>

<ul class="tabNav specialLink">
	<c:choose>
		<c:when test="${pres_active_page == 'summary'}">
			<li class="selected"><span><fmt:message key="pres_summary"/></span></li>
		</c:when>
		<c:otherwise>
			<li><a href="<osp:url value="editPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_summary"/></a></li>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${pres_active_page == 'content'}">
			<li class="selected"><span><fmt:message key="pres_content"/></span></li>
		</c:when>
		<c:otherwise>
			<li><a href="<osp:url value="editContent.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_content"/></a></li>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${pres_active_page == 'share'}">
			<li class="selected"><span><fmt:message key="pres_share"/></span></li>	
		</c:when>
		<c:otherwise>
			<li><a href="<osp:url value="sharePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share"/></a></li>	
		</c:otherwise>
	</c:choose>
</ul>
