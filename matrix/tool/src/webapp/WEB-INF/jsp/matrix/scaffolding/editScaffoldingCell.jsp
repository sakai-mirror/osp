<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/WEB-INF/jsp/userSelectFunctions.inc" %>


<form method="POST" onsubmit="return updateItems('reviewers');">
  
    <c:if test="${not scaffoldingCell.scaffolding.published}">
	    <div class="navIntraTool">
	        <a href="javascript:updateItems('reviewers');document.forms[0].dest.value='addExpectation';document.forms[0].submitAction.value='forward';document.forms[0].onsubmit();document.forms[0].submit();">Add Expectation</a>
	    </div>
    </c:if>


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
    
    <table class="itemSummary" cellspacing="0">
       <tr><th>Level: </th><td><c:out value="${scaffoldingCell.level.description}"/></td></tr>
       <tr><th>Criteria: </th><td><c:out value="${scaffoldingCell.rootCriterion.description}"/></td></tr>
    </table>
    
		<h4>Cell Settings</h4>
      
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
		
		<p class="longtext">
			<label class="block">Expectation Header</label>
			<spring:bind path="scaffoldingCell.expectationHeader">
                    <table><tr>
					<td><textarea rows="10" cols="75" name="<c:out value="${status.expression}"/>" 
							id="<c:out value="${status.expression}"/>" ><c:out value="${status.value}"/></textarea></td>
                    </tr></table>
				<span class="error_message"><c:out value="${status.errorMessage}"/></span>
			</spring:bind>
		</p>
		
		
		
        <spring:bind path="scaffoldingCell.gradableReflection">
            <c:if test="${status.error}">
                <div class="validation"><c:out value="${status.errorMessage}"/></div>
            </c:if>
		    <p class="checkbox">
    				<input type="checkbox" name="<c:out value="${status.expression}"/>_checkBox"
                            id="<c:out value="${status.expression}"/>_checkBox"
    						value="true" 
    						<c:if test="${status.value}">checked</c:if>
    						onChange="form['<c:out value="${status.expression}"/>'].value=this.checked" 
    				/>
    				<label for="<c:out value="${status.expression}"/>_checkBox">Gradable Reflection</label>
				<input type="hidden" name="<c:out value="${status.expression}"/>"
					value="<c:out value="${status.value}"/>" />
		    </p>
        </spring:bind>
		
		<br />
		<p class="longtext">
            <label class="block">Reviewers</label>
			<c:set var="controlName" value="scaffoldingCell.reviewers" />
			<c:set var="object" value="${scaffoldingCell.reviewers}" />
			<%@ include file="/WEB-INF/jsp/userSelectControls.inc" %>
		</p>
		<br />



		<h4>Expectations</h4>
		
		<table class="listHier" cellspacing="0" >
			<thead>
				<tr>
					<th scope="col">Description</th>
					<th scope="col">Required?</th>
				</tr>
			</thead>
	        <tbody>
	   
				<c:forEach var="e" items="${scaffoldingCell.expectations}" varStatus="loopCount">
					<tr>
						<td>
							<c:out value="${e.description}" escapeXml="false"/>
							
								<div class="itemAction">
		          	    <a href="javascript:document.forms[0].dest.value='addExpectation';
		          	      document.forms[0].submitAction.value='forward';
		          	      document.forms[0].params.value='index=<c:out value="${loopCount.index}"/>';
		          	      document.forms[0].onsubmit();
		          	      document.forms[0].submit();">
		          		     Edit
		                    </a>
						<c:if test="${not scaffoldingCell.scaffolding.published}">
		                    | <a href="javascript:document.forms[0].dest.value='removeExpectation';               
         				  document.forms[0].finalDest.value='deleteExpectation';               
         				  document.forms[0].label.value='Expectation';               
         				  document.forms[0].displayText.value='<c:out value="${e.description}"/>'; 
	      				  document.forms[0].submitAction.value='forward';
	      				  document.forms[0].params.value='expectation_id=<c:out value="${e.id}"/>:index=<c:out value="${loopCount.index}"/>';
	      				  document.forms[0].onsubmit();
	      				  document.forms[0].submit();">
		          		     Remove
		                    </a>
		                    | <a href="javascript:document.forms[0].dest.value='moveExpectation';
		          	      document.forms[0].submitAction.value='forward';
		          	      document.forms[0].params.value='current_index=<c:out value="${loopCount.index}"/>:dest_index=<c:out value="${loopCount.index-1}"/>';
		          	      document.forms[0].onsubmit();
		          	      document.forms[0].submit();">
		          		     Up
		                    </a>
		                    | <a href="javascript:document.forms[0].dest.value='moveExpectation';
		          	      document.forms[0].submitAction.value='forward';
		          	      document.forms[0].params.value='current_index=<c:out value="${loopCount.index}"/>:dest_index=<c:out value="${loopCount.index+1}"/>';
		          	      document.forms[0].onsubmit();
		          	      document.forms[0].submit();">
		          		     Down
		                    </a>
						</c:if>
		                    	</div>
						</td>
						<td>
							<c:out value="${e.required}"/>
						</td>
					</tr>
		   
				</c:forEach>
	        </tbody>
		</table>

		<br />
		<br />


	<spring:bind path="scaffoldingCell.id">
		<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
		<span class="error_message"><c:out value="${status.errorMessage}"/></span>
	</spring:bind>

	<div class="act">
		<input type="submit" name="action" value="Save" class="active" onclick="javascript:document.forms[0].validate.value='true';" />
		<input type="button" name="action" value="Cancel" onclick="window.document.location='<osp:url value="viewScaffolding.osp?scaffolding_id=${scaffoldingCell.scaffolding.id}"/>'"/>
	</div>
</form>

<script type="text/javascript" language="JavaScript">
	//prepopulate first select box
	updateParticipantList("filterSelect","select1","reviewers");
</script>

<script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
<script type="text/javascript" defer="1">chef_setupformattedtextarea('expectationHeader');</script>


<%--@ include file="/WEB-INF/jsp/htmlarea.inc" --%>