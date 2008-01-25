<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3><fmt:message key="title_manageCellStatus"/></h3>
   
<div class="validation">
   <fmt:message key="validation_statusWarning" />
</div>

<form method="POST">

   <fieldset>
      <legend class="radio">
      	<fmt:message key="legend_changeStatusTo" />

			<select id="newStatusValue" name="newStatusValue">
				<c:forEach items="${statuses}" var="status">
					<option value="${status}">
						<fmt:message key="${status}"/>
					</option>
				</c:forEach>
			</select>
		</legend>
      <div class="checkbox indnt1">
         <input type="radio" id="changeUserOnly" name="changeOption" value="changeUserOnly" />
         <label for="changeUserOnly"><fmt:message key="label_forThisUserOnly"/></label>
      </div>
      <div class="checkbox indnt1">
         <input type="radio" id="changeAll" name="changeOption" value="changeAll" />
         <label for="changeAll"><fmt:message key="label_forAllMatrixUsers"/></label>
      </div>
   </fieldset>
    
   <div class="act">
      <input name="continue" type="submit" value="<osp:message key="button_continue"/>" accesskey="s" class="active" />
      <input name="cancel" type="submit" value="<osp:message key="button_cancel"/>" accesskey="x" />
   </div>
</form>