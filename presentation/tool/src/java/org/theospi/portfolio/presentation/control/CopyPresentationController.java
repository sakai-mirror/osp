/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/HidePresentationController.java $
 * $Id: HidePresentationController.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.theospi.portfolio.presentation.control;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * This triggers the presentation copy function
 * http://jira.sakaiproject.org/browse/SAK-17351
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class CopyPresentationController extends ListPresentationController {
    protected final static Log logger = LogFactory.getLog(CopyPresentationController.class);

    /* (non-Javadoc)
     * @see org.theospi.portfolio.presentation.control.ListPresentationController#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
     */
    public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
        String id = (String) request.get("id");

        // Agent current = getAuthManager().getAgent();
        // getPresentationManager();
        logger.info("Copy activated for presentation: "+id);
        // TODO actually do the copy, I'm sure this is the easy part... -AZ

        return super.handleRequest(requestModel, request, session, application, errors);
    }

}
