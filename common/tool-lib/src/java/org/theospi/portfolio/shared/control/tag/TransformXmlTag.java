package org.theospi.portfolio.shared.control.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;

//FIXME: This is shamelessly ripped off from RenderPresentationTag -- decide whether it should be common or not

public class TransformXmlTag extends TagSupport {
	
	   private Transformer template = null;
	   private Document doc = null;
	   private URIResolver uriResolver;
	   private Boolean omitProlog = false;

	   public final int doStartTag() throws JspException {
		   System.out.println("Rendering TransformXmlTag");
		   if (uriResolver == null) {
			   uriResolver = new URIResolver() {
				public Source resolve(String href, String base) throws TransformerException {
					//We don't have a resolver, so let the template try on its own
					return null;
				}
			   };
		   }
	      if(doc != null) {
	         // transform xml and spit it out
	         try {
	        	 template.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
	            template.setURIResolver(uriResolver);
	            template.transform(new JDOMSource(doc),
	               new StreamResult(pageContext.getOut()));
	         } catch (TransformerException e) {
	            throw new JspException(e);
	         }
	      }
	      return EVAL_BODY_INCLUDE;
	   }

	   public Transformer getTemplate() {
	      return template;
	   }

	   public void setTemplate(Object template) {
	      setTemplate((Transformer) template);
	   }

	   public void setTemplate(Transformer template) {
	      this.template = template;
	   }

	   public Document getDoc() {
	      return doc;
	   }

	   public void setDoc(Object doc) {
	      if(doc instanceof Document)
	         setDoc((Document) doc);
	   }

	   public void setDoc(Document doc) {
	      this.doc = doc;
	   }

	   public URIResolver getUriResolver() {
	      return uriResolver;
	   }

	   public void setUriResolver(URIResolver uriResolver) {
	      this.uriResolver = uriResolver;
	   }
	   
	   public void setUriResolver(Object uriResolver) {
	      setUriResolver((URIResolver) uriResolver);
	   }
	   
	   public void setOmitProlog(Boolean value) {
		   this.omitProlog = value;
	   }
	   
	   public Boolean getOmitProlog() {
		   return omitProlog;
	   }
}
