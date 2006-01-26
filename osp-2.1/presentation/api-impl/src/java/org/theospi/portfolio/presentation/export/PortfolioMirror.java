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
package org.theospi.portfolio.presentation.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import websphinx.*;

import javax.servlet.http.HttpUtils;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Vector;

public class PortfolioMirror extends Mirror {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private int index = 0;
   private URL base = null;
   private boolean needRewrite = false;
   Vector files = new Vector ();
   private String webappName = null;

   private static final String OSP_LIBRARY_PATH = "/osp-common-tool/";

   public PortfolioMirror(String directory, String webappName) throws IOException {
      super(directory);
      base = new File(directory).toURL();
      this.webappName = webappName;
   }

   /**
    * Write a page to the mirror. Stores the page on the local
    * disk, fixing up its links to point to the local
    * copies of any pages already stored to this mirror.
    *
    * @param page Page to write
    */
   public synchronized void writePage(Page page) throws IOException {
      URL url = page.getURL ();
      String local = toLocalFileURL (url);
      URL localURL = new URL (local);
      File localFile = Link.URLToFile (localURL);

      File parent = new File (localFile.getParent ());
      if (parent != null)
          Access.getAccess ().makeDir (parent);

      PortfolioMirrorTransformer out = new PortfolioMirrorTransformer (this, localFile);
      out.setBase (localURL);
      out.setEmitBaseElement (getEmitBaseElement ());
      out.writePage (page);
      out.close ();

      needRewrite = !files.isEmpty ();
      files.addElement (out);
   }

   /**
    * Get number of pages written to this mirror.
    * @return number of calls to writePage() on this mirror
    */
   public synchronized int getPageCount () {
       return files.size ();
   }


   /**
    * Rewrite the mirror to make local links consistent.
    */
   public synchronized void rewrite () throws IOException {
       if (needRewrite) {
           for (int i=0, n = files.size (); i < n; ++i) {
               RewritableLinkTransformer r =
                   (RewritableLinkTransformer)files.elementAt (i);
               r.rewrite ();
           }
           needRewrite = false;
       }
   }

   // maps a remote URL to a local file URL ("<root>/<host>/<filename>")
   // resulting URL is never slash-terminated
   protected String toLocalFileURL (URL remoteURL) {
      if (isMapped (remoteURL))
         return lookup (null, remoteURL);

      String local;
      if (remoteURL.getFile().startsWith("/access")) {
         File file = new File(remoteURL.getPath());
         local = base + webappName + "/repository/" + file.getName();
         local = ensureUnique(local);
      }
      else if (remoteURL.getFile().startsWith(OSP_LIBRARY_PATH)) {
         String fileName = "/library/" + remoteURL.getFile().substring(OSP_LIBRARY_PATH.length());
         local = base + webappName + encode(fileName);
         local = ensureUnique(local);
      }
      else if (remoteURL.getFile().startsWith(webappName + "/viewPresentation.osp?")) {
         local = base + webappName + "/myPresentation.html";
         local = ensureUnique(local);
      }
      else if (!remoteURL.getFile().startsWith(webappName + "/")) {
         local = base + webappName + encode(remoteURL.getFile());
      }
      else {
         local = base + encode(remoteURL.getFile());
      }

      map (remoteURL, local);
      return local;
   }

   protected String ensureUnique(String local) {
      String orig = local;
      File file = null;
      try {
         file = new File(new URI(local));
         int current = 0;
         while (file.exists()) {
            current++;
            int dotPos = orig.lastIndexOf('.');
            if (dotPos == -1) {
               local = orig + current;
            }
            else {
               local = orig.substring(0, dotPos) + "-" + current + orig.substring(dotPos);
            }
            file = new File(new URI(local));
         }
      }
      catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }

      return local;
   }

   private static String encode (String component) {
       char[] chars = component.toCharArray ();

       for (int i=0; i<chars.length; ++i)
           switch (chars[i]) {
               case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
               case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
               case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
               case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
               case 'Y': case 'Z':

               case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
               case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
               case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
               case 's': case 't': case 'u': case 'v': case 'w': case 'x':
               case 'y': case 'z':

               case '0': case '1': case '2': case '3': case '4':
               case '5': case '6': case '7': case '8': case '9':

               case '/': case '.': case '-': case '_': case '~':

                  break;

              default:
                  chars[i] = '_';
                  break;
          }

       return new String (chars);
   }

   private class PortfolioMirrorTransformer extends RewritableLinkTransformer {
      private PortfolioMirror mirror; // on the wall?
      private File file;
       public PortfolioMirrorTransformer (PortfolioMirror mirror, File file) throws IOException {
           super (file.toString());
           this.mirror = mirror;
          this.file = file;
       }

       public String lookup (URL base, URL url) {
           return mirror.lookup (base, url);
       }

       public void map (URL remoteURL, String href) {
           mirror.map (remoteURL, href);
       }

       public void map (URL remoteURL, URL url) {
           mirror.map (remoteURL, url);
       }

       public boolean isMapped (URL url) {
           return mirror.isMapped (url);
       }

      /**
       * Write a page through the transformer.  If
       * getEmitBaseElement() is true and getBase() is
       * non-null, then the transformer
       * outputs a &lt;BASE&gt; element either inside the
       * page's &lt;HEAD&gt; element (if present) or before
       * the first tag that belongs in &lt;BODY&gt;.
       *
       * @param page Page to write
       */
      public synchronized void writePage(Page page) throws IOException {
         if (page instanceof StreamedPage) {
            StreamedPage sp = (StreamedPage)page;

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[PresentationExport.BUFFER];
            InputStream is = sp.getStream();

            int count;
            while ((count = is.read(buffer, 0, PresentationExport.BUFFER)) != -1) {
               fos.write(buffer, 0, count);
            }
            fos.close();
         }
         else {
            super.writePage(page);
         }
      }
   }

}
