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
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.exception.IdUnusedException;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardStyleItem;
import org.theospi.portfolio.wizard.model.WizardSupportItem;
import org.theospi.portfolio.shared.tool.BuilderTool;
import org.theospi.portfolio.shared.tool.BuilderScreen;
import org.theospi.portfolio.shared.model.OspException;

public class WizardTool extends BuilderTool {

   private WizardManager wizardManager;
   private GuidanceManager guidanceManager;
   private AuthorizationFacade authzManager;
   private IdManager idManager;
   private DecoratedWizard current = null;
   private String commentItem;
   private String reflectionItem;
   private String evaluationItem;
   private String expandedGuidanceSection = "false";
   private List wizardTypes = null;
   private DecoratedCategory currentCategory;
   private DecoratedCategoryChild moveCategoryChild;
   private List deletedItems = new ArrayList();
   private int nextWizard = 0;

   public final static String LIST_PAGE = "listWizards";
   public final static String EDIT_PAGE = "editWizard";
   public final static String EDIT_PAGES_PAGE = "editWizardPages";
   public final static String EDIT_SUPPORT_PAGE = "editWizardSupport";
   public final static String EDIT_DESIGN_PAGE = "editWizardDesign";
   public final static String EDIT_PROPERTIES_PAGE = "editWizardProperties";

   public final static String FORM_TYPE = "form";
   public final static String VALUE_SEPARATOR = ":";
   
   final public static int ID_INDEX = 0;
   final public static int TYPE_INDEX = 1;
   final public static int ITEM_ID_INDEX = 2;

   private BuilderScreen[] screens = {
      new BuilderScreen(EDIT_PAGE),
      new BuilderScreen(EDIT_PAGES_PAGE),
      new BuilderScreen(EDIT_SUPPORT_PAGE),
      new BuilderScreen(EDIT_DESIGN_PAGE),
      new BuilderScreen(EDIT_PROPERTIES_PAGE)
      };


   public WizardTool() {
      setScreens(screens);
   }

   protected void saveScreen(BuilderScreen screen) {
      processActionSave(screen.getNavigationKey());
   }

   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
   
   public DecoratedWizard getCurrent() {
      ToolSession session = SessionManager.getCurrentToolSession();

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
         if (type.equals(WizardFunctionConstants.COMMENT_TYPE))            
            this.setCommentItem(id);
         else if (type.equals(WizardFunctionConstants.REFLECTION_TYPE))
            this.setReflectionItem(id);
         else  //it's an evaluation
            this.setEvaluationItem(id);
      }
      
      if (wizard.getExposedPageId() != null && !wizard.getExposedPageId().equals("") &&
            (wizard.getExposeAsTool() == null || wizard.getExposeAsTool().booleanValue())) {
         wizard.setExposeAsTool(new Boolean(true));
      }

