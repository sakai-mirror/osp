<%@ page
    import="org.sakaiproject.service.framework.portal.cover.PortalService"%>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- GUID=<c:out value="${newTermId}" /> -->

<c:if test="${!global}">
    <osp-c:authZMap prefix="osp.help.glossary." var="can" />
</c:if>
<c:if test="${global}">
    <osp-c:authZMap prefix="osp.help.glossary." var="can"
        qualifier="${globalQualifier}" />
</c:if>

<osp-c:authZMap prefix="" var="canWorksite" useSite="true" />

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.glossary.bundle.Messages"/>

<div class="navIntraTool">
    <c:if test="${can.add}">
        <a href="<osp:url value="editGlossaryTerm.osp"/>" title="<fmt:message key="label_title_new"/>">
        <fmt:message key="action_new"/></a>
    </c:if>
    <c:if test="${canWorksite.maintain}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
                    <osp:param name="message"> 
                      <fmt:message key="message_permissionsEdit">
   	                    <fmt:param><c:out value="${tool.title}"/></fmt:param>
		                <fmt:param><c:out value="${worksite.title}"/></fmt:param>
		              </fmt:message>
                    </osp:param>
                    <osp:param name="name" value="glossary"/>
                    <c:if test="${!global}">
                        <osp:param name="qualifier" value="${tool.id}"/>
                    </c:if>
                    <c:if test="${global}">
                        <osp:param name="qualifier" value="theospi.help.glossary.global"/>
                    </c:if>
                    <osp:param name="returnView" value="glossaryListRedirect"/>
                </osp:url>"
            title="<fmt:message key="action_permissions_title"/>">
            <fmt:message key="action_permissions"/></a>
    </c:if>
    <c:if test="${can.export}">
    <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=helpManagerTarget&templateId=<c:out value="${template.id.value}"/>/<c:out value="${worksite.title}" /> Glossary.zip"><fmt:message key="action_export"/></a>
            
    </c:if>
    <c:if test="${can.add}">
        <a href="<osp:url value="importGlossaryTerm.osp"/>" title="<fmt:message key="label_import"/>">
        <fmt:message key="action_import"/> </a>
    </c:if>
</div>



<osp:url var="listUrl" value="glossaryList.osp" />
<osp:listScroll listUrl="${listUrl}" className="navIntraTool" />
<c:if test="${!global}">
    <h3><fmt:message key="title_glossaryManager"/></h3>
</c:if>
<c:if test="${global}">
    <h3><fmt:message key="title_glossaryManagerGlobal"/></h3>
</c:if>


<c:if test="${import_success}">
   <div style="color:#008800"><fmt:message key="import_msg_success"/></div>
</c:if>
<c:if test="${import_unrecognized_file}">
   <div style="color:#880000"><fmt:message key="import_msg_bad_file"/></div>
</c:if>
<c:if test="${import_failed}">
   <div style="color:#880000"><fmt:message key="import_msg_failed"/></div>
</c:if>
<c:if test="${import_bad_parse}">
   <div style="color:#880000"><fmt:message key="import_msg_bad_file_parse"/></div>
</c:if>

<table class="listHier" cellspacing="0">
    <thead>
        <tr>
            <th scope="col"><fmt:message key="label_Term"/></th>
            <th scope="col"><fmt:message key="label_desc"/></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="term" items="${glossary}">

            <TR>
                <TD>
                    <a href="<osp:url value="/glossary.osp" />&id=<c:out value="${term.id}" />"
                        target="osp.glossary"><c:out value="${term.term}" /></a>
                    <c:if test="${can.edit || can.delete}">
                        <div class="itemAction">
                            <c:if test="${can.edit}">
                                <a href="<osp:url value="editGlossaryTerm.osp"/>&id=<c:out value="${term.id}" />"><fmt:message key="table_action_edit"/></a>
                            </c:if>
                            <c:if test="${can.edit && can.delete}">
                                |
                            </c:if>
                            <c:if test="${can.delete}">
                                <a href="<osp:url value="removeGlossaryTerm.osp"/>&id=<c:out value="${term.id}" />"><fmt:message key="table_action_delete"/></a>
                            </c:if>
                        </div>
                    </c:if>
                </TD>

                <TD><c:out value="${term.description}" /></TD>
            </TR>
        </c:forEach>
    </tbody>
</table>