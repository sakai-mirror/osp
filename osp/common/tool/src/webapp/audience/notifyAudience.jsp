<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
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

<sakai:view_title rendered="#{!audience.inviteFeedbackAudience}" value="#{common_msgs.title_notifyViewers}"/>
<sakai:view_title rendered="#{audience.inviteFeedbackAudience}" value="#{common_msgs.title_notifyUsers}"/>

<c:if test="${!audience.inviteFeedbackAudience}">
	<p class="instruction"><h:outputText value="#{common_msgs.instructions_pickUsersFromList}"/></p>
</c:if>
<c:if test="${audience.inviteFeedbackAudience}">
	<sakai:instruction_message value="#{common_msgs.instructions_notifyViewersChangesToX}"/>
</c:if>

<h3><div class="highlight"><h:outputText value="#{audience.pageContext}"/></div></h3>
<div class="highlight"><h:outputText value="#{audience.pageContext2}"/></div>
<sakai:view>   




<sakai:messages/>

<h:form id="mainForm">
    
    
    
    <c:if test="${!audience.inviteFeedbackAudience}">
		<p class="indnt2">
			<h:selectManyListbox id="selectedUsers" size="10" value="#{audience.selectedArray}"
														   style="width:350px;">
			   <f:selectItems value="#{audience.selectedList}"/>
			</h:selectManyListbox>
		</p>
    </c:if>
    
    
    
    <p class="longtext">
    	<sakai:view_title value="#{common_msgs.label_yourMessage}" />
        <label class="block"><fmt:message key="label_yourMessage"/></label>
        <h:inputTextarea id="message" rows="5" cols="80" value="#{audience.message}"/>
    </p>
    
    <sakai:button_bar>
        <sakai:button_bar_item id="save_button" action="#{audience.processActionNotify}"
                               value="#{common_msgs.notify_audience}" styleClass="active" accesskey="s" />
        <sakai:button_bar_item id="_target1" action="#{audience.processActionCancel}"
                               value="#{common_msgs.cancel_audience}" accesskey="x" />
    </sakai:button_bar>
</h:form>

</sakai:view>
</f:view>