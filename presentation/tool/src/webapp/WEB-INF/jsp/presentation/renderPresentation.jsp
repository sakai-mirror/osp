<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>
<osp-p:renderPresentation 
 template="<%= request.getAttribute("renderer") %>" 
 doc="<%= request.getAttribute("document") %>"
 uriResolver="<%= request.getAttribute("uriResolver") %>" />
<osp-c:authZMap prefix="osp.presentation." qualifier="${presentation.id}" var="isAuthorizedTo" />


<c:if test="${presentation.allowComments}">
 <%@ include file="/WEB-INF/jsp/presentation/comments.inc" %>
</c:if>
