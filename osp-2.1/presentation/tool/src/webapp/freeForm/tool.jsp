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
<f:loadBundle basename="org.theospi.portfolio.presentation.messages" var="msgs"/>

<sakai:view>
<h:form>

   <sakai:tool_bar>
      <sakai:tool_bar_item
      action="#{freeForm.processActionNewPage}"
      value="#{msgs.new_page}" />
   </sakai:tool_bar>

   <sakai:view_title value=""/>
   <sakai:instruction_message value="" />
   <sakai:messages />

   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">


   <sakai:flat_list value="#{freeForm.pageList}" var="page">
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.presentation_pages_header}" />
         </f:facet>
      </h:column>
      <h:column>
         <h:outputText value="#{page.base.title}"/>
         <h:commandLink action="#{page.processActionArrange}">
            <h:outputText value="#{msgs.arrangeItems}"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{page.processActionEdit}">
            <h:outputText value="#{msgs.editProperties}" />
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{page.processActionDelete}">
            <h:outputText value="#{msgs.delete}" />
         </h:commandLink>
      </h:column>
   </sakai:flat_list>



      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="items.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

   <f:subview id="navigation">
      <%@ include file="navigation.jspf" %>
   </f:subview>

</h:form>

</sakai:view>
</f:view>
