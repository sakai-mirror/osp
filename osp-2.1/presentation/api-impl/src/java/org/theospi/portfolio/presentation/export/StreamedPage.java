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
package org.theospi.portfolio.presentation.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import websphinx.Page;
import websphinx.Link;
import websphinx.Access;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;

public class StreamedPage extends Page {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Link link;

   public StreamedPage(Link link) {
      super("Streamed");
      this.link = link;
   }

   /**
    * Get the Link that points to this page.
    *
    * @return the Link object that was used to download this page.
    */
   public Link getOrigin() {
      return link;
   }

   public InputStream getStream() throws IOException {
      URLConnection conn =
          Access.getAccess ().openConnection (link);

      // fetch and store final redirected URL and response headers
      InputStream returned = conn.getInputStream ();

      this.setContentEncoding(conn.getContentEncoding());
      this.setContentType(conn.getContentType());
      this.setExpiration(conn.getExpiration());
      this.setLastModified(conn.getLastModified());

      return returned;
   }

   /**
    * Get the URL.
    *
    * @return the URL of the link that was used to download this page
    */
   public URL getURL() {
      return getOrigin().getURL();
   }

}
