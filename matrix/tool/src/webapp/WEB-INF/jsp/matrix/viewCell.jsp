<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<form name="form" method="POST"
	<c:if test="${helperPage == 'true'}">
      action="<osp:url value="wizardPage.osp"/>"
   </c:if>
	<c:if test="${helperPage != 'true'}">
      action="<osp:url value="viewCell.osp"/>"
   </c:if>
   >

   <osp:form/>
   <input type="hidden" name="submitAction" value="" />

	<c:set var="cell" value="${cellBean.cell}"/>
   <osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />
   <osp-c:authZMap prefix="osp.matrix." var="matrixCan" />
   <osp-c:authZMap prefix="osp.wizard." var="wizardCan" />

	<div class="navIntraTool">
		<c:if test="${can.create}">
         <a name="linkManageCellStatus" id="linkManageCellStatus" href="<osp:url value="manageCellStatus.osp">
            <osp:param name="page_id" value="${cell.wizardPage.id}"/>
            </osp:url>"><osp:message key="manage_cell_status" bundle="${msgs}" /></a>
      </c:if>
      <c:if test="${matrixCan.review && cell.scaffoldingCell.reviewDevice != null}">
         <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
            <osp:param name="org_theospi_portfolio_review_type" value="2" />
            <osp:param name="process_type_key" value="page_id" />
            </osp:url>">
                  <osp:message key="review" bundle="${msgs}" /></a>
      </c:if> 
      <c:if test="${(matrixCan.evaluate || wizardCan.evaluate) && cell.scaffoldingCell.evaluationDevice != null && cell.status == 'PENDING'}">
         <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
            <osp:param name="org_theospi_portfolio_review_type" value="1" />
            <osp:param name="process_type_key" value="page_id" />
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
               <%--
               <c:forEach var="guidanceItemAtt" items="${guidanceItem.attachments}" varStatus="loopStatus">
                  <a href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />" target="_new">
                     <c:out value="${guidanceItemAtt.diaplayName}" /></a>         
               </c:forEach>
               --%>
            </div>
         </p>
         </c:if>
      </c:forEach>
      <a href="<osp:url value="osp.guidance.helper/view">
         <osp:param name="session.page_id" value="${cell.wizardPage.id}"/>
         <osp:param name="${CURRENT_GUIDANCE_ID_KEY}" value="${cell.scaffoldingCell.guidance.id}"/>
      </osp:url>" title="<osp:message key="guidance_link_title" bundle="${msgs}" />">
         <osp:message key="guidance_link_text" bundle="${msgs}" /></a>
   </c:if>

    <h4>Cell Items
      <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
      <a name="linkNew" id="linkNew" href="<osp:url value="attachToCell.osp">
         <osp:param name="page_id" value="${cell.wizardPage.id}"/>
         </osp:url>">Manage Cell Items...</a>
      </c:if>
    </h4>

	<p class="instruction">Items currently associated with cell:</p>
   <c:set var="nodes" value="${cellBean.nodes}"/>
   <c:set var="allowedNodeType" value=""/>
   <%@ include file="cellContent.jspf" %>
	

	<br/>
   
   <c:forEach var="cellFormDef" items="${cellFormDefs}">
         <h4><c:out value="${cellFormDef.name}" />
         <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
         <a href="<osp:url value="cellFormPicker.osp">
                        <osp:param name="page_id" value="${cell.wizardPage.id}" />
                        <osp:param name="attachFormAction" value="${cellFormDef.id}" />
                        </osp:url>">
                     Manage Forms</a>
</c:if>
</h4>
      <c:set var="nodes" value="${cellForms}"/>
      <c:set var="allowedNodeType" value="${cellFormDef.id}"/>
      <%@ include file="cellContent.jspf" %>
      </c:forEach>
   
   <c:if test="${cell.scaffoldingCell.reflectionDevice != null}">   
      <h4><osp:message key="reflection_section_header" bundle="${msgs}" /></h4>
      
      <c:if test="${empty reflections}">
         <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               </osp:url>">
                     <osp:message key="reflection_create" bundle="${msgs}" /></a>
      </c:if>
      <c:if test="${not empty reflections}">
         <c:set var="canReflect" value="true"/>
         <c:out value="${reflections[0].reviewContentNode.displayName}" />
         <c:if test="${readOnlyMatrix != 'true'}">
         <a href="<osp:url value="reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               <osp:param name="current_review_id" value="${reflections[0].reviewContentNode.resource.id}" />
               <osp:param name="process_type_key" value="page_id" />
               </osp:url>">
                     <osp:message key="reflection_edit" bundle="${msgs}" /></a>
         </c:if>
      </c:if>
   </c:if>
   
	<!-- if status is ready -->
    <p class="act">
    	<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
    		<c:if test="${canReflect == 'true'}">
    		
    			<input type="submit" name="submit" class="active" value="<osp:message key="submit" bundle="${msgs}" />"/>
    		</c:if>
    	</c:if>
    	<input type="hidden" name="page_id" value="<c:out value="${cell.wizardPage.id}"/>"/>
    	<input type="submit" name="matrix" value="<osp:message key="matrix" bundle="${msgs}" />"/>
    </p>
</form>
<hr/>

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