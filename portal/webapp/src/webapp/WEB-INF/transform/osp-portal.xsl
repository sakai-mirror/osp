<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:osp="http://www.osportfolio.org/OspML"
	xmlns:xs="http://www.w3.org/2001/XMLSchema">

   <xsl:output method="html" version="4.01"  
      encoding="utf-8" indent="yes" doctype-public="http://www.w3.org/TR/html4/loose.dtd"/>

   <xsl:variable name="config" select="/portal/config" />
   <xsl:variable name="externalized" select="/portal/externalized" />

   <xsl:variable name="roles" select="/portal/roles"/>

   <!--
   ============match /portal===============
   main template processing
   ========================================
   -->
	<xsl:template match="portal">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

      <xsl:for-each select="skins/skin">
         <link type="text/css" rel="stylesheet" media="all">
            <xsl:attribute name="href">
               <xsl:value-of select="."/>
            </xsl:attribute>
         </link>
      </xsl:for-each>

    <meta http-equiv="Content-Style-Type" content="text/css" />
      <title><xsl:value-of select="pageTitle" /></title>
      <script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js">
         <xsl:value-of select="' '"/>
      </script>
   </head>
<body class="portalBody">
<a href="#tocontent"  class="skip" accesskey="c">
   <xsl:attribute name="title">
      <xsl:value-of select="$externalized/entry[@key='sit.jumpcontent']"/>
   </xsl:attribute>
   <xsl:value-of select="$externalized/entry[@key='sit.jumpcontent']"/>
</a>
<a href="#toolmenu"  class="skip" accesskey="l">
   <xsl:attribute name="title">
      <xsl:value-of select="$externalized/entry[@key='sit.jumptools']"/>
   </xsl:attribute>
   <xsl:value-of select="$externalized/entry[@key='sit.jumptools']"/>
</a>
<a href="#sitetabs" class="skip" title="jump to worksite list" accesskey="w">
   <xsl:attribute name="title">
      <xsl:value-of select="$externalized/entry[@key='sit.jumpworksite']"/>
   </xsl:attribute>
   <xsl:value-of select="$externalized/entry[@key='sit.jumpworksite']"/>
</a>

   <xsl:call-template name="site_tabs" />

   <xsl:call-template name="breadcrumbs" />

<div id="container" class="project">

<xsl:call-template name="site_tools" />

   <xsl:choose>
      <xsl:when test="siteTypes/siteType/sites/site[@selected='true']">
         <xsl:for-each select="categories/category" >
            <xsl:sort select="@order" data-type="number" />
            <xsl:apply-templates select=".">
               <xsl:with-param name="content" select="'true'"/>
            </xsl:apply-templates>
         </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="portal_tool">
            <xsl:with-param name="base" select="siteTypes/siteType[@selected='true']"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>

<div>
<xsl:call-template name="footer"/>
</div>
</div>
</body></html>
	</xsl:template>

   <!--
   ===============name portal_tool========================
   setup an iframe with the currently selected helper tool
   param: base - the node to get key and helperUrl from
   =======================================================
   -->
   <xsl:template name="portal_tool">
      <xsl:param name="base"/>
      <xsl:variable name="key" select="$base/key"/>
      <h1 class="skip">
         <xsl:value-of select="$externalized/entry[@key='sit.contentshead']"/>
      </h1>
      <a id="tocontent" class="skip" name="tocontent"></a>
      <div id="content">
      <div>
<div class="portletMainWrap">
<iframe
class ="portletMainIframe"
height="50"
width="100%"
frameborder="0"
marginwidth="0"
marginheight="0"
scrolling="auto">
<xsl:attribute name="title">
   <xsl:value-of select="$externalized/entry[@key=$key]" />
