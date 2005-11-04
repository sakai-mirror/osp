<%@ include file="/WEB-INF/jsp/include.jsp" %><%@ 
taglib prefix="spring" uri="http://www.springframework.org/tags" %><osp-p:renderPresentation 
 template="<%= request.getAttribute("renderer") %>" 
 doc="<%= request.getAttribute("document") %>" /><osp-c:authZMap prefix="osp.presentation." qualifier="${presentation.id}" var="isAuthorizedTo" />
<c:if test="${presentation.template.includeComments && isAuthorizedTo.comment}"><%@ include file="/WEB-INF/jsp/presentation/comments.inc" %></c:if>