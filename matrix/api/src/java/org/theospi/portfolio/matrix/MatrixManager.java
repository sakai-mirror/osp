/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005, 2006, 2007 The Sakai Foundation.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.shared.model.Node;

/**
 * @author apple
 */
public interface MatrixManager extends WorkflowEnabledManager {

   public static final String EXPOSED_MATRIX_KEY = "osp.exposedmatrix.scaffolding.id";
   
   Matrix getMatrix(Id scaffoldingId, Id agentId);
   List getCellsByScaffoldingCell(Id scaffoldingCellId);
   List getPagesByPageDef(Id pageDefId);

   Cell getCell(Matrix matrix, Criterion rootCriterion, Level level);

   void unlockNextCell(Cell cell);

   Criterion getCriterion(Id criterionId);
   Level getLevel(Id levelId);

   Cell getCell(Id cellId);

   Cell getCellFromPage(Id pageId);

   List getCells(Matrix matrix);

   Id storeCell(Cell cell);

   Id storePage(WizardPage page);

   Scaffolding storeScaffolding(Scaffolding scaffolding);
   Scaffolding saveNewScaffolding(Scaffolding scaffolding);
   
   Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell);
   
   void publishScaffolding(Id scaffoldingId);
	
   void previewScaffolding(Id scaffoldingId);

   Object store(Object obj);
   Object save(Object obj);
   
   Matrix createMatrix(Agent owner, Scaffolding scaffolding);

   Attachment getAttachment(Id attachmentId);
   
   Attachment attachArtifact(Id pageId, Reference artifactId);

   void detachArtifact(final Id pageId, final Id artifactId);
   void detachForm(final Id pageId, final Id artifactId);
   
   void removeFromSession(Object obj);
   void clearSession();

   Matrix getMatrix(Id matrixId);

   public List getMatricesForWarehousing();

   Scaffolding getScaffolding(Id scaffoldingId);
   
   /**
    * 
    * @param siteIdStr
    * @param userId
    * @return
    */
   public List findAvailableScaffolding(String siteIdStr, Agent user);
   public List findAvailableScaffolding(List sites, Agent user);
   
   List findPublishedScaffolding(List sites);
   
   ScaffoldingCell getNextScaffoldingCell(ScaffoldingCell scaffoldingCell, 
         int progressionOption);
   ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level);
   ScaffoldingCell getScaffoldingCell(Id id);
   
   /**
    * Get all scaffolding cells for a given scaffolding
    * @param scaffoldingId
    * @return
    */
   public Set<ScaffoldingCell> getScaffoldingCells(Id scaffoldingId);
   
   ScaffoldingCell getScaffoldingCellByWizardPageDef(Id id);
   String getScaffoldingCellsStatus(Id id);

   Set getPageContents(WizardPage page);
   Set getPageForms(WizardPage page);
   //List getPageArtifacts(WizardPage page);
   List getCellsByArtifact(Id artifactId);
   List getCellsByForm(Id artifactId);

   Cell submitCellForEvaluation(Cell cell);

   WizardPage submitPageForEvaluation(WizardPage page);

   /**
    * gets all the cells, pages, and wizards that this user can evaluate within a worksite
    * @param agent Agent 
    * @param worksiteId Id
    * @return List of org.theospi.portfolio.shared.model.EvaluationContentWrapper
    */
   List getEvaluatableItems(Agent agent, Id worksiteId);

   /**
    * gets all the cells, pages, and wizards that this user can evaluate within all worksites they are a member of
    * @param agent Agent 
    * @return List of org.theospi.portfolio.shared.model.EvaluationContentWrapper
    */
   List getEvaluatableItems(Agent agent);

   /**
    * @param matrixId
    */
   void deleteMatrix(Id matrixId);
   
   void deleteScaffolding(Id scaffoldingId);
   public void exposeMatrixTool(Scaffolding scaffolding);
   public void removeExposedMatrixTool(Scaffolding scaffolding);
   
   void packageScffoldingForExport(Id scaffoldingId, OutputStream os) throws IOException;

   Node getNode(Id artifactId);

   Node getNode(Reference ref);

   Scaffolding uploadScaffolding(Reference uploadedScaffoldingFile,
                                 String toContext) throws IOException;

   //Scaffolding uploadScaffolding(String toContext, ZipInputStream zis) throws IOException;
   
   void checkPageAccess(String id);
   
   Scaffolding createDefaultScaffolding();

   public List getScaffolding();
   public List getScaffoldingForWarehousing();

   public List getMatrices(Id scaffoldingId);
   public List getMatrices(Id scaffoldingId, Id agentId);

   WizardPage getWizardPage(Id pageId);
   List getWizardPagesForWarehousing();
   
   Matrix getMatrixByPage(Id pageId);

   public boolean isUseExperimentalMatrix();
}
