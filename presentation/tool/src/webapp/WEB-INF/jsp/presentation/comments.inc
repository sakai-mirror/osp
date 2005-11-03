<script type="text/javascript">
<!--
  function confirmDelete() {
    return confirm("Are you sure you want to delete this comment?");
  }
-->
</script>


	<a name="comments"></a>
	<h3>Comments on this Presentation:</h3>

   <c:if test="${!empty comments}">
    <c:forEach begin="0" items="${comments}" var="comment" >
      <a name="comment<c:out value="${comment.id.value}"/>"></a>
      <h4>
         <c:if test="${comment.visibility == 1}">
            Private 
         </c:if>
         <c:if test="${comment.visibility == 2}">
            Shared 
         </c:if>
         <c:if test="${comment.visibility == 3}">
            Public 
         </c:if>      
      Comment from 
      
      <c:choose>
      <c:when test="${comment.creator == currentAgent}">
         Yourself
      </c:when>
      <c:otherwise>
         <c:out value="${comment.creator.displayName}"/>
      </c:otherwise>
      </c:choose>
      &nbsp;&nbsp;(<c:out value="${comment.created}"/>)

      <c:set var="firstAction" value="false" />

      <c:if test="${comment.creator == currentAgent}">
         <c:set var="firstAction" value="true" />         
         &nbsp;<a href='<osp:url value="deleteComment.osp"/>&id=<c:out value="${comment.id.value}" />' >delete</a>
      </c:if>

      <c:choose>
      <c:when test="${comment.creator == currentAgent && comment.presentation.owner == currentAgent}">
         <!-- you created the comment and are the element owner -->
		<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
         <c:if test="${comment.visibility == 1}">
            <c:set var="firstAction" value="true" />
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>">make public</a>
         </c:if>
         <c:if test="${comment.visibility != 1}">
            <c:set var="firstAction" value="true" />
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>">make private</a>
         </c:if>
      </c:when>
      <c:when test="${comment.creator == currentAgent}">
         <!-- you created the comment and are not the element owner -->
         <c:if test="${comment.visibility == 1}">
		<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>">make shared</a>
			&nbsp;<span class="pipe">|</a>&nbsp;
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>">make public</a>
         </c:if>
         <c:if test="${comment.visibility == 2}">
		<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>">make private</a>
			&nbsp;<span class="pipe">|</a>&nbsp;
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>">make public</a>
         </c:if>
         <c:if test="${comment.visibility == 3}">
		<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>">make private</a>
			&nbsp;<span class="pipe">|</a>&nbsp;
            <a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>">make shared</a>
         </c:if>
      </c:when>
      <c:when test="${comment.presentation.owner == currentAgent}">
         <!-- you are the element owner and didn't create the comment -->
		<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
         <c:if test="${comment.visibility == 2}"><a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>">make public</a></c:if>
         <c:if test="${comment.visibility == 3}"><a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>">make shared</a></c:if>
      </c:when>
      </c:choose>

      </h4>
     <p class="indnt1">
     <c:out value="${comment.title}"/>&nbsp;-&nbsp;<c:out value="${comment.comment}"/>
     </p>
    </c:forEach>
   </c:if>
   <c:if test="${empty comments}">
      <h4>Currently there are no comments about this presentation.</h4>
   </c:if>

   <c:if test="${!empty currentAgent && currentAgent.displayName != 'anonymous'}">
   <br /><br />
    <h3>Add new comment about this presentation:</h3>
    
   <form method="POST" action="addComment.osp" >
     <osp:form/>
      <input type="hidden" name="presentationId" value="<c:out value="${presentation.id.value}"/>"/>
      <input type="hidden" name="id" value="<c:out value="${presentation.id.value}"/>"/>

		<p class="instruction">
		    Add Comment (required fields are noted with an
                <span class="reqStarInline">*</span>)
		</p>
		
		
		<spring:bind path="newComment.title">
            <c:if test="${status.error}">
               <div class="validation"><c:out value="${status.errorMessage}" /></div>
            </c:if>
			<p class="shorttext">
                <span class="reqStar">*</span>
				<label>Title:</label>
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
				<label class="block">Comment:</label>
			
		        <textarea name="<c:out value="${status.expression}"/>" cols="80" rows="5" 
		           onkeyup="limitChar(this,4000)"><c:out value="${status.value}"/></textarea>
		    </p>
		</spring:bind>
		
		
		<spring:bind path="newComment.visibility">
            <fieldset>
                <legend class="radio">Make this comment visible to:</legend>
                <div class="checkbox indnt1">
		            <input type="radio" id="onlyme" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 1}">checked</c:if> value="1"/>
		            <label for="onlyme">Only Me</label>
		        </div>
		        <c:if test="${currentAgent != presentation.owner}">
                    <div class="checkbox indnt1">
		                <input type="radio" id="ownerAndI" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 2}">checked</c:if> value="2"/>
		                <label for="ownerAndI">The Owner and I</label>
		            </div>
		        </c:if>
                <div class="checkbox indnt1">
		            <input type="radio" id="everyone" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 3 || status.value == 0}">checked</c:if> value="3"/>
		            <label for="everyone">Everyone</label>
                </div>
            </fieldset>
		</spring:bind>
		

     <div class="act">
      	<input type="submit" name="submit" value="submit" alt="Save this comment" />
      </div>
   </form>
   </c:if>