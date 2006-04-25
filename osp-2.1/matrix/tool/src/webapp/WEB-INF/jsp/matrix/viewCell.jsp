<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<c:set var="date_format"><osp:message key="dateFormat_full"/></c:set>

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
	</div>


	<c:if test="${isWizard == 'true'}">
	   <osp-h:glossary link="true" hover="true">
       <h3><c:out value="${wizardTitle}"/></h3>
    
       <div class="instruction">
          <c:out value="${wizardDescription}" escapeXml="false" />
       </div>
	   </osp-h:glossary>
	</c:if>
	<c:if test="${isWizard != 'true'}">
	   <osp-h:glossary link="true" hover="true">
       <h3><c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}"/></h3>
    
       <div class="instruction">
          <c:out value="${cell.scaffoldingCell.wizardPageDefinition.description}" escapeXml="false" />
       </div>
	   </osp-h:glossary>
	</c:if>
    
	
	
       <c:if test="${sequential != 'true' && isWizard == 'true'}">
          <h3><c:out value="${categoryTitle}" /><c:if test="${categoryTitle != ''}">:
          
          </c:if>
          
          <c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}" /></h3>
       </c:if>
       <c:if test="${sequential == 'true' && isWizard == 'true'}">
    <p class="step">
       <fmt:message key="seq_pages_step">
	      <fmt:param><c:out value="${currentStep}" /></fmt:param>
          <fmt:param><c:out value="${totalSteps}" /></fmt:param>
          <fmt:param><c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}" /></fmt:param>
	   </fmt:message>
    </p>
       </c:if>
    
    <div class="instruction">
       <c:out value="${cell.scaffoldingCell.wizardPageDefinition.description}" escapeXml="false" />
    </div>
    
	<c:if test="${cell.status != 'READY'}">
		<div class="validation">
		   <fmt:message key="status_warning">
            <fmt:param value="${cell.status}"/>
           </fmt:message>
		</div>
	</c:if>
   
   <!-- ************* Guidance Area Start, we want to keep an order ************* -->   
   <c:if test="${not empty cell.scaffoldingCell.guidance}">
      <table class="listHier lines">
		<tr>
			<th><osp:message key="guidance_header"/></th>
		</tr>
	  </table>
      
      
      <div class="instruction">
         <osp:message key="guidance_instructions"/>
      </div>
      
      
      <div id="guidanceDiv" class="indnt1">
   		<!-- ** instruction ** -->
      <c:forEach var="guidanceItem" items="${cell.scaffoldingCell.guidance.items}" varStatus="loopStatus">
         <c:if test="${guidanceItem.type == 'instruction'}">
         <b>
            <osp:message key="instructions"/>
         </b>
         
         <p class="longtext">
            <div class="indnt2">
               <c:out value="${guidanceItem.text}" escapeXml="false" />
               
               <c:forEach var="guidanceItemAtt" items="${guidanceItem.attachments}" varStatus="loopStatus">
                  <br/><a href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />" target="_new">
                     
                     <img border="0" title="<c:out value="${hover}" />"
                                  alt="<c:out value="${guidanceItemAtt.displayName}"/>" 
                                  src="/library/image/<osp-c:contentTypeMap 
                                  fileType="${guidanceItemAtt.mimeType}" mapType="image" 
                                  />"/>
                     
                     <c:out value="${guidanceItemAtt.displayName}" /></a>         
               </c:forEach>
               
            </div>
         </p>
         <br/><br />
         </c:if>
      </c:forEach>
      
      
   		<!-- ** rationale ** -->
      <c:forEach var="guidanceItem" items="${cell.scaffoldingCell.guidance.items}" varStatus="loopStatus">
         <c:if test="${guidanceItem.type == 'rationale'}">
         <b>
            <osp:message key="raiontale"/>
         </b>
         
         <p class="longtext">
            <div class="indnt2">
               
      <a href="<osp:url value="osp.guidance.helper/view">
         <osp:param name="session.page_id" value="${cell.wizardPage.id}"/>
         <osp:param name="${CURRENT_GUIDANCE_ID_KEY}" value="${cell.scaffoldingCell.guidance.id}"/>
      </osp:url>" title="<osp:message key="guidance_link_title"/>">
         <osp:message key="viewRationale"/></a>
               
            </div>
         </p>
         <br/><br />
         </c:if> <!--
	         <a href="<osp:url value="osp.guidance.helper/view">
	         <osp:param name="session.page_id" value="${cell.wizardPage.id}"/>
	         <osp:param name="${CURRENT_GUIDANCE_ID_KEY}" value="${cell.scaffoldingCell.guidance.id}"/>
	      </osp:url>" title="<osp:message key="guidance_link_title"/>">
	         <osp:message key="guidance_link_text"/></a>
         -->
      </c:forEach>
      
      
   		<!-- ** example ** -->
      <c:forEach var="guidanceItem" items="${cell.scaffoldingCell.guidance.items}" varStatus="loopStatus">
         <c:if test="${guidanceItem.type == 'example'}">
         <b>
            <osp:message key="examples"/>
         </b>
         
         <p class="longtext">
            <div class="indnt2">
               
      <a href="<osp:url value="osp.guidance.helper/view">
         <osp:param name="session.page_id" value="${cell.wizardPage.id}"/>
         <osp:param name="${CURRENT_GUIDANCE_ID_KEY}" value="${cell.scaffoldingCell.guidance.id}"/>
         <osp:param name="org.theospi.portfolio.guidance.exampleFlag" value="true"/>
         <osp:param name="org.theospi.portfolio.guidance.rationaleFlag" value="false"/>
         <osp:param name="org.theospi.portfolio.guidance.instructionFlag" value="false"/>
      </osp:url>" title="<osp:message key="guidance_link_title"/>">
         <osp:message key="viewExamples"/></a>
               
            </div>
         </p>
         <br/><br />
         </c:if>
      </c:forEach>
      </div>
      
   </c:if>
   <!-- ************* Guidance Area End ************* -->
   
   <!-- ************* Form Area Start ************* -->
   <table class="listHier lines">
		<tr>
			<th colspan="2"><osp:message key="items"/></th>
			<th><osp:message key="table_header_owner"/></th>
			<th><osp:message key="table_header_modified"/></th>
		</tr>
	   <c:forEach var="cellFormDef" items="${cellFormDefs}" varStatus="loopStatus">
			<tr>
				<td colspan="2">
					<c:out value="${cellFormDef.name}" />
					<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
						<div class="itemAction indnt2">
						
							 <a href="<osp:url value="cellFormPicker.osp">
										<osp:param name="page_id" value="${cell.wizardPage.id}" />
										<osp:param name="createFormAction" value="${cellFormDef.id}" />
										</osp:url>" onclick="javascript:stopEvents(event)">
									 <fmt:message key="action_createForm"/></a>
							 | 
							 <a href="<osp:url value="cellFormPicker.osp">
										<osp:param name="page_id" value="${cell.wizardPage.id}" />
										<osp:param name="attachFormAction" value="${cellFormDef.id}" />
										</osp:url>" onclick="javascript:stopEvents(event)">
									 <fmt:message key="action_chooseForms"/></a>
						</div>
					</c:if>
				</td>
				<td><%-- <c:out value="${cellFormDef.owner}" /> --%></td>
				<td><%-- <fmt:formatDate value="${cellFormDef.modifiedDate}" type="date" pattern="${date_format}" /> --%></td>
			</tr>
	      <!-- ***** the filled out forms ***** -->
	      <c:forEach var="node" items="${cellForms}" varStatus="loopStatus">
	      
            <c:if test="${node.fileType == cellFormDef.id or allowedNodeType == ''}" >
            <c:set var="canReflect" value="true"/>

            <tr>
               <td colspan="2">
				  <div class="indnt2">
               
                     
                                  
                  <c:if test="${not (cell.status == 'READY' and readOnlyMatrix != 'true')}">
                  <a href='<c:out value="${node.externalUri}"/>' target="_blank" >
                  </c:if>
                     <img border="0" src="/library/image/sakai/generic.gif"/><c:out value="${node.name}"/>
                  <c:if test="${not (cell.status == 'READY' and readOnlyMatrix != 'true')}">
                  </a>
                  </c:if>
                  <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
                      <div class="itemAction indent2">
                          &nbsp; &nbsp; &nbsp;<a href="<osp:url value="cellFormPicker.osp">
                              <osp:param name="page_id" value="${cell.wizardPage.id}" />
                              <osp:param name="createFormAction" value="${cellFormDef.id}" />
                              <osp:param name="current_form_id" value="${node.resource.id}" />
                              </osp:url>"><fmt:message key="edit"/></a>
                          |
                          <a href="<osp:url value="formDelete.osp">
                              <osp:param name="page_id" value="${cell.wizardPage.id}" />
                              <osp:param name="formDefId" value="${cellFormDef.id}" />
                              <osp:param name="current_form_id" value="${node.id}" />
                              <osp:param name="submit" value="delete" />
                              </osp:url>"><fmt:message key="delete"/></a>
                      </div>
                  </c:if>
                  </div>
               </td>
               <td>
                  <c:out value="${node.technicalMetadata.owner.displayName}"/>
               </td>
               <td>
                  <fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="${date_format}" />
               </td>
            </tr>
            </c:if>
	      </c:forEach>
	      
	   </c:forEach>
	   <!-- ***** show the attached resources ***** -->
         <c:forEach var="node" items="${cellBean.nodes}">
            <c:set var="canReflect" value="true"/>

            <tr>
               <td>
                  <a href='<c:out value="${node.externalUri}"/>' target="_blank">
                     <img border="0" title="<c:out value="${hover}" />"
                                  alt="<c:out value="${node.name}"/>" 
                                  src="/library/image/<osp-c:contentTypeMap 
                                  fileType="${node.mimeType}" mapType="image" 
                                  />"/><c:out value="${node.name}"/>
                  </a>
                  <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
                      <div class="itemAction indnt2">
                   <%--       <a name="linkNew" href="<osp:url value="attachToCell.osp">
						 <osp:param name="page_id" value="${cell.wizardPage.id}"/>
						 </osp:url>" onclick="javascript:stopEvents(event)"><fmt:message key="edit"/></a>
						 |   --%>
                          <a name="linkNew" href="<osp:url value="resourceDelete.osp">
						 <osp:param name="page_id" value="${cell.wizardPage.id}"/>
						 <osp:param name="resource_id" value="${node.id}"/>
						 <osp:param name="submit" value="delete"/>
						 </osp:url>" onclick="javascript:stopEvents(event)"><fmt:message key="delete"/></a>
                      </div>
                  </c:if>
               </td><td>
                  <c:choose>
                     <c:when test="${node.technicalMetadata.size > 1024 * 1024}">
                        <fmt:formatNumber value="${node.technicalMetadata.size / (1024 * 1024)}" maxFractionDigits="1"/><fmt:message key="text_MB"/>
                     </c:when>
                     <c:when test="${node.technicalMetadata.size > 1024}">
                        <fmt:formatNumber value="${node.technicalMetadata.size / (1024)}"  maxFractionDigits="1"/><fmt:message key="text_KB"/>
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
                  <fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="${date_format}" />
               </td>
            </tr>
         </c:forEach>
   </table>
   
   <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
      <div class="act">
         <input type="submit" name="manageAttachments" value="<fmt:message key="action_manageItems"/>"
         	onclick="javascript:stopEvents(event); document.form.method='GET';document.form.action='<osp:url value="attachToCell.osp">
        	 <osp:param name="page_id" value="${cell.wizardPage.id}"/>
        	 </osp:url>'" /> <!--
      <a name="linkNew" id="linkNew" href="<osp:url value="attachToCell.osp">
         <osp:param name="page_id" value="${cell.wizardPage.id}"/>
         </osp:url>" onclick="javascript:stopEvents(event)"><fmt:message key="action_manageItems"/></a>
       --> 
      </div>
   </c:if>
   
   <!-- ************* Form Area End ************* -->
   
   
   
   <!-- ************* Reflection Area Start ************* -->
   <c:if test="${cell.scaffoldingCell.reflectionDevice != null}">
      <table class="listHier lines">
		<tr>
			<th><osp:message key="reflection_section_header"/></th>
		</tr>
	  </table>
      
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
            <a href='<c:out value="${reflections[0].reviewContentNode.externalUri}"/>' target="_blank" >
               <img src = '/library/image/sakai/generic.gif' border= '0' hspace='0' />
               <c:out value="${reflections[0].reviewContentNode.displayName}"/>
            </a>            
         </c:if>
         <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
           <img src = '/library/image/sakai/generic.gif' border= '0' hspace='0' />
                     
           <c:out value="${reflections[0].reviewContentNode.displayName}" />
           <div class="itemAction indnt2">
             <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
               <osp:param name="page_id" value="${cell.wizardPage.id}" />
               <osp:param name="org_theospi_portfolio_review_type" value="0" />
               <osp:param name="current_review_id" value="${reflections[0].reviewContentNode.resource.id}" />
               <osp:param name="process_type_key" value="page_id" />
               </osp:url>">
                     <osp:message key="reflection_edit"/></a>
           </div>
         </c:if>
      </c:if>
      
   </c:if>
	<!-- if status is ready -->
    <p class="act">
    	<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
    		<c:if test="${canReflect == 'true'}">
    			<input type="submit" name="submit" value="<osp:message key="submit_for_evaluation"/>"/>
    		</c:if>
    		<c:if test="${canReflect == 'true' && currentStep == (totalSteps) && isWizard == 'true'}">
    			<input type="submit" name="submitWizard" value="<osp:message key="submit_wizard_for_evaluation"/>"/>
    		</c:if>
    	</c:if>
    	<input type="hidden" name="page_id" value="<c:out value="${cell.wizardPage.id}"/>"/>
    </p>
   <!-- ************* Reflection Area End ************* -->
   
   

