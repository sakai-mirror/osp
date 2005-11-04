package org.theospi.portfolio.shared.control.tag;

import org.sakaiproject.service.legacy.content.ContentTypeImageService;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import java.util.Map;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 28, 2005
 * Time: 9:23:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentTypeMapTag extends TagSupport {

   private static final String MAP_TYPE_IMAGE = "image";
   private static final String MAP_TYPE_NAME = "name";
   private static final String MAP_TYPE_EXTENSION = "extension";

   private String fileType;
   private String mapType;

   public ContentTypeMapTag() {
      init();
   }

   public int doStartTag() throws JspException {
      MimeType fileMimeType = evaluateFileType();
      String result = getValue(fileMimeType.getValue(), mapType, getImageTypeService());
      try {
         pageContext.getOut().write(result);
      }
      catch (IOException e) {
         throw new JspException(e);
      }
      return super.doStartTag();
   }

   protected void init() {
      mapType = MAP_TYPE_IMAGE;
   }

   protected String getValue(String fileType, String mapType, ContentTypeImageService service) {
      if (mapType.equals(MAP_TYPE_IMAGE)) {
         return service.getContentTypeImage(fileType);
      }
      else if (mapType.equals(MAP_TYPE_NAME)) {
         return service.getContentTypeDisplayName(fileType);
      }
      else if (mapType.equals(MAP_TYPE_EXTENSION)) {
         return service.getContentTypeExtension(fileType);
      }
      else {
         return null;
      }
   }

   public MimeType evaluateFileType() throws JspException {
      return (MimeType)ExpressionEvaluatorManager.evaluate(
           "fileType", fileType,
          MimeType.class, this, pageContext);
   }

   protected ContentTypeImageService getImageTypeService() {
      return org.sakaiproject.service.legacy.content.cover.ContentTypeImageService.getInstance();
   }

   public String getFileType() {
      return fileType;
   }

   public void setFileType(String fileType) {
      this.fileType = fileType;
   }

   public String getMapType() {
      return mapType;
   }

   public void setMapType(String mapType) {
      this.mapType = mapType;
   }

}
