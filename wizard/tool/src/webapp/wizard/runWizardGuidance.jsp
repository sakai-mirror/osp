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
	   
	<sakai:tool_bar>
      <sakai:tool_bar_item rendered="#{wizard.canCreate}"
      action="manageWizardStatus"
      value="#{msgs.manage_wizard_status}" />
    </sakai:tool_bar>

   <h:outputText value="#{msgs.wizard_preview_title}" styleClass="validation" rendered="#{wizard.current.base.preview}"/>

   <sakai:view_title value="#{wizard.current.base.name}"/>

   <sakai:messages />
   
   <h:outputText value="#{wizard.lastSavePage} #{msgs.page_was_submitted}" styleClass="success" rendered="#{wizard.lastSavePage != ''}" />
   <h:outputText value="#{msgs.changes_saved}" styleClass="success" rendered="#{wizard.pageSaved}" />
   
   <sakai:instruction_message value="#{wizard.current.base.description}" />
   
   
   <f:subview id="viewUsers" rendered="#{(wizard.canEvaluateTool || wizard.canReviewTool) && wizard.current.base.published}">

    	<h:selectOneMenu id="users" immediate="true" value="#{wizard.currentUserId}" valueChangeListener="#{wizard.current.processActionChangeUser}" onchange="this.form.submit();">
		  	<f:selectItems value="#{wizard.current.userListForSelect}"/>
		</h:selectOneMenu>
		<%@include file="showWizardOwnerMessage.jspf"%>
   </f:subview>
  

   
   <f:subview id="instructionSV" rendered="#{(wizard.current.instruction.text != '' and wizard.current.instruction != null) || not empty wizard.current.instruction.attachments}">
	
		<ospx:xheader>
			<ospx:xheadertitle id="instructionsHeader">
				<h:outputText value="#{msgs.guidance_instructions}" />
			</ospx:xheadertitle>
		</ospx:xheader>
  		 	
   		<div class="textPanel"><h:outputText value="#{wizard.current.instruction.text}" escape="false" /></div>
   </f:subview>
   <h:dataTable value="#{wizard.current.guidanceInstructionsAttachments}" var="attachment"  rendered="#{not empty wizard.current.instruction.attachments}" border="0" styleClass="indnt1" style="width:50%">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="instrFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
         <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
		 <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})" styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>

   
   <f:subview id="guidanceSV" rendered="#{(wizard.current.rationale.text != '' and wizard.current.rationale != null) || not empty wizard.current.rationale.attachments}">
		<ospx:xheader>
			<ospx:xheadertitle id="rationaleHeader">
				<h:outputText value="#{msgs.guidance_rationale}" />
			</ospx:xheadertitle>
		</ospx:xheader>
   		<div class="textPanel"><h:outputText value="#{wizard.current.rationale.text}" escape="false" /></div>
  	</f:subview>  
   <h:dataTable value="#{wizard.current.guidanceRationaleAttachments}" var="attachment"  rendered="#{not empty wizard.current.rationale.attachments}"  border="0" styleClass="indnt1" style="width:50%">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="rationaleFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
      <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"  styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>
   
   <f:subview id="exapmleSV" rendered="#{(wizard.current.example.text != '' and wizard.current.example != null) || not empty wizard.current.example.attachments}">
		<ospx:xheader>
			<ospx:xheadertitle id="exampleHeader">
				<h:outputText value="#{msgs.guidance_examples}" />
			</ospx:xheadertitle>
		</ospx:xheader>
   	  <div class="textPanel"><h:outputText value="#{wizard.current.example.text}" escape="false" /></div>
   </f:subview> 
   <h:dataTable value="#{wizard.current.guidanceExamplesAttachments}" var="attachment" border="0" styleClass="indnt1" style="width:50%" rendered="#{not empty wizard.current.example.attachments}" >
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="exampleFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
      <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"  styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>
   
 
    <f:subview id="thePagesCat" >
   <h:dataTable value="#{wizard.current.runningWizard.rootCategory.categoryPageList}" var="item" styleClass="listHier lines nolines" headerClass="exclude">
   
     <h:column>
     	 <f:facet name="header">
            <f:subview id="header">
               <h:outputText value="#{msgs.items}"  />
            </f:subview>
         </f:facet>
     	<f:subview id="categoryView" rendered="#{item.classInfo == 'completedCategory'}" >
         <h:outputLabel value="#{item.category.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/categoryExpanded.gif" rendered="#{item.category.category && item.category.hasChildren}" />
         <h:graphicImage value="/img/category.gif" rendered="#{item.category.category && !item.category.hasChildren}" />

         <h:outputText value="#{item.category.title}" rendered="#{item.category.category || item.category.wizard}" />

       </f:subview>
         
         
      <f:subview id="pageView" rendered="#{item.classInfo == 'completedPage'}" >

         <h:outputLabel value="#{item.page.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/page.gif" rendered="#{!item.page.category && !item.page.wizard}" />
                  
         <h:outputText value="#{item.page.title}" rendered="#{item.page.category || item.page.wizard ||  wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
         
         <h:commandLink action="#{item.page.processExecPage}" rendered="#{!item.page.category && !item.page.wizard && wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
         	<h:outputText value="#{item.page.title}"/>
         </h:commandLink>
         <h:outputText value=" #{item.statusThroughBundle}" rendered="#{item.classInfo == 'completedPage'}" />
       </f:subview>
      </h:column>
   
   
         <%-- TODO given that the description could be just about anything (markup wise) the value of having it here should be
	  		balanced with the certain malformed layout and possible  invalid/unbalanced markup (if the trimming is just on chars) --%>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_description}" />
         </f:facet>
         <f:subview id="pageView2" rendered="#{item.classInfo == 'completedPage'}" >
         	<h:outputText value="#{item.page.description}" escape="false" />
         </f:subview>
         <f:subview id="categoryView2" rendered="#{item.classInfo == 'completedCategory'}" >
         	<h:outputText value="#{item.category.description}" escape="false" />
         </f:subview>
      </h:column>
            
   </h:dataTable>
   </f:subview>   
    <!-- ****************** reflection ****************** -->
	<%--TODO  this layout should match the one in matrix cells --%>
    <f:subview id="reflectionArea" rendered="#{wizard.current.base.reflectionDevice != null && 
      				wizard.current.base.reflectionDevice.value != ''}">
