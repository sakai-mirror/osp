<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.glossary.bundle.Messages"/>

    <h3>
        <c:if test="${not empty entry.id}"><fmt:message key="title_editGlossaryTerm"/></c:if>
        <c:if test="${empty entry.id}"><fmt:message key="title_addGlossaryTerm"/></c:if>
    </h3>
   

    <p class="instructions">
        <fmt:message key="instructions_addGlossaryTerm"/>
    </p>
    <spring:hasBindErrors name="entry">
        <div class="validation"><fmt:message key="error_add"/></div>
    </spring:hasBindErrors>


<form method="post" action="<c:out value="${action}"/>" > 
    <osp:form/>
    
    <spring:bind path="entry.term">
        <c:if test="${status.error}"> 
            <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
        <p class="shorttext indnt2">
            <span class="reqStar">*</span>
            <label>
                <fmt:message key="label_Term"/>
            </label>
            <input type="text" name="term" 
                   value="<c:out value="${status.value}"/>" 
                   size="50" maxlength="255"
            />
        </p>
    </spring:bind>
    
    <spring:bind path="entry.description">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
        <p class="longtext indnt2">
            <span class="reqStar">*</span>
            <label class="block">
                <fmt:message key="label_shortDesc"/>
            </label>
            <br />
            <c:set var="item" value="${status.value}"/>
            <textarea rows="3" name="description" 
                onkeyup="limitChar(this,255)"  cols="80"
            ><c:out value="${item}"/></textarea>
        </p>
    </spring:bind>
    
    <spring:bind path="entry.longDescription">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
        <p class="longtext indnt2">
            <span class="reqStar">*</span>
            <label class="block">
                <fmt:message key="label_longDesc"/>
            </label>
            <c:set var="item" value="${status.value}"/>
            <table><tr>
            <td><textarea id="longDescription" name="longDescription" rows="25" cols="80"><c:out value="${item}"/></textarea></td>
            </tr></table>
        </p>
    </spring:bind>

    
    <input type="hidden" name="worksiteId" value="<c:out value="${entry.worksiteId}" />"/>
    
    <script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
    <script type="text/javascript" defer="1">chef_setupformattedtextarea('longDescription');</script>
    
    <div class="act">
        <c:if test="${not empty entry.id}">
            <input type="submit" name="submitButton" class="active" value="<fmt:message key="button_submitEdit"/>" />
        </c:if>
        <c:if test="${empty entry.id}">
            <input type="submit" name="submitButton" class="active" value="<fmt:message key="button_submitAdd"/>" />
        </c:if>

        <input type="button" value="<fmt:message key="button_cancel"/>" onclick="window.document.location='<osp:url value="glossaryList.osp"/>'">
    </div>

</form>
