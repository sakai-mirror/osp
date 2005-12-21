<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setBundle basename="org.theospi.portfolio.matrix.messages" var="msgs" />

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
   <osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />
   <osp-c:authZMap prefix="osp.matrix." var="matrixCan" />

	<div class="navIntraTool">
		<a name="linkNew" id="linkNew" href="<osp:url value="attachToCell.osp">
			<osp:param name="cell_id" value="${cell.id}"/>
			</osp:url>">New...</a>
		<c:if test="${can.create}">
         <a name="linkManageCellStatus" id="linkManageCellStatus" href="<osp:url value="manageCellStatus.osp">
            <osp:param name="cell_id" value="${cell.id}"/>
            </osp:url>"><osp:message key="manage_cell_status" bundle="${msgs}" /></a>
      </c:if>
      <c:if test="${matrixCan.review && cell.scaffoldingCell.reviewDevice != null}">
         <a href="<osp:url value="viewCell.osp">
            <osp:param name="cell_id" value="${cell.id}"/>
            <osp:param name="action" value="review"/>
            </osp:url>"><osp:message key="review" bundle="${msgs}" /></a>
      </c:if>      
      <c:if test="${matrixCan.evaluate && cell.scaffoldingCell.evaluationDevice != null}">
         <a href="<osp:url value="manageCellStatus.osp">
            <osp:param name="cell_id" value="${cell.id}"/>
            </osp:url>"><osp:message key="evaluate" bundle="${msgs}" /></a>
      </c:if>
	</div>

    <h3><osp:message key="view_cell" bundle="${msgs}" /></h3>
    
	<osp-h:glossary link="true" hover="true">
		<table class="itemSummary">
			<tr><th><c:out value="${cell.scaffoldingCell.scaffolding.columnLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.level.description}"/></td></tr>
			<tr><th><c:out value="${cell.scaffoldingCell.scaffolding.rowLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.rootCriterion.description}"/></td></tr>
		</table>
	</osp-h:glossary>

	<c:if test="${cell.status != 'READY'}">
		<div class="validation">
			Cell status is <c:out value="${cell.status}"/> and cannot be altered
		</div>
	</c:if>
   
   <c:if test="${not empty cell.scaffoldingCell.guidance}">
      <h4><osp:message key="guidance_header" bundle="${msgs}" /></h4>
      <c:forEach var="guidanceItem" items="${cell.scaffoldingCell.guidance.items}" varStatus="loopStatus">
         <c:if test="${guidanceItem.type == 'instruction'}">
         <p class="longtext">
            <div class="indnt2">
               <c:out value="${guidanceItem.text}" escapeXml="false" />
               <c:forEach var="guidanceItemAtt" items="${guidanceItem.attachments}" varStatus="loopStatus">
                  <a href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />" target="_new">
                     <c:out value="${guidanceItemAtt.diaplayName}" /></a>         
               </c:forEach>
            </div>
         </p>
         </c:if>
      </c:forEach>
      <a href="<osp:url value="viewCell.osp">
         <osp:param name="cell_id" value="${cell.id}"/>
         <osp:param name="action" value="guidance"/>
      </osp:url>" title="<osp:message key="guidance_link_title" bundle="${msgs}" />">
         <osp:message key="guidance_link_text" bundle="${msgs}" /></a>
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
			<c:forEach var="node" items="${cellBean.nodes}">
				<c:set var="canReflect" value="true"/>

				<tr>
					<td>
						<a href='<c:out value="${node.externalUri}"/>' target="_blank" ><c:out value="${node.name}"/></a>
                        <!-- if status is ready -->
                        <c:if test="${cell.status == 'READY' and readOnlyMatrix != 'true'}">
                            <div class="itemAction">
                                <a href="<osp:url value="removeConfirmation.osp?&cell_id=${cell.id}&selectedArtifacts=${node.id}"/>">Remove</a>
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

	<br/>
   
   
   <h4>Reflection Device</h4>
   
   <c:if test="${cell.scaffoldingCell.reflectionDeviceType == 'form'}">
   Pick a form
   </c:if>
   
   
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

<h4>Reviews</h4>
<c:forEach var="review" items="${reviews}" varStatus="loopStatus">
   <c:out value="${review.id}" />
</c:forEach>
<h4>Evaluations</h4>