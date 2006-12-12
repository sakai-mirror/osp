<script type="text/javascript">
<!--
  function confirmDelete() {
    return confirm("<fmt:message key="comments_confirmDelete"/>");
  }
-->
</script>

   <a name="comments"></a>
   <h3><fmt:message key="title_comments"/></h3>

   <c:if test="${!empty comments}">
    <c:forEach begin="0" items="${comments}" var="comment" >
      <a name="comment<c:out value="${comment.id.value}"/>"></a>
      <h4>
         <c:if test="${comment.visibility == 1}">
            <fmt:message key="comments_private"/> 
         </c:if>
         <c:if test="${comment.visibility == 2}">
            <fmt:message key="comments_shared"/> 
         </c:if>
         <c:if test="${comment.visibility == 3}">
            <fmt:message key="comments_public"/> 
         </c:if>      
      <fmt:message key="comments_from"/>
      
      <c:choose>
      <c:when test="${comment.creator == currentAgent}">
         <fmt:message key="comments_yourself"/>
      </c:when>
      <c:otherwise>
         <c:out value="${comment.creator.displayName}"/>
      </c:otherwise>
      </c:choose>
      &nbsp;&nbsp;(<c:out value="${comment.created}"/>)

      <c:set var="firstAction" value="false" />

      <c:if test="${comment.creator == currentAgent}">
         <c:set var="firstAction" value="true" />         
         &nbsp;<a href='<osp:url value="deleteComment.osp"/>&id=<c:out value="${comment.id.value}" />' ><fmt:message key="action_delete"/></a>
      </c:if>

      <c:choose>
      <c:when test="${comment.creator == currentAgent && comment.presentation.owner == currentAgent}">
         <!-- you created the comment and are the element owner -->
         <c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
         <c:if test="${comment.visibility == 1}">
            <c:set var="firstAction" value="true" />
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a>
         </c:if>
         <c:if test="${comment.visibility != 1}">
            <c:set var="firstAction" value="true" />
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePrivate"/></a>
         </c:if>
      </c:when>
      <c:when test="${comment.creator == currentAgent}">
         <!-- you created the comment and are not the element owner -->
         <c:if test="${comment.visibility == 1}">
         <c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makeShared"/></a>
            &nbsp;<span class="pipe">|</a>&nbsp;
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a>
         </c:if>
         <c:if test="${comment.visibility == 2}">
            <c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePrivate"/></a>
            &nbsp;<span class="pipe">|</a>&nbsp;
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a>
         </c:if>
         <c:if test="${comment.visibility == 3}">
         <c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePrivate"/></a>
            &nbsp;<span class="pipe">|</a>&nbsp;
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makeShared"/></a>
         </c:if>
      </c:when>
      <c:when test="${comment.presentation.owner == currentAgent}">
         <!-- you are the element owner and didn't create the comment -->
         <c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
         <c:if test="${comment.visibility == 2}"><a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a></c:if>
         <c:if test="${comment.visibility == 3}"><a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makeShared"/></a></c:if>
      </c:when>
      </c:choose>

      </h4>
     <p class="indnt1">
     <c:out value="${comment.title}"/>&nbsp;-&nbsp;<c:out value="${comment.comment}"/>
     </p>
    </c:forEach>
   </c:if>
   <c:if test="${empty comments}">
      <h4><fmt:message key="comments_noComments"/></h4>
   </c:if>

   <c:if test="${!empty currentAgent && currentAgent.displayName != 'anonymous' && allowComments}">
   <br /><br />
    <h3><fmt:message key="comments_addComment"/></h3>
    
   <form method="POST" action="addComment.osp" >
     <osp:form/>
      <input type="hidden" name="presentationId" value="<c:out value="${presentation.id.value}"/>"/>
      <input type="hidden" name="id" value="<c:out value="${presentation.id.value}"/>"/>
      <input type="hidden" name="sakai.tool.placement.id" value="<c:out value="${placementId}"/>"/>
      <p class="instruction">
         <fmt:message key="instructions_addComment"/> 
         <fmt:message key="instructions_requiredFields"/>
      </p>


      <spring:bind path="newComment.title">
         <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
         </c:if>
         <p class="shorttext">
            <span class="reqStar">*</span>
            <label><fmt:message key="table_row_title"/></label>
            <input name="<c:out value="${status.expression}"/>" size="25" maxlength="50" value="<c:out value="${status.value}"/>"/>
         </p>
         <td>
      </spring:bind>


      <spring:bind path="newComment.comment">
         <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
         </c:if>
         <p class="longtext">
            <span class="reqStar">*</span>
            <label class="block"><fmt:message key="table_row_comment"/></label>

            <textarea name="<c:out value="${status.expression}"/>" value="" cols="80" rows="5" maxlength="4000"></textarea>
         </p>
      </spring:bind>


      <spring:bind path="newComment.visibility">
         <fieldset>
            <legend class="radio"><fmt:message key="legend_makeCommentVisibleTo"/></legend>
            <div class="checkbox indnt1">
               <input type="radio" id="onlyme" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 1}">checked</c:if> value="1"/>
               <label for="onlyme"><fmt:message key="label_onlyMe"/></label>
            </div>
            <c:if test="${currentAgent != presentation.owner}">
               <div class="checkbox indnt1">
                  <input type="radio" id="ownerAndI" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 2}">checked</c:if> value="2"/>
                  <label for="ownerAndI"><fmt:message key="label_ownerAndI"/></label>
               </div>
            </c:if>
            <div class="checkbox indnt1">
               <input type="radio" id="everyone" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 3 || status.value == 0}">checked</c:if> value="3"/>
               <label for="everyone"><fmt:message key="label_everyone"/></label>
            </div>
         </fieldset>
      </spring:bind>


      <div class="act">
         <%-- <input type="submit" name="<fmt:message key="button_saveComment"/>" value="submit" alt="<fmt:message key="button_saveCommentAlt"/>" /> --%>
         <input type="submit" name="submit" value="<fmt:message key="button_saveComment"/>" alt="<fmt:message key="button_saveCommentAlt"/>" />
      </div>
   </form>
   </c:if>