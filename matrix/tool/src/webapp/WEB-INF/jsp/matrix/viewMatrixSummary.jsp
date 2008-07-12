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
    <link href="/extraction-tool/lib/ext-2.1/resources/css/ext-all.css" type="text/css" rel="stylesheet" media="all"/>
    
    <link href="/extraction-tool/lib/ext-2.1/examples/grid/grid-examples.css" type="text/css" rel="stylesheet" />
    
    <style type="text/css">
        body .x-panel {
            margin-bottom:20px;
        }
        .icon-grid {
            background-image:url(/extraction-tool/lib/ext-2.1/examples/shared/icons/fam/grid.png) !important;
        }
        #button-grid .x-panel-body {
            border:1px solid #99bbe8;
            border-top:0 none;
        }
        .add {
            background-image:url(/extraction-tool/lib/ext-2.1/examples/shared/icons/fam/add.gif) !important;
        }
        .option {
            background-image:url(/extraction-tool/lib/ext-2.1/examples/shared/icons/fam/plugin.gif) !important;
        }
        .remove {
            background-image:url(/extraction-tool/lib/ext-2.1/examples/shared/icons/fam/delete.gif) !important;
        }
        .save {
            background-image:url(/extraction-tool/lib/ext-2.1/examplesshared/icons/save.gif) !important;
        }
    </style>
    
    
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <title><%= org.sakaiproject.tool.cover.ToolManager.getCurrentTool().getTitle()%></title>
    <script type="text/javascript"  src="/library/js/headscripts.js">
    </script>
    <script type="text/javascript" src="/osp-common-tool/js/eport.js"></script>    	
<script type="text/javascript" src="/extraction-tool/lib/ext-2.1/adapter/jquery/jquery.js">//empty</script>
<script type="text/javascript" src="/extraction-tool/lib/ext-2.1/adapter/jquery/ext-jquery-adapter.js">//empty</script>
<script type="text/javascript" src="/extraction-tool/lib/ext-2.1/ext-all.js">//empty</script>
<script type="text/javascript" src="/extraction-tool/lib/trimpath-template-1.0.38.js">//empty</script>
<script type="text/javascript">
var osp;
if (!osp) osp = {};
osp.summaryScaffoldingId = '<%= request.getAttribute("summary-scaffolding-id") %>';
osp.summaryJobId = '<%= request.getAttribute("summary-job-id") %>';
</script>
<script type="text/javascript" src="<%= request.getAttribute("summary-script") %>"> //empty </script>
    	
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

	<div class="navIntraTool">
		<a href="<osp:url value="listScaffolding.osp"/>"><fmt:message key="action_list"/></a>
		<a href="<osp:url value="viewMatrix.osp"/>&scaffolding_id=${matrixData.scaffolding.id}">View Matrix</a>
	</div>
	
<h3>Summary View</h3>

<p>
<a href="/extraction-tool/data/<%= request.getAttribute("summary-job-id") %>/<%= request.getAttribute("summary-scaffolding-id") %>/table?format=xls">
Download Spreadsheet (Excel format)
</a>
</p>

<div id="matrix-summary">Summarizing data...</div>

         <c:if test="${not empty requestScope.panelId}"></div></c:if>
      </div>
   </body>
</html>
