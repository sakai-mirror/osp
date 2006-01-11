<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />
<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="common_msgs"/>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<form name="form" method="POST" action="<osp:url value="viewCell.osp"/>">
	<osp:form/>
   <input type="hidden" name="submitAction" value="" />

	<c:set var="cell" value="${cellBean.cell}"/>
   <osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />
   <osp-c:authZMap prefix="osp.matrix." var="matrixCan" />

	<div class="navIntraTool">
		<a name="linkNew" id="linkNew" href="<osp:url value="attachToCell.osp">
			<osp:param name="cell_id" value="${cell.id}"/>
			</osp:url>">New...</a>
		<c:if test="${can.create}">
         <a name="linkManageCellStatus" id="linkManageCellStatus" href="<osp:url value="manageCellStatus.osp">
            <osp:param name="cell_id" value="${cell.id}"/>
            </osp:url>"><osp:message key="manage_cell_status" bundle="${msgs}" /></a>
      </c:if>
      <c:if test="${matrixCan.review && cell.scaffoldingCell.reviewDevice != null}">
         <a href="<osp:url value="reviewHelper.osp">
            <osp:param name="cell_id" value="${cell.id}" />
            <osp:param name="org_theospi_portfolio_review_type" value="2" />
            </osp:url>">
                  <osp:message key="review" bundle="${msgs}" /></a>
      </c:if> 
      <c:if test="${matrixCan.evaluate && cell.scaffoldingCell.evaluationDevice != null && cell.status == 'PENDING'}">
         <a href="<osp:url value="reviewHelper.osp">
            <osp:param name="cell_id" value="${cell.id}"/>
            <osp:param name="org_theospi_portfolio_review_type" value="1" />
            </osp:url>"><osp:message key="evaluate" bundle="${msgs}" /></a>
      </c:if>
	</div>


    <h3><osp:message key="view_cell" bundle="${msgs}" /></h3>
    
	<osp-h:glossary link="true" hover="true">
		<table class="itemSummary">
			<tr><th><c:out value="${cell.scaffoldingCell.scaffolding.columnLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.level.description}"/></td></tr>
			<tr><th><c:out value="${cell.scaffoldingCell.scaffolding.rowLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.rootCriterion.description}"/></td></tr>
		</table>
	</osp-h:glossary>

	<c:if test="${cell.status != 'READY'}">
		<div class="validation">
			<fmt:message key="status_warning" bundle="${msgs}">
            <fmt:param value="${cell.status}"/>
         </fmt:message>
		</div>
	</c:if>
   
   <c:if test="${not empty cell.scaffoldingCell.guidance}">
      <h4><osp:message key="guidance_header" bundle="${msgs}" /></h4>
      <c:forEach var="guidanceItem" items="${cell.scaffoldingCell.guidance.items}" varStatus="loopStatus">
         <c:if test="${guidanceItem.type == 'instruction'}">
         <p class="longtext">
            <div class="indnt2">
               <c:out value="${guidanceItem.text}" escapeXml="false" />
               <c:forEach var="guidanceItemAtt" items="${guidanceItem.attachments}" varStatus="loopStatus">
                  <a href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />" target="_new">
                     <c:out value="${guidanceItemAtt.diaplayName}" /></a>         
               </c:forEach>
            </div>
         </p>
         </c:if>
      </c:forEach>
      <a href="<osp:url value="viewCell.osp">
         <osp:param name="cell_id" value="${cell.id}"/>
         <osp:param name="guidanceAction" value="guidance"/>
      </osp:url>" title="<osp:message key="guidance_link_title" bundle="${msgs}" />">
         <osp:message key="guidance_link_text" bundle="${msgs}" /></a>
   </c:if>

    <h4>Cell Files</h4>

	<p class="instruction">Items currently associated with cell:</p>


	<table class="listHier" cellspacing="0" >
		<thead>
			<tr>
				<th scope="col">Title</th>
				<th scope="col">Size</th>
				<th scope="col">Created By</th>
				<th scope="col">Last Modified</th>
			</tr>
		</thead>
		<tbody>


			<c:set var="canReflect" value="false"/>
			<c:forEach var="node" items="${cellBean.nodes}">
				<c:set var="canReflect" value="true"/>

				<tr>
					<td>
						<a href='<c:out value="${node.externalUri}"/>' target="_blank" ><c:out value="${node.name}"/></a>
                        <!-- if status is ready -->
                        <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
                            <div class="itemAction">
                                <a href="<osp:url value="removeConfirmation.osp?&cell_id=${cell.id}&selectedArtifacts=${node.id}"/>">Remove</a>
                            </div>
                        </c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${node.technicalMetadata.size > 1024 * 1024}">
								<fmt:formatNumber value="${node.technicalMetadata.size / (1024 * 1024)}" maxFractionDigits="1"/>MB
							</c:when>
							<c:when test="${node.technicalMetadata.size > 1024}">
								<fmt:formatNumber value="${node.technicalMetadata.size / (1024)}"  maxFractionDigits="1"/>KB
							</c:when>
							<c:when test="${node.technicalMetadata.size > 0}">
								<fmt:formatNumber value="${node.technicalMetadata.size}" />
							</c:when>
						</c:choose>
					</td>
					<td>
						<c:out value="${node.technicalMetadata.owner.displayName}"/>
					</td>
					<td>
						<fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="MM-dd-yyyy" />
					</td>
				</tr>
			</c:forEach>
			<c:if test="${canReflect != 'true'}">
				<tr><td>&nbsp;&nbsp;&nbsp;There are no resource items at this location.</td></tr>
			</c:if>
		</tbody>
	</table> <!-- End the file list table -->

	<br/>
   
   <c:forEach var="cellFormDef" items="${cell.scaffoldingCell.additionalForms}">
         <h4><c:out value="${cellFormDef}" /></h4>
         <a href="<osp:url value="sakai.filepicker.helper/tool?panel=Main&session.sakaiproject.filepicker.attachLinks=true">
                        <osp:param name="session.cell_id" value="${cell.id}" />
                        </osp:url>">
                     Manage Forms</a>
         <c:forEach var="cellForm" items="${cell.cellForms}">
            <c:if test="${cellForm.formType == cellFormDef}" >
               <c:out value="${cellForm.id}" />
            </c:if>
         </c:forEach>
   </c:forEach>
   
   <c:if test="${cell.scaffoldingCell.reflectionDevice != null}">   
      <h4><osp:message key="reflection_section_header" bundle="${msgs}" /></h4>
      
      <c:if test="${empty reflections}">
         <a href="<osp:url value="reviewHelper.osp">
               <osp:param name="cell_id" value="${cell.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               </osp:url>">
                     <osp:message key="reflection_create" bundle="${msgs}" /></a>
      </c:if>
      <c:if test="${not empty reflections}">
         <c:out value="${reflections[0].title}" />
         <a href="<osp:url value="reviewHelper.osp">
               <osp:param name="cell_id" value="${cell.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               <osp:param name="current_review_id" value="${reflections[0].id}" />
               </osp:url>">
                     <osp:message key="reflection_edit" bundle="${msgs}" /></a>
      </c:if>
   </c:if>
   
	<!-- if status is ready -->
    <p class="act">
    	<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
    		<c:if test="${canReflect == 'true'}">
    		
    			<input type="submit" name="submit" class="active" value="<osp:message key="submit" bundle="${msgs}" />"/>
    		</c:if>
    	</c:if>
    	<input type="hidden" name="cell_id" value="<c:out value="${cell.id}"/>"/>
    	<input type="submit" name="matrix" value="<osp:message key="matrix" bundle="${msgs}" />"/>
    </p>
</form>
<hr/>
<c:set var="numberOfItems" value="0" />
<c:forEach var="thing" items="${cell.reviewerItems}" varStatus="loopStatus">
	<c:set var="numberOfItems" value="${loopStatus.index+1}" />
</c:forEach>

<c:if test="${cell.status != 'READY' or readOnlyMatrix == 'true'}">
	<script language="javascript">
		disableLink("linkNew");
	<!-- //TODO figure out how better way to disable Reflection and New-->
	</script>
</c:if>


<h4 style="cursor:pointer" onclick="javascript:showHideDiv('reviewDiv','/osp-jsf-resource')">
<img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgreviewDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
<osp:message key="reviews_section_header" bundle="${msgs}" /></h4>
<div id="reviewDiv">
   <c:set value="${reviews}" var="objectList" />
   <%@ include file="review_eval_table.jspf" %>
</div>
<h4 style="cursor:pointer" onclick="javascript:showHideDiv('evalDiv','/osp-jsf-resource')">
<img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgevalDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
<osp:message key="evals_section_header" bundle="${msgs}" /></h4>
<div id="evalDiv">
   <c:set value="${evaluations}" var="objectList" />
   <%@ include file="review_eval_table.jspf" %>
</div>