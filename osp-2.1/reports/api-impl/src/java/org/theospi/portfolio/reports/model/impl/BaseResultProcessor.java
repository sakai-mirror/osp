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
package org.theospi.portfolio.reports.model.impl;

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
