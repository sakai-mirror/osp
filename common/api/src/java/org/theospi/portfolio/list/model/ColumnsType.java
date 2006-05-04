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
 * $Header: /opt/CVS/osp/src/portfolio/org/theospi/portfolio/list/model/ColumnsType.java,v 1.2 2004/10/29 23:47:40 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.list.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.usertype.UserType;
import org.hibernate.HibernateException;

import java.io.Serializable;
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
    * @throws org.hibernate.HibernateException
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
    * @throws org.hibernate.HibernateException
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

   public int hashCode(Object arg0) throws HibernateException {
      // TODO Auto-generated method stub
      return 0;
   }

   public Serializable disassemble(Object arg0) throws HibernateException {
      // TODO Auto-generated method stub
      return null;
   }

   public Object assemble(Serializable arg0, Object arg1) throws HibernateException {
      // TODO Auto-generated method stub
      return null;
   }

   public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException {
      // TODO Auto-generated method stub
      return null;
   }
}