</xsl:attribute>
<xsl:attribute name="name">Main<xsl:value-of select="$base/escapedKey" /></xsl:attribute>
<xsl:attribute name="id">Main<xsl:value-of select="$base/escapedKey" /></xsl:attribute>
<xsl:attribute name="src"><xsl:value-of select="$base/helperUrl" /></xsl:attribute>
your browser doesn't support iframes
</iframe>
</div>
      </div>
   </div>
   </xsl:template>


   <!--
   =========match category that isn't categorized=====================
   process a tool category
   param:content - "true" or "false" if rendering tool content or tool list
   =================================
   -->
   <xsl:template match="category[key='org.theospi.portfolio.portal.model.ToolCategory.uncategorized']">
      <xsl:param name="content"/>
      <xsl:for-each select="pages/page" >
         <xsl:sort select="@order" data-type="number"/>
         <xsl:apply-templates select=".">
            <xsl:with-param name="content" select="$content"/>
         </xsl:apply-templates>
      </xsl:for-each>
   </xsl:template>

   <!--
   =========match category selected================
   process a tool category
   param:content - "true" or "false" if rendering tool content or tool list
   ================================================
   -->
   <xsl:template match="category[@selected='true']">
      <xsl:param name="content"/>
      <xsl:if test="$content != 'true'">
         <xsl:variable name="key" select="key"/>
         <li>
            <a accesskey="1" class="selected">
               <xsl:attribute name="href">
                  <xsl:value-of select="url"/>
               </xsl:attribute>            
               <xsl:attribute name="accesskey">
                  <xsl:value-of select="@order"/>
               </xsl:attribute>
               <xsl:attribute name="title">
                  <xsl:value-of select="$externalized/entry[@key=$key]"/>
               </xsl:attribute>
               <xsl:value-of select="$externalized/entry[@key=$key]"/>
            </a>
         </li>
      </xsl:if>
      <xsl:if test="$content='true'">
         <xsl:choose>
         <xsl:when test="pages/page[@selected='true']">
            <xsl:for-each select="pages/page" >
               <xsl:sort select="@order" data-type="number"/>
               <xsl:apply-templates select=".">
                  <xsl:with-param name="content" select="$content"/>
               </xsl:apply-templates>
            </xsl:for-each>
         </xsl:when>
            <xsl:otherwise>
               <!-- make the tool category here -->
               <!--xsl:call-template name="portal_tool">
                  <xsl:with-param name="base" select="."/>
               </xsl:call-template-->
               <xsl:call-template name="tool_category">
                  <xsl:with-param name="category" select="."/>
               </xsl:call-template>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:if>
   </xsl:template>

   <!--
   =========match category================
   process a tool category
   param:content - "true" or "false" if rendering tool content or tool list
   ================================================
   -->
   <xsl:template match="category">
      <xsl:param name="content"/>
      <xsl:if test="$content != 'true'">
         <xsl:variable name="key" select="key"/>
         <li>
            <a>
               <xsl:attribute name="href">
                  <xsl:value-of select="url"/>
               </xsl:attribute>
               <xsl:attribute name="accesskey">
                  <xsl:value-of select="@order" />
               </xsl:attribute>
               <xsl:attribute name="title">
                  <xsl:value-of select="$externalized/entry[@key=$key]"/>
               </xsl:attribute>
               <xsl:value-of select="$externalized/entry[@key=$key]"/>
            </a>
         </li>
      </xsl:if>
   </xsl:template>

   <!--
   ========name site_tabs============
   Handle putting up the site tabs
   ===============================
   -->
   <xsl:template name="site_tabs">
      <!-- site tabs here -->
      <div class="siteNavBlock">
      <table class="mast-head" height="50" cellpadding="0" cellspacing="0" border="0" width="100%">
         <tr>
            <td class="left">
               <img title="Logo" alt="Logo">
                  <xsl:attribute name="src">
                     <xsl:value-of select="config/logo"/>
                  </xsl:attribute>
               </img>
            </td>
            <td class="middle">
               <img title="Banner" alt="Banner">
                  <xsl:attribute name="src">
                     <xsl:value-of select="config/banner"/>
                  </xsl:attribute>
               </img>
            </td>
            <td class="mast-head-r right">
               <xsl:choose>
              <xsl:when test="currentUser">
                 <a target="_parent">
                    <xsl:attribute name="href">
                       <xsl:value-of select="config/logout"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                       <xsl:value-of select="$externalized/entry[@key='sit.log']"/>
                    </xsl:attribute>
                    <xsl:value-of select="$externalized/entry[@key='sit.log']"/>
                 </a>
              </xsl:when>
              <xsl:otherwise>
<form method="post" action="/osp-portal/xlogin" enctype="application/x-www-form-urlencoded" target="_parent">
   <xsl:value-of select="$externalized/entry[@key='log.userid']"/>
   <input name="eid" id="eid" type="text" style ="width: 10em" />

   <xsl:value-of select="$externalized/entry[@key='log.pass']"/>
   <input name="pw" type="password" style ="width: 10em" />
   <input name="submit" type="submit" id="submit">
      <xsl:attribute name="value">
         <xsl:value-of select="$externalized/entry[@key='log.login']"/>
      </xsl:attribute>
   </input>
