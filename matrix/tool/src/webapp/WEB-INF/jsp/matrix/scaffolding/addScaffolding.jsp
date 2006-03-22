<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<form method="POST">
    <c:if test="${!scaffolding.published}" >
	   <div class="navIntraTool">
			<a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
				<fmt:message key="action_addColumn"/>
			</a>
			<a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].onsubmit();document.forms[0].submit();">
				<fmt:message key="action_addRow"/>
			</a>
	   </div>
    </c:if>


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
		  <fmt:message key="instructions_scaffoldingPublisched"/>
		  <c:set var="disabledText" value="disabled=\"disabled\""/>
	  </c:if>

      <spring:hasBindErrors name="entry">
          <div class="validation"><fmt:message key="error_problemWithSubmission"/></div>
      </spring:hasBindErrors>


		<h4><fmt:message key="title_generalScaffoldInfo"/></h4>


        <spring:bind path="scaffolding.title">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
    		<p class="shorttext">
    			<span class="reqStar">*</span><label><fmt:message key="label_title"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"
                	   value="<c:out value="${status.value}"/>"
					   size="25" maxlength="25" <c:out value="${disabledText}"/>>
    		</p>
        </spring:bind>

		<p class="longtext">
			<label class="block"><fmt:message key="label_description"/></label>
			<spring:bind path="scaffolding.description">
                <table><tr>
				<td><textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80">
               <c:out value="${status.value}"/></textarea></td>
                </tr></table>
				<font color="red"><c:out value="${status.errorMessage}"/></font>
			</spring:bind>
		</p>

      <!-- ************* Style Area Start ************* -->
         <p class="shorttext">
            <label><fmt:message key="style_section_header"/></label>


         <c:if test="${empty scaffolding.style}">
            <input name="styleName" value="<c:out value="" />" />
            <a href="javascript:document.forms[0].dest.value='scaffoldingStylePickerAction';
            document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='stylePickerAction=true:scaffolding_id=<c:out value="${scaffolding.id}" />';
            document.forms[0].onsubmit();
            document.forms[0].submit();">
            <osp:message key="select_style" /></a>
         </c:if>
         <c:if test="${not empty scaffolding.style}">
            <c:set value="${scaffolding.style}" var="style" />
            <input name="styleName" value="<c:out value="${style.name}" />" />
            <a href="javascript:document.forms[0].dest.value='scaffoldingStylePickerAction';
            document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='stylePickerAction=true:currentStyleId=<c:out value="${style.id}"/>:scaffolding_id=<c:out value="${scaffolding.id}" />';
            document.forms[0].onsubmit();
            document.forms[0].submit();">
            <osp:message key="change_style" /></a>
         </c:if>
         </p>
   <!-- ************* Style Area End ************* -->

        <br />


		<h4><fmt:message key="title_columns"/>
         <c:if test="${!scaffolding.published}" >
         <a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
            <fmt:message key="action_addColumn"/>
         </a>
         </c:if>
      </h4>
      <spring:bind path="scaffolding.columnLabel">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
         <div class="shorttext">
            <label><fmt:message key="label_columnLabel"/></label>
            <input type="text" name="<c:out value="${status.expression}"/>"
                     value="<c:out value="${status.value}"/>"
                  size="25" maxlength="25">
         </div>
        </spring:bind>


        <spring:bind path="scaffolding.levels">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
    		<table class="listHier" cellspacing="0" border="0">
    			<thead>
    				<tr>
    					<th scope="col"><fmt:message key="table_header_name"/></th>
    				</tr>
    			</thead>
    			<tbody>

    				<c:forEach var="level" items="${scaffolding.levels}" varStatus="itemLoopStatus">
    					<tr>
                        <td <c:if test="${not empty level.color}">bgcolor="<c:out value="${level.color}"/>"</c:if>>
    							<div class="tier0">
    								<font color="<c:out value="${level.textColor}"/>"><c:out value="${level.description}"/></font>

    									<div class="itemAction">

                         <a href="javascript:document.forms[0].dest.value='addLevel';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>';
                        document.forms[0].onsubmit();
                	      document.forms[0].submit();">
                		     <fmt:message key="table_action_edit"/>
                	   </a>
    				<c:if test="${!scaffolding.published}" >
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
    								     </c:if>
    								 </div>
    							</div>
    						</td>
    				    </tr>
    			    </c:forEach>

    			</tbody>
    		</table>
        </spring:bind>


		<br />

		<h4><fmt:message key="title_rows"/>
         <c:if test="${!scaffolding.published}" >
         <a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].onsubmit();document.forms[0].submit();">
            <fmt:message key="action_addRow"/>
         </a>
         </c:if>
      </h4>
      <spring:bind path="scaffolding.rowLabel">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
            <div class="shorttext">
            <label><fmt:message key="label_rowLabel"/></label>
            <input type="text" name="<c:out value="${status.expression}"/>"
                     value="<c:out value="${status.value}"/>"
                  size="25" maxlength="25">
