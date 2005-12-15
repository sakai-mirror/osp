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

import websphinx.Access;
import websphinx.Link;
import websphinx.DownloadParameters;

import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 14, 2005
 * Time: 1:38:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionAccess extends Access {

   private ThreadLocal threadCookies = new ThreadLocal();

   public URLConnection openConnection (URL url) throws IOException {
       URLConnection conn = url.openConnection ();
       connect(conn);
       return conn;
   }

   public URLConnection openConnection (Link link) throws IOException {
       // get the URL
       int method = link.getMethod();
       URL url;
       switch (method) {
           case Link.GET:
               url = link.getPageURL();
               break;
           case Link.POST:
               url = link.getServiceURL();
               break;
           default:
               throw new IOException ("Unknown HTTP method " + link.getMethod());
       }

       // open a connection to the URL
       URLConnection conn = url.openConnection ();

       // set up request headers
       DownloadParameters dp = link.getDownloadParameters ();
       if (dp != null) {
           conn.setAllowUserInteraction (dp.getInteractive ());
           conn.setUseCaches (dp.getUseCaches ());

           String userAgent = dp.getUserAgent ();
           if (userAgent != null)
               conn.setRequestProperty ("User-Agent", userAgent);

           String types = dp.getAcceptedMIMETypes ();
           if (types != null)
               conn.setRequestProperty ("accept", types);
       }

       // submit the query if it's a POST (GET queries are encoded in the URL)
       if (method == Link.POST) {
//#ifdef JDK1.1
           if (conn instanceof HttpURLConnection)
               ((HttpURLConnection)conn).setRequestMethod ("POST");
//#endif JDK1.1

           String query = link.getQuery ();
           if (query.startsWith ("?"))
               query = query.substring (1);

           conn.setDoOutput (true);
           conn.setRequestProperty ("Content-type",
                                    "application/x-www-form-urlencoded");
           conn.setRequestProperty ("Content-length", String.valueOf(query.length()));

           // commence request
//#ifdef JDK1.1
           PrintStream out = new PrintStream (conn.getOutputStream ());
//#endif JDK1.1
/*#ifdef JDK1.0
            PrintStream out = new PrintStream (conn.getOutputStream ());
#endif JDK1.0*/
           out.print (query);
           out.flush ();
       }

       connect(conn);
       return conn;
   }

   protected void connect(URLConnection conn) throws IOException {
      setCookies(conn);
      conn.connect ();
      saveCookies(conn);
   }


   protected void setCookies( URLConnection urlconn )  {
      String headerfield = "";
      String host = "";

      // get the host
      host = urlconn.getURL().getHost();

      // get saved cookies from hashtable
      headerfield = (String)getCookies().get( host );

      // check if there are any saved cookies
      if( headerfield != null ) {
         // set cookie string as request property
         urlconn.setUseCaches(false);
         urlconn.setRequestProperty( "Cookie", headerfield );
      }
   }

   protected Map getCookies() {
      if (threadCookies.get() == null) {
         threadCookies.set(new HashMap());
      }
      return (Map) threadCookies.get();
   }

   protected void saveCookies( URLConnection urlconn ) {
      int i = 0;
      String key = null;
      String cookie = null;
      String host = null;
      String headerfield = null;
      StringTokenizer tok = null;

      // get the host
      host = urlconn.getURL().getHost();

      // forward pass any starting null values
      while( urlconn.getHeaderFieldKey( i ) == null ) i++;

      // check all the headerfields until there are no more
      while( urlconn.getHeaderFieldKey( i ) != null ) {
         key = urlconn.getHeaderFieldKey( i );

         // check if it is a Set-Cookie header
         if( key.equalsIgnoreCase( "set-cookie" ) ) {
            headerfield = urlconn.getHeaderField( i );
            if( headerfield != null ) { // can the headerfield be null here ?
               // parse out only the name=value pair and ignore the rest
               tok = new StringTokenizer( headerfield , ";" , false );

               // if this is anything but the first cookie add a semicolon and a space
               // before we add the next cookie.
               cookie = ( cookie != null ? cookie + "; " + tok.nextToken() : tok.nextToken() );
            }
         }
         i++;
      }

      // save the cookies in our cookie collection with the hostname as key
      if( cookie != null ) {
         getCookies().put( host, cookie );
      }
   }


}
