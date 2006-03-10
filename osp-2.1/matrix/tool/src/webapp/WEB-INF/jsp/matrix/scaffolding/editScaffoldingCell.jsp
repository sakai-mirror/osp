<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<form method="POST">
  
    
	 <div class="navIntraTool">
       <a href="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].submit();"><osp:message key="select_evaluators" /></a>
	 </div>

    <h3><fmt:message key="${pageTitleKey}" /></h3>

	<div class="instruction"> 
          <fmt:message key="${pageInstructionsKey}"/>
          <fmt:message key="instructions_requiredFields"/> 
          <br/><br/>
	      <c:if test="${scaffoldingCell.scaffolding.published}">
			  <fmt:message key="instructions_hasBeenPublished"/>
		      <c:set var="localDisabledText" value="disabled=\"disabled\""/>
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
    

      <c:if test="${empty helperPage}">
          <table class="itemSummary" cellspacing="0">
             <tr><th><c:out value="${scaffoldingCell.scaffolding.columnLabel}"/>: </th><td><c:out value="${scaffoldingCell.level.description}"/></td></tr>
             <tr><th><c:out value="${scaffoldingCell.scaffolding.rowLabel}"/>: </th><td><c:out value="${scaffoldingCell.rootCriterion.description}"/></td></tr>
          </table>
      </c:if>

        <spring:bind path="scaffoldingCell.wizardPageDefinition.title">
          <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
          </c:if>
		  <p class="shorttext indnt2">
				<span class="reqStar">*</span><label><fmt:message key="label_cellTitle"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"
					   value="<c:out value="${status.displayValue}"/>" size="40"/>
		    </p>
        </spring:bind>
        
        <p class="longtext indnt2">
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
      </p>
      
      <spring:bind path="scaffoldingCell.initialStatus">  
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
          <p class="shorttext">
            <span class="reqStar">*</span>
            <label><fmt:message key="label_initialStatus"/></label>     
               <select name="<c:out value="${status.expression}"/>" >
                  <option value="READY" <c:if test="${status.value=='READY'}"> selected</c:if>>Ready</option>
                  <option value="LOCKED" <c:if test="${status.value=='LOCKED'}"> selected</c:if>>Locked</option>
               </select>
          </p>
      </spring:bind>
      
     <!-- ************* Style Area Start ************* -->
         <p class="shorttext">
            <label><fmt:message key="style_section_header"/></label>    

      
         <c:if test="${empty scaffoldingCell.wizardPageDefinition.style}">
            <input name="styleName" value="<c:out value="" />" />
            <a href="javascript:document.forms[0].dest.value='stylePickerAction';
            document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='stylePickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:styleReturnView=<c:out value="${styleReturnView}" />';
            document.forms[0].submit();">
            <osp:message key="select_style" /></a>
         </c:if>
         <c:if test="${not empty scaffoldingCell.wizardPageDefinition.style}">
            <c:set value="${scaffoldingCell.wizardPageDefinition.style}" var="style" />
            <input name="styleName" value="<c:out value="${style.name}" />" />
            <a href="javascript:document.forms[0].dest.value='stylePickerAction';
            document.forms[0].submitAction.value='forward';
            document.forms[0].params.value='stylePickerAction=true:currentStyleId=<c:out value="${style.id}"/>:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:styleReturnView=<c:out value="${styleReturnView}" />';
            document.forms[0].submit();">
            <osp:message key="change_style" /></a>
         </c:if>
         </p>
   <!-- ************* Style Area End ************* -->

   <!-- ************* Guidance and reflection Area Start ************* -->        
      <h4><osp:message key="guidance_header"/></h4>
      <c:if test="${empty scaffoldingCell.guidance}">
         <a href="javascript:document.forms[0].dest.value='createGuidance';
                        document.forms[0].submitAction.value='forward';
                        document.forms[0].submit();">
         <osp:message key="create_guidance"/></a>
      </c:if>
      <c:if test="${not empty scaffoldingCell.guidance}">
      <c:out value="${scaffoldingCell.guidance.description}" />
         <div class="itemAction">
             <a href="javascript:document.forms[0].dest.value='createGuidance';
               document.forms[0].submitAction.value='forward';
               document.forms[0].params.value='id=<c:out value="${scaffoldingCell.guidance.id}"/>';
               document.forms[0].submit();">
                 <osp:message key="edit"/>
                 </a> | 
             <a href="javascript:document.forms[0].dest.value='deleteGuidance';
               document.forms[0].submitAction.value='forward';
               document.forms[0].params.value='id=<c:out value="${scaffoldingCell.guidance.id}"/>';
               document.forms[0].submit();">
                 <osp:message key="delete"/>
                 </a>
         </div>
      </c:if>
      
            <spring:bind path="scaffoldingCell.reflectionDeviceType">  
            <input type="hidden" name="<c:out value="${status.expression}"/>"
               value="<c:out value="${status.value}"/>" />
        </spring:bind>
        
      <spring:bind path="scaffoldingCell.reflectionDevice">  
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
          <p class="shorttext">
            <label><fmt:message key="label_selectReflectionDevice"/></label>    
               <select name="<c:out value="${status.expression}"/>" <c:out value="${localDisabledText}"/>>
                     <option onclick="document.forms[0].reflectionDeviceType.value='';" value="">None</option>
                  <c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].reflectionDeviceType.value='<c:out value="${refDev.type}"/>');" 
                        value="<c:out value="${refDev.id}"/>" <c:if test="${status.value==refDev.id}"> selected</c:if>><c:out value="${refDev.name}"/></option>
                  </c:forEach>
               </select>
          </p>
        </spring:bind>
   <!-- ************* Guidance and reflection Area End ************* -->        
   
      <!-- ************* Review and Evaluation Area Start ************* -->            
		
   <h4 style="cursor:pointer" onclick="javascript:showHideDiv('evaluatorsDiv','/osp-jsf-resource')">
      <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgevaluatorsDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
      <fmt:message key="label_Evaluators"/></h4>
   <div id="evaluatorsDiv">  
   
   <spring:bind path="scaffoldingCell.reviewDeviceType">  
            <input type="hidden" name="<c:out value="${status.expression}"/>"
               value="<c:out value="${status.value}"/>" />
        </spring:bind>   
     <spring:bind path="scaffoldingCell.reviewDevice">  
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
       <p class="shorttext">
         <label><fmt:message key="label_selectReviewDevice"/></label>    
            <select name="<c:out value="${status.expression}"/>" <c:out value="${localDisabledText}"/>>
                     <option onclick="document.forms[0].reviewDeviceType.value='';" value="">None</option>
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
     <spring:bind path="scaffoldingCell.evaluationDevice">  
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
       <p class="shorttext">
         <label><fmt:message key="label_selectEvaluationDevice"/></label>    
            <select name="<c:out value="${status.expression}"/>" <c:out value="${localDisabledText}"/>>
                     <option onclick="document.forms[0].evaluationDeviceType.value='';" value="">None</option>
                  <c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].evaluationDeviceType.value='<c:out value="${evalDev.type}"/>';" 
                        value="<c:out value="${evalDev.id}"/>" <c:if test="${status.value==evalDev.id}"> selected</c:if>><c:out value="${evalDev.name}"/></option>
                  </c:forEach>
               </select>
       </p>
     </spring:bind>
   
   
      <c:forEach var="eval" items="${evaluators}">
         <div class="indnt1"><c:out value="${eval}" /></div>
      </c:forEach>
   </div>
   
      <!-- ************* Review and Evaluation Area End ************* -->
      
   <!-- ************* Additional Forms Area Start ************* -->   
        
      <h4><fmt:message key="title_additionalForms"/></h4>

      <p class="shorttext">
         <label><fmt:message key="label_selectForm"/></label>    
         <select name="selectAdditionalFormId" >
            <option value="" selected>None</option>
            <c:forEach var="addtlForm" items="${additionalFormDevices}" varStatus="loopCount">
               <option value="<c:out value="${addtlForm.id}"/>">
                  <c:out value="${addtlForm.name}"/></option>
            </c:forEach>
         </select>
         <span class="act">
            <input type="submit" name="addForm" value="<fmt:message key="button_add"/>" class="active" onclick="javascript:document.forms[0].validate.value='false';" />
         </span>
      </p>
      
      <c:forEach var="chosenForm" items="${selectedAdditionalFormDevices}">
      <c:out value="${chosenForm.name}" />
         <div class="itemAction">
             <a href="javascript:document.forms[0].submitAction.value='removeFormDef';
               document.forms[0].params.value='id=<c:out value="${chosenForm.id}"/>';
               document.forms[0].submit();">
                 <osp:message key="remove"/>
                 </a>
         </div>
      
      </c:forEach>
        
      <!-- ************* Additional Forms Area End ************* -->   
      
	<spring:bind path="scaffoldingCell.id">
		<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
		<span class="error_message"><c:out value="${status.errorMessage}"/></span>
	</spring:bind>

	<div class="act">
		<input type="submit" name="saveAction" value="<osp:message key="save"/>" class="active" onclick="javascript:document.forms[0].validate.value='true';" />

      <c:if test="${empty helperPage}">
         <input type="button" name="action" value="<osp:message key="cancel"/>"
            onclick="window.document.location='<osp:url value="viewScaffolding.osp?scaffolding_id=${scaffoldingCell.scaffolding.id}"/>'"/>
      </c:if>
      <c:if test="${not empty helperPage}">
   		<input type="submit" name="action" value="<osp:message key="cancel"/>" class="active"
            onclick="javascript:document.forms[0].validate.value='false';document.forms[0].canceling.value='true'" />
         <input type="hidden" name="canceling" value="" />
      </c:if>

	</div>
   
   <script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
    <script type="text/javascript" defer="1">chef_setupformattedtextarea('descriptionTextArea');</script>
    
</form>
