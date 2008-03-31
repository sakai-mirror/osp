<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<script type="text/javascript">

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

<form name="form" method="post">

	<c:if test="${taggable}">
		<div class="navIntraTool">
			<c:if test="${!(empty helperInfoList)}">
			<c:forEach var="helperInfo" items="${helperInfoList}">
				<a href="javascript:document.forms[0].submitAction.value='tagActivity';document.forms[0].providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.forms[0].onsubmit();document.forms[0].submit();"
					title="<c:out value="${helperInfo.description}"/>">
					<c:out value="${helperInfo.name}"/>
				</a>
			</c:forEach>
			</c:if>
			<a href="javascript:document.forms[0].submitAction.value='listPageActivities';document.forms[0].providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.forms[0].onsubmit();document.forms[0].submit();"
					title="<fmt:message key="link_page_activities"/>">
					<fmt:message key="link_page_activities"/>
				</a>
		</div>
	</c:if>
	
	
	<c:if test="${isWizard}">
		<!-- Since wizard doesn't have anything to check these, assume they are false until it is set to true based on isPageUsed --->
		<c:set var="evaluationFormUsed" value="false"/>
		<c:set var="feedbackFormUsed" value="false"/>
		<c:set var="reflectionFormUsed" value="false"/>
		<c:set var="customFormUsed" value="false"/>
	</c:if>
	

	<h3><fmt:message key="${pageTitleKey}" /> - 
		<c:if test="${!isWizard}">
			<fmt:message key="matrix_name"/>
		</c:if>
		<c:if test="${isWizard}">
			<fmt:message key="wizard_name"/>
		</c:if>
		<span class="highlight"><c:out value="${scaffoldingCell.scaffolding.title}"/></span>
	</h3>

	<c:if test="${empty helperPage}">
		(<c:out value="${scaffoldingCell.scaffolding.rowLabel}"/>: <span class="highlight"><c:out value="${scaffoldingCell.rootCriterion.description}"/></span>; <c:out value="${scaffoldingCell.scaffolding.columnLabel}"/>: <span class="highlight">
		<c:out value="${scaffoldingCell.level.description}"/></span>) 
		</c:if>
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="${pageInstructionsKey}"/></legend>
		<div class="instruction"> 
			<fmt:message key="instructions_requiredFields"/> 
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
					
					<!-- Since wizard doesn't have anything to check these, assume the isUsed booleans are all true --->
					<c:set var="evaluationFormUsed" value="true"/>
					<c:set var="feedbackFormUsed" value="true"/>
					<c:set var="reflectionFormUsed" value="true"/>
					<c:set var="customFormUsed" value="true"/>
					
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
				<p class="shorttext validFail">
			</c:if>			
			<c:if test="${!status.error}">
				<p class="shorttext">
			</c:if>	
				<span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_cellTitle"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.displayValue}"/>" size="40" id="<c:out value="${status.expression}"/>-id" />
				<c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>
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
					<label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_initialStatus"/></label>     
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id">
						<option value="READY" <c:if test="${status.value=='READY'}"> selected="selected"</c:if>><fmt:message key="matrix_legend_ready"/></option>
						<option value="LOCKED" <c:if test="${status.value=='LOCKED'}">selected="selected"</c:if>><fmt:message key="matrix_legend_locked"/></option>
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
		
		<p class="shorttext">
			<spring:bind path="scaffoldingCell.wizardPageDefinition.suppressItems">  
				<label for="suppressItems" ><fmt:message key="suppressSelectItems_header"/></label>    
				<input type="checkbox" name="suppressItems" value="true"  id="suppressItems" 
				<c:if test="${status.value}">checked</c:if> />
			</spring:bind>
		</p>
	</fieldset>
	<!-- ************* Style Area End ************* -->
	
	<!-- ************* Guidance Area Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><osp:message key="guidance_header"/></legend>
		<c:if test ="${empty scaffoldingCell.guidance.instruction.limitedText && empty scaffoldingCell.guidance.instruction.attachments}">
			<h5><osp:message key="instructions"/></h5>
			<p class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='editInstructions';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
					<osp:message key="add_first_instructions"/></a>
				&nbsp;<osp:message key="add_first_instructions_message"/>
			</p>	
		</c:if>
		<c:if test ="${not empty scaffoldingCell.guidance.instruction.limitedText || not empty scaffoldingCell.guidance.instruction.attachments}">
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%" summary="<osp:message key="guidance_table_summary"/>">
				<tr>
					<th><h5><osp:message key="instructions"/></h5></th>                     
					<th style="text-align:right"  class="specialLink itemAction">
						<a href="#" 
							onclick="javascript:document.forms[0].dest.value='editInstructions';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
							<osp:message key="reviseInstructions"/>
						</a>	
					</th>
				</tr>
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.instruction.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.instruction.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.instruction.attachments}">
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
							</ul>
						</c:if>
					</td>
				</tr>
			</table>
		</c:if>	
		<c:if test ="${empty scaffoldingCell.guidance.rationale.limitedText && empty scaffoldingCell.guidance.rationale.attachments}">
			<h5><osp:message key="rationale"/></h5>
			<p class="indnt1">
				<a href="#" onclick="javascript:document.forms[0].dest.value='editRationale';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
					<osp:message key="add_first_rationale"/></a>
				&nbsp;<osp:message key="add_first_rationale_message"/>				
			</p>
		</c:if>	
		<c:if test ="${not empty scaffoldingCell.guidance.rationale.limitedText || not empty scaffoldingCell.guidance.rationale.attachments}">		
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%"  summary="<osp:message key="rationale_table_summary"/>">
				<tr>
					<th><h5><osp:message key="rationale"/></h5></th>
					<th style="text-align:right" class="specialLink itemAction">
						<a href="#" 
							onclick="javascript:document.forms[0].dest.value='editRationale';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
							<osp:message key="reviseRationale"/>
						</a>
					</th>
				</tr>	
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.rationale.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.rationale.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.rationale.attachments}">
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
							</ul>
						</c:if>	
					</td>
				</tr>
			</table>
		</c:if>	
	
		<c:if test ="${empty scaffoldingCell.guidance.example.limitedText && empty scaffoldingCell.guidance.example.attachments}">
			<h5><osp:message key="examples"/></h5>
			<p class="indnt1">
				<a href="#" onclick="javascript:document.forms[0].dest.value='editExamples';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
					<osp:message key="add_first_examples"/></a>
				&nbsp;<osp:message key="add_first_examples_message"/>				
			</p>	
		</c:if>
		<c:if test ="${not empty scaffoldingCell.guidance.example.limitedText || not empty scaffoldingCell.guidance.example.attachments}">	
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%"  summary="<osp:message key="examples_table_summary"/>">
				<tr>
					<th><h5><osp:message key="examples"/></h5></th>
					<th style="text-align:right" class="itemAction specialLink">
						<a href="#"  onclick="javascript:document.forms[0].dest.value='editExamples';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
							<osp:message key="reviseExamples"/>
						</a>	
					</th>
				</tr>
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.example.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.example.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.example.attachments}">
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
							</ul>
						</c:if>	
					</td>
				</tr>
			</table>
		</c:if>
	</fieldset>	
	<!-- ************* Guidance Area End ************* -->    
	
	
	<!-- ************* Guidance and reflection Area Start ************* -->   
	<SCRIPT type="text/javascript">
		function defaultFormClicked(checked, trueSpan, falseSpan){

		
			if(checked){			
				document.getElementById(trueSpan).style.display = "";
				document.getElementById(falseSpan).style.display = "none";
			}else{
				document.getElementById(trueSpan).style.display = "none";
				document.getElementById(falseSpan).style.display = "";
			}
			
			mySetMainFrameHeight(self.name);

		}
			
	</SCRIPT>
	
	
	
	<!-- *************  User Forms Area  Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="legend_additional_user_Forms"/></legend>
		
		<h5><fmt:message key="title_additionalForms"/></h5>
		
		<!-- default case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null}" >
		
			<!-- ************* Default Matrix Custom Form Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultCustomForm">  
				<input type="hidden" name="hiddenDefaultCustomForm" value="${status.value}"/>
				<input type="checkbox" name="defaultCustomForm" value="true"  id="defaultCustomForm" 
					<c:if test="${status.value}">
						checked
					</c:if> 
					<c:if test="${customFormUsed}">
					    <c:out value="${localDisabledText}"/> 
					</c:if>
					onclick="defaultFormClicked(this.checked, 'defaultCustomFormSpan', 'cellCustomFormSpan');document.forms[0].hiddenDefaultCustomForm.value=this.checked;"/>
				<label for="defaultCustomForm" ><fmt:message key="defaultCustomFormText"/></label>    
			</spring:bind>		
			<!-- ************* Default Matrix Checkbox End *********** -->
			
			
		
			<!-- Start of Defualt Custom Forms -->
			
			
			<span name="defaultCustomFormSpan" id="defaultCustomFormSpan" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultCustomForm}">style='display:none' </c:if>>
			
				<c:if test="${ empty defaultSelectedAdditionalFormDevices}">
					<p class="indnt1"> 
						<span class="highlight"><fmt:message key="addForms_instructions_noforms" /></span>
					</p>
				</c:if>
				<c:if test="${not empty defaultSelectedAdditionalFormDevices}">
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<fmt:message key="table_forms_summary"/>" style="width:50%">
						<c:forEach var="chosenForm" items="${defaultSelectedAdditionalFormDevices}">
							<tr>
								<td>
									<span class="indnt1">
										<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
										<c:out value="${chosenForm.name}" />
									</span>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
			</span>
			
			<!--- End of Defualt Custom Forms --->
			
		<!-- default case is currently only needed for matrices -->
		</c:if>
		
		
			<!--- Cell Custom Forms Start --->
			
			<span name="cellCustomFormSpan" id="cellCustomFormSpan" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultCustomForm}">style='display:none' </c:if>>
				
				<p class="indnt1"> 
					<fmt:message key="addForms_instructions" />
					<c:if test="${ empty selectedAdditionalFormDevices}">
						<span class="highlight"><fmt:message key="addForms_instructions_noforms" /></span>
					</c:if>
				</p>
				
				
				<p class="shorttext">
					<label for="selectAdditionalFormId" ><fmt:message key="label_selectForm"/></label>    
					<select name="selectAdditionalFormId"  id="selectAdditionalFormId"  onchange="document.getElementById('addForm-id').className='active';">
						<option value="" selected="selected"><fmt:message key="select_form_text" /></option>
						<c:forEach var="addtlForm" items="${additionalFormDevices}" varStatus="loopCount">
							<option value="<c:out value="${addtlForm.id}"/>">
						<c:out value="${addtlForm.name}"/></option>
						</c:forEach>
					</select>
					<span class="act">
						<input type="submit" id="addForm-id" name="addForm" value="<fmt:message key="button_add"/>" onclick="javascript:document.forms[0].validate.value='false';" />
					</span>
				</p>
				<c:if test="${not empty selectedAdditionalFormDevices}">
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<fmt:message key="table_forms_summary"/>" style="width:50%">
						<c:forEach var="chosenForm" items="${selectedAdditionalFormDevices}">
							<tr>
								<td>
									<span class="indnt1">
										<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
										<c:out value="${chosenForm.name}" />
									</span>
								</td>
								<td style="text-align:right">
			   						<c:set var="formUsed" value="false"/>
			   						<c:forEach var="usedForm" items="${usedAdditionalForms}">
			   							<c:if test="${usedForm == chosenForm.id}">
			   								<c:set var="formUsed" value="true"/>
			   							</c:if>
									</c:forEach>
									<c:if test="${formUsed == false}">
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
				</c:if>
			</span>
			
			
			<!--- Cell Custom Forms End --->
			
			
			
			<!-- ************* Assignments Area Start ************* -->   
				
			<c:if test="${enableAssignments}">
			<br><br>
				<h5><osp:message key="edit.assignments"/></h5>
				<c:if test="${empty selectedAssignments}">
					<p class="indnt1">
						<a href="#"	onclick="javascript:document.forms[0].dest.value='assignPickerAction';
							document.forms[0].submitAction.value='forward';
							document.forms[0].params.value='assignPickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:assignReturnView=<c:out value="${returnView}" />';
							document.forms[0].onsubmit();
							document.forms[0].submit();">
							<osp:message key="add_first_assignment"/></a>
						&nbsp;<osp:message key="add_first_assignment_message"/>					
					</p>	
				</c:if>
				<c:if test="${not empty selectedAssignments}">
					<table cellpadding="0" cellspacing="0" border="0" style="width:70%" class="listHier lines nolines collectionListBordered">
						<tr>
							<th style="text-align:left"></th> 
							<th style="text-align:right" class="itemAction">
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
								<td colspan="2">
									<span class="indnt1">
										<img src = '/library/image/silk/page_white_edit.png' border= '0' alt ='' />
										<c:out value="${assign.title}" />
									</span>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>	
			</c:if>	
			
			<!-- ************* Assignments Area End ************* -->   
			
			
		<br><br>
			
		<h5><osp:message key="label_selectReflectionDevice"/></h5>
		
		<!-- default case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null}" >
		
			<!-- ************* Default Matrix Reflection Form Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultReflectionForm">  
				<input type="hidden" name="hiddenDefaultReflectionForm" value="${status.value}"/>
				<input type="checkbox" name="defaultReflectionForm" value="true"  id="defaultReflectionForm" 
					<c:if test="${status.value}">
						checked
					</c:if> 
				    <c:if test="${reflectionFormUsed}"><c:out value="${localDisabledText}"/></c:if>
					onclick="defaultFormClicked(this.checked, 'defaultReflectionFormSpan', 'cellReflectionFormSpan');document.forms[0].hiddenDefaultReflectionForm.value=this.checked;"/>
				<label for="defaultReflectionForm" ><fmt:message key="defaultReflectionFormText"/></label>    
			</spring:bind>		
			<!-- ************* Default Matrix Checkbox End *********** -->
			
			
			
			<!-- Default Reflection Area start -->
			
			<span name="defaultReflectionFormSpan" id="defaultReflectionFormSpan" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultReflectionForm}">style='display:none' </c:if>>
				
				<spring:bind path="scaffoldingCell.scaffolding.reflectionDevice">
					<c:if test="${status.value == null}">
						<p class="indnt1"> 
							<span class="highlight"><fmt:message key="addForms_instructions_noforms" /></span>
						</p>
					</c:if>
						
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<fmt:message key="table_forms_summary"/>" style="width:50%">
						<c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
							<c:if test="${status.value==refDev.id}">
								<tr>
									<td>
										<span class="indnt1">
											<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
											<c:out value="${refDev.name}"/>
										</span>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>						
				
				</spring:bind>
			</span>
			
			<!-- Default Reflection Area end -->
		
		<!-- default case is currently only needed for matrices -->
		</c:if>
		
		

		<!-- cell Reflection area start -->   
		
		<span name="cellReflectionFormSpan" id="cellReflectionFormSpan" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultReflectionForm}">style='display:none' </c:if>>

			<spring:bind path="scaffoldingCell.reflectionDeviceType">  
				<input type="hidden" name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.value}"/>" />
			</spring:bind>
		
			<spring:bind path="scaffoldingCell.reflectionDevice">  
				<c:if test="${status.error}">
					<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>
				<p class="indnt1">
					<fmt:message key="reflection_select_instructions"/>
				</p>	
				<p class="shorttext"> 
					<label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectReflectionDevice"/></label>    
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>" 
						<c:if test="${not empty status.value}"> <c:if test="${reflectionFormUsed}"><c:out value="${localDisabledText}"/></c:if> </c:if>>
						<option onclick="document.forms[0].reflectionDeviceType.value='';" value=""><fmt:message key="select_item_text" /></option>
						<c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
							<option onclick="document.forms[0].reflectionDeviceType.value='<c:out value="${refDev.type}"/>';" 
							value="<c:out value="${refDev.id}"/>" <c:if test="${status.value==refDev.id}"> selected="selected"</c:if>><c:out value="${refDev.name}"/></option>
						</c:forEach>
					</select>
				</p>
			</spring:bind>
		
		</span>
		<!-- *********  End span for hidding user forms when default user forms is checked *** -->
	</fieldset>
	
	
	

	<!--- Feedback Fieldset: --->

	
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="legend_feedback"/></legend>
		
		<h5><osp:message key="label_selectReviewDevice"/></h5>		
		
		
		<!-- this case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null}">


			<!-- ************* Default Matrix Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultFeedbackForm">  		
				<input type="hidden" name="hiddenDefaultFeedbackForm" value="${status.value}"/>
				<input type="checkbox" name="defaultFeedbackForm" value="true"  id="defaultFeedbackForm" 
				<c:if test="${status.value}">checked</c:if> onclick="defaultFormClicked(this.checked, 'defaultFeedbackEvalSpan', 'cellFeedbackFormSpan');document.forms[0].hiddenDefaultFeedbackForm.value=this.checked;" <c:if test="${feedbackFormUsed}"><c:out value="${localDisabledText}"/></c:if> />
				<label for="defaultFeedbackForm" ><fmt:message key="defaultFeedbackFormText"/></label> 
			</spring:bind>
			
			<!-- ************* Default Matrix Checkbox Start *********** -->
	
	
			<!-- Default Feedback Form start -->
			<span name="defaultFeedbackEvalSpan" id="defaultFeedbackEvalSpan" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultFeedbackForm}">style='display:none' </c:if>>
				<!-- Feedback -->
				
				<spring:bind path="scaffoldingCell.scaffolding.reviewDevice">
					<c:if test="${status.value == null}">
						<p class="indnt1"> 
							<span class="highlight"><fmt:message key="addForms_instructions_noforms" /></span>
						</p>
					</c:if>
						
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<fmt:message key="table_forms_summary"/>" style="width:50%">
						<c:forEach var="revDev" items="${reviewDevices}" varStatus="loopCount">
							<c:if test="${status.value==revDev.id}">
								<tr>
									<td>
										<span class="indnt1">
											<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
											<c:out value="${revDev.name}"/>
										</span>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>						
				</spring:bind>
			</span>
			<!-- Default Feedback Form end -->
		
			
		<!-- this case is currently only needed for matrices -->
		</c:if>

		<!--- Cell Feedback form start --->
		<span name="cellFeedbackFormSpan" id="cellFeedbackFormSpan" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultFeedbackForm}">style='display:none' </c:if>>
			
			<spring:bind path="scaffoldingCell.reviewDeviceType">  
				<input type="hidden" name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.value}"/>" />
			</spring:bind>   
			
			<spring:bind path="scaffoldingCell.reviewDevice">  
				<c:if test="${status.error}">
					<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>

				<p class="indnt1">
					<fmt:message key="feedback_select_instructions"/>
				</p>	
				<p class="shorttext">
					<label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectReviewDevice"/></label>    
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
						<c:if test="${not empty status.value}"> <c:if test="${feedbackFormUsed}"><c:out value="${localDisabledText}"/></c:if> </c:if>>
						<option onclick="document.forms[0].reviewDeviceType.value='';" value=""><fmt:message key="select_item_text" /></option>
						<c:forEach var="reviewDev" items="${reviewDevices}" varStatus="loopCount">
							<option onclick="document.forms[0].reviewDeviceType.value='<c:out value="${reviewDev.type}"/>';" 
							value="<c:out value="${reviewDev.id}"/>" <c:if test="${status.value==reviewDev.id}"> selected="selected"</c:if>><c:out value="${reviewDev.name}"/></option>
						</c:forEach>
					</select>
				</p>
			</spring:bind>
		</span>
		<!--- Cell Feedback form end --->			
			
			
		<c:if test="${!isWizard}">
		
			<!--- Reviewers Area Start --->
			
			<h5><osp:message key="label_reviwers"/></h5>		
			
			
			<!-- this case is currently only needed for matrices -->
			<c:if test="${scaffoldingCell.scaffolding != null}">
	
	
				<!-- ************* Default Matrix Checkbox Start *********** -->
				<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultReviewers">  			   
					<input type="checkbox" name="defaultReviewers" value="true"  id="defaultReviewers" 
					<c:if test="${status.value}">checked</c:if> onclick="defaultFormClicked(this.checked, 'defaultReviewersSpan', 'cellReviewersSpan');" />
					<label for="defaultReviewers" ><fmt:message key="defaultReviewersText"/></label> 
				</spring:bind>
				
				<!-- ************* Default Matrix Checkbox Start *********** -->
		
		
				<!-- Default Reviewers start -->
				<span name="defaultReviewersSpan" id="defaultReviewersSpan" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultReviewers}">style='display:none' </c:if>>
	
					<!-- Reviewers list -->
	
					<c:if test="${not empty defaultReviewers}">
						<ol>
							<c:forEach var="eval" items="${defaultReviewers}">
								<li><c:out value="${eval}" /></li>
							</c:forEach>
						</ol>
					</c:if>	
					<c:if test="${empty defaultReviewers}">
						<p class="indnt1">
							<span class="highlight"><fmt:message key="info_reviewersNoneNoBracket"/></span>
						</p>			
					</c:if>
					
					<p class="indnt1">
						<c:if test="${scaffoldingCell.scaffolding.allowRequestFeedback}">
							<c:out value="*"/><fmt:message key="allowRequestFeedback"/>
						</c:if>
						<c:if test="${!scaffoldingCell.scaffolding.allowRequestFeedback}">
							<c:out value="*"/><fmt:message key="doNotAllowRequestFeedback"/>
						</c:if>
					</p>
				</span>
				<!--  Default Reviewers start  -->
				
				
	
			<!-- this case is currently only needed for matrices -->
			</c:if>
				
					
			<!-- Cell Reviewers Start -->            
			<span name="cellReviewersSpan" id="cellReviewersSpan" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultReviewers}">style='display:none' </c:if>>
		
				<c:if test="${not empty reviewers}">
					<ol>
						<c:forEach var="eval" items="${reviewers}">
							<li><c:out value="${eval}" /></li>
						</c:forEach>
					</ol>
				</c:if>	
				<p class="indnt1">
					<a href="#"	onclick="javascript:document.forms[0].dest.value='selectReviewers';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
						<osp:message key="select_reviewers"/>
					</a>	 
					<c:if test="${empty reviewers}">
						&nbsp;<fmt:message key="info_reviewersNone"/>
					</c:if>
				</p>
				<p class="indnt1"> 
				<spring:bind path="scaffoldingCell.wizardPageDefinition.allowRequestFeedback">  			
					<input type="checkbox" name="allowRequestFeedback" value="true"  id="allowRequestFeedback" 
						<c:if test="${status.value}">
							checked
						</c:if> 
					 />
					<label for="allowRequestFeedback" ><fmt:message key="allowRequestFeedback"/></label>    
				</spring:bind>	
				</p>
			</span>
			<!-- Cell Reviewers End -->
		</c:if>
			
	</fieldset>
		
	<!--  ********** Evaluation start ************* -->
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="legend_evaluation"/></legend>

		<h5><fmt:message key="header_Evaluators"/></h5>

		<!-- this case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null}">


			<!-- ************* Default Matrix Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultEvaluationForm">  		
				<input type="hidden" name="hiddenDefaultEvaluationForm" value="${status.value}"/>
				<input type="checkbox" name="defaultEvaluationForm" value="true"  id="defaultEvaluationForm" 
					<c:if test="${status.value}">checked</c:if> onclick="defaultFormClicked(this.checked, 'defaultEvaluationFormSpan', 'cellEvaluationFormSpan');document.forms[0].hiddenDefaultEvaluationForm.value=this.checked;" 
					<c:if test="${evaluationFormUsed}"><c:out value="${localDisabledText}"/></c:if>  
				/>
				<label for="defaultEvaluationForm" ><fmt:message key="defaultEvaluationFormText"/></label> 
			</spring:bind>
		
		
		
		
			<!-- Evaluation Form Default Area Start-->
			<span name="defaultEvaluationFormSpan" id="defaultEvaluationFormSpan" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultEvaluationForm}">style='display:none' </c:if>>
				<spring:bind path="scaffoldingCell.scaffolding.evaluationDevice">
					<c:if test="${status.value == null}">
						<p class="indnt1"> 
							<span class="highlight"><fmt:message key="addForms_instructions_noforms" /></span>
						</p>
					</c:if>
						
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<fmt:message key="table_forms_summary"/>" style="width:50%">
						<c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
							<c:if test="${status.value==evalDev.id}">
								<tr>
									<td>
										<span class="indnt1">
											<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
											<c:out value="${evalDev.name}"/>
										</span>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>						
				</spring:bind>
			</span>
			
			<!-- Evaluation Form Default Area End -->	
		
		<!-- this case is currently only needed for matrices -->
		</c:if>	
		
		
		<!-- Evaluation Form Cell Area Start -->
		<span name="cellEvaluationFormSpan" id="cellEvaluationFormSpan" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultEvaluationForm}">style='display:none' </c:if>>
			<div id="evaluatorsDiv">  
				<p class="indnt1">
					<fmt:message key="evaluation_select_instructions"/>
				</p>
				<spring:bind path="scaffoldingCell.evaluationDevice">  
					<c:if test="${status.error}">
				<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>
					<p class="shorttext">
						<label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectEvaluationDevice"/></label>    
						<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
							<c:if test="${not empty status.value}"> <c:if test="${evaluationFormUsed}"><c:out value="${localDisabledText}"/></c:if> </c:if>>
							<option onclick="document.forms[0].evaluationDeviceType.value='';" value=""><fmt:message key="select_item_text" /></option>
							<c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
								<option onclick="document.forms[0].evaluationDeviceType.value='<c:out value="${evalDev.type}"/>';" 
								value="<c:out value="${evalDev.id}"/>" <c:if test="${status.value==evalDev.id}"> selected="selected"</c:if>><c:out value="${evalDev.name}"/></option>
							</c:forEach>
						</select>
					</p>
				</spring:bind>
			</div>
		</span>		
		<!-- Evaluation Form Cell Area End -->		
				
				
				
				
				
		<!--  Evaluator List Area Start --->
		<h5><fmt:message key="label_evaluators"/></h5>
		
		
		<!-- this case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null}">


			<!-- ************* Default Matrix Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultEvaluators">  			   
				<input type="checkbox" name="defaultEvaluators" value="true"  id="defaultEvaluators" 
				<c:if test="${status.value}">checked</c:if> onclick="defaultFormClicked(this.checked, 'defaultEvaluatorsSpan', 'cellEvaluatorsSpan');" />
				<label for="defaultEvaluators" ><fmt:message key="defaultEvaluatorsText"/></label> 
			</spring:bind>
			
			<!-- Evaluator List Default Area Start-->
			<span name="defaultEvaluatorsSpan" id="defaultEvaluatorsSpan" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultEvaluators}">style='display:none' </c:if>>
				<c:if test="${not empty defaultEvaluators}">
					<ol>
						<c:forEach var="eval" items="${defaultEvaluators}">
							<li><c:out value="${eval}" /></li>
						</c:forEach>
					</ol>
				</c:if>	
				<c:if test="${empty defaultEvaluators}">
					<p class="indnt1">
						<span class="highlight"><fmt:message key="no_evaluators2"/></span>
					</p>			
				</c:if>
			
			</span>
			<!-- Evaluator List Default Area End -->
			
			
		<!-- this case is currently only needed for matrices -->
		</c:if>	
			
			
		<!-- Cell Evaluator List Start -->
		<span name="cellEvaluatorsSpan" id="cellEvaluatorsSpan" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultEvaluators}">style='display:none' </c:if>>
			<c:if test="${not empty evaluators}">
				<ol>
					<c:forEach var="eval" items="${evaluators}">
						<li><c:out value="${eval}" /></li>
					</c:forEach>
				</ol>
			</c:if>	
			<p class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
					<osp:message key="select_evaluators"/>
				</a>	 
				<c:if test="${empty evaluators}">
					&nbsp;<fmt:message key="no_evaluators"/>
				</c:if>
			</p>
		
		</span>
		<!-- Cell Evaluator List End -->

	</fieldset>



	<spring:bind path="scaffoldingCell.id">
		<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
		<span class="error" style="border:none"><c:out value="${status.errorMessage}"/></span>
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
	
<form name="cancelForm" method="post">
	<osp:form/>

	<input type="hidden" name="validate" value="false" />
	<input type="hidden" name="canceling" value="true" />
</form>