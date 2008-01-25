<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<script type="text/javascript" language="JavaScript">
    function updateItems() {
       var arrBox = new Array();
       var i = 0;
       var j = 0;
    
    <c:forEach var="itemDefinition" items="${types}" varStatus="loopCounter">
       <c:if test="${itemDefinition.allowMultiple == true}">
          arrBox[i] = ospGetElementById('items_<c:out value="${loopCounter.index}"/>');
          i++;
       </c:if>
    </c:forEach>
       for (i = 0; i < arrBox.length; i++) {
          var nextBox = arrBox[i];
          for (j = 0; j < nextBox.options.length; j++) {
             nextBox.options[j].selected = true;
          }
       }
       return true;
    }
    
    function getNodeId(elementName){
       var element = ospGetElementById(elementName);
       var index = element.selectedIndex;
    
       if (index == -1 || undefined == index){
          return;
       }
    
       var key = element.options[index].value;
       var values = new Array();
    <c:forEach var="itemDefinition" items="${types}" varStatus="loopCounter">
    <c:forEach var="artifact" items="${artifacts[itemDefinition.id.value]}">
       values["<c:out value="${itemDefinition.id.value}"/>.<c:out value="${artifact.id.value}"/>"] = "<c:out value="${artifact.id.value}"/>";
       <c:set var="value"><c:out value="${itemDefinition.id.value}"/>.<c:out value="${artifact.id.value}"/></c:set>
    </c:forEach>
    </c:forEach>
    
       return values[key];
    }
    
    <c:if test="${preview == true}">
      window.open('<osp:url value="/viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />');
    </c:if>
</script>

<c:set var="targetPrevious" value="_target2" />
<c:set var="targetPreview" value="_target3"/>
<c:set var="targetNext" value="_target4"/>
<c:set var="begin_state" value="current_state"/>
<c:set var="design_state" value="next_state"/>
<c:set var="publish_state" value="next_state"/>
<c:set var="step" value="2" />

<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>

