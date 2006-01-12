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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/MatrixValidator.java,v 1.1 2005/07/15 21:10:34 rpembry Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.matrix.control;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.springframework.validation.Errors;
import org.theospi.portfolio.matrix.model.CriterionTransport;
import org.theospi.portfolio.matrix.model.Expectation;
import org.theospi.portfolio.matrix.model.ExpectationTransport;
import org.theospi.portfolio.matrix.model.LevelTransport;
import org.theospi.portfolio.matrix.model.ReflectionItemTransport;
import org.theospi.portfolio.matrix.model.ReflectionTransport;
import org.theospi.portfolio.matrix.model.ReviewerItem;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.ScaffoldingUploadForm;
import org.theospi.utils.mvc.impl.ValidatorBase;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 2:37:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixValidator extends ValidatorBase {
   
   private AuthenticationManager authManager;
   
   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      if (MatrixFormBean.class.isAssignableFrom(clazz)) return true;
      else if (ScaffoldingUploadForm.class.isAssignableFrom(clazz)) return true;
      else if (Scaffolding.class.isAssignableFrom(clazz)) return true;
      else if (ScaffoldingCell.class.isAssignableFrom(clazz)) return true;
      else if (LevelTransport.class.isAssignableFrom(clazz)) return true;
      else if (CriterionTransport.class.isAssignableFrom(clazz)) return true;
      else if (ExpectationTransport.class.isAssignableFrom(clazz)) return true;
      else if (ReviewerItem.class.isAssignableFrom(clazz)) return true;
      else if (ReflectionTransport.class.isAssignableFrom(clazz)) return true;
      else if (CellAndNodeForm.class.isAssignableFrom(clazz)) return true;
      else return false;
   }

   /**
    * Validate a presentation object, which must be of a class for which
    * the supports() method returned true.
    *
    * @param obj    Populated object to validate
    * @param errors Errors object we're building. May contain
    *               errors for this field relating to types.
    */
   public void validate(Object obj, Errors errors) {
      //if (obj instanceof ScaffoldingUploadForm) 
      //   validateScaffoldingImport((ScaffoldingUploadForm)obj, errors);
      if (obj instanceof CriterionTransport)
         validateCriterion((CriterionTransport)obj, errors);
      else if (obj instanceof LevelTransport)
         validateLevel((LevelTransport)obj, errors);
      else if (obj instanceof Scaffolding) {
         Scaffolding scaffolding = (Scaffolding) obj;
         if (scaffolding.isValidate())
            validateScaffolding(scaffolding, errors);
      }
      else if (obj instanceof ExpectationTransport)
         validateExpectation((ExpectationTransport)obj, errors);
      else if (obj instanceof ScaffoldingCell) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) obj;
         if (scaffoldingCell.isValidate())
            validateScaffoldingCell(scaffoldingCell, errors);
      }
      else if (obj instanceof CellAndNodeForm)
         validateCellAttachment((CellAndNodeForm)obj, errors);
   }
  /* 
   private void validateScaffoldingImport(ScaffoldingUploadForm obj, Errors errors) {
      RepositoryNode node = (RepositoryNode)getRepositoryManager().getRootNode(getAuthManager().getAgent());
      if (node.hasChild(obj.getDisplayName())) {
         errors.rejectValue("displayName", "duplicate", "duplicate");
      }
      if (!obj.getUploadedScaffoldingForm().getContentType().equals("text/xml")) {
         errors.rejectValue("uploadedScaffoldingForm", "invalid file", "invalid file");
      }
   }
   */
   
   protected void validateCellAttachment(CellAndNodeForm form, Errors errors) {
      if (form.getNode_id() == null || form.getNode_id().equals("")) {
         errors.rejectValue("node_id", "error.required", "required");
      }
   }
   
   protected void validateScaffoldingCell(ScaffoldingCell scaffoldingCell, Errors errors) {
      if (scaffoldingCell.getInitialStatus() == null ||
            scaffoldingCell.getInitialStatus().equals("")) {
         errors.rejectValue("initialStatus", "error.required", "required");
      }
   }

   protected void validateCriterion(CriterionTransport criterion, Errors errors) {
      if (criterion.getDescription() == null || criterion.getDescription().equals("")) {
         errors.rejectValue("description", "error.required", "required");
      }
   }

   protected void validateLevel(LevelTransport level, Errors errors) {
      if (level.getDescription() == null || level.getDescription().equals("")) {
         errors.rejectValue("description", "error.required", "required");
      }
   }
   
   protected void validateExpectation(ExpectationTransport expectation, Errors errors) {
      if (expectation.getDescription() == null || expectation.getDescription().equals("") || 
            stripHtml(expectation.getDescription()).trim().equals("")) {
         errors.rejectValue("description", "error.required", "required");
      }
   }
   
   protected void validateScaffolding(Scaffolding scaffolding, Errors errors) {
      if (scaffolding.getTitle() == null || scaffolding.getTitle().equals("")) {
         errors.rejectValue("title", "error.required", "required");
      }
      if (scaffolding.getLevels() == null || scaffolding.getLevels().size() == 0) {
         errors.rejectValue("levels", "error.required", "required");
      }
      if (scaffolding.getCriteria() == null || scaffolding.getCriteria().size() == 0) {
         errors.rejectValue("criteria", "error.required", "required");
      }
   }
   
   protected void validateReflectionCheckBoxes(Object o, Errors errors) {
      ReflectionTransport bean = (ReflectionTransport) o;
      if (bean.getSelectedExpectations() == null ||
            bean.getSelectedExpectations().length == 0 || 
            bean.getSelectedExpectations()[0] == null ||
            allOff(bean.getSelectedExpectations())) {
         errors.rejectValue("selectedExpectations", "error.required", "required");
      }
   }
   
   private boolean allOff(String[] items) {
      List list = Arrays.asList(items);
      for (Iterator i = list.iterator(); i.hasNext();) {
         //Object obj = i.next();
         String val = (String)i.next();
         //if (obj instanceof String) {
            // off, on
           // String val = (String)obj;
            if (val.equals("on") || isNumber(val))
               return false;
         //}
         //else {
            // the index of the page that's on
         //   return false;
         //}
      }
      return true;
   }
   
   private static boolean isNumber(String n) {
      try {
         double d = Double.valueOf(n).doubleValue();
         return true;
      }
      catch (NumberFormatException e) {
         //swallow exception because we don't care
         return false;
      }
   }

   
   protected void validateReflectionSubmit(Object o, Errors errors) {
      //Reflection bean = (Reflection) o;
      //Reflection reflection = bean.getWizardPage().getReflection();
      //Reflection reflection = (Reflection) o;
      ReflectionTransport reflection = (ReflectionTransport) o;
      int i=0;
      for (Iterator iter = reflection.getReflectionItems().iterator(); iter.hasNext();) {
         ReflectionItemTransport item = (ReflectionItemTransport) iter.next();
         Expectation ex = item.getExpectation();
         if (ex.isRequired()) {
            if (item.getConnect() == null || stripHtml(item.getConnect()).trim().equals("")) {
               errors.rejectValue("reflectionItems[" + i + "].connect", "error.required", "required");      
            }
            if (item.getEvidence() == null || stripHtml(item.getEvidence()).trim().equals("")) {
               errors.rejectValue("reflectionItems[" + i + "].evidence", "error.required", "required");      
            }
         }
         i++;
      }
      //if (reflection.getWizardPage().getScaffoldingCell().isGradableReflection()) {
      //   if (reflection.getGrowthStatement() == null || 
      //         stripHtml(reflection.getGrowthStatement()).trim().equals("")) {
      //      errors.rejectValue("growthStatement", "error.required", "required");      
      //   }
      //}
   }
   
   private String stripHtml(String input) {
      return input.replaceAll("<[\\w/]+[^<>]*>", "");     
   }

   /**
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthManager() {
      return authManager;
   }
   /**
    * @param authManager The authManager to set.
    */
   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }
}