<br/>
</form>
              </xsl:otherwise>
               </xsl:choose>
            </td>
         </tr>
      </table>
      <xsl:choose>
         <xsl:when test="currentUser">
      <div class="tabHolder project">
         <table border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td class="tabCell">
                  <a id="sitetabs" class="skip" name="sitetabs"></a>
                  <h1 class="skip">
                     <xsl:value-of select="$externalized/entry[@key='sit.worksiteshead']"/>
                  </h1>

                  <ul id="tabNavigation">
                     <xsl:for-each select="siteTypes/siteType">
                        <xsl:sort select="@order" data-type="number"/>
                        <xsl:apply-templates select="." >
                           <xsl:with-param name="extra" select="'false'" />
                        </xsl:apply-templates>
                     </xsl:for-each>
                     <li style="display:none;border-width:0" class="fixTabsIE"><a href="javascript:void(0);">#x20;</a></li>
                  </ul>
               </td>
            </tr>
         </table>
      <div class="divColor" id="tabBottom"><br /></div></div>
         </xsl:when>
         <xsl:otherwise>
            <table border="0" cellspacing="0" cellpadding="0">
               <tr>
                  <td>
                     <div class="divColor" id="tabBottom"><br /></div>
                  </td>
               </tr>
            </table>
         </xsl:otherwise>
      </xsl:choose>
            </div>
   </xsl:template>

   <!--
   =========match selected 1 column layous============
   process a selected page with one column layouts
   param:content - "true" or "false" if rendering tool content or tool list
   ===================================================
   -->
   <xsl:template match="page[@layout='0' and @selected='true']">
      <xsl:param name="content"/>
      <xsl:if test="$content='true'">
         <xsl:call-template name="page-content">
            <xsl:with-param name="page" select="."/>
         </xsl:call-template>
      </xsl:if>
      <xsl:if test="$content='false'">
         <li>
            <a accesskey="1" class="selected" href="#">
               <xsl:attribute name="accesskey">
                  <xsl:value-of select="../../@order"/>
               </xsl:attribute>
               <xsl:value-of select="title"/>
            </a>
         </li>
      </xsl:if>
   </xsl:template>

   <!--
   ===============match selected 2 column layous============
   process a selected page with two column layouts
   param:content - "true" or "false" if rendering tool content or tool list
   =========================================================
   -->
   <xsl:template match="page[@layout='1' and @selected='true']">
      <xsl:param name="content"/>
      <xsl:if test="$content='true'">
         <xsl:call-template name="page-content-columns">
            <xsl:with-param name="page" select="."/>
         </xsl:call-template>
      </xsl:if>
      <xsl:if test="$content='false'">
         <li>
            <a accesskey="1" class="selected" href="#">
               <xsl:attribute name="accesskey">
                  <xsl:value-of select="../../@order"/>
               </xsl:attribute>
               <xsl:value-of select="title"/>
            </a>
         </li>
      </xsl:if>
   </xsl:template>

   <!--
   ===============match page (default case)=================
   process a page
   param:content - "true" or "false" if rendering tool content or tool list
   =========================================================
   -->
   <xsl:template match="page">
      <xsl:param name="content"/>
      <xsl:if test="$content='true'">
         <!-- do nothing -->
      </xsl:if>
      <xsl:if test="$content='false'">
         <li>
            <a>
               <xsl:if test="@popUp='false'">
                  <xsl:attribute name="href">
                     <xsl:value-of select="url"/>
                  </xsl:attribute>
               </xsl:if>
               <xsl:if test="@popUp='true'">
                  <xsl:attribute name="href">#</xsl:attribute>
                  <xsl:attribute name="onclick">
                     window.open('<xsl:value-of select="popUrl"/>','<xsl:value-of select="title"/>',
                        'resize=yes,toolbar=no,scrollbars=yes, width=800,height=600')
                  </xsl:attribute>
               </xsl:if>
               <xsl:attribute name="accesskey">
                  <xsl:value-of select="../../@order" />
               </xsl:attribute>
               <xsl:value-of select="title"/>
            </a>
         </li>
      </xsl:if>
   </xsl:template>

   <!--
   ======================name page-content============
   process a page's content
   param:page - node for the current page
   ===================================================
   -->
   <xsl:template name="page-content">
      <xsl:param name="page"/>
      <h1 class="skip">
         <xsl:value-of select="$externalized/entry[@key='sit.contentshead']"/>
      </h1>
      <a id="tocontent" class="skip" name="tocontent"></a>
