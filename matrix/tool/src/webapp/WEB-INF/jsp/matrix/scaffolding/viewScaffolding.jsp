<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../matrixStyle.jspf" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<c:forEach var="style" items="${styles}">
   <link href="<c:out value='${style}'/>" type="text/css" rel="stylesheet"
      media="all" />
</c:forEach>

<script type="text/javascript">

function hrefViewCell(cellId) {
  window.location="<osp:url value="editScaffoldingCell.osp?scaffoldingCell_id="/>"+cellId;
}

</script>

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" qualifier="${matrixContents.scaffolding.worksiteId}"/>
		<div class="navIntraTool">
			<c:if test="${can.create}">
				<a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><fmt:message key="action_edit"/></a>
			</c:if>
         <a href="<osp:url value="listScaffolding.osp"/>"><fmt:message key="action_list"/></a>
		</div>
	<h3><fmt:message key="title_matrixScaffolding"/></h3>
   
   <c:if test="${not empty matrixContents.scaffolding.description}">
      <p class="instruction">
         <osp-h:glossary link="true" hover="true">
            <c:out value="${matrixContents.scaffolding.description}" escapeXml="false" />
         </osp-h:glossary>
      </p>
   </c:if>
  
	<c:if test="${empty matrixContents.columnLabels}">
		<p class="instruction"><fmt:message key="instructions_clickEdittosetup"/></p>
	</c:if>
	<c:if test="${not empty matrixContents.columnLabels}">
		<p class="instruction"><fmt:message key="instructions_clickOnaCelltoEdit"/></p>  

		<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
		<table cellspacing="0" width="100%" summary="<fmt:message key="table_summary_matrixScaffolding"/>">
			<tr>
				<th class="matrix-row-heading" scope="col">
               <osp-h:glossary link="true" hover="true">
   					<c:out value="${matrixContents.scaffolding.title}"/>
               </osp-h:glossary>
				</th>
				<c:forEach var="head" items="${columnHeading}">
					<th class="matrix-column-heading matriColumnDefault" 
                  bgcolor="<c:out value="${head.color}"/>" 
                  style="color: <c:if test="${not empty head.textColor}" ><c:out value="${head.textColor}"/></c:if>" scope="col">
                  <osp-h:glossary link="true" hover="true">
                     <c:out value="${head.description}"/>
                  </osp-h:glossary>
					</th>
				</c:forEach>
			</tr>   
			<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
				<tr>
					<th class="matrix-row-heading matriRowDefault" bgcolor="<c:out value="${rowLabel.color}"/>" 
						style="color: <c:if test="${not empty rowLabel.textColor}" ><c:out value="${rowLabel.textColor}"/></c:if>">
                  <osp-h:glossary link="true" hover="true">
                        <c:out value="${rowLabel.description}"/>
                  </osp-h:glossary>
					</th>
	    			<c:forEach var="cell" items="${matrixContents.matrixContents[loopStatus.index]}">
						<td class="matrix-cell-border matrix-<c:out value="${cell.initialStatus}"/>" onclick="hrefViewCell('<c:out value="${cell.id}"/>') " style="cursor:pointer">
						 <a href="#" onclick="hrefViewCell('<c:out value="${cell.id}"/>') " class="skip"><fmt:message key="table_cell_link_title"/></a>
							&nbsp;
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	  
     <%@ include file="../matrixLegend.jspf" %>
  
	</c:if>
