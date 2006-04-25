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

<sakai:view>
   <sakai:view_title value="#{common_msgs.guidance_title}"/>
   <sakai:instruction_message value="#{guidance.guidanceInstructions}" />
   <sakai:messages />

<h:form>
  <ospx:xheader rendered="#{guidance.instructionsRendered}">
      <ospx:xheadertitle id="instructionTitleAdd" value="#{common_msgs.instruction_title_add}" rendered="#{guidance.current.instruction.base.text == ''}" />
      <ospx:xheadertitle id="instructionTitleEdit" value="#{common_msgs.instruction_title_edit}" rendered="#{guidance.current.instruction.base.text != ''}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
          <ospx:splitarea direction="vertical" width="100%">
             <ospx:splitsection valign="top">
               <sakai:instruction_message value="#{common_msgs.instruction_message}" />
               <sakai:inputRichText value="#{guidance.current.instruction.base.text}"
                  attachedFiles="#{guidance.current.instruction.attachmentLinks}"
                  rows="15" cols="60" buttonSet="large" showXPath="false" />
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <f:subview id="instrItems" rendered="#{not empty guidance.current.instruction.attachments}">
               <sakai:flat_list value="#{guidance.current.instruction.attachments}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{common_msgs.item_header}"/>
                     </f:facet>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_new">
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                     <f:facet name="footer">
                        <sakai:button_bar rendered="true">
                           <sakai:button_bar_item id="manageInstructionItems" action="#{guidance.current.instruction.processActionManageAttachments}"
                           value="#{common_msgs.manage_instruction}" />
                        </sakai:button_bar>
                     </f:facet>
                  </h:column>
               </sakai:flat_list>
               </f:subview>
               <f:subview id="instrNoItems" rendered="#{empty guidance.current.instruction.attachments}">
               <sakai:flat_list value="#{common_msgs.no_items}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{common_msgs.item_header}"/>
                     </f:facet>
                     <h:outputText value="#{material}" />
                     <f:facet name="footer">
                        <sakai:button_bar rendered="true">
                           <sakai:button_bar_item id="manageInstructionItems" action="#{guidance.current.instruction.processActionManageAttachments}"
                           value="#{common_msgs.manage_instruction}" />
                        </sakai:button_bar>
                     </f:facet>
                  </h:column>
               </sakai:flat_list>
               </f:subview>
             </ospx:splitsection>
         </ospx:splitarea>
      </ospx:xheaderdrawer>
  </ospx:xheader>
<h:outputText id="description_spacer" value="<br>" escape="false" />
  <ospx:xheader rendered="#{guidance.examplesRendered}">
      <ospx:xheadertitle id="exampleTitleAdd" value="#{common_msgs.example_title_add}" rendered="#{guidance.current.example.base.text == ''}" />
      <ospx:xheadertitle id="exampleTitleEdit" value="#{common_msgs.example_title_edit}" rendered="#{guidance.current.example.base.text != ''}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
          <ospx:splitarea direction="vertical" width="100%">
             <ospx:splitsection valign="top">
               <sakai:instruction_message value="#{common_msgs.example_message}" />
               <sakai:inputRichText value="#{guidance.current.example.base.text}"
                  attachedFiles="#{guidance.current.example.attachmentLinks}"
                  rows="15" cols="60" buttonSet="large" showXPath="false" />
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <f:subview id="exampleItems" rendered="#{not empty guidance.current.example.attachments}">
               <sakai:flat_list value="#{guidance.current.example.attachments}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{common_msgs.item_header}"/>
                     </f:facet>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_new">
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                     <f:facet name="footer">
                        <sakai:button_bar rendered="true">
                           <sakai:button_bar_item id="manageExampleItems" action="#{guidance.current.example.processActionManageAttachments}"
                           value="#{common_msgs.manage_instruction}" />
                        </sakai:button_bar>
                     </f:facet>
                  </h:column>
               </sakai:flat_list>
               </f:subview>
               <f:subview id="exampleNoItems" rendered="#{empty guidance.current.example.attachments}">
               <sakai:flat_list value="#{common_msgs.no_items}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{common_msgs.item_header}"/>
                     </f:facet>
                     <h:outputText value="#{material}" />
                     <f:facet name="footer">
                        <sakai:button_bar rendered="true">
                           <sakai:button_bar_item id="manageExampleItems" action="#{guidance.current.example.processActionManageAttachments}"
                           value="#{common_msgs.manage_instruction}" />
                        </sakai:button_bar>
                     </f:facet>
                  </h:column>
               </sakai:flat_list>
               </f:subview>
             </ospx:splitsection>
         </ospx:splitarea>
      </ospx:xheaderdrawer>
  </ospx:xheader>
<h:outputText id="description_spacer" value="<br>" escape="false" />
  <ospx:xheader rendered="#{guidance.rationaleRendered}">
      <ospx:xheadertitle id="rationaleTitleAdd" value="#{common_msgs.rationale_title_add}" rendered="#{guidance.current.rationale.base.text == ''}" />
      <ospx:xheadertitle id="rationaleTitleEdit" value="#{common_msgs.rationale_title_edit}" rendered="#{guidance.current.rationale.base.text != ''}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
          <ospx:splitarea direction="vertical" width="100%">
             <ospx:splitsection valign="top">
               <sakai:instruction_message value="#{common_msgs.rationale_message}" />
               <sakai:inputRichText value="#{guidance.current.rationale.base.text}"
                  attachedFiles="#{guidance.current.rationale.attachmentLinks}"
                  rows="15" cols="60" buttonSet="large" showXPath="false" />
             </ospx:splitsection>
             <ospx:splitsection valign="top">
             </ospx:splitsection>
             <f:subview id="rationaleItems" rendered="#{not empty guidance.current.rationale.attachments}">
               <sakai:flat_list value="#{guidance.current.rationale.attachments}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{common_msgs.item_header}"/>
                     </f:facet>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_new">
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                     <f:facet name="footer">
                        <sakai:button_bar rendered="true">
                           <sakai:button_bar_item id="manageRationaleItems" action="#{guidance.current.rationale.processActionManageAttachments}"
                           value="#{common_msgs.manage_instruction}" />
                        </sakai:button_bar>
                     </f:facet>
                  </h:column>
               </sakai:flat_list>
               </f:subview>
               <f:subview id="rationaleNoItems" rendered="#{empty guidance.current.rationale.attachments}">
               <sakai:flat_list value="#{common_msgs.no_items}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:outputText value="#{common_msgs.item_header}"/>
                     </f:facet>
                     <h:outputText value="#{material}" />
                     <f:facet name="footer">
                        <sakai:button_bar rendered="true">
                           <sakai:button_bar_item id="manageRationaleItems" action="#{guidance.current.rationale.processActionManageAttachments}"
                           value="#{common_msgs.manage_instruction}" />
                        </sakai:button_bar>
                     </f:facet>
                  </h:column>
               </sakai:flat_list>
               </f:subview>
         </ospx:splitarea>
      </ospx:xheaderdrawer>
  </ospx:xheader>

   <sakai:button_bar>
      <sakai:button_bar_item id="submit" value="#{common_msgs.save_guidance}" action="#{guidance.processActionSave}" />
      <sakai:button_bar_item id="cancel" value="#{common_msgs.cancel_guidance}" action="#{guidance.processActionCancel}" />
   </sakai:button_bar>

</h:form>
</sakai:view>

</f:view>
