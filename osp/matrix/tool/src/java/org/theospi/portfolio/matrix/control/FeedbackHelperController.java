package org.theospi.portfolio.matrix.control;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.email.cover.EmailService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;


public class FeedbackHelperController implements Controller {

	private ServerConfigurationService serverConfigurationService;
	private MatrixManager matrixManager;
	private IdManager idManager = null;
	private ResourceLoader toolBundle;
	private Site site;
	private AuthorizationFacade authzManager;
	private SiteService siteService;
	private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	/** This accepts email addresses */
    private static final Pattern emailPattern = Pattern.compile(
          "^" +
             "(?>" +
                "\\.?[a-zA-Z\\d!#$%&'*+\\-/=?^_`{|}~]+" +
             ")+" + 
          "@" + 
             "(" +
                "(" +
                   "(?!-)[a-zA-Z\\d\\-]+(?<!-)\\." +
                ")+" +
                "[a-zA-Z]{2,}" +
             "|" +
                "(?!\\.)" +
                "(" +
                   "\\.?" +
                   "(" +
                      "25[0-5]" +
                   "|" +
                      "2[0-4]\\d" +
                   "|" +
                      "[01]?\\d?\\d" +
                   ")" +
                "){4}" +
             ")" +
          "$"
          );
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		if(session.get("feedbackMatrixCall") != null){
			if(session.get("feedbackCellId") != null){
				Cell cell = matrixManager.getCell(idManager.getId(session.get("feedbackCellId").toString()));
				setAudienceSelectionVariables(cell, session);
				session.remove("feedbackMatrixCall");
				session.remove("feedbackCellId");
				return new ModelAndView("inviteFeedback");	
			}
		}
		
		Map model = new HashMap();
		
		//this checks if the user requested feedback.  There are two return values: 1. inviteFeedbackReturn (user clicked cancel or just finish)
		//2. inviteFeedbackNotify (user clicked finish and notify button).  Both values return the cell id.  inviteFeedbackNotify needs to call another
		//helper to finish the notify part.
		if(request.get("inviteFeedbackReturn") != null || request.get("inviteFeedbackNotify") != null){
			if(request.get("inviteFeedbackReturn") != null){
				model.put("page_id", request.get("inviteFeedbackReturn"));
				model.put("feedbackReturn", request.get("inviteFeedbackReturn"));
				return new ModelAndView("viewCell", model);
			}else if(request.get("inviteFeedbackNotify") != null){
				//inviteFeedbackNotify is returned from FeedbackHelperController and is the Id of the wizardPage of the cell.
				Cell cell = matrixManager.getCellFromPage(idManager.getId(request.get("inviteFeedbackNotify").toString()));
				setAudienceSelectionVariables(cell, session);				
				return new ModelAndView("notifyAudience");
			}
		}
		
		if(session.get("submitForReview") != null){
			if(session.get("feedbackCellId") != null){
				Cell cell = matrixManager.getCell(idManager.getId(session.get("feedbackCellId").toString()));		
				session.remove("feedbackMatrixCall");
				session.remove("feedbackCellId");
				Id reviewObjectId = null;
				if(cell.getScaffoldingCell().isDefaultReviewers()){
					reviewObjectId = cell.getScaffoldingCell().getScaffolding().getId();
				}else{
					reviewObjectId = cell.getScaffoldingCell().getWizardPageDefinition().getId();
				}
				notifyAudience(cell, reviewObjectId);
				return new ModelAndView("viewScaffolding", "scaffolding_id", cell.getScaffoldingCell().getScaffolding().getId().getValue());	
			}			
		}
		
		return null;
	}
	
	protected Map setAudienceSelectionVariables(Cell cell, Map session) {
		String baseUrl = this.getServerConfigurationService().getServerUrl();
	//	String url =  baseUrl + "/osp-matrix-tool/viewCell.osp?page_id=" + cell.getWizardPage().getId().getValue();

		session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION, 
				AudienceSelectionHelper.AUDIENCE_FUNCTION_INVITE_FEEDBACK );

		String id = cell.getWizardPage().getId()!=null ? cell.getWizardPage().getId().getValue() : cell.getWizardPage().getNewId().getValue();
		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_SITE,cell.getWizardPage().getPageDefinition().getSiteId());

//		//session.put(AudienceSelectionHelper.AUDIENCE_PUBLIC_FLAG, cell.getIsPublic() ? "true" : "false");
//		session.put(AudienceSelectionHelper.AUDIENCE_PUBLIC_URL,  url);
//
		session.put(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET, "inviteFeedbackReturn=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET, "inviteFeedbackNotify=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET, "inviteFeedbackReturn=" + id);

		if(cell.getScaffoldingCell().isDefaultReviewers()){
			session.put(AudienceSelectionHelper.MATRIX_REVIEWER_OBJECT_ID, cell.getScaffoldingCell().getScaffolding().getId().getValue());
		}else{
			session.put(AudienceSelectionHelper.MATRIX_REVIEWER_OBJECT_ID, cell.getScaffoldingCell().getWizardPageDefinition().getId().getValue());
		}
		session.put(AudienceSelectionHelper.MATRIX_REVIEWER_FUNCTION, MatrixFunctionConstants.REVIEW_MATRIX);
		//
