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
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/portfolio/shared/control/servlet/FileDownloadServlet.java,v 1.1 2005/08/05 01:08:05 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.shared.control.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.shared.intf.DownloadableManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class FileDownloadServlet extends HttpServlet {
   protected final Log logger = LogFactory.getLog(getClass());

   public final static String REPOSITORY_PREFIX = "repository";
   private static final String MANAGER_NAME = "manager";

   /**
    * Called by the server (via the <code>service</code> method) to
    * allow a servlet to handle a GET request.
    * <p/>
    * <p>Overriding this method to support a GET request also
    * automatically supports an HTTP HEAD request. A HEAD
    * request is a GET request that returns no body in the
    * response, only the request header fields.
    * <p/>
    * <p>When overriding this method, read the request data,
    * write the response headers, get the response's writer or
    * output stream object, and finally, write the response data.
    * It's best to include content type and encoding. When using
    * a <code>PrintWriter</code> object to return the response,
    * set the content type before accessing the
    * <code>PrintWriter</code> object.
    * <p/>
    * <p>The servlet container must write the headers before
    * committing the response, because in HTTP the headers must be sent
    * before the response body.
    * <p/>
    * <p>Where possible, set the Content-Length header (with the
    * {@link javax.servlet.ServletResponse#setContentLength} method),
    * to allow the servlet container to use a persistent connection
    * to return its response to the client, improving performance.
    * The content length is automatically set if the entire response fits
    * inside the response buffer.
    * <p/>
    * <p>When using HTTP 1.1 chunked encoding (which means that the response
    * has a Transfer-Encoding header), do not set the Content-Length header.
    * <p/>
    * <p>The GET method should be safe, that is, without
    * any side effects for which users are held responsible.
    * For example, most form queries have no side effects.
    * If a client request is intended to change stored data,
    * the request should use some other HTTP method.
    * <p/>
    * <p>The GET method should also be idempotent, meaning
    * that it can be safely repeated. Sometimes making a
    * method safe also makes it idempotent. For example,
    * repeating queries is both safe and idempotent, but
    * buying a product online or modifying data is neither
    * safe nor idempotent.
    * <p/>
    * <p>If the request is incorrectly formatted, <code>doGet</code>
    * returns an HTTP "Bad Request" message.
    *
    * @param request  an {@link javax.servlet.http.HttpServletRequest} object that
    *             contains the request the client has made
    *             of the servlet
    * @param response an {@link javax.servlet.http.HttpServletResponse} object that
    *             contains the response the servlet sends
    *             to the client
    * @throws java.io.IOException            if an input or output error is
    *                                        detected when the servlet handles
    *                                        the GET request
    * @throws javax.servlet.ServletException if the request for the GET
    *                                        could not be handled
    * @see javax.servlet.ServletResponse#setContentType
    */
   protected void doGet(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
      java.util.Enumeration tokenizer = new StringTokenizer(
         request.getRequestURI(), "/");

      if (!tokenizer.hasMoreElements()) {
         throw new ServletException("Incorrect format url.");
      }
      String base = (String)tokenizer.nextElement(); // burn off the first element of the path

      while (!base.equalsIgnoreCase(REPOSITORY_PREFIX)) {
         if (!tokenizer.hasMoreElements()) {
            throw new ServletException("Incorrect format url.");
         }
         base = (String)tokenizer.nextElement();
      }

      Hashtable params = HttpUtils.parseQueryString(getNextToken(tokenizer));

      DownloadableManager manager = getDownloadableManager(((String[])params.get(MANAGER_NAME))[0]);
      manager.packageForDownload(params, response.getOutputStream());
   }

   protected DownloadableManager getDownloadableManager(String name) {
      return (DownloadableManager)ComponentManager.get(name);
   }

   protected String getNextToken(Enumeration tokenizer) throws ServletException {
      if (!tokenizer.hasMoreElements()) {
         throw new ServletException("Incorrect format url.");
      }
      return (String)tokenizer.nextElement();
   }

}
