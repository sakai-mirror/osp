<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script language="javascript">
var runHide=true;

function showHideDiv(divNo)
  {
  //alert(divNo);
  var tmpdiv = "div" + divNo;
  var tmpimg = "img" + divNo;
  var divisionNo = ospGetElementById(tmpdiv);
  var imgNo = ospGetElementById(tmpimg);
  if(divisionNo)
    {
    //alert(divisionNo.style.display);
    if(divisionNo.style.display =="none")
      {
      //alert("in if");
      divisionNo.style.display="block";
      imgNo.src ="/osp-common-tool/img/collapse.gif";
      }
    else
      {
      //alert("in else");
      divisionNo.style.display ="none";
      imgNo.src ="/osp-common-tool/img/expand.gif";
      }
    }
  }

function startup()
  {
  hideUnhideAllDivs(9,"none");
  }
function hideUnhideAllDivs(maxDivs,action)
  {
  if(runHide==true)
    {
    //alert("called");
    runHide=false;
    for(i=0; i <(maxDivs); i++)
      {
      //alert(i);
      divisionNo = "div"+i;
      document.getElementById(divisionNo).style.display =action;
      }
    }
  }

function cancelLink () {
  return false;
}

function disableLink (linkName) {
  var link = ospGetElementById(linkName);
  if (link.onclick)
    link.oldOnClick = link.onclick;
  link.onclick = cancelLink;
  if (link.style) {
    link.style.cursor = 'default';
    link.style.color = '#999999';
  }
  link.disabled='true';
}

function hrefViewCell(cellId) {
  window.location="<osp:url value="viewCell.osp">
  		<osp:param name="cell_id" value="${cell.id}"/>
  		<osp:param name="action" value="Browse"/>
  		</osp:url>";
}

</script>

<form name="form" method="POST" action="<osp:url value="viewCell.osp"/>">
	<osp:form/>

	<c:set var="cell" value="${cellBean.cell}"/>

	<div class="navIntraTool">
		<a name="linkNew" id="linkNew" href="<osp:url value="attachToCell.osp">
			<osp:param name="cell_id" value="${cell.id}"/>
			</osp:url>">New...</a>
		<a name="linkReflection" id="linkReflection" href="<osp:url value="reflect.osp">
			<osp:param name="cell_id" value="${cell.id}"/>
			<osp:param name="id" value="${cell.reflection.id}"/>
			</osp:url>">Reflection...</a>
	</div>

    <h3>View Cell</h3>
    
	<osp-h:glossary link="true" hover="true">
		<table class="itemSummary">
			<tr><th>Level: </th><td><c:out value="${cell.scaffoldingCell.level.description}"/></td></tr>
			<tr><th>Criteria: </th><td>
                
                <%-- there is only one because sub-criteria was removed --%>
				<c:forEach var="criterionItem" items="${cellBean.criteriaRequirements}" varStatus="loopCount" >
					<c:out value="${criterionItem.displayString}" escapeXml="false" />
				</c:forEach>
			</td></tr>
		</table>
	</osp-h:glossary>

	<c:if test="${cell.status != 'READY'}">
		<div class="validation">
			Cell status is <c:out value="${cell.status}"/> and cannot be altered
		</div>
	</c:if>

    <h4>Cell Files</h4>

	<p class="instruction">Items currently associated with cell:</p>


	<table class="listHier" cellspacing="0" >
		<thead>
			<tr>
				<th scope="col">Title</th>
				<th scope="col">Size</th>
				<th scope="col">Created By</th>
				<th scope="col">Last Modified</th>
				<th scope="col">Remove?</th>
			</tr>
		</thead>
		<tbody>


			<c:set var="canReflect" value="false"/>
			<c:forEach var="node" items="${cellBean.attachments}">
				<c:set var="canReflect" value="true"/>
				<c:set var="criteriaList" value="${node.criteriaList}"/>
				<c:set var="node" value="${node.node}" />



				<tr>
					<td>
						<a href='<c:out value="${node.externalUri}"/>' target="_blank" ><c:out value="${node.name}"/></a>
                        <!-- if status is ready -->
                        <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
                            <div class="itemAction">
                                <a href="<osp:url value="removeConfirmation.osp?&cell_id=${cell.id}&selectedArtifacts=${node.id}"/>">Remove</a>
                               
                                <c:forEach var="criterion" items="${criteriaList}" varStatus="loopCount">
                                    | <c:if test="${loopCount.index!=0}"> </c:if>
                                    <a href='<osp:url value="manageArtifactAssociations.osp?cell_id=${cell.id}&node_id=${node.id}"/>'>
                                        <c:out value="${criterion}"/>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${node.technicalMetadata.size > 1024 * 1024}">
								<fmt:formatNumber value="${node.technicalMetadata.size / (1024 * 1024)}" maxFractionDigits="1"/>MB
							</c:when>
							<c:when test="${node.technicalMetadata.size > 1024}">
								<fmt:formatNumber value="${node.technicalMetadata.size / (1024)}"  maxFractionDigits="1"/>KB
							</c:when>
							<c:when test="${node.technicalMetadata.size > 0}">
								<fmt:formatNumber value="${node.technicalMetadata.size}" />
							</c:when>
						</c:choose>
					</td>
					<td>
						<c:out value="${node.technicalMetadata.owner.displayName}"/>
					</td>
					<td align="center">
						<fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="MM-dd-yyyy" />
					</td>
					<td>
						<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
							<input type="checkbox" name="selectedArtifacts" value="<c:out value="${node.id}"/>">
						</c:if>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${canReflect != 'true'}">
				<tr><td>&nbsp;&nbsp;&nbsp;There are no resource items at this location.</td></tr>
			</c:if>
		</tbody>
	</table> <!-- End the file list table -->

	<c:if test="${canReflect != 'true' or cell.status != 'READY' or readOnlyMatrix == 'true'}">
		<script language="javascript">
			disableLink("linkReflection");
			<!-- //TODO figure out how better way to disable Reflection and New -->
		</script>
	</c:if>
	<br/>
	<!-- if status is ready -->
    <p class="act">
    	<c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
    		<c:if test="${canReflect == 'true'}">
    		
    			<input type="submit" name="action" class="active" value="Update"/>
    		</c:if>
    	</c:if>
    	<input type="hidden" name="cell_id" value="<c:out value="${cell.id}"/>"/>
    	<input type="submit" name="action" value="Cancel"/>
    </p>
