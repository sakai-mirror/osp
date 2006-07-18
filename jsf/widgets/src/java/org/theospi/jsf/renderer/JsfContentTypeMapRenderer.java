/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2006 The Sakai Foundation.
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

package org.theospi.jsf.renderer;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentTypeImageService;
import org.sakaiproject.jsf.util.RendererUtil;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.theospi.jsf.tag.JsfContentTypeMapTag;

public class JsfContentTypeMapRenderer extends Renderer {
   
   public boolean supportsComponentType(UIComponent component)
   {
      return (component instanceof UIOutput);
   }

   public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
      String fileType = (String) RendererUtil.getAttribute(context, component, "fileType");
      String mapType = (String) RendererUtil.getAttribute(context, component, "mapType");
      String displayName = (String) RendererUtil.getAttribute(context, component, "displayName");
      String pathPrefix = (String) RendererUtil.getAttribute(context, component, "pathPrefix");
      String var = (String) RendererUtil.getAttribute(context, component, "var");
      
      
      MimeType fileMimeType = new MimeType(fileType);//evaluateFileType(context);
      String result = getValue(fileMimeType.getValue(), mapType, getImageTypeService(), displayName, pathPrefix);
      
      if (var == null || var.length()==0) {
         context.getResponseWriter().write(result);
      }
      else {
         Map requestMap = context.getExternalContext().getRequestMap();
         requestMap.put(var, result);
      }
   }


   protected String getValue(String fileType, String mapType, ContentTypeImageService service, String displayName, String pathPrefix) {
      if (mapType.equals(JsfContentTypeMapTag.MAP_TYPE_IMAGE)) {
         String imgPath =  service.getContentTypeImage(fileType);
         String url = ServerConfigurationService.getServerUrl();
         return url + pathPrefix + imgPath;
      }
      else if (mapType.equals(JsfContentTypeMapTag.MAP_TYPE_NAME)) {
         return service.getContentTypeDisplayName(fileType);
      }
      else if (mapType.equals(JsfContentTypeMapTag.MAP_TYPE_EXTENSION)) {
         return service.getContentTypeExtension(fileType);
      }
      else {
         return null;
      }
   }


   protected ContentTypeImageService getImageTypeService() {
      return org.sakaiproject.content.cover.ContentTypeImageService.getInstance();
   }

}
