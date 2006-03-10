<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<c:if test="${not empty defaultStyleUrl}" >
   <link href="<c:out value="${defaultStyleUrl}"/>" type="text/css" rel="stylesheet" media="all" />
</c:if>
<c:if test="${not empty styleUrl}" >
   <link href="<c:out value="${styleUrl}"/>" type="text/css" rel="stylesheet" media="all" />
</c:if>

<form name="form" method="POST"
	<c:if test="${sequential == 'true'}">
      action="<osp:url value="sequentialWizardPage.osp"/>"
   </c:if>
	<c:if test="${helperPage == 'true' && !sequential == 'true'}">
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
            <osp:param name="readOnlyMatrix" value="${readOnlyMatrix}" />
            </osp:url>"><osp:message key="manage_cell_status"/></a>
      </c:if>
      <c:if test="${(matrixCan.review || wizardCan.review) && cell.scaffoldingCell.reviewDevice != null}">
         <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
            <osp:param name="org_theospi_portfolio_review_type" value="2" />
            <osp:param name="process_type_key" value="page_id" />
            </osp:url>">
                  <osp:message key="review"/></a>
      </c:if> 
      <c:if test="${(matrixCan.evaluate || wizardCan.evaluate) && cell.scaffoldingCell.evaluationDevice != null && cell.status == 'PENDING'}">
         <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
            <osp:param name="org_theospi_portfolio_review_type" value="1" />
            <osp:param name="process_type_key" value="page_id" />
            </osp:url>"><osp:message key="evaluate"/></a>
      </c:if>
	</div>


    <h3><osp:message key="${pageTitleKey}"/></h3>
    
	<osp-h:glossary link="true" hover="true">
		<table class="itemSummary">
         <tr><th><osp:message key="label_cellTitle"/>: </th><td><c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}"/></td></tr>         
         <tr><th><osp:message key="label_cellDescription"/>: </th><td><c:out value="${cell.scaffoldingCell.wizardPageDefinition.description}" escapeXml="false"/></td></tr>         
		</table>
	</osp-h:glossary>

	<c:if test="${cell.status != 'READY'}">
		<div class="validation">
			<fmt:message key="status_warning">
            <fmt:param value="${cell.status}"/>
         </fmt:message>
		</div>
	</c:if>
   
   <!-- ************* Guidance Area Start ************* -->   
   <c:if test="${not empty cell.scaffoldingCell.guidance}">
      <h4 class="xheader" style="cursor:pointer" onclick="javascript:showHideDiv('guidanceDiv','/osp-jsf-resource')">
   <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgguidanceDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
   <osp:message key="guidance_header"/></h4>
   <div id="guidanceDiv">
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
      </osp:url>" title="<osp:message key="guidance_link_title"/>">
         <osp:message key="guidance_link_text"/></a>
      </div>
   </c:if>
   <!-- ************* Guidance Area End ************* -->
   
   <!-- ************* Artifact Area Start ************* -->
   <h4 class="xheader" style="cursor:pointer" onclick="javascript:showHideDiv('cellItemDiv','/osp-jsf-resource')">
   <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgcellItemDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
   <osp:message key="title_cellItems"/>
      <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
      <a name="linkNew" id="linkNew" href="<osp:url value="attachToCell.osp">
         <osp:param name="page_id" value="${cell.wizardPage.id}"/>
         </osp:url>" onclick="javascript:stopEvents(event)"><fmt:message key="action_manageItems"/></a>
      </c:if>
   </h4>
   <div id="cellItemDiv">
	<p class="instruction"><fmt:message key="instructions_currently_associated"/></p>
   <c:set var="nodes" value="${cellBean.nodes}"/>
   <c:set var="allowedNodeType" value=""/>
   <%@ include file="cellContent.jspf" %>
	</div>
   <!-- ************* Artifact Area End ************* -->
	<br/>
   
   <!-- ************* Form Area Start ************* -->
   <c:forEach var="cellFormDef" items="${cellFormDefs}" varStatus="loopStatus">
         <h4 class="xheader" style="cursor:pointer" onclick="javascript:showHideDiv('form<c:out value="${loopStatus.index}" />Div','/osp-jsf-resource')">
   <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgform<c:out value="${loopStatus.index}" />Div" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
   <c:out value="${cellFormDef.name}" />
         <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
         <a href="<osp:url value="cellFormPicker.osp">
                        <osp:param name="page_id" value="${cell.wizardPage.id}" />
                        <osp:param name="attachFormAction" value="${cellFormDef.id}" />
                        </osp:url>" onclick="javascript:stopEvents(event)">
                     <fmt:message key="action_chooseForms"/></a> | 
         <a href="<osp:url value="cellFormPicker.osp">
                        <osp:param name="page_id" value="${cell.wizardPage.id}" />
                        <osp:param name="createFormAction" value="${cellFormDef.id}" />
                        </osp:url>" onclick="javascript:stopEvents(event)">
                     <fmt:message key="action_createForm"/></a>
