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
package org.theospi.utils.mvc.impl;

import org.sakaiproject.metaobj.shared.control.RedirectView;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 11:25:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class HelperView extends RedirectView {

   private String modelPrefix;

   public void render(Map model, HttpServletRequest request,
                      HttpServletResponse response) throws Exception {
      // take model entries and register them as helper params
      ToolSession toolSession = SessionManager.getCurrentToolSession();

      for (Iterator i=model.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         toolSession.setAttribute(createName(entry.getKey()),
            entry.getValue());
      }

      super.render(model, request, response);
   }

   protected String createName(Object key) {
      return getModelPrefix() + key;
   }

   public String getModelPrefix() {
      return modelPrefix;
   }

   public void setModelPrefix(String modelPrefix) {
      this.modelPrefix = modelPrefix;
   }

}
