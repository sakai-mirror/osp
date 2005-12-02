package org.theospi.portfolio.wizard.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardSupportItem;

public class WizardTool {

   private WizardManager wizardManager;
   private GuidanceManager guidanceManager;
   private IdManager idManager;
   private DecoratedWizard current = null;
   private String commentItem;
   private String reflectionItem;
   private String evaluationItem;
   private String expandedGuidanceSection = "false";

   public final static String LIST_PAGE = "listWizards";
   public final static String EDIT_PAGE = "editWizard";
   public final static String EDIT_SUPPORT_PAGE = "editWizardSupport";
   public final static String EDIT_DESIGN_PAGE = "editWizardDesign";
   public final static String EDIT_PROPERTIES_PAGE = "editWizardProperties";
   public final static String COMMENT_TYPE = "comment";
   public final static String REFLECTION_TYPE = "reflection";
   public final static String EVALUATION_TYPE = "evaluation";
   
   public final static String FORM_TYPE = "form";
   public final static String VALUE_SEPARATOR = ":";
   
   final public static int ID_INDEX = 0;
   final public static int TYPE_INDEX = 1;
   final public static int ITEM_ID_INDEX = 2;
   
   
   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
   
   public DecoratedWizard getCurrent() {
      ToolSession session = SessionManager.getCurrentToolSession();
      
      if (session.getAttribute(WizardManager.CURRENT_WIZARD_ID) != null) {
         String id = (String)session.getAttribute(WizardManager.CURRENT_WIZARD_ID);
         current = new DecoratedWizard(this, getWizardManager().getWizard(id));
         session.removeAttribute(WizardManager.CURRENT_WIZARD_ID);
      }
      else if (session.getAttribute(WizardManager.CURRENT_WIZARD) != null) {
         current = new DecoratedWizard(this,
               (Wizard)session.getAttribute(WizardManager.CURRENT_WIZARD));
         session.removeAttribute(WizardManager.CURRENT_WIZARD);
      }
      
      Wizard wizard = current.getBase();
      
      if (session.getAttribute(GuidanceManager.CURRENT_GUIDANCE) != null) {
         Guidance guidance = (Guidance)session.getAttribute(GuidanceManager.CURRENT_GUIDANCE);
         wizard.setGuidanceId(guidance.getId());
         
         session.removeAttribute(GuidanceManager.CURRENT_GUIDANCE);
         setExpandedGuidanceSection("true");
      }
      if (wizard.getGuidanceId() != null && wizard.getGuidance() == null) {
         wizard.setGuidance(getGuidanceManager().getGuidance(wizard.getGuidanceId()));
      }
      
      for (Iterator iter = wizard.getSupportItems().iterator(); iter.hasNext();) {
         WizardSupportItem wsi = (WizardSupportItem)iter.next();
         String type = wsi.getGenericType();
         String id = wsi.getId().getValue() + VALUE_SEPARATOR + wsi.getContentType() + VALUE_SEPARATOR + wsi.getItem().getValue();
         if (type.equals(COMMENT_TYPE))            
            this.setCommentItem(id);
         else if (type.equals(REFLECTION_TYPE))
            this.setReflectionItem(id);
         else  //it's an evaluation
            this.setEvaluationItem(id);
      }
      
      if (wizard.getExposedPageId() != null && !wizard.getExposedPageId().equals("")) {
         wizard.setExposeAsTool(true);
      }

      return current;
   }
   
   public Reference decorateReference(String reference) {
      return getWizardManager().decorateReference(getCurrent().getBase(), reference);
   }
   
   public List getWizards() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      List returned = new ArrayList();
      List wizards = getWizardManager().listAllWizards(
            SessionManager.getCurrentSessionUserId(), currentSiteId);
      
