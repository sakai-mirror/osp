<h:panelGrid columns="1">
   <sakai:view_title value="#{audience.selectedTitle}" />
   <sakai:button_bar>
      <sakai:button_bar_item id="remove_user_button" 
         action="#{audience.processActionRemove}"
         value="#{msgs.remove_member}"/>
   </sakai:button_bar>
</h:panelGrid>
<ospx:scrollablearea id="selectedMembers" scrollYStyle="auto" width="100%" height="100%">
   <sakai:flat_list value="#{audience.selectedMembers}" var="member">
      <h:column>
         <h:selectBooleanCheckbox id="memberSelect" value="#{member.selected}" />
         <h:outputLabel value="#{member.displayName}" for="memberSelect" />
      </h:column>
   </sakai:flat_list>
</ospx:scrollablearea>