//		
//		session.put(AudienceSelectionHelper.AUDIENCE_SITE, cell.getWizardPage().getPageDefinition().getSiteId());
		
		//cleans up any previous context values
		session.remove(AudienceSelectionHelper.CONTEXT);
		session.remove(AudienceSelectionHelper.CONTEXT2);

		if(cell.getScaffoldingCell().getScaffolding() != null){ 
			session.put(AudienceSelectionHelper.CONTEXT,
					cell.getScaffoldingCell().getScaffolding().getTitle());
		}
		session.put(AudienceSelectionHelper.CONTEXT2,
				cell.getScaffoldingCell().getWizardPageDefinition().getTitle());
		
		return session;
	}
	
	protected List getMatrixReviewersList(Id reviewObjectId) {
		List returnList = new ArrayList();
		

		List evaluators = getAuthzManager().getAuthorizations(null,
				MatrixFunctionConstants.REVIEW_MATRIX, reviewObjectId);

		for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
			Authorization az = (Authorization) iter.next();
			Agent agent = az.getAgent();
			

			if (agent.isRole()) {
				returnList.add("ROLE" + "." + agent.getDisplayName());
		       } else {
		           try {
		               returnList.add(UserDirectoryService.getUserByEid(agent.getEid().toString()).getEmail());
		           }

		           catch (UserNotDefinedException e) {
		              	e.printStackTrace();
		           }
		       }			
		}

		return returnList;
	}
	
	
	   protected void notifyAudience(Cell cell, Id reviewObjectId){
	    	String url;
	    	String emailMessage = "";
	    	String subject = "";
	    	User user = UserDirectoryService.getCurrentUser();
	    	

	    	String id = cell.getWizardPage().getId()!=null ? cell.getWizardPage().getId().getValue() : cell.getWizardPage().getNewId().getValue();
	    	subject = myResources.getString("matrixFeedbackSubject");
	    	url = getServerConfigurationService().getServerUrl() +
	    	"/osp-matrix-tool/viewCell.osp?page_id=" + id;

	    	String context1 = "", context2;
	    	if(cell.getScaffoldingCell().getScaffolding() != null){ 
				context1 = cell.getScaffoldingCell().getScaffolding().getTitle();
			}
	    	context2 = cell.getScaffoldingCell().getWizardPageDefinition().getTitle();
	    	
	    	emailMessage = myResources.getFormattedMessage("matrixFeedbackBody", 
	    			new Object[]{user.getDisplayName()}) + " " + context1 + " - " + context2;


	    	try {

	    		String from = getServerConfigurationService().getString("setup.request", 
	    				"postmaster@".concat(getServerConfigurationService().getServerName()));

	    		//add all reviewers that are selected by the matrix creator
	    		List matrixReviewers = getMatrixReviewersList(reviewObjectId);

	    		String[] emailList = new String[matrixReviewers.size()];
	    		for(int i = 0; i < matrixReviewers.size(); i++){
	    			emailList[i] = matrixReviewers.get(i).toString();
	    		}
	    		
	    		List sentEmailAddrs = new ArrayList();

	    		for (int i = 0; i < emailList.length; i++) {
	    			String toUser = emailList[i];
	    			if (toUser.startsWith("ROLE")) {
	    				String role = toUser.substring(5, toUser.length());
	    				Set members = getSite(cell.getWizardPage().getPageDefinition().getSiteId()).getMembers();
	    				for (Iterator j = members.iterator(); j.hasNext();) {
	    					Member member = (Member) j.next();
	    					if (member.getRole().getId().equals(role)) {
	    						String email = UserDirectoryService.getUser(member.getUserId()).getEmail();
	    						if (validateEmail(email) && !sentEmailAddrs.contains(email)) {
	    							sentEmailAddrs.add(email);
	    							EmailService.send(from, email,
	    									subject, emailMessage, null, null, null);
	    						}
	    					}
	    				}

	    			} else {
	    				if (validateEmail(toUser) && !sentEmailAddrs.contains(toUser)) {
	    					sentEmailAddrs.add(toUser);
	    					EmailService.send(from, toUser,
	    							subject, emailMessage, null, null, null);
	    				}
	    			}
	    		}

	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	

	public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

	
	public Site getSite(String siteId) {
		if (site == null) {
			try {
				site = getSiteService().getSite(siteId);
			}
			catch (IdUnusedException e) {
				throw new RuntimeException(e);
			}
		}
		return site;
	}
	protected boolean validateEmail(String displayName)
	{
		if (!emailPattern.matcher(displayName).matches()) {
			return false;
		}

		return true;
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	
}
