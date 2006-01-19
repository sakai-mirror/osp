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
package org.theospi.portfolio.shared.model;

import org.sakaiproject.metaobj.shared.IdGenerator;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.io.Serializable;
import java.sql.SQLException;

import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.HibernateException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 18, 2006
 * Time: 5:38:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentifiableIdGenerator extends IdGenerator {

   public Serializable generate(SessionImplementor arg0, Object arg1) throws SQLException, HibernateException {

      IdentifiableObject object = (IdentifiableObject) arg1;
      if (object.getNewId() != null) {
         return object.getNewId();
      }
      return super.generate(arg0, arg1);
   }

}
