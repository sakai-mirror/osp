package org.theospi.jsf.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.util.ConfigurationResource;
import org.theospi.jsf.util.TagUtil;

public class FormLabelRenderer extends Renderer {
   
   private static final String RESOURCE_PATH;
   private static final String REQ_CHAR;
   private static final String CSS_LOC;

   static {
     ConfigurationResource cr = new ConfigurationResource();
     RESOURCE_PATH = "/" + cr.get("resources");
     REQ_CHAR = cr.get("req_field_char");
     CSS_LOC = RESOURCE_PATH + "/" + cr.get("cssFile");
   }
   
   public boolean supportsComponentType(UIComponent component)
   {
      return (component instanceof UIOutput);
   }

   /**
    * This renders html for the beginning of the tag.
    * 
    * @param context
    * @param component
    * @throws IOException
    */
   public void encodeBegin(FacesContext context, UIComponent component) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      String valueRequired = (String) RendererUtil.getAttribute(context, component, "valueRequired");
      String displayCharOnRight = (String) RendererUtil.getAttribute(context, component, "displayCharOnRight");
      if (valueRequired.equalsIgnoreCase("true") && 
            !displayCharOnRight.equalsIgnoreCase("true")) {
         writeReqChar(context, writer);
      }      
   }


   /**
    * @param context FacesContext for the request we are processing
    * @param component UIComponent to be rendered
    * @exception IOException if an input/output error occurs while rendering
    * @exception NullPointerException if <code>context</code> or <code>component</code> is null
    */
   public void encodeEnd(FacesContext context, UIComponent component) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      String valueRequired = (String) RendererUtil.getAttribute(context, component, "valueRequired");
      String displayCharOnRight = (String) RendererUtil.getAttribute(context, component, "displayCharOnRight");
      
      if (valueRequired.equalsIgnoreCase("true") &&
            displayCharOnRight.equalsIgnoreCase("true")) {
         writeReqChar(context, writer);
      }
   }
   
   protected void writeReqChar(FacesContext context, ResponseWriter writer) throws IOException {
      TagUtil.writeExternalCSSDependencies(context, writer, "osp.jsf.css", CSS_LOC);
      writer.write("<span class=\"osp_required_field\">");
      writer.write(REQ_CHAR);
      writer.write("</span>");
   }

}
