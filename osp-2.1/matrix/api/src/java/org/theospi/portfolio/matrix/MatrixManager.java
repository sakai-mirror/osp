/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/matrix/api/src/java/org/theospi/portfolio/matrix/MatrixManager.java,v 1.7 2005/10/26 23:53:01 jellis Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 28, 2004
 */
package org.theospi.portfolio.matrix;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Reflection;
import org.theospi.portfolio.matrix.model.ReviewRubricValue;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.MatrixTool;
import org.theospi.portfolio.matrix.model.ReviewerItem;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.shared.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * @author apple
 */
public interface MatrixManager {
   /**
    * All the criteria for a given Cell
    *
    * @param cell
    * @return
    */
   
   ReviewRubricValue findReviewRubricValue(String id);
   List getReviewRubrics();

   Matrix getMatrix(Id matrixToolId, Id agentId);
   List getCellsByScaffoldingCell(Id scaffoldingCellId);

   Cell getCell(Matrix matrix, Criterion rootCriterion, Level level);

   void unlockNextCell(Cell cell);

   Criterion getCriterion(Id criterionId);
   Level getLevel(Id levelId);

   Cell getCell(Id cellId);
   
   List getCells(Matrix matrix);
   
   Reflection getReflection(Id reflectionId);
   
   Id storeMatrixTool(MatrixTool matrixTool);

   Id storeCell(Cell cell);

   Id storeScaffolding(Scaffolding scaffolding);
   
   Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell);
   
   void publishScaffolding(Id scaffoldingId);

   void store(final Object obj);

   MatrixTool createMatrixTool(String toolId, Scaffolding scaffolding);
   
   Matrix createMatrix(Agent owner, MatrixTool matrixTool);

   Attachment getAttachment(Id attachmentId);
   
   Attachment attachArtifact(Id cellId, String[] criteriaId, Reference artifactId, ElementBean elementBean);

   void detachArtifact(final Id cellId, final Id artifactId);
   
   void removeFromSession(Object obj);
   void clearSession();

   Matrix getMatrix(Id matrixId);
   
   MatrixTool getMatrixTool(Id matrixToolId);

   Scaffolding getScaffolding(Id scaffoldingId);
   
   ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level);
   ScaffoldingCell getScaffoldingCell(Id id);
   String getScaffoldingCellsStatus(Id id);

   List getArtifactAssociationCriteria(Id cellId, Id nodeId);

   Set getCellContents(Cell cell);
   Set getCellForms(Cell cell);
   List getCellArtifacts(Cell cell);
   List getCellsByArtifact(Id artifactId);

   Cell submitCellForEvaluation(Cell cell);

   List getEvaluatableCells(Agent agent, Id worksiteId);

   ReviewerItem getReviewerItem(Id id);

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

   void checkCellAccess(String id);
   
   Scaffolding createDefaultScaffolding();

   public List getScaffolding();

   public List getMatrices(Id scaffoldingId);

}
