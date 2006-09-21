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
   <sakai:messages />

<h:form>
    <sakai:panel_titled title="#{common_msgs.instruction_title}" >
       <ospx:splitarea direction="horizontal" width="100%">
          <ospx:splitsection size="475" valign="top">
            <h:outputText value="#{guidance.current.instruction.base.text}" escape="false" />
          </ospx:splitsection>
          <ospx:splitsection valign="top">
            <sakai:flat_list value="#{guidance.current.instruction.attachments}" var="material">
               <h:column>
                  <f:facet name="header">
                     <h:outputText value="#{common_msgs.attachments}"/>
                  </f:facet>
                  <h:outputLink title="#{material.displayName}"
                     value="#{material.fullReference.base.url}" target="_new">
                     <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                     <h:graphicImage id="instrFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                     <h:outputText value="#{material.displayName}"/>
                  </h:outputLink>
               </h:column>
            </sakai:flat_list>
          </ospx:splitsection>
      </ospx:splitarea>
    </sakai:panel_titled>
    <sakai:panel_titled title="#{common_msgs.example_title}" >
       <ospx:splitarea direction="horizontal" width="100%">
          <ospx:splitsection size="475" valign="top">
            <h:outputText value="#{guidance.current.example.base.text}" escape="false" />
          </ospx:splitsection>
          <ospx:splitsection valign="top">
            <sakai:flat_list value="#{guidance.current.example.attachments}" var="material">
               <h:column>
                  <f:facet name="header">
                     <h:outputText value="#{common_msgs.attachments}"/>
                  </f:facet>
                  <h:outputLink title="#{material.displayName}"
                     value="#{material.fullReference.base.url}" target="_new">
                     <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                     <h:graphicImage id="exampleFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                     <h:outputText value="#{material.displayName}"/>
                  </h:outputLink>
               </h:column>
            </sakai:flat_list>
          </ospx:splitsection>
      </ospx:splitarea>
    </sakai:panel_titled>
    <sakai:panel_titled title="#{common_msgs.rationale_title}" >
       <ospx:splitarea direction="horizontal" width="100%">
          <ospx:splitsection size="475" valign="top">
            <h:outputText value="#{guidance.current.rationale.base.text}" escape="false" />
          </ospx:splitsection>
          <ospx:splitsection valign="top">
            <sakai:flat_list value="#{guidance.current.rationale.attachments}" var="material">
               <h:column>
                  <f:facet name="header">
                     <h:outputText value="#{common_msgs.attachments}"/>
                  </f:facet>
                  <h:outputLink title="#{material.displayName}"
                     value="#{material.fullReference.base.url}" target="_new">
                     <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                     <h:graphicImage id="rationaleFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                     <h:outputText value="#{material.displayName}"/>
                  </h:outputLink>
               </h:column>
            </sakai:flat_list>
          </ospx:splitsection>
      </ospx:splitarea>
    </sakai:panel_titled>

	<h:commandButton id="cancel" value="#{common_msgs.back_guidance}" action="#{guidance.processActionCancel}" />

</h:form>
</sakai:view>

</f:view>
