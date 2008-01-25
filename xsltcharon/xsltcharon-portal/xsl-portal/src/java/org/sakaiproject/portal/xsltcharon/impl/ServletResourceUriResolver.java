package org.sakaiproject.portal.xsltcharon.impl;

import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jul 17, 2007
 * Time: 10:09:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServletResourceUriResolver implements URIResolver {

   private ServletContext context;

   public ServletResourceUriResolver(ServletContext context) {
      this.context = context;
   }

   public Source resolve(String href, String base) throws TransformerException {
      return new StreamSource(context.getResourceAsStream(href));
   }
}
