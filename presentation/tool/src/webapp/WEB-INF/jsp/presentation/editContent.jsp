<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.presentation.bundle.Messages"/>

<script type="text/javascript" src="/library/js/jquery.js"></script>
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
    
    <c:if test="${not empty msg}">
    	$(window).load(function() {
    		$('#portfolioMessage').slideDown(1000, function() {
    			$(this).slideUp(3000);
    		});
    		//$('#portfolioMessage').animate({ backgroundColor: 'white' }, 3000, function() {
    			//$(this).slideUp(3000);
    		//});
    	});
    </c:if>
</script>

<%--
<c:if test="${not empty msg}">
	<div id="portfolioMessage" style="display:none; position: absolute; top: 0px; width: 100%;">
		<div style="margin: 0 auto; text-align: center; width: 50%; border: 1px solid black; padding: 2em; font-size: 1.5em; background: #FFFF66;">
		<c:out value="${msg}" />
		</div>
	</div>
</c:if>
--%>

<c:set var="pres_active_page" value="content"/>
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>

<%--  Editing is getting lost somewhere and throwing an NPE
<script type="text/javascript">
$(document).ready(function() {
	$('a.inlineFormEdit').click(function(ev) {
		ev.preventDefault();
		var idx = this.href.lastIndexOf("&");
		var id = this.href.substring(idx+5);
		var href = this.href.substring(0, idx);
		if ($('#' + id).get(0).selectedIndex > 1) {
			var formId = $('#' + id).val();
			formId = formId.substring(formId.indexOf('.') + 1);
			window.location = href + '&formId=' + formId;
		}
	});
});
</script>
--%>

<div class="tabNavPanel">
 <!-- temp separation; end of tabs -->

<h3>
   <fmt:message key="pres_content_heading"/>
</h3>

<form method="post" name="wizardform" action="editContent.osp" onsubmit="updateItems();">
<osp:form/>
    
    <input type="hidden" name="preview" value="" />
    <input type="hidden" name="id" value="<c:out value="${presentation.id.value}" />" />    
    
    <div class="editContentPanel">
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
	<table width="100%" class="sidebyside" border="0" summary="<fmt:message key="item_selection_table_summary_step2"/>">
		<tr>
			<th>
				<fmt:message key="label_availableItems_step2"/>
				<c:if test="${not itemDefintion.hasMimeTypes}">
					<a href="<osp:url value="editPresentationForm.osp"/>&id=<c:out value="${presentation.id.value}" />&formTypeId=<c:out value="${itemDefinition.type}"/>"
					   class="inlineCreate"><fmt:message key="create_new"/></a>
					   <%-- 
					   &nbsp;
					<a href="<osp:url value="editPresentationForm.osp"/>&id=<c:out value="${presentation.id.value}" />&formTypeId=<c:out value="${itemDefinition.type}"/>&box=<c:out value="${list1}"/>"
					   class="inlineFormEdit"><fmt:message key="edit_selected"/></a>
					   --%>
				</c:if>
			</th>
			<th></th>
			<th><fmt:message key="label_selectedItems_step2"/></th>
		</tr>
		<tr>
			<td style="width:40%">
				<select multiple="multiple"
				        style="width:100%"
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
					value="<fmt:message key="button_add"/> &gt;" 
				/> 
				<br />
				<input name="add all" type="button" 
					onclick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',true)" 
					value="<fmt:message key="button_addAll"/> &gt;&gt;" 
				/>
				<hr class="itemSeparator" />
				<input name="remove" type="button"
					onclick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',false)"
					value="<fmt:message key="button_remove"/> &lt;"
				/>
				<br />
				<input name="remove all" type="button" 
					onclick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',true)" 
					value="<fmt:message key="button_removeAll"/> &lt;&lt;"
				/>
			</td>
			<td style="width:40%">
				<select
					multiple="multiple"
					style="width:100%"
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
									<c:if test="${not itemDefintion.hasMimeTypes}">
	                                    <div>
    										<a href="<osp:url value="editPresentationForm.osp"/>&id=<c:out value="${presentation.id.value}" />&formTypeId=<c:out value="${itemDefinition.type}"/>"
    										   class="inlineCreate"><fmt:message key="create_new" /></a>
    										   <%--
    										   &nbsp;
											<a href="<osp:url value="editPresentationForm.osp"/>&id=<c:out value="${presentation.id.value}" />&formTypeId=<c:out value="${itemDefinition.type}"/>&box=<c:out value="${selectBox}"/>"
											   class="inlineFormEdit"><fmt:message key="edit_selected"/></a>
											   --%>
        		                        </div>
									</c:if>
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
</div>
<div class="act">
   <input name="save" type="submit" value="<fmt:message key="button_saveEdit" />" class="active" accesskey="s" />
   <input name="undo" type="submit" value="<fmt:message key="button_undo" />"  accesskey="x" />
</div>
</form>
</div>