      return current;
   }

   public void setCurrent(DecoratedWizard current) {
      this.current = current;
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

      DecoratedWizard lastWizard = null;

      for (Iterator i=wizards.iterator();i.hasNext();) {
         Wizard wizard = (Wizard)i.next();
         DecoratedWizard current = new DecoratedWizard(this, wizard);
         returned.add(current);
         if (lastWizard != null) {
            lastWizard.setNext(current);
            current.setPrev(lastWizard);
         }
         lastWizard = current;
      }

      if (lastWizard != null) {
         setNextWizard(lastWizard.getBase().getSequence() + 1);
      }

      return returned;
   }
   
   public String processActionEdit(Wizard wizard) {
      wizard = getWizardManager().getWizard(wizard.getId());
      setCurrent(new DecoratedWizard(this, wizard));
      return startBuilder();
   }

   public String processActionDelete(Wizard wizard) {
      getWizardManager().deleteWizard(wizard);
      current = null;
      return LIST_PAGE;
   }
   
   public String processActionCancel() {
      setCurrent(null);
      return LIST_PAGE;
   }
   
   protected Id cleanBlankId(String id) {
      if (id.equals("")) return null;
      return getIdManager().getId(id);
   }

   public String processActionSaveFinished() {
      processActionSave(getCurrentScreen().getNavigationKey());
      return LIST_PAGE;
   }

   protected void processActionSave(String currentView) {
      if (currentView.equals(EDIT_PAGE) && getCurrent().getBase().getType().equals(Wizard.WIZARD_TYPE_SEQUENTIAL)) {
         boolean foundOne = false;
         List pageList = getCurrent().getRootCategory().getBase().getChildPages();
         List decoratedPageList = getCurrent().getRootCategory().getCategoryPageList();
         for (Iterator i=decoratedPageList.iterator();i.hasNext();) {
            DecoratedWizardPage page = (DecoratedWizardPage) i.next();
            if (!pageList.contains(page.getBase())) {
               pageList.add(page.getBase());
               page.getBase().setCategory(getCurrent().getRootCategory().getBase());
               foundOne = true;
            }
         }
         if (foundOne) {
            getCurrent().getRootCategory().resequencePages();
         }
      }
      getWizardManager().deleteObjects(deletedItems);
      deletedItems.clear();
      Wizard wizard = getCurrent().getBase();
      Set items = new HashSet();
      
      if (getCommentItem() != null && !getCommentItem().equals("")) {
         String[] comment = getCommentItem().split(VALUE_SEPARATOR);
         items.add(new WizardSupportItem(cleanBlankId(comment[ID_INDEX]),
               getIdManager().getId(comment[ITEM_ID_INDEX]),
               WizardFunctionConstants.COMMENT_TYPE, comment[TYPE_INDEX], wizard));
      }         
      if (getReflectionItem() != null && !getReflectionItem().equals("")) {
         String[] reflection = getReflectionItem().split(VALUE_SEPARATOR);
         items.add(new WizardSupportItem(cleanBlankId(reflection[ID_INDEX]),
               getIdManager().getId(reflection[ITEM_ID_INDEX]), 
               WizardFunctionConstants.REFLECTION_TYPE, reflection[TYPE_INDEX], wizard));
      }
      if (getEvaluationItem() != null && !getEvaluationItem().equals("")) {
         String[] evaluation = getEvaluationItem().split(VALUE_SEPARATOR);
         items.add(new WizardSupportItem(cleanBlankId(evaluation[ID_INDEX]),
               getIdManager().getId(evaluation[ITEM_ID_INDEX]), 
               WizardFunctionConstants.EVALUATION_TYPE, evaluation[TYPE_INDEX], wizard));
      }
      //this.getCommentItem()
      wizard.setSupportItems(items);
      getWizardManager().saveWizard(wizard);
   }
   
   public String processActionNew() {
      Wizard newWizard = getWizardManager().createNew();

      newWizard.setSequence(getNextWizard());
      
      setCurrent(new DecoratedWizard(this, newWizard));

      return startBuilder();
   }
   
   public String processActionRemoveGuidance() {
      //Placement placement = ToolManager.getCurrentPlacement();
      //String currentSite = placement.getContext();
      Wizard wizard = getCurrent().getBase();
      getGuidanceManager().deleteGuidance(wizard.getGuidance());
      wizard.setGuidance(null);
      //session.setAttribute(WizardManager.CURRENT_WIZARD, getCurrent().getBase());

      return getCurrentScreen().getNavigationKey();
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
   
   public void processActionAudienceHelper() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      //Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();
      
      //Placement placement = ToolManager.getCurrentPlacement();  
      //String currentSite = placement.getContext();  
      Wizard wizard = getCurrent().getBase();

      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FUNCTION, 
            WizardFunctionConstants.REVIEW_WIZARD);  
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_QUALIFIER, 
            wizard.getId().getValue());
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_INSTRUCTIONS, 
            getMessageFromBundle("audience_instructions"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GLOBAL_TITLE, 
            getMessageFromBundle("audience_global_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_INDIVIDUAL_TITLE, 
            getMessageFromBundle("audience_individual_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GROUP_TITLE, 
            getMessageFromBundle("audience_group_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, "false");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_PUBLIC_TITLE, 
            null);
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_SELECTED_TITLE, 
            getMessageFromBundle("audience_selected_title"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_FILTER_INSTRUCTIONS, 
            getMessageFromBundle("audience_filter_instructions"));
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_GUEST_EMAIL, "false");
      session.setAttribute(AudienceSelectionHelper.AUDIENCE_WORKSITE_LIMITED, "true");
      
      //Guidance guidance = wizard.getGuidance();
      //if (guidance == null) {
      //   guidance = getGuidanceManager().createNew(wizard.getName() + " Guidance", currentSite, null, "", ""); 
      //}
      
      //session.setAttribute(GuidanceManager.CURRENT_GUIDANCE, guidance);  

      try {
         context.redirect("osp.audience.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   public boolean isMaintainer() {
      return new Boolean(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
         getIdManager().getId(PortalService.getCurrentSiteId()))).booleanValue();
   }

   public String processPermissions()
   {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

       //todo userCan = null;

       try {
           context.redirect("sakai.permissions.helper.helper/tool?" +
                 "session.sakaiproject.permissions.description=" +
                    getPermissionsMessage() +
                 "&session.sakaiproject.permissions.siteRef=" +
                    getWorksite().getReference() +
                 "&session.sakaiproject.permissions.prefix=" +
                    WizardFunctionConstants.WIZARD_PREFIX);
       }
       catch (IOException e) {
           throw new RuntimeException("Failed to redirect to helper", e);
       }
       return null;
   }

   public String getPermissionsMessage() {
      return getMessageFromBundle("perm_description", new Object[]{
         getTool().getTitle(), getWorksite().getTitle()});
   }

   public Tool getTool() {
      return ToolManager.getCurrentTool();
   }

   public Site getWorksite() {
      try {
         return SiteService.getSite(PortalService.getCurrentSiteId());
      }
      catch (IdUnusedException e) {
         throw new OspException(e);
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
         WizardStyleItem wsItem = (WizardStyleItem)i.next();
         wsItemRefs.add(wsItem.getBaseReference().getBase());
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
         retForms.add(createSelect(id, sad.getDescription()));
      }
      
      return retForms;
   }
   
   public Collection getCommentFormsForSelect() {
      return getFormsForSelect(WizardFunctionConstants.COMMENT_TYPE, getCommentItem());      
   }
   
   public Collection getReflectionFormsForSelect() {
      return getFormsForSelect(WizardFunctionConstants.REFLECTION_TYPE, getReflectionItem());
   }
   
   public Collection getEvaluationFormsForSelect() {
      return getFormsForSelect(WizardFunctionConstants.EVALUATION_TYPE, getEvaluationItem());
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
         String id = VALUE_SEPARATOR + wizard.getType() + VALUE_SEPARATOR + wizard.getId().getValue();
         if (selectedId != null && selectedId.endsWith(id))
            id = selectedId;
         retWizards.add(createSelect(id, wizard.getName()));
      }
      
      return retWizards;
   }
   
   public Collection getCommentWizardsForSelect() {
      return getWizardsForSelect(WizardFunctionConstants.COMMENT_TYPE, getCommentItem());
   }
   
   public Collection getReflectionWizardsForSelect() {
      return getWizardsForSelect(WizardFunctionConstants.REFLECTION_TYPE, getReflectionItem());
   }
   
   public Collection getEvaluationWizardsForSelect() {
      return getWizardsForSelect(WizardFunctionConstants.EVALUATION_TYPE, getEvaluationItem());
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

   public List getWizardTypes() {
      if (wizardTypes == null) {
         wizardTypes = new ArrayList();
         wizardTypes.add(createSelect(Wizard.WIZARD_TYPE_SEQUENTIAL,
               getMessageFromBundle(Wizard.WIZARD_TYPE_SEQUENTIAL)));
         wizardTypes.add(createSelect(Wizard.WIZARD_TYPE_HIERARCHICAL,
               getMessageFromBundle(Wizard.WIZARD_TYPE_HIERARCHICAL)));
      }
      return wizardTypes;
   }

   public void setWizardTypes(List wizardTypes) {
      this.wizardTypes = wizardTypes;
   }

   public DecoratedCategory getCurrentCategory() {
      return currentCategory;
   }

   public void setCurrentCategory(DecoratedCategory currentCategory) {
      this.currentCategory = currentCategory;
   }

   public DecoratedCategoryChild getMoveCategoryChild() {
      return moveCategoryChild;
   }

   public void setMoveCategoryChild(DecoratedCategoryChild moveCategoryChild) {
      this.moveCategoryChild = moveCategoryChild;
   }

   public boolean isMoving() {
      return getMoveCategoryChild() != null;
   }

   public List getDeletedItems() {
      return deletedItems;
   }

   public void setDeletedItems(List deletedItems) {
      this.deletedItems = deletedItems;
   }

   public String getMovingInstructions() {
      String key = null;

      if (getMoveCategoryChild() == null) {
         return null;
      }

      if (getMoveCategoryChild().isCategory()) {
         key = "move_category_instructions";
      }
      else {
         key = "move_page_instructions";
      }

      return getMessageFromBundle(key, new Object[]{getMoveCategoryChild().getTitle()});
   }

   public int getNextWizard() {
      return nextWizard;
   }

   public void setNextWizard(int nextWizard) {
      this.nextWizard = nextWizard;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }
}
