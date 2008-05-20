<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<fmt:setLocale value="${locale}" />
<fmt:setBundle basename="org.theospi.portfolio.matrix.bundle.Messages" />

<c:set var="date_format">
	<osp:message key="dateFormat_full" />
</c:set>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css"
	rel="stylesheet" media="all" />
<script type="text/javascript"
	src="/osp-jsf-resource/xheader/xheader.js"></script>

<c:forEach var="style" items="${styles}">
	<link href="<c:out value='${style}'/>" type="text/css" rel="stylesheet"
		media="all" />
</c:forEach>
<form name="form" method="post"
	<c:if test="${sequential == 'true'}">
      action="<osp:url value="sequentialWizardPage.osp"/>"
   </c:if>
	<c:if test="${helperPage == 'true' && !sequential == 'true'}">
      action="<osp:url value="wizardPage.osp"/>"
   </c:if>
	<c:if test="${helperPage != 'true'}">
      action="<osp:url value="viewCell.osp"/>"
   </c:if>>

<osp:form /> <input type="hidden" name="submitAction" value="" /> <c:if
	test="${sequential == 'true'}">
	<input type="hidden" name="view" value="sequentialWizardPage.osp" />
</c:if> <c:if test="${helperPage == 'true' && !sequential == 'true'}">
	<input type="hidden" name="view" value="wizardPage.osp" />
</c:if> <c:if test="${helperPage != 'true'}">
	<input type="hidden" name="view" value="viewCell.osp" />
</c:if> <c:set var="cell" value="${cellBean.cell}" /> <osp-c:authZMap
	prefix="osp.matrix.scaffolding." var="can"
	qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}" /> <osp-c:authZMap
	prefix="osp.matrix." var="matrixCan"
	qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}" /> <osp-c:authZMap
	prefix="osp.wizard." var="wizardCan"
	qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}" /> <%-- TODO - need to see if user gets any of these abilities, if not omit whole toolbar --%>
<div class="navIntraTool"><c:if
	test="${(isWizard != 'true' && can.create) || (isWizard == 'true' && wizardCan.create)}">
	<a name="linkManageCellStatus" id="linkManageCellStatus"
		href="<osp:url value="manageCellStatus.osp">
            <osp:param name="page_id" value="${cell.wizardPage.id}"/>
            <osp:param name="readOnlyMatrix" value="${readOnlyMatrix}" />
            <osp:param name="isWizard" value="${isWizard}" />
            <osp:param name="sequential" value="${sequential}" />
            </osp:url>"><osp:message
		key="manage_cell_status" /></a>
</c:if> <c:if test="${taggable && !(empty helperInfoList)}">
	<c:forEach var="helperInfo" items="${helperInfoList}">
		<a title="<c:out value="${helperInfo.description}"/>"
			href="javascript:document.form.submitAction.value='tagItem';document.form.providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.form.submit();">
		<c:out value="${helperInfo.name}" /> </a>
	</c:forEach>
</c:if></div>
<c:if test="${cell.scaffoldingCell.scaffolding.preview}">
	<div class="validation"><fmt:message key="title_cellPreview" /></div>
</c:if> <c:if test="${isWizard == 'true'}">
	<osp-h:glossary link="true" hover="true">
		<h3><c:out value="${wizardTitle}" /></h3>
		<div class="instruction"><c:out value="${wizardDescription}"
			escapeXml="false" /></div>
	</osp-h:glossary>

	<c:if test="${sequential == 'true'}">
		<p class="step"><fmt:message key="seq_pages_step">
			<fmt:param>
				<c:out value="${currentStep}" />
			</fmt:param>
			<fmt:param>
				<c:out value="${totalSteps}" />
			</fmt:param>
			<fmt:param>
				<c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}" />
			</fmt:param>
		</fmt:message></p>
	</c:if>

</c:if> <osp-h:glossary link="true" hover="true">
	<h3><c:if test="${isWizard == 'true' and sequential != 'true'}">
		<c:out value="${categoryTitle}" />
		<c:if test="${categoryTitle != ''}">:
          
          </c:if>
	</c:if> <c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}" />
	</h3>

	<div class="instruction"><c:out
		value="${cell.scaffoldingCell.wizardPageDefinition.description}"
		escapeXml="false" /></div>