<div id="content">
<div>

   <xsl:for-each select="$page/columns/column[@index='0']/tools/tool">
      <xsl:call-template name="tool">
         <xsl:with-param name="tool" select="."/>
      </xsl:call-template>
   </xsl:for-each>

</div>
      </div>
   </xsl:template>

   <!--
   ================name page-content-columns================
   process a page's content
   param:page - node for the current page
   =========================================================
   -->
   <xsl:template name="page-content-columns">
      <xsl:param name="page"/>
      <h1 class="skip">
         <xsl:value-of select="$externalized/entry[@key='sit.contentshead']"/>
      </h1>
      <a id="tocontent" class="skip" name="tocontent"></a>
      <div id="content">
      <div>
         <div style="width:49%;float:left;margin:0;">
         <xsl:for-each select="$page/columns/column[@index='0']/tools/tool">
            <xsl:call-template name="tool">
               <xsl:with-param name="tool" select="."/>
            </xsl:call-template>
         </xsl:for-each>
         </div>
         <div style="width:50%;float:right">
         <xsl:for-each select="$page/columns/column[@index='1']/tools/tool">
            <xsl:call-template name="tool">
               <xsl:with-param name="tool" select="."/>
            </xsl:call-template>
         </xsl:for-each>
         </div>
      </div>
      </div>
   </xsl:template>

   <!--
   ================name tool===============================
   process a tool for displaying content
   param:tool - node for the current tool
   ========================================================
   -->
   <xsl:template name="tool">
      <xsl:param name="tool"/>

<div class="portletTitleWrap">
<iframe
	class ="portletTitleIframe"
	height="22"
	width="99%"
	frameborder="0"
	marginwidth="0"
	marginheight="0"
	scrolling="no">
   <xsl:attribute name="title">
      <xsl:value-of select="$tool/title" />
   </xsl:attribute>
   <xsl:attribute name="name">Title<xsl:value-of select="$tool/escapedId" /></xsl:attribute>
   <xsl:attribute name="id">Title<xsl:value-of select="$tool/escapedId" /></xsl:attribute>
   <xsl:attribute name="src">
      <xsl:value-of select="$tool/titleUrl" />
   </xsl:attribute>
   your browser doesn't support iframes
</iframe>
</div>
<div class="portletMainWrap">
<iframe
	class ="portletMainIframe"
	height="50"
	width="100%"
	frameborder="0"
	marginwidth="0"
	marginheight="0"
	scrolling="auto">
   <xsl:attribute name="title">
      <xsl:value-of select="$tool/title" />
   </xsl:attribute>
   <xsl:attribute name="name">Main<xsl:value-of select="$tool/escapedId" /></xsl:attribute>
   <xsl:attribute name="id">Main<xsl:value-of select="$tool/escapedId" /></xsl:attribute>
   <xsl:attribute name="src">
      <xsl:value-of select="$tool/url" />
   </xsl:attribute>
   your browser doesn't support iframes
</iframe>
</div>
   </xsl:template>

   <!--
   ======================name site_tools====================
   process the site tools list
   =============================================================
   -->
   <xsl:template name="site_tools">
<div class="divColor" id="sidebar">
	<div id="divLogo">
      <a name="logo"/>
      <xsl:if test="siteTypes/siteType[@selected='true']/sites/site[@selected='true' and @published='false']">
         <p id="siteStatus">unpublished site</p>
      </xsl:if>
      <xsl:if test="siteTypes/siteType[@selected='true']/key = 'project'">
         <p id="siteType">project</p>
      </xsl:if>
	</div>
	<a id="toolmenu" class="skip" name="toolmenu"></a>
	<h1 class="skip">
      <xsl:value-of select="$externalized/entry[@key='sit.toolshead']"/>
	</h1>

	<div id="leftnavlozenge">
		<ul>

<xsl:for-each select="categories/category" >
   <xsl:sort select="@order" data-type="number"/>
   <xsl:apply-templates select=".">
      <xsl:with-param name="content" select="'false'"/>
   </xsl:apply-templates>
