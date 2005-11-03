package org.theospi.portfolio.shared.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.TypeException;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 27, 2005
 * Time: 6:09:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TechnicalMetadata {
   private Id id;
   private Type type;
   private String name;
   private Date lastModified;
   private Date creation;
   private long size;
   private MimeType mimeType;
   private Agent owner;

   public TechnicalMetadata(Id id, ContentResource resource, Agent owner) {
      this.id = id;
      this.name = resource.getProperties().getProperty(
         resource.getProperties().getNamePropDescription());
      this.size = resource.getContentLength();
      mimeType = new MimeType(resource.getContentType());

      lastModified = getDate(resource, resource.getProperties().getNamePropModifiedDate());
      creation = getDate(resource, resource.getProperties().getNamePropCreationDate());
      this.owner = owner;
   }

   protected Date getDate(ContentResource resource, String propName) {
      try {
         Time time = resource.getProperties().getTimeProperty(propName);
         return new Date(time.getTime());
      }
      catch (EmptyException e) {
         return null;
      }
      catch (TypeException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * @return Returns the creation.
    */
   public Date getCreation() {
      return creation;
   }
   /**
    * @param creation The creation to set.
    */
   public void setCreation(Date creation) {
      this.creation = creation;
   }
   /**
    * @return Returns the lastModified.
    */
   public Date getLastModified() {
      return lastModified;
   }
   /**
    * @param lastModified The lastModified to set.
    */
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
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
    * @return Returns the size.
    */
   public long getSize() {
      return size;
   }
   /**
    * @param size The size to set.
    */
   public void setSize(long size) {
      this.size = size;
   }
   /**
    * @return Returns the type.
    */
   public Type getType() {
      return type;
   }
   /**
    * @param type The type to set.
    */
   public void setType(Type type) {
      this.type = type;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

}