<!-- ************* Review Area Start ************* -->
   <table class="listHier lines">
		<tr>
			<th colspan="2"><osp:message key="reviews_section_header"/></th>
			<th><osp:message key="table_header_owner"/></th>
			<th><fmt:message key="table_header_creationDate"/></th>
		</tr>
		<tr>
			<td width="32"><img border="0" src="/library/image/sakai/dir_openminus.gif"/></td>
			<td><osp:message key="reviews_section_header"/>
			   <div class="itemAction">
				 
				  <c:if test="${(matrixCan.review || wizardCan.review) && cell.scaffoldingCell.reviewDevice != null}">
					 <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
						   <osp:param name="page_id" value="${cell.wizardPage.id}" />
						<osp:param name="org_theospi_portfolio_review_type" value="2" />
						<osp:param name="process_type_key" value="page_id" />
						</osp:url>">
							  <osp:message key="review"/></a>
				  </c:if> 
			   </div>
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
      <c:forEach var="object" items="${reviews}" varStatus="loopStatus">
         <tr>
            <td />
            <td>
               <a href='<c:out value="${object.reviewContentNode.externalUri}"/>' target="_blank" >
               <img src = '/library/image/sakai/generic.gif' border= '0' hspace='0' />
               <c:out value="${object.reviewContentNode.displayName}"/></a>            
            </td>
            <td>
               <c:out value="${object.reviewContentNode.technicalMetadata.owner.displayName}" />
            </td>
            <td>
               <fmt:formatDate value="${object.reviewContentNode.technicalMetadata.creation}" pattern="${date_format}" />
            </td>
         </tr>
      </c:forEach>
   </table>
	
