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
   <sakai:view_title value=""/>
   <sakai:instruction_message value="" />
   <sakai:messages />

<h:form>
   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">

      <ospx:xmlDocument  factory="#{freeForm.factory}"
         xmlFile="#{freeForm.currentPage.xmlFile}"
         var="freeForm.currentPage.regionMap"/>

      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="items.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>

   <h:commandButton action="#{freeForm.processActionBack}"
      value="#{msgs.back}"/>
   <h:commandButton action="#{freeForm.processActionContinue}"
      value="#{msgs.continue}"/>
   <h:commandButton action="#{freeForm.processActionSave}"
      value="#{msgs.save}"/>
   <h:commandButton action="#{freeForm.processActionCancel}"
      value="#{msgs.cancel}"/>

</h:form>

</sakai:view>
</f:view>