</osp-h:glossary> <c:if test="${cell.status != 'READY'}">
	<div class="information"><fmt:message key="status_warning">
		<fmt:param>
			<fmt:message key="${cell.status}" />
		</fmt:param>
	</fmt:message></div>
</c:if> <!-- ************* Guidance Area Start, we want to keep an order ************* -->
<c:if test="${not empty cell.scaffoldingCell.guidance}">

	<c:set value="false" var="oneDisplayed" />
	<c:set value="0" var="i" />

	<!-- ** instruction ** -->

	<c:forEach var="guidanceItem"
		items="${cell.scaffoldingCell.guidance.items}">
		<c:if
			test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
			<c:if test="${guidanceItem.type == 'instruction'}">
				<h4 class="xheader"><img
					src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
					id="expandImg<c:out value='${i}'/>" alt=""
					onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
					<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
					src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
					id="collapseImg<c:out value='${i}'/>" alt=""
					onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
					<c:if test="${oneDisplayed}"> style="display:none;" </c:if> /> <osp:message
					key="instructions" /></h4>
				<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
					<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
				<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
					test="${not empty guidanceItem.attachments}">
					<ul class="attachList indnt1">
						<c:forEach var="guidanceItemAtt"
							items="${guidanceItem.attachments}">
							<li><img border="0" title="<c:out value="${hover}" />"
								alt="<c:out value="${guidanceItemAtt.displayName}"/>"
								src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
							<a
								href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
								target="_blank"> <c:out
								value="${guidanceItemAtt.displayName}" /> </a></li>
						</c:forEach>
					</ul>
				</c:if></div>
			</c:if>
		</c:if>
	</c:forEach>

	<c:set value="1" var="i" />

	<!-- ** rationale ** -->

	<c:forEach var="guidanceItem"
		items="${cell.scaffoldingCell.guidance.items}">
		<c:if
			test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
			<c:if test="${guidanceItem.type == 'rationale'}">
				<h4 class="xheader"><img
					src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
					id="expandImg<c:out value='${i}'/>" alt=""
					onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
					<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
					src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
					id="collapseImg<c:out value='${i}'/>" alt=""
					onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
					<c:if test="${oneDisplayed}"> style="display:none;" </c:if> /> <osp:message
					key="rationale" /></h4>
				<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
					<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
				<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
					test="${not empty guidanceItem.attachments}">
					<ul class="attachList indnt1">
						<c:forEach var="guidanceItemAtt"
							items="${guidanceItem.attachments}">
							<li><img border="0" title="<c:out value="${hover}" />"
								alt="<c:out value="${guidanceItemAtt.displayName}"/>"
								src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
							<a
								href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
								target="_blank"> <c:out
								value="${guidanceItemAtt.displayName}" /> </a></li>
						</c:forEach>
					</ul>
				</c:if></div>
			</c:if>
		</c:if>
	</c:forEach>
	<c:set value="2" var="i" />

	<!-- ** examples ** -->

	<c:forEach var="guidanceItem"
		items="${cell.scaffoldingCell.guidance.items}">
		<c:if
			test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
			<c:if test="${guidanceItem.type == 'example'}">
				<h4 class="xheader"><img
					src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
					id="expandImg<c:out value='${i}'/>" alt=""
					onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
					<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
					src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
					id="collapseImg<c:out value='${i}'/>" alt=""
					onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
					<c:if test="${oneDisplayed}"> style="display:none;" </c:if> /> <osp:message
					key="examples" /></h4>
				<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
					<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
				<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
					test="${not empty guidanceItem.attachments}">
					<ul class="attachList indnt1">
						<c:forEach var="guidanceItemAtt"
							items="${guidanceItem.attachments}">
							<li><img border="0" title="<c:out value="${hover}" />"
								alt="<c:out value="${guidanceItemAtt.displayName}"/>"
								src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
							<a
								href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
								target="_blank"> <c:out
								value="${guidanceItemAtt.displayName}" /> </a></li>
						</c:forEach>
					</ul>
				</c:if></div>
			</c:if>
		</c:if>
	</c:forEach>

</c:if> <!-- ************* Guidance Area End ************* --> <!-- ************* Form Area Start ************* -->

