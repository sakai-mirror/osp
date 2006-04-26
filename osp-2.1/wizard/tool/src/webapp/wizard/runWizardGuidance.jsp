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
<h:form>

   <sakai:view_title value="#{wizard.current.base.name}"/>
   
   <sakai:messages />
   
   <h:outputText value="#{wizard.lastSavePage} #{msgs.page_was_submitted}" styleClass="success" rendered="#{wizard.lastSavePage != ''}" />
   <h:outputText value="#{msgs.changes_saved}" styleClass="success" rendered="#{wizard.pageSaved}" />
   
   
   <sakai:instruction_message value="#{wizard.current.base.description}" />
   

   <h:outputText value="<b>#{msgs.guidance_instructions}</b><br><br>" escape="false" />
   <h:outputText value="#{wizard.current.instruction.text}" />
   <h:outputText value="<br><br>" escape="false" />
   <sakai:flat_list value="#{wizard.current.instruction.attachments}" var="attachment">
      <h:column>
      <h:outputText value="<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' hspace='0' />" escape="false" />
      <h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_new">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"/>
      </h:column>
   </sakai:flat_list>
   <h:outputText value="<br><br>" escape="false" rendered="#{not empty wizard.current.instruction.attachments}" />
   
   
   
   <h:outputText value="<b>#{msgs.guidance_rationale}</b><br><br>" escape="false" />
   <h:outputText value="#{wizard.current.rationale.text}" />
   <h:outputText value="<br><br>" escape="false" />
   <sakai:flat_list value="#{wizard.current.rationale.attachments}" var="attachment">
      <h:column>
      <h:outputText value="<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' hspace='0' />" escape="false" />
      <h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_new">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"/>
      </h:column>
   </sakai:flat_list>
   <h:outputText value="<br><br>" escape="false" rendered="#{not empty wizard.current.rationale.attachments}" />
   
   
   
   
   <h:outputText value="<b>#{msgs.guidance_examples}</b><br><br>" escape="false" />
   <h:outputText value="#{wizard.current.example.text}" />
   <h:outputText value="<br><br>" escape="false" />
   <sakai:flat_list value="#{wizard.current.example.attachments}" var="attachment">
      <h:column>
      <h:outputText value="<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' hspace='0' />" escape="false" />
      <h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_new">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"/>
      </h:column>
   </sakai:flat_list>
   <h:outputText value="<br><br>" escape="false" rendered="#{not empty wizard.current.example.attachments}" />
   
   
   <f:subview id="thePagesCat"  rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" >
   <h:dataTable value="#{wizard.current.rootCategory.categoryPageList}" var="item" styleClass="listHier lines" headerClass="exclude">
     
      <h:column>
         <f:facet name="header">
            <f:subview id="header">
               <h:outputText value="#{msgs.items}"  />
            </f:subview>
         </f:facet>
         <h:outputLabel value="#{item.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/categoryExpanded.gif" rendered="#{item.category && item.hasChildren}" />
         <h:graphicImage value="/img/category.gif" rendered="#{item.category && !item.hasChildren}" />

         <h:graphicImage value="/img/page.gif" rendered="#{!item.category && !item.wizard}" />
         <!--h:selectBooleanCheckbox id="itemSelect" value="#{item.selected}" /-->
         
         <h:outputText value="#{item.title}" rendered="#{item.category || item.wizard}" />
         
         <h:commandLink action="#{item.processExecPage}" rendered="#{!item.category && !item.wizard}">
         	<h:outputText value="#{item.title}"/>
         </h:commandLink>
         
      </h:column>
      
      
      
      
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_description}" />
         </f:facet>
         <h:outputLabel value="#{item.description}" />
      </h:column>
      
      
   </h:dataTable>
   </f:subview>
   
   
      <ospx:xheader>
         <ospx:xheadertitle id="reflectiontitleheader" value="#{msgs.reflection_section_header}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
      
      <f:subview id="noReflection" 
      	rendered="#{empty wizard.current.runningWizard.reflections && wizard.current.runningWizard.base.status == 'READY'}">
         <h:commandLink action="#{wizard.processActionReflection}">
         	<h:outputText value="#{msgs.reflection_create}"/>
         </h:commandLink>
      </f:subview>
      
      <f:subview id="showReflection" rendered="#{not empty wizard.current.runningWizard.reflections}">
      
         <f:subview id="displayReflection" rendered="#{wizard.current.runningWizard.base.status != 'READY'}">
            <h:outputLink value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.externalUri}" target="_blank">
               <f:verbatim>
                  <img src = '/library/image/sakai/generic.gif' border= '0' hspace='0' />
               </f:verbatim>
               <h:outputText value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.displayName}"/>
            </h:outputLink>
         </f:subview>
         <f:subview id="editReflection" rendered="#{wizard.current.runningWizard.base.status == 'READY'}">
           <f:verbatim>
              <img src = '/library/image/sakai/generic.gif' border= '0' hspace='0' />
           </f:verbatim>
                     
           <h:outputText value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.displayName}" />
           <div class="itemAction indnt2">
              <h:commandLink action="#{wizard.processEditReflection}">
                 <h:outputText value="#{msgs.reflection_edit}"/>
              </h:commandLink></a>
           </div>
         </f:subview>
         
         <h:outputText value="<br><br>" escape="false" />
      </f:subview>
      </ospx:xheaderdrawer>
   </ospx:xheader>
      

   <ospx:xheader rendered="#{not empty wizard.current.runningWizard.reviews}">
      <ospx:xheadertitle id="wizardReviews" value="#{msgs.wizard_reviews}" />
      <ospx:xheaderdrawer initiallyexpanded="false" cssclass="drawerBorder">

         <sakai:flat_list value="#{wizard.current.runningWizard.reviews}" var="review">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
               <h:outputLink value="#{review.reviewContentNode.externalUri}" target="_blank">
                  <h:outputText value="#{review.reviewContentNode.displayName}" />
               </h:outputLink>               
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_owner}" />
               </f:facet>
               <h:outputText value="#{review.reviewContentNode.technicalMetadata.owner.displayName}" />
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_date}" />
               </f:facet>
               <h:outputText value="#{review.reviewContentNode.technicalMetadata.creation}" />
            </h:column>
         </sakai:flat_list>
      </ospx:xheaderdrawer>
  </ospx:xheader>
     
   <ospx:xheader rendered="#{not empty wizard.current.runningWizard.evaluations}">
      <ospx:xheadertitle id="wizardEvals" value="#{msgs.wizard_evals}" />
      <ospx:xheaderdrawer initiallyexpanded="false" cssclass="drawerBorder">

         <sakai:flat_list value="#{wizard.current.runningWizard.evaluations}" var="eval">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
               <h:outputLink value="#{eval.reviewContentNode.externalUri}" target="_blank">
                  <h:outputText value="#{eval.reviewContentNode.displayName}" />
               </h:outputLink>
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_owner}" />
               </f:facet>
               <h:outputText value="#{eval.reviewContentNode.technicalMetadata.owner.displayName}" />
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_date}" />
               </f:facet>
               <h:outputText value="#{eval.reviewContentNode.technicalMetadata.creation}" />
            </h:column>
         </sakai:flat_list>
      </ospx:xheaderdrawer>
  </ospx:xheader>
      
   
   
<f:subview id="seqWizardButtons"  rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}" >
   <sakai:button_bar>
    <sakai:button_bar_item id="submitContinue" value="#{msgs.save_continue_wizard}"
       action="#{wizard.processExecPages}" />
    <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" action="#{wizard.processActionCancel}"
        />
   </sakai:button_bar>
</f:subview>
<f:subview id="hierWizardButtons"  rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" >
   <sakai:button_bar>
    <sakai:button_bar_item id="returnToList" value="#{msgs.wizard_list}"
       action="#{wizard.processActionCancel}" />
    <sakai:button_bar_item id="cancel" value="#{msgs.submit_wizard_for_evaluation}" 
       rendered="#{wizard.current.runningWizard.base.status == 'READY'}"
       action="confirmSubmit" immediate="true"
        />
   </sakai:button_bar>
</f:subview>

     

</h:form>
</sakai:view>

</f:view>
