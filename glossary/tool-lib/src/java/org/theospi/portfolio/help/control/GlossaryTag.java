/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/tool-lib/src/java/org/theospi/portfolio/help/control/GlossaryTag.java $
* $Id:GlossaryTag.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help.control;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpManager;

/**
 * Matches keywords in the body to those in the glossary,
 * and places links around the keywords which link the glossary entries.
 * The glossary entry text is also available via a hover.
 * Linking or hovering can be turned on/off using the link and hover attributes
 * Linking is on by default, hover is off be default.
 * Use true/false as the attributes values to modify these from the defaults.
 * Hovering requires the following two lines be placed in the jsp, making sure
 * the path to the eport.js file is correct: <br/><br/>
 * <p/>
 * &lt;script language="JavaScript" src="../js/eport.js"&gt;&lt;/script&gt; <br/>
 * &lt;div id="tooltip" style="position:absolute;visibility:hidden;border:1px solid black;font-size:10px;layer-background-color:lightyellow;background-color:lightyellow;padding:1px"&gt;&lt;/div&gt; <br/>
 */
public class GlossaryTag extends BodyTagSupport {
   private boolean firstOnly = false;
   private boolean hover = false;
   private boolean link = true;
   private String glossaryLink;
   protected final Log logger = LogFactory.getLog(getClass());


   /**
    * Default processing of the start tag returning EVAL_BODY_BUFFERED.
    *
    * @return EVAL_BODY_BUFFERED
    * @throws javax.servlet.jsp.JspException if an error occurred while processing this tag
    * @see javax.servlet.jsp.tagext.BodyTag#doStartTag
    */

