<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<form method="post">
    <%--<form method="POST" name="wizardform" onsubmit="return true;"> --%>

    <osp:form/>
    <input type="hidden" name="params" value="" />
    <input type="hidden" name="submitAction" value="" />
    <input type="hidden" name="dest" value="" />
    <input type="hidden" name="finalDest" value="" />
    <input type="hidden" name="label" value="" />
    <input type="hidden" name="displayText" value="" />
    <input type="hidden" name="validate" value="false" />
	<c:if test="${empty scaffolding.title}">
		<h3><fmt:message key="title_scaffoldingAdd"/></h3>
		<div class="instruction">
		  <fmt:message key="instructions_scaffoldingAdd"/>
		</div>
	</c:if>
	<c:if test="${not empty scaffolding.title}">
		<h3><fmt:message key="title_scaffoldingReviseProp"/></h3>
		<div class="instruction">
		  <fmt:message key="instructions_scaffoldingEdit"/>
		</div>
	</c:if>
	
	
	<c:if test="${scaffolding.published}">
	<c:if test="${isMatrixUsed}" >
		<span  class="instruction">
	  		<fmt:message key="instructions_hasBeenUsed"/>
	  	</span>
	</c:if>
	<c:if test="${!isMatrixUsed}" >
		<span  class="instruction">
	  		<fmt:message key="instructions_hasBeenPublished"/>
	  	</span>
	</c:if>
	  <c:set var="disabledText" value="disabled=\"disabled\""/>
	</c:if>

	<spring:hasBindErrors name="entry">
	  <div class="validation"><fmt:message key="error_problemWithSubmission"/></div>
	</spring:hasBindErrors>
	<!-- ************* Matrix Info  Area Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="title_generalScaffoldInfo"/></legend>
		<spring:bind path="scaffolding.title">
			<c:if test="${status.error}">
				<p class="shorttext validFail">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext">
			</c:if>
			<span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_title"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id"
					   value="<c:out value="${status.value}"/>"
					   size="25" maxlength="25" <c:out value="${disabledText}"/> />
				<c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
			</c:if>
			</p>
		</spring:bind>

		<p class="longtext">
			<label class="block"><fmt:message key="label_description"/></label>
			<spring:bind path="scaffolding.description">
				<table><tr>
				<td><textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80"><c:out value="${status.value}"/></textarea></td>
				</tr></table>
				<c:if test="${status.error}">
				<div class="validation"><c:out value="${status.errorMessage}"/></div>
			</c:if>
			</spring:bind>
		</p>
	</fieldset>	
	<!-- ************* Matrix Info  Area End ************* -->
	
	<!-- ************* Color and Style Area Start ************* -->
	<fieldset class="fieldsetVis">
	  	<legend><fmt:message key="matrix_colors_and_style"/></legend>
		 <p class="shorttext">
			<label for="styleName-id"><fmt:message key="style_section_header"/></label>


			 <c:if test="${empty scaffolding.style}">
				<input type="text"  name="styleName" id="styleName-id" value="<c:out value="" />" />
				<a href="javascript:document.forms[0].dest.value='scaffoldingStylePickerAction';
				document.forms[0].submitAction.value='forward';
				document.forms[0].params.value='stylePickerAction=true:scaffolding_id=<c:out value="${scaffolding.id}" />';
				document.forms[0].onsubmit();
				document.forms[0].submit();">
				<osp:message key="select_style" /></a>
			 </c:if>
			 <c:if test="${not empty scaffolding.style}">
				<c:set value="${scaffolding.style}" var="style" />
				<input type="text"  name="styleName" id="styleName-id" value="<c:out value="${style.name}" />" />
				<a href="javascript:document.forms[0].dest.value='scaffoldingStylePickerAction';
				document.forms[0].submitAction.value='forward';
				document.forms[0].params.value='stylePickerAction=true:currentStyleId=<c:out value="${style.id}"/>:scaffolding_id=<c:out value="${scaffolding.id}" />';
				document.forms[0].onsubmit();
				document.forms[0].submit();">
				<osp:message key="change_style" /></a>
			 </c:if>
		 </p>
		  <h4><fmt:message key="title_matrixStatusColors"/></h4>
		  <c:forTokens var="token" items="scaffolding.readyColor,scaffolding.pendingColor,scaffolding.completedColor,scaffolding.lockedColor,scaffolding.returnedColor"
						delims=",">
			<spring:bind path="${token}">
			   <c:if test="${status.error}">
				   <div class="validation"><c:out value="${status.errorMessage}"/></div>
			   </c:if>
			   	<p class="shorttext">
				   <label for="<c:out value="${status.expression}"/>-id"><osp:message key="${status.expression}_label"  /></label>
				   <c:choose>
						<c:when test="${status.expression == 'readyColor'}">
							<c:set var="styleColor" value="matrix-READY" />
						</c:when>
						<c:when test="${status.expression == 'pendingColor'}">
							<c:set var="styleColor" value="matrix-PENDING" />
						</c:when>
						<c:when test="${status.expression == 'completedColor'}">
							<c:set var="styleColor" value="matrix-COMPLETE" />
						</c:when>
						<c:when test="${status.expression == 'lockedColor'}">
							<c:set var="styleColor" value="matrix-LOCKED" />
						</c:when>
						<c:when test="${status.expression == 'returnedColor'}">
							<c:set var="styleColor" value="matrix-RETURNED" />
						</c:when>
				   </c:choose>
				   <input type="text" disabled="disabled" value="" size="2" class="<c:out value="${styleColor}"/>"
							name="<c:out value="${status.expression}"/>_sample"
							<c:if test="${status.value != ''}">
							style="background-color: <c:out value="${status.value}"/>" 
							</c:if>
							/>
				   <input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id"
							value="<c:out value="${status.value}"/>"
						 size="25" maxlength="25"
						   onchange="resetColor(document.forms[0].elements['<c:out value="${status.expression}"/>_sample'], document.forms[0].elements['<c:out value="${status.expression}"/>'].value);"/>
				   <!--
					  Put icon by the input control.
					  Make it the link calling picker popup.
					  Specify input object reference as first parameter to the function and palete selection as second.
				   -->
				   <a href="javascript:TCP.popup(document.forms[0].elements['<c:out value="${status.expression}"/>'])" title="<fmt:message key="color_picker_linktitle_status"/>">
				   <img width="15" height="13" border="0" alt="<fmt:message key="color_picker_linktitle"/>" src="<osp:url value="/js/colorPicker/img/sel.gif"/>" /></a>
				</p>
			   </spring:bind>
		  </c:forTokens>
	</fieldset> 
		 
   <!-- ************* Color and Style Area End ************* -->

   <!-- ************* Structure Area Start ************* -->   
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="matrix_structure"/></legend>
		<h4><fmt:message key="title_columns"/>
	  </h4>
	  <spring:bind path="scaffolding.columnLabel">
		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>	
		<c:if test="${!status.error}">
			<p class="shorttext">
		</c:if>
			<label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_columnLabel"/></label>
			<input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id"
					 value="<c:out value="${status.value}"/>"
				  size="25" maxlength="25" />
			<c:if test="${status.error}">
				<span class="alertMessageInline"><c:out value="${status.errorMessage}"/></span>
			</c:if>
		 </p>
		</spring:bind>

	
		<spring:bind path="scaffolding.levels">
			<c:if test="${(empty scaffolding.levels)}">
			<c:if test="${status.error}">
				<div class="indnt1 highlight">
			</c:if>	
			<c:if test="${!status.error}">
				<div class="instruction indnt1">
			</c:if>
				<span class="reqStarInline">*</span>
					<fmt:message key="no_cols_created_message"/>&nbsp;&nbsp;
				
						<a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
							<fmt:message key="action_first_addColumn"/>
						</a>
				&nbsp;&nbsp;
					
					<c:if test="${status.error}">
					   <span span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
					</c:if>
				</div>
			</c:if>	
			<c:if test="${!(empty scaffolding.levels)}">
				<span class="itemAction">
					<a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
						<fmt:message key="action_addColumn"/>
					</a>
				</span>
				<table class="listHier lines nolines" cellspacing="0" border="0" style="width:70%" summary="<fmt:message key="table_summary_cols"/>">
					<thead>
						<tr>
							<th scope="col" colspan="2"><fmt:message key="table_header_name"/></th>
							<th scope="col">
								<fmt:message key="actions"/>
							</th>
						</tr>
					</thead>
					<tbody>
	
						<c:forEach var="level" items="${scaffolding.levels}" varStatus="itemLoopStatus">
							<tr>
							<td>
								<span class="matrixColumnDefault">
									<c:out value="${level.description}"/>
								</span>
							</td>
							<td>
									<c:if test="${not empty level.color}">
										<div class="colorBox"  style="background-color: <c:out value="${level.color}"/>;color: <c:if test="${not empty level.textColor}" ><c:out value="${level.textColor}"/></c:if>"><fmt:message key="swatch_text"/></div>
									</c:if>
									<c:if test="${ empty level.color}">
										<div class="colorBox"><fmt:message key="swatch_text_none"/></div>
									</c:if>				
							</td>
							<td style="white-space:nowrap">
									<span class="itemAction">
									 <a href="javascript:document.forms[0].dest.value='addLevel';
									  document.forms[0].submitAction.value='forward';
									  document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>';
									document.forms[0].onsubmit();
									  document.forms[0].submit();">
										 <fmt:message key="table_action_edit"/>
								   </a>
								 <c:if test="${!isMatrixUsed}" >
									 | <a href="javascript:document.forms[0].dest.value='removeLevCrit';
									  document.forms[0].finalDest.value='deleteLevel';
									  document.forms[0].label.value=document.forms[0].columnLabel.value;
									  document.forms[0].displayText.value='<c:out value="${level.description}"/>';
									  document.forms[0].submitAction.value='forward';
									  document.forms[0].params.value='level_id=<c:out value="${level.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
									document.forms[0].onsubmit();
									  document.forms[0].submit();">
										 <fmt:message key="table_action_remove"/>
								   </a>
								 </c:if>
									 | <a href="javascript:document.forms[0].dest.value='moveLevel';
									  document.forms[0].submitAction.value='forward';
									  document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>';
									document.forms[0].onsubmit();
									  document.forms[0].submit();">
										 <fmt:message key="table_action_up"/>
								   </a>
									 | <a href="javascript:document.forms[0].dest.value='moveLevel';
									  document.forms[0].submitAction.value='forward';
									  document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>';
									document.forms[0].onsubmit();
									  document.forms[0].submit();">
										 <fmt:message key="table_action_down"/>
								   </a>
								</span>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>	
	   </spring:bind>
	
	
		<h4><fmt:message key="title_rows"/></h4>
	  <spring:bind path="scaffolding.rowLabel">
			<c:if test="${status.error}">
				<p class="shorttext validFail">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext">
			</c:if>
				<label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_rowLabel"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id"
						 value="<c:out value="${status.value}"/>"
					  size="25" maxlength="25" />
				
				 <c:if test="${status.error}">
					<span class="alertMessageInline"><c:out value="${status.errorMessage}"/></span>
				 </c:if>
			</p>
		</spring:bind>
	
		<spring:bind path="scaffolding.criteria">
			<c:if test="${(empty scaffolding.criteria)}">
			<c:if test="${status.error}">
				<div class="indnt1 highlight">
			</c:if>	
			<c:if test="${!status.error}">
				<div class="instruction indnt1">
			</c:if>
				<span class="reqStarInline">*</span>
					<fmt:message key="no_rows_created_message"/>&nbsp;&nbsp;
					
					<a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].onsubmit();document.forms[0].submit();">
						<fmt:message key="action_first_addRow"/></a>&nbsp;&nbsp;
			
					<c:if test="${status.error}">
						<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
					</c:if>
				</div>
				
			</c:if>
			<c:if test="${!(empty scaffolding.criteria)}">
				<span class="itemAction"> 
					<a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].onsubmit();document.forms[0].submit();">
						<fmt:message key="action_addRow"/></a>
				</span>
				<table class="listHier lines nolines" cellspacing="0" border="0" style="width:70%" summary="<fmt:message key="table_summary_rows"/>">
					<thead>
						<tr>
							<th scope="col" colspan="2"><fmt:message key="table_header_name"/></th>
							<th>
								<fmt:message key="actions"/>
							</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="criterion" items="${scaffolding.criteria}" varStatus="itemLoopStatus">
							<tr>
								<td>
										<span class="matrixRowDefault">
											<c:out value="${criterion.description}"/>
										</span>
								</td>
								<td>	
										<c:if test="${not empty criterion.color}">
											<div class="colorBox"
													style="background-color: <c:out value="${criterion.color}"/>;
													color: <c:if test="${not empty criterion.textColor}" >
														<c:out value="${criterion.textColor}"/>
													</c:if>"><fmt:message key="swatch_text"/></div>
										</c:if>
										<c:if test="${ empty criterion.color}">
											<div class="colorBox"><fmt:message key="swatch_text_none"/></div>
										</c:if>
	
										</td>
								<td style="white-space:nowrap">
									<span class="itemAction">
										  <a href="javascript:document.forms[0].dest.value='addCriterion';
										  document.forms[0].submitAction.value='forward';
										  document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>:path=';
										  document.forms[0].onsubmit();
										  document.forms[0].submit();">
											  <fmt:message key="table_action_edit"/>
										  </a>
					
										 <c:if test="${!isMatrixUsed}" >
										  | <a href="javascript:document.forms[0].dest.value='removeLevCrit';
										  document.forms[0].finalDest.value='deleteCriterion';
										  document.forms[0].label.value=document.forms[0].rowLabel.value;
										  document.forms[0].displayText.value='<c:out value="${criterion.description}"/>';
										  document.forms[0].submitAction.value='forward';
										  document.forms[0].params.value='criterion_id=<c:out value="${criterion.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
										  document.forms[0].onsubmit();
										  document.forms[0].submit();">
											  <fmt:message key="table_action_remove"/>
										  </a>
										 </c:if>
										  | <a href="javascript:document.forms[0].dest.value='moveCriterion';
										  document.forms[0].submitAction.value='forward';
										  document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>';
										  document.forms[0].onsubmit();
										  document.forms[0].submit();">
											  <fmt:message key="table_action_up"/>
										  </a>
										  | <a href="javascript:document.forms[0].dest.value='moveCriterion';
										  document.forms[0].submitAction.value='forward';
										  document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>';
										  document.forms[0].onsubmit();
										  document.forms[0].submit();">
											  <fmt:message key="table_action_down"/>
										  </a>
										</span>
									</td>
								</tr>
						</c:forEach>
	
					</tbody>
				</table>
			</c:if>	
		</spring:bind>
	</fieldset>	
	<!-- ************* Structure Area End ************* -->

	<!-- ************* Progression Area Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><osp:message key="matrix_progression"/></legend>
	<spring:bind path="scaffolding.workflowOption">
         <c:forTokens var="token" items="none,horizontal,vertical,open,manual"
                    delims="," varStatus="loopCount">
            <div class="checkbox indnt1">
            <input type="radio" id="<c:out value="${token}" />" name="<c:out value="${status.expression}"/>" value="<c:out value="${loopCount.index}" />"
               <c:if test="${status.value == loopCount.index}"> checked="checked" </c:if>
					<c:if test="${isMatrixUsed}"><c:out value="${disabledText}"/></c:if>
				/>
            <label for="<c:out value="${token}" />">
			   <osp:message key="${token}_progression_icon"  var="icon" />
               <c:if test="${not empty icon}" ><img src="<osp:url value="${icon}"  />" align="baseline" /></c:if>
			   <osp:message key="${token}_progression_label"  />
			   <osp:message key="${token}_progression_desc"  />
            </label>
         </div>
         </c:forTokens>
        </spring:bind>
  </fieldset>
  <!-- ************* Progression Area End ************* -->






	<!-- *************  User Forms Area  Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="legend_additional_user_Forms"/></legend>
		<p>
			<fmt:message key="info_defaultForms" />
		</p>
		<!-- ************* Additional Forms Area Start ************* -->   
		<h5><fmt:message key="title_additionalForms"/></h5>
		<p class="indnt1"> 
			<fmt:message key="addForms_instructions" />
		</p>

		<p class="shorttext">
			<label for="selectAdditionalFormId" ><fmt:message key="label_selectCustomForm"/></label>    
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
		<c:if test="${ empty selectedAdditionalFormDevices}">
			<div class="instruction indnt2">
				<fmt:message key="addForms_instructions_noforms" />
			</div>
		</c:if>
		<c:if test="${not empty selectedAdditionalFormDevices}">
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<fmt:message key="table_forms_summary"/>" style="width:50%">
				<c:forEach var="chosenForm" items="${selectedAdditionalFormDevices}">
					<tr>
						<td>
							<span class="indnt2">
								<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
								<c:out value="${chosenForm.name}" />
							</span>
							
							
							<c:if test="${empty localDisabledText}">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<span class="itemAction">
									<a href="javascript:document.forms[0].submitAction.value='removeFormDef';
										document.forms[0].params.value='id=<c:out value="${chosenForm.id}"/>';
										document.forms[0].onsubmit();
										document.forms[0].submit();">
										<osp:message key="remove"/>
									</a>
								</span>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	
		<!-- ************* Additional Forms Area End ************* -->   
 

		<!-- ************* Reflection Form Aream Start *********** -->
		<spring:bind path="scaffolding.reflectionDeviceType">  
			<input type="hidden" name="<c:out value="${status.expression}"/>"
			value="<c:out value="${status.value}"/>" />
		</spring:bind>
	
		<spring:bind path="scaffolding.reflectionDevice">  
			<c:if test="${status.error}">
				<div class="validation"><c:out value="${status.errorMessage}"/></div>
			</c:if>
			<h5><osp:message key="label_selectReflectionDevice"/></h5>
			<p class="indnt1">
				<fmt:message key="reflection_select_instructions"/>
			</p>	
			<p class="shorttext"> 
				<label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectReflectionDevice"/></label>    
				<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>" 
					<c:if test="${not empty status.value}"> <c:out value="${localDisabledText}"/> </c:if>>
					<option onclick="document.forms[0].reflectionDeviceType.value='';" value=""><fmt:message key="select_form_text" /></option>
					<c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
						<option onclick="document.forms[0].reflectionDeviceType.value='<c:out value="${refDev.type}"/>';" 
						value="<c:out value="${refDev.id}"/>" <c:if test="${status.value==refDev.id}"> selected="selected"</c:if>><c:out value="${refDev.name}"/></option>
					</c:forEach>
				</select>
			</p>
		</spring:bind>
		
		<!-- ************* Reflection Form Aream End *********** -->
	</fieldset>











	<!--  ********** Feedback start ************* -->
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="legend_feedback"/></legend>
		<p>
			<fmt:message key="info_defaultForms" />
		</p>
		<!-- ************* Feedback Area Start ************* -->   
		<spring:bind path="scaffolding.reviewDeviceType">  
			<input type="hidden" name="<c:out value="${status.expression}"/>"
			value="<c:out value="${status.value}"/>" />
		</spring:bind>   
		<spring:bind path="scaffolding.reviewDevice">  
			<c:if test="${status.error}">
				<div class="validation"><c:out value="${status.errorMessage}"/></div>
			</c:if>
			<h5> <osp:message key="label_selectReviewDevice"/></h5>
			<p class="indnt1">
				<fmt:message key="feedback_select_instructions"/>
			</p>	
			<p class="shorttext">
				<label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectReviewDevice"/></label>    
				<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
					<c:if test="${not empty status.value}"> <c:out value="${localDisabledText}"/> </c:if>>
					<option onclick="document.forms[0].reviewDeviceType.value='';" value=""><fmt:message key="select_form_text" /></option>
					<c:forEach var="reviewDev" items="${reviewDevices}" varStatus="loopCount">
						<option onclick="document.forms[0].reviewDeviceType.value='<c:out value="${reviewDev.type}"/>';" 
						value="<c:out value="${reviewDev.id}"/>" <c:if test="${status.value==reviewDev.id}"> selected="selected"</c:if>><c:out value="${reviewDev.name}"/></option>
					</c:forEach>
				</select>
			</p>
		</spring:bind>
		
		<spring:bind path="scaffolding.evaluationDeviceType">  
			<input type="hidden" name="<c:out value="${status.expression}"/>"
			value="<c:out value="${status.value}"/>" />
		</spring:bind>
		
		
		
		<!-- ************* Reviewers List Start ************* -->            
	
		<h5 style="display:inline"><fmt:message key="label_reviwers"/></h5>
		<c:if test="${empty reviewers}">
			<span class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='selectReviewers';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
						<osp:message key="add_reviewers"/>
				</a>
			</span>
		</c:if>
		<c:if test="${not empty reviewers}">
			<ol>
				<c:forEach var="reviwer" items="${reviewers}">
					<li><c:out value="${reviwer}" /></li>
				</c:forEach>
			</ol>
			<p class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='selectReviewers';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
					<osp:message key="select_reviewers"/>
				</a>	
			</p>
		</c:if>	
		<c:if test="${empty reviewers}">
			<div class="instruction indnt1">
				<fmt:message key="info_reviewersNone"/>
			</div>
		</c:if>
		<p class="indnt1">
			<spring:bind path="scaffolding.allowRequestFeedback">  	
				<span>		
					<input type="checkbox" name="allowRequestFeedback" value="true"  id="allowRequestFeedback" 
						<c:if test="${status.value}">
							checked
						</c:if> 
					 />				
					<label for="allowRequestFeedback" ><fmt:message key="allowRequestFeedback"/></label>
				</span>    
			</spring:bind>	
			</p>
		</p>
	<!-- ************* Reviewers List End ************* -->
		
		
		
		<!-- ************* Feedback Area End ************* -->   
		
	</fieldset>	
		
		
	<!--  ********** Evaluation start ************* -->
	<fieldset class="fieldsetVis">
		<legend><fmt:message key="legend_evaluation"/></legend>
		<p>
			<fmt:message key="info_defaultForms" />
		</p>
		
		
		
		<!-- ************* Review and Evaluation Area Start ************* -->            
		
		<h5><fmt:message key="header_Evaluators"/></h5>
		<div id="evaluatorsDiv">  
			<p class="indnt1">
				<fmt:message key="evaluation_select_instructions"/>
			</p>
			<spring:bind path="scaffolding.evaluationDevice">  
				<c:if test="${status.error}">
			<div class="validation"><c:out value="${status.errorMessage}"/></div>
			</c:if>
				<p class="shorttext">
					<label for="<c:out value="${status.expression}-id"/>"><fmt:message key="label_selectEvaluationDevice"/></label>    
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
						<c:if test="${not empty status.value}"> <c:out value="${localDisabledText}"/> </c:if>>
						<option onclick="document.forms[0].evaluationDeviceType.value='';" value=""><fmt:message key="select_form_text" /></option>
						<c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
							<option onclick="document.forms[0].evaluationDeviceType.value='<c:out value="${evalDev.type}"/>';" 
							value="<c:out value="${evalDev.id}"/>" <c:if test="${status.value==evalDev.id}"> selected="selected"</c:if>><c:out value="${evalDev.name}"/></option>
						</c:forEach>
					</select>
				</p>
			</spring:bind>
		</div>
	
		<!-- ************* Review and Evaluation Area End ************* -->
		
		
		<!-- ************* Evaluators List Start ************* -->            
	
		<h5  style="display:inline"><fmt:message key="label_evaluators"/></h5>
		<c:if test="${empty evaluators}">
			<span class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
					<osp:message key="add_evaluators"/>
				</a>
			</span>
		</c:if>
		<c:if test="${not empty evaluators}">
			<ol>
				<c:forEach var="eval" items="${evaluators}">
					<li><c:out value="${eval}" /></li>
				</c:forEach>
			</ol>
			<p class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();" >
					<osp:message key="select_evaluators"/>
				</a>	 
			</p>
		</c:if>	
		<c:if test="${empty evaluators}">
			<div class="instruction indnt1">
				<fmt:message key="no_evaluators"/>
			</div>
		</c:if>
		
	<!-- ************* Evaluators List End ************* -->
		
		
	</fieldset>	
	<!--  ********** Feedback and Evaluation end ************* -->


	<c:if test="${not empty isInSession}">
		<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
	</c:if>

	<div class="act">
		<input type="submit" name="generateAction" class="active" accesskey="s"
		<c:if test="${empty scaffolding.title}">
			value="<osp:message key="button_generateMatrix"  />"            
		</c:if>
		<c:if test="${not empty scaffolding.title}">
			value="<osp:message key="button_save"  />"
		</c:if>
		onclick="javascript:document.forms[0].validate.value='true';"/>
		<c:if test="${empty scaffolding.title}">
		 	<input type="submit" name="cancelAction" value="<osp:message key="button_cancel"/>" accesskey="x"/>
		</c:if>
		<c:if test="${not empty scaffolding.title}">
			<input type="submit" name="cancelActionExisting" value="<osp:message key="button_cancel"/>" accesskey="x"/>
		</c:if>
		
	</div>

	<osp:richTextWrapper textAreaId="descriptionTextArea" />
</form>
<script type="text/javascript">
	function resetColor(element, value) {
		if (value != '')
			element.style.backgroundColor=value;
	
	}
</script>