</form>
<hr/>
<c:set var="numberOfItems" value="0" />
<c:forEach var="thing" items="${cell.reviewerItems}" varStatus="loopStatus">
	<c:set var="numberOfItems" value="${loopStatus.index+1}" />
</c:forEach>

<c:if test="${cell.status != 'READY' or readOnlyMatrix == 'true'}">
	<script language="javascript">
		disableLink("linkNew");
	<!-- //TODO figure out how better way to disable Reflection and New-->
	</script>
</c:if>

<c:if test="${cell.status == 'PENDING' or cell.status == 'COMPLETE' or readOnlyMatrix == 'true' or (numberOfItems > 0 and cell.status == 'READY')}">
	<br/>
		<c:forEach var="expectation" items="${cell.scaffoldingCell.expectations}" varStatus="loopStatus">
			<c:set var="i" value="${loopStatus.index}"/>
			<c:set var="reflection" value="${cell.reflection.reflectionItems[i]}"/>
			
			<h4>Expectation <c:out value="${i+1}"/></h4>

			<c:out value="${expectation.description}" escapeXml="false"/>
            
            <p class="longtext">
                <label class="block">Evidence</label>
                <p class="instruction">
                    What evidence demonstrates that you have met this expectation? Please cut and
                    paste or add a link of your evidence into the space below.
                </p>
                <div class="indnt5">
                    <c:out value="${reflection.evidence}" escapeXml="false"/>
                </div>
             </p>
            
            
            <p class="longtext">
                <label class="block">Connect</label>
                <p class="instruction">
                    Explain how your evidence demonstrates this expectation.
                </p>
                <div class="indnt5">
                    <c:out value="${reflection.connect}" escapeXml="false"/>
                </div>
            </p>
            <br /><br />
		</c:forEach>
	
		<h4>Intellectual Growth</h4>
        
        <p class="longtext">
            <div class="indnt5">
                <c:out value="${cell.reflection.growthStatement}" escapeXml="false"/>
            </div>
        </p>
        <br /><br />
        
		<h4>Reviewer Comments:</h4>
        
        
    <table class="itemSummary">
		<c:forEach var="review" items="${cell.reviewerItems}" varStatus="loopStatus">
			<c:if test="${review.status=='COMPLETE'}">
                <tr>
                    <th>
                        Grade
                    </th>
                    <td>
                        <spring:message code="${reviewRubrics[review.grade].displayText}" text="${reviewRubrics[review.grade].displayText}" />
                    </td>
                </tr>
                <tr>
                    <th>
                        Date
                    </th>
                    <td>
                        <fmt:formatDate value="${review.modified}" pattern="MM-dd-yyyy" />
                    </td>
                </tr>
                <tr>
                    <th>
                        Commentary
                    </th>
                    <td class="indnt5">
                        <c:out value="${review.comments}" escapeXml="false" />
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
			</c:if>
		</c:forEach>
	</table>

</c:if>