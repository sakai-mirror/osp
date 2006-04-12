<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<c:set var="targetNext" value="_target1" />

    <c:if test="${empty template.name}">
    <h3><fmt:message key="title_addTemplate1"/></h3>
    </c:if>
    <c:if test="${not empty template.name}">
    <h3><fmt:message key="title_editTemplate1"/></h3>
    </c:if>
    <%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>
    
    
<form method="POST" action="addTemplate.osp"><osp:form />
    <spring:bind path="template.name">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <span class="reqStar">*</span>
            <label for="<c:out value="${status.expression}"/>"><fmt:message key="label_name"/></label>
            <input
                type="text" name="<c:out value="${status.expression}"/>"
                length="50" value="<c:out value="${status.value}"/>">
        </p>
    </spring:bind>
    
    
    <spring:bind path="template.description">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="longtext">
            <label class="block" for="<c:out value="${status.expression}"/>"><fmt:message key="label_description"/></label>
            <table><tr>
            <td><textarea name="description" id="description" cols="80" rows="5"><c:out
                value="${template.description}" /></textarea></td>
            </tr></table>
        </p>
    </spring:bind>
    
    
    <spring:bind path="template.includeHeaderAndFooter">
        <fieldset>
            <legend class="radio"><fmt:message key="legend_showWithinPortfolioNavigation"/></legend>
            <div class="checkbox indnt1">
                <input type="radio" id="showWithinYes"
                    name="<c:out value="${status.expression}"/>" value="true"
                    <c:if test="${status.value}">checked</c:if> />
                    <label for="showWithinYes"><fmt:message key="label_yes"/></label>
            </div>
            <div class="checkbox indnt1">
                <input type="radio" id="showWithinNo"
                    name="<c:out value="${status.expression}"/>" value="false"
                    <c:if test="${status.value == false}">checked</c:if> />
                    <label for="showWithinNo"><fmt:message key="label_no"/></label>
            </div>
        </fieldset>
    </spring:bind>
    <BR />
    
    <spring:bind path="template.includeComments">
        <fieldset>
            <legend class="radio"><fmt:message key="legend_allowComments"/></legend>
            <div class="checkbox indnt1">
                <input type="radio" id="commentsYes"
                    name="<c:out value="${status.expression}"/>" value="true"
                    <c:if test="${status.value}">checked</c:if> />
                <label for="commentsYes"><fmt:message key="label_yes"/></label>
            </div>
            <div class="checkbox indnt1">
                <input type="radio" id="commentsNo"
                    name="<c:out value="${status.expression}"/>" value="false"
                    <c:if test="${status.value == false}">checked</c:if> />
                    <label for="commentsNo"><fmt:message key="label_no"/></label>
            </div>
        </fieldset>
    </spring:bind>
    
<c:set var="suppress_previous" value="true" />
<c:set var="suppress_submit" value="true" />
<c:set var="suppress_save" value="true" />
<%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
</form>
