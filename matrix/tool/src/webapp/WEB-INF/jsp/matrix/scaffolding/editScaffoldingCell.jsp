<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>

<form method="POST">
  
    
	 <div class="navIntraTool">
       <a href="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].submit();"><osp:message key="select_evaluators" bundle="${msgs}" /></a>
	 </div>

    <h3>Edit Cell Settings</h3>

	<div class="instruction"> 
          Cell Settings (required fields are noted with an <span class="reqStarInline">*</span>)
          <br/><br/>
	      <c:if test="${scaffoldingCell.scaffolding.published}">
			  Scaffolding has been published and only allows limited editing.
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
				<span class="reqStar">*</span><label>Title</label>
				<input type="text" name="<c:out value="${status.expression}"/>"
					   value="<c:out value="${status.displayValue}"/>"/>
		    </p>
        </spring:bind>

        <spring:bind path="scaffoldingCell.wizardPageDefinition.description">
          <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
          </c:if>
		  <p class="shorttext indnt2">
				<label>Description</label>
				<input type="text" name="<c:out value="${status.expression}"/>"
					   value="<c:out value="${status.displayValue}"/>"/>
		    </p>
        </spring:bind>

      <h4><osp:message key="guidance_header" bundle="${msgs}" /></h4>
      <c:if test="${empty scaffoldingCell.guidance}">
         <a href="javascript:document.forms[0].dest.value='createGuidance';
                        document.forms[0].submitAction.value='forward';
                        document.forms[0].submit();">
         <osp:message key="create_guidance" bundle="${msgs}" /></a>
      </c:if>
      <c:if test="${not empty scaffoldingCell.guidance}">
      <c:out value="${scaffoldingCell.guidance.description}" />
         <div class="itemAction">
             <a href="javascript:document.forms[0].dest.value='createGuidance';
               document.forms[0].submitAction.value='forward';
               document.forms[0].params.value='id=<c:out value="${scaffoldingCell.guidance.id}"/>';
               document.forms[0].submit();">
                 <osp:message key="edit" bundle="${msgs}" />
                 </a> | 
             <a href="javascript:document.forms[0].dest.value='deleteGuidance';
               document.forms[0].submitAction.value='forward';
               document.forms[0].params.value='id=<c:out value="${scaffoldingCell.guidance.id}"/>';
               document.forms[0].submit();">
                 <osp:message key="delete" bundle="${msgs}" />
                 </a>
         </div>
      </c:if>
      
    
		<h4><osp:message key="cell_settings_header" bundle="${msgs}" /></h4>
      
        <spring:bind path="scaffoldingCell.initialStatus">  
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
		    <p class="shorttext">
    			<span class="reqStar">*</span>
    			<label>Initial Status</label>		
    				<select name="<c:out value="${status.expression}"/>" >
    					<option value="READY" <c:if test="${status.value=='READY'}"> selected</c:if>>Ready</option>
    					<option value="LOCKED" <c:if test="${status.value=='LOCKED'}"> selected</c:if>>Locked</option>
    				</select>
		    </p>
        </spring:bind>
        
      <h4>Additional Forms</h4>

      <p class="shorttext">
         <label>Select Form</label>    
         <select name="selectAdditionalFormId" >
            <option value="" selected>None</option>
            <c:forEach var="addtlForm" items="${additionalFormDevices}" varStatus="loopCount">
               <option value="<c:out value="${addtlForm.id}"/>">
                  <c:out value="${addtlForm.name}"/></option>
            </c:forEach>
         </select>
         <span class="act">
            <input type="submit" name="addForm" value="Add" class="active" onclick="javascript:document.forms[0].validate.value='false';" />
         </span>
      </p>
      
      <c:forEach var="chosenForm" items="${selectedAdditionalFormDevices}">
      <c:out value="${chosenForm.name}" />
         <div class="itemAction">
             <a href="javascript:document.forms[0].submitAction.value='removeFormDef';
               document.forms[0].params.value='id=<c:out value="${chosenForm.id}"/>';
               document.forms[0].submit();">
                 <osp:message key="remove" bundle="${msgs}" />
                 </a>
         </div>
      
      </c:forEach>
        
		<spring:bind path="scaffoldingCell.reflectionDeviceType">  
            <input type="hidden" name="<c:out value="${status.expression}"/>"
               value="<c:out value="${status.value}"/>" />
        </spring:bind>
        
		<spring:bind path="scaffoldingCell.reflectionDevice">  
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
          <p class="shorttext">
            <label>Select Reflection Device</label>    
               <select name="<c:out value="${status.expression}"/>" <c:out value="${localDisabledText}"/>>
                     <option onclick="document.forms[0].reflectionDeviceType.value='';" value="">None</option>
                  <c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].reflectionDeviceType.value='<c:out value="${refDev.type}"/>');" 
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
       <p class="shorttext">
         <label>Select Review Device</label>    
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
         <label>Select Evaluation Device</label>    
            <select name="<c:out value="${status.expression}"/>" <c:out value="${localDisabledText}"/>>
                     <option onclick="document.forms[0].evaluationDeviceType.value='';" value="">None</option>
                  <c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
                     <option onclick="document.forms[0].evaluationDeviceType.value='<c:out value="${evalDev.type}"/>';" 
                        value="<c:out value="${evalDev.id}"/>" <c:if test="${status.value==evalDev.id}"> selected</c:if>><c:out value="${evalDev.name}"/></option>
                  </c:forEach>
               </select>
       </p>
     </spring:bind>
		
   <h4 style="cursor:pointer" onclick="javascript:showHideDiv('evaluatorsDiv','/osp-jsf-resource')">
      <img style="position:relative; float:left; margin-right:10px; left:3px; top:2px;" id="imgevaluatorsDiv" src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif" />
      Evaluators</h4>
   <div id="evaluatorsDiv">  
      <c:forEach var="eval" items="${evaluators}">
         <div class="indnt1"><c:out value="${eval}" /></div>
      </c:forEach>
   </div>
	<spring:bind path="scaffoldingCell.id">
		<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
		<span class="error_message"><c:out value="${status.errorMessage}"/></span>
	</spring:bind>

	<div class="act">
		<input type="submit" name="saveAction" value="Save" class="active" onclick="javascript:document.forms[0].validate.value='true';" />

      <c:if test="${empty helperPage}">
         <input type="button" name="action" value="<osp:message key="cancel" bundle="${msgs}" />"
            onclick="window.document.location='<osp:url value="viewScaffolding.osp?scaffolding_id=${scaffoldingCell.scaffolding.id}"/>'"/>
      </c:if>
      <c:if test="${not empty helperPage}">
   		<input type="submit" name="action" value="Cancel" class="active"
            onclick="javascript:document.forms[0].validate.value='false';document.forms[0].canceling.value='true'" />
         <input type="hidden" name="canceling" value="" />
      </c:if>

	</div>
</form>
