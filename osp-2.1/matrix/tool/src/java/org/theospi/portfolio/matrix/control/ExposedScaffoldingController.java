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

package org.theospi.portfolio.matrix.control;

import java.util.Map;

import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;

public class ExposedScaffoldingController implements FormController {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   
   
   private void removeTool(Scaffolding scaffolding) {
      String siteId = scaffolding.getWorksiteId().getValue();
      try {
         Site siteEdit = SiteService.getSite(siteId);

         SitePage page = siteEdit.getPage(scaffolding.getExposedPageId());
         siteEdit.removePage(page);
         SiteService.save(siteEdit);
         scaffolding.setExposedPageId(null);
      } catch (IdUnusedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void addTool(Scaffolding scaffolding) {
      //TODO add logging errors back
      String siteId = scaffolding.getWorksiteId().getValue();
      try {
         Site siteEdit = SiteService.getSite(siteId);


         SitePage page = siteEdit.addPage();

         page.setTitle(scaffolding.getTitle());
         page.setLayout(SitePage.LAYOUT_SINGLE_COL);

         ToolConfiguration tool = page.addTool();
         tool.setTool(ToolManager.getTool("osp.exposedmatrix"));
         tool.setTitle(scaffolding.getTitle());
         tool.setLayoutHints("0,0");
         tool.getPlacementConfig().setProperty(MatrixManager.EXPOSED_MATRIX_KEY, scaffolding.getId().getValue());

         //LOG.info(this+": SiteService.commitEdit():" +siteId);

         SiteService.save(siteEdit);
         scaffolding.setExposedPageId(page.getId());


      } catch (IdUnusedException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      // TODO Auto-generated method stub
      return null;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Id scaffoldingId = getIdManager().getId((String)request.get("scaffolding_id"));
      String expose = (String)request.get("expose");
      Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);
      if (expose.equals("true") &&
            scaffolding.getExposedPageId() == null) {
         addTool(scaffolding);
      }
      else if (expose.equals("false") &&
            scaffolding.getExposedPageId() != null) {
         removeTool(scaffolding);
      }
      getMatrixManager().storeScaffolding(scaffolding);
      return new ModelAndView("success");
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
