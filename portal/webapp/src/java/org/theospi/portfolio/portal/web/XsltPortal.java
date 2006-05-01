/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.portal.web;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.api.kernel.tool.ToolException;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.portal.charon.CharonPortal;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.cover.PreferencesService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.java.ResourceLoader;
import org.sakaiproject.util.web.Web;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.portal.model.SitePageWrapper;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 9, 2006
 * Time: 2:21:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class XsltPortal extends CharonPortal {

   private PortalManager portalManager;
   private DocumentBuilder documentBuilder;
   private Templates templates;
   private Templates toolCategoryTemplates;
   private URIResolver servletResolver;

   /** messages. */
   private static ResourceLoader rb = new ResourceLoader("org/theospi/portfolio/portal/messages");

   /** base messages. */
   private static ResourceLoader rbsitenav = new ResourceLoader("sitenav");

   private static final String TOOL_CATEGORY = "category";
   private static final String TOOL_CATEGORY_HELPER = "category_helper";
   private static final String SITE_TYPE = "site_type";
   private static final String SITE_TYPE_HELPER = "site_type_helper";

   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      // get the Sakai session
      Session session = SessionManager.getCurrentSession();

      // recognize what to do from the path
      String option = req.getPathInfo();

      // get the parts (the first will be "")
      String[] parts = option.split("/");

      if (parts.length < 2) {
         super.doPost(req, res);
         return;
      }

      if (parts[1].equals(SITE_TYPE_HELPER)) {
         // Resolve the site_type of the form /portal/site_type_helper/project
         String siteTypeKey = parts[2];
         doSiteTypeHelper(req, res, session, siteTypeKey, parts.length == 4);
      }
      else {
         super.doPost(req, res);
      }
   }


   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      // get the Sakai session
      Session session = SessionManager.getCurrentSession();

      // recognize what to do from the path
      String option = req.getPathInfo();

      // get the parts (the first will be "")
      String[] parts = option.split("/");

      if (parts.length < 2) {
         super.doGet(req, res);
         return;
      }

      if (parts[1].equals(SITE_TYPE)) {
         // Resolve the site_type of the form /portal/site_type/project
         String siteTypeKey = parts[2];
         doSiteType(req, res, session, siteTypeKey);
      }
      else if (parts[1].equals(SITE_TYPE_HELPER)) {
         // Resolve the site_type of the form /portal/site_type_helper/project
         String siteTypeKey = parts[2];
         doSiteTypeHelper(req, res, session, siteTypeKey, parts.length == 4);
      }
      else if (parts[1].equals(TOOL_CATEGORY)) {
         // Resolve the site_type of the form /portal/category/<siteId>/<categoryKey>/<optionalToolId>
         String siteId = parts[2];
         String categoryKey = parts[3];
         String pageId = null;
         if (parts.length > 4) {
            pageId = parts[4];
         }
         doCategory(req, res, session, siteId, categoryKey, pageId);
      }
      else if (parts[1].equals(TOOL_CATEGORY_HELPER)) {
         // Resolve the site_type of the form /portal/category_helper/<siteId>/<categoryKey>
         String siteId = parts[2];
         String categoryKey = parts[3];
         doCategoryHelper(req, res, session, siteId, categoryKey, parts.length > 4);
      }
      else {
         super.doGet(req, res);
      }
   }

   protected void doCategoryHelper(HttpServletRequest req, HttpServletResponse res, Session session,
                                   String siteId, String categoryKey, boolean returning) throws ToolException, IOException {
      if (session.getUserId() == null) {
         doLogin(req, res, session, req.getPathInfo(), false);
         return;
      }

      Site site = getPortalManager().getSite(siteId);
      String siteTypeKey = getPortalManager().decorateSiteType(site);
      SiteType siteType = getPortalManager().getSiteType(siteTypeKey);

      Document doc = getDocumentBuilder().newDocument();
      Element root = doc.createElement("toolCategory");
      ToolCategory category = getPortalManager().getToolCategory(siteType.getKey(), categoryKey);

      Map categoryPageMap = getPortalManager().getPagesByCategory(siteId);
      List categoryPages = (List) categoryPageMap.get(category);
      root.appendChild(createCategoryXml(doc, category, categoryPages, siteId, categoryKey, null));

      Element rolesElement = createRolesXml(doc, siteId);
      root.appendChild(rolesElement);

      doc.appendChild(root);
      outputDocument(req, res, session, doc, getToolCategoryTransformer());

      /*
      ToolSession toolSession = session.getToolSession(Web.escapeJavascript(categoryKey));

      if (!returning) {
         toolSession.setAttribute(PortalManager.SITE_TYPE, siteTypeKey);
         toolSession.setAttribute(PortalManager.SITE_ID, siteId);
         toolSession.setAttribute(PortalManager.TOOL_CATEGORY, categoryKey);
         toolSession.setAttribute(PortalManager.CONTEXT, getContext());
      }

      // put the session in the request attribute
      req.setAttribute(Tool.TOOL_SESSION, toolSession);

      // set as the current tool session
      SessionManager.setCurrentToolSession(toolSession);

      // put the placement id in the request attribute
      String placementId = Web.escapeJavascript(categoryKey);
      req.setAttribute(Tool.PLACEMENT_ID, placementId);

      ActiveTool helperTool = ActiveToolManager.getActiveTool("osp.tool.category");
      Placement placement = new org.sakaiproject.util.Placement(placementId, helperTool, null, null, null);

      String context = req.getPathInfo();
      forwardTool(helperTool, req, res, placement, siteType.getSkin(), getContext() + "/" + context, "/toolCategory");
      */
   }

   protected Element createRolesXml(Document doc, String siteId) {
      Element rolesElement = doc.createElement("roles");
      List roles = getPortalManager().getRoles(siteId);
      for (Iterator i=roles.iterator();i.hasNext();) {
         Role role = (Role) i.next();
         if (role != null) {
            Element roleElement = doc.createElement("role");
            roleElement.setAttribute("id", role.getId());
            rolesElement.appendChild(roleElement);
         }
      }
      return rolesElement;
   }

   protected void doSiteTypeHelper(HttpServletRequest req, HttpServletResponse res,
                                   Session session, String siteTypeKey, boolean returning) throws ToolException {
      if (session.getUserId() == null) {
         doLogin(req, res, session, req.getPathInfo(), false);
         return;
      }

      SiteType siteType = getPortalManager().getSiteType(siteTypeKey);

      ToolSession toolSession = session.getToolSession(Web.escapeJavascript(siteTypeKey));

      if (!returning) {
         toolSession.setAttribute(PortalManager.SITE_TYPE, siteTypeKey);
      }

      // put the session in the request attribute
      req.setAttribute(Tool.TOOL_SESSION, toolSession);

      // set as the current tool session
      SessionManager.setCurrentToolSession(toolSession);

      // put the placement id in the request attribute
      String placementId = Web.escapeJavascript(siteTypeKey);
      req.setAttribute(Tool.PLACEMENT_ID, placementId);

      ActiveTool helperTool = ActiveToolManager.getActiveTool("osp.site.type");
      Placement placement = new org.sakaiproject.util.Placement(placementId, helperTool, null, null, null);

      String context = req.getPathInfo();
      forwardTool(helperTool, req, res, placement, siteType.getSkin(), getContext() + "/" + context, "/siteType");
   }

   protected void doCategory(HttpServletRequest req, HttpServletResponse res, Session session,
                             String siteId, String categoryKey, String pageId) throws IOException, ToolException {
      siteId = checkVisitSite(siteId, session, req, res);
      if (siteId == null) {
         return;
      }
      Document doc = createPortalDocument(null, siteId, categoryKey, pageId);
      outputDocument(req, res, session, doc);
   }

   protected void doSiteType(HttpServletRequest req, HttpServletResponse res, Session session,
                             String siteTypeKey) throws IOException, ToolException  {
      if (session.getUserId() == null) {
         doLogin(req, res, session, req.getPathInfo(), false);
         return;
      }
      Document doc = createPortalDocument(siteTypeKey, null, null, null);
      outputDocument(req, res, session, doc);
   }

   protected void outputDocument(HttpServletRequest req, HttpServletResponse res,
                                 Session session, Document doc) throws IOException {
      outputDocument(req, res, session, doc, getTransformer());
   }


   protected void outputDocument(HttpServletRequest req, HttpServletResponse res,
                                    Session session, Document doc, Transformer transformer) throws IOException {

      res.setContentType("text/html; charset=UTF-8");
      res.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      res.addDateHeader("Last-Modified", System.currentTimeMillis());
      res.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      res.addHeader("Pragma", "no-cache");

      PrintWriter out = res.getWriter();

      try {
         StreamResult outputTarget = new StreamResult(out);
         transformer.transform(new DOMSource(doc), outputTarget);
      }
      catch (TransformerException e) {
         throw new RuntimeException(e);
      }
   }

   protected void doSite(HttpServletRequest req, HttpServletResponse res, Session session,
                         String siteId, String pageId, String toolContextPath) throws IOException, ToolException {
      siteId = checkVisitSite(siteId, session, req, res);

      if (siteId == null) {
         return;
      }

      Site site = getPortalManager().getSite(siteId);

      Document doc = createPortalDocument(null, siteId, null, pageId);
      outputDocument(req, res, session, doc);
   }

   protected void doWorksite(HttpServletRequest req, HttpServletResponse res, Session session,
                             String siteId, String pageId, String toolContextPath) throws IOException, ToolException {
      siteId = checkVisitSite(siteId, session, req, res);
      if (siteId == null) {
         return;
      }
      Document doc = createPortalDocument(null, siteId, null, pageId);
      outputDocument(req, res, session, doc);
   }

   protected void doPage(HttpServletRequest req, HttpServletResponse res, Session session,
                         String pageId, String toolContextPath) throws IOException, ToolException {
      SitePage page = getPortalManager().getSitePage(pageId);
      if (page == null) {
         doError(req, res, session, ERROR_WORKSITE);
         return;
      }
      String siteId = page.getSiteId();
      siteId = checkVisitSite(siteId, session, req, res);
      if (siteId == null) {
         return;
      }
      Document doc = createPortalDocument(null, siteId, getPortalManager().getPageCategory(siteId, pageId), pageId);
      outputDocument(req, res, session, doc);
   }

   protected void postLogin(HttpServletRequest req, HttpServletResponse res, Session session, String loginPath) throws ToolException {
      session.setAttribute(Tool.HELPER_DONE_URL, Web.returnUrl(req, null));
      super.postLogin(req, res, session, loginPath);
   }

   protected Document createPortalDocument(String siteTypeKey, String siteId, String toolCategoryKey, String pageId) {
      Document doc = getDocumentBuilder().newDocument();

      Element root = doc.createElement("portal");
      doc.appendChild(root);

      User currentUser = getPortalManager().getCurrentUser();
      boolean loggedIn = false;
      if (currentUser != null && currentUser.getId().length() > 0) {
         root.appendChild(createUserXml(doc, currentUser));
         loggedIn = true;
      }

      Map siteTypesMap = getPortalManager().getSitesByType(siteId);
      Site site = null;
      SitePage page = null;

      if (siteId != null) {
         site = getPortalManager().getSite(siteId);
      }

      if (pageId != null) {
         page = getPortalManager().getSitePage(pageId);
      }

      if (siteTypeKey == null) {
         siteTypeKey = getPortalManager().decorateSiteType(site);
      }

      SiteType siteType = findSiteType(siteTypesMap, siteTypeKey);

      root.appendChild(createTitleXml(doc, site, page));

      List skins = getSkins(siteType, site);
      root.appendChild(createSkinsXml(doc, skins));
      root.appendChild(createConfixXml(doc, skins, site, loggedIn));
      root.appendChild(createSiteTypesXml(doc, siteTypesMap, siteTypeKey, siteId));

      if (siteId != null) {
         Map pageCateogries = getPortalManager().getPagesByCategory(siteId);

         if (pageId == null && toolCategoryKey == null) {
            // need to pick first page or category
            ToolCategory category = findFirstCategory(pageCateogries);
            if (category.getKey().equals(ToolCategory.UNCATEGORIZED_KEY)) {
               List pages = (List) pageCateogries.get(category);
               pageId = findFirstPage(pages);
            }
            else {
               toolCategoryKey = category.getKey();
            }
         }

         root.appendChild(createPageCategoriesXml(doc, pageCateogries, siteId, toolCategoryKey, pageId));
      }

      if (siteId != null) {
         Element rolesElement = createRolesXml(doc, siteId);
         root.appendChild(rolesElement);
      }

      root.appendChild(createExternalizedXml(doc));

      return doc;
   }

   protected String findFirstPage(List pages) {
      SitePageWrapper first = null;
      for (Iterator i=pages.iterator();i.hasNext();) {
         SitePageWrapper wrapper = (SitePageWrapper) i.next();
         if (first == null || first.getOrder() > wrapper.getOrder()) {
            first = wrapper;
         }
      }
      return first.getPage().getId();
   }

   protected ToolCategory findFirstCategory(Map pageCateogries) {
      ToolCategory first = null;

      for (Iterator i=pageCateogries.keySet().iterator();i.hasNext();) {
         ToolCategory category = (ToolCategory) i.next();
         if (first == null || first.getOrder() > category.getOrder()) {
            first = category;
         }
      }

      return first;
   }

   protected SiteType findSiteType(Map siteTypesMap, String siteTypeKey) {
      for (Iterator i=siteTypesMap.keySet().iterator();i.hasNext();) {
         SiteType siteType = (SiteType) i.next();
         if (siteType.getKey().equals(siteTypeKey)) {
            return siteType;
         }
      }

      return null;
   }

   protected Element createExternalizedXml(Document doc) {
      Element externalized = doc.createElement("externalized");

      for (Iterator i=rb.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         externalized.appendChild(createExternalizedEntryXml(doc, entry.getKey(), entry.getValue()));
      }

      for (Iterator i=rbsitenav.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         externalized.appendChild(createExternalizedEntryXml(doc, entry.getKey(), entry.getValue()));
      }

      return externalized;
   }

   protected Element createExternalizedEntryXml(Document doc, Object key, Object value) {
      Element entry = doc.createElement("entry");
      entry.setAttribute("key", (String) key);

      appendTextElementNode(doc, "value", (String) value, entry);

      return entry;
   }

   protected Element createTitleXml(Document doc, Site site, SitePage page) {
      Element titleElement = doc.createElement("pageTitle");
      String title = ServerConfigurationService.getString("ui.service");

      if (site != null) {
         title += ":" + site.getTitle();
      }

      if (page != null) {
         title += ":" + page.getTitle();
      }
      safeAppendTextNode(doc, titleElement, title, true);

      return titleElement;
   }

   protected Element createConfixXml(Document doc, List skins, Site site, boolean loggedIn) {
      Element config = doc.createElement("config");

      String skinRepo = ServerConfigurationService.getString("skin.repo");
      if (skins.size() > 0) {
         skinRepo += "/" + skins.get(skins.size() - 1);
      }
      else {
         skinRepo += "/" + ServerConfigurationService.getString("skin.default");
      }

      if (site != null) {
         String presenceUrl = getContext() + "/presence/" + site.getId();
         boolean showPresence = ServerConfigurationService.getBoolean("display.users.present", true);
         Element presence = doc.createElement("presence");
         safeAppendTextNode(doc, presence, presenceUrl, true);
         presence.setAttribute("include", new Boolean(showPresence && loggedIn).toString());
         config.appendChild(presence);
      }

      Element logo = doc.createElement("logo");
      safeAppendTextNode(doc, logo, skinRepo + "/images/logo_inst.gif", true);
      Element banner = doc.createElement("banner");
      safeAppendTextNode(doc, banner, skinRepo + "/images/banner_inst.gif", true);
      Element logout = doc.createElement("logout");
      safeAppendTextNode(doc, logout, getContext() + "/logout", true);

      String copyright = ServerConfigurationService.getString("bottom.copyrighttext");
      String service = ServerConfigurationService.getString("ui.service", "Sakai");
      String serviceVersion = ServerConfigurationService.getString("version.service", "?");
      String sakaiVersion = ServerConfigurationService.getString("version.sakai", "?");
      String server = ServerConfigurationService.getServerId();
      String helpUrl = ServerConfigurationService.getHelpUrl(null);
      String[] bottomNav = ServerConfigurationService.getStrings("bottomnav");
      String[] poweredByUrl = ServerConfigurationService.getStrings("powered.url");
      String[] poweredByImage = ServerConfigurationService.getStrings("powered.img");
      String[] poweredByAltText = ServerConfigurationService.getStrings("powered.alt");

      config.appendChild(logo);
      config.appendChild(banner);
      config.appendChild(logout);

      appendTextElementNode(doc, "copyright", copyright, config);
      appendTextElementNode(doc, "service", service, config);
      appendTextElementNode(doc, "serviceVersion", serviceVersion, config);
      appendTextElementNode(doc, "sakaiVersion", sakaiVersion, config);
      appendTextElementNode(doc, "server", server, config);
      appendTextElementNode(doc, "helpUrl", helpUrl, config);
      appendTextElementNodes(doc, bottomNav, config, "bottomNavs", "bottomNav");

      if ((poweredByUrl != null) && (poweredByImage != null) && (poweredByAltText != null)
            && (poweredByUrl.length == poweredByImage.length) && (poweredByUrl.length == poweredByAltText.length)) {
         for (int i = 0; i < poweredByUrl.length; i++) {
            config.appendChild(createPoweredByXml(doc, poweredByAltText[i], poweredByImage[i], poweredByUrl[i]));
         }
      }
      else {
         config.appendChild(createPoweredByXml(doc, "Powered by Sakai",
            "/library/image/sakai_powered.gif", "http://sakaiproject.org"));
      }

      appendTextElementNodes(doc, poweredByUrl, config, "poweredByUrls", "poweredByUrl");
      appendTextElementNodes(doc, poweredByImage, config, "poweredByImages", "poweredByImage");
      appendTextElementNodes(doc, poweredByAltText, config, "poweredByAltTexts", "poweredByAltText");

      return config;
   }

   protected Element createPoweredByXml(Document doc, String text, String image, String url) {
      Element poweredBy = doc.createElement("poweredBy");

      appendTextElementNode(doc, "text", text, poweredBy);
      appendTextElementNode(doc, "image", image, poweredBy);
      appendTextElementNode(doc, "url", url, poweredBy);

      return poweredBy;
   }

   protected void appendTextElementNodes(Document doc, String[] strings, Element parent,
                                         String topNodeName, String nodeName) {
      Element topNode = doc.createElement(topNodeName);

      if (strings == null) {
         return;
      }

      for (int i=0;i<strings.length;i++) {
         appendTextElementNode(doc, nodeName, strings[i], topNode);
      }

      parent.appendChild(topNode);
   }

   protected void appendTextElementNode(Document doc, String name, String text, Element parent) {
      Element element = doc.createElement(name);
      safeAppendTextNode(doc, element, text, true);
      parent.appendChild(element);
   }

   protected Element createPageCategoriesXml(Document doc, Map pages, String siteId, String toolCategoryKey, String pageId) {
      Element pagesElement = doc.createElement("categories");

      for (Iterator i=pages.keySet().iterator();i.hasNext();) {
         ToolCategory category = (ToolCategory) i.next();
         List categoryPageList = (List) pages.get(category);
         pagesElement.appendChild(createCategoryXml(doc, category, categoryPageList, siteId, toolCategoryKey, pageId));
      }
      return pagesElement;
   }

   protected Element createCategoryXml(Document doc, ToolCategory category, List categoryPageList,
                                       String siteId, String categoryKey, String pageId) {
      Element categoryElement = doc.createElement("category");
      boolean selected = category.getKey().equals(categoryKey);
      categoryElement.setAttribute("selected", new Boolean(selected).toString());
      categoryElement.setAttribute("order", new Integer(category.getOrder()).toString());
      Element categoryKeyElement = doc.createElement("key");
      safeAppendTextNode(doc, categoryKeyElement, category.getKey(), false);
      Element categoryEscapedKeyElement = doc.createElement("escapedKey");
      safeAppendTextNode(doc, categoryEscapedKeyElement, Web.escapeJavascript(category.getKey()), false);
      Element categoryUrlElement = doc.createElement("url");
      // Resolve the site_type of the form /portal/category/<siteId>/<categoryKey>/<optionalToolId>
      safeAppendTextNode(doc, categoryUrlElement,
            getContext() + "/" + TOOL_CATEGORY + "/" + siteId + "/" + category.getKey(), true);
      Element categoryHelperUrlElement = doc.createElement("helperUrl");
      // Resolve the site_type of the form /portal/category_helper/<siteId>/<categoryKey>/<optionalToolId>
      safeAppendTextNode(doc, categoryHelperUrlElement,
            getContext() + "/" + TOOL_CATEGORY_HELPER + "/" + siteId + "/" + category.getKey(), true);

      categoryElement.appendChild(categoryKeyElement);
      categoryElement.appendChild(categoryEscapedKeyElement);
      categoryElement.appendChild(categoryUrlElement);
      categoryElement.appendChild(categoryHelperUrlElement);
      appendTextElementNode(doc, "layoutFile", category.getHomePagePath(), categoryElement);

      Element pagesElement = doc.createElement("pages");

      for (Iterator i=categoryPageList.iterator();i.hasNext();) {
         SitePageWrapper page = (SitePageWrapper) i.next();
         pagesElement.appendChild(createPageXml(doc, page.getOrder(), siteId, page.getPage(), category.getKey(), pageId));
      }

      categoryElement.appendChild(pagesElement);
      return categoryElement;
   }

   protected Element createPageXml(Document doc, int index, String siteId, SitePage page,
                                   String parentCategoryKey, String pageId) {
      Element pageElement = doc.createElement("page");
      pageElement.setAttribute("order", new Integer(index).toString());
      boolean pageSelected = page.getId().equals(pageId);
      pageElement.setAttribute("selected", new Boolean(pageSelected).toString());
      pageElement.setAttribute("layout", new Integer(page.getLayout()).toString());
      pageElement.setAttribute("popUp", new Boolean(page.isPopUp()).toString());
      pageElement.setAttribute("toolId", getFirstToolId(page));
      Element pageName = doc.createElement("title");
      safeAppendTextNode(doc, pageName, page.getTitle(), true);
      // portal/site/9607661f-f3aa-4938-8005-c3ffaa228c6c/page/0307f10c-225b-4db8-803e-b12f24e38544

      Element pageUrl = doc.createElement("url");
      if (parentCategoryKey != null && !parentCategoryKey.equals(ToolCategory.UNCATEGORIZED_KEY)) {
         safeAppendTextNode(doc, pageUrl, getContext() + "/category/" +
               siteId + "/"  + parentCategoryKey + "/" + page.getId(), true);
      }
      else {
         safeAppendTextNode(doc, pageUrl, getContext() + "/site/" + siteId + "/page/" + page.getId(), true);
      }

      Element popPageUrl = doc.createElement("popUrl");
      safeAppendTextNode(doc, popPageUrl, getContext() + "/page/" + page.getId(), true);

      Element columns = doc.createElement("columns");

      for (int i=0;i<2;i++) {
         Element column = doc.createElement("column");
         column.setAttribute("index", new Integer(i).toString());
         column.appendChild(createColumnToolsXml(doc, page.getTools(i), page));
         columns.appendChild(column);
      }

      pageElement.appendChild(pageName);
      pageElement.appendChild(pageUrl);
      pageElement.appendChild(popPageUrl);
      pageElement.appendChild(columns);

      return pageElement;
   }

   protected String getFirstToolId(SitePage page) {
      List tools = page.getTools();

      if (tools.size() > 0) {
         Placement placement = (Placement) tools.get(0);
         return placement.getTool().getId();
      }

      return "";
   }

   protected Element createColumnToolsXml(Document doc, List tools, SitePage page) {
      Element toolsElement = doc.createElement("tools");

      for (Iterator i=tools.iterator();i.hasNext();) {
         Placement placement = (Placement) i.next();
         toolsElement.appendChild(createToolXml(doc, placement, page));
      }

      return toolsElement;
   }

   protected Element createToolXml(Document doc, Placement placement, SitePage page) {
      Element toolElement = doc.createElement("tool");

      Element title = doc.createElement("title");
      safeAppendTextNode(doc, title, page.getTitle(), true);

      Element escapedId = doc.createElement("escapedId");
      String id = Web.escapeJavascript("i" + placement.getId());

      id = id.substring(1);

      safeAppendTextNode(doc, escapedId, id, true);

      //portal/tool/ad222467-e186-4cca-80e9-d12a9d6db392?panel=Main
      Element toolUrl = doc.createElement("url");
      safeAppendTextNode(doc, toolUrl, getContext() + "/tool/" + placement.getId() + "?panel=Main", true);

      //portal/title/ad222467-e186-4cca-80e9-d12a9d6db392
      Element toolTitleUrl = doc.createElement("titleUrl");
      safeAppendTextNode(doc, toolTitleUrl, getContext() + "/title/" + placement.getId(), true);

      toolElement.appendChild(title);
      toolElement.appendChild(escapedId);
      toolElement.appendChild(toolUrl);
      toolElement.appendChild(toolTitleUrl);

      return toolElement;
   }

   protected Element createSiteTypesXml(Document doc, Map siteTypesMap, String siteTypeKey, String siteId) {
      Element siteTypes = doc.createElement("siteTypes");
      List types = new ArrayList(siteTypesMap.keySet());
      Collections.sort(types);

      for (Iterator i=types.iterator();i.hasNext();) {
         SiteType type = (SiteType)i.next();
         boolean selected = type.getKey().equals(siteTypeKey);
         siteTypes.appendChild(createSiteTypeXml(doc, type, (List)siteTypesMap.get(type), selected, siteId));
      }

      return siteTypes;
   }

   protected Element createSiteTypeXml(Document doc, SiteType type, List sites, boolean selected, String siteId) {
      Element siteTypeElement = doc.createElement("siteType");
      siteTypeElement.setAttribute("selected", new Boolean(selected).toString());
      siteTypeElement.setAttribute("order", new Integer(type.getOrder()).toString());
      siteTypeElement.setAttribute("userSite", new Boolean(type == SiteType.MY_WORKSPACE).toString());
      Element siteTypeKey = doc.createElement("key");
      safeAppendTextNode(doc, siteTypeKey, type.getKey(), false);
      siteTypeElement.appendChild(siteTypeKey);

      Element siteTypeEscapedKey = doc.createElement("escapedKey");
      safeAppendTextNode(doc, siteTypeEscapedKey, Web.escapeJavascript(type.getKey()), false);
      siteTypeElement.appendChild(siteTypeEscapedKey);

      Element siteTypeUrl = doc.createElement("url");
      // /portal/site_type/<key>
      safeAppendTextNode(doc, siteTypeUrl, getContext() + "/" + SITE_TYPE + "/" + type.getKey(), true);
      siteTypeElement.appendChild(siteTypeUrl);

      Element siteTypeHelperUrl = doc.createElement("helperUrl");
      // /portal/site_type_helper/<key>
      safeAppendTextNode(doc, siteTypeHelperUrl, getContext() + "/" + SITE_TYPE_HELPER + "/" + type.getKey(), true);
      siteTypeElement.appendChild(siteTypeHelperUrl);

      siteTypeElement.appendChild(createSitesListXml(doc, sites, siteId));
      return siteTypeElement;
   }

   protected void safeAppendTextNode(Document doc, Element element, String text, boolean cdata) {
      if (text != null) {
         element.appendChild(cdata?doc.createCDATASection(text):doc.createTextNode(text));
      }
   }

   protected Element createSitesListXml(Document doc, List mySites, String siteId) {
      Session session = SessionManager.getCurrentSession();

      int prefTabs = 4;
      List prefExclude = new Vector();
      List prefOrder = new Vector();
      if (session.getUserId() != null)
      {
         Preferences prefs = PreferencesService.getPreferences(session.getUserId());
         ResourceProperties props = prefs.getProperties("sakai.portal.sitenav");
         try
         {
            prefTabs = (int) props.getLongProperty("tabs");
         }
         catch (Exception any)
         {
         }

         List l = props.getPropertyList("exclude");
         if (l != null)
         {
            prefExclude = l;
         }

         l = props.getPropertyList("order");
         if (l != null)
         {
            prefOrder = l;
         }
      }

      int tabsToDisplay = prefTabs;

      mySites.removeAll(prefExclude);
      // re-order mySites to have order first, the rest later
      List ordered = new Vector();
      for (Iterator i = prefOrder.iterator(); i.hasNext();)
      {
         String id = (String) i.next();

         // find this site in the mySites list
         int pos = indexOf(id, mySites);
         if (pos != -1)
         {
            // move it from mySites to order
            Site s = (Site) mySites.get(pos);
            ordered.add(s);
            mySites.remove(pos);
         }
      }

      // pick up the rest of the mySites
      ordered.addAll(mySites);
      mySites = ordered;

      Element sitesElement = doc.createElement("sites");
      int order = 0;
      for (Iterator i=mySites.iterator();i.hasNext();) {
         Site site= (Site) i.next();
         boolean selected = siteId != null?site.getId().equals(siteId):false;
         sitesElement.appendChild(createSiteXml(doc, site, selected, order, false));
         order++;
      }
      return sitesElement;
   }

   protected Element createSiteXml(Document doc, Site site, boolean selected, int order, boolean extra) {
      Element siteElement = doc.createElement("site");
      siteElement.setAttribute("selected", new Boolean(selected).toString());
      siteElement.setAttribute("extra", new Boolean(extra).toString());
      siteElement.setAttribute("published", new Boolean(site.isPublished()).toString());
      siteElement.setAttribute("order", new Integer(order).toString());

      Element siteUrl = doc.createElement("url");
      // http://localhost:8080/portal/site/bc5b1aa2-c53b-4fd1-0017-f91eef511e65<key>
      safeAppendTextNode(doc, siteUrl, getContext() + "/site/" + site.getId(), true);
      siteElement.appendChild(siteUrl);

      Element siteTitle = doc.createElement("title");
      safeAppendTextNode(doc, siteTitle, site.getTitle(), true);
      siteElement.appendChild(siteTitle);

      Element siteDescription = doc.createElement("description");
      safeAppendTextNode(doc, siteDescription, site.getDescription(), true);
      siteElement.appendChild(siteDescription);

      return siteElement;
   }

   protected Element createSkinsXml(Document doc, List skins) {
      Element skinsElement = doc.createElement("skins");
      int index = 0;

      String skinRepo = ServerConfigurationService.getString("skin.repo");

      Element skin = doc.createElement("skin");
      skin.setAttribute("order", index + "");
      safeAppendTextNode(doc, skin, getContext() + "/library/css/osp-portal.css", true);
      skinsElement.appendChild(skin);
      index++;

      for (Iterator i=skins.iterator();i.hasNext();) {
         String skinUrl = (String) i.next();
         index = appendSkin(skinRepo, skinUrl, doc, index, skinsElement, "/portal.css");
      }

      if (index == 1) {
         String skinUrl = ServerConfigurationService.getString("skin.default");
         index = appendSkin(skinRepo, skinUrl, doc, index, skinsElement, "/portal.css");
      }

      return skinsElement;
   }

   protected Element createToolSkinsXml(Document doc, List skins) {
      Element skinsElement = doc.createElement("toolSkins");
      int index = 0;

      String skinRepo = ServerConfigurationService.getString("skin.repo");

      Element skin = doc.createElement("skin");
      skin = doc.createElement("skin");
      skin.setAttribute("order", index + "");
      safeAppendTextNode(doc, skin, getContext() + "/library/skin/tool_base.css", true);
      skinsElement.appendChild(skin);
      index++;

      for (Iterator i=skins.iterator();i.hasNext();) {
         String skinUrl = (String) i.next();
         index = appendSkin(skinRepo, skinUrl, doc, index, skinsElement, "/tool.css");
      }

      if (index == 2) {
         String skinUrl = ServerConfigurationService.getString("skin.default");
         index = appendSkin(skinRepo, skinUrl, doc, index, skinsElement, "/tool.css");
      }

      return skinsElement;
   }

   protected int appendSkin(String skinRepo, String skinUrl,
                            Document doc, int index, Element skinsElement, String cssFile) {
      Element skin;
      String skinPortalUrl = skinRepo + "/" + skinUrl + cssFile;
      skin = doc.createElement("skin");
      skin.setAttribute("order", index + "");
      safeAppendTextNode(doc, skin, skinPortalUrl, true);
      skinsElement.appendChild(skin);
      index++;
      return index;
   }

   protected List getSkins(SiteType siteType, Site site) {
      List skins = new ArrayList();

      if (siteType != null && siteType.getSkin() != null) {
         skins.add(siteType.getSkin());
      }

      if (site != null && site.getSkin() != null) {
         skins.add(site.getSkin());
      }

      return skins;
   }

   protected Element createUserXml(Document doc, User current) {
      Element user = doc.createElement("currentUser");

      appendTextElementNode(doc, "id", current.getId(), user);
      appendTextElementNode(doc, "first", current.getFirstName(), user);
      appendTextElementNode(doc, "last", current.getLastName(), user);
      appendTextElementNode(doc, "email", current.getEmail(), user);

      return user;
   }

   protected String checkVisitSite(String siteId, Session session,
                                    HttpServletRequest req, HttpServletResponse res) throws IOException, ToolException {
      // default site if not set
      if (siteId == null)
      {
         if (session.getUserId() == null)
         {
            siteId = ServerConfigurationService.getGatewaySiteId();
         }
         else
         {
            siteId = SiteService.getUserSiteId(session.getUserId());
         }
      }

      // find the site, for visiting
      Site site = null;
      try
      {
         site = SiteService.getSiteVisit(siteId);
      }
      catch (IdUnusedException e)
      {
         doError(req, res, session, ERROR_SITE);
         return null;
      }
      catch (PermissionException e)
      {
         // if not logged in, give them a chance
         if (session.getUserId() == null)
         {
            doLogin(req, res, session, req.getPathInfo(), false);
         }
         else
         {
            doError(req, res, session, ERROR_SITE);
         }
         return null;
      }

      return siteId;
   }

   public void init(ServletConfig config) throws ServletException {
      super.init(config);
      setPortalManager((PortalManager) ComponentManager.get(PortalManager.class));
      try {
         setDocumentBuilder(DocumentBuilderFactory.newInstance().newDocumentBuilder());
         String transformPath = config.getInitParameter("transform");
         Templates templates = createTemplate(config, transformPath);
         setTemplates(templates);

         String transformToolCategoryPath = config.getInitParameter("transformToolCategory");
         Templates templatesToolCategory = createTemplate(config, transformToolCategoryPath);
         setServletResolver(new ServletResourceUriResolver(config.getServletContext()));
         setToolCategoryTemplates(templatesToolCategory);
      }
      catch (ParserConfigurationException e) {
         throw new ServletException(e);
      }
      catch (TransformerConfigurationException e) {
         throw new ServletException(e);
      }
      catch (MalformedURLException e) {
         throw new ServletException(e);
      }
   }

   private Templates createTemplate(ServletConfig config, String transformPath) throws MalformedURLException, TransformerConfigurationException {
      InputStream stream = config.getServletContext().getResourceAsStream(
            transformPath);
      URL url = config.getServletContext().getResource(transformPath);
      String urlPath = url.toString();
      String systemId = urlPath.substring(0, urlPath.lastIndexOf('/') + 1);
      Templates templates = TransformerFactory.newInstance().newTemplates(
                     new StreamSource(stream, systemId));
      return templates;
   }

   public PortalManager getPortalManager() {
      return portalManager;
   }

   public void setPortalManager(PortalManager portalManager) {
      this.portalManager = portalManager;
   }

   protected String getContext() {
      return "/" + this.getServletContext().getServletContextName();
   }

   public DocumentBuilder getDocumentBuilder() {
      return documentBuilder;
   }

   public void setDocumentBuilder(DocumentBuilder documentBuilder) {
      this.documentBuilder = documentBuilder;
   }

   protected Transformer getToolCategoryTransformer() {
      try {
         Transformer trans = getToolCategoryTemplates().newTransformer();
         trans.setURIResolver(getServletResolver());
         return trans;
      }
      catch (TransformerConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   public Transformer getTransformer() {
      try {
         Transformer trans = getTemplates().newTransformer();
         trans.setURIResolver(getServletResolver());
         return trans;
      }
      catch (TransformerConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   public Templates getTemplates() {
      return templates;
   }

   public void setTemplates(Templates templates) {
      this.templates = templates;
   }

   public Templates getToolCategoryTemplates() {
      return toolCategoryTemplates;
   }

   public void setToolCategoryTemplates(Templates toolCategoryTemplates) {
      this.toolCategoryTemplates = toolCategoryTemplates;
   }

   public URIResolver getServletResolver() {
      return servletResolver;
   }

   public void setServletResolver(URIResolver servletResolver) {
      this.servletResolver = servletResolver;
   }

   protected void dumpDocument(Element element) throws IOException {
      OutputFormat format = new OutputFormat();

      format.setIndent(3);
      format.setIndenting(true);
      format.setPreserveSpace(true);

      Serializer serializer = SerializerFactory.getSerializerFactory("xml").makeSerializer(System.out, format);
      serializer.asDOMSerializer().serialize(element);
   }

}
