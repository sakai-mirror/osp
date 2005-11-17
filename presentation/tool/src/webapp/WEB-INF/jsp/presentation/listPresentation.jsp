<%@ page import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<osp-c:authZMap prefix="osp.presentation." var="can" />

<!-- GUID=<c:out value="${newPresentationId}"/> -->

<div class="navIntraTool">
    <c:if test="${can.create}">
        <a href="<osp:url value="addPresentation.osp"/>&resetForm=true"
            title="New..."> New... </a>
    </c:if>
    <a href="<osp:url value="myComments.osp">
               <osp:param name="sortByColumn" value="created"/>
               <osp:param name="direction" value="desc"/>
            </osp:url>"
        title="My Comments..."> My Comments... </a>
    <a href="<osp:url value="commentsForMe.osp">
               <osp:param name="sortByColumn" value="created"/>
               <osp:param name="direction" value="desc"/>
            </osp:url>"
        title="Comments from others about me..."> Comments from others... </a>

    <c:if test="${isMaintainer}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
                <osp:param name="name" value="presentation"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listPresentationRedirect"/>
                </osp:url>"
            title="Permissions..."> Permissions... </a>
    </c:if>
</div>

<c:forEach var="presentation" items="${presentations}" varStatus="presentationStatus">
   <c:set var="presentationCount" value="${presentationStatus.count}" scope="request" />
  </c:forEach>


<osp:url var="listUrl" value="listPresentation.osp"/>
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />

<h3>Presentation Manager</h3>

<table class="listHier" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col">Name</th>
         <th scope="col">Date Modified</th>
         <th scope="col">Template</th>
         <th scope="col">Owner</th>
         <th scope="col">Expired</th>
      </tr>
   </thead>
    <tbody>
  <c:forEach var="presentation" items="${presentations}">
    <c:set var="isAuthorizedTo" value="${presentation.authz}" />

    <TR>
      <TD nowrap>
      <c:out value="${presentation.name}" />
         <div class="itemAction">
             <a <c:if test="${presentation.template.includeHeaderAndFooter == false}">target="_new"</c:if>
                href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">View</a>
    
             <c:if test="${isAuthorizedTo.edit || can.edit}">
               | <a href="<osp:url value="addPresentation.osp"/>&resetForm=true&id=<c:out value="${presentation.id.value}" />">Edit</a>
             </c:if>
    
             <c:if test="${isAuthorizedTo.delete || can.delete}">
               | <a onclick="return confirmDeletion();" href="<osp:url value="deletePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">Delete</a>
             </c:if>
    
              <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
               | <a href="<osp:url value="PresentationStats.osp"/>&id=<c:out value="${presentation.id.value}" />">View Stats</a>
             </c:if>

              <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
               | <a href="<osp:url value="osp.audience.helper/tool.jsf?panel=Main">
                   <osp:param name="session.org.theospi.portfolio.security.audienceFunction"
                        value="osp.presentation.view"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceQualifier"
                        value="${presentation.id.value}"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceInstructions"
                        value="Add viewers to your presentation"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceGlobalTitle"
                        value="Audiences to Publish To"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceIndTitle"
                        value="Publish to an Individual"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceGroupTitle"
                        value="Publish to a Group"/>
                   <osp:param name="session.org.theospi.portfolio.security.audiencePublic"
                        value="${presentation.isPublic}"/>
                   <osp:param name="session.org.theospi.portfolio.security.audiencePublicTitle"
                        value="Publish to the Internet"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceSelectedTitle"
                        value="Selected Audience"/>
                   <osp:param name="session.org.theospi.portfolio.security.audienceGuestEmail"
                        value="true"/>
                   </osp:url>"title="Select Audience..." >Select Audience...
                 </a>
             </c:if>
         </div>
      </TD>
      <TD><fmt:formatDate value="${presentation.modified}" pattern="yyy-MM-dd hh:mm a" /></TD>
      <TD><c:out value="${presentation.template.name}" /></TD>
      <TD><c:out value="${presentation.owner.displayName}" /></TD>
      <TD style="text-align: center;">
         <c:if test="${presentation.expired}">
            <img alt="This presentation has expired"  src="<osp:url value="/img/checkon.gif"/>" border="0"/>
         </c:if>
      </TD>
    </TR>
  </c:forEach>
   </tbody>
  </table>
