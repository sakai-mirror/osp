<h:panelGrid columns="1">
   <c:if test="${audience.wizardAudience}">
      <sakai:view_title value="#{common_msgs.audience_selected_evaluators}" />
   </c:if>
   <c:if test="${audience.matrixAudience}">
      <sakai:view_title value="#{common_msgs.audience_selected_evaluators}" />
   </c:if>
   <c:if test="${audience.portfolioAudience}">
      <sakai:view_title value="#{common_msgs.audience_selected_audience}" />
   </c:if>
   
   <sakai:button_bar>
      <sakai:button_bar_item id="remove_user_button" 
         action="#{audience.processActionRemoveBrowseMember}"
         value="#{common_msgs.remove_member}"/>
   </sakai:button_bar>
</h:panelGrid>
<ospx:scrollablearea id="selectedMembers" width="100%" height="100%">
   <h:dataTable styleClass="" value="#{audience.selectedMembers}" var="member">
      <h:column>
         <nobr>
         <h:selectBooleanCheckbox id="memberSelect" value="#{member.selected}" />
         <h:outputLabel value="#{member.displayName}" for="memberSelect" />
         </nobr>
      </h:column>
   </h:dataTable>
</ospx:scrollablearea>
