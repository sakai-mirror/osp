package org.sakaiproject.portal.xsltcharon.impl;

import org.sakaiproject.portal.api.PortalHandler;
import org.sakaiproject.portal.api.PortalHandlerException;
import org.sakaiproject.portal.api.Portal;
import org.sakaiproject.portal.api.PortalService;
import org.sakaiproject.tool.api.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jul 20, 2007
 * Time: 12:04:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransformHandler implements PortalHandler {

   private PortalHandler baseHandler;
   private String fragment;

   public TransformHandler(PortalHandler baseHandler, String fragment) {
      this.baseHandler = baseHandler;
      this.fragment = fragment;
   }

   public int doGet(String[] parts, HttpServletRequest req, HttpServletResponse res, Session session) throws PortalHandlerException {
      return getBaseHandler().doGet(prepareParts(parts), req, res, session);
   }

   public String getUrlFragment() {
      return fragment;
   }

   public void deregister(Portal portal) {
      getBaseHandler().deregister(portal);
   }

   public void register(Portal portal, PortalService portalService, ServletContext servletContext) {
      getBaseHandler().register(portal, portalService, servletContext);
   }

   public int doPost(String[] parts, HttpServletRequest req, HttpServletResponse res, Session session) throws PortalHandlerException {
      return getBaseHandler().doPost(prepareParts(parts), req, res, session);
   }

   public PortalHandler getBaseHandler() {
      return baseHandler;
   }

   public void setBaseHandler(PortalHandler baseHandler) {
      this.baseHandler = baseHandler;
   }

   protected String[] prepareParts(String[] parts) {
      String[] subparts = new String[parts.length - 1];

      for (int i=0;i<subparts.length;i++){
         subparts[i] = parts[i+1];
      }
      return subparts;
   }

}