   public int doStartTag() throws JspException {
      try {
         pageContext.getOut().write("" +
            "<div id=\"tooltip\" style=\"position:absolute;visibility:hidden;" +
            "border:1px solid black;font-size:10px;layer-background-color:lightyellow;" +
            "background-color:lightyellow;padding:1px\"></div>");
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      return super.doStartTag();
   }

   public int doAfterBody() throws JspException {
      boolean wordState = true;
      boolean inPhrase = false;
      Collection foundWords = new HashSet();
      BodyContent body = getBodyContent();
      JspWriter out = body.getEnclosingWriter();
      StringBuffer buf = new StringBuffer();
      Reader reader = body.getReader();
      Set termSet = getHelpManager().getSortedWorksiteTerms();
      GlossaryEntry[] terms = new GlossaryEntry[termSet.size()];
      terms = (GlossaryEntry[]) termSet.toArray(terms);

      try {

         for (int i = 0; i < body.getBufferSize() - body.getRemaining(); i++) {
            char in = (char) reader.read();

            if (wordState && isWordBoundary(in)) {
               boolean currentPhrase = isPhraseStart(buf.toString() + in, terms);

               if (currentPhrase) {
                  inPhrase = true;
                  buf.append(in);
                  continue;
               }

               GlossaryEntry entry = searchGlossary(buf.toString(), terms);
               if (inPhrase && entry == null) {
                  outputPhrase(out, buf, foundWords, terms);
                  inPhrase = false;
               }
               else if (entry == null ||
                  (firstOnly && foundWords.contains(buf.toString()))) {
                  out.write(buf.toString());
               }
               else {
                  out.write(getMarkup(buf.toString(), entry));
                  foundWords.add(buf.toString());
               }

               if (in == '<') {
                  wordState = false;
               }
               out.write(in);

               buf = new StringBuffer();
            } else if (wordState) {
               buf.append(in);
            } else if (in == '>') {
               wordState = true;
               out.write(in);
            } else {
               out.write(in);
            }
         }

         if (buf != null) {
            handleLast(out, buf, terms, inPhrase, foundWords);
         }
         out.flush();
      } catch (IOException ioe) {
         logger.error(ioe.getMessage(), ioe);
      } finally {
         body.clearBody(); // Clear for next evaluation
      }
      return (SKIP_BODY);
   }

   protected void handleLast(JspWriter out, StringBuffer buf, GlossaryEntry[] terms,
                             boolean inPhrase, Collection foundWords) throws IOException {
      GlossaryEntry entry = searchGlossary(buf.toString(), terms);
      if (inPhrase && entry == null) {
         outputPhrase(out, buf, foundWords, terms);
         inPhrase = false;
      }
      else if (entry == null ||
         (firstOnly && foundWords.contains(buf.toString()))) {
         out.write(buf.toString());
      }
      else {
         out.write(getMarkup(buf.toString(), entry));
         foundWords.add(buf.toString());
      }
   }

   protected GlossaryEntry searchGlossary(String phrase, GlossaryEntry[] terms) {
      for (int i=0;i<terms.length;i++) {
         GlossaryEntry entry = terms[i];
         if (entry.getTerm().toLowerCase().equals(phrase.toLowerCase())) {
            return entry;
         }
      }
      return null;
   }

   protected boolean isPhraseStart(String phrase, GlossaryEntry[] terms) {
      if (phrase.length() == 0) {
         return false;
      }

      char start = phrase.charAt(0);

      if (!Character.isLetterOrDigit(start)) {
         return false;
      }

      // go backwards... more efficient
      for (int i=terms.length - 1;i>=0;i--) {
         GlossaryEntry entry = terms[i];
         if (entry.getTerm().toLowerCase().startsWith(phrase.toLowerCase())) {
            return true;
         }
      }
      return false;
   }

   protected void outputPhrase(Writer out, StringBuffer buf, Collection foundWords, GlossaryEntry[] terms) throws IOException {
      StringBuffer newBuf = new StringBuffer();
      boolean firstWord = false;
      boolean inPhrase = false;

      for (int i=0;i<buf.length();i++) {
         char in = buf.charAt(i);

         if (isWordBoundary(in) && newBuf.length() > 0) {
            boolean currentPhrase = false;
            GlossaryEntry entry = null;

            if (firstWord) {
               currentPhrase = isPhraseStart(newBuf.toString() + in, terms);
            }

            firstWord = true;
            if (!currentPhrase) {
               entry = searchGlossary(newBuf.toString(), terms);
            }
            else {
               inPhrase = true;
               newBuf.append(in);
               continue;
            }

            if (inPhrase && entry == null) {
               outputPhrase(out, newBuf, foundWords, terms);
               inPhrase = false;
            }
            else if (entry == null ||
               (firstOnly && foundWords.contains(newBuf.toString()))) {
               out.write(newBuf.toString());
               out.write(in);
            } else {
               out.write(getMarkup(newBuf.toString(), entry));
               foundWords.add(newBuf.toString());
               out.write(in);
            }

            newBuf = new StringBuffer();
         }
         else if (isWordBoundary(in)) {
            out.write(in);
         }
         else {
            newBuf.append(in);
         }
      }

      GlossaryEntry entry = searchGlossary(newBuf.toString(), terms);
      if (entry == null ||
         (firstOnly && foundWords.contains(newBuf.toString()))) {
         out.write(newBuf.toString());
      } else {
         out.write(getMarkup(newBuf.toString(), entry));
         foundWords.add(newBuf.toString());
      }
   }

   protected boolean isWordBoundary(char c) {
      return !String.valueOf(c).matches("[A-Za-z0-9]");
   }

   protected String getMarkup(String originalTerm, GlossaryEntry entry) {
      StringBuffer markup = new StringBuffer();
      String url = ServerConfigurationService.getServerUrl();
      String linkName = url + getHelpManager().getGlossary().getUrl() + "?id=" + entry.getId();

      markup.append("<a href=\"#\" onclick=\"openNewWindow('" + linkName + "');return false;\"");

      if (hover) {
         markup.append(" onMouseover=\"showtip(this,event,'" +
               replaceQuotes(entry.getDescription()) +
               "')\" onMouseOut=\"hidetip()\" ");
      }
      if (!link) {
         markup.append(" onClick=\"return false\" ");
      }
      markup.append(">" + originalTerm);
      markup.append("</a>");

      return markup.toString();

   }

   protected String replaceQuotes(String description) {
      // replace \ with \\
      description = description.replaceAll("\\\\", "\\\\\\\\");

      // replace ' with \'
      description = description.replaceAll("\\\'", "\\\\'");

      // replace " with &quot;
      description = description.replaceAll("\\\"", "&quot;");

      return description;
   }

   public HelpManager getHelpManager() {
      return (HelpManager) ComponentManager.getInstance().get("helpManager");
   }


   public boolean isHover() {
      return hover;
   }

   public void setHover(boolean hover) {
      this.hover = hover;
   }

   public boolean isLink() {
      return link;
   }

   public void setLink(boolean link) {
      this.link = link;
   }

   public String getGlossaryLink() {
      return glossaryLink;
   }

   public void setGlossaryLink(String glossaryLink) {
      this.glossaryLink = glossaryLink;
   }

   public boolean isFirstOnly() {
      return firstOnly;
   }

   public void setFirstOnly(boolean firstOnly) {
      this.firstOnly = firstOnly;
   }
}
