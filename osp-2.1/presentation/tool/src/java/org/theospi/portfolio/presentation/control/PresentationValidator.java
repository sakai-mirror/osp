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
 * $Header: /opt/CVS/osp2.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/PresentationValidator.java,v 1.2 2005/08/19 21:30:54 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.portfolio.presentation.control;

import org.springframework.validation.Errors;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.sakaiproject.metaobj.utils.TypedMap;
import org.theospi.utils.mvc.impl.ValidatorBase;
import org.jdom.Element;
import org.jdom.IllegalNameException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 2:37:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationValidator extends ValidatorBase {

   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      if (PresentationTemplate.class.isAssignableFrom(clazz)) return true;
      if (PresentationItemDefinition.class.isAssignableFrom(clazz)) return true;
      if (Presentation.class.isAssignableFrom(clazz)) return true;
      if (PresentationItem.class.isAssignableFrom(clazz)) return true;
      if (TemplateFileRef.class.isAssignableFrom(clazz)) return true;
      if (PresentationLayout.class.isAssignableFrom(clazz)) return true;
      return false;
   }

   /**
    * Validate a presentation object, which must be of a class for which
    * the supports() method returned true.
    *
    * @param obj    Populated object to validate
    * @param errors Errors object we're building. May contain
    *               errors for this field relating to types.
    */
   public void validate(Object obj, Errors errors) {
      if (obj instanceof PresentationTemplate) validateTemplate(obj, errors);
      if (obj instanceof PresentationItem) validateItem(obj, errors);
      if (obj instanceof PresentationItemDefinition) validateItemDefinition(obj, errors);
      if (obj instanceof Presentation) validatePresentation(obj, errors);
      if (obj instanceof TemplateFileRef) validateTemplateFileRef((TemplateFileRef)obj, errors);
      if (obj instanceof PresentationLayout) validateLayout((PresentationLayout)obj, errors);
   }

   protected void validateTemplateFileRef(TemplateFileRef templateFileRef, Errors errors) {
      if (templateFileRef.getUsage() == null ||
         templateFileRef.getUsage().equals("")) {
         errors.rejectValue("usage", "error.required", "required");
      } else if (!isValidXMLElementName(templateFileRef.getUsage())){
            errors.rejectValue("usage", "error.invalidXmlElementName", "invalid name");
      }
      if (templateFileRef.getFileId() == null) {
         errors.rejectValue("fileId", "error.required", "required");
      }
   }

   protected void validateTemplateFirstPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;
      if (template.getName() == null || template.getName().length() == 0) {
         errors.rejectValue("name", "error.required", "required");
      }
   }

   protected void validateTemplateSecondPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;
      if (template.getRenderer() == null ||
            template.getRenderer().getValue() == null ||
            template.getRenderer().getValue().length() == 0) {
         errors.rejectValue("renderer", "error.required", "required");
      }
   }

   protected void validateTemplateThirdPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;

      if (template.getItem().getAction() != null && template.getItem().getAction().equals("addItem")){
         if (template.getItem().getType() == null || template.getItem().getType().length() == 0){
            errors.rejectValue("item.type", "error.required", "required");
         }
         if (template.getItem().getName() == null || template.getItem().getName().length() == 0){
            errors.rejectValue("item.name", "error.required", "required");
         } else if (!isValidXMLElementName(template.getItem().getName())){
            errors.rejectValue("item.name", "error.invalidXmlElementName", "invalid name");
         }
         if (template.getItem().getTitle() == null || template.getItem().getTitle().length() == 0){
            errors.rejectValue("item.title", "error.required", "required");
         }
      }
   }

   protected void validateTemplateFourthPage(Object obj, Errors errors){
      PresentationTemplate template = (PresentationTemplate) obj;

      if (template.getFileRef().getAction() != null && template.getFileRef().getAction().equals("addFile")){
         if (template.getFileRef().getUsage() == null || template.getFileRef().getUsage().length() == 0){
            errors.rejectValue("fileRef.usage", "error.required", "required");
         } else if (!isValidXMLElementName(template.getFileRef().getUsage())){
               errors.rejectValue("fileRef.usage", "error.invalidXmlElementName", "invalid name");
         }
         if (template.getFileRef().getFileId() == null ||
               template.getFileRef().getFileId().length() == 0){
            errors.rejectValue("fileRef.fileId", "error.required", "required");
         }
      }
   }

   protected void validateTemplate(Object obj, Errors errors) {
      validateTemplateFirstPage(obj, errors);
      validateTemplateSecondPage(obj, errors);
      validateTemplateThirdPage(obj, errors);
      validateTemplateFourthPage(obj, errors);
   }

   protected void validateItem(Object obj, Errors errors) {
      PresentationItem item = (PresentationItem) obj;

   }

   protected boolean isValidXMLElementName(String name){
      try {
         Element element = new Element(name);
      } catch (IllegalNameException e){
         return false;
      }
      return true;
   }

   protected void validateItemDefinition(Object obj, Errors errors) {
      PresentationItemDefinition itemDef = (PresentationItemDefinition) obj;
      if (itemDef.getName() == null || itemDef.getName().length() == 0) {
         errors.rejectValue("name", "error.required", "name is required");
      }
      if (itemDef.getTitle() == null || itemDef.getTitle().length() == 0) {
         errors.rejectValue("title", "error.required", "title is required");
      }

   }

   protected void validatePresentation(Object obj, Errors errors) {
      validatePresentationFirstPage(obj, errors);
      validatePresentationSecondPage(obj, errors);
      validatePresentationThirdPage(obj, errors);
   }

   protected void validatePresentationFirstPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;
      if (presentation.getName() == null || presentation.getName().length() == 0) {
         errors.rejectValue("name", "error.required", "name is required");
      }

      if (presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
         presentation.getTemplate().setId(Presentation.FREEFORM_TEMPLATE_ID);
      }

      if (presentation.getTemplate().getId() == null ||
         presentation.getTemplate().getId().getValue() == null ||
         presentation.getTemplate().getId().getValue().length() == 0) {
         errors.rejectValue("template.id", "error.required", "template is required");
      }

      if (presentation.getExpiresOnBean() != null){
         presentation.setExpiresOn(presentation.getExpiresOnBean().getDate());
      }
   }

   protected void validatePresentationSecondPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;
   }

   protected void validatePresentationThirdPage(Object obj, Errors errors) {
      Presentation presentation = (Presentation) obj;

   }

   public void validatePresentationProperties(Presentation presentation, Errors errors) {
      TypedMap properties = presentation.getProperties();
      if (properties != null && presentation.getTemplate().getDocumentRoot().length() != 0) {
         PresentationPropertiesValidator propertyValidator = new PresentationPropertiesValidator();
         pushNestedPath("properties.", errors);
         propertyValidator.validate(properties, errors);
         popNestedPath(errors);
      }
   }
   
   protected void validateLayout(PresentationLayout layout, Errors errors) {
      if (layout.isValidate()) {
         if (layout.getName() == null || layout.getName().length() == 0) {
            errors.rejectValue("name", "error.required", "name is required");
         }
         if (layout.getXhtmlFileId() == null || 
               layout.getXhtmlFileId().getValue() == null || 
               layout.getXhtmlFileId().getValue().length() == 0) {
            errors.rejectValue("xhtmlFileId", "error.required", "XHTML file is required");
         }
      }
   }
}
