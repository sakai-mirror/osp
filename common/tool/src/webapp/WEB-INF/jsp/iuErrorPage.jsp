<%@ page import="java.io.PrintWriter,
                 org.apache.commons.logging.Log,
                 org.apache.commons.logging.LogFactory,
                 org.sakaiproject.service.framework.session.cover.UsageSessionService"%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class ="chefPortletContent">

<img src=/osp-common-tool/img/system_error.gif" height="20" />

<h3 style="color: #900">We're sorry. You have encountered a System Error!</h3>


<%
   Log logger = LogFactory.getLog(getClass());
   Exception ex = (Exception) request.getAttribute("exception");
   logger.error("uncaught exception displayed to user: '" + UsageSessionService.getSessionId() + "'",ex);
   //pageContext.getOut().print(ex.toString());
%>

<hr  size="1" noshade />
  <p>Please help us solve this problem by following these quick and easy steps:</p>
	 <ol>
	   <li>Call your <a href="http://kb.indiana.edu/data/abxl.html" target="_blank">campus help desk</a> or send an 
	       email to <a href="mailto:oncrs@iu.edu?subject=ePortfolio System Error">oncrs@iu.edu</a>.</li>
	   <!-- 
	   <li>Take note of the steps that were taken leading up to this error screen and type these steps in the email 
	       message or relay it to your campus help desk.</li>
	   -->
	   <li>Note the steps taken just before the error occurred.</li>
	   <!-- 
	   <li>If emailing, copy all of the text, beginning with, "The full error message follows" and running through the
	       end of the page and paste this in the email message.</li>
	   -->
	   <li>Paste all of the text of this page in the email</li>
	 </ol>
  <p>Thank you for your providing this information. Your assistance will be an aid in improving the application.</p> 

<hr size="1" noshade />
<h3>The full error message follows:</h3>
<span class="error_message">Your session id: <%= UsageSessionService.getSessionId() %></span>
<pre>
 <% ex.printStackTrace(new PrintWriter(pageContext.getOut()));%>
</pre>

</div>