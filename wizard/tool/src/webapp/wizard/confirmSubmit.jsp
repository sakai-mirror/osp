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

    <h3><osp:message key="submit_cell_confirmation"/></h3>
   
      <span class="chefAlert"><fmt:message key="complete_wizard"/></span>
      <br />

	<table class="listHier">
	   <tr>
	      <th><fmt:message key="table_header_title"/></th>
          <th><fmt:message key="label_description"/></th>
       </tr>
       <tr>
          <td><c:out value="${wizard.current.title}"/></td>
          <td><c:out value="${wizard.current.description}" escapeXml="false"/></td>
       </tr>
    </table>
    
   <sakai:view_title value="#{msgs.complete_wizard}"/>
   <sakai:instruction_message value="#{msgs.complete_wizard_instructions}" />

<sakai:button_bar>
   <sakai:button_bar_item id="submit" value="#{msgs.submit_wizard}"
      action="#{wizard.current.runningWizard.processSubmitWizard}" />
   <sakai:button_bar_item id="cancel" value="#{msgs.cancel_submit_wizard}"
      action="runWizard" immediate="true" />
</sakai:button_bar>


</h:form>
</sakai:view>
</f:view>