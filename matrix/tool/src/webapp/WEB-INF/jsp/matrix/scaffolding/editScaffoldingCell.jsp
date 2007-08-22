<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<form name="form" method="POST">
  
    <c:if test="${taggable && !(empty helperInfoList)}">
      <div class="navIntraTool">
        <c:forEach var="helperInfo" items="${helperInfoList}">
          <a href="javascript:document.forms[0].submitAction.value='tagActivity';document.forms[0].providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.forms[0].onsubmit();document.forms[0].submit();"
             title="<c:out value="${helperInfo.description}"/>">
            <c:out value="${helperInfo.name}"/>
          </a>
        </c:forEach>
      </div>
    </c:if>

    <h3><fmt:message key="${pageTitleKey}" /></h3>
		<c:if test="${empty helperPage}">
			(<c:out value="${scaffoldingCell.scaffolding.rowLabel}"/>: <span class="highlight"><c:out value="${scaffoldingCell.rootCriterion.description}"/></span>; <c:out value="${scaffoldingCell.scaffolding.columnLabel}"/>: <span class="highlight"><span class="highlight"><c:out value="${scaffoldingCell.level.description}"/></span>) </h3>
		</span
			</c:if>
	<div class="instruction"> 
          <fmt:message key="${pageInstructionsKey}"/>
          <fmt:message key="instructions_requiredFields"/> 
          <br/><br/>
         <c:if test="${scaffoldingCell.scaffolding.published}">
           <c:if test="${isCellUsed}">
             <fmt:message key="instructions_hasBeenUsed"/>
             <c:set var="localDisabledText" value="disabled=\"disabled\""/>
           </c:if>
           <c:if test="${!isCellUsed}">
             <fmt:message key="instructions_hasBeenPublished"/>
           </c:if>
         </c:if>
	      <c:if test="${wizardPublished}">
           <c:if test="${isPageUsed}">
             <fmt:message key="instructions_hasBeenUsed"/>
             <c:set var="localDisabledText" value="disabled=\"disabled\""/>
           </c:if>
           <c:if test="${!isPageUsed}">
             <fmt:message key="instructions_wizardHasBeenPublished"/>
           </c:if>
	      </c:if>
	      
	</div>


    <osp:form/>
    <input type="hidden" name="params" value="" />
    <input type="hidden" name="submitAction" value="forward" />
    <input type="hidden" name="dest" value="loadReviewers" />
	<input type="hidden" name="label" value="" /> 
	<input type="hidden" name="displayText" value="" /> 
	<input type="hidden" name="finalDest" value="" /> 
    <input type="hidden" name="validate" value="false" />

        <spring:bind path="scaffoldingCell.title">
          <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
          </c:if>
		  <p class="shorttext">
				<span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_cellTitle"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"
					   value="<c:out value="${status.displayValue}"/>" size="40" id="<c:out value="${status.expression}"/>-id"/>
		    </p>
        </spring:bind>
        
        <div class="longtext">
            <label class="block"><fmt:message key="label_cellDescription"/></label>
            <spring:bind path="scaffoldingCell.wizardPageDefinition.description">
                     <table><tr>
               <td><textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80">
                  <c:out value="${status.value}"/></textarea></td>
                     </tr></table>
               <c:if test="${status.error}">
               <div class="validation"><c:out value="${status.errorMessage}"/></div>
               </c:if>
            </spring:bind>
      </div>
      <c:if test="${isWizard != 'true'}">
         <spring:bind path="scaffoldingCell.initialStatus">  
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
          <p class="shorttext">
            <span class="reqStar">*</span>
            <label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_initialStatus"/></label>     
               <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id">
                  <option value="READY" <c:if test="${status.value=='READY'}"> selected</c:if>><fmt:message key="matrix_legend_ready"/></option>
                  <option value="LOCKED" <c:if test="${status.value=='LOCKED'}"> selected</c:if>><fmt:message key="matrix_legend_locked"/></option>
               </select>
          </p>
         </spring:bind>
      </c:if>
      <c:if test="${isWizard == 'true'}">
         <spring:bind path="scaffoldingCell.initialStatus">  
            <input type="hidden" name="<c:out value="${status.expression}"/>" value="READY" />
         </spring:bind>
      </c:if>
      
     <!-- ************* Style Area Start ************* -->
         <p class="shorttext">
            <label for="styleName"><fmt:message key="style_section_header"/></label>    

      
         <c:if test="${empty scaffoldingCell.wizardPageDefinition.style}">
            <input name="styleName" value="<c:out value="" />" id="styleName" type="text" />
            <a href="javascript:document.forms[0].dest.value='stylePickerAction';
            document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='stylePickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:styleReturnView=<c:out value="${returnView}" />';
            document.forms[0].onsubmit();
            document.forms[0].submit();">
            <osp:message key="select_style" /></a>
         </c:if>
         <c:if test="${not empty scaffoldingCell.wizardPageDefinition.style}">
            <c:set value="${scaffoldingCell.wizardPageDefinition.style}" var="style" />
            <input name="styleName" value="<c:out value="${style.name}" />" id="styleName" type="text" />
            <a href="javascript:document.forms[0].dest.value='stylePickerAction';
            document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='stylePickerAction=true:currentStyleId=<c:out value="${style.id}"/>:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:styleReturnView=<c:out value="${returnView}" />';
            document.forms[0].onsubmit();
            document.forms[0].submit();">
            <osp:message key="change_style" /></a>
         </c:if>
         </p>
   <!-- ************* Style Area End ************* -->
   
   <!-- ************* Additional Forms Area Start ************* -->   
        
      <c:if test="${not empty selectedAdditionalFormDevices}">
         <h4><fmt:message key="title_additionalForms"/></h4>
	     <div class="instruction"> 
	        <fmt:message key="addForms_instructions" />
	     </div>
      </c:if>
	  <p class="shorttext">
         <label for="selectAdditionalFormId" ><fmt:message key="label_selectForm"/></label>    
         <select name="selectAdditionalFormId"  id="selectAdditionalFormId" 
            <option value="" selected><fmt:message key="select_form_text" /></option>
            <c:forEach var="addtlForm" items="${additionalFormDevices}" varStatus="loopCount">
               <option value="<c:out value="${addtlForm.id}"/>">
                  <c:out value="${addtlForm.name}"/></option>
            </c:forEach>
         </select>
			 <span class="act">
				<input type="submit" name="addForm" value="<fmt:message key="button_add"/>" onclick="javascript:document.forms[0].validate.value='false';" />
			 </span>
		</p>	 
	  <table class="listHier lines nolines" cellpadding="0" cellspacing="0" summary="" style="width:50%">
		  <c:forEach var="chosenForm" items="${selectedAdditionalFormDevices}">
		  <tr>
			  <td>
			  	<span class="indnt1">
				<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
				<c:out value="${chosenForm.name}" />
				</span>
			</td>
			<td>
				<c:if test="${empty localDisabledText}">
					<div class="itemAction">
						<a href="javascript:document.forms[0].submitAction.value='removeFormDef';
						  document.forms[0].params.value='id=<c:out value="${chosenForm.id}"/>';
						  document.forms[0].onsubmit();
						  document.forms[0].submit();">
							<osp:message key="remove"/>
					   </a>
					</div>
				</c:if>
			</td>
			</tr>
		  </c:forEach>
	 </table>
      <!-- ************* Additional Forms Area End ************* -->   
      
   <!-- *************  Assignments Area Start ************* -->   
	<c:if test="${enableAssignments}">
      <table cellpadding="0" cellspacing="0" border="0" style="width:50%">
         <tr>
        <th style="text-align:left"><h4><osp:message key="edit.assignments"/></h4></th> 
			<th style="text-align:right">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='assignPickerAction';
				document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='assignPickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:assignReturnView=<c:out value="${returnView}" />';
				document.forms[0].onsubmit();
				document.forms[0].submit();">
					<osp:message key="edit.addAssign"/>
				</a>
			 </th>
			</tr>
         
		  <c:forEach var="assign" items="${selectedAssignments}">
		  <tr>
			  <td>
			  	<span class="indnt1">
				<img src = '/library/image/sakai/assignment.gif' border= '0' alt ='' />
				<c:out value="${assign.title}" />
				</span>
			</td>
			</tr>
		  </c:forEach>
       </table>
		 <br/>
	</c:if>

   <!-- ************* Assignments Area End ************* -->   

   <!-- ************* Guidance Area Start ************* -->        
      <h4><osp:message key="guidance_header"/></h4>
      <table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:50%" summary="<osp:message key="guidance_table_summary"/>">
         <tr>
            <th><h5><osp:message key="instructions"/></h5></th>                     
			<th style="text-align:right"  class="specialLink itemAction">
			  <c:if test="${empty scaffoldingCell.guidance.instruction.limitedText}">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='editInstructions';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
					<osp:message key="addInstructions"/>
				</a>
			  </c:if>
			  <c:if test="${not empty scaffoldingCell.guidance.instruction.limitedText}">
				 <a href="#" 
					onclick="javascript:document.forms[0].dest.value='editInstructions';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
					<osp:message key="reviseInstructions"/>
				</a>	
			  </c:if>
			 </th>
			</tr>
			<tr class="exclude">
			<td colspan="2">
				<div class="textPanel">
				   <c:if test="${not empty scaffoldingCell.guidance.instruction.limitedText}">
					  <c:out value="${scaffoldingCell.guidance.instruction.limitedText}" escapeXml="false" />
				   </c:if>
				   <ul class="attachList indnt1">
					   <c:forEach var="attachment" items="${scaffoldingCell.guidance.instruction.attachments}" varStatus="loopStatus">
							<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										  alt="<c:out value="${attachment.displayName}"/>" 
										  src="/library/image/<osp-c:contentTypeMap 
										  fileType="${attachment.mimeType}" mapType="image" 
										  />"/>
							 <a title="<c:out value="${attachment.displayName}" />"
								href="<c:out value="${attachment.fullReference.base.url}" />" target="_blank">
								<c:out value="${attachment.displayName}"/>
							 </a>
								<c:out value=" (${attachment.contentLength})"/>
							</li>
					   </c:forEach>
					   <li><!--TODO empty list item placeholder - remove when list can be omitted because empty--></li>
					</ul>   
				 </div>  
            </td>
         </tr>
		 </table>
		   <table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:50%"  summary="<osp:message key="rationale_table_summary"/>">
         <tr>
            <th><h5><osp:message key="rationale"/></h5></th>
			<th style="text-align:right" class="specialLink itemAction">
				<c:if test="${empty scaffoldingCell.guidance.rationale.limitedText}">
					<a href="#" onclick="javascript:document.forms[0].dest.value='editRationale';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
						 <osp:message key="addRationale"/>
					 </a>
				</c:if>
			  <c:if test="${not empty scaffoldingCell.guidance.rationale.limitedText}">
				 <a href="#" 
					onclick="javascript:document.forms[0].dest.value='editRationale';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
				<osp:message key="reviseRationale"/>
				</a>
			  </c:if>
		  </th>
		</tr>	
      	<tr class="exclude">
			<td colspan="2">
				<div class="textPanel">
					<c:if test="${not empty scaffoldingCell.guidance.rationale.limitedText}">
					  <c:out value="${scaffoldingCell.guidance.rationale.limitedText}" escapeXml="false" />
				   </c:if>
				   <ul class="attachList indnt1">
					   <c:forEach var="attachment" items="${scaffoldingCell.guidance.rationale.attachments}" varStatus="loopStatus">
							<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										  alt="<c:out value="${attachment.displayName}"/>" 
										  src="/library/image/<osp-c:contentTypeMap 
										  fileType="${attachment.mimeType}" mapType="image" 
										  />"/>
							 <a title="<c:out value="${attachment.displayName}" />"
								href="<c:out value="${attachment.fullReference.base.url}" />" target="_blank">
								<c:out value="${attachment.displayName}"/>
							 </a>
								<c:out value=" (${attachment.contentLength})"/>
								</li>
					   </c:forEach>
					   <li><!--TODO empty list item placeholder - remove when list can be omitted because empty--></li>
				   </ul>	   
				 </div>  
            </td>
         </tr>
		</table>
		  <table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:50%"  summary="<osp:message key="examples_table_summary"/>">
         <tr>
            <th><h5><osp:message key="examples"/></h5></th>
            <th style="text-align:right" class="itemAction specialLink">
			  <c:if test="${empty scaffoldingCell.guidance.example.limitedText}">
					<a href="#" onclick="javascript:document.forms[0].dest.value='editExamples';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
					<osp:message key="addExamples"/>
				</a>	
			  </c:if>
			  <c:if test="${not empty scaffoldingCell.guidance.example.limitedText}"> 
					<a href="#"  onclick="javascript:document.forms[0].dest.value='editExamples';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
						<osp:message key="reviseExamples"/>
					</a>	
			  </c:if>
			 </th>
			 </tr>
			 <tr class="exclude">
			 <td colspan="2">
			 	<div class="textPanel">
				<c:if test="${not empty scaffoldingCell.guidance.example.limitedText}">
                  <c:out value="${scaffoldingCell.guidance.example.limitedText}" escapeXml="false" />
      		   </c:if>
			   <ul class="attachList indnt1">
               <c:forEach var="attachment" items="${scaffoldingCell.guidance.example.attachments}" varStatus="loopStatus">
               		<li><img border="0" title="<c:out value="${attachment.displayName}" />"
                                  alt="<c:out value="${attachment.displayName}"/>" 
                                  src="/library/image/<osp-c:contentTypeMap 
                                  fileType="${attachment.mimeType}" mapType="image" 
                                  />"/>
                     <a title="<c:out value="${attachment.displayName}" />"
                        href="<c:out value="${attachment.fullReference.base.url}" />" target="_new">
                        <c:out value="${attachment.displayName}"/>
                     </a>
                        <c:out value=" (${attachment.contentLength})"/>
					</li>	
               </c:forEach>
			      <li><!--TODO empty list item placeholder - remove when list can be omitted because empty--></li>
				   </ul>	   
				 </div>
            </td>
         </tr>
      </table>
   <!-- ************* Guidance Area End ************* -->    
      
      
   <!-- ************* Guidance and reflection Area Start ************* -->   
     
      
            <spring:bind path="scaffoldingCell.reflectionDeviceType">  
            <input type="hidden" name="<c:out value="${status.expression}"/>"
               value="<c:out value="${status.value}"/>" />
        </spring:bind>
        
      <spring:bind path="scaffoldingCell.reflectionDevice">  
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
           <h4><osp:message key="label_selectReflectionDevice"/></h4>
	<div class="instruction"><fmt:message key="reflection_select_instructions"/></div> 
          <p class="shorttext"> 
            <label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectReflectionDevice"/></label>    
               <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>" 
                     <c:if test="${not empty status.value}"> <c:out value="${localDisabledText}"/> </c:if>>
                     <option onclick="document.forms[0].reflectionDeviceType.value='';" value=""><fmt:message key="select_item_text" /></option>
                  <c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].reflectionDeviceType.value='<c:out value="${refDev.type}"/>';" 
                        value="<c:out value="${refDev.id}"/>" <c:if test="${status.value==refDev.id}"> selected</c:if>><c:out value="${refDev.name}"/></option>
                  </c:forEach>
               </select>
          </p>
        </spring:bind>
		<spring:bind path="scaffoldingCell.reviewDeviceType">  
            <input type="hidden" name="<c:out value="${status.expression}"/>"
               value="<c:out value="${status.value}"/>" />
        </spring:bind>   
     <spring:bind path="scaffoldingCell.reviewDevice">  
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
		<h4> <osp:message key="label_selectReviewDevice"/></h4>
		<div class="instruction"><fmt:message key="feedback_select_instructions"/></div> 
       <p class="shorttext">
         <label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectReviewDevice"/></label>    
            <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
                     <c:if test="${not empty status.value}"> <c:out value="${localDisabledText}"/> </c:if>>
                     <option onclick="document.forms[0].reviewDeviceType.value='';" value=""><fmt:message key="select_item_text" /></option>
                  <c:forEach var="reviewDev" items="${reviewDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].reviewDeviceType.value='<c:out value="${reviewDev.type}"/>';" 
                        value="<c:out value="${reviewDev.id}"/>" <c:if test="${status.value==reviewDev.id}"> selected</c:if>><c:out value="${reviewDev.name}"/></option>
                  </c:forEach>
               </select>
       </p>
     </spring:bind>
     
         <spring:bind path="scaffoldingCell.evaluationDeviceType">  
            <input type="hidden" name="<c:out value="${status.expression}"/>"
               value="<c:out value="${status.value}"/>" />
        </spring:bind>
   <!-- ************* Guidance and reflection Area End ************* -->        
   
   <!-- ************* Review and Evaluation Area Start ************* -->            
		
   <h4><fmt:message key="header_Evaluators"/></h4>
   <div id="evaluatorsDiv">  
	<div class="instruction"><fmt:message key="evaluation_select_instructions"/></div>
   
     <spring:bind path="scaffoldingCell.evaluationDevice">  
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
       <p class="shorttext">
         <label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectEvaluationDevice"/></label>    
            <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
                     <c:if test="${not empty status.value}"> <c:out value="${localDisabledText}"/> </c:if>>
                     <option onclick="document.forms[0].evaluationDeviceType.value='';" value=""><fmt:message key="select_item_text" /></option>
                  <c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].evaluationDeviceType.value='<c:out value="${evalDev.type}"/>';" 
                        value="<c:out value="${evalDev.id}"/>" <c:if test="${status.value==evalDev.id}"> selected</c:if>><c:out value="${evalDev.name}"/></option>
                  </c:forEach>
               </select>
       </p>
     </spring:bind>
   </div>
      <!-- ************* Review and Evaluation Area End ************* -->
   

      <!-- ************* Evaluators List Start ************* -->            
   <h4><fmt:message key="label_evaluators"/></h4>
    	<ol>
      <c:forEach var="eval" items="${evaluators}">
         <li><c:out value="${eval}" /></li>
      </c:forEach>
      </ol>
      <c:if test="${empty evaluators}">
         <fmt:message key="no_evaluators"/>
      </c:if>
        <div class="act">
   		   <input type="button" name="selEval" value="<osp:message key="select_evaluators"/>"
              onclick="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" />
        </div>
      <!-- ************* Evaluators List End ************* -->
   
      
      
	<spring:bind path="scaffoldingCell.id">
		<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
		<span class="error_message"><c:out value="${status.errorMessage}"/></span>
	</spring:bind>

    <c:if test="${taggable}">
      <%@ include file="../tagLists.jspf" %>
    </c:if>

	<div class="act">
		<input type="submit" name="saveAction" value="<osp:message key="save"/>" class="active" onclick="javascript:document.forms[0].validate.value='true';" accesskey="s" />

      <c:if test="${empty helperPage}">
         <input type="button" name="action" value="<osp:message key="cancel"/>"
                onclick="javascript:document.form.submitAction.value='cancel';document.form.submit();" accesskey="x"/>
      </c:if>
      <c:if test="${not empty helperPage}">
   		<input type="button" name="action" value="<osp:message key="cancel"/>"
            onclick="javascript:doCancel()"  accesskey="x"/>
         <input type="hidden" name="canceling" value="" />
      </c:if>

	</div>
   
   <osp:richTextWrapper textAreaId="descriptionTextArea" />

</form>

   <form name="cancelForm" method="POST">
      <osp:form/>

      <input type="hidden" name="validate" value="false" />
      <input type="hidden" name="canceling" value="true" />
   </form>