</c:if>
</h4>
      <div id="form<c:out value="${loopStatus.index}" />Div">
      <c:set var="nodes" value="${cellForms}"/>
      <c:set var="allowedNodeType" value="${cellFormDef.id}"/>
      <%@ include file="cellContent.jspf" %>
      </div>
   </c:forEach>
   
   <!-- ************* Form Area End ************* -->
   
   <!-- ************* Reflection Area Start ************* -->
   <c:if test="${cell.scaffoldingCell.reflectionDevice != null}">   
      <h4 class="xheader" style="cursor:pointer" onclick="javascript:showHideDiv('reflectionDiv','/osp-jsf-resource')">
   <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgreflectionDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
   <osp:message key="reflection_section_header"/></h4>
   <div id="reflectionDiv">
      
      <c:if test="${empty reflections}">
         <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               <osp:param name="process_type_key" value="page_id" />
               </osp:url>">
                     <osp:message key="reflection_create"/></a>
      </c:if>
      <c:if test="${not empty reflections}">
         <c:set var="canReflect" value="true"/>
         <c:if test="${cell.status != 'READY' or readOnlyMatrix == 'true'}">
            <a href='<c:out value="${reflections[0].reviewContentNode.externalUri}"/>' target="_blank" ><c:out value="${reflections[0].reviewContentNode.displayName}"/></a>            
         </c:if>
         <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
         <c:out value="${reflections[0].reviewContentNode.displayName}" />
         <a href="<osp:url value="reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               <osp:param name="current_review_id" value="${reflections[0].reviewContentNode.resource.id}" />
               <osp:param name="process_type_key" value="page_id" />
               </osp:url>">
                     <osp:message key="reflection_edit"/></a>
         </c:if>
      </c:if>
   </div>
   </c:if>
   <!-- ************* Reflection Area End ************* -->
   
	<!-- if status is ready -->
    <p class="act">
    	<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
    		<c:if test="${canReflect == 'true'}">
    		
    			<input type="submit" name="submit" class="active" value="<osp:message key="button_submit"/>"/>
    		</c:if>
    	</c:if>
    	<input type="hidden" name="page_id" value="<c:out value="${cell.wizardPage.id}"/>"/>
    	<input type="submit" name="matrix" value="<osp:message key="matrix"/>"/>
    </p>
<hr/>

<!-- ************* Review Area Start ************* -->
<c:if test="${not empty reviews}">
   <h4 class="xheader" style="cursor:pointer" onclick="javascript:showHideDiv('reviewDiv','/osp-jsf-resource')">
   <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgreviewDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
   <osp:message key="reviews_section_header"/></h4>
   <div id="reviewDiv">
      <c:set value="${reviews}" var="objectList" />
      <%@ include file="review_eval_table.jspf" %>
   </div>
</c:if>
<!-- ************* Review Area End ************* -->
<!-- ************* Evaluation Area Start ************* -->
<c:if test="${not empty evaluations}">
   <h4 class="xheader" style="cursor:pointer" onclick="javascript:showHideDiv('evalDiv','/osp-jsf-resource')">
   <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgevalDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
   <osp:message key="evals_section_header"/></h4>
   <div id="evalDiv">
      <c:set value="${evaluations}" var="objectList" />
      <%@ include file="review_eval_table.jspf" %>
   </div>
</c:if>
<!-- ************* Evaluation Area End ************* -->

<c:if test="${sequential == 'true'}">
<div class="act">
    <c:if test="${currentStep != 0}">
        <input type="submit" name="_back" value="<fmt:message key="button_back"/>"/>
    </c:if>
    <c:if test="${currentStep < (totalSteps - 1)}">
        <input type="submit" name="_next" value="<fmt:message key="button_continue"/>"/>
    </c:if>
    <input type="submit" name="matrix" value="<fmt:message key="button_save"/>"/>
</div>
</c:if>

</form>
