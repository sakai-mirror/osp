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
 * $Header: /opt/CVS/osp2.x/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ReflectionController.java,v 1.2 2005/07/20 18:25:13 rpembry Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on Jun 1, 2004
 */
package org.theospi.portfolio.matrix.control;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.impl.servlet.ServletRequestBeanDataBinder;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Expectation;
import org.theospi.portfolio.matrix.model.Reflection;
import org.theospi.portfolio.matrix.model.ReflectionItem;
import org.theospi.portfolio.matrix.model.ReflectionTransport;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ReflectionController extends AbstractWizardFormController {

   private int virtualPageIndex;
   private IdManager idManager;
   private MatrixManager matrixManager;

   /**
    * Indicates which of the wizard pages is to be accessed multiple times (i.e. acts like
    * multiple pages)
    *
    * @return
    */
   public int getVirtualPageIndex() {
      return virtualPageIndex;
   }

   public void setVirtualPageIndex(int virtualPageIndex) {
      this.virtualPageIndex = virtualPageIndex;
   }
   
   private void setBeanProperties(ReflectionTransport reflection) {
      int totalPages = reflection.getCell().getScaffoldingCell().getExpectations().size();
      String[] replacement = new String[totalPages];
      for (int i=0; i<replacement.length; i++) {
         replacement[i] = "off";
      }
      List activePages = new ArrayList();
      try {
         List checkedExpectations = new ArrayList();
         checkedExpectations = Arrays.asList(reflection.getSelectedExpectations());
         int count = 0;
         for (Iterator j = checkedExpectations.iterator(); j.hasNext();) {
            String str = (String)j.next();
            if (str == null || str.equals("off") || str.equals("")) {
               String tmpStr = String.valueOf(count);
               Integer val = Integer.valueOf(tmpStr);
               replacement[val.intValue()] = "off";
            }
            else if (str.equals("on")) {
               str = String.valueOf(count);
            }
            if (str != null && !str.equals("off") && !str.equals("")) {
               Integer val = Integer.valueOf(str);
               replacement[val.intValue()] = "on";
               activePages.add(val.toString());
            }

            count = count+1;
         }
         reflection.setSelectedExpectations(replacement);
         reflection.setActivePages(activePages);
         reflection.setTotalPages(activePages.size()+1);
      }
      catch (Exception e) {
         logger.error("", e);
      }
   }
   
   public Object formBackingObject(HttpServletRequest request) throws Exception {
      logger.debug("In ReflectionController:formBackingObject");
      Reflection reflection = new Reflection();
      
      // this is an edit, load model
      if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
         Id id = getIdManager().getId(request.getParameter("id"));
         reflection = getMatrixManager().getReflection(id);
         List tmpList = new ArrayList();
         for (Iterator i = reflection.getCell().getScaffoldingCell().getExpectations().iterator(); i.hasNext();) {
            Expectation ex = (Expectation)i.next();
            ReflectionItem ri = getReflectionItem(reflection.getReflectionItems(), ex);
            if (ri == null) {
               ri = new ReflectionItem();
               ri.setExpectation(ex);
               ri.setReflection(reflection);
            } 
            tmpList.add(ri);
         }
         reflection.setReflectionItems(tmpList);
      }
      else {

         Id cellId = getIdManager().getId(request.getParameter("cell_id"));
         Cell cell = getMatrixManager().getCell(cellId);
         cell.setReflection(reflection);
         reflection.setCell(cell);
         for (Iterator i = cell.getScaffoldingCell().getExpectations().iterator(); i.hasNext();) {
            Expectation ex = (Expectation)i.next();
            ReflectionItem ri = new ReflectionItem();
            ri.setExpectation(ex);
            ri.setReflection(reflection);
            reflection.getReflectionItems().add(ri);
         }
      }
      return new ReflectionTransport(reflection);
   }
   
   private ReflectionItem getReflectionItem(Collection reflectionItems, Expectation expectation) {
      for (Iterator iter=reflectionItems.iterator(); iter.hasNext();) {
         ReflectionItem ri = (ReflectionItem) iter.next();
         if (ri.getExpectation().getId().getValue().equals(expectation.getId().getValue())) {
            return ri;
         }
      }
      return null;
   }
   
   protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int currentPage) throws Exception {
      logger.debug("In ReflectionController:referenceData");
      ReflectionTransport reflect = (ReflectionTransport) command;

      Cell cell = reflect.getCell();
      
      if (reflect.getCell() == null){
         Id id = getIdManager().getId(request.getParameter("cell_id"));
         cell = getMatrixManager().getCell(id);
         reflect.setCell(cell);
      }

      if (currentPage == 6) {
         Map model = new HashMap();
         model.put("cell_id", reflect.getCell().getId());
         return model;
      }
      
      if (reflect.isUsingWizard() && currentPage > 2) {
         if (reflect.getActivePages() != null)
            reflect.setEffectivePage(reflect.getActivePages().size() + currentPage - 1);
         else
            reflect.setEffectivePage(0);
      } else {
         reflect.setEffectivePage(currentPage);
      }
      //if (currentPage == 1 && request.getParameterValues("selectedExpectations") == null) {
      //   reflect.setSelectedExpectations(null);
      //}
      setBeanProperties(reflect);

//    Traversing the collections to un-lazily load
      touchAllCollections(cell);
      reflect.setCell(cell);
      
      List nodeList = matrixManager.getCellArtifacts(cell);
      reflect.setCellArtifacts(nodeList);
      
      Map model = new HashMap();
      //model.put("bean", bean);
      model.put("cell_id", cell.getId());
      return model;
   }
   
   protected void touchAllCollections(Cell cell) {
      ScaffoldingCell sCell = cell.getScaffoldingCell();
      
      cell.getAttachments().size();
      for (Iterator iter = cell.getAttachments().iterator(); iter.hasNext();) {
         Attachment att = (Attachment) iter.next();
         att.getAttachmentCriteria().size();
      }
      
      cell.getReflection().getReflectionItems().size();
      
      cell.getReviewerItems().size();
      sCell.getExpectations().size();
      sCell.getCells().size();
      sCell.getReviewers().size();
      //sCell.getRootCriterion().getCriteria().size();
   }
   
   private void persist(ReflectionTransport reflect, String cellId) {
      Cell cell = this.getMatrixManager().getCell(idManager.getId(cellId));
      
      reflect.setCell(cell);
      Reflection reflection = new Reflection();
      if (reflect.getId()==null) {
         reflection = new Reflection(reflect);
         cell.setReflection(reflection);
      }
      else {
         reflection = this.getMatrixManager().getReflection(reflect.getId());
         reflection.copy(reflect);
      }
      this.getMatrixManager().store(reflection);
      reflect.copy(reflection);
   }
   
   protected boolean isFinish(HttpServletRequest request) {
      String dir = (String)request.getParameter("direction");
      
      if (dir != null && !"cancel".equals(dir) && !"next".equals(dir) && 
            !"previous".equals(dir)) {
         return true;
      }
      return false;      
   }
   
   protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
      
      if (logger.isDebugEnabled()) {
         logger.debug("In ReflectionController:getTargetPage()");
      }
      
      if (errors != null && errors.getErrorCount() > 0) return currentPage;

      ReflectionTransport reflect = (ReflectionTransport) command;
      if (currentPage == 0) {
         if (!reflect.isUsingWizard()) {
            reflect.setCurrentPage(4);
            return 4;
         } 
         else if(reflect.getCell().getScaffoldingCell().getExpectations().size()==0) {
            reflect.setActivePages(new ArrayList());
            reflect.setEffectivePage(0);
            reflect.setTotalPages(1);
            reflect.setCurrentPage(3);
            return 3;
         }
         else {
            reflect.setCurrentVirtualPage(0);
            reflect.setCurrentPage(1);
            return 1;
         }
      }

      int direction = 0;
      String dir = reflect.getDirection();
      if ("previous".equals(dir))
         direction = 1;
      else if ("next".equals(dir) || "continue".equals(dir))
         direction = 2;
      boolean isVirtual = (currentPage == this.getVirtualPageIndex());

      logger.debug("Current page is: " + currentPage + " and direction is: " + direction);

      if ("submit".equals(dir)) {
         if (currentPage != 5) {
            reflect.setCurrentPage(5);
            return 5;
         }
         else {
            getMatrixManager().submitCellForReview(reflect.getCell());
            reflect.setCurrentPage(6);
            return 6;  //returning to last page - a redirect
         }
      }

      switch (direction) {
         case 0: //"save"
            return currentPage;
         case 1: //"previous"
            if (isVirtual && !reflect.bof()) {
               reflect.previous();
               reflect.setCurrentPage(currentPage);
               return currentPage;
            } else {
               reflect.setCurrentPage(currentPage-1);
               return currentPage - 1;
            }
         case 2: //"next""
            if (isVirtual && !reflect.eof()) {
               reflect.next();
               reflect.setCurrentPage(currentPage);
               return currentPage;
            }
            reflect.setCurrentPage(currentPage+1);
            return currentPage + 1;
      }
      return -12345; //this should never execute
   }

   protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
      //binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
      
      for (Iterator i = getCustomTypedEditors().iterator(); i.hasNext();) {
         TypedPropertyEditor editor = (TypedPropertyEditor) i.next();
         binder.registerCustomEditor(editor.getType(), editor);
      }
   }

   protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command) throws Exception {
      ServletRequestDataBinder binder = null;
      binder = new ServletRequestBeanDataBinder(command, getCommandName());
      initBinder(request, binder);
      return binder;
   }
   
   protected void validatePage(Object o, Errors errors, int page) {
      logger.debug("validatePage()");
      MatrixValidator validator = (MatrixValidator) getValidator();
      ReflectionTransport reflection = (ReflectionTransport)o;
      
      if (page == 1 && reflection.getCurrentPage() == 1) 
         validator.validateReflectionCheckBoxes(o, errors);
      else if (page == 4 && reflection.getCurrentPage() == 4) 
         validator.validateReflectionSubmit(o, errors);
   }
   

   protected ModelAndView processFinish(HttpServletRequest request, 
         HttpServletResponse response, Object o, BindException e) throws Exception {
      if (logger.isDebugEnabled()) {
         logger.debug("In ReflectionController:processFinish()");
      }
      
      ReflectionTransport reflect = (ReflectionTransport) o;
      String cellId = (String)request.getParameter("cell_id");
      persist(reflect, cellId);
      
      int target = this.getTargetPage(request, o, null, this.getCurrentPage(request));
      logger.debug("Saving and going to page: " + target);
      return showPage(request, e, target);
   }

   protected ModelAndView processCancel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, BindException e) throws Exception {
      ReflectionTransport reflect = (ReflectionTransport) o;
      Id cellId = reflect.getCell().getId();
      return new ModelAndView("viewCellRedirect", "cell_id", cellId);
   }

   protected boolean isCancel(HttpServletRequest request) {
      String dir = request.getParameter("direction");
      if (dir != null && dir.equals("cancel"))
         return true;
      return false;
   }
   private List customTypedEditors;

   public List getCustomTypedEditors() {
      return customTypedEditors;
   }

   public void setCustomTypedEditors(List customTypedEditors) {
      this.customTypedEditors = customTypedEditors;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

}
