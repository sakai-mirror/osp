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
	</c:if>
	<c:if test="${not empty scaffolding.title}">
	<h3><fmt:message key="title_scaffoldingReviseProp"/></h3>
	</c:if>
	<div class="instruction">
	  <fmt:message key="instructions_scaffolding"/>
	  <fmt:message key="instructions_requiredFields"/>
	</div>
	
	<c:if test="${scaffolding.published}">
	<c:if test="${isMatrixUsed}" >
	  <fmt:message key="instructions_hasBeenUsed"/>
	</c:if>
	<c:if test="${!isMatrixUsed}" >
	  <fmt:message key="instructions_hasBeenPublished"/>
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
		  <c:forTokens var="token" items="scaffolding.readyColor,scaffolding.pendingColor,scaffolding.completedColor,scaffolding.lockedColor"
						delims=",">
			<spring:bind path="${token}">
			   <c:if test="${status.error}">
				   <div class="validation"><c:out value="${status.errorMessage}"/></div>
			   </c:if>
			   	<p class="shorttext">
				   <span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><osp:message key="${status.expression}_label"  /></label>
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
				<div class="validation"><c:out value="${status.errorMessage}"/></div>
			</c:if>
		 <div class="shorttext">
			<label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_columnLabel"/></label>
			<input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id"
					 value="<c:out value="${status.value}"/>"
				  size="25" maxlength="25" />
		 </div>
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
					<c:if test="${status.error}">
					   <span span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
					</c:if>
				</div>
			</c:if>	
			<c:if test="${!(empty scaffolding.levels)}">
				<table class="listHier lines nolines" cellspacing="0" border="0" style="width:70%" summary="<fmt:message key="table_summary_cols"/>">
					<thead>
						<tr>
							<th scope="col" colspan="2"><fmt:message key="table_header_name"/></th>
							<th scope="col" style="text-align:right">
								<span class="itemAction">
									<a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
										<fmt:message key="action_addColumn"/>
									</a>
								</span>
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
										<div class="colorBoxNone"><fmt:message key="swatch_text_none"/></div>
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
			<div class="shorttext">
				<label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_rowLabel"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id"
						 value="<c:out value="${status.value}"/>"
					  size="25" maxlength="25" />
				 <c:if test="${status.error}">
					<span class="alertMessageInline"><c:out value="${status.errorMessage}"/></span>
				 </c:if>
			</div>
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
					<fmt:message key="action_first_addRow"/></a>
					<c:if test="${status.error}">
						<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
					</c:if>
				</div>
				
			</c:if>
			<c:if test="${!(empty scaffolding.criteria)}">
				<table class="listHier lines nolines" cellspacing="0" border="0" style="width:70%" summary="<fmt:message key="table_summary_rows"/>">
					<thead>
						<tr>
							<th scope="col" colspan="2"><fmt:message key="table_header_name"/></th>
							<th style="text-align:right"><span class="itemAction"> <a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].onsubmit();document.forms[0].submit();">
								<fmt:message key="action_addRow"/></a></span>
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
											<div class="colorBoxNone"><fmt:message key="swatch_text_none"/></div>
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

	<!--  ********** Reviewer Group Access Start ************-->
	<c:if test="${not ignoreReviewerGroups}">
		<fieldset class="fieldsetVis">
			<legend><osp:message key="matrix_group_access"/></legend>
			<spring:bind path="scaffolding.reviewerGroupAccess">
				<c:forTokens var="token" items="normal,unrestricted" delims="," varStatus="loopCount">
					<div class="checkbox indnt1">
						<input type="radio" id="<c:out value="${token}" />" name="<c:out value="${status.expression}"/>" value="<c:out value="${loopCount.index}" />"
							<c:if test="${status.value == loopCount.index}"> checked="checked" </c:if>
						/>
						<label for="<c:out value="${token}" />">
							<osp:message key="${token}_group_access_label" />
							<osp:message key="${token}_group_access_desc" />
						</label>
					</div>
				</c:forTokens>
			</spring:bind>
		</fieldset>
	</c:if>
	<!--  ********** Reviewer Group Access End ************-->

	<!--  ********** Feedback Options Start ************-->
	<fieldset class="fieldsetVis">
		<legend><osp:message key="feedback_options"/></legend>
		<table cellspacing="0" border="0" summary="<fmt:message key="feedback_options"/>">
		<thead>
		<tr>
		<th><h4><fmt:message key="feedback_options_gen"/></h4></th>
		<th><h4><fmt:message key="feedback_options_item"/></h4></th>
		</tr>
		</thead>
		<tbody><tr>
		<td>
		<spring:bind path="scaffolding.generalFeedbackOption">
			<c:forTokens var="token" items="open,single,none" delims="," varStatus="loopCount">
				<div class="checkbox indnt1">
					<input type="radio" id="<c:out value="feedback_option_gen_${token}" />" 
						name="<c:out value="${status.expression}"/>" value="<c:out value="${loopCount.index}" />"
						<c:if test="${status.value == loopCount.index}"> checked="checked" </c:if>
						<c:out value="${disabledText}"/>
					/>
					<label for="<c:out value="feedback_option_gen_${token}" />">
						<osp:message key="feedback_option_gen_${token}" />
					</label>
				</div>
			</c:forTokens>
		</spring:bind>
		</td><td>
		<spring:bind path="scaffolding.itemFeedbackOption">
			<c:forTokens var="token" items="open,single,none" delims="," varStatus="loopCount">
				<div class="checkbox indnt1">
					<input type="radio" id="<c:out value="feedback_option_item_${token}" />" 
						name="<c:out value="${status.expression}"/>" value="<c:out value="${loopCount.index}" />"
						<c:if test="${status.value == loopCount.index}"> checked="checked" </c:if>
						<c:out value="${disabledText}"/>
					/>
					<label for="<c:out value="feedback_option_item_${token}" />">
						<osp:message key="feedback_option_item_${token}" />
					</label>
				</div>
			</c:forTokens>
		</spring:bind>
		</td>
		</tr><tbody>
		</table>
	</fieldset>
	<!--  ********** Feedback Options End ************-->

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
		<input type="submit" name="cancelAction" value="<osp:message key="button_cancel"/>" accesskey="x"/>
	</div>

	<osp:richTextWrapper textAreaId="descriptionTextArea" />
</form>
<script type="text/javascript">
	function resetColor(element, value) {
		if (value != '')
			element.style.backgroundColor=value;
	
	}
</script>