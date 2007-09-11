<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>
<c:set var="imageLib" value="/library/image/"/>

<form name="form" method="POST"
   <dl>
      <dt><osp:message key="hdr.assignment"/></dt>
      <dd><c:out value="${submission.assignment.title}"/></dd>
        
      <dt><osp:message key="hdr.status"/></dt>
      <dd><c:out value="${submission.status}"/></dd>

      <c:if test="${submission.gradeReleased}">        
         <dt><osp:message key="hdr.grade"/></dt>
         <dd><c:out value="${submission.grade}"/></dd>
      </c:if>
        
      <br/>
      <dt><osp:message key="assign.instruct"/></dt>
      <dd><c:out value="${submission.assignment.content.instructions}" escapeXml="false"/></dd>
      
      <c:if test="${not empty assignAttachments}">
         <dt><osp:message key="assign.attach"/></dt>
         <dd>
         <c:forEach var="attach" items="${assignAttachments}">
            <img src="<c:out value='${imageLib}${attach.iconUrl}'/>" border="0"/>
            <a href="${attach.url}" target="_blank"><c:out value="${attach.displayName}"/></a>
            (<c:out value="${attach.size}"/>)<br/>
         </c:forEach>
         </dd>
      </c:if>
      
      <hr/>
      
      <c:if test="${submission.submitted}">
         <c:if test="${not empty submission.feedbackFormattedText}">
            <dt><osp:message key="assign.submission"/></dt>
            <dd><c:out value="${submission.feedbackFormattedText}" escapeXml="false"/></dd>
         </c:if>
          
         <c:if test="${not empty submitAttachments}">
            <dt><osp:message key="assign.submit.attach"/></dt>
            <dd>
            <c:forEach var="attach" items="${submitAttachments}">
               <img src="<c:out value='${imageLib}${attach.iconUrl}'/>" border="0"/>
               <a href="${attach.url}" target="_blank"><c:out value="${attach.displayName}"/></a>
               (<c:out value="${attach.size}"/>)<br/>
            </c:forEach>
            </dd>
         </c:if>
         
         <c:if test="${not empty submission.feedbackComment}">
            <dt><osp:message key="assign.comments"/></dt>
            <dd><c:out value="${submission.feedbackComment}" escapeXml="false"/></dd>
         </c:if>
         
         <c:if test="${not empty feedbackAttachments}">
            <dt><osp:message key="assign.feedback.attach"/></dt>
            <dd>
            <c:forEach var="attach" items="${feedbackAttachments}">
               <img src="<c:out value='${imageLib}${attach.iconUrl}'/>" border="0"/>
               <a href="${attach.url}" target="_blank"><c:out value="${attach.displayName}"/></a>
               (<c:out value="${attach.size}"/>)<br/>
            </c:forEach>
            </dd>
         </c:if>
      
      </c:if>
   </dl>

   <input type="submit" name="submit" value="<fmt:message key="button_back"/>" accesskey="b"/>
</form>
