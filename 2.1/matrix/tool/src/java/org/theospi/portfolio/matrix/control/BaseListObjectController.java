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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/BaseListObjectController.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */


package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.CriterionTransport;
import org.theospi.portfolio.matrix.model.Expectation;
import org.theospi.portfolio.matrix.model.ExpectationTransport;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.LevelTransport;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;


/**
 * @author chmaurer
 */
public abstract class BaseListObjectController implements FormController, LoadObjectController, CustomCommandController {
   
  
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      return model;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      String index = (String)request.get("index");
      if (index != null) {
         EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
               EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         Object obj = null;
         String path = (String)request.get("path");
         if (incomingModel instanceof CriterionTransport) {
            obj = new CriterionTransport(findCriterion(sessionBean.getScaffolding(), path.concat("." + index)));
            //obj = sessionBean.getScaffolding().getCriteria().get(
            //      Integer.parseInt(index));
         }
         else if (incomingModel instanceof LevelTransport) {
            obj = new LevelTransport((Level)sessionBean.getScaffolding().getLevels().get(
                     Integer.parseInt(index)));
         }
         else if (incomingModel instanceof ExpectationTransport) {
            obj = new ExpectationTransport((Expectation)sessionBean.getScaffoldingCell().getExpectations().get(
                  Integer.parseInt(index)));
         }  
         
         return obj;
      }
      return incomingModel;
   }
   
  
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      Map model = new HashMap();
      
      String action = (String) request.get("action");
      if (action == null) action = (String) request.get("submitAction");
      if (action != null) {
         if (action.equals("Update")) {
            String index = (String)request.get("index");
            if (requestModel instanceof CriterionTransport) {
               CriterionTransport obj = (CriterionTransport) requestModel;
               String path = (String)request.get("path");
               obj.setIndent(getIndent(path));
               //Criterion parent = findCriterion(sessionBean.getScaffolding(), path);
               List parentList = findCriteria(sessionBean.getScaffolding(), path);
               if (index == null) {
                  parentList.add(new Criterion(obj));
               }
               else {
                  int idx = Integer.parseInt(index);
                  Criterion criterion = (Criterion) parentList.get(idx);
                  criterion.copy(obj);
                  parentList.set(idx, criterion);
               }
               //updateScaffolding(scaffolding, parentList, path);
               sessionBean.setScaffolding(scaffolding); 
               model.put("path", path);
            }
            else if (requestModel instanceof LevelTransport) {
               LevelTransport obj = (LevelTransport) requestModel;
               if (index == null) {
                  //scaffolding.add(obj);
                  scaffolding.add(new Level(obj));
               }
               else {
                  int idx = Integer.parseInt(index);
                  Level level = (Level)scaffolding.getLevels().get(idx);
                  level.copy(obj);
                  scaffolding.getLevels().set(idx, level);
               }
               sessionBean.setScaffolding(scaffolding);
            }
            else if (requestModel instanceof ExpectationTransport) {
               ScaffoldingCell scaffoldingCell = sessionBean.getScaffoldingCell();
               ExpectationTransport obj = (ExpectationTransport) requestModel;
               if (index == null) {
                  scaffoldingCell.add(new Expectation(obj));
               }
               else {
                  int idx = Integer.parseInt(index);
                  Expectation expectation = (Expectation)scaffoldingCell.getExpectations().get(idx);
                  expectation.copy(obj);
                  scaffoldingCell.getExpectations().set(idx, expectation);
               }
               sessionBean.setScaffoldingCell(scaffoldingCell);
            }
            
            session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
                  sessionBean);
         }
         else if (action.equals("forward")) {
            String forwardView = (String)request.get("dest");
            String params = (String)request.get("params");
            if (!params.equals("")) {
               String[] paramsList = params.split(":");
               for (int i=0; i<paramsList.length; i++) {
                  String[] pair = paramsList[i].split("=");
                  String val = null;
                  if (pair.length>1)
                     val = pair[1];
                  model.put(pair[0], val);
               }
            }
            //matrixManager.storeScaffolding(scaffolding);
            sessionBean.setScaffolding(scaffolding);
            model.put("scaffolding_id", scaffolding.getId());
            return new ModelAndView(forwardView, model);
            
         }
      }

      model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");

      return new ModelAndView("success", model);
   }

   protected List findCriteria(Scaffolding scaffolding, String path) {
      StringTokenizer tok = new StringTokenizer(path, ".");
      //ElementBean current = bean;
      List current = scaffolding.getCriteria();

      while (tok.hasMoreTokens()) {
         Criterion obj = (Criterion)current.get(Integer.parseInt(tok.nextToken()));
         current = obj.getCriteria();
      }

      return current;
   }
   
   protected Criterion findCriterion(Scaffolding scaffolding, String path) {
      StringTokenizer tok = new StringTokenizer(path, ".");
      
      //ElementBean current = bean;
      List current = scaffolding.getCriteria();
      Criterion obj = null;
      while (tok.hasMoreTokens()) {
         obj = (Criterion)current.get(Integer.parseInt(tok.nextToken()));
         current = obj.getCriteria();
      }
      //obj = (Criterion)current.get(Integer.parseInt(index));

      return obj;
   }

   protected Integer getIndent(String path) {
      int retVal;
      if (path == null) { 
         retVal = 0;
      }
      else {
         StringTokenizer tok = new StringTokenizer(path, ".");
         retVal = tok.countTokens();
      }
      return new Integer(retVal);    
   }
}
