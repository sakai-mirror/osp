/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

package org.theospi.portfolio.matrix.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.shared.model.EvaluationContentWrapper;

public class EvaluationContentWrapperForMatrixCell extends EvaluationContentWrapper{
   
   public EvaluationContentWrapperForMatrixCell(Id id, String title, Agent owner, Date submittedDate) throws UserNotDefinedException {
      setId(id);
      setTitle(title);
      setSubmittedDate(submittedDate);
      
      setOwner(UserDirectoryService.getUser(owner.getId().getValue()));
      setEvalType(Cell.TYPE);
      
      setUrl("viewCell.osp");
      
      Set params = new HashSet();
      
      params.add(new ParamBean("page_id", getId().getValue()));
      params.add(new ParamBean("readOnlyMatrix", "true"));
      params.add(new ParamBean("view_user", owner.getId().getValue()));
      setUrlParams(params);      
   }
}
