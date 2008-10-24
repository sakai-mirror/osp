<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.presentation.bundle.Messages"/>

<script type="text/javascript" src="/library/js/jquery.js"></script>

<script  type ="text/javascript">
$(document).ready(function() {
	setupMessageListener("messageHolder", "messageInformation");
	$(".multSelectHolder").each(function(){
		if ($(this).height() > 180) {
		$(this).addClass("oversize")
}
})

	$(".multSelectHolder input:checkbox").click( function() {
		if ($(this).attr('checked')) {
		$(this).parents("li").addClass("selected")
		}
		else
		{
		$(this).parents("li").removeClass("selected")
		}
})

	jQuery('body').click(function(e) { 
			
		if ( e.target.className !='menuOpen' && e.target.className !='dropdn'  ){
			$('.makeMenuChild').fadeOut();
		}
			else
			{
				if( e.target.className =='dropdn' ){
			targetId=$(e.target).parent('li').attr('id');
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);

}
				else{
			targetId=e.target.id;
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);
			}}
	});
	});

</script>

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

<c:set var="pres_active_page" value="content"/>
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>

<script type="text/javascript">
$(document).ready(function() {
	osp.bag.selections = {};
	$('select.artifactPicker').change(function() {
		osp.bag.selections[$(this).attr('id')] = $(this).val();
	});
	$('select.artifactPicker').each(function() {
		osp.bag.selections[$(this).attr('id')] = $(this).val();
	});
	osp.bag.formTypes = {};
	<c:forEach var="itemDefinition" items="${types}">
		osp.bag.formTypes['<c:out value="${itemDefinition.id.value}"/>'] = '<c:out value="${itemDefinition.type}"/>';  
	</c:forEach>
	$('a.inlineFormEdit').click(function(ev) {
		ev.preventDefault();
		var item = this.href.substring(this.href.indexOf('#') + 1);
		if (osp.bag.selections[item]) {
			var pieces = osp.bag.selections[item].split('.');
			var itemDefId = pieces[0];
			var formTypeId = osp.bag.formTypes[itemDefId];
			var formId = pieces[1];
			window.location = '<osp:url value="editPresentationForm.osp" />'
					+ '&id=<c:out value="${presentation.id.value}" />'
					+ '&formTypeId=' + formTypeId
					+ '&formId=' + formId;
		}		
	});
});
</script>

<div class="tabNavPanel">
 <!-- temp separation; end of tabs -->

<h3>
   <p class="instructionMessage"><fmt:message key="instructions_addPresentation2"/></p>
</h3>


<c:if test="${actionSave}">
	<div class="messageInformation" id="messageHolder" style="width:20em">
    	<fmt:message key="confirm_save"/>
	</div>
</c:if>
<c:if test="${actionUndo}">
	<div class="messageInformation" id="messageHolder" style="width:20em">
    	<fmt:message key="confirm_undo"/>
	</div>
</c:if>

<form method="post" name="wizardform" action="editContent.osp" onsubmit="updateItems();">
<osp:form/>
    
    <input type="hidden" name="preview" value="" />
    <input type="hidden" name="id" value="<c:out value="${presentation.id.value}" />" />    
    
<div class="editContentPanel">
	<div class="instruction">
		
	</div>
	<spring:bind path="presentation.items">
		<ul class="presentationElementGroup">
			<c:forEach var="itemDefinition" items="${types}"
				varStatus="loopCounter">
	
				<c:if test="${loopCounter.index % 2 == 0}">
					<c:set var="alternating">class="bg"</c:set>
				</c:if>
				<c:if test="${loopCounter.index % 2 != 0}">
					<c:set var="alternating"></c:set>
				</c:if>
	
				<li class="presentationElement">
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
										<th style="padding:0">
											<table width="100%" style="margin:0">
												<tr>
													<td>
														<fmt:message key="label_availableItems_step2"/>
													</td>
													<c:if test="${not itemDefintion.hasMimeTypes}">
														<td style="text-align:right">
															<a href="<osp:url value="editPresentationForm.osp"/>&amp;id=<c:out value="${presentation.id.value}" />&amp;formTypeId=<c:out value="${itemDefinition.type}"/>"
												   class="inlineCreate"><fmt:message key="create_new"/></a>
												   &nbsp;
														</td>
													</c:if>
												</tr>
											</table>	
										</th>
										<th></th>
										<th style="padding:0">
											<table width="100%" style="margin:0">
												<tr>
													<td>
														<fmt:message key="label_selectedItems_step2"/>
													</td>
													<td style="text-align:right">
														<a href="<osp:url value="editPresentationForm.osp"/>&amp;id=<c:out value="${presentation.id.value}" />&amp;formTypeId=<c:out value="${itemDefinition.type}"/>&amp;box=<c:out value="${list1}"/>"
													  class="inlineFormEdit"><fmt:message key="edit_selected"/></a>
													</td>
												</tr>
											</table>	
									  </th>
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
									<div class="viewNav"style="background:transparent;width:60%">
										<h3 style="margin:0;padding:0;"><c:out
											value="${itemDefinition.title}" /></h3>
										<c:if test="${not empty itemDefinition.description}">
											<div class="instruction" style="margin:0">
												<c:out
													value="${itemDefinition.description}" />
											</div>		
										</c:if>

									</div>
									<div class="listNav">
										<label  for="<c:out value="${selectBox}"/>" class="itemAction" style="margin-left:0;padding-left:0;display:block"><span><fmt:message key="label_availableItems"/></span></label>
										<select
											class="artifactPicker"
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
										<span class="itemAction"  style="margin-left:0;padding-left:0;white-space:nowrap;padding-top:4px;display:inline-block;">
											<a href="<osp:url value="editPresentationForm.osp"/>&amp;id=<c:out value="${presentation.id.value}" />&amp;formTypeId=<c:out value="${itemDefinition.type}"/>"
											   class="inlineCreate""><fmt:message key="create_new" /></a>| 
											<a href="#<c:out value="${selectBox}" />"
											   class="inlineFormEdit"><fmt:message key="edit_selected"/></a>
										</span>
									</c:if>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</li>
			</c:forEach>
		</li>
    </spring:bind>
</div>
<div class="act">
   <input name="save" type="submit" value="<fmt:message key="button_saveEdit" />" class="active" accesskey="s" />
   <input name="undo" type="submit" value="<fmt:message key="button_undo" />"  accesskey="x" />
</div>
</form>
</div>
