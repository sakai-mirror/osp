package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.shared.tool.BaseFormResourceFilter;

public class CellFormPickerController implements FormController, LoadObjectController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private SessionManager sessionManager;
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   
   public Map referenceData(Map request, Object command, Errors errors) {
      

      ToolSession session = getSessionManager().getCurrentToolSession();
      String cellId = (String) request.get("cell_id");
      if (cellId == null) {
         cellId = (String)session.getAttribute("cell_id");
      }
      Cell cell = getMatrixManager().getCell(getIdManager().getId(cellId));
      
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         cell.getCellForms().clear();
         for (Iterator iter = refs.iterator(); iter.hasNext();) {
            Reference ref = (Reference) iter.next();
            String strId = getMatrixManager().getNode(ref).getId().getValue();
            cell.getCellForms().add(strId);
         }
         getMatrixManager().storeCell(cell);
         
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      }
      return null;
   }

   public Object fillBackingObject(Object incomingModel, Map request,
         Map session, Map application) throws Exception {
      
      //ToolSession session = getSessionManager().getCurrentToolSession();
      String cellId = (String) request.get("cell_id");
      if (cellId == null) {
         cellId = (String)session.get("cell_id");
      }
      Cell cell = getMatrixManager().getCell(getIdManager().getId(cellId));
      
      if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         cell.getCellForms().clear();
         for (Iterator iter = refs.iterator(); iter.hasNext();) {
            Reference ref = (Reference) iter.next();
            Id id = getMatrixManager().getNode(ref).getId();
            WizardPageForm wpf = new WizardPageForm();
            wpf.setArtifactId(id);
            wpf.setFormType(ref.getProperties().getProperty(
                  ref.getProperties().getNamePropStructObjType()));
            wpf.setWizardPage(cell.getWizardPage());
            cell.getCellForms().add(wpf);
         }
         getMatrixManager().storeCell(cell);
         
         session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
      }
      return null;
   }
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String attachFormAction = (String) request.get("attachFormAction");
      String cellId = (String) request.get("cell_id");
      if (cellId == null) {
         cellId = (String)session.get("cell_id");
         session.remove("cell_id");
      }
      Cell cell = getMatrixManager().getCell(getIdManager().getId(cellId));
      
      if (attachFormAction != null) {
         //session.setAttribute(TEMPLATE_PICKER, request.getParameter("pickerField"));
         //session.setAttribute("SessionPresentationTemplate", template);
         //session.setAttribute(STARTING_PAGE, request.getParameter("returnPage"));
         
         List files = new ArrayList();
         
         //String pickField = (String)request.get("formType");
         String id = "";
         for (Iterator iter = cell.getCellForms().iterator(); iter.hasNext();) {
            WizardPageForm wpf = (WizardPageForm) iter.next();
            if (wpf.getFormType().equals(attachFormAction)) {
               id = getContentHosting().resolveUuid(wpf.getArtifactId().getValue());
               Reference ref;
               try {
                  ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
                  files.add(ref);        
               } catch (PermissionException e) {
                  logger.error("", e);
               } catch (IdUnusedException e) {
                  logger.error("", e);
               } catch (TypeException e) {
                  logger.error("", e);               
               }
            }
         }
         BaseFormResourceFilter crf = new BaseFormResourceFilter();
         
         crf.getFormTypes().add(attachFormAction);
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, crf);
         session.put("cell_id", cellId);
         session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         return new ModelAndView("formPicker");
         
      }

      return new ModelAndView("cell", "cell_id", cellId);
   }

   /**
    * @return Returns the contentHosting.
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting The contentHosting to set.
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return Returns the entityManager.
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager The entityManager to set.
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @return Returns the sessionManager.
    */
   public SessionManager getSessionManager() {
      return sessionManager;
   }

   /**
    * @param sessionManager The sessionManager to set.
    */
   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

}
