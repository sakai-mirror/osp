<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="matrixStyle.jspf" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<SCRIPT LANGUAGE="JavaScript">

function hrefViewCell(pageId) {
  window.location="<osp:url value="viewCell.osp?page_id="/>"+pageId;
}


</SCRIPT>
<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" />
<osp-c:authZMap prefix="osp.matrix." var="matrixCan" />


    <div class="navIntraTool">
        <c:if test="${can.create}">
            <a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><fmt:message key="action_createEdit"/></a>
            
            <c:if test="${empty matrixContents.scaffolding}">
                <a href="<osp:url value="importScaffolding.osp"/>" title="<fmt:message key="action_import_title"/>" >
                   <fmt:message key="action_import"/>
                </a>
            </c:if>	   
        </c:if> 
        <c:if test="${isMaintainer}">
            <c:if test="${not empty matrixContents.scaffolding && can.create}">
                <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=matrixManager&scaffoldingId=<c:out value="${matrixContents.scaffolding.id.value}"/>/<c:out value="${matrixContents.scaffolding.title}" />.zip">
                   <fmt:message key="action_export"/>
                </a>
            </c:if>
             <a href="<osp:url value="osp.permissions.helper/editPermissions">
               <osp:param name="message"><fmt:message key="action_message_setPermission">
	             <fmt:param><c:out value="${tool.title}"/></fmt:param>
		         <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
		       </osp:param>
               <osp:param name="name" value="scaffolding"/>
               <osp:param name="qualifier" value="${tool.id}"/>
               <osp:param name="returnView" value="matrixRedirect"/>
               </osp:url>"
               title="<fmt:message key="action_permissions_title"/>" >
         	<fmt:message key="action_permissions"/>
             </a>
         </c:if>
    </div>

    <h3><fmt:message key="title_matrixManager"/></h3>
    
    <c:if test="${can.create}">
      <c:if test="${not empty matrixContents.scaffolding.description}">
       	<p class="instruction">
            <c:out value="${matrixContents.scaffolding.description}" escapeXml="false" />
         </p>
      </c:if>
      <p class="instruction">
        	<fmt:message key="instructions_create"/>
        </p>
    </c:if>
    <c:if test="${(empty matrixContents.columnLabels && !can.create) || !matrixContents.scaffolding.published}">
    	<p class="instruction">
        	<br/>
        	<fmt:message key="instructions_notFullySetup"/>
        	<br/>
        	<fmt:message key="instructions_contactAdmin"/>
        </p>
    </c:if>
    

    <c:if test="${(not empty matrixContents.scaffolding) && matrixContents.scaffolding.published}">
        <c:if test="${matrixCan.review && not empty members}">
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
                    <INPUT type="submit" value="<fmt:message key="button_go"/>"/>
                </div>
            </form>
            <c:set var="readOnly_label"><fmt:message key="matrix_readOnly"/></c:set> 
	        <fmt:message key="matrix_viewing">
	          <fmt:param><c:if test="${readOnlyMatrix}"><strong><c:out value="${readOnly_label}"/></strong></c:if></fmt:param>
              <fmt:param><c:out value="${matrixOwner.displayName}" /></fmt:param>
	        </fmt:message>
        </c:if>
    
    </c:if>
    <c:if test="${not empty matrixContents.columnLabels && matrixContents.scaffolding.published}">
        <p class="instruction">
           <fmt:message key="instructions_clickOnaCellToEdit"/>
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
                    <th class="matrix-column-heading" width="200" 
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
                 <osp-h:glossary link="true" hover="true">
                    <th class="matrix-row-heading" bgcolor="<c:out value="${rowLabel.color}"/>" >
                        <font color="<c:out value="${rowLabel.textColor}"/>">
                           <c:out value="${rowLabel.description}"/>
                        </font>
                    </th>
                 </osp-h:glossary>
                 <c:forEach var="cellBean" items="${matrixContents.matrixContents[loopStatus.index]}">
                     <c:set var="cell" value="${cellBean.cell}"/>
                     
                     <td class="matrix-cell-border matrix-<c:out value="${cell.status}"/>" onClick="hrefViewCell('<c:out value="${cell.wizardPage.id}"/>') " style="cursor:pointer">
                        &nbsp;
                        <c:forEach var="node" items="${cellBean.nodes}">
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
        
        <%@ include file="matrixLegend.jspf" %>
    
    </c:if>