<!-- ************* Review Area End ************* -->
<!-- ************* Evaluation Area Start ************* -->

   <c:if test="${((matrixCan.evaluate || wizardCan.evaluate) && cell.scaffoldingCell.evaluationDevice != null && cell.status == 'PENDING') || not empty evaluations}">
      <table class="listHier lines">
		<tr>
			<th colspan="2"><osp:message key="evals_section_header"/></th>
			<th><osp:message key="table_header_owner"/></th>
			<th><fmt:message key="table_header_modified"/></th>
		</tr>
		<tr>
			<td width="32"><img border="0" src="/library/image/sakai/dir_openminus.gif"/></td>
			<td><osp:message key="evals_section_header"/>
			   <div class="itemAction">
				 
      			  <c:if test="${(matrixCan.evaluate || wizardCan.evaluate) && cell.scaffoldingCell.evaluationDevice != null && cell.status == 'PENDING'}">
					 <a href="<osp:url value="osp.review.processor.helper/reviewHelper.osp">
						   <osp:param name="page_id" value="${cell.wizardPage.id}" />
						<osp:param name="org_theospi_portfolio_review_type" value="2" />
						<osp:param name="process_type_key" value="page_id" />
						</osp:url>">
							  <osp:message key="add_evaluation"/></a>
				  </c:if> 
			   </div>
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
      <c:forEach var="object" items="${evaluations}" varStatus="loopStatus">
         <tr>
            <td />
            <td>
               <a href='<c:out value="${object.reviewContentNode.externalUri}"/>' target="_blank" >
               <img src = '/library/image/sakai/generic.gif' border= '0' hspace='0' />
               <c:out value="${object.reviewContentNode.displayName}"/></a>            
            </td>
            <td>
               <c:out value="${object.reviewContentNode.technicalMetadata.owner.displayName}" />
            </td>
            <td>
               <fmt:formatDate value="${object.reviewContentNode.technicalMetadata.creation}" pattern="${date_format}" />
            </td>
         </tr>
      </c:forEach>
   </table>
</c:if>

<!-- ************* Evaluation Area End ************* -->

<c:if test="${sequential == 'true'}">
<div class="act">
    <c:if test="${currentStep < (totalSteps)}">
        <input type="submit" name="_next" value="<fmt:message key="button_continue"/>"/>
    </c:if>
    <c:if test="${currentStep != 1}">
        <input type="submit" name="_back" value="<fmt:message key="button_back"/>"/>
    </c:if>
    <input type="submit" name="matrix" value="<fmt:message key="button_finish"/>"/>
    <input type="submit" name="cancel" value="<fmt:message key="button_cancel"/>"/>
</div>
</c:if>
<c:if test="${sequential != 'true'}">
<div class="act">
   <c:if test="${isWizard == 'true'}">
      <input type="submit" name="matrix" class="active" value="<fmt:message key="button_back_to_wizard"/>"/>
   </c:if>
   <c:if test="${isWizard != 'true'}">
      <input type="submit" name="matrix" class="active" value="<fmt:message key="button_back_to_matrix"/>"/>
   </c:if>
</div>
</c:if>

</form>
