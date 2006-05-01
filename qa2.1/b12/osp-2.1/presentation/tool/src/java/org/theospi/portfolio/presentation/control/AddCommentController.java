/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.presentation.control;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.security.AuthorizationFacade;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 1, 2004
 * Time: 10:52:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class AddCommentController extends AbstractPresentationController {

   private AuthorizationFacade authzManager = null;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {

      PresentationComment comment = (PresentationComment) requestModel;

      if (comment.getTitle() == null || comment.getTitle().length() == 0) {
         errors.rejectValue("title", "required", "required");
      }

      if (comment.getComment() == null || comment.getComment().length() == 0) {
         errors.rejectValue("comment", "required", "required");
      }

      request.put(BindException.ERROR_KEY_PREFIX + "newComment",
         errors);

      if (!errors.hasErrors()) {
         getPresentationManager().createComment((PresentationComment) requestModel);
      }

      return new ModelAndView("success", "id", comment.getPresentation().getId());
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }
}
