
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
package org.theospi.portfolio.shared.model;

import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.exception.ServerOverloadException;

import java.io.InputStream;

public class Node {

   private Id id;
   private String name;
   private String displayName;
   private TechnicalMetadata technicalMetadata;
   private MimeType mimeType;
   private String externalUri;
   private String fileType;
   private ContentResource resource;
   private boolean hasCopyright = false;

   public Node(Id id, ContentResource resource, Agent owner) {
      this.resource = resource;
      this.id = id;
      name = resource.getProperties().getProperty(
            resource.getProperties().getNamePropDisplayName());
      displayName = name;
      
      //check for copyright
      hasCopyright = Boolean.getBoolean(resource.getProperties().getProperty(
            resource.getProperties().getNamePropCopyrightAlert()));
      
      externalUri = resource.getUrl();
      mimeType = new MimeType(resource.getContentType());
      String propName = resource.getProperties().getNamePropStructObjType();
      String saType = resource.getProperties().getProperty(propName);
      fileType = (saType != null && !saType.equals("")) ? saType : "fileArtifact"; 
         
      setTechnicalMetadata(new TechnicalMetadata(id, resource, owner));
   }
   
   /**
    * @return Returns the externalUri.
    */
   public String getExternalUri() {
      return externalUri;
   }



   /**
    * @param externalUri The externalUri to set.
    */
   public void setExternalUri(String externalUri) {
      this.externalUri = externalUri;
   }



   /**
    * @return Returns the mimeType.
    */
   public MimeType getMimeType() {
      return mimeType;
   }



   /**
    * @param mimeType The mimeType to set.
    */
   public void setMimeType(MimeType mimeType) {
      this.mimeType = mimeType;
   }



   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }



   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }



   /**
    * @return Returns the displayName.
    */
   public String getDisplayName() {
      return displayName;
   }
   /**
    * @param displayName The displayName to set.
    */
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }
   /**
    * @return Returns the technicalMetadata.
    */
   public TechnicalMetadata getTechnicalMetadata() {
      return technicalMetadata;
   }



   /**
    * @param technicalMetadata The technicalMetadata to set.
    */
   public void setTechnicalMetadata(TechnicalMetadata technicalMetadata) {
      this.technicalMetadata = technicalMetadata;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public InputStream getInputStream() {
      try {
         return resource.streamContent();
      }
      catch (ServerOverloadException e) {
         throw new RuntimeException(e);
      }
   }
   
   public ContentResource getResource() {
      return resource;
   }

   public String getFileType() {
      return fileType;
   }

   public void setFileType(String fileType) {
      this.fileType = fileType;
   }

   public boolean isHasCopyright() {
      return hasCopyright;
   }

   public void setHasCopyright(boolean hasCopyright) {
      this.hasCopyright = hasCopyright;
   }

}
