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

import org.sakaiproject.portal.charon.CharonPortal;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.util.java.ResourceLoader;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.theospi.portfolio.portal.intf.PortalManager;
import org.theospi.portfolio.portal.model.SiteType;
import org.theospi.portfolio.portal.model.ToolCategory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.PrintWriter;
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

   /** messages. */
   private static ResourceLoader rb = new ResourceLoader("org/theospi/portfolio/portal/messages");
   private static final String TOOL_CATEGORY = "category";
   private static final String SITE_TYPE = "site_type";

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
      else if (parts[1].equals(TOOL_CATEGORY)) {
         // Resolve the site_type of the form /portal/category/<siteId>/<categoryKey>/<optionalToolId>
         String siteId = parts[2];
         String categoryKey = parts[3];
         String toolId = null;
         if (parts.length > 4) {
            toolId = parts[4];
         }
         doCategory(req, res, session, siteId, categoryKey, toolId);
      }
      else {
         super.doGet(req, res);
      }
   }

   protected void doCategory(HttpServletRequest req, HttpServletResponse res, Session session,
                             String siteId, String categoryKey, String toolId) throws IOException {
      siteId = checkVisitSite(siteId, session, req, res);
      if (siteId == null) {
         return;
      }
   }

   protected void doSiteType(HttpServletRequest req, HttpServletResponse res, Session session,
                             String siteTypeKey) throws IOException  {
      Document doc = createPortalDocument(siteTypeKey, null, null, null);
      outputDocument(req, res, session, doc);
   }

   protected void outputDocument(HttpServletRequest req, HttpServletResponse res,
                                 Session session, Document doc) throws IOException {
      PrintWriter out = res.getWriter();

      try {
         StreamResult outputTarget = new StreamResult(out);
         getTransformer().transform(new DOMSource(doc), outputTarget);
      }
      catch (TransformerException e) {
         throw new RuntimeException(e);
      }
   }

   protected void doGallery(HttpServletRequest req, HttpServletResponse res, Session session,
                            String siteId, String pageId, String toolContextPath) throws IOException {
      super.doGallery(req, res, session, siteId, pageId, toolContextPath);
   }

   protected void doSite(HttpServletRequest req, HttpServletResponse res, Session session,
                         String siteId, String pageId, String toolContextPath) throws IOException {
      siteId = checkVisitSite(siteId, session, req, res);
      if (siteId == null) {
         return;
      }
      Document doc = createPortalDocument(null, siteId, null, pageId);
      outputDocument(req, res, session, doc);
   }

   protected void doWorksite(HttpServletRequest req, HttpServletResponse res, Session session,
                             String siteId, String pageId, String toolContextPath) throws IOException {
      siteId = checkVisitSite(siteId, session, req, res);
      if (siteId == null) {
         return;
      }
      Document doc = createPortalDocument(null, siteId, null, pageId);
      outputDocument(req, res, session, doc);
   }

   protected void doPage(HttpServletRequest req, HttpServletResponse res, Session session,
                         String pageId, String toolContextPath) throws IOException {
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
      Document doc = createPortalDocument(null, siteId, getPortalManager().getPageCategory(pageId), pageId);
      outputDocument(req, res, session, doc);
   }

   protected Document createPortalDocument(String siteTypeKey, String siteId, String toolCategoryKey, String pageId) {
      Document doc = getDocumentBuilder().newDocument();

      Element root = doc.createElement("portal");
      doc.appendChild(root);

      User currentUser = getPortalManager().getCurrentUser();
      if (currentUser != null) {
         root.appendChild(createUserXml(doc, currentUser));
      }

      Map siteTypesMap = getPortalManager().getSitesByType();
      Site site = null;

      if (siteId != null) {
         site = getPortalManager().getSite(siteId);
      }

      if (siteTypeKey == null) {
         siteTypeKey = site.getType();
      }

      SiteType siteType = findType(siteTypesMap, siteTypeKey);
      List skins = getSkins(siteType, site);
      root.appendChild(createSkinsXml(doc, skins));
      root.appendChild(createSiteTypesXml(doc, siteTypesMap, siteTypeKey, siteId));

      if (siteId != null) {
         Map pageCateogries = getPortalManager().getPagesByCategory(siteId);
         root.appendChild(createPageCategoriesXml(doc, pageCateogries, siteId, toolCategoryKey, pageId));
      }

/*

   public List getToolsForPage(String pageId);

*/

      return doc;
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
      boolean selected = category.getKey().equals(categoryPageList);
      categoryElement.setAttribute("selected", new Boolean(selected).toString());
      categoryElement.setAttribute("order", new Integer(category.getOrder()).toString());
      Element categoryKeyElement = doc.createElement("key");
      safeAppendTextNode(doc, categoryKeyElement, category.getKey(), false);
      Element categoryDescriptionElement = doc.createElement("description");
      safeAppendTextNode(doc, categoryDescriptionElement, category.getDescription(), true);
      Element categoryUrlElement = doc.createElement("url");
      // Resolve the site_type of the form /portal/category/<siteId>/<categoryKey>/<optionalToolId>
      safeAppendTextNode(doc, categoryUrlElement,
            getContext() + "/" + TOOL_CATEGORY + "/" + siteId + "/" + category.getKey(), true);

      categoryElement.appendChild(categoryKeyElement);
      categoryElement.appendChild(categoryDescriptionElement);
      categoryElement.appendChild(categoryUrlElement);

      Element pagesElement = doc.createElement("pages");

      int index = 0;

      for (Iterator i=categoryPageList.iterator();i.hasNext();) {
         SitePage page = (SitePage) i.next();
         pagesElement.appendChild(createPageXml(doc, index, siteId, page, pageId));
      }

      categoryElement.appendChild(pagesElement);
      return categoryElement;
   }

   protected Element createPageXml(Document doc, int index, String siteId, SitePage page, String pageId) {
      Element pageElement = doc.createElement("page");
      pageElement.setAttribute("order", new Integer(index).toString());
      boolean pageSelected = page.getId().equals(pageId);
      pageElement.setAttribute("selected", new Boolean(pageSelected).toString());
      pageElement.setAttribute("layout", new Integer(page.getLayout()).toString());
      Element pageName = doc.createElement("title");
      safeAppendTextNode(doc, pageName, page.getTitle(), true);
      // portal/site/9607661f-f3aa-4938-8005-c3ffaa228c6c/page/0307f10c-225b-4db8-803e-b12f24e38544
      Element pageUrl = doc.createElement("url");
      safeAppendTextNode(doc, pageUrl, getContext() + "/site/" + siteId + "/page/" + page.getId(), true);

      Element columns = doc.createElement("columns");

      for (int i=0;i<2;i++) {
         Element column = doc.createElement("column");
         column.setAttribute("index", new Integer(i).toString());
         column.appendChild(createColumnToolsXml(doc, page.getTools(i), pageId));
         columns.appendChild(column);
      }

      pageElement.appendChild(pageName);
      pageElement.appendChild(pageUrl);
      pageElement.appendChild(columns);

      return pageElement;
   }

   protected Element createColumnToolsXml(Document doc, List tools, String pageId) {
      Element toolsElement = doc.createElement("tools");

      for (Iterator i=tools.iterator();i.hasNext();) {
         Placement placement = (Placement) i.next();
         toolsElement.appendChild(createToolXml(doc, placement, pageId));
      }

      return toolsElement;
   }

   protected Element createToolXml(Document doc, Placement placement, String pageId) {
      Element toolElement = doc.createElement("tool");

      //portal/tool/ad222467-e186-4cca-80e9-d12a9d6db392?panel=Main
      Element toolUrl = doc.createElement("url");
      safeAppendTextNode(doc, toolUrl, getContext() + "/tool/" + placement.getId() + "?panel=Main", true);

      //portal/title/ad222467-e186-4cca-80e9-d12a9d6db392
      Element toolTitleUrl = doc.createElement("titleUrl");
      safeAppendTextNode(doc, toolTitleUrl, getContext() + "/title/" + placement.getId(), true);

      toolElement.appendChild(toolUrl);
      toolElement.appendChild(toolTitleUrl);

      return toolElement;
   }

   protected SiteType findType(Map siteTypesMap, String siteTypeKey) {
      for (Iterator i=siteTypesMap.keySet().iterator();i.hasNext();) {
         SiteType type = (SiteType) i.next();
         if (type.equals(siteTypeKey)) {
            return type;
         }
      }
      return null;
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
      Element siteTypeKey = doc.createElement("key");
      safeAppendTextNode(doc, siteTypeKey, type.getKey(), false);
      siteTypeElement.appendChild(siteTypeKey);

      Element siteTypeUrl = doc.createElement("url");
      // /portal/site_type/<key>
      safeAppendTextNode(doc, siteTypeUrl, getContext() + "/" + SITE_TYPE + "/" + type.getKey(), true);
      siteTypeElement.appendChild(siteTypeUrl);

      Element siteTypeDescription = doc.createElement("description");
      safeAppendTextNode(doc, siteTypeDescription, type.getDescription(), true);
      siteTypeElement.appendChild(siteTypeDescription);
      siteTypeElement.appendChild(createSitesListXml(doc, sites, siteId));
      return siteTypeElement;
   }

   protected void safeAppendTextNode(Document doc, Element element, String text, boolean cdata) {
      if (text != null) {
         element.appendChild(cdata?doc.createCDATASection(text):doc.createTextNode(text));
      }
   }

   protected Element createSitesListXml(Document doc, List sites, String siteId) {
      Element sitesElement = doc.createElement("sites");
      int order = 0;
      for (Iterator i=sites.iterator();i.hasNext();) {
         Site site= (Site) i.next();
         boolean selected = site.getId().equals(siteId);
         sitesElement.appendChild(createSiteXml(doc, site, selected, order));
         order++;
      }
      return sitesElement;
   }

   protected Element createSiteXml(Document doc, Site site, boolean selected, int order) {
      Element siteElement = doc.createElement("site");
      siteElement.setAttribute("selected", new Boolean(selected).toString());
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
      for (Iterator i=skins.iterator();i.hasNext();) {
         String skinUrl = (String) i.next();
         skinUrl = skinRepo + "/" + skinUrl + "/portal.css";
         Element skin = doc.createElement("skin");
         skin.setAttribute("order", index + "");
         safeAppendTextNode(doc, skin, skinUrl, true);
         skinsElement.appendChild(skin);
         index++;
      }

      if (index == 0) {
         String skinUrl = ServerConfigurationService.getString("skin.default");
         skinUrl = skinRepo + "/" + skinUrl + "/portal.css";
         Element skin = doc.createElement("skin");
         skin.setAttribute("order", index + "");
         safeAppendTextNode(doc, skin, skinUrl, true);
         skinsElement.appendChild(skin);
      }

      return skinsElement;
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
      // todo fill in details
      return user;
   }

   protected String checkVisitSite(String siteId, Session session,
                                    HttpServletRequest req, HttpServletResponse res) throws IOException {
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
         // todo get real template as transformer
         //setTemplate(TransformerFactory.newInstance().newTransformer());
      }
      catch (ParserConfigurationException e) {
         throw new ServletException(e);
      }
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

   public Transformer getTransformer() {
      try {
         return TransformerFactory.newInstance().newTransformer();
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

}
