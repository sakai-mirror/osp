<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h3>Notify Viewers</h3>

<form method="POST">
    <osp:form/>

    <h4>Notify Viewers of changes to <em><c:out value="${presentation.name}"/></em></h4>
    <p class="instruction">Pick the users from the list that you would like to notify of updates to this share.
    You may also include your own message to the viewers.</p>
    
    <spring:bind path="form.recipients">
    <p class="indnt2">
     <c:forEach var="viewer" items="${presentation.viewers}" >
        <input align="absmiddle"
             type="checkbox"
             name="<c:out value="${status.expression}"/>"
           <c:choose>
           <c:when test="${viewer.role == 'ROLE_GUEST'}">
             value="<c:out value="${viewer.displayName}"/>"
           </c:when>
           <c:otherwise>
             value="<c:out value="${viewer.profile.email}"/>"
           </c:otherwise>
           </c:choose>
           >
       <label><c:out value="${viewer.displayName}"/></label> <br/>
     </c:forEach>
     </p>
    </spring:bind>
    
    <br/>
    
    <spring:bind path="form.message">
    <p class="longtext">
        <label class="block">Your Message</label>
        <textarea name="<c:out value="${status.expression}"/>" rows="5" cols="80"></textarea>
    </p>
    </spring:bind>
    <br /><br />
    
    <div class="act">
        <input type="submit" name="submit" value="Send"/>
        <input type="submit" name="_cancel" value="Cancel"/>
    </div>

</form>