</xsl:for-each>

			<li>
				<a  accesskey="h" href="javascript:;">
               <xsl:attribute name="onclick">
                  window.open('<xsl:value-of select="config/helpUrl"/>','Help','resize=yes,toolbar=no,scrollbars=yes, width=800,height=600')
               </xsl:attribute>
               <xsl:attribute name="onkeypress">
                  window.open('<xsl:value-of select="config/helpUrl"/>','Help','resize=yes,toolbar=no,scrollbars=yes, width=800,height=600')
               </xsl:attribute>
               Help</a>
			</li>
		</ul>
	</div>

   <xsl:if test="$config/presence[@include='true']">
      <xsl:call-template name="presence" />
   </xsl:if>

</div>
   </xsl:template>

   <!--
   ===============name footer==========================
   process the main portal footer
   ========================================================
   -->
   <xsl:template name="footer">
<div align="center" id="footer">
	<div class="footerExtNav" align="center">
   <xsl:for-each select="config/bottomNavs/bottomNav">
      <xsl:value-of select="." disable-output-escaping="yes"/>
      <xsl:if test="last() != position()">
         <xsl:value-of select="' | '" />
      </xsl:if>
   </xsl:for-each>
	</div>

	<div id="footerInfo">
      <span class="skip">
         <xsl:value-of select="$externalized/entry[@key='site.newwindow']"/>
      </span>
      <xsl:for-each select="config/poweredBy">
         <a href="http://sakaiproject.org" target="_blank">
            <img border="0" src="/library/image/sakai_powered.gif" alt="Powered by Sakai" />
         </a>
      </xsl:for-each>

      <br />
      <span class="sakaiCopyrightInfo"><xsl:value-of select="config/copyright"/><br />
         <xsl:value-of select="config/service"/> - <xsl:value-of select="config/serviceVersion"/> - Sakai <xsl:value-of
            select="config/sakaiVersion"/> - Server "<xsl:value-of select="config/server"/>"
      </span>
	</div>
