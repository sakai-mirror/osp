<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<SCRIPT LANGUAGE="JavaScript">

function hrefViewCell(cellId) {
  window.location="<osp:url value="editScaffoldingCell.osp?scaffoldingCell_id="/>"+cellId;
}


</SCRIPT>

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />


	<c:if test="${can.create}">
		<div class="navIntraTool">
			<c:if test="${can.create}">
				<a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>">Edit...</a>
			</c:if>
			<c:if test="${!matrixContents.scaffolding.published && can.publish}">
				<a href="<osp:url value="publishScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>">Publish</a>
			</c:if>
		</div>
	</c:if>

	<h3>Matrix Scaffolding</h3>
  
	<c:if test="${empty matrixContents.columnLabels}">
		<p class="instruction">Click Edit to setup scaffolding</p>
	</c:if>
	<c:if test="${not empty matrixContents.columnLabels}">
		<p class="instruction">Click on a cell to edit its settings.</p>
<!--
  <table width="800" border="0" height="33" bgcolor="#FFFFB8">
    <tr bgcolor="#FFFFB8"> 
      <td height="36" width="100" align="left">
        &nbsp;
      </td>
      <td height="36" align="left">
        <font face="Verdana, Arial, Helvetica, sans-serif" size="2">
           Click on a cell to edit its settings.
        </font>
      </td>
    </tr>
  </table>
  <br/>
 -->  

		<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
		<table cellspacing="0" width="800">
			<tr>
				<th class="matrix-row-heading" width="400">
					<c:out value="${matrixContents.scaffolding.title}"/>
				</th>
				<c:forEach var="head" items="${columnHeading}">
					<th class="matrix-column-heading" width="200">
						<c:out value="${head.description}"/>
					</th>
				</c:forEach>
			</tr>   
			<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
				<tr>
					<th class="matrix-row-heading" bgcolor="<c:out value="${rowLabel.color}"/>" >
						<c:out value="${rowLabel.description}"/>
					</th>
	    
					<c:forEach var="cell" items="${matrixContents.matrixContents[loopStatus.index]}">
						<td class="matrix-<c:out value="${cell.initialStatus}"/>" onClick="hrefViewCell('<c:out value="${cell.id}"/>') " style="cursor:pointer">
							&nbsp;
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	  
		<table width="550" border="0" cellspacing="10">
			<tr> 
				<td width="93" height="21" align="left" valign="bottom"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Legend</font></td>
				<td width="78"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td width="108"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td width="82"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td width="167"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
			</tr>
			<tr> 
				<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td bgcolor="#86F283"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Ready</font></td>
				<td bgcolor="#A6C7EA"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Completed</font></td>
			</tr>
			<tr> 
				<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td bgcolor="#F7EF84"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Pending</font></td>
				<td bgcolor="#AC326B"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp;</font></td>
				<td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Locked</font></td>
			</tr>
		</table>
  
	</c:if>