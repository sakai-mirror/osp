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
<f:loadBundle basename="org.theospi.portfolio.portal.messages" var="msgs"/>
<sakai:view>
<h:form>

<sakai:pager id="pager"
    totalItems="#{siteType.sites.totalItems}"
    firstItem="#{siteType.sites.firstItem}"
    pageSize="#{siteType.sites.pageSize}"
    textStatus="#{msgs.site_types_pager_status}" />

   <sakai:flat_list value="#{siteType.sites.subList}" var="site">
      <h:column>
         <h:outputLink
            value="/osp-portal/site/#{site.id}" target="_parent"
            title="#{site.title}">
               <h:outputText value="#{site.title}"/>
         </h:outputLink>
         <h:outputText value=" -- " rendered="#{site.description != null}" />
         <h:outputText value="#{site.description}"/>
      </h:column>
   </sakai:flat_list>

   </h:form>
   </sakai:view>
</f:view>
