/**********************************************************************************
* $URL:$
* $Id:$
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
package org.theospi.portfolio.warehouse.osp.wizard;

import org.theospi.portfolio.wizard.mgt.WizardManager;

import org.theospi.portfolio.warehouse.impl.BaseWarehouseTask;
import java.util.Collection;
import java.util.ArrayList;

class WizardWarehouseTask extends BaseWarehouseTask {

   private WizardManager wizardManager;
   
   protected Collection getItems() {
      return new ArrayList();
   }

   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
}