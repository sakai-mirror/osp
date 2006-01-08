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
package org.theospi.component.app.reports;

import org.theospi.portfolio.reports.model.ResultProcessor;
import org.theospi.portfolio.reports.model.ReportResult;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.reports.model.ReportResult;
import org.theospi.portfolio.reports.model.ResultProcessor;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 22, 2005
 * Time: 5:32:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseResultProcessor implements ResultProcessor {

   protected final transient Log logger = LogFactory.getLog(getClass());
   private SAXBuilder builder = new SAXBuilder();

   protected Document getResults(ReportResult result) {
      Document rootElement = null;
      try {
         rootElement = builder.build(new StringReader(result
                     .getXml()));
      }
      catch (JDOMException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      return rootElement;
   }

   protected ReportResult setResult(ReportResult result, Document doc) {
      result.setXml((new XMLOutputter()).outputString(doc));
      return result;
   }

}
