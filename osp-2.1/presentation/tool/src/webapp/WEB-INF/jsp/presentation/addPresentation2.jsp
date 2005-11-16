<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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
</script>

<h3>Add Content</h3>

<c:set var="targetPrevious" value="_target1" />
<c:set var="targetNext" value="_target3" />
<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>

<form method="POST" name="wizardform" action="addPresentation.osp"
    onsubmit="updateItems();"><input type="hidden" name="direction"
    value="" />
    
    <osp:form />
    
    <div class="instruction">
        Select the instances of your items for use in this presentation.
    </div>

    <spring:bind path="presentation.items">
        <table class="alternating">
            <c:forEach var="itemDefinition" items="${types}"
                varStatus="loopCounter">
    
                <c:if test="${loopCounter.index % 2 == 0}">
                    <c:set var="alternating">class="bg"</c:set>
                </c:if>
                <c:if test="${loopCounter.index % 2 != 0}">
                    <c:set var="alternating"></c:set>
                </c:if>
    
                <tr <c:out escapeXml="false" value="${alternating}"/>>
                    <td>
                    <table cellspacing="0" width="100%">
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
    
                                <c:set var="previewFrame">artifactMetadata<c:out
                                        value="${loopCounter.index}" />
                                </c:set>
                                <c:set var="previewButton">previewButton<c:out
                                        value="${loopCounter.index}" />
                                </c:set>
                                <c:set var="closeButton">metadataArea<c:out
                                        value="${loopCounter.index}" />
                                </c:set>
                                <c:set var="selectBox">
                                    <c:out value="${list1}" />
                                </c:set>
                                <c:set var="nothingSelectedMessage">please select an item first</c:set>
                                <c:set var="previewUrl">"<osp:url
                                        value="/viewNode.osp" />&panelId=<c:out
                                        value="${previewFrame}" />&type=<c:out
                                        value="${itemDefinition.type}" />&viewOnly=true&nodeId=" + getNodeId("<c:out
                                        value="${selectBox}" />")</c:set>
    
                                <tr class="underline">
                                    <td class="underline" width="40%"
                                        rowspan="2" valign="top">
                                    <h3><c:out
                                        value="${itemDefinition.title}" /></h3>
                                    <br />
                                    <c:out
                                        value="${itemDefinition.description}" /></td>
                                    <td class="underline" colspan="3"
                                        align="center" valign="top">Available
                                    Items :</td>
                                </tr>
                                <tr class="underline">
                                    <td width="23%" align="center"
                                        valign="top">
                                        
                                        <select multiple="true"
                                        size="10"
                                        onchange='closeFrame("<c:out value="${previewFrame}"/>", "<c:out value="${previewButton}"/>","<c:out value="${closeButton}"/>")'
                                        onDblClick='move("<c:out value="${list1}"/>","<c:out value="${list2}"/>",false);closeFrame("<c:out value="${previewFrame}"/>", "<c:out value="${previewButton}"/>","<c:out value="${closeButton}"/>")'
                                        id="<c:out value="${list1}"/>"
                                        name="<c:out value="${list1}"/>"
                                        style="width:175">
                                        <c:forEach var="artifact"
                                            items="${artifacts[itemDefinition.id.value]}">
                                            <c:set var="value">
                                                <c:out
                                                    value="${itemDefinition.id.value}" />.<c:out
                                                    value="${artifact.id.value}" />
                                            </c:set>
                                            <c:set var="found" value="false" />
                                            <c:forEach var="next"
                                                items="${items}">
                                                <c:if
                                                    test="${value eq next}">
                                                    <c:set var="found"
                                                        value="true" />
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${found == false}">
                                                <option
                                                    value="<c:out value="${value}" />">
                                                <c:out
                                                    value="${artifact.displayName}" />
                                                </option>
                                            </c:if>
                                        </c:forEach>
                                    </select></td>
                                    <td width="12%" align="center"
                                        valign="top">
                                    <div class="chefButtonRow"><input
                                        name="add" type="button" style="width:100px;"
                                        onClick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',false)"
                                        value="Add >"> <br />
                         <input name="add all" type="button"  style="width:100px;" onClick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',true)" value="Add All >>"> <br/>
                         <br />
                                    <input name="remove" type="button"  style="width:100px;"
                                        onClick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',false)"
                                        value="Remove <"> <br/>
                         <input name="remove all" type="button"  style="width:100px;" onClick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',true)" value="Remove All <<"> <br/>
                      </div>
                   </td>
                   <td width="25%" align="center" valign="top">
    
                      <select
                            multiple="true"
                            size="10"
                            onDblClick='move("<c:out value="${list2}"/>","<c:out value="${list1}"/>",false);closeFrame("<c:out value="${previewFrame}"/>", "<c:out value="${previewButton}"/>","<c:out value="${closeButton}"/>")'
                            id="<c:out value="${list2}"/>"
                            name="<c:out value="${status.expression}"/>"
                            style="width:175">
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
    
                </c:when>
                <c:otherwise>
                <c:set var="previewFrame">artifactMetadata<c:out value="${loopCounter.index}"/></c:set>
                <c:set var="previewButton">previewButton<c:out value="${loopCounter.index}"/></c:set>
                <c:set var="closeButton">metadataArea<c:out value="${loopCounter.index}"/></c:set>
                <c:set var="selectBox"><c:out value="${status.expression}"/><c:out value="${loopCounter.index}"/></c:set>
                <c:set var="nothingSelectedMessage">please select an item first</c:set>
                <c:set var="previewUrl">"<osp:url value="/viewNode.osp"/>&
                                        panelId=<c:out value="${previewFrame}"/>
                                        &type=<c:out value="${itemDefinition.type}"/>
                                        &viewOnly=true&nodeId=" + getNodeId("<c:out value="${selectBox}"/>")</c:set>
                                <tr class="underline">
                                    <td class="underline" valign="top">
                                    <h3><c:out
                                        value="${itemDefinition.title}" /></h3>
                                    <br />
                                    <c:out
                                        value="${itemDefinition.description}" /></td>
                                    <td align="right" valign="top">Available
                                    Items : <select
                                        id="<c:out value="${selectBox}"/>"
                                        name="<c:out value="${status.expression}"/>"
                                        onchange='closeFrame("<c:out value="${previewFrame}"/>", "<c:out value="${previewButton}"/>","<c:out value="${closeButton}"/>")'>
                                        <option value="">Please select an
                                        item</option>
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
                                                <c:forEach var="next" items="${items}"><c:if test="${value eq next}">selected</c:if></c:forEach>>
                                            <c:out
                                                value="${artifact.displayName}" />
                                            </option>
                                        </c:forEach>
                                    </select></td>
                                </tr>
                                <tr>
                                    <td colspan="2">
                                    <div style="visibility:visible"
                                        id="<c:out value="${previewButton}"/>">
                                    <a href="#"
                                        onclick='return showFrame("<c:out value="${selectBox}"/>","<c:out value="${previewFrame}"/>", "<c:out value="${previewButton}"/>","<c:out value="${closeButton}"/>",<c:out escapeXml="false" value="${previewUrl}"/>,"<c:out value="${nothingSelectedMessage}"/>")'>preview
                                    selected item</a></div>
                                    <div style="visibility:hidden"
                                        id="<c:out value="${closeButton}"/>">
                                    <a href="#"
                                        onclick='return closeFrame("<c:out value="${previewFrame}"/>", "<c:out value="${previewButton}"/>","<c:out value="${closeButton}"/>")'>close
                                    preview</a> <iframe
                                        name="<c:out value="${previewFrame}"/>"
                                        id="<c:out value="${previewFrame}"/>"
                                        height="0" width="650"
                                        frameborder="0" marginwidth="0"
                                        marginheight="0" scrolling="yes"> </iframe>
                                    </div>
                                    </td>
                                </tr>
    
                                </c:otherwise>
                        </c:choose>
                    </table>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </spring:bind>
    <c:set var="suppress_submit" value="true" />
    
    <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
</form>
