package org.theospi.portfolio.shared.mgt;

import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.exception.ServerOverloadException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.Stack;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 3:12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentEntityWrapper implements ContentResource {

   private ContentResource base;
   private String reference;

   public ContentEntityWrapper(ContentResource base, String reference) {
      this.base = base;
      this.reference = reference;
   }

   public int getContentLength() {
      return base.getContentLength();
   }

   public String getContentType() {
      return base.getContentType();
   }

   public byte[] getContent() throws ServerOverloadException {
      return base.getContent();
   }

   public InputStream streamContent() throws ServerOverloadException {
      return base.streamContent();
   }

   public String getUrl() {
      return ServerConfigurationService.getAccessUrl() + getReference();
   }

   public String getReference() {
      return reference;
   }

   public String getId() {
      return base.getId();
   }

   public ResourceProperties getProperties() {
      return base.getProperties();
   }

   public Element toXml(Document doc, Stack stack) {
      return base.toXml(doc, stack);
   }

   public ContentResource getBase() {
      return base;
   }

   public void setBase(ContentResource base) {
      this.base = base;
   }
}
