/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.theospi.jsf.renderer;

import org.sakaiproject.jsf.renderer.InputRichTextRenderer;
import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.util.ConfigurationResource;
import org.theospi.jsf.intf.InitObjectContainer;

import javax.faces.context.ResponseWriter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 21, 2005
 * Time: 10:23:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinkListInputRichTextRenderer extends InputRichTextRenderer {

   private static final String RESOURCE_PATH;
   private static final String JS_LOC;
   private static final String INSERT_LOC;
   private static final MessageFormat LIST_ITEM_FORMAT =
         new MessageFormat(",\"{0}\" : \"<a href=''{1}''>{0}</a>\"");

   static {
      ConfigurationResource cr = new ConfigurationResource();
      RESOURCE_PATH = "/" + cr.get("resources");
      JS_LOC = RESOURCE_PATH + "/" + cr.get("inputRichTextScript");
      INSERT_LOC = RESOURCE_PATH + "/" + cr.get("inputRichTextInsertImage");
   }

   protected void writeExternalScripts(Locale locale, ResponseWriter writer) throws IOException {
      super.writeExternalScripts(locale, writer);
      writer.write("<script type=\"text/javascript\" src=\"" + JS_LOC + "\"></script>\n");
   }

   protected void writeAdditionalConfig(FacesContext context, UIComponent component,
                                        String configVar, String clientId, String toolbar,
                                        int widthPx, int heightPx, Locale locale, ResponseWriter writer)
         throws IOException {

      super.writeAdditionalConfig(context, component, configVar, clientId, toolbar,
            widthPx, heightPx, locale, writer);
      Object attchedFiles = RendererUtil.getAttribute(context,  component, "attachedFiles");
      if (attchedFiles != null && getSize(attchedFiles) > 0) {
         String arrayVar = configVar + "_Resources";

         writer.write("  var " + arrayVar + "= {\n");
         writer.write("\"select a file url to insert\" : \"\"\n");

         if (attchedFiles instanceof Map) {
            writer.write(outputFiles((Map)attchedFiles));
         }
         else {
            writer.write(outputFiles((List)attchedFiles));
         }

         writer.write("};\n");

         writer.write(  "sakaiRegisterResourceList(");
         writer.write(configVar + ",'" + INSERT_LOC + "'," + arrayVar);
         writer.write(");\n");

         writer.write("  " + configVar + ".toolbar = " + addToolbar(toolbar) + ";\n");
      }
      registerWithParent(component, configVar, clientId);
   }

   protected void registerWithParent(UIComponent component, String configVar, String clientId) {

      InitObjectContainer parentContainer = null;

      UIComponent testContainer = component.getParent();
      while (testContainer != null) {
         if (testContainer instanceof InitObjectContainer) {
            parentContainer = (InitObjectContainer)testContainer;

            String script = " resetEditor(\"" + clientId + "_inputRichText\"," + configVar + ");\n";

            parentContainer.addInitScript(script);
         }
         testContainer = testContainer.getParent();
      }
   }

   protected String outputFiles(Map map) {
      StringBuffer sb = new StringBuffer();

      for (Iterator i=map.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();

         LIST_ITEM_FORMAT.format(new Object[]{entry.getValue(), entry.getKey()}, sb, null);
      }

      return sb.toString();
   }

   protected String outputFiles(List list) {
      StringBuffer sb = new StringBuffer();

      for (Iterator i=list.iterator();i.hasNext();) {
         Object value = i.next();

         String url;
         String label;

         if (value instanceof SelectItem) {
            SelectItem item = (SelectItem)value;
            url = item.getValue().toString();
            label = item.getLabel();
         }
         else {
            url = value.toString();
            label = value.toString();
         }

         LIST_ITEM_FORMAT.format(new Object[]{label, url}, sb, null);
      }

      return sb.toString();
   }

   protected int getSize(Object attchedFiles) {
      if (attchedFiles instanceof Map) {
         return ((Map)attchedFiles).size();
      }
      else {
         return ((List)attchedFiles).size();
      }
   }

   protected String addToolbar(String toolbar) {
      int pos = toolbar.lastIndexOf("]");
      toolbar = toolbar.substring(0, pos) +
         ",[\"filedropdown\", \"insertfile\", ]" +
         toolbar.substring(pos);
      return toolbar;
   }

}
