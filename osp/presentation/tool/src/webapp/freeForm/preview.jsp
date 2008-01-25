<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ taglib uri="http://www.theospi.org" prefix="osp" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%
    response.setContentType("text/html; charset=UTF-8");
    response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
    response.addDateHeader("Last-Modified", System.currentTimeMillis());
    response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
    response.addHeader("Pragma", "no-cache");
%>

<f:view>
    <sakai:view>
        <h:form id="redirectForm">
            <h:commandButton action="main" id="backToMain" value="#{msgs.back}" />

        </h:form>
        <script type="text/javascript" language="JavaScript"> 
            window.open('<c:out value="${freeForm.previewUrl}" />?1=1&id=<c:out value="${presentation.id.value}" />');
        </script>
        <script>
            function redirect() {
                document.forms['redirectForm'].elements['redirectForm:backToMain'].click();
                return true;
            }
             
            redirect();
        </script>
    </sakai:view>
</f:view>
