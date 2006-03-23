<%-- ###NOTE###
If you want to disable all of the controls on this page, put the following line 
in the page that is including this one:
<c:set var="disabledText" value="disabled=\"disabled\""/>
--%>

   <spring:bind path="${controlName}">
   <table>
      <tr>
      <td colspan="3"><fmt:message key="table_header_filterBy"/>
        <select id="filterSelect" name="filterSelect" onchange='updateOrLoadParticipantList("<c:out value="${status.expression}"/>", "filterSelect","select1","<c:out value="${status.expression}"/>");'
        		<c:out value="${disabledText}"/>>
          <option selected value="roles">All Roles</option>
          <option <c:if test="${filterSelect=='all'}">selected</c:if> value="all"><fmt:message key="optionControls_allParticipants"/></option>
            <c:forEach var="role" items="${roles}">
                <option  <c:if test="${filterSelect==role.id.value}">selected</c:if> value="<c:out value="${role.id.value}"/>">
                    <fmt:message key="optionControls_allWithRole">
                        <param><c:out value="${role.displayName}"/></param>
                    </fmt:message>
                </option>
            </c:forEach>
        </select><br/>
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
      </td>
      </tr>
      <tr>
      <td>
         <select  name="select1"
                  id="select1"
                  size="10"
                  multiple="multiple"
                  width="250"
                  style="width: 250;"
                  onDblClick='move("select1","<c:out value="${status.expression}"/>",false);'
                  <c:out value="${disabledText}"/>>
         </select>
      </td>
      <td>
         <div class="act">
            <input name="add" type="button" onClick="move('select1','<c:out value="${status.expression}"/>',false)" value="<fmt:message key="button_add"/> >" <c:out value="${disabledText}"/> style="width:100px;"> <br/>
            <input name="remove" type="button" onClick="move('<c:out value="${status.expression}"/>','select1',false)" value="<fmt:message key="button_remove"/> <" <c:out value="${disabledText}"/> style="width:100px;"> <br/>
            <br/>
            <input name="add all" type="button" onClick="move('select1','<c:out value="${status.expression}"/>',true)" value="<fmt:message key="button_addAll"/> >>" <c:out value="${disabledText}"/> style="width:100px;"> <br/>
            <input name="remove all" type="button" onClick="move('<c:out value="${status.expression}"/>','select1',true)" value="<fmt:message key="button_removeAll"/> <<" <c:out value="${disabledText}"/> style="width:100px;"> <br/>
         </div>
      </td>
      <td>
         <select  name="<c:out value="${status.expression}"/>"
                  id="<c:out value="${status.expression}"/>"
                  size="10"
                  multiple="multiple"
                  width="250"
                  style="width: 250;"
                  onDblClick='move("<c:out value="${status.expression}"/>","select1",false);'
                  <c:out value="${disabledText}"/>>
               <c:forEach var="_viewer" varStatus="status" items="${object}">
                  <option value="<c:out value="${_viewer.id.value}"/>"><c:out value="${_viewer.displayName}"/> (<c:out value="${_viewer.id.value}"/>)</option>
               </c:forEach>
         </select>
      </td>
      </tr>
   </table>
</spring:bind>
