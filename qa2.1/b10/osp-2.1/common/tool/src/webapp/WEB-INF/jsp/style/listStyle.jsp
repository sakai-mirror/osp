<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<!-- GUID=<c:out value="${newStyleId}"/> -->

<osp-c:authZMap prefix="osp.style." useSite="true" var="can" />


<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addStyle.osp"/>" title="<fmt:message key="action_new_title"/>" >
          <fmt:message key="action_new"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer && selectableStyle != 'true'}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
                 <fmt:param><c:out value="${tool.title}"/></fmt:param>
                <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
              </osp:param>
                <osp:param name="name" value="style"/>
                <osp:param name="qualifier" value="${worksite.id}"/>
                <osp:param name="returnView" value="listStyleRedirect"/>
                </osp:url>"
                title="<fmt:message key="action_permissions_title"/>" >
          <fmt:message key="action_permissions"/>
          </a>
       </c:if>
    </div>
</c:if>



<osp:url var="listUrl" value="listStyle.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3><fmt:message key="title_styleManager"/></h3>

<c:if test="${not empty styleError}">
   <div class="validation"><fmt:message key="${styleError}"/></div>
</c:if>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col"></th>
         <th scope="col"><fmt:message key="table_header_name"/></th>
         <th scope="col"><fmt:message key="table_header_description"/></th>
         <th scope="col"><fmt:message key="table_header_owner"/></th>
         <th scope="col"><fmt:message key="table_header_global_published"/></th>
      </tr>
   </thead>
   <tbody>
  <c:forEach var="style" items="${styles}">
    <osp-c:authZMap prefix="osp.style." qualifier="${style.id}" var="isAuthorizedTo" />
    <TR>
      <td>&nbsp;
         <c:if test="${selectedStyle == style.id}">
            <img src="<osp:url value="/img/arrowhere.gif"/>" title="Selected Style" />
         </c:if>
      </td>
      <TD nowrap>
         <c:out value="${style.name}" />
         <c:set var="hasFirstAction" value="false" />
         <div class="itemAction">
             <c:if test="${isAuthorizedTo.edit}">
               <a href="<osp:url value="editStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_edit"/></a>
               <c:set var="hasFirstAction" value="true" />
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                 <a onclick="return confirmDeletion();"
                   href="<osp:url value="deleteStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_delete"/></a>
             </c:if>
         
             <!-- Used the regular can.globalPublish instead of the isAuthorizedTo 
               because the style may have come from a different site -->
             <c:if test="${can.globalPublish && (style.globalState == 0 || style.globalState == 1) && isGlobal}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                 <a href="<osp:url value="publishStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&publishTo=global"><fmt:message key="table_action_global_publish"/></a>
             </c:if>
             
             <c:if test="${selectableStyle != 'true' && isAuthorizedTo.suggestGlobalPublish && style.globalState == 0 && !isGlobal}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                 <a href="<osp:url value="publishStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&publishTo=suggestGlobal"><fmt:message key="table_action_suggest_global_publish"/></a>
             </c:if>
             
             <c:if test="${selectableStyle == 'true' and selectedStyle != style.id.value}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                 <a href="<osp:url value="selectStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&selectAction=on"><fmt:message key="table_action_select"/></a>
             </c:if>
             
             <c:if test="${selectableStyle == 'true' and selectedStyle == style.id.value}">
                 <c:if test="${hasFirstAction}" > | </c:if>
                 <c:set var="hasFirstAction" value="true" />
                 <a href="<osp:url value="selectStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&selectAction=off"><fmt:message key="table_action_unselect"/></a>
             </c:if>
             
         </div>
      </TD>
      <TD><c:out value="${style.description}" /></TD>
      <TD><c:out value="${style.owner.displayName}" /></TD>
      <TD><fmt:message key="style_published_status${style.globalState}"/></TD>
    </TR>

  </c:forEach>
  
    </tbody>
  </table>
   <div class="act">
      <c:if test="${selectableStyle == 'true'}">
         <input type="button" name="goBack" class="active" value="<fmt:message key="button_goback"/>"
            onclick="window.document.location='<osp:url value="selectStyle.osp"/>'"/>
      </c:if>
   </div>