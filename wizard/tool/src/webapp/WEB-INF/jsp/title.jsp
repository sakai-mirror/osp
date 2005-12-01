<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.sakaiproject.service.legacy.site.ToolConfiguration"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
     <link href="<c:out value="${sakai_skin_base}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
 <link href="<c:out value="${sakai_skin}"/>"
       type="text/css"
       rel="stylesheet"
       media="all" />
 <meta http-equiv="Content-Style-Type" content="text/css" />
 <title>Sakai</title>
 <script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js">
 </script>
</head>
<body onload="setMainFrameHeight('<c:out value="${mainPanel}"/>'); setFocus(focus_path);parent.updCourier(doubleDeep, ignoreCourier);">
<table border="0" cellpadding="0" cellspacing="0" width="100%" class="toolTitle" summary="layout">
      <%pageContext.setAttribute("resetUrl",((ToolConfiguration)request.getAttribute("tool")).getConfig().getProperty("theospi.resetUrl")); 
      pageContext.setAttribute("helpDocId",((ToolConfiguration)request.getAttribute("tool")).getConfig().getProperty("theospi.helpDocId"));%>
	<tr>
		<td class="title">
         <c:if test="${not empty resetUrl}">
            <a href="/portal/tool/<c:out value="${tool.id}"/>/<c:out value="${resetUrl}"/>?panel=Main"
               target="<c:out value="${mainPanel}"/>"  title="Reset"><img src="/library/image/transparent.gif" alt="Reset" border="0"/></a>
         </c:if>
         <c:out value="${tool.title}"/>
		</td>
		<td class="action">
		<c:if test="${not empty helpDocId}">
         <a href="javascript:;" onClick="window.open('/tunnel/sakai-help-tool/help/jsf.tool?helpDocId=<c:out value="${helpDocId}"/>&pid=/tunnel/sakai-help-tool/help/jsf.tool','Help',
            'resizable=0,toolbar=no,scrollbars=yes, width=800,height=600')">
            <img src="/library/image/transparent.gif" alt="Help" border="0"/>
         </a>
      </c:if>
	   </td>
	</tr>
</table>
</body>
</html>