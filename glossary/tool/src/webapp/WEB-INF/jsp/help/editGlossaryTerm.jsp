<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

    <h3>
        <c:if test="${not empty entry.id}">Edit</c:if>
        <c:if test="${empty entry.id}">Add</c:if>
         Glossary Term
    </h3>
   

    <p class="instructions">
        Glossary Term
            (required fields are noted with an 
            <span class="reqStarInline">*</span>)
    </p>
    <spring:hasBindErrors name="entry">
        <div class="validation">There were problems in your last submission.  
            Please see below for details</div>
    </spring:hasBindErrors>


<form method="post" action="<c:out value="${action}"/>" > 
    <osp:form/>
    
    <h4>Glossary Term</h4>
    
    <spring:bind path="entry.term">
        <c:if test="${status.error}"> 
            <div class="validation"><c:out value="${status.errorMessage}"/></div>
        </c:if>
        <p class="shorttext indnt2">
            <span class="reqStar">*</span>
            <label>
                Term
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
                Short Description
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
                Long Description
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
        <input type="submit" name="submitButton" class="active" value="Save"/>
        <input type="button" value="Cancel" onclick="window.document.location='<osp:url value="glossaryList.osp"/>'">
    </div>

</form>
