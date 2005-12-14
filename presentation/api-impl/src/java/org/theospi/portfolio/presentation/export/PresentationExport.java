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
import websphinx.*;

import java.net.URL;
import java.io.*;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Adler32;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;

public class PresentationExport extends Crawler implements LinkListener {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private PortfolioMirror mirror = null;
   private String hostName = null;
   private String webappName = null;
   private String tempDirectory = null;
   public static final int BUFFER = 1024 * 10;
   private ArrayList errorLinks = new ArrayList();

   public PresentationExport(String url, String tempDirectory) throws IOException {
      this.tempDirectory = tempDirectory;

      URL urlObj = new URL(url);
      this.hostName = urlObj.getHost();
      String path = urlObj.getPath();

      StringTokenizer tok = new StringTokenizer(path, "/", false);

      webappName = tok.nextToken();
      if (!tok.hasMoreTokens()) {
         webappName = "";
      }
      else {
         webappName = "/" + webappName;
      }

      mirror = new PortfolioMirror(tempDirectory, webappName);

      this.setRootHrefs(url);
      this.setLinkType(Crawler.ALL_LINKS);
      this.setSynchronous(true);
      this.setDomain(Crawler.WEB);
      this.addLinkListener(this);

      DownloadParameters dp = getDownloadParameters();
      setDownloadParameters(dp.changeMaxPageSize(2000));
   }

   public void createZip(OutputStream out) throws IOException {
      File directory = new File(tempDirectory + webappName);

      CheckedOutputStream checksum = new CheckedOutputStream(out, new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

      recurseDirectory("", directory, zos);

      zos.finish();
      zos.flush();
   }

   protected void recurseDirectory(String parentPath, File directory, ZipOutputStream zos) throws IOException {
      // get all files... go through those
      File[] files = directory.listFiles(new DirectoryFileFilter(false));
      addFiles(zos, parentPath, files);

      // get all directories... go through those...
      File[] directories = directory.listFiles(new DirectoryFileFilter(true));
      for (int i=0;i<directories.length;i++) {
         recurseDirectory(parentPath + directories[i].getName() + "/",
            directories[i], zos);
      }

   }

   protected void addFiles(ZipOutputStream out, String parentPrefix,
                      File [] files) throws IOException {

      BufferedInputStream origin = null;

      byte data[] = new byte[BUFFER];
      for (int i=0;i<files.length;i++) {
         String fileName = parentPrefix + files[i].getName();
         logger.debug("Adding " + fileName);
         InputStream in = new FileInputStream(files[i]);

         if (in == null)
            throw new NullPointerException();

         origin = new BufferedInputStream(in, BUFFER);

         if (fileName == null)
            throw new NullPointerException();

         ZipEntry entry = new ZipEntry(fileName);
         out.putNextEntry(entry);
         int count;
         while ((count = origin.read(data, 0, BUFFER)) != -1) {
            out.write(data, 0, count);
         }
         out.closeEntry();
         in.close();
      }
   }

   /**
    * Start crawling.  Returns either when the crawl is done, or
    * when pause() or stop() is called.  Because this method implements the
    * java.lang.Runnable interface, a crawler can be run in the
    * background thread.
    */
   public void run() {
      super.run();

      // process error links
      for (Iterator i=errorLinks.iterator();i.hasNext();) {
         Link link = (Link)i.next();
         visit(link.getPage());
      }
   }


   public synchronized void visit(Page page) {

      try {
         mirror.writePage(page);
         mirror.rewrite();
      } catch (IOException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }

      logger.debug("visiting page");
      super.visit(page);
   }

   public synchronized boolean shouldVisit(Link link) {
      if (link.getMethod() == Link.POST) {
         return false;
      }

      if (!link.getHost().equalsIgnoreCase(hostName)) {
         return false;
      }

      if (link.getURL().getFile().startsWith(webappName + "/showPublicPortfolio.do")) {
         return false;
      }

      return true;
   }

   public void deleteTemp() {
      File temp = new File(tempDirectory);

      deleteContent(temp);
      temp.delete();
   }

   protected void deleteContent(File directory) {
      File[] files = directory.listFiles(new DirectoryFileFilter(false));

      if (files != null) {
         for (int i=0;i<files.length;i++) {
            files[i].delete();
         }
      }

      // get all directories... go through those...
      File[] directories = directory.listFiles(new DirectoryFileFilter(true));
      if (directories != null) {
         for (int i=0;i<directories.length;i++) {
            deleteContent(directories[i]);
            directories[i].delete();
         }
      }
   }

   /**
    * Notify that an event occured on a link.
    */
   public void crawled(LinkEvent event) {
      if (event.getID() == LinkEvent.ERROR) {
         // switch to stream page link

         if (!(event.getLink().getPage() instanceof StreamedPage)) {
            logger.debug("loading file through streamed page.");
            Link newLink = new Link(event.getLink().getURL());
            newLink.setPage(new StreamedPage(event.getLink()));
            addErrorLink(newLink);
         }
         else {
            logger.error("Link error " + event.getLink().getURL().toExternalForm(),
               event.getException());
         }
      }
      else if (event.getID() == LinkEvent.QUEUED) {
         if (event.getLink().getPage() instanceof StreamedPage) {
            event.getLink().setStatus(LinkEvent.DOWNLOADED);
         }
      }
   }

   protected synchronized void addErrorLink(Link newLink) {
      errorLinks.add(newLink);
   }

   private class DirectoryFileFilter implements FileFilter {
      private boolean directories = false;

      public DirectoryFileFilter(boolean directories) {
         this.directories = directories;
      }

      /**
       * Tests whether or not the specified abstract pathname should be
       * included in a pathname list.
       *
       * @param pathname The abstract pathname to be tested
       * @return <code>true</code> if and only if <code>pathname</code>
       *         should be included
       */
      public boolean accept(File pathname) {
         if (directories) {
            return pathname.isDirectory();
         }
         else {
            return pathname.isFile();
         }
      }

   }


}
