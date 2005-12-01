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

import org.theospi.portfolio.warehouse.intf.WarehouseTask;
import org.theospi.portfolio.warehouse.intf.ChildWarehouseTask;
import org.quartz.JobExecutionException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 30, 2005
 * Time: 4:51:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseWarehouseTask implements WarehouseTask {

   private DataSource dataSource;
   private ChildWarehouseTask task;

   public void execute() throws JobExecutionException {
      Connection connection = null;

      try {
         connection = getDataSource().getConnection();
         task.execute(getItems(), getDataSource().getConnection());
      }
      catch (SQLException e) {
         throw new JobExecutionException(e);
      }
      finally {
         if (connection != null) {
            try {
               connection.close();
            }
            catch (Exception e) {
               // can't do anything with this.
            }
         }
      }
   }

   protected abstract Collection getItems();

   public DataSource getDataSource() {
      return dataSource;
   }

   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
   }

   public ChildWarehouseTask getTask() {
      return task;
   }

   public void setTask(ChildWarehouseTask task) {
      this.task = task;
   }
}
