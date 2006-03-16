<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="targetPrevious" value="_target1"/>
<c:set var="targetNext" value="_target3"/>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<h3><fmt:message key="title_addTemplate3"/></h3>

<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc" %>
<div class="instruction">
   <fmt:message key="instructions_template_new3"/>
</div>

<form  method="POST" action="addTemplate.osp">
<osp:form/>



      <%@ include file="/WEB-INF/jsp/presentation/addItemDefinition.jsp" %>



<table class="listHier" cellspacing="0">
            <thead>
               <tr>
                  <th scope="col" width="12%"><fmt:message key="table_header_sequence"/></th>
                  <th scope="col"><fmt:message key="table_header_title"/></th>
               </tr>
            </thead>
         <tbody>
           <c:if test="${template.itemDefinitions['empty']}">
                 <TR>
                   <TD colspan="2" align="center">
                        <b><fmt:message key="addTemplate_thereIsNoContentYet"/></b>
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
                            <a href="<osp:url value="editItemDefinition.osp"/>&id=<c:out value="${itemDef.id.value}" />"><fmt:message key="action_edit"/></a>
                            |
                            <a href="<osp:url value="deleteItemDefinition.osp"/>&id=<c:out value="${itemDef.id.value}" />"><fmt:message key="action_delete"/></a>
                        </div>
                   </TD>
    
                 </TR>
               </c:forEach>
           </c:if>
         </tbody>
         </table>
<br /><br />

<c:set var="suppress_submit" value="true" />
<%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc" %>

</form>

