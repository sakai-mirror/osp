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
package org.theospi.portfolio.matrix;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.theospi.portfolio.matrix.model.*;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.shared.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * @author apple
 */
public interface MatrixManager extends WorkflowEnabledManager {

   
   Matrix getMatrix(Id matrixToolId, Id agentId);
   List getCellsByScaffoldingCell(Id scaffoldingCellId);

   Cell getCell(Matrix matrix, Criterion rootCriterion, Level level);

   void unlockNextCell(Cell cell);

   Criterion getCriterion(Id criterionId);
   Level getLevel(Id levelId);

   Cell getCell(Id cellId);

   Cell getCellFromPage(Id pageId);

   List getCells(Matrix matrix);
   
   Id storeMatrixTool(MatrixTool matrixTool);

   Id storeCell(Cell cell);

   Id storePage(WizardPage page);

   Id storeScaffolding(Scaffolding scaffolding);
   
   Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell);
   
   void publishScaffolding(Id scaffoldingId);

   void store(final Object obj);

   MatrixTool createMatrixTool(String toolId, Scaffolding scaffolding);
   
   Matrix createMatrix(Agent owner, MatrixTool matrixTool);

   Attachment getAttachment(Id attachmentId);
   
   Attachment attachArtifact(Id pageId, Reference artifactId);

   void detachArtifact(final Id pageId, final Id artifactId);
   
   void removeFromSession(Object obj);
   void clearSession();

   Matrix getMatrix(Id matrixId);

   MatrixTool getMatrixTool(Id matrixToolId);
   public List getMatrixTools();

   Scaffolding getScaffolding(Id scaffoldingId);
   
   ScaffoldingCell getNextScaffoldingCell(ScaffoldingCell scaffoldingCell, 
         int progressionOption);
   ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level);
   ScaffoldingCell getScaffoldingCell(Id id);
   String getScaffoldingCellsStatus(Id id);

   Set getPageContents(WizardPage page);
   Set getPageForms(WizardPage page);
   List getPageArtifacts(WizardPage page);
   List getCellsByArtifact(Id artifactId);

   Cell submitCellForEvaluation(Cell cell);

   WizardPage submitPageForEvaluation(WizardPage page);

   List getEvaluatableItems(Agent agent, Id worksiteId);


   /**
    * @return Returns the idManager.
    */
   IdManager getIdManager();

   /**
    * @param idManager The idManager to set.
    */
   void setIdManager(IdManager idManager);

   /**
    * @param matrixId
    */
   void deleteMatrix(Id matrixId);
   
   void packageScffoldingForExport(Id scaffoldingId, OutputStream os) throws IOException;
   Scaffolding uploadScaffolding(String scaffoldingFileName, String toolId, 
         InputStream zipFileStream) throws IOException;

   Node getNode(Id artifactId);

   Node getNode(Reference ref);

   Scaffolding uploadScaffolding(Reference uploadedScaffoldingFile,
                                 ToolConfiguration currentPlacement) throws IOException;

   void checkPageAccess(String id);
   
   Scaffolding createDefaultScaffolding();

   public List getScaffolding();

   public List getMatrices(Id scaffoldingId);
   public List getMatrices(Id matrixToolId, Id agentId);

   WizardPage getWizardPage(Id pageId);

}
