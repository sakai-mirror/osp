<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>



<%--<form method="POST" action="<osp:url value="/reviewer/reviewCell.osp"/>"> --%>
<form method="POST">
    <osp:form/>

    <h3>Review Expectation</h3>

    <c:set var="cell" value="${reviewerItem.cell}"/>
    <table class="itemSummary">
        <tr><th><c:out value="${cell.scaffoldingCell.scaffolding.columnLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.level.description}"/></td></tr>
        <tr><th><c:out value="${cell.scaffoldingCell.scaffolding.rowLabel}"/>: </th><td><c:out value="${cell.scaffoldingCell.rootCriterion.description}"/></td></tr>
    </table><br />

   <c:forEach var="refItem" items="${cell.reflection.reflectionItems}" varStatus="loopStatus">
        <c:set var="reflectionItem" value="${cell.reflection.reflectionItems[loopStatus.index]}"/>
   
        <h4>Expectation <c:out value="${loopStatus.index+1}"/></h4>
		
        
        <p class="longtext">
            <label class="block">Evidence &#8211; Supporting Documentation</label>
            
            <c:out value="${reflectionItem.evidence}"  escapeXml="false" />
        </p>
        
        
        <p class="longtext">
            <label class="block">Connect &#8211; Supporting Documentation</label>
            
            <c:out value="${reflectionItem.connect}"  escapeXml="false"  />
        </p>
        <br />
        <br />
   </c:forEach>
 
    <h4>Intellectual Growth</h4>
    
    
    <p class="longtext">
        <c:out value="${cell.reflection.growthStatement}" escapeXml="false" />
    </p>
    <br />
    <br />

    <h4>Response</h4>
    
    
    <p class="shorttext">
        <label>Grade</label>
        <spring:bind path="reviewerItem.grade">

            <select name="grade">
                <c:forEach var="reviewItem" items="${reviewRubrics}" varStatus="loopStatus">
                    <spring:message code="${reviewItem.displayText}" text="${reviewItem.displayText}" var="revDisplayText" />
                    <option value="<c:out value="${reviewItem.id}"/>" 
                        <c:if test="${reviewerItem.grade==reviewItem.id}" >selected</c:if> ><c:out value="${revDisplayText}"/></option>
                </c:forEach>
            </select>
         <span class="error_message"><c:out value="${status.errorMessage}"/></span>
        </spring:bind> 
    </p>
    <p class="longtext">
        <label class="block">Comment</label>
        <spring:bind path="reviewerItem.comments">
            <table><tr>
            <td><textarea name="comments" id="comments" cols="70" rows="15"><c:out value="${reviewerItem.comments}"/></textarea></td>
            </tr></table>
            <span class="error_message"><c:out value="${status.errorMessage}"/></span>
        </spring:bind>

    <br/>
    <div class="act">
        <input type="hidden" name="cell_id" value="<c:out value="${cell.id}"/>"/>
        <input type="button" name="action" value="Printable View" onclick="open('<osp:url value="viewPrintableReflection.osp"/>&action=Printable View&reviewerItem_id=<c:out value="${reviewerItem.id.value}" />','Sample',
            'location=no,scrollbars=yes')"/>
        <input type="submit" name="action" value="Save"/>
        <input type="submit" name="action" value="Submit"/>
        <input type="submit" name="action" value="Cancel"/>
    </div>
</form>

<script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
<script type="text/javascript" defer="1">chef_setupformattedtextarea('comments');</script>


<%--@ include file="/WEB-INF/jsp/htmlarea.inc" --%>