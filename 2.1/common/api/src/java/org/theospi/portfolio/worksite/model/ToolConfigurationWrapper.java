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
 * $Header
 * $Revision
 * $Date
 */

package org.theospi.portfolio.worksite.model;

import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.exception.IdUnusedException;
import org.theospi.portfolio.shared.model.OspException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Properties;

public class ToolConfigurationWrapper implements Serializable, ToolConfiguration{
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ToolConfiguration toolConfig;
   public ToolConfigurationWrapper(ToolConfiguration toolConfig){
      this.toolConfig = toolConfig;
   }

   //public String getToolId() {
   //   return toolConfig.getTool().getId();
   //}

   public String getTitle() {
      return toolConfig.getTitle();
   }

   public String getLayoutHints() {
      return toolConfig.getLayoutHints();
   }

   public int[] parseLayoutHints() {
      return toolConfig.parseLayoutHints();
   }

   public String getSkin() {
      return toolConfig.getSkin();
   }

   public String getPageId() {
      return toolConfig.getPageId();
   }

   public String getSiteId() {
      return toolConfig.getSiteId();
   }

   public SitePage getContainingPage() {
      SitePage returned = toolConfig.getContainingPage();

      if (returned == null) {
         Site site = null;
         try {
            site = SiteService.getSite(getSiteId());
         } catch (IdUnusedException e) {
            logger.error("", e);
            throw new OspException(e);
         }
         returned = site.getPage(getPageId());
      }
      return returned;
   }

   public String getId() {
      if (toolConfig == null) return null;
      return toolConfig.getId();
   }

	public void setLayoutHints(String layoutHints) {
		toolConfig.setLayoutHints(layoutHints);	
	}

	public void moveUp() {
		toolConfig.moveUp();	
	}
	
	public void moveDown() {
		toolConfig.moveDown();
	}
	
	public int getPageOrder() {
		return toolConfig.getPageOrder();
	}
	
	public Properties getConfig() {
		return toolConfig.getConfig();
	}
	
	public String getContext() {
		return toolConfig.getContext();
	}
	
	public Properties getPlacementConfig() {
		return toolConfig.getPlacementConfig();
	}
	
	public Tool getTool() {
		return toolConfig.getTool();
	}
	
	public void setTitle(String title) {
		toolConfig.setTitle(title);	
	}
	
	public void setTool(Tool tool) {
		toolConfig.setTool(tool);
	}
	
	public void save() {
		toolConfig.save();	
	}
}