<%--      <h:outputText value="<br><br>" escape="false" rendered="#{wizard.current.base.reflectionDevice != null && 
      				wizard.current.base.reflectionDevice.value != ''}" /> --%>
      <ospx:xheader rendered="#{wizard.current.base.reflectionDevice != null || 
      				wizard.current.base.reflectionDevice.value != ''}">
         <ospx:xheadertitle id="reflectiontitleheader" value="#{msgs.reflection_section_header}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
		  <f:verbatim>
			  <div class=" indnt2">
		   </f:verbatim>
      
      <f:subview id="noReflection" 
      	rendered="#{empty wizard.current.runningWizard.reflections && wizard.current.runningWizard.base.status == 'READY' &&
      		not wizard.current.runningWizard.isReadOnly}">
		  <f:verbatim>
			  <div class="itemAction indnt2">
		   </f:verbatim>

         <h:commandLink action="#{wizard.processActionReflection}">
         	<h:outputText value="#{msgs.reflection_create}"/>
         </h:commandLink>
		  <f:verbatim>
			  </div>
		   </f:verbatim>

		 </f:subview>
	  	<f:subview id="showReflection" rendered="#{not empty wizard.current.runningWizard.reflections}">
			<f:subview id="displayReflection" rendered="#{wizard.current.runningWizard.base.status != 'READY' ||
				wizard.current.runningWizard.isReadOnly}">
					<f:verbatim>
						<img src = '/library/image/silk/application_form.gif' border= '0' hspace='0' />
					</f:verbatim>
					<h:outputText value=" " />
				<h:outputLink value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.fixedExternalUri}" target="_blank">
					<h:outputText value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.displayName}"/>
				</h:outputLink>
			</f:subview>
			<f:subview id="editReflection" rendered="#{wizard.current.runningWizard.base.status == 'READY' && 
				not wizard.current.runningWizard.isReadOnly}">
				<f:verbatim>
					<img src = '/library/image/silk/application_form.gif' border= '0' hspace='0' />
				</f:verbatim>
				<h:outputText value=" " />
				<h:outputText value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.displayName}" />
				<f:verbatim>
					<div class="itemAction indnt2">
				</f:verbatim>
				<h:commandLink action="#{wizard.processEditReflection}">
					<h:outputText value="#{msgs.reflection_edit}"/>
				</h:commandLink>
				<f:verbatim>
					</div>
				</f:verbatim>
			</f:subview>
		</f:subview>
		  <f:verbatim>
			  </div>
		   </f:verbatim>

      </ospx:xheaderdrawer>
   </ospx:xheader>
   </f:subview>
   <!-- ****************** feedback ****************** -->
 
   <ospx:xheader rendered="#{wizard.commentItem != ''}">
      <ospx:xheadertitle id="wizardReviews" value="#{msgs.wizard_reviews}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <f:verbatim>
			      <div class="itemAction indnt4" style="margin-bottom:1em;padding-top:0">
               </f:verbatim>
				  <f:subview id="feedbackAdd" 
				  	rendered="#{wizard.canReview && wizard.current.base.reviewDevice != null &&
				  		wizard.current.base.reviewDevice.value != ''}">
                    <h:commandLink action="#{wizard.processActionReview}">
                       <h:outputText value="#{msgs.review_add}"/>
                    </h:commandLink>
				  </f:subview>
              <f:verbatim>
                 </div>
              </f:verbatim>
		<%-- TODO  need a rendered attribute on this list - omit if no items --%>	   
         <sakai:flat_list value="#{wizard.current.runningWizard.reviews}" var="review">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
		<%-- TODO Dupe? --%>	   
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
			   		<f:verbatim>
					<img src = '/library/image/silk/comment.gif' border= '0' hspace='0' /><h:outputText value=" " />
				</f:verbatim>
               <h:outputLink value="#{review.reviewContentNode.fixedExternalUri}" target="_blank">
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
     
     
   <!-- ****************** evaluation ****************** -->
   <ospx:xheader rendered="#{wizard.evaluationItem != ''}">
      <ospx:xheadertitle id="wizardEvals" value="#{msgs.wizard_evals}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <f:verbatim>
			      <div class="itemAction indnt4" style="margin-bottom:1em">
               </f:verbatim>
				  <f:subview id="evaluationAdd" 
				  	rendered="#{wizard.canEvaluate && wizard.current.base.evaluationDevice != null &&
				  		wizard.current.base.evaluationDevice.value != '' &&
				  		wizard.current.runningWizard.base.status == 'PENDING'}">
				  	
                    <h:commandLink action="#{wizard.processActionEvaluate}">
                       <h:outputText value="#{msgs.evaluation_add}"/>
                    </h:commandLink>
				  </f:subview>
              <f:verbatim>
                 </div>
              </f:verbatim>
			   
         <sakai:flat_list value="#{wizard.current.runningWizard.evaluations}" var="eval">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
			   <f:verbatim>
					<img src = '/library/image/silk/comments.gif' border= '0' hspace='0' /><h:outputText value=" " />
				</f:verbatim>
               <h:outputLink value="#{eval.reviewContentNode.fixedExternalUri}" target="_blank">
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
      
   <sakai:button_bar>
		<f:subview id="seqWizardButtons"  rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}" >
       	<sakai:button_bar_item id="submitContinue" value="#{msgs.save_continue_wizard}"
	      	 action="#{wizard.processExecPages}" accesskey="s" styleClass="active" />
		</f:subview>

    <sakai:button_bar_item id="returnToList" value="#{msgs.wizard_list}"
       action="#{wizard.processActionCancelRun}" rendered="#{!wizard.fromEvaluation}"  accesskey="l"/>
    <sakai:button_bar_item id="returnToEvaluations" value="#{msgs.evaluation_list}"
       action="#{wizard.processActionCancelRun}" rendered="#{wizard.fromEvaluation}" />
       
   <f:subview id="evalSubmitSV" rendered="#{wizard.evaluationItem != ''}">
    <sakai:button_bar_item id="submitEvalWizard" value="#{msgs.submit_wizard_for_evaluation}" 
       rendered="#{wizard.current.runningWizard.base.status == 'READY' && wizard.current.runningWizard.isReadOnly == 'false'}"
       action="confirmSubmit" immediate="true"
        />
   </f:subview>
</sakai:button_bar>

</h:form>
</sakai:view>

</f:view>
