<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3><fmt:message key="title_manageCellStatus"/></h3>
   
<div class="validation">
   <fmt:message key="validation_statusWarning">
     <fmt:param><c:out value="${newStatus}" /></fmt:param>
   </fmt:message>
</div>

<form method="POST">

   <fieldset>
      <legend class="radio"><fmt:message key="legend_changeStatusTo">
                              <fmt:param><c:out value="${newStatus}" /></fmt:param>
                            </fmt:message></legend>
      <div class="checkbox indnt1">
         <input type="radio" id="changeUserOnly" name="changeUserOnly" value="true" />
         <label for="changeUserOnly"><fmt:message key="label_forThisUserOnly"/></label>
      </div>
      <div class="checkbox indnt1">
         <input type="radio" id="changeAll" name="changeAll" value="true" />
         <label for="changeAll"><fmt:message key="label_forAllMatrixUsers"/></label>
      </div>
   </fieldset>
    
   <div class="act">
      <input name="continue" type="submit" value="<osp:message key="button_continue"/>"/>
      <input name="cancel" type="submit" value="<osp:message key="button_cancel"/>"/>
   </div>
</form>