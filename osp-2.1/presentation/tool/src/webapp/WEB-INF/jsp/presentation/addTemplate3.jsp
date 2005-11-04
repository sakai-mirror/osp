<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="targetPrevious" value="_target1"/>
<c:set var="targetNext" value="_target3"/>


<h3>List Content</h3>

<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc" %>
<div class="instruction">
   Please specify items that will appear in the presentation. Users of the
   template will fill in content from their repository or by uploading files.
</div>

<form  method="POST" action="addTemplate.osp">
<osp:form/>



      <%@ include file="/WEB-INF/jsp/presentation/addItemDefinition.jsp" %>



<table class="listHier" cellspacing="0">
            <thead>
               <tr>
                  <th scope="col" width="12%">Sequence</th>
                  <th scope="col">Title</th>
               </tr>
            </thead>
         <tbody>
           <c:if test="${template.itemDefinitions['empty']}">
                 <TR>
                   <TD colspan="2" align="center">
                        <b>There is no content yet</b>
                   </TD>
                 </TR>
           </c:if>
           <c:if test="${not template.itemDefinitions['empty']}">
               <c:forEach var="itemDef" items="${template.sortedItems}">
                 <TR>
                   <TD width="12%">
                      <input type="text" name="itemSequence" value="<c:out value="${itemDef.sequence}"/>"
                         size="4" maxlength="4" />
                   </TD>
                   <TD><c:out value="${itemDef.title}" />
                        <div class="itemAction">
                            <a href="<osp:url value="editItemDefinition.osp"/>&id=<c:out value="${itemDef.id.value}" />">Edit</a>
                            |
                            <a href="<osp:url value="deleteItemDefinition.osp"/>&id=<c:out value="${itemDef.id.value}" />">Delete</a>
                        </div>
                   </TD>
    
                 </TR>
               </c:forEach>
           </c:if>
         </tbody>
         </table>
<br /><br />

<c:set var="suppress_submit" value="true" />
<c:set var="updateSeq" value="true" />
<%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc" %>

</form>

