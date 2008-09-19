<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<table width="100%">
   <tr>
      <td align="left">
          <h3><c:out value="${presentation.name}" /></h3>
      </td>
      <td align="right">
		  <a <c:if test="${presentation.template.includeHeaderAndFooter == false}">target="_blank" title="<fmt:message key="table_presentationManager_new_window"/>"</c:if>
					href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">
               <fmt:message key="pres_preview" />
		  </a>
	   </td>
   </tr>
</table>
   
<br/> <!-- temp separation; start of tabs -->
<div class="navIntraTool">
 [ 
 <fmt:message key="pres_summary"/>
 | 
 <fmt:message key="pres_content"/>
 | 
 <fmt:message key="pres_required"/>
 | 
 <fmt:message key="pres_share"/>
 ]
</div>
<br/> <!-- temp separation; end of tabs -->

<h3>
   <fmt:message key="pres_share_this"/>
</h3>

<form method="post" name="mainForm">
   <div class="checkbox">
      <input type="checkbox" name="pres_share_public" id="pres_share_public"
         <c:if test="${pres_share_public=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_public">
         <fmt:message key="pres_share_public"/>
      </label>
      
      <a href="${publicUrl}" target="_blank_"><fmt:message key="pres_share_here"/></a>
   </div>
      
   <div class="checkbox">
      <input type="checkbox" name="pres_share_select" id="pres_share_select" disabled="true"
         <c:if test="${pres_share_select=='true'}"> checked="checked"</c:if>
      />
      <label for="pres_share_select">
         <fmt:message key="pres_share_select"/>
      </label>
   </div>
   
   <br/>
   
   <c:choose>
     <c:when test="${empty shareList}">
         <fmt:message key="pres_share_none"/>
         <%-- OLD LINK
         <a href="<osp:url value="addPresentation.osp"/>&target=_target5&resetForm=true&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_add"/></a>
         --%>
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_add"/></a>
     </c:when>
     
     <c:otherwise>
         <table width="80%" rules="groups">
         <thead>
         <tr>
         <td><b><fmt:message key="pres_share_list"/></b></td>
         <td align="right">
         <%-- OLD LINK
         <a href="<osp:url value="addPresentation.osp"/>&target=_target5&resetForm=true&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_more"/></a>
         --%>
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share_more"/></a>
         </td>
         </tr>
         </thead>
         
         <tbody>
         <c:forEach var="shareMember" items="${shareList}"> 
           <tr><td colspan="2">
           <div class="checkbox">
           <input type="checkbox" name="${shareMember.id.value}" id="${shareMember.id.value}" />
           <label for="${shareMember.id.value}">
           <c:out value="${shareMember.displayName}" />
           <c:if test="${shareMember.role}"> <fmt:message key="pres_share_role"/></c:if>
           </label>
           </div>
           </td></tr>
         </c:forEach>
         </tbody>
         
         <tfoot>
         <tr>
         <td colspan="2" align="right">
            <a href="javascript:document.mainForm.submit();"><fmt:message key="pres_share_rem"/></a> 
         </td>
         </tr>
         </tfoot>
         </table>
     </c:otherwise>
   </c:choose>

   <div class="act">
      <input name="save" type="submit" value="<fmt:message key="button_saveEdit" />" class="active" accesskey="s" />
      <input name="undo" type="submit" value="<fmt:message key="button_undo" />"  accesskey="x" />
   </div>
</form>