</div>      
   </xsl:template>

   <!--
   ===============match siteType selected===============
   process the siteType element
   param:extra - if this is running during the "more" list
   ============================================================
   -->
   <xsl:template match="siteType[@userSite='true' and @selected='true']">
      <xsl:param name="extra"/>
      <xsl:variable name="key" select="key"/>
      <li class="selectedTab">
         <a>
            <xsl:attribute name="href">
               <xsl:value-of select="sites/site/url"/>
            </xsl:attribute>
            <xsl:attribute name="title">
               <xsl:value-of select="$externalized/entry[@key=$key]"/>
            </xsl:attribute>
            <xsl:value-of select="$externalized/entry[@key=$key]"/>
         </a>
      </li>
   </xsl:template>

   <!--
   ===============match siteType selected===============
   process the siteType element
   param:extra - if this is running during the "more" list
   ============================================================
   -->
   <xsl:template match="siteType[@userSite='true' and @selected='false']">
      <xsl:param name="extra"/>
      <xsl:variable name="key" select="key"/>
      <li>
         <a target="_parent">
            <xsl:attribute name="href">
               <xsl:value-of select="sites/site/url"/>
            </xsl:attribute>
            <xsl:attribute name="title">
               <xsl:value-of select="$externalized/entry[@key=$key]"/>
            </xsl:attribute>
            <xsl:value-of select="$externalized/entry[@key=$key]"/>
         </a>
      </li>
   </xsl:template>
   
   <!--
   ===============match siteType selected===============
   process the siteType element
   param:extra - if this is running during the "more" list
   ============================================================
   -->
   <xsl:template match="siteType[@userSite='false' and @selected='true']">
      <xsl:param name="extra"/>
      <xsl:variable name="key" select="key"/>
      <li class="selectedTab">
         <a>
            <xsl:attribute name="href">
               <xsl:value-of select="url"/>
            </xsl:attribute>
            <xsl:attribute name="title">
               <xsl:value-of select="$externalized/entry[@key=$key]"/>
            </xsl:attribute>
            <xsl:value-of select="$externalized/entry[@key=$key]"/>
         </a>
      </li>
   </xsl:template>

   <!--
   ===============match siteType===============
   process the siteType element
   param:extra - if this is running during the "more" list
   ============================================================
   -->
   <xsl:template match="siteType">
      <xsl:param name="extra"/>
      <xsl:variable name="key" select="key"/>
      <li>
         <a target="_parent">
            <xsl:attribute name="href">
               <xsl:value-of select="url"/>
            </xsl:attribute>
            <xsl:attribute name="title">
               <xsl:value-of select="$externalized/entry[@key=$key]"/>
            </xsl:attribute>
            <xsl:value-of select="$externalized/entry[@key=$key]"/>
         </a>
      </li>
   </xsl:template>

   <!--
   ===============match site marked as an extra site===============
   process an extra site for navigation
   param:extra - if this is running during the "more" list
   =====================================================================
   -->
   <!--xsl:template match="site[@extra='true']">
      <xsl:param name="extra"/>
      <xsl:if test="$extra='true'">
         <option>
            <xsl:attribute name="title">
               <xsl:value-of select="title"/>
            </xsl:attribute>
            <xsl:attribute name="value">
               <xsl:value-of select="url"/>
            </xsl:attribute>
            <xsl:value-of select="title"/>
         </option>
      </xsl:if>
   </xsl:template-->

   <!--
   ===============match site that has been selected===============
   process a selected site for navigation
   param:extra - if this is running during the "more" list
   ===================================================================
   -->
   <!--xsl:template match="site[@selected='true']">
      <xsl:param name="extra"/>
      <xsl:if test="$extra='false'">
         <td>
            <a href="#">
               <xsl:value-of select="title"/>
            </a>
         </td>
      </xsl:if>
   </xsl:template-->

   <!--
   ===============match site (default case)===============
   process a selected site for navigation
   param:extra - if this is running during the "more" list
   ============================================================
   -->
   <!--xsl:template match="site">
      <xsl:param name="extra"/>
      <xsl:if test="$extra='false'">
         <td>
            <a target="_parent">
               <xsl:attribute name="href">
                  <xsl:value-of select="url"/>
               </xsl:attribute>
               <xsl:attribute name="title">
                  <xsl:value-of select="title"/>
               </xsl:attribute>
               <xsl:value-of select="title"/>
            </a>
         </td>
      </xsl:if>
   </xsl:template-->

   <!--
   ======-name presence===========================
   process the presence area
   ====================================================
   -->
   <xsl:template name="presence">
      <div class="sideBarText"  id="pres_title">
         <xsl:value-of select="$externalized/entry[@key='sit.presencetitle']"/>
      </div>
      <iframe
         name="presence"
         id="presence"
         title="Users Present in Site"
         frameborder="0"
         marginwidth="0"
         marginheight="0"
         scrolling="auto">
         <xsl:attribute name="src">
            <xsl:value-of select="$config/presence"/>
         </xsl:attribute>
         Your browser doesn't support frames
      </iframe>
   </xsl:template>

   <!--
   =================name breadcrumbs==============
   breadcumb processing
   ===============================================
   -->
   <xsl:template name="breadcrumbs">
      <xsl:variable name="siteTypeKey" select="siteTypes/siteType[@selected='true']/key"/>
      <xsl:variable name="toolCategoryKey" select="categories/category[@selected='true']/key"/>

<div class="breadcrumbHolder workspace">
   <table border="0">
      <tr><td>
         <ul id="breadcrumbNavigation">
            <!--Active link/breadcrum li gets the class selectedCrumb-->
            <xsl:if test="siteTypes/siteType[@selected='true']">
               <xsl:if test="siteTypes/siteType[@selected='true' and key!='org.theospi.portfolio.portal.myWorkspace']">
                  <xsl:call-template name="breadcrumb_entry">
                     <xsl:with-param name="node" select="siteTypes/siteType[@selected='true']"/>
                     <xsl:with-param name="title" select="$externalized/entry[@key=$siteTypeKey]"/>
                     <xsl:with-param name="last" select="count(siteTypes/siteType/sites/site[@selected='true']) = 0"/>
                  </xsl:call-template>
               </xsl:if>
               <xsl:if test="siteTypes/siteType/sites/site[@selected='true']">
                  <xsl:call-template name="breadcrumb_entry">
                     <xsl:with-param name="node" select="siteTypes/siteType/sites/site[@selected='true']"/>
                     <xsl:with-param name="title" select="siteTypes/siteType/sites/site[@selected='true']/title"/>
                  </xsl:call-template>
                  <xsl:if test="categories/category[@selected='true']">
                     <xsl:if test="categories/category[key!='org.theospi.portfolio.portal.model.ToolCategory.uncategorized']">
                        <xsl:call-template name="breadcrumb_entry">
                           <xsl:with-param name="node" select="categories/category[@selected='true']"/>
                           <xsl:with-param name="title" select="$externalized/entry[@key=$toolCategoryKey]"/>
                           <xsl:with-param name="last" select="count(categories/category/pages/page[@selected='true']) = 0"/>
                        </xsl:call-template>
                     </xsl:if>
                  </xsl:if>
                  <xsl:if test="categories/category/pages/page[@selected='true']">
                     <xsl:call-template name="breadcrumb_entry">
                        <xsl:with-param name="node" select="categories/category/pages/page[@selected='true']"/>
                        <xsl:with-param name="title" select="categories/category/pages/page[@selected='true']/title"/>
                        <xsl:with-param name="last" select="'true'"/>
                     </xsl:call-template>
                  </xsl:if>
               </xsl:if>
            </xsl:if>
         </ul>
      </td></tr>
   </table>
