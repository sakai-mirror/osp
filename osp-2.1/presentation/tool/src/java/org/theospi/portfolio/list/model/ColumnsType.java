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
package org.theospi.portfolio.list.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.sf.hibernate.UserType;
import net.sf.hibernate.HibernateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ColumnsType implements UserType {
   protected final transient Log logger = LogFactory.getLog(getClass());


   /**
    * Return the SQL type codes for the columns mapped by this type. The
    * codes are defined on <tt>java.sql.Types</tt>.
    *
    * @return int[] the typecodes
    * @see java.sql.Types
    */
   public int[] sqlTypes() {
      return new int[]{Types.VARCHAR};
   }

   /**
    * The class returned by <tt>nullSafeGet()</tt>.
    *
    * @return Class
    */
   public Class returnedClass() {
      return List.class;
   }

   /**
    * Compare two instances of the class mapped by this type for persistence "equality".
    * Equality of the persistent state.
    *
    * @param x
    * @param y
    * @return boolean
    */
   public boolean equals(Object x, Object y) throws HibernateException {
      List xArray = (List)x;
      List yArray = (List)y;

      if (xArray == null && yArray == null) return true;
      if (xArray == null || yArray == null) return false;
      if (xArray.size() != yArray.size()) return false;

      for (int i=0;i<xArray.size();i++){
         if (!xArray.get(i).equals(yArray.get(i))) {
            return false;
         }
      }

      return true;
   }

   /**
    * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
    * should handle possibility of null values.
    *
    * @param rs    a JDBC result set
    * @param names the column names
    * @param owner the containing entity
    * @return Object
    * @throws net.sf.hibernate.HibernateException
    *
    * @throws java.sql.SQLException
    */
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
      String result = rs.getString(names[0]);
      if (result == null)
         return null;

      StringTokenizer st = new StringTokenizer(result, ",");
      List returned = new ArrayList();

      while (st.hasMoreTokens()) {
         returned.add(st.nextToken());
      }

      return returned;
   }

   /**
    * Write an instance of the mapped class to a prepared statement. Implementors
    * should handle possibility of null values. A multi-column type should be written
    * to parameters starting from <tt>index</tt>.
    *
    * @param st    a JDBC prepared statement
    * @param value the object to write
    * @param index statement parameter index
    * @throws net.sf.hibernate.HibernateException
    *
    * @throws java.sql.SQLException
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
      StringBuffer sb = new StringBuffer();
      List value1 = (List)value;

      for (Iterator i=value1.iterator();i.hasNext();) {
         sb.append(i.next().toString());
         if (i.hasNext()) {
            sb.append(',');
         }
      }

      st.setString(index, sb.toString());
   }

   /**
    * Return a deep copy of the persistent state, stopping at entities and at
    * collections.
    *
    * @return Object a copy
    */
   public Object deepCopy(Object value) throws HibernateException {
      List old = (List)value;
      List new1 = new ArrayList();

      for (Iterator i=old.iterator();i.hasNext();) {
         new1.add(new String(i.next().toString()));
      }

      return new1;
   }

   /**
    * Are objects of this type mutable?
    *
    * @return boolean
    */
   public boolean isMutable() {
      return true;
   }
}
