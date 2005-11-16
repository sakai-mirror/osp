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
<f:loadBundle basename="org.theospi.portfolio.guidance.messages" var="msgs"/>

<sakai:view>
   <sakai:view_title value="#{msgs.guidance_title}"/>
   <sakai:instruction_message value="#{guidance.guidanceInstructions}" />
   <sakai:messages />

<h:form>
  <ospx:xheader>
      <ospx:xheadertitle id="instructionTitle">
          <h:outputText value="#{msgs.instruction_title_add}" />
      </ospx:xheadertitle>
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
          <ospx:splitarea direction="horizontal" width="100%">
             <ospx:splitsection size="475" valign="top">
               <sakai:instruction_message value="#{msgs.instruction_message}" />
               <sakai:inputRichText value="#{guidance.current.instruction.base.text}" rows="15" cols="60" buttonSet="large" />
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <sakai:flat_list value="#{guidance.current.instruction.attachments}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:commandButton action="#{guidance.current.instruction.processActionManageAttachments}"
                           value="#{msgs.manage_instruction}"/>
                     </f:facet>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_new">
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
             </ospx:splitsection>
         </ospx:splitarea>
  	   </ospx:xheaderdrawer>
  </ospx:xheader>

  <ospx:xheader>
      <ospx:xheadertitle id="exampleTitle">
          <h:outputText value="#{msgs.example_title_add}" />
      </ospx:xheadertitle>
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
          <ospx:splitarea direction="horizontal" width="100%">
             <ospx:splitsection size="475" valign="top">
               <sakai:instruction_message value="#{msgs.example_message}" />
               <sakai:inputRichText value="#{guidance.current.example.base.text}" rows="15" cols="60" buttonSet="large" />
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <sakai:flat_list value="#{guidance.current.example.attachments}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:commandButton action="#{guidance.current.example.processActionManageAttachments}"
                           value="#{msgs.manage_instruction}"/>
                     </f:facet>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_new">
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
             </ospx:splitsection>
         </ospx:splitarea>
      </ospx:xheaderdrawer>
  </ospx:xheader>

  <ospx:xheader>
      <ospx:xheadertitle id="rationaleTitle">
          <h:outputText value="#{msgs.rationale_title_add}" />
      </ospx:xheadertitle>
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
          <ospx:splitarea direction="horizontal" width="100%">
             <ospx:splitsection size="475" valign="top">
               <sakai:instruction_message value="#{msgs.rationale_message}" />
               <sakai:inputRichText value="#{guidance.current.rationale.base.text}" rows="15" cols="60" buttonSet="large" />
             </ospx:splitsection>
             <ospx:splitsection valign="top">
               <sakai:flat_list value="#{guidance.current.rationale.attachments}" var="material">
                  <h:column>
                     <f:facet name="header">
                        <h:commandButton action="#{guidance.current.rationale.processActionManageAttachments}"
                           value="#{msgs.manage_instruction}"/>
                     </f:facet>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_new">
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
             </ospx:splitsection>
         </ospx:splitarea>
      </ospx:xheaderdrawer>
  </ospx:xheader>

	<h:commandButton id="cancel" value="#{msgs.cancel_guidance}" action="#{guidance.processActionCancel}" />
	<h:commandButton id="submit" value="#{msgs.save_guidance}" action="#{guidance.processActionSave}" />

</h:form>
</sakai:view>

</f:view>
