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
package org.theospi.portfolio.warehouse.impl;

import org.theospi.portfolio.warehouse.intf.ChildWarehouseTask;
import org.theospi.portfolio.warehouse.intf.PropertyAccess;
import org.theospi.portfolio.warehouse.intf.ParentPropertyAccess;
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
public class BaseChildWarehouseTask implements ChildWarehouseTask {

   private List fields;
   private String insertStmt;
   private String clearStmt;
   private List complexFields;
   private int batchSize = 100;

   public void execute(Object parent, Collection items, Connection connection)
         throws JobExecutionException {
      PreparedStatement ps = null;

      try {
         int current = 0;
         ps = connection.prepareStatement(getInsertStmt());
         for (Iterator i=items.iterator();i.hasNext();) {
            processItem(parent, i.next(), ps);
            ps.addBatch();
            current++;
            if (current > batchSize) {
               current = 0;
               ps.executeBatch();
            }
            ps.clearParameters();
         }
         if (current > 0) {
            ps.executeBatch();
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

   public void prepare(Connection connection) {
      try {
         connection.createStatement().execute(getClearStmt());

         if (getComplexFields() != null) {
            for (Iterator i=getComplexFields().iterator();i.hasNext();) {
               ChildFieldWrapper wrapper = (ChildFieldWrapper)i.next();
               wrapper.getTask().prepare(connection);
            }
         }
      }
      catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   protected void processItem(Object parent, Object item, PreparedStatement ps)
         throws JobExecutionException {

      try {
         int index = 1;
         for (Iterator i=getFields().iterator();i.hasNext();) {
            Object o = i.next();
            if (o instanceof PropertyAccess) {
               PropertyAccess pa = (PropertyAccess)o;
               ps.setObject(index, pa.getPropertyValue(item));
            }
            else if (o instanceof ParentPropertyAccess) {
               ParentPropertyAccess pa = (ParentPropertyAccess)o;
               ps.setObject(index, pa.getPropertyValue(parent, item));
            }
            index++;
         }

         // now, lets look for complex fields
         if (getComplexFields() != null) {
            for (Iterator i=getComplexFields().iterator();i.hasNext();) {
               ChildFieldWrapper wrapper = (ChildFieldWrapper)i.next();

               wrapper.getTask().execute(item,
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

   public int getBatchSize() {
      return batchSize;
   }

   public void setBatchSize(int batchSize) {
      this.batchSize = batchSize;
   }

   public String getClearStmt() {
      return clearStmt;
   }

   public void setClearStmt(String clearStmt) {
      this.clearStmt = clearStmt;
   }
}
