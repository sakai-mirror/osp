<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<form method="POST">
    <c:if test="${!scaffolding.published}" >
	   <div class="navIntraTool">
			<a href="javascript:document.forms[0].dest.value='addLevel';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
				Add Level...
			</a>
			<a href="javascript:document.forms[0].dest.value='addCriterion';document.forms[0].submitAction.value='forward';document.forms[0].params.value='path=';document.forms[0].submit();">
				Add Criterion...
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
        
        <br />
      
      
		<h4>Levels</h4>
        
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
    								<c:out value="${level.description}"/>
    					
    									<div class="itemAction">
    						
                         <a href="javascript:document.forms[0].dest.value='addLevel';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>';
                	      document.forms[0].submit();">
                		     Edit
                	   </a>
    				<c:if test="${!scaffolding.published}" >
                         | <a href="javascript:document.forms[0].dest.value='removeLevCrit';
                	      document.forms[0].finalDest.value='deleteLevel';
                	      document.forms[0].label.value='Level';
                	      document.forms[0].displayText.value='<c:out value="${level.description}"/>';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='level_id=<c:out value="${level.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
                	      document.forms[0].submit();">
                		     Remove
                	   </a>
                         | <a href="javascript:document.forms[0].dest.value='moveLevel';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>';
                	      document.forms[0].submit();">
                		     Up
                	   </a>
                         | <a href="javascript:document.forms[0].dest.value='moveLevel';
                	      document.forms[0].submitAction.value='forward';
                	      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>';
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
		
		<h4>Criteria</h4>
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
					<c:set var="rootIndex" value="-1"/>
					<c:forEach var="criterion" items="${scaffolding.criteria}" varStatus="itemLoopStatus">
						<c:if test="${criterion.indent==0}">
							<c:set var="rootIndex" value="${rootIndex+1}"/>
							<tr>
								<td <c:if test="${not empty criterion.color}">bgcolor="<c:out value="${criterion.color}"/>"</c:if>>
									<div class="tier0">
										<c:out value="${criterion.description}"/>
										
										<div class="itemAction">
										
                      <a href="javascript:document.forms[0].dest.value='addCriterion';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='index=<c:out value="${itemLoopStatus.index}"/>:path=';
                      document.forms[0].submit();">
                          Edit
                      </a>

											<c:if test="${!scaffolding.published}" >
                      | <a href="javascript:document.forms[0].dest.value='removeLevCrit';
                      document.forms[0].finalDest.value='deleteCriterion';
                      document.forms[0].label.value='Criterion';
                      document.forms[0].displayText.value='<c:out value="${criterion.description}"/>';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='criterion_id=<c:out value="${criterion.id}"/>:index=<c:out value="${itemLoopStatus.index}"/>';
                      document.forms[0].submit();">
                          Remove
                      </a>
                      | <a href="javascript:document.forms[0].dest.value='moveCriterion';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index-1}"/>:current_root_index=<c:out value="${rootIndex}"/>:dest_root_index=<c:out value="${rootIndex-1}"/>';
                      document.forms[0].submit();">
                          Up
                      </a>
                      | <a href="javascript:document.forms[0].dest.value='moveCriterion';
                      document.forms[0].submitAction.value='forward';
                      document.forms[0].params.value='current_index=<c:out value="${itemLoopStatus.index}"/>:dest_index=<c:out value="${itemLoopStatus.index+1}"/>:current_root_index=<c:out value="${rootIndex}"/>:dest_root_index=<c:out value="${rootIndex+1}"/>';
                      document.forms[0].submit();">
                          Down
                      </a>
											</c:if>
										</div>
									</div>
								</td>
							</tr>
						</c:if>
					</c:forEach>
					
    			</tbody>
    		</table>
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
    
</form>

  