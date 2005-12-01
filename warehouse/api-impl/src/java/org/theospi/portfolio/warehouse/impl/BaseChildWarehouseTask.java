/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
package org.theospi.portfolio.warehouse.impl;

import org.theospi.portfolio.warehouse.intf.ChildWarehouseTask;
import org.theospi.portfolio.warehouse.intf.PropertyAccess;
import org.quartz.JobExecutionException;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:58:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseChildWarehouseTask implements ChildWarehouseTask {

   private List fields;
   private String insertStmt;
   private List complexFields;

   public void execute(Collection items, Connection connection)
         throws JobExecutionException {
      PreparedStatement ps = null;

      try {
         ps = connection.prepareStatement(getInsertStmt());
         for (Iterator i=items.iterator();i.hasNext();) {
            processItem(i.next(), ps);
         }
      }
      catch (SQLException e) {
         throw new JobExecutionException(e);
      }
      finally {
         try {
            ps.close();
         }
         catch (Exception e) {
            // nothing to do here.
         }
      }

   }

   protected void processItem(Object item, PreparedStatement ps)
         throws JobExecutionException {

      try {
         int index = 1;
         for (Iterator i=getFields().iterator();i.hasNext();) {
            PropertyAccess pa = (PropertyAccess)i.next();
            ps.setObject(index, pa.getPropertyValue(item));
            index++;
         }
         // does this insert...
         ps.execute();

         // now, lets look for complex fields
         if (getComplexFields() != null) {
            for (Iterator i=getComplexFields().iterator();i.hasNext();) {
               ChildFieldWrapper wrapper = (ChildFieldWrapper)i.next();

               wrapper.getTask().execute(
                     (Collection)wrapper.getPropertyAccess().getPropertyValue(item), ps.getConnection());
            }
         }
      }
      catch (Exception e) {
         throw new JobExecutionException(e);
      }
   }

   public List getFields() {
      return fields;
   }

   public void setFields(List fields) {
      this.fields = fields;
   }

   public String getInsertStmt() {
      return insertStmt;
   }

   public void setInsertStmt(String insertStmt) {
      this.insertStmt = insertStmt;
   }

   public List getComplexFields() {
      return complexFields;
   }

   public void setComplexFields(List complexFields) {
      this.complexFields = complexFields;
   }
}