<h3><osp:message key="hdr.table.evidence" /></h3>
<table class="matrixCellList" cellpadding="0" cellspacing="0" border="0"
	summary="<osp:message key="table.evidence.summary"/>">
	<tr>
		<th colspan="2"><osp:message key="evidence_head" /></th>
		<th><osp:message key="table_header_createdBy" /></th>
		<th><osp:message key="table_header_modified" /></th>
	</tr>
	<c:forEach var="cellFormDef" items="${cellFormDefs}"
		varStatus="loopStatus">
		<tr class="cellItemAddLine">
			<td colspan="4">
			<h4><c:choose>
				<c:when
					test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
					<div class="itemAction"><a
						href="<osp:url value="osp.wizard.page.contents.helper/cellFormPicker.osp">
										<osp:param name="page_id" value="${cell.wizardPage.id}" />
										<osp:param name="createFormAction" value="${cellFormDef.id}" />
										<osp:param name="isMatrix" value="${isMatrix}" />
										<osp:param name="isWizard" value="${isWizard}" />
										<osp:param name="objectId" value="${objectId}" />
										<osp:param name="objectTitle" value="${objectTitle}" />
                              <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
										</osp:url>"
						onclick="javascript:stopEvents(event)"> <fmt:message
						key="action_createForm" /> <c:out value="${cellFormDef.name}" />
					</a> | <a
						href="<osp:url value="osp.wizard.page.contents.helper/cellFormPicker.osp">
										<osp:param name="page_id" value="${cell.wizardPage.id}" />
										<osp:param name="attachFormAction" value="${cellFormDef.id}" />
                              <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
										</osp:url>"
						onclick="javascript:stopEvents(event)"> <fmt:message
						key="action_chooseForms" /> <c:out value="${cellFormDef.name}" /></a>
					</div>
				</c:when>
				<c:otherwise>
					<c:out value="${cellFormDef.name}" />
				</c:otherwise>
			</c:choose></h4>
			</td>
		</tr>
		<c:if test="${empty cellForms}">
			<tr>
				<td colspan="4">
				<p class="instruction indnt2"><fmt:message
					key="form_section_empty">
					<fmt:param>
						<c:out value="${cellFormDef.name}" />
					</fmt:param>
				</fmt:message>
				</td>
			</tr>
		</c:if>

		<!-- ***** Filled-out Forms ***** -->

		<c:forEach var="node" items="${cellForms}" varStatus="loopStatus">

			<c:if
				test="${node.fileType == cellFormDef.id or allowedNodeType == ''}">
				<c:set var="canReflect" value="true" />

				<tr>
					<td>
					<h5><span class="indnt2"> <img border="0"
						src="/library/image/silk/application_form.gif" alt="" /> <c:if
						test="${not (cell.status == 'READY' and readOnlyMatrix != 'true')}">
						<a href='<c:out value="${node.externalUri}"/>' target="_blank">
					</c:if> <c:out value="${node.name}" /> <c:if
						test="${not (cell.status == 'READY' and readOnlyMatrix != 'true')}">
						</a>
					</c:if> </span></h5>
					</td>
					<td style="white-space: nowrap">
					<div class="itemAction"><c:if
						test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">

						<a
							href="<osp:url value="osp.wizard.page.contents.helper/cellFormPicker.osp">
                            <osp:param name="page_id" value="${cell.wizardPage.id}" />
                            <osp:param name="createFormAction" value="${cellFormDef.id}" />
                            <osp:param name="current_form_id" value="${node.resource.id}" />
                            <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
                            </osp:url>"
							title="<fmt:message key="edit"/>"> <img
							src="/library/image/silk/application_form_edit.png"
							alt="<fmt:message key="edit"/>" /> </a>
                        |
                        <a
							href="<osp:url value="osp.wizard.page.contents.helper/formDelete.osp">
                            <osp:param name="page_id" value="${cell.wizardPage.id}" />
                            <osp:param name="formDefId" value="${cellFormDef.id}" />
                            <osp:param name="current_form_id" value="${node.id}" />
                            <osp:param name="submit" value="delete" />
                            </osp:url>"
							title="<fmt:message key="delete"/>"><img
							src="/library/image/silk/application_form_delete.png"
							alt="<fmt:message key="delete"/>" /></a>
						<c:if
							test="${((isWizard != 'true' && matrixCan.review) || (isWizard == 'true' && wizardCan.review)) && cell.scaffoldingCell.reviewDevice != null}">
                        |
                        </c:if>
					</c:if> <c:if
						test="${((isWizard != 'true' && matrixCan.review) || (isWizard == 'true' && wizardCan.review)) && cell.scaffoldingCell.reviewDevice != null}">
						<a
							href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
                          <osp:param name="page_id" value="${cell.wizardPage.id}" />
                          <osp:param name="org_theospi_portfolio_review_type" value="2" />
                          <osp:param name="process_type_key" value="page_id" />
                          <osp:param name="isWizard" value="${isWizard}" />
                          <osp:param name="objectId" value="${objectId}" />
                          <osp:param name="objectTitle" value="${objectTitle}" />
                          <osp:param name="itemId" value="${node.id}" />
                          <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
                          </osp:url>"><osp:message
							key="review" /></a>
					</c:if></div>
					</td>
					<td><c:out value="${node.technicalMetadata.owner.displayName}" />
					</td>
					<td><fmt:formatDate
						value="${node.technicalMetadata.lastModified}"
						pattern="${date_format}" /></td>
				</tr>
				<!-- ************* Item-specific Review (Feedback) Area Start ************* -->
				<c:forEach var="object" items="${reviews}" varStatus="loopStatus">
					<c:if test="${object.itemId == node.id}">
						<tr>
							<td>
							<h6><span class="indnt3"> <c:if
								test="${object.itemId == node.id}">
								<img src='/library/image/silk/comment.gif' border='0' hspace='0'
									alt="" />
								<a
									href='<c:out value="${object.reviewContentNode.externalUri}"/>'
									target="_blank"> <c:out
									value="${object.reviewContentNode.displayName}" /></a>
							</c:if> </span></h6>
							</td>
							<td></td>
							<td><c:out
								value="${object.reviewContentNode.technicalMetadata.owner.displayName}" />
							</td>
							<td><fmt:formatDate
								value="${object.reviewContentNode.technicalMetadata.creation}"
								pattern="${date_format}" /></td>
						</tr>
					</c:if>
				</c:forEach>
			</c:if>
			<!-- ************* Item-specific Review (Feedback) Area End ************* -->
		</c:forEach>

	</c:forEach>
	<!-- ***** show the attached resources ***** -->

	<c:if test="${!cell.scaffoldingCell.suppressItems}">

		<tr class="cellItemAddLine">
			<td colspan="4">
			<h4><c:choose>
				<c:when
					test="${cell.status == 'READY' and readOnlyMatrix != 'true' && !cell.scaffoldingCell.suppressItems}">
					<div class="itemAction"><%-- these should be links below--%>
					<c:choose>
						<c:when test="${empty cellBean.nodes}">
							<a
								href="<osp:url value="osp.wizard.page.contents.helper/attachToCell.osp">
							<osp:param name="page_id" value="${cell.wizardPage.id}"/></osp:url>"
								onClick="javascript:stopEvents(event); document.form.method='GET';">
							<fmt:message key="action_addItems" /> </a>
						</c:when>
						<c:otherwise>
							<a
								href="<osp:url value="osp.wizard.page.contents.helper/attachToCell.osp">
							 <osp:param name="page_id" value="${cell.wizardPage.id}"/>
							 </osp:url>"
								onclick="javascript:stopEvents(event); document.form.method='GET';">
							<fmt:message key="action_manageItems" /> </a>
						</c:otherwise>
					</c:choose></div>
				</c:when>
				<c:otherwise>
					<fmt:message key="other_items_header" />
				</c:otherwise>
			</c:choose></h4>
			</td>
		</tr>
	</c:if>
	<c:if
		test="${empty cellBean.nodes &&   !cell.scaffoldingCell.suppressItems}">
		<tr>

			<td colspan="4">
			<p class="instruction indnt2"><fmt:message
				key="other_items_header_none" /></p>
			</td>
		</tr>
	</c:if>

	<c:forEach var="node" items="${cellBean.nodes}">
		<c:set var="canReflect" value="true" />

		<tr>
			<td>
			<h5><span class="indnt2"> <img border="0"
				title="<c:out value="${hover}" />"
				alt="<c:out value="${node.name}"/>"
				src="/library/image/<osp-c:contentTypeMap 
				  fileType="${node.mimeType}" mapType="image" 
				  />" />
			<a href='<c:out value="${node.externalUri}"/>' target="_blank"> <c:out
				value="${node.name}" /> </a> <span class="textPanelFooter">( <c:choose>
				<c:when test="${node.technicalMetadata.size > 1024 * 1024}">
					<fmt:formatNumber
						value="${node.technicalMetadata.size / (1024 * 1024)}"
						maxFractionDigits="1" />
					<fmt:message key="text_MB" />
				</c:when>
				<c:when test="${node.technicalMetadata.size > 1024}">
					<fmt:formatNumber value="${node.technicalMetadata.size / (1024)}"
						maxFractionDigits="1" />
					<fmt:message key="text_KB" />
				</c:when>
				<c:when test="${node.technicalMetadata.size > 0}">
					<fmt:formatNumber value="${node.technicalMetadata.size}" />
				</c:when>
			</c:choose> )</span> </span></h5>
			</td>
			<td style="white-space: nowrap">
			<div class="itemAction"><c:if
				test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
				<%--       <a name="linkNew" href="<osp:url value="attachToCell.osp">
						 <osp:param name="page_id" value="${cell.wizardPage.id}"/>
						 </osp:url>" onclick="javascript:stopEvents(event)"><fmt:message key="edit"/></a>
						 |   --%>
				<a
					href="<osp:url value="osp.wizard.page.contents.helper/resourceDelete.osp">
						   <osp:param name="page_id" value="${cell.wizardPage.id}"/>
						   <osp:param name="resource_id" value="${node.id}"/>
						   <osp:param name="submit" value="delete"/>
						   </osp:url>"
					onclick="javascript:stopEvents(event)"
					title="<fmt:message key="delete"/>"> <img
					src="/library/image/silk/page_white_delete.png"
					alt="<fmt:message key="delete"/>" /> </a>
			</c:if> <c:if
				test="${((isWizard != 'true' && matrixCan.review) || (isWizard == 'true' && wizardCan.review)) && cell.scaffoldingCell.reviewDevice != null}">
				<a
					href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
                          <osp:param name="page_id" value="${cell.wizardPage.id}" />
                          <osp:param name="org_theospi_portfolio_review_type" value="2" />
                          <osp:param name="process_type_key" value="page_id" />
                          <osp:param name="isWizard" value="${isWizard}" />
                          <osp:param name="objectId" value="${objectId}" />
                          <osp:param name="objectTitle" value="${objectTitle}" />
                          <osp:param name="itemId" value="${node.id}" />
                          <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
                          </osp:url>"><osp:message
					key="review" /></a>
			</c:if></div>
			</td>
			<td><c:out value="${node.technicalMetadata.owner.displayName}" />
			</td>
			<td><fmt:formatDate
				value="${node.technicalMetadata.lastModified}"
				pattern="${date_format}" /></td>
		</tr>

		<!-- ************* Attached Resources Review (Feedback) Area Start ************* -->
		<c:set var="feedbackHeader" value="false" />
		<c:forEach var="object" items="${reviews}" varStatus="loopStatus">
			<c:if test="${object.itemId == node.id}">

				<tr>
					<td>
					<h6><span class="indnt3"> <img
						src='/library/image/silk/comment.gif' border='0' hspace='0' alt="" />
					<a href='<c:out value="${object.reviewContentNode.externalUri}"/>'
						target="_blank"> <c:out
						value="${object.reviewContentNode.displayName}" /></a> </span></h6>
					</td>
					<td></td>
					<td><c:out
						value="${object.reviewContentNode.technicalMetadata.owner.displayName}" />
					</td>
					<td><fmt:formatDate
						value="${object.reviewContentNode.technicalMetadata.creation}"
						pattern="${date_format}" /></td>
				</tr>
			</c:if>
		</c:forEach>

		<!-- ************* Attached Resources Review (Feedback) Area End ************* -->
	</c:forEach>


	<!-- *********** Attached Assignments Area Start ******** -->
	<c:if test="${not empty assignments}">
		<tr class="cellItemAddLine">
			<td colspan="4">
			<h4><osp:message key="hdr.assignment" /></h4>
			</td>
		</tr>

		<c:forEach var="assign" items="${assignments}" varStatus="loopStatus">
			<tr>
				<td colspan="4">
				<h5 class="indnt2"><c:if test="${assign.submitted}">
					<img border="0" src="/library/image/silk/page_white_edit.png"
						alt="" />

					<a
						href="<osp:url value="viewAssign.osp">
                            <osp:param name="assign_ref" value="${assign.reference}" />
                            <osp:param name="page_id" value="${cellBean.cell.wizardPage.id}" />
                            <osp:param name="returnView" value="${returnView}" />
                            <osp:param name="isMatrix" value="${isMatrix}" />
                            <osp:param name="isWizard" value="${isWizard}" />
                            </osp:url>">
					<c:out value="${assign.assignment.title}" /> </a>
				</c:if> <c:if test="${!assign.submitted}">
					<c:out value="${assign.assignment.title}" />
				</c:if></h5>
				&nbsp;&nbsp; <span class="textPanelFooter">(<osp:message
					key="hdr.submitted" />: <c:out
					value="${assign.timeSubmittedString}" /> - <osp:message
					key="hdr.status" />: <c:out value="${assign.status}" /> <c:if
					test="${assign.gradeReleased}"> - <osp:message
						key="hdr.grade" />: <c:out value="${assign.grade}" />
				</c:if>)</span></td>
			</tr>
		</c:forEach>
	</c:if>
