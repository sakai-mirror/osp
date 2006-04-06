<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<f:loadBundle basename="org.theospi.portfolio.wizard.bundle.Messages" var="msgs"/>
<sakai:view>
<h:form styleClass="portletBody">

   <%@include file="steps.jspf"%>

   <sakai:tool_bar>
      <sakai:tool_bar_item
         action="#{wizard.current.rootCategory.processActionNewPage}"
         value="#{msgs.new_root_wizard_page}" rendered="#{!wizard.moving}"/>
      <sakai:tool_bar_item
         rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.moving}"
         action="#{wizard.current.rootCategory.processActionNewCategory}"
         value="#{msgs.new_root_wizard_category}" />
   </sakai:tool_bar>

   <sakai:view_title value="#{msgs.edit_wizard}" rendered='#{!wizard.current.newWizard}'/>
   <sakai:view_title value="#{msgs.add_wizard}"  rendered='#{wizard.current.newWizard}'/>
   
   <f:subview id="instructionsHier" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.moving}">
      <sakai:instruction_message value="#{msgs.wizard_pages_instructions_hier}" />
   </f:subview>
   <f:subview id="instructionsSeq" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.sequential' && !wizard.moving}">
      <sakai:instruction_message value="#{msgs.wizard_pages_instructions_seq}" />
   </f:subview>
   <f:subview id="instructionsMove" rendered="#{wizard.moving}">
      <sakai:instruction_message value="#{wizard.movingInstructions}" />
   </f:subview>

<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages />

   <%@include file="wizardPropertiesFrame.jspf"%>

   <sakai:flat_list value="#{wizard.current.rootCategory.categoryPageList}" var="item">
      <h:column rendered="#{wizard.moving}">
         <f:facet name="header">
            <h:outputText value="" />
         </f:facet>
         <h:graphicImage value="/img/arrowhere.gif" rendered="#{item.moveTarget}" />
      </h:column>
      <h:column>
         <f:facet name="header">
            <f:subview id="header">
               <h:outputText value="#{msgs.category_page_title_column_header_hier}"
               		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
               <h:outputText value="#{msgs.category_page_title_column_header_seq}"
               		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}"/>
            </f:subview>
         </f:facet>
         <h:outputLabel value="#{item.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/categoryExpanded.gif" rendered="#{item.category && item.hasChildren}" />
         <h:graphicImage value="/img/category.gif" rendered="#{item.category && !item.hasChildren}" />

         <h:graphicImage value="/img/page.gif" rendered="#{!item.category}" />
         <!--h:selectBooleanCheckbox id="itemSelect" value="#{item.selected}" /-->
         <h:outputLabel value="#{item.title}"/>
         <f:facet name="footer">
            <f:subview id="moveFooter" rendered="#{wizard.moving}">
               <h:commandLink action="#{wizard.current.rootCategory.processActionMoveTo}"
                  rendered="#{wizard.current.rootCategory.containerForMove}">
                  <h:outputText value="#{msgs.move_to_here_category}" rendered="#{wizard.moveCategoryChild.category}"/>
                  <h:outputText value="#{msgs.move_to_here_page}" rendered="#{!wizard.moveCategoryChild.category}"/>
               </h:commandLink>
               <h:outputText value=" | " rendered="#{wizard.current.rootCategory.containerForMove}"/>
               <h:commandLink action="#{wizard.moveCategoryChild.processActionCancelMove}" rendered="#{wizard.moving}">
                  <h:outputText value="#{msgs.cancel_move}" />
               </h:commandLink>
            </f:subview>
         </f:facet>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.actions_column_header}" />
         </f:facet>
         <h:commandLink action="#{item.processActionEdit}" rendered="#{!wizard.moving}">
            <h:outputText value="#{msgs.editProperties}" />
         </h:commandLink>
         <h:outputText value=" | "  rendered="#{!wizard.moving && !wizard.current.base.published}"/>
         <h:commandLink action="#{item.processActionDelete}" rendered="#{!wizard.moving && !wizard.current.base.published}">
            <h:outputText value="#{msgs.delete}" />
         </h:commandLink>

         <h:outputText value=" | " rendered="#{item.category && !wizard.moving && !wizard.current.base.published}"/>
         <h:commandLink action="#{item.processActionNewCategory}" rendered="#{item.category && !wizard.moving && !wizard.current.base.published}">
            <h:outputText value="#{msgs.new_category}" />
         </h:commandLink>
         <h:outputText value=" | "  rendered="#{item.category && !wizard.moving && !wizard.current.base.published}"/>
         <h:commandLink action="#{item.processActionNewPage}" rendered="#{item.category && !wizard.moving && !wizard.current.base.published}">
            <h:outputText value="#{msgs.new_page}" />
         </h:commandLink>

         <h:outputText value=" | " rendered="#{!wizard.moving && !wizard.current.base.published &&
               wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>
         <h:commandLink action="#{item.processActionMove}" rendered="#{!wizard.moving && !wizard.current.base.published &&
               wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
            <h:outputText value="#{msgs.move_category}" rendered="#{item.category}"/>
            <h:outputText value="#{msgs.move_page}" rendered="#{!item.category}"/>
         </h:commandLink>

         <h:commandLink action="#{item.processActionMoveTo}" rendered="#{item.category && item.containerForMove}">
            <h:outputText value="#{msgs.move_to_here_category}" rendered="#{wizard.moveCategoryChild.category}"/>
            <h:outputText value="#{msgs.move_to_here_page}" rendered="#{!wizard.moveCategoryChild.category}"/>
         </h:commandLink>

      </h:column>
      <h:column rendered="#{!wizard.moving && !wizard.current.base.published}">
         <f:facet name="header">
            <h:outputText value="#{msgs.re_order}" />
         </f:facet>
         <h:commandLink action="#{item.moveUp}" rendered="#{!item.first}">
            <h:graphicImage value="/img/arrowUp.gif" />
         </h:commandLink>
         <h:commandLink action="#{item.moveDown}" rendered="#{!item.last}">
            <h:graphicImage value="/img/arrowDown.gif" />
         </h:commandLink>
      </h:column>
   </sakai:flat_list>
   <f:subview id="buttonBar" rendered="#{!wizard.moving}">
      <%@include file="builderButtons.jspf"%>
   </f:subview>

</h:form>
</sakai:view>

</f:view>
