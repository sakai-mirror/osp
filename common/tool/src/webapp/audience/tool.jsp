<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<f:loadBundle basename="org.theospi.portfolio.audience.messages" var="msgs"/>

<sakai:view>
   <sakai:view_title value="#{audience.globalTitle}"/>
   <sakai:instruction_message value="#{audience.instructions}" />
   <sakai:messages />

<h:form>
   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">
         <ospx:xheader>
            <ospx:xheadertitle id="groupTitle">
                <h:outputText value="#{audience.groupTitle}" />
            </ospx:xheadertitle>
            <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <!-- worksite group drawer -->
							<ospx:splitarea direction="horizontal" width="100%">
								<ospx:splitsection size="25%" valign="top">
                  <h:outputLabel value="Worksite: #{audience.site.title}" for="siteRoles" />
								</ospx:splitsection>
								<ospx:splitsection size="50%" valign="top">
									<ospx:scrollablearea width="400px"  height="100px">
	                  <h:selectManyCheckbox id="siteRoles" value="#{audience.selectedRoles}" layout="pageDirection">
	                     <f:selectItems value="#{audience.siteRoles}" />
	                  </h:selectManyCheckbox>
									</ospx:scrollablearea>
								</ospx:splitsection>
								<ospx:splitsection size="25%" valign="top">
                  <h:panelGrid columns="1">
                     <h:commandButton action="#{audience.processActionAddGroup}"
                        value="#{msgs.add_all}"/>
                  </h:panelGrid>
								</ospx:splitsection>
							</ospx:splitarea>
            </ospx:xheaderdrawer>
         </ospx:xheader>
         <ospx:xheader>
            <ospx:xheadertitle id="individualTitle">
                <h:outputText value="#{audience.individualTitle}" />
            </ospx:xheadertitle>
            <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <!-- user drawer -->
							<ospx:splitarea direction="horizontal" width="75%">
								<ospx:splitsection size="25%" valign="bottom">
									<h:panelGrid columns="1">
	                  <h:outputLabel value="#{msgs.user_id_label}" for="userId" />
	              	</h:panelGrid>
								</ospx:splitsection>
								<ospx:splitsection size="50%" valign="top">
										<sakai:instruction_message value="#{msgs.userid_instructions}" />
                  	<h:inputText value="#{audience.searchUsers}" id="userId" size="70" />
								</ospx:splitsection>
								<ospx:splitsection size="24%" valign="bottom">
	                  <h:commandButton action="#{audience.processActionAddUser}"
	                     value="#{msgs.add_members}"/>
								</ospx:splitsection>
							</ospx:splitarea>
              <h:commandButton action="#{audience.processActionSearchUsers}"
                 value="#{msgs.search_members}"/>
                       <h:outputText value="" />
                       <h:outputText value="" />
							<ospx:splitarea direction="horizontal" width="75%">
								<ospx:splitsection size="25%" valign="bottom">
	                  <h:outputLabel value="#{msgs.email_label}" for="emails" />
								</ospx:splitsection>
								<ospx:splitsection size="50%" valign="top">
										<sakai:instruction_message value="#{msgs.email_instructions}" />
	                	<h:inputText value="#{audience.searchEmails}" id="emails" size="70" />
								</ospx:splitsection>
								<ospx:splitsection size="24%" valign="bottom">
	                  <h:commandButton action="#{audience.processActionAddEmail}"
	                     value="#{msgs.add_members}"/>
									</ospx:splitsection>
							</ospx:splitarea>
            </ospx:xheaderdrawer>
         </ospx:xheader>
         <ospx:xheader rendered="#{audience.publicCapable}">
            <ospx:xheadertitle id="publicTitle">
                <h:outputText value="#{audience.publicTitle}" />
            </ospx:xheadertitle>
            <ospx:xheaderdrawer initiallyexpanded="true"
               cssclass="drawerBorder">
               <h:selectBooleanCheckbox id="isPublic" value="#{audience.publicAudience}" />
               <h:outputLabel value="#{msgs.public_label}" for="isPublic" />
            </ospx:xheaderdrawer>
         </ospx:xheader>
      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top">
            <h:panelGrid columns="2">
           <sakai:view_title value="#{audience.selectedTitle}" />
               <h:commandButton action="#{audience.processActionRemove}"
                  value="#{msgs.remove_member}"/>
            </h:panelGrid>
            <ospx:scrollablearea id="selectedMembers" width="100%" height="100%">
               <sakai:flat_list value="#{audience.selectedMembers}" var="member">
                  <h:column>
                     <h:selectBooleanCheckbox id="memberSelect" value="#{member.selectedForRemoval}" />
                     <h:outputLabel value="#{member.displayName}" for="memberSelect" />
                  </h:column>
               </sakai:flat_list>
            </ospx:scrollablearea>
      </ospx:splitsection>
   </ospx:splitarea>

   <h:commandButton action="#{audience.processActionCancel}"
      value="#{msgs.cancel_audience}"/>
   <h:commandButton action="#{audience.processActionSave}"
      value="#{msgs.save_audience}"/>

</h:form>

</sakai:view>
</f:view>