</table>

<!-- *********** Attached Assignments Area End ******** --> <!-- ************* Form Area End ************* -->



<!-- ************* Reflection Area Start ************* --> <c:if
	test="${cell.scaffoldingCell.reflectionDevice != null}">
	<h3><osp:message key="reflection_section_header" /></h3>
	<c:if test="${empty reflections}">
		<p class="matrixCellList"><span style="padding: .4em 2.6em;">
		<h4><span class="instruction"><osp:message
			key="reflection_section_empty" /></span></h4>
		<c:if
			test="${empty reflections && cell.status == 'READY' and readOnlyMatrix != 'true'}">
			<span class="itemAction"> <a
				href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
					   <osp:param name="page_id" value="${cell.wizardPage.id}" />
					   <osp:param name="org_theospi_portfolio_review_type" value="0" />
					   <osp:param name="process_type_key" value="page_id" />
					   <osp:param name="isMatrix" value="${isMatrix}" />
							<osp:param name="isWizard" value="${isWizard}" />
							<osp:param name="objectId" value="${objectId}" />
							<osp:param name="objectTitle" value="${objectTitle}" />
                 <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
					   </osp:url>">
			<osp:message key="reflection_create" /></a> </span>
		</c:if> </span></p>
	</c:if>
	<c:if test="${not empty reflections}">
		<div class="matrixCellList">
		<h4><span style="padding: .4em 2.6em;"> <c:set
			var="canReflect" value="true" /> <c:if
			test="${cell.status != 'READY' or readOnlyMatrix == 'true'}">

			<img src='/library/image/silk/application_form.gif' border='0'
				hspace='0' alt="" />
			<a
				href='<c:out value="${reflections[0].reviewContentNode.externalUri}"/>'
				target="_blank"> <c:out
				value="${reflections[0].reviewContentNode.displayName}" /> </a>
		</c:if> <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
			<img src='/library/image/silk/application_form.gif' border='0'
				hspace='0' alt="" />
			<c:out value="${reflections[0].reviewContentNode.displayName}" />
			<span class="itemAction"> <img
				src="/library/image/silk/application_form_edit.png"
				alt="<fmt:message key="edit"/>" /> <a
				href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
						   <osp:param name="page_id" value="${cell.wizardPage.id}" />
						   <osp:param name="org_theospi_portfolio_review_type" value="0" />
						   <osp:param name="current_review_id" value="${reflections[0].reviewContentNode.resource.id}" />
						   <osp:param name="process_type_key" value="page_id" />
                     <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
						   </osp:url>">
			<osp:message key="reflection_edit" /></a> </span>
		</c:if>
	</c:if>
	</span>
	</h4>
	</div>