</div>
        </spring:bind>

        <spring:bind path="scaffolding.criteria">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
    		<table class="listHier" cellspacing="0">
    			<thead>
    				<tr>
    					<th scope="col"><fmt:message key="table_header_name"/></th>
    				</tr>
    			</thead>
    			<tbody>
					<c:forEach var="criterion" items="${scaffolding.criteria}" varStatus="itemLoopStatus">
							<tr>
								<td <c:if test="${not empty criterion.color}">bgcolor="<c:out value="${criterion.color}"/>"</c:if>>
									<div class="tier0">
										<font color="<c:out value="${criterion.textColor}"/>"><c:out value="${criterion.description}"/></font>
										<div class="itemAction">

                      <a href="javascript:document.forms[0].dest.value='addCriterion';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>:path=';
                      document.forms[0].onsubmit();
                      document.forms[0].submit();">
                          <fmt:message key="table_action_edit"/>
                      </a>

											<c:if test="${!scaffolding.published}" >
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
											</c:if>
										</div>
									</div>
								</td>
							</tr>
					</c:forEach>

    			</tbody>
    		</table>
        </spring:bind>

      <spring:bind path="scaffolding.workflowOption">
      <h4><osp:message key="matrix_progression"/></h4>
      <fieldset>
         <legend class="radio"><osp:message key="matrix_progression_text"/></legend>
         <c:forTokens var="token" items="none,horizontal,vertical,open,manual"
                    delims="," varStatus="loopCount">
            <div class="checkbox indnt1">
            <input type="radio" id="<c:out value="${token}" />" name="<c:out value="${status.expression}"/>" value="<c:out value="${loopCount.index}" />"
               <c:if test="${status.value == loopCount.index}"> checked="checked" </c:if>
               <c:out value="${disabledText}"/> />
            <label for="<c:out value="${token}" />"><osp:message key="${token}_progression_label"  />
               <osp:message key="${token}_progression_icon"  var="icon" />
               <c:if test="${not empty icon}" ><img src="<osp:url value="${icon}"/>" /></c:if>
               <osp:message key="${token}_progression_desc"  />
            </label>
         </div>
         </c:forTokens>
      </fieldset>
      </spring:bind>

      <h4><fmt:message key="title_matrixStatusColors"/></h4>
      <c:forTokens var="token" items="scaffolding.readyColor,scaffolding.pendingColor,scaffolding.completedColor,scaffolding.lockedColor"
                    delims=",">
        <spring:bind path="${token}">
               <c:if test="${status.error}">
                   <div class="validation"><c:out value="${status.errorMessage}"/></div>
               </c:if>
            <p class="shorttext">
               <span class="reqStar">*</span><label><osp:message key="${status.expression}_label"  /></label>
               <input type="text" disabled="disabled" value="" size="2"
                        name="<c:out value="${status.expression}"/>_sample"
                        style="background-color: <c:out value="${status.value}"/>" />
               <input type="text" name="<c:out value="${status.expression}"/>"
                        value="<c:out value="${status.value}"/>"
                     size="25" maxlength="25"
                     onchange="document.forms[0].elements['<c:out value="${status.expression}"/>_sample'].style.backgroundColor='' + document.forms[0].elements['<c:out value="${status.expression}"/>'].value">
               <!--
                  Put icon by the input control.
                  Make it the link calling picker popup.
                  Specify input object reference as first parameter to the function and palete selection as second.
               -->
               <a href="javascript:TCP.popup(document.forms[0].elements['<c:out value="${status.expression}"/>'])">
               <img width="15" height="13" border="0" alt="Click Here to Pick up the color" src="<osp:url value="/js/colorPicker/img/sel.gif"/>"></a>
            </p>
           </spring:bind>
      </c:forTokens>

		<br />
		<br />
		<c:if test="${not empty isInSession}">
			<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
		</c:if>

		<div class="act">
		<input type="submit" name="generateAction" class="active"
            <c:if test="${empty scaffolding.title}">
                value="<osp:message key="button_generateMatrix"  />"
            </c:if>
            <c:if test="${not empty scaffolding.title}">
                value="<osp:message key="button_save"  />"
            </c:if>
            onclick="javascript:document.forms[0].validate.value='true';"/>
			<input type="submit" name="cancelAction" value="<osp:message key="button_cancel"/>"/>
		</div>

    <script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
    <script type="text/javascript" defer="1">chef_setupformattedtextarea('descriptionTextArea');</script>

</form>
