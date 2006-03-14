<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../matrixStyle.jspf" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<SCRIPT LANGUAGE="JavaScript">

function hrefViewCell(cellId) {
  window.location="<osp:url value="editScaffoldingCell.osp?scaffoldingCell_id="/>"+cellId;
}

</SCRIPT>

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />


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
<!--
  <table width="800" border="0" height="33" bgcolor="#FFFFB8">
    <tr bgcolor="#FFFFB8"> 
      <td height="36" width="100" align="left">
        &nbsp;
      </td>
      <td height="36" align="left">
        <font face="Verdana, Arial, Helvetica, sans-serif" size="2">
           <fmt:message key="instructions_clickOnaCelltoEdit"/>
        </font>
      </td>
    </tr>
  </table>
  <br/>
 -->  

		<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
		<table cellspacing="0" width="100%">
			<tr>
				<th class="matrix-row-heading" width="10%">
               <osp-h:glossary link="true" hover="true">
   					<c:out value="${matrixContents.scaffolding.title}"/>
               </osp-h:glossary>
				</th>
				<c:forEach var="head" items="${columnHeading}">
					<th class="matrix-column-heading" width="10%" 
                  bgcolor="<c:out value="${head.color}"/>">
                  <osp-h:glossary link="true" hover="true">
                     <font color="<c:out value="${head.textColor}"/>">
   						   <c:out value="${head.description}"/>
                     </font>
                  </osp-h:glossary>
					</th>
				</c:forEach>
			</tr>   
			<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
				<tr>
					<th class="matrix-row-heading" bgcolor="<c:out value="${rowLabel.color}"/>" >
                  <osp-h:glossary link="true" hover="true">
                     <font color="<c:out value="${rowLabel.textColor}"/>">
                        <c:out value="${rowLabel.description}"/>
                     </font>
                  </osp-h:glossary>
					</th>
	    
					<c:forEach var="cell" items="${matrixContents.matrixContents[loopStatus.index]}">
						<td class="matrix-cell-border matrix-<c:out value="${cell.initialStatus}"/>" onClick="hrefViewCell('<c:out value="${cell.id}"/>') " style="cursor:pointer">
							&nbsp;
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	  
     <%@ include file="../matrixLegend.jspf" %>
  
	</c:if>