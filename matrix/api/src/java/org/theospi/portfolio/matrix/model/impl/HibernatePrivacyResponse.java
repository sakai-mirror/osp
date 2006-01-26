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
package org.theospi.portfolio.matrix.model.impl;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.metaobj.shared.model.ElementBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class HibernatePrivacyResponse implements UserType {
   protected final Log logger = LogFactory.getLog(getClass());
   private final static int[] SQL_TYPES = new int[]{Types.CLOB};

   public HibernatePrivacyResponse() {
   }

   public int[] sqlTypes() {
      return SQL_TYPES;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
    */
   public Object deepCopy(Object arg0) throws HibernateException {
      return arg0;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#equals(java.lang.Object, java.lang.Object)
    */
   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#isMutable()
    */
   public boolean isMutable() {
      return false;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
    */
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
      throws HibernateException, SQLException {
      Clob clob = rs.getClob(names[0]);
      if (rs.wasNull()) return null;

      ElementBean elementBean = new ElementBean();
      elementBean.setDeferValidation(true);
      InputStream in = clob.getAsciiStream();
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
         Document doc = saxBuilder.build(in);
         elementBean.setBaseElement(doc.getRootElement());
      } catch (JDOMException e) {
         throw new HibernateException(e);
      } catch (IOException e) {
         throw new HibernateException(e);
      }
      return elementBean;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index)
   throws HibernateException, SQLException {
   if (value == null) {
      st.setNull(index, Types.VARBINARY);
   } else {
      Document doc = new Document();
      ElementBean elementBean = (ElementBean) value;
      Element rootElement = elementBean.getBaseElement();
      
      rootElement.detach();
      doc.setRootElement(rootElement);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      XMLOutputter xmlOutputter = new XMLOutputter();

      //SAXBuilder sax = new SAXBuilder();

      try {
         xmlOutputter.output(doc, out);
      } catch (IOException e) {
         throw new HibernateException(e);
      }
      StringBuffer sb = new StringBuffer();
      byte[] bytes = out.toByteArray();
      for (int i=0; i<bytes.length; i++) {
         sb.append((char)bytes[i]);
     }
      st.setString(index, sb.toString());
   }
}

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#returnedClass()
    */
   public Class returnedClass() {
      return ElementBean.class;
   }

}