</c:if> <!-- if status is ready --> <%-- TODO omit the following block if the items inside it are not rendered --%>
<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">

	<c:if
		test="${canReflect == 'true' && cell.scaffoldingCell.evaluationDevice != null}">
		<p class="act" style="margin:0">
			<c:choose>
				<c:when test="${isWizard !='true'}">
					<input type="submit" name="submit"
						value="<osp:message key='submit_cell_for_evaluation'/>"
						<c:if test="${sequential == 'true' && currentStep < (totalSteps)}">
							  onclick="document.form._next=true"
						   </c:if>
					/>
				</c:when>
				<c:otherwise>
					<input type="submit" name="submit"
						value="<osp:message key='submit_wpage_for_evaluation'/>"
						<c:if test="${sequential == 'true' && currentStep < (totalSteps)}">
							  onclick="document.form._next=true"
						   </c:if>
					/>
				</c:otherwise>
			</c:choose>	
				   
		<c:if test="${sequential == 'true'}">
			<c:if test="${currentStep < (totalSteps)}">
				<input type="hidden" name="_next" id="_next" />
			</c:if>
			<c:if test="${currentStep == (totalSteps)}">
				<input type="hidden" name="_last" id="_last" value="true" />
			</c:if>
		</c:if>
	</c:if>
	<%-- TODO: this seems very confusing - being in the last step is no indication that I am done - I may want to go through again and edit unsubmitted pages.--%>
	<c:if
		test="${canReflect == 'true' && currentStep == (totalSteps) && sequential == 'true' && evaluationItem != ''}">
		<input type="submit" name="submitWizard"
			value="<osp:message key="submit_wizard_for_evaluation"/>" />
	</c:if>
	</p>
