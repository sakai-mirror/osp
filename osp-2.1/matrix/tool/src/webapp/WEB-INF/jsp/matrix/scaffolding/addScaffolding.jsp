<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

<form method="POST">
    <c:if test="${!scaffolding.published}" >
	   <div class="navIntraTool">
			<a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">
				Add Column...
			</a>
			<a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].onsubmit();document.forms[0].submit();">
				Add Row...
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
    
      <h3>Scaffolding</h3>
      
	  <div class="instruction">
	      
		  Scaffolding (required fields are noted with an <span class="reqStarInline">*</span>)
      </div>
      
	  <c:if test="${scaffolding.published}">
		  Scaffolding has been published and allows limited editing.
		  <c:set var="disabledText" value="disabled=\"disabled\""/>
	  </c:if>
      
      <spring:hasBindErrors name="entry">
          <div class="validation">There were problems in your last submission.  
            Please see below for details</div>
      </spring:hasBindErrors>
  

		<h4>General Scaffolding Information</h4>
        
        
        <spring:bind path="scaffolding.title">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
    		<p class="shorttext">
    			<span class="reqStar">*</span><label>Title</label>
				<input type="text" name="<c:out value="${status.expression}"/>" 
                	   value="<c:out value="${status.value}"/>" 
					   size="25" maxlength="25" <c:out value="${disabledText}"/>>
    		</p>
        </spring:bind>
		
		<p class="longtext">
			<label class="block">Description</label>
			<spring:bind path="scaffolding.description">
                <table><tr>
				<td><textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80" 
						  <c:out value="${disabledText}"/>><c:out value="${status.value}"/></textarea></td>
                </tr></table>
				<font color="red"><c:out value="${status.errorMessage}"/></font>
			</spring:bind>
		</p>
