package org.theospi.portfolio.review.tool;

import java.io.IOException;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.theospi.portfolio.review.DecoratedReview;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.review.model.ReviewAttachment;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.tool.BaseFormResourceFilter;
import org.theospi.portfolio.shared.tool.HelperToolBase;

public class ReviewTool extends HelperToolBase {

   private DecoratedReview current = null;

   private ReviewManager reviewManager;
   
   
   public DecoratedReview getCurrent() {
      ToolSession session = SessionManager.getCurrentToolSession();

      if (session.getAttribute(ReviewManager.CURRENT_REVIEW_ID) != null) {
         String id = (String)session.getAttribute(ReviewManager.CURRENT_REVIEW_ID);
         current = new DecoratedReview(this, getReviewManager().getReview(id));
         session.removeAttribute(ReviewManager.CURRENT_REVIEW_ID);
      }
      else if (session.getAttribute(ReviewManager.CURRENT_REVIEW) != null) {
         current = new DecoratedReview(this,
               (Review)session.getAttribute(ReviewManager.CURRENT_REVIEW));
         session.removeAttribute(ReviewManager.CURRENT_REVIEW);
      }
      else if (current == null) 
      {
         Placement placement = ToolManager.getCurrentPlacement();
         String currentSite = placement.getContext();
         Review review = getReviewManager().createNew(SessionManager.getCurrentSessionUserId(), 
               "New Review", currentSite, null, "", ""); 
         current = new DecoratedReview(this, review);
      }
      
      if ((String)session.getAttribute(ReviewHelper.REVIEW_PARENT) != null) {
         String id = (String)session.getAttribute(ReviewHelper.REVIEW_PARENT);
         current.getBase().setParent(id);
         this.removeAttribute(ReviewHelper.REVIEW_PARENT);
      }
      
      setReviewFormId(current.getBase());

      return current;
   }
   
   
   public void setReviewFormId(Review review) {
      Id id = null;
      
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
         session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         //List newAttachments = new ArrayList();

         for(int i=0; i<refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            //Review review = current.getBase();
            Reference fullRef = getReviewManager().decorateReference(review, ref.getReference());
            Node node = getReviewManager().getNode(ref);
            id = node.getId();
            
            ReviewAttachment attachment = new ReviewAttachment(review,
                           ref, fullRef);

            review.setReviewAttachment(attachment);
            review.setReviewContent(id);
         }
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         //base.getAttachments().clear();
         //base.getAttachments().addAll(newAttachments);
      }
      //return id;
      //return base.getAttachments();
   }
   
   public String processActionChooseForm() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      
      BaseFormResourceFilter crf = new BaseFormResourceFilter();
      
      session.setAttribute(ReviewManager.CURRENT_REVIEW, getCurrent().getBase());
      
      crf.getFormTypes().add((String)getAttribute(ReviewHelper.REVIEW_FORM_TYPE));
      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, crf);
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, new Boolean(true).toString());
      
      try {
         context.redirect("sakai.filepicker.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      
      return null;
   }
   
   public String processActionEdit(Review review) {
      review = getReviewManager().getReview(review.getId());
      invokeTool(review);
      return null;
   }

   public String processActionDelete(Review review) {
      getReviewManager().deleteReview(review);
      current = null;
      return "list";
   }

   public String processActionView(Review review) {
      review = getReviewManager().getReview(review.getId());
      invokeToolView(review.getId().getValue());
      return null;
   }
   
   public String processActionCancel() {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(ReviewManager.CURRENT_REVIEW);
      session.removeAttribute(ReviewManager.CURRENT_REVIEW_ID);
      current = null;
      return returnToCaller();
   }

   public String processActionSave() {
      getReviewManager().saveReview(getCurrent().getBase());

      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(ReviewManager.CURRENT_REVIEW, getCurrent().getBase());

      return returnToCaller();
   }
   
   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }
   
   protected void invokeTool(Review review) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(ReviewManager.CURRENT_REVIEW, review);

      try {
         context.redirect("osp.review.helper/tool");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }

   /**
    * sample
    * @param id
    */
   protected void invokeToolView(String id) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();

      session.setAttribute(ReviewManager.CURRENT_REVIEW_ID, id);

      try {
         context.redirect("osp.review.helper/view");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
   }
   
}