</c:if> <input type="hidden" name="page_id"
	value="<c:out value="${cell.wizardPage.id}"/>" /> <!-- ************* Reflection Area End ************* -->


<!-- ************* General Review (Feedback) Area Start ************* -->

<c:if
	test="${(((isWizard != 'true' && matrixCan.review) || (isWizard == 'true' && wizardCan.review)) && cell.scaffoldingCell.reviewDevice != null) || not empty reviews}">
	<table class="matrixCellList" cellpadding="0" cellspacing="0"
		border="0" summary="">
		<tr>
			<th><osp:message key="reviews_section_general" /></th>
			<th>&nbsp;</th>
			<th><osp:message key="table_header_createdBy" /></th>
			<th><fmt:message key="table_header_creationDate" /></th>
		</tr>
		<tr>
			<td>
			<h5><osp:message key="reviews_section_header" /></h5>
			</td>
			<td>
			<div class="itemAction"><c:if
				test="${((isWizard != 'true' && matrixCan.review) || (isWizard == 'true' && wizardCan.review)) && cell.scaffoldingCell.reviewDevice != null}">
				<a
					href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
						<osp:param name="page_id" value="${cell.wizardPage.id}" />
						<osp:param name="org_theospi_portfolio_review_type" value="2" />
						<osp:param name="process_type_key" value="page_id" />
						<osp:param name="isWizard" value="${isWizard}" />
						<osp:param name="objectId" value="${objectId}" />
						<osp:param name="objectTitle" value="${objectTitle}" />
                  <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
						</osp:url>">
				<osp:message key="review" /></a>
			</c:if></div>
			</td>
			<td></td>
			<td></td>


		</tr>
		<c:if test="${empty reviews}">
			<tr>
				<td colspan="4"><span class="instruction indnt3"><osp:message
					key="review_section_empty" /></span></td>
			</tr>
		</c:if>
		<c:forEach var="object" items="${reviews}" varStatus="loopStatus">
			<c:if test="${empty object.itemId}">
				<tr>

					<td colspan="2">
					<h5><span class="indnt2"> <img
						src='/library/image/silk/comment.gif' border='0' hspace='0' alt="" />
					<a href='<c:out value="${object.reviewContentNode.externalUri}"/>'
						target="_blank"> <c:out
						value="${object.reviewContentNode.displayName}" /></a> </span></h5>
					</td>
					<td><c:out
						value="${object.reviewContentNode.technicalMetadata.owner.displayName}" />
					</td>
					<td><fmt:formatDate
						value="${object.reviewContentNode.technicalMetadata.creation}"
						pattern="${date_format}" /></td>
				</tr>
			</c:if>
		</c:forEach>
	</table>
