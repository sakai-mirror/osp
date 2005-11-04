<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<SCRIPT LANGUAGE="JavaScript">

function hrefViewCell(cellId) {
  window.location="<osp:url value="viewCell.osp?cell_id="/>"+cellId+"&readOnlyMatrix=<c:out value="${readOnlyMatrix}"/>";
}


</SCRIPT>
<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />


    <div class="navIntraTool">
        <c:if test="${can.create}">
            <a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>">Create/Edit</a>
            
            <c:if test="${empty matrixContents.scaffolding}">
                <a href="<osp:url value="importScaffolding.osp"/>" title="Import..." >
                   Import...
                </a>
            </c:if>	   
        </c:if> 
        <c:if test="${isMaintainer}">
            <c:if test="${not empty matrixContents.scaffolding && can.create}">
                <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=matrixManager&scaffoldingId=<c:out value="${matrixContents.scaffolding.id.value}"/>/<c:out value="${matrixContents.scaffolding.title}" />.zip">
                   Export...
                </a>
            </c:if>
            <a href="<osp:url value="osp.permissions.helper/editPermissions">
               <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
               <osp:param name="name" value="scaffolding"/>
               <osp:param name="qualifier" value="${tool.id}"/>
               <osp:param name="returnView" value="matrixRedirect"/>
               </osp:url>"
               title="Permissions..." >
         	Permissions...
             </a>
         </c:if>
    </div>

    <h3>Matrix Manager</h3>
    
    <c:if test="${can.create}">
    	<p class="instruction">
        	Click Create/Edit to set up Matrix
        </p>
    </c:if>
    <c:if test="${(empty matrixContents.columnLabels && !can.create) || !matrixContents.scaffolding.published}">
    	<p class="instruction">
        	<br/>
        	Matrix has not been fully setup
        	<br/>
        	Contact Worksite administrator
        </p>
    </c:if>
    

    <c:if test="${(not empty matrixContents.scaffolding) && matrixContents.scaffolding.published}">
        <c:if test="${can.viewUsers && not empty members}">
            <form method="GET" action="<osp:url value="viewMatrix.osp"/>">
                <osp:form/>
                <div class="act">
                   <select name="view_user">
                      <c:forEach var="user" items="${members}">
                          <option value="<c:out value="${user.id}"/>" <c:if test="${matrixOwner.id.value == user.id}"> selected </c:if>>
                              <c:out value="${user.sortName}"/>
                          </option>
                      </c:forEach>
                    </select>
                    <INPUT type="submit" value="Go"/>
                </div>
            </form>
            Viewing the <c:if test="${readOnlyMatrix}"><strong>READ ONLY</strong></c:if> matrix of <c:out value="${matrixOwner.displayName}" />
        </c:if>
    
    </c:if>
    <c:if test="${not empty matrixContents.columnLabels && matrixContents.scaffolding.published}">
        <p class="instruction">
           Click on a cell to view/edit
        </p>
    
        <c:set var="columnHeading" value="${matrixContents.columnLabels}" />
        <table cellspacing="0" width="800">
            <tr>
                <th class="matrix-row-heading" width="400">
                    <osp-h:glossary link="true" hover="true">
                       <c:out value="${matrixContents.scaffolding.title}"/>
                    </osp-h:glossary>
                </th>
                <c:forEach var="head" items="${columnHeading}">
                    <th class="matrix-column-heading" width="200">
                        <osp-h:glossary link="true" hover="true">
                        	<c:out value="${head.description}"/>
                        </osp-h:glossary>
                    </th>
                </c:forEach>
            </tr>   
            <c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
              <tr>
                 <osp-h:glossary link="true" hover="true">
                    <th class="matrix-row-heading" bgcolor="<c:out value="${rowLabel.color}"/>" >
                       <c:out value="${rowLabel.description}"/>
                    </th>
                 </osp-h:glossary>
                 <c:forEach var="cellBean" items="${matrixContents.matrixContents[loopStatus.index]}">
                     <c:set var="cell" value="${cellBean.cell}"/>
                     <c:set var="attachments" value="${cellBean.attachments}"/>
                  
                     <td class="matrix-<c:out value="${cell.status}"/>" onClick="hrefViewCell('<c:out value="${cell.id}"/>') " style="cursor:pointer">
                        &nbsp;
                        <c:forEach var="node" items="${attachments}">
                            <fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="MM/dd/yyyy" var="date"/>
                               <c:set var="hover" value="Name:${node.name}; Size:${node.technicalMetadata.size} bytes; Last Modified: ${date}"/>
                               <img border="0" title="<c:out value="${hover}" />"
                               	alt="<c:out value="${node.name}"/>" 
                               	src="/library/image/<osp-c:contentTypeMap fileType="${node.mimeType}" mapType="image" />"/>
                        </c:forEach>
                     </td>
                 </c:forEach>
              </tr>
            </c:forEach>
        </table>
        
        <table width="550" border="0" cellspacing="10">
          <tr> 
            <td width="93" height="21" align="left" valign="bottom">Legend</td>
            <td width="78">&nbsp;</td>
            <td width="108">&nbsp;</td>
            <td width="82">&nbsp;</td>
            <td width="167">&nbsp;</td>
          </tr>
          <tr> 
            <td>&nbsp;</td>
            <td bgcolor="#86F283">&nbsp;</td>
            <td>Ready</td>
            <td bgcolor="#A6C7EA">&nbsp;</td>
            <td>Completed</td>
          </tr>
          <tr> 
            <td>&nbsp;</td>
            <td bgcolor="#F7EF84">&nbsp;</td>
            <td>Pending</td>
            <td bgcolor="#AC326B">&nbsp;</td>
            <td>Locked</td>
          </tr>
        </table>
    
    </c:if>
