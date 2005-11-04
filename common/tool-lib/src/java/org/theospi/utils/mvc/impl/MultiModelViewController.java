/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/utils/mvc/impl/MultiModelViewController.java,v 1.1 2005/08/11 17:01:08 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.utils.mvc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MultiModelViewController implements LoadObjectController, CustomCommandController {
   protected final Log logger = LogFactory.getLog(getClass());

   private List controllers = null;


   public Object formBackingObject(Map request, Map session, Map application) {
      List currentList = new ArrayList();

      for (Iterator i=controllers.iterator();i.hasNext();) {
         Controller controller = (Controller)i.next();
         ControllerWrapper wrapper = new ControllerWrapper();
         wrapper.controller = controller;
         if (controller instanceof CustomCommandController){
            wrapper.currentObject = ((CustomCommandController)controller).formBackingObject(request, session, application);
         }
         currentList.add(wrapper);
      }

      return currentList;
   }

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {

      List currentList = (List)incomingModel;

      for (Iterator i=currentList.iterator();i.hasNext();) {
         ControllerWrapper controller = (ControllerWrapper)i.next();

         if (controller instanceof LoadObjectController){
            controller.currentObject = ((LoadObjectController)controller.controller).fillBackingObject(
               controller.currentObject, request, session, application);
         }
      }

      return currentList;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      List currentList = (List)requestModel;
      Hashtable globalMap = new Hashtable();

      for (Iterator i=currentList.iterator();i.hasNext();) {
         ControllerWrapper controller = (ControllerWrapper)i.next();
         ModelAndView controllerMv = controller.controller.handleRequest(
            controller.currentObject, request, session, application, errors);
         globalMap.putAll(controllerMv.getModel());
      }

      return new ModelAndView("success", globalMap);
   }

   private class ControllerWrapper {
      public Controller controller;
      public Object currentObject;
   }

   public List getControllers() {
      return controllers;
   }

   public void setControllers(List controllers) {
      this.controllers = controllers;
   }

}