</div>
   </xsl:template>

   <!--
   =================name breadcrumb_entry==============
   breadcumb processing
   ===============================================
   -->
   <xsl:template name="breadcrumb_entry">
      <xsl:param name="node"/>
      <xsl:param name="title"/>
      <xsl:param name="last"/>
      <li>
         <xsl:if test="$last = 'true'">
            <xsl:attribute name="class">selectedCrumb</xsl:attribute>
         </xsl:if>
         <span class="breadcrumbNavigation">
            <a>
               <xsl:attribute name="href">
                  <xsl:value-of select="$node/url"/>
               </xsl:attribute>
               <xsl:attribute name="title">
                  <xsl:value-of select="$title"/>
               </xsl:attribute>
               <xsl:value-of select="$title"/>
            </a>
            <xsl:if test="$last != 'true'">
               &gt;
            </xsl:if>
         </span>
      </li>
   </xsl:template>

   <!--
   ====================================================
   -->
   <xsl:template name="tool_category">
      <xsl:param name="category"/>
      <xsl:variable name="layoutFile" select="$category/layoutFile"/>
      <xsl:variable name="layout" select="document($layoutFile)"/>

      <h1 class="skip">
         <xsl:value-of select="$externalized/entry[@key='sit.contentshead']"/>
      </h1>
      <a id="tocontent" class="skip" name="tocontent"></a>
      <div id="content">
      <div>
<div class="portletMainWrap">
<div class="portletBody">
      <xsl:apply-templates select="$layout/*">
         <xsl:with-param name="category" select="$category" />
      </xsl:apply-templates>
</div>
</div>
      </div>
      </div>
   </xsl:template>

   <xsl:template match="osp:tool">
      <xsl:param name="category" />
      <xsl:variable name="currentToolId" select="@id" />
      <xsl:if test="$category/pages/page[@toolId=$currentToolId]">
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$category/pages/page[@toolId=$currentToolId]" />
            <xsl:with-param name="category" select="$category" />
         </xsl:apply-templates>
      </xsl:if>
   </xsl:template>

   <xsl:template match="osp:toolLink">
      <xsl:param name="category" />
      <xsl:param name="currentTool" />
      <a target="_parent">
         <xsl:attribute name="href">
            <xsl:value-of select="$currentTool/url"/>
         </xsl:attribute>
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$currentTool" />
            <xsl:with-param name="category" select="$category" />
         </xsl:apply-templates>
      </a>
   </xsl:template>

   <xsl:template match="osp:site_role">
      <xsl:param name="category" />
      <xsl:param name="currentTool" />
      <xsl:variable name="roleId" select="@role"/>
      <xsl:comment>
         got a role section:
         <xsl:value-of select="$roleId"/>
      </xsl:comment>
      <xsl:if test="$roles/role[@id=$roleId]">
         <xsl:comment>
            matched a role:
            <xsl:value-of select="$roleId"/>
         </xsl:comment>
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$currentTool" />
            <xsl:with-param name="category" select="$category" />
         </xsl:apply-templates>
      </xsl:if>
   </xsl:template>

   <!-- Identity transformation -->
   <xsl:template match="@*|*">
      <xsl:param name="currentTool" />
      <xsl:param name="category" />
      <xsl:if test="count($category) > 0">
         <xsl:copy>
            <xsl:apply-templates select="@*|node()" >
               <xsl:with-param name="currentTool" select="$currentTool" />
               <xsl:with-param name="category" select="$category" />
            </xsl:apply-templates>
         </xsl:copy>
      </xsl:if>
   </xsl:template>

</xsl:stylesheet>
