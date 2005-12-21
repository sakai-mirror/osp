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
<f:loadBundle basename="org.theospi.portfolio.common.messages" var="msgs"/>

<sakai:view>
   <sakai:view_title value="#{msgs.review_title}"/>
   <sakai:messages />

<h:form>

This is the review form.

         <sakai:instruction_message value="#{msgs.review_instruction_message}" />
            <sakai:flat_list value="#{review.current.base.reviewAttachment}" var="content">
               <h:column>
                  <f:facet name="header">
                     <h:commandButton action="#{review.processActionChooseForm}"
                        value="#{msgs.manage_review_content}"/>
                  </f:facet>
                  <h:outputLink title="#{content.displayName}"
                     value="#{content.fullReference.base.url}" target="_new">
                     <h:outputText value="#{content.displayName}"/>
                  </h:outputLink>
               </h:column>
            </sakai:flat_list>


   <h:commandButton id="cancel" value="#{msgs.cancel_review}" action="#{review.processActionCancel}" />
   <h:commandButton id="submit" value="#{msgs.save_review}" action="#{review.processActionSave}" />
   
</h:form>
</sakai:view>

</f:view>