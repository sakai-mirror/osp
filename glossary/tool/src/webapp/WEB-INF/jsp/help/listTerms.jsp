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

<div class="navIntraTool">
    <c:if test="${can.add}">
        <a href="<osp:url value="editGlossaryTerm.osp"/>" title="New...">
        New... </a>
    </c:if>
    <c:if test="${canWorksite.maintain}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
                    <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
                    <osp:param name="name" value="glossary"/>
                    <c:if test="${!global}">
                        <osp:param name="qualifier" value="${tool.id}"/>
                    </c:if>
                    <c:if test="${global}">
                        <osp:param name="qualifier" value="theospi.help.glossary.global"/>
                    </c:if>
                    <osp:param name="returnView" value="glossaryListRedirect"/>
                </osp:url>"
            title="Permissions..."> Permissions... </a>
    </c:if>
    <c:if test="${can.export}">
    <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=helpManagerTarget&templateId=<c:out value="${template.id.value}"/>/<c:out value="${worksite.title}" /> Glossary.zip">Export</a>
            
    </c:if>
    <c:if test="${can.add}">
        <a href="<osp:url value="importGlossaryTerm.osp"/>" title="Import...">
        Import... </a>
    </c:if>
</div>

<osp:url var="listUrl" value="glossaryList.osp" />

<div class="rightNav">
<osp:listScroll listUrl="${listUrl}" className="chefToolBarWrap" />
</div>
<h3>Glossary Manager</h3>
<table class="listHier" cellspacing="0">
    <thead>
        <tr>
            <th scope="col">Term</th>
            <th scope="col">Description</th>
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
                                <a href="<osp:url value="editGlossaryTerm.osp"/>&id=<c:out value="${term.id}" />">Edit</a>
                            </c:if>
                            <c:if test="${can.edit || can.delete}">
                                |
                            </c:if>
                            <c:if test="${can.delete}">
                                <a href="<osp:url value="removeGlossaryTerm.osp"/>&id=<c:out value="${term.id}" />">Delete</a>
                            </c:if>
                        </div>
                    </c:if>
                </TD>

                <TD><c:out value="${term.description}" /></TD>
            </TR>
        </c:forEach>
    </tbody>
</table>
