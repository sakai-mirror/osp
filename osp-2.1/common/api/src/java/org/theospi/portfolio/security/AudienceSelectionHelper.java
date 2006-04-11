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
package org.theospi.portfolio.security;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 4:12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AudienceSelectionHelper {

   public static final String AUDIENCE_FUNCTION =
         "org.theospi.portfolio.security.audienceFunction";

   public static final String AUDIENCE_QUALIFIER =
         "org.theospi.portfolio.security.audienceQualifier";

   public static final String AUDIENCE_INSTRUCTIONS =
         "org.theospi.portfolio.security.audienceInstructions";

   public static final String AUDIENCE_GLOBAL_TITLE =
         "org.theospi.portfolio.security.audienceGlobalTitle";

   public static final String AUDIENCE_INDIVIDUAL_TITLE =
         "org.theospi.portfolio.security.audienceIndTitle";

   public static final String AUDIENCE_GROUP_TITLE =
         "org.theospi.portfolio.security.audienceGroupTitle";

   public static final String AUDIENCE_PUBLIC_FLAG =
         "org.theospi.portfolio.security.audiencePublic";

   public static final String AUDIENCE_PUBLIC_TITLE =
         "org.theospi.portfolio.security.audiencePublicTitle";

   public static final String AUDIENCE_SELECTED_TITLE =
         "org.theospi.portfolio.security.audienceSelectedTitle";

   /** Tells the audience helper if the user can find users by email  */
   public static final String AUDIENCE_GUEST_EMAIL =
         "org.theospi.portfolio.security.audienceGuestEmail";

   public static final String AUDIENCE_WORKSITE_LIMITED =
         "org.theospi.portfolio.security.audienceWorksiteLimited";

   public static final String AUDIENCE_FILTER_INSTRUCTIONS =
         "org.theospi.portfolio.security.audienceFilterInstructions";

}
