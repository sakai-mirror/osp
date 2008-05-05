<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%
		response.setContentType("text/html; charset=UTF-8");
%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all" href="/osp-common-tool/css/eport.css" />
    <link href="<c:out value="${sakai_skin_base}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
    <link href="<c:out value="${sakai_skin}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
    <link href="/library/ext-2.1/resources/css/ext-all.css" type="text/css" rel="stylesheet" media="all"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <title><%= org.sakaiproject.tool.cover.ToolManager.getCurrentTool().getTitle()%></title>
    <script type="text/javascript"  src="/library/js/headscripts.js">
    </script>
    <script type="text/javascript" src="/osp-common-tool/js/eport.js"></script>
    	<!--  <script type="text/javascript" language="JavaScript" src="/library/js/jquery-1.1.2.js"></script>-->
    	
<script type="text/javascript" src="/library/ext-2.1/adapter/jquery/jquery.js">//empty</script>
<script type="text/javascript" src="/library/ext-2.1/adapter/jquery/ext-jquery-adapter.js">//empty</script>
<script type="text/javascript" src="/library/ext-2.1/ext-all.js">//empty</script>
<script type="text/javascript" src="/library/from-markup.js"> //empty </script>
    	
  <%
      String panelId = request.getParameter("panel");
      if (panelId == null) {
         panelId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
      }

  %>
<%@ include file="matrixStyle.jspf" %>
  

  <script type="text/javascript">
   function resetHeight() {
      setMainFrameHeight('<%= org.sakaiproject.util.Validator.escapeJavascript(panelId)%>');
   }

   function loaded() {
      resetHeight();
      parent.updCourier(doubleDeep, ignoreCourier);
      if (parent.resetHeight) {
         parent.resetHeight();
      }
   }
  </script>
  </head>
  <body onload="loaded();">
      <div class="portletBody">
         <c:if test="${not empty requestScope.panelId}"><div class="ospEmbedded"></c:if>


<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.matrix.bundle.Messages"/>

<!-- TODO: test for isExposedPage -->
	<div class="navIntraTool">
		<a href="<osp:url value="listScaffolding.osp"/>"><fmt:message key="action_list"/></a>
		<a href="<osp:url value="viewMatrix.osp"/>&scaffolding_id=${matrixData.scaffolding.id}">View Matrix</a>
	</div>
	
	<%-- 
	<form action="<osp:url value="viewMatrixSummary.osp"/>" method="get">
		<input type="hidden" name="scaffolding_id" value="${matrixData.scaffolding.id}"/>
		<label for="job">Job</label> <input type="text" name="job" id="job"/><br />
		<label for="xsl">XSL</label> <input type="text" name="xsl" id="xsl"/><br />
		<label for="data">Data</label>
		<textarea name="data" id="data" rows="10" cols="80"></textarea>
		<input type="submit" name="submit" value="Submit"/>
	</form>
	--%>

<h3>Summary View</h3>

<%-- 
<pre>
<c:out value="${matrixData.raw}"></c:out>
</pre>
--%>


<c:if test="${matrixData.shouldTransform}">
<osp-c:transformXml 
 template="<%= request.getAttribute("renderer") %>" 
 doc="<%= request.getAttribute("document") %>"
 omitProlog="true" />
</c:if>

<%--
<c:forEach var="thing" items="${matrixData.things}">
	<pre><c:out value="${matrixData.stuff[thing]}"/></pre>
</c:forEach>
--%>

         <c:if test="${not empty requestScope.panelId}"></div></c:if>
      </div>
   </body>
</html>