</c:if> <!-- ************* General Review (Feedback) Area End ************* -->
<!-- ************* Evaluation Area Start ************* --> <c:if
	test="${(((isWizard != 'true' && matrixCan.evaluate) || (isWizard == 'true' && wizardCan.evaluate)) && cell.scaffoldingCell.evaluationDevice != null)}">
	<c:if test="${ cell.status == 'PENDING' and empty evaluations}">
		<h3><osp:message key="evals_section_header" /></h3>
		<p class="matrixCellList"><span style="padding: .4em 2.6em;">
		<h4><span class="instruction"><fmt:message
			key="evaluation_section_empty" /></span> <span class="itemAction"> <a
			href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
						   <osp:param name="page_id" value="${cell.wizardPage.id}" />
						<osp:param name="org_theospi_portfolio_review_type" value="1" />
						<osp:param name="process_type_key" value="page_id" />
					<osp:param name="isWizard" value="${isWizard}" />
					<osp:param name="objectId" value="${objectId}" />
					<osp:param name="objectTitle" value="${objectTitle}" />
               <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
						</osp:url>">
		<osp:message key="add_evaluation" /></a> </span></p>
		</h4>
	</c:if>
</c:if> <c:if test="${not empty evaluations}">
	<h3><osp:message key="evals_section_header" /></h3>
	<table class="matrixCellList" cellpadding="0" cellspacing="0"
		border="0" summary="">
		<tr>
			<th><osp:message key="eval_items_section_header" /></th>
			<th>&nbsp;</th>
			<th><osp:message key="table_header_createdBy" /></th>
			<th><fmt:message key="table_header_modified" /></th>
		</tr>
		<tr>
			<td>
			<h4><osp:message key="evals_section_header" /></h4>
			</td>
			<td>
			<div class="itemAction"><c:if
				test="${((isWizard != 'true' && matrixCan.evaluate) || (isWizard == 'true' && wizardCan.evaluate)) && cell.scaffoldingCell.evaluationDevice != null && cell.status == 'PENDING'}">
				<a
					href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
						   <osp:param name="page_id" value="${cell.wizardPage.id}" />
						<osp:param name="org_theospi_portfolio_review_type" value="1" />
						<osp:param name="process_type_key" value="page_id" />
					<osp:param name="isWizard" value="${isWizard}" />
					<osp:param name="objectId" value="${objectId}" />
					<osp:param name="objectTitle" value="${objectTitle}" />
               <osp:param name="sakai_helperSessionId" value="${cell.uniqueId}" />
						</osp:url>">
				<osp:message key="add_evaluation" /></a>
			</c:if></div>
			</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach var="object" items="${evaluations}" varStatus="loopStatus">
			<tr>
				<td colspan="2">
				<h5><span class="indnt2"> <img
					src='/library/image/silk/comments.gif' border='0' hspace='0' alt="" />
				<a href='<c:out value="${object.reviewContentNode.externalUri}"/>'
					target="_blank"> <c:out
					value="${object.reviewContentNode.displayName}" /></a> </span></h5>
				</td>
				<td><c:out
					value="${object.reviewContentNode.technicalMetadata.owner.displayName}" />
				</td>
				<td><fmt:formatDate
					value="${object.reviewContentNode.technicalMetadata.creation}"
					pattern="${date_format}" /></td>
			</tr>
		</c:forEach>
	</table>

