<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="matrixStyle.jspf" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<c:forEach var="style" items="${styles}">
   <link href="<c:out value='${style}'/>" type="text/css" rel="stylesheet"
      media="all" />
</c:forEach>

<script type="text/javaScript">

	function hrefViewCell(pageId) {
	  window.location="<osp:url value="viewCell.osp?page_id="/>"+pageId;
	}


</script>
<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" qualifier="${matrixContents.scaffolding.worksiteId}"/>
<osp-c:authZMap prefix="osp.matrix." var="matrixCan" qualifier="${matrixContents.scaffolding.worksiteId}"/>
<c:if test="${isExposedPage != true}">
	<div class="navIntraTool">
		<a href="<osp:url value="listScaffolding.osp"/>"><fmt:message key="action_list"/></a>
	</div>
</c:if>


<h3>
	<c:set var="readOnly_label"><fmt:message key="matrix_readOnly"/></c:set>
	<c:choose>
		<c:when test="${(matrixCan.evaluate || matrixCan.review)}">
			<fmt:message key="matrix_viewing_title_eval">
				<fmt:param>
					<c:if test="${matrixContents.scaffolding.preview}">
						<span class="highlight">
							<fmt:message key="matrix_viewing_title_preview"/>
						</span>
					</c:if>		  
					<c:if test="${!matrixContents.scaffolding.preview}">
						<fmt:message key="matrix_viewing_title_view"/>
					</c:if>
				</fmt:param>	
				<fmt:param><c:out value="${matrixContents.scaffolding.title}" /></fmt:param>
				<fmt:param><c:if test="${readOnlyMatrix}">(<c:out value="${readOnly_label}"/>)</c:if></fmt:param>
				
				<fmt:param><c:out value="${matrixOwner.displayName}" /></fmt:param>
			</fmt:message>
		</c:when>
		<c:otherwise>
			<c:if test="${matrixContents.scaffolding.preview}">
				<span class="highlight">
					<fmt:message key="scaffolding_published_preview"/>
				</span>
			</c:if>		  
			<c:out value="${matrixContents.scaffolding.title}" />
		</c:otherwise>
	</c:choose>	
</h3>
   
<c:if test="${matrixContents.scaffolding.preview}">
	<div class="information">
    	<fmt:message key="title_matrixPreview"/>
	</div>
</c:if>
<c:if test="${not empty matrixContents.scaffolding.description}">
	<div class="textPanelFooter">
		<osp-h:glossary link="true" hover="true">
			<c:out value="${matrixContents.scaffolding.description}" escapeXml="false" />
		</osp-h:glossary>
	</div>
</c:if>

<c:if test="${(not empty matrixContents.scaffolding)}">
	<div class="navPanel">
		<c:if test="${(matrixCan.evaluate || matrixCan.review)}">
			<c:choose>
				<c:when test="${hasGroups && empty userGroups}">
					<p class="instruction"><fmt:message key="matrix_groups_unavailable"></fmt:message></p>
				</c:when>
				<c:otherwise>
					<form method="get" action="<osp:url value="viewMatrix.osp"/>">
						<osp:form/>
						<div class="viewNav">
							<c:if test="${not empty userGroups && userGroupsCount > 1}">
								<label for="group_filter-id"><fmt:message key="matrix_viewing_select_group" /></label>
								<select name="group_filter" id="group_filter-id" onchange="this.form.submit()">
									<option value="" <c:if test="${empty filteredGroup}">selected="selected"</c:if>>
									<fmt:message key="matrix_groups_showall"></fmt:message>
									</option>
									<c:forEach var="group" items="${userGroups}">
										<option value="<c:out value="${group.id}"/>" <c:if test="${filteredGroup == group.id}">selected="selected"</c:if>>
											<c:out value="${group.title}"></c:out>
										</option>
									</c:forEach>
								</select>
							</c:if>
							 &nbsp;&nbsp;&nbsp;
							<label for="view_user-id"><fmt:message key="matrix_viewing_select_user" /></label>
							<select name="view_user"  id="view_user-id" onchange="this.form.submit()">
								<c:forEach var="user" items="${members}">
									<option value="<c:out value="${user.id}"/>" <c:if test="${matrixOwner.id.value == user.id}"> selected="selected" </c:if>>
										<c:out value="${user.sortName}"/>
									</option>
								</c:forEach>
							</select>
							<input type="hidden" name="scaffolding_id" value="<c:out value="${matrixContents.scaffolding.id.value}" />" />
						</div>
					</form>
				</c:otherwise>
			</c:choose>
		</c:if>
	</div>	
</c:if>

<c:if test="${not empty matrixContents.columnLabels}">
	<p class="instruction">
		<fmt:message key="instructions_clickOnaCellToEdit"/>
	</p>
	<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
        <table cellspacing="0" width="100%" summary="<fmt:message key="table_summary_matrixScaffolding"/>">
            <tr>
                <th class="matrix-row-heading" scope="col">
                    <osp-h:glossary link="true" hover="true">
                       <c:out value="${matrixContents.scaffolding.title}"/>
                    </osp-h:glossary>
                </th>
                <c:forEach var="head" items="${columnHeading}">
                    <th class="matrix-column-heading matrixColumnDefault" 
                        bgcolor="<c:out value="${head.color}"/>"
                        style="color: <c:if test="${not empty head.textColor}" ><c:out value="${head.textColor}"/></c:if>"  scope="col">
                        <osp-h:glossary link="true" hover="true">
                              <c:out value="${head.description}"/>
                        </osp-h:glossary>
                    </th>
                </c:forEach>
            </tr>   
            <c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
              <tr>
                    <th class="matrix-row-heading matrixRowDefault" bgcolor="<c:out value="${rowLabel.color}"/>" 
                    		style="color: <c:if test="${not empty rowLabel.textColor}" ><c:out value="${rowLabel.textColor}"/></c:if>" scope="row"> 
							 <osp-h:glossary link="true" hover="true">
                           <c:out value="${rowLabel.description}"/>
							</osp-h:glossary>
                    </th>

                 <c:forEach var="cellBean" items="${matrixContents.matrixContents[loopStatus.index]}">
                     <c:set var="cell" value="${cellBean.cell}"/>
                     
                     <td class="matrix-cell-border matrix-<c:out value="${cell.status}"/>" onclick="hrefViewCell('<c:out value="${cell.wizardPage.id}"/>') " style="cursor:pointer">
                        &nbsp;
						<a href="#" onclick="hrefViewCell('<c:out value="${cell.wizardPage.id}"/>') " class="skip"><fmt:message key="table_cell_link_title"/></a>
                        <c:forEach var="node" items="${cellBean.nodes}">
                            <fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="MM/dd/yyyy" var="date"/>
                               <c:set var="hover" value="Name:${node.name}; Size:${node.technicalMetadata.size} bytes; Last Modified: ${date}"/>
                               <img border="0" title="<c:out value="${hover}" />"
                               	alt="<c:out value="${node.name}"/>" 
                               	src="/library/image/<osp-c:contentTypeMap fileType="${node.mimeType}" mapType="image" />"/>
                        </c:forEach>
                        <c:if test="${ !(empty cellBean.assignments)}">
                          <br/>&nbsp;
						  <c:forEach var="node" items="${cellBean.assignments}">
							<img src = '/library/image/silk/page_white_edit.png' border= '0' alt ='' mapType="image" />
						   </c:forEach>
                        </c:if>
                     </td>
                 </c:forEach>
              </tr>
            </c:forEach>
        </table>
        
        <%@ include file="matrixLegend.jspf" %>
    
    </c:if>