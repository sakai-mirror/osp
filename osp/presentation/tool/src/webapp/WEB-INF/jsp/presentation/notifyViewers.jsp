<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<h3><fmt:message key="title_notifyViewers"/></h3>

<form method="POST">
    <osp:form/>
    <h4><fmt:message key="instructions_notifyViewersChangesToX">
           <fmt:param><c:out value="${presentation.name}"/></fmt:param>
        </fmt:message>
    </h4>
    <p class="instruction"><fmt:message key="instructions_pickUsersFromList"/></p>
    <spring:bind path="form.recipients">
    <p class="indnt2">
     <c:forEach var="viewer" items="${viewers}" >
        <input align="absmiddle"
             type="checkbox"
             name="<c:out value="${status.expression}"/>"
             value="<c:out value="${viewer.email}"/>">
       <label><c:out value="${viewer.displayName}"/></label> <br/>
     </c:forEach>
     </p>
    </spring:bind>
    <br/>
    <spring:bind path="form.message">
    <p class="longtext">
        <label class="block"><fmt:message key="label_yourMessage"/></label>
        <textarea name="<c:out value="${status.expression}"/>" rows="5" cols="80"></textarea>
    </p>
    </spring:bind>
    <br /><br />
    <div class="act">
        <input type="submit" name="submit"  value="<fmt:message key="button_send"/>"/>
        <input type="submit" name="_cancel" value="<fmt:message key="button_cancel"/>"/>
    </div>

</form>