</c:if> <!-- ************* Evaluation Area End ************* --> <c:if
	test="${taggable}">
	<%@ include file="tagLists.jspf"%>
</c:if>

<div class="act"><c:if test="${sequential == 'true'}">
	<c:if test="${currentStep < (totalSteps)}">
		<!-- this is included because evaluating a seq wizard the user can browse all the pages -->
		<input type="submit" name="_next"
			value="<fmt:message key="button_continue"/>" accesskey="s" />
	</c:if>

	<c:if test="${isEvaluation != 'true'}">
		<c:if test="${currentStep != 1}">
			<input type="submit" name="_back"
				value="<fmt:message key="button_back"/>" accesskey="b" />
		</c:if>
		<input type="submit" name="matrix"
			value="<fmt:message key="button_finish"/>" />
			
		<!-- 
	   <input type="submit" name="cancel" value="<fmt:message key="button_cancel"/>"/>
	   -->
	</c:if>
</c:if> <c:if test="${isEvaluation == 'true'}">
	<input type="submit" name="matrix" class="active"
		value="<fmt:message key="button_back_to_evaluation"/>" accesskey="x" />
</c:if> <c:if test="${sequential != 'true' && isEvaluation != 'true'}">
	<c:if test="${isWizard == 'true'}">
		<input type="submit" name="matrix" class="active"
			value="<fmt:message key="button_back_to_wizard"/>" accesskey="x" />
	</c:if>
	<c:if test="${isMatrix == 'true'}">
		<input type="submit" name="matrix" class="active"
			value="<fmt:message key="button_back_to_matrix"/>" accesskey="x" />
	</c:if>
</c:if></div>

</form>