<%--		
		<p class="shorttext">
			<label>Permission Statement Definition File</label>
			<spring:bind path="scaffolding.privacyXsdId">
				<input type="text" id="xsdName" disabled="true" value="<c:out value="${xsdName}"/>"/>
				<input type="hidden" name="privacyXsdId" id="privacyXsdId" value="<c:out value="${status.value}"/>" />
				<span class="error_message"><c:out value="${status.errorMessage}"/></span>
				<c:if test="${not scaffolding.published}">
                    <a href="javascript:document.forms[0].dest.value='pickPrivacy';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].submit();">
    				    Pick file
				    </a>
                </c:if>
			</spring:bind>
		</p>
        <br/>
		<p class="shorttext">
			<label>Permission Statement File Element</label>
			<spring:bind path="scaffolding.documentRoot">
				<select name="documentRoot" id="<c:out value="${status.expression}" />" <c:out value="${disabledText}"/>>
					<c:forEach var="element" items="${elements}" varStatus="status">
						<option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
					</c:forEach>
				</select>
			</spring:bind>
		</p>
   --%>     
        <br />
      
      
		<h4>Columns</h4>
      <spring:bind path="scaffolding.columnLabel">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
         <div class="shorttext">
            <label>Column Label</label>
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
    					<th scope="col">Name</th>
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
                		     Edit
                	   </a>
    				<c:if test="${!scaffolding.published}" >
                         | <a href="javascript:document.forms[0].dest.value='removeLevCrit';
                	      document.forms[0].finalDest.value='deleteLevel';
                	      document.forms[0].label.value='Column';
                	      document.forms[0].displayText.value='<c:out value="${level.description}"/>';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='level_id=<c:out value="${level.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
                        document.forms[0].onsubmit();
                	      document.forms[0].submit();">
                		     Remove
                	   </a>
                         | <a href="javascript:document.forms[0].dest.value='moveLevel';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>';
                        document.forms[0].onsubmit();
                	      document.forms[0].submit();">
                		     Up
                	   </a>
                         | <a href="javascript:document.forms[0].dest.value='moveLevel';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>';
                        document.forms[0].onsubmit();
                	      document.forms[0].submit();">
                		     Down
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
		
		<h4>Rows</h4>
      <spring:bind path="scaffolding.rowLabel">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
            <div class="shorttext">
            <label>Row Label</label>
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
    					<th scope="col">Name</th>
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
                          Edit
                      </a>

											<c:if test="${!scaffolding.published}" >
                      | <a href="javascript:document.forms[0].dest.value='removeLevCrit';
                      document.forms[0].finalDest.value='deleteCriterion';
                      document.forms[0].label.value='Row';
                      document.forms[0].displayText.value='<c:out value="${criterion.description}"/>';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='criterion_id=<c:out value="${criterion.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
                      document.forms[0].onsubmit();
                      document.forms[0].submit();">
                          Remove
                      </a>
                      | <a href="javascript:document.forms[0].dest.value='moveCriterion';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>';
                      document.forms[0].onsubmit();
                      document.forms[0].submit();">
                          Up
                      </a>
                      | <a href="javascript:document.forms[0].dest.value='moveCriterion';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>';
                      document.forms[0].onsubmit();
                      document.forms[0].submit();">
                          Down
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
		
      <h4>Matrix Status Colors</h4>
      <spring:bind path="scaffolding.readyColor">
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
      <p class="shorttext">
         <span class="reqStar">*</span><label>Ready Color</label>
         <input type="text" disabled="disabled" value="" size="2" 
                  name="<c:out value="${status.expression}"/>_sample"
                  style="background-color: <c:out value="${status.value}"/>" />
         <input type="text" name="<c:out value="${status.expression}"/>" 
                  value="<c:out value="${status.value}"/>" 
               size="25" maxlength="25" <c:out value="${disabledText}"/>
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
      <spring:bind path="scaffolding.pendingColor">
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
      <p class="shorttext">
         <span class="reqStar">*</span><label>Pending Color</label>
         <input type="text" disabled="disabled" value="" size="2" 
                  name="<c:out value="${status.expression}"/>_sample"
                  style="background-color: <c:out value="${status.value}"/>" />
         <input type="text" name="<c:out value="${status.expression}"/>" 
                  value="<c:out value="${status.value}"/>" 
               size="25" maxlength="25" <c:out value="${disabledText}"/>
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
      <spring:bind path="scaffolding.completedColor">
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
      <p class="shorttext">
         <span class="reqStar">*</span><label>Completed Color</label>
         <input type="text" disabled="disabled" value="" size="2" 
                  name="<c:out value="${status.expression}"/>_sample"
                  style="background-color: <c:out value="${status.value}"/>" />
         <input type="text" name="<c:out value="${status.expression}"/>" 
                  value="<c:out value="${status.value}"/>" 
               size="25" maxlength="25" <c:out value="${disabledText}"/>
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
      <spring:bind path="scaffolding.lockedColor">
         <c:if test="${status.error}">
             <div class="validation"><c:out value="${status.errorMessage}"/></div>
         </c:if>
      <p class="shorttext">
         <span class="reqStar">*</span><label>Locked Color</label>
         <input type="text" disabled="disabled" value="" size="2" 
                  name="<c:out value="${status.expression}"/>_sample"
                  style="background-color: <c:out value="${status.value}"/>" />
         <input type="text" name="<c:out value="${status.expression}"/>" 
                  value="<c:out value="${status.value}"/>" 
               size="25" maxlength="25" <c:out value="${disabledText}"/>
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
      
		<br />
		<br />
		<c:if test="${not empty isInSession}">
			<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
		</c:if>
		
		<div class="act">
			     <input type="submit" name="action" class="active" 
                        value="Generate Matrix" 
  				        onclick="javascript:document.forms[0].validate.value='true';"/>
			<input type="submit" name="action" value="Cancel"/>
		</div>
      
    <script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
    <script type="text/javascript" defer="1">chef_setupformattedtextarea('descriptionTextArea');</script>
    
</form>

  