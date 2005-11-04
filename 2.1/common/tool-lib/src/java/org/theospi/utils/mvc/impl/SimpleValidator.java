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
 * $Header: /opt/CVS/osp2.x/common/tool-lib/src/java/org/theospi/utils/mvc/impl/SimpleValidator.java,v 1.1.1.1 2005/07/14 15:53:42 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.theospi.utils.mvc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.theospi.portfolio.shared.model.OspException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleValidator extends ValidatorBase implements Validator {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private List requiredFields;
   private String messageCode = "Required";
   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      return true;
   }

   /**
    * Validate an object, which must be of a class for which
    * the supports() method returned true.
    *
    * @param obj    Populated object to validate
    * @param errors Errors object we're building. May contain
    *               errors for this field relating to types.
    */
   public void validate(Object obj, Errors errors) {
      for (Iterator i=requiredFields.iterator();i.hasNext();) {
         String field = (String)i.next();
         validate(field, obj, errors);
      }
   }

   protected void validate(String field, Object obj, Errors errors) {
      if (obj instanceof Map) {
         Map map = (Map)obj;
         if (map.get(field) == null) {
            errors.rejectValue(field, messageCode, messageCode);
         }
      }
      else {
         PropertyDescriptor prop = null;
         try {
            prop = new PropertyDescriptor(field, obj.getClass());

            Object value = prop.getReadMethod().invoke(obj, new Object[]{});
            if (value == null || value.toString().length() == 0) {
               errors.rejectValue(field, messageCode, messageCode);
            }
         } catch (IntrospectionException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (IllegalAccessException e) {
            logger.error("", e);
            throw new OspException(e);
         } catch (InvocationTargetException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      }
   }

   public String getMessageCode() {
      return messageCode;
   }

   public void setMessageCode(String messageCode) {
      this.messageCode = messageCode;
   }

   public List getRequiredFields() {
      return requiredFields;
   }

   public void setRequiredFields(List requiredFields) {
      this.requiredFields = requiredFields;
   }
}
