<h4>Expectation <c:out value="${i+1}"/></h4>

<osp-h:glossary link="true" hover="true"><c:out value="${reflect.reflectionItems[i].expectation.description}" escapeXml="false"/></osp-h:glossary>





<p class="longtext">
	<label>
		<c:choose>
			<c:when test="${reflect.reflectionItems[i].expectation.required}">
		    	<span class="reqStar">*</span> Evidence
		    </c:when>
		    <c:otherwise>
		    	Evidence
		    </c:otherwise>
	    </c:choose>
    </label>
    
	<spring:bind path="reflect.reflectionItems[${i}].evidence">
		<c:if test="${status.error}">
			<div class="validation"><c:out value="${status.errorMessage}"/></div>
		</c:if>
	</spring:bind>

	<osp-h:glossary link="true" hover="true">
		<p class="instruction">
	    	What evidence demonstrates that you have met this expectation? Please cut and
			paste or add a link of your evidence into the space below.
		</p>
	</osp-h:glossary>

	<spring:bind path="reflect.reflectionItems[${i}].evidence">
		<table><tr>
          <td><textarea id="<c:out value="${status.expression}"/>"  
          	name="<c:out value="${status.expression}"/>" 
          	rows="10" cols="75"><c:out value="${status.value}"/></textarea></td>
        </tr></table>
    </spring:bind>
</p>






<p class="longtext">
	<label>
		<c:choose>
			<c:when test="${reflect.reflectionItems[i].expectation.required}">
				<span class="reqStar">*</span>Connect
			</c:when>
			<c:otherwise>
				Connect
			</c:otherwise>
		</c:choose>
	</label>
	<spring:bind path="reflect.reflectionItems[${i}].connect">
		<c:if test="${status.error}">
			<div class="validation"><c:out value="${status.errorMessage}"/></div>
		</c:if>
	</spring:bind>
	
	<osp-h:glossary link="true" hover="true">
		<p class="instruction">
			Explain how your evidence demonstrates this expectation.
		</p>
	</osp-h:glossary>
	
	<spring:bind path="reflect.reflectionItems[${i}].connect">
		<table><tr>
          <td><textarea id="<c:out value="${status.expression}"/>"  \
          		name="<c:out value="${status.expression}"/>" 
          		rows="10" cols="75"><c:out value="${status.value}"/></textarea></td>
          </td></table>
	</spring:bind>
</p>


<br /><br /><br />