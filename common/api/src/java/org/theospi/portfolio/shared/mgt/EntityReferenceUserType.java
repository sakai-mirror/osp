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
package org.theospi.portfolio.shared.mgt;

import net.sf.hibernate.UserType;
import net.sf.hibernate.HibernateException;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.Serializable;

import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:42:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityReferenceUserType implements UserType, Serializable {

   public int[] sqlTypes() {
      return new int[]{Types.VARCHAR};
   }

   public Class returnedClass() {
      return ReferenceHolder.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   public Object nullSafeGet(ResultSet resultSet, String[] names, Object o) throws HibernateException, SQLException {
      String result = resultSet.getString(names[0]);
      if (result == null)
         return null;

      ReferenceHolder ref = new ReferenceHolder(EntityManager.newReference(result));
      return ref;
   }

   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
      ReferenceHolder ref = (ReferenceHolder) value;
      if (value == null) {
         st.setNull(index, Types.VARCHAR);
      } else {
         st.setString(index, ref.getBase().getReference());
      }
   }

   public Object deepCopy(Object o) throws HibernateException {
      return o;
   }

   public boolean isMutable() {
      return false;
   }

}