      for (Iterator i=wizards.iterator();i.hasNext();) {
         Wizard wizard = (Wizard)i.next();
         returned.add(new DecoratedWizard(this, wizard));
      }
      return returned;
   }
   
   public String processActionEdit(Wizard wizard) {
      wizard = getWizardManager().getWizard(wizard.getId());
      invokeTool(wizard);
      return null;
   }

   public String processActionDelete(Wizard wizard) {
      getWizardManager().deleteWizard(wizard);
      current = null;
      return "list";
   }
   
   public String processActionCancel() {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(WizardManager.CURRENT_WIZARD);
      session.removeAttribute(WizardManager.CURRENT_WIZARD_ID);
      current = null;
      return goToPage(LIST_PAGE);
   }
   
   public String processActionGoToEditWizardSupport() {
      return processActionSave(EDIT_SUPPORT_PAGE);
   }
   
   public String processActionGoToEditWizardDesign() {
      return processActionSave(EDIT_DESIGN_PAGE);
   }

   public String processActionGoToEditWizardProperties() {
      return processActionSave(EDIT_PROPERTIES_PAGE);
   }
   
   public String processActionSave() {
      return processActionSave(LIST_PAGE);
   }
   
   protected Id cleanBlankId(String id) {
      if (id.equals("")) return null;
      return getIdManager().getId(id);
   }
   
   protected String processActionSave(String nextView) {
      Wizard wizard = getCurrent().getBase();
      Set items = new HashSet();
      
      if (getCommentItem() != null && !getCommentItem().equals("")) {
         String[] comment = getCommentItem().split(VALUE_SEPARATOR);
         items.add(new WizardSupportItem(cleanBlankId(comment[ID_INDEX]),
               getIdManager().getId(comment[ITEM_ID_INDEX]),
               COMMENT_TYPE, comment[TYPE_INDEX], wizard));
      }         
      if (getReflectionItem() != null && !getReflectionItem().equals("")) {
         String[] reflection = getReflectionItem().split(VALUE_SEPARATOR);
         items.add(new WizardSupportItem(cleanBlankId(reflection[ID_INDEX]),
               getIdManager().getId(reflection[ITEM_ID_INDEX]), 
               REFLECTION_TYPE, reflection[TYPE_INDEX], wizard));
      }
      if (getEvaluationItem() != null && !getEvaluationItem().equals("")) {
         String[] evaluation = getEvaluationItem().split(VALUE_SEPARATOR);
         items.add(new WizardSupportItem(cleanBlankId(evaluation[ID_INDEX]),
               getIdManager().getId(evaluation[ITEM_ID_INDEX]), 
               EVALUATION_TYPE, evaluation[TYPE_INDEX], wizard));
      }
      //this.getCommentItem()
      wizard.setSupportItems(items);
      getWizardManager().saveWizard(wizard);

      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(WizardManager.CURRENT_WIZARD, getCurrent().getBase());

      return goToPage(nextView);
   }
   
   protected String goToPage(String nextPage) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      try {
         context.redirect(nextPage);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to page", e);
      }
      return null;
   }

   public String processActionNew() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      Wizard newWizard = getWizardManager().createNew(SessionManager.getCurrentSessionUserId(), currentSite, null, "", "");

      invokeTool(newWizard);

      return null;
   }
   
   public String processActionRemoveGuidance() {
      //Placement placement = ToolManager.getCurrentPlacement();
      //String currentSite = placement.getContext();
      Wizard wizard = getCurrent().getBase();
      getGuidanceManager().deleteGuidance(wizard.getGuidance());
      wizard.setGuidance(null);
      //session.setAttribute(WizardManager.CURRENT_WIZARD, getCurrent().getBase());
      goToPage(EDIT_SUPPORT_PAGE);

      return null;
   }
   
   protected void invokeTool(Wizard wizard) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(WizardManager.CURRENT_WIZARD, wizard);

      try {
         context.redirect(EDIT_PAGE);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }


   public void processActionGuidanceHelper() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      //Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();
      
      Placement placement = ToolManager.getCurrentPlacement();  
      String currentSite = placement.getContext();  
      //session.setAttribute(tool.getId() + Tool.HELPER_DONE_URL, "");
      //session.setAttribute(WizardManager.CURRENT_WIZARD_ID, getCurrent().getBase().getId());
      Wizard wizard = getCurrent().getBase();
      session.setAttribute(WizardManager.CURRENT_WIZARD, wizard);
      
      Guidance guidance = wizard.getGuidance();
      if (guidance == null) {
         guidance = getGuidanceManager().createNew(wizard.getName() + " Guidance", currentSite, null, "", ""); 
      }
      
      session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);  

      try {
         context.redirect("osp.guidance.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }
   
   public String processActionManageStyle() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, new Boolean(true).toString());
      //session.setAttribute(GuidanceTool.ATTACHMENT_TYPE, type);
      
      //getCurrent().getBase().getStyle()
      
      Wizard wizard = getCurrent().getBase();

      //WizardStyleItem wsItem = wizard.getWizardStyleItem();
      
      List wsItems = wizard.getWizardStyleItems();
      List wsItemRefs = EntityManager.newReferenceList();

      for (Iterator i=wsItems.iterator();i.hasNext();) {
         GuidanceItemAttachment attachment = (GuidanceItemAttachment)i.next();
         wsItemRefs.add(attachment.getBaseReference().getBase());
      }

      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, wsItemRefs);
      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
            ComponentManager.get("org.sakaiproject.service.legacy.content.ContentResourceFilter.wizardStyleFile"));

      try {
         context.redirect("sakai.filepicker.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }
   
   
   protected Collection getFormsForSelect(String type, String selectedId) {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      Collection commentForms = 
               getWizardManager().getAvailableForms(currentSiteId, type);
      
      List retForms = new ArrayList();
      for(Iterator iter = commentForms.iterator(); iter.hasNext();) {
         //Artifact art = (Artifact)iter.next();
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next(); 
         //String type = art.getHome().getType().getId().getValue();
         
         String id = VALUE_SEPARATOR + FORM_TYPE + VALUE_SEPARATOR + sad.getId().getValue();
         if (selectedId != null && selectedId.endsWith(id))
            id = selectedId;
         retForms.add(new SelectItem(id, sad.getDescription()));
      }
      
      return retForms;
   }
   
   public Collection getCommentFormsForSelect() {
      return getFormsForSelect(COMMENT_TYPE, getCommentItem());      
   }
   
   public Collection getReflectionFormsForSelect() {
      return getFormsForSelect(REFLECTION_TYPE, getReflectionItem());
   }
   
   public Collection getEvaluationFormsForSelect() {
      return getFormsForSelect(EVALUATION_TYPE, getEvaluationItem());
   }
   
   protected Collection getWizardsForSelect(String type, String selectedId) {
      //TODO is only here just in case we decide to give wizards types
      // The type isn't being used yet
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      List wizards = getWizardManager().listWizardsByType(
            SessionManager.getCurrentSessionUserId(), currentSiteId, type);
      List retWizards = new ArrayList();
      for(Iterator iter = wizards.iterator(); iter.hasNext();) {
         Wizard wizard = (Wizard)iter.next();
         String id = VALUE_SEPARATOR + Wizard.WIZARD_TYPE + VALUE_SEPARATOR + wizard.getId().getValue();
         if (selectedId != null && selectedId.endsWith(id))
            id = selectedId;
         retWizards.add(new SelectItem(id, wizard.getName()));
      }
      
      return retWizards;
   }
   
   public Collection getCommentWizardsForSelect() {
      return getWizardsForSelect(COMMENT_TYPE, getCommentItem());
   }
   
   public Collection getReflectionWizardsForSelect() {
      return getWizardsForSelect(REFLECTION_TYPE, getReflectionItem());
   }
   
   public Collection getEvaluationWizardsForSelect() {
      return getWizardsForSelect(EVALUATION_TYPE, getEvaluationItem());
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   public String getCommentItem() {
      return commentItem;
   }

   public void setCommentItem(String commentItem) {
      this.commentItem = commentItem;
   }

   public String getEvaluationItem() {
      return evaluationItem;
   }

   public void setEvaluationItem(String evaluationItem) {
      this.evaluationItem = evaluationItem;
   }

   public String getReflectionItem() {
      return reflectionItem;
   }

   public void setReflectionItem(String reflectionItem) {
      this.reflectionItem = reflectionItem;
   }

   public String getExpandedGuidanceSection() {
      return expandedGuidanceSection;
   }

   public void setExpandedGuidanceSection(String expandedGuidanceSection) {
      this.expandedGuidanceSection = expandedGuidanceSection;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}
