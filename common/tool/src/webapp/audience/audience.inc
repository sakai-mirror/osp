<h:panelGrid columns="1">
   <sakai:view_title value="#{audience.selectedTitle}" />
   <sakai:button_bar>
      <sakai:button_bar_item id="remove_user_button" 
         action="#{audience.processActionRemove}"
         value="#{msgs.remove_member}"/>
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