<form method="post" name="wizardform" action="addPresentation.osp"
    onsubmit="updateItems();"><input type="hidden" name="direction"
    value="" />
    <input type="hidden" name="preview" value="" />
    
    <osp:form />
    
    <div class="instruction">
        <fmt:message key="instructions_addPresentation2"/>
    </div>
    <spring:bind path="presentation.items">
        <div class="alternating">
            <c:forEach var="itemDefinition" items="${types}"
                varStatus="loopCounter">
    
                <c:if test="${loopCounter.index % 2 == 0}">
                    <c:set var="alternating">class="bg"</c:set>
                </c:if>
                <c:if test="${loopCounter.index % 2 != 0}">
                    <c:set var="alternating"></c:set>
                </c:if>
    
                <div <c:out escapeXml="false" value="${alternating}"/>>

                    <div class="highlightPanel" style="margin:0">
                        <c:choose>
						<c:when
                                test="${itemDefinition.allowMultiple == true}">
						
                                <c:set var="list1">
                                    <c:out value="${status.expression}" />_unselected_<c:out
                                        value="${loopCounter.index}" />
                                </c:set>
                                <c:set var="list2">
                                    <c:out value="${status.expression}" />_<c:out
                                        value="${loopCounter.index}" />
                                </c:set>
    
                                <c:set var="selectBox">
                                    <c:out value="${list1}" />
                                </c:set>
	<h3><c:out value="${itemDefinition.title}" /></h3>
	<div class="textPanel"><c:out value="${itemDefinition.description}" /></div>
	<table class="sidebyside" border="0" summary="<fmt:message key="item_selection_table_summary_step2"/>">
		<tr>
			<th><fmt:message key="label_availableItems_step2"/></th>
			<th></th>
			<th><fmt:message key="label_selectedItems_step2"/></th>
		</tr>
		<tr>
			<td>
				<select multiple="multiple"
					size="10"
					ondblclick='move("<c:out value="${list1}"/>","<c:out value="${list2}"/>",false);'
					id="<c:out value="${list1}"/>"
					name="<c:out value="${list1}"/>">
					<c:forEach var="artifact"
						items="${artifacts[itemDefinition.id.value]}">
						<c:set var="value">
							<c:out value="${itemDefinition.id.value}" />.<c:out
								value="${artifact.id.value}" />
						</c:set>
						<c:set var="found" value="false" />
						<c:forEach var="next"
							items="${items}">
							<c:if
								test="${value eq next}">
								<c:set var="found" value="true" />
							</c:if>
						</c:forEach>
						<c:if test="${found == false}">
							<option
								value="<c:out value="${value}" />">
								<c:out value="${artifact.displayName}" />
							</option>
						</c:if>
					</c:forEach>
				</select>
			</td>
			<td style="text-align:center">
				<input name="add"  type="button"
					onclick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',false)"
					value="<fmt:message key="button_add"/> >" 
				/> 
				<br />
				<input name="add all" type="button" 
					onclick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',true)" 
					value="<fmt:message key="button_addAll"/> >>" 
				/>
				<hr class="itemSeparator" />
				<input name="remove" type="button"
					onclick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',false)"
					value="<fmt:message key="button_remove"/> <"
				/>
				<br />
				<input name="remove all" type="button" 
					onclick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',true)" 
					value="<fmt:message key="button_removeAll"/> <<"
				/>
			</td>
			<td>
				<select
					multiple="multiple"
					size="10"
					ondblclick='move("<c:out value="${list2}"/>","<c:out value="${list1}"/>",false);'
					id="<c:out value="${list2}"/>"
					name="<c:out value="${status.expression}"/>">
					<c:forEach var="artifact" items="${artifacts[itemDefinition.id.value]}">
						<c:set var="value"><c:out value="${itemDefinition.id.value}"/>.<c:out value="${artifact.id.value}"/></c:set>
						<c:forEach var="next" items="${items}">
							<c:if test="${value eq next}">
								<option value="<c:out value="${value}" />">
									<c:out value="${artifact.displayName}"/>
								</option>
							</c:if>
						</c:forEach>
					</c:forEach>
				</select>
			</td>
		</tr>
	</table>
</c:when>
<c:otherwise>
                <c:set var="selectBox"><c:out value="${status.expression}"/><c:out value="${loopCounter.index}"/></c:set>
                
                                <div class="navPanel" style="background:transparent;">
                                    <div class="viewNav">
                                    <h3><c:out
                                        value="${itemDefinition.title}" /></h3>
                                    </div>
									
                                    <div class="listNav">
									<label  for="<c:out value="${selectBox}"/>"><fmt:message key="label_availableItems"/></label>
									<select
                                        id="<c:out value="${selectBox}"/>"
                                        name="<c:out value="${status.expression}"/>">
                                        <option value=""><fmt:message key="addPresentation2_selectItem"/>
                                           </option>
                                        <option value="">- - - - - - - - - -
                                        - - - - - - - - - - -</option>
                                        <c:forEach var="artifact"
                                            items="${artifacts[itemDefinition.id.value]}">
                                            <c:set var="value">
                                                <c:out
                                                    value="${itemDefinition.id.value}" />.<c:out
                                                    value="${artifact.id.value}" />
                                            </c:set>
                                            <option
                                                value="<c:out value="${value}" />"
                                                <c:forEach var="next" items="${items}"><c:if test="${value eq next}">selected="selected"</c:if></c:forEach>>
                                            <c:out
                                                value="${artifact.displayName}" />
                                            </option>
                                        </c:forEach>
                                    </select>
									</div>
								</div>
								<c:if test="${not empty itemDefinition.description}">
								<div class="textPanel indnt1">
															<c:out
                                        value="${itemDefinition.description}" />
								</div>		
								</c:if>
			
                                </c:otherwise>
                        </c:choose>
                    </div>
                    </div>
            </c:forEach>
        </div>
    </spring:bind>
    <c:set var="suppress_submit" value="true" />
    <c:set var="previewPres" value="true" />
    <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
</form>
