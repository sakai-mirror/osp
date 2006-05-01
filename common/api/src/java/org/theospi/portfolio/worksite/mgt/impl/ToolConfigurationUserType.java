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
package org.theospi.portfolio.worksite.mgt.impl;

import net.sf.hibernate.UserType;
import net.sf.hibernate.HibernateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.io.Serializable;

import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.theospi.portfolio.worksite.model.ToolConfigurationWrapper;

public class ToolConfigurationUserType implements UserType, Serializable {
   private static final int ID_COLUMN = 0;

   public int[] sqlTypes() {
      return new int[] {
         Types.VARCHAR};
   }

   public Class returnedClass() {
      return ToolConfiguration.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   public Object nullSafeGet(ResultSet resultSet, String[] columns, Object o) throws HibernateException, SQLException {
      String idValue = resultSet.getString(columns[ID_COLUMN]);
      if (resultSet.wasNull()) {
         return null;
      }
      ToolConfiguration tool = getWorksiteManager().getTool(idValue);
      if (tool == null) return null;
      return new ToolConfigurationWrapper(tool) ;
   }

   public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
      ToolConfiguration toolConfiguration = (ToolConfiguration) value;
      if (toolConfiguration == null || toolConfiguration.getId() == null) {
         preparedStatement.setNull(index, Types.VARCHAR);
      } else {
         preparedStatement.setString(index, toolConfiguration.getId());
      }
   }

   public Object deepCopy(Object o) throws HibernateException {
      return o;
   }

   public boolean isMutable() {
      return false;
   }

   protected WorksiteManager getWorksiteManager(){
      return (WorksiteManager) ComponentManager.getInstance().get(WorksiteManager.class.getName());
   }
}
