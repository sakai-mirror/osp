<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<table width="100%">
<c:forEach begin="0" end="0" items="${comments}" var="comment">
   <tr>
      <td align="left">
		  <h3><c:out value="${comment.presentation.name}" /></h3>
      </td>
      <td align="right">
	     <a target="_blank" href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${comment.presentation.id.value}" />#comment<c:out value="${comment.id.value}" />"
	   		title="<fmt:message key="table_comments_link_hint"/>">
               <fmt:message key="pres_preview" />
		  </a>
	   </td>
   </tr>
</c:forEach>
</table>
   
   <c:if test="${not empty comments}">
	   <table class="listHier lines nolines" cellspacing="0"  cellpadding="0" border="0" summary="<fmt:message key="table_comments_summary"/>">
		<thead>
			<tr>
				<th scope="col">
					  <fmt:message key="table_header_comment_title"/>
				</th>
				<th scope="col">
					  <fmt:message key="table_header_date"/>
				</th>
				<th scope="col">
					  <fmt:message key="table_header_commentAuthor"/>
				</th>
				<th scope="col">
					  <fmt:message key="table_header_visibility"/>
				</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach begin="0" items="${comments}" var="comment">
				<tr class="lightHighLightRow">
				  <td>
					<h4><c:out value="${comment.title}" /></h4>
				  </td>
				  <td class="ListData">
					<c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${comment.created}" pattern="${dateFormat}"/>
				  </td>
				  <td>
					<c:out value="${comment.creator.displayName}" />
				  </td>
				  <td>
					   <c:if test="${comment.visibility == 1}">
						  <fmt:message key="comments_private"/>
					   </c:if>
					   <c:if test="${comment.visibility == 2}">
						  <fmt:message key="comments_shared"/>
					   </c:if>
					   <c:if test="${comment.visibility == 3}">
						  <fmt:message key="comments_public"/>
					   </c:if>
				  </td>
				</tr>
				<tr class="exclude">
					<td colspan="5">
					   <p class="indnt1"><c:out value="${comment.comment}" /></p>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>	

<form method="post" action="listPresentation.osp">
	<p class="act">
	   <input type="submit" name="_cancel" value="<fmt:message key="button_back"/>" accesskey="x" class="active" />
	 </p>  
</form>

