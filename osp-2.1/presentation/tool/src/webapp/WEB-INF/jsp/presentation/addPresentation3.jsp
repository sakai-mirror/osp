<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<%@ include file="/WEB-INF/jsp/userSelectFunctions.inc" %>

<h3><fmt:message key="title_addPresentation3"/></h3>

<c:set var="targetPrevious" value="_target3" />
<c:set var="targetNext" value="_finish_notify" />
<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>


<form method="POST" name="wizardform" action="addPresentation.osp"
    onsubmit='return updateItems("viewers");'>
    <osp:form />

    <div class="instruction">
        <fmt:message key="instructions_addPresentation3"/>
    </div>

    <h4><fmt:message key="addPresentation3_worksiteUsers"/></h4>
    
    <c:set var="controlName" value="presentation.viewers" />
    <c:set var="object" value="${presentation.viewers}" />
    <%@ include file="/WEB-INF/jsp/userSelectControls.inc"%>
    
    <br /><br />
    
    <h4><fmt:message key="addPresentation3_otherUser"/></h4>
    
    <p class="instruction">
        <fmt:message key="instructions_addPresentation3_enterEmailUser"/><c:if 
        test="${allowGuests == 'true'}"><fmt:message key="instructions_addPresentation3_enterEmailOther"/></c:if>.
    </p>
    <spring:bind path="viewer.displayName">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <label><fmt:message key="label_email"/></label>
            <c:if test="${status.error}">
                <c:set var="otherUserValue" value="${status.value}" />
            </c:if>
            <input align="absmiddle" 
                name="<c:out value="${status.expression}"/>" 
                id="<c:out value="${status.expression}"/>" type="text" 
                size="30" value="<c:out value="${otherUserValue}"/>"/>
            &nbsp;
            <input type="button" value="<fmt:message key='button_add'/>"
                onclick="addViewer('<c:out value="${status.expression}"/>','<osp:url value="addViewer.osp"/>&isGuest=true&displayName=');"
        />
        </p>
    </spring:bind>
    <br />
        <%--
    
    <tr>
    <td class="chefLabel" nowrap="nowrap"><fmt:message key="table_header_otherUsers"/></td>
    <td nowrap>
      <div class="chefButtonRow">
      <input align="absmiddle" name="newViewer" type="text" size="30" />&nbsp; <input type="button" value="<fmt:message key="button_add"/>" onclick="alert('<fmt:message key="alert_notImplemented"/>');"/>
      </div>
    </td>
    </tr>
    <c:if test="${allowGuests == 'true'}">
    <tr>
    <td class="chefLabel" nowrap="nowrap"><fmt:message key="table_header_guestEmail"/></td>
    <td nowrap>
      <div class="chefButtonRow">
      <input align="absmiddle" name="newViewer" type="text" size="30" />&nbsp; <input type="button" value="<fmt:message key="button_add"/>" onclick="alert('<fmt:message key="alert_notImplemented"/>');"/>
      </div>
    </td>
    </tr>
    </c:if>
    --%>
    
    <h4><fmt:message key="addPresentation3_sharing"/></h4>
    
        <spring:bind path="presentation.isPublic">
            <fieldset>
                <legend class="radio"><fmt:message key="legend_makePublicShare"/></legend>
                <div class="checkbox indnt1">
                    <input type="radio" id="publicYes"
                        name="<c:out value="${status.expression}"/>"
                        value="true" <c:if test="${status.value}">checked</c:if> />
                    <label for="publicYes"><fmt:message key="label_yes"/></label>
                </div>
                <div class="checkbox indnt1">
                    <input type="radio" id="publicNo"
                        name="<c:out value="${status.expression}"/>"
                        value="false"
                        <c:if test="${status.value == false}">checked</c:if> />
                    <label for="publicNo"><fmt:message key="label_no"/></label>
                </div>
            </fieldset>
                     
            <p class="instruction">
                <fmt:message key="instructions_addPresentation3_publicPresentation"/>
            </p>
            <c:if test="${!empty presentation.id}">
                <fmt:message key="instructions_addPresentation3_sendPublicLink"/><br />
                 <c:set var="url">
                        <c:out value="${baseUrl}"/>/osp-presentation-tool/viewPresentation.osp?id=<c:out value="${presentation.id.value}"/>
                </c:set>
                <a target="_new" href="<c:out value="${url}"/>"><c:out
                    value="${url}" /></a>
            </c:if>
        </spring:bind>
        <br /><br />
    <c:set var="suppress_next" value="true" />
    <%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
</form>
    
    <script type="text/javascript" language="JavaScript">
       //prepopulate first select box
       updateParticipantList("filterSelect","select1","viewers");
    </script>

<%--

<script>
<!--
   function deleteUrl(){
      url = "<osp:url value="deleteViewer.osp"/>";
      for (var i=0; i < <c:out value="${count}"/>; i++){
            checkbox = ospGetElementById('selectedViewer' + i);
            if (checkbox.checked == true){
               url += "&id=" + checkbox.value;
            }
      }
      return url;
   }

-->
</script>
--%>
