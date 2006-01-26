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
package org.theospi.portfolio.matrix;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
//import org.theospi.portfolio.repository.RepositoryManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.theospi.portfolio.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.springframework.core.io.Resource;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class renders an object into an xml object.  This implementation
 * uses bean introspection to navigate the object model and convert into
 * a jdom model. In relies on the objectStructure xml file to specify which
 * properties to traverse.  The prevents circlular references from being
 * traversed.
 *
 * Valid values for type attribute are: collection, artifact, object.
 * If not specified object is assumed.
 *
 *
 */
public class GenericXmlRenderer implements PresentableObjectHome {
   protected final Log logger = LogFactory.getLog(getClass());
   private ArtifactFinder artifactFinder;
   private Resource objectStructure;
   private String rootName;
   private String supportedType;
   private String artifactType;

   protected Element getObjectStructureRoot(){
      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(getObjectStructure().getInputStream());
         return doc.getRootElement();
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   protected Element getXml(Object object) {
      Element matrixElement = new Element(getRootName());
      try {
         addObjectNodeInfo(matrixElement, object, getObjectStructureRoot());
      } catch (IntrospectionException e) {
         logger.error("",e);
      }

      return matrixElement;
   }

   protected boolean isTraversableType(PropertyDescriptor descriptor, Element structure) {
      if (structure == null) {
         return false;
      }
      if (structure.getChild(descriptor.getName()) != null || structure.getName().equals(descriptor.getName())){
         return true;
      }

      return false;
   }

   protected void addObjectNodeInfo(Element parentNode, Object object, Element structure) throws IntrospectionException {
      if (object == null) return;
      // go through each property... put each one in...
      logger.debug("adding object of class " + object.getClass());

      BeanInfo info = Introspector.getBeanInfo(object.getClass());

      PropertyDescriptor[] props = info.getPropertyDescriptors();

      for (int i = 0; i < props.length; i++) {
         PropertyDescriptor property = props[i];
         logger.debug("examining property: " + property.getName());
         if (isTraversableType(property, structure)){
            if (isCollection(property,structure)){
               addCollectionItems(parentNode, property, object, structure);
            } else if (isArtifact(property,structure)) {
               addArtifactItem(parentNode, property, object);
            } else {
               addItem(parentNode, property, object, structure);
            }
         } else {
            addItemToXml(parentNode, property, object);
         }

      }
   }

   protected void addItemToXml(Element parentNode, PropertyDescriptor prop, Object object) {
      String attribName = prop.getName();

      logger.debug("adding attribute: " + attribName);

      Method readMethod = prop.getReadMethod();
      if (readMethod == null || readMethod.getParameterTypes().length > 0) {
         logger.debug("skipping attrib: " + attribName);
         return;
      }

      Element attribute = new Element(attribName);
      Object attribValue = null;

      try {
         attribValue = readMethod.invoke(object, (Object[]) null);
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      }
      if (attribValue != null && attribValue.toString().length() > 0) {
         logger.debug("value for attrib " + attribName + " is not null.");
         attribute.addContent(attribValue.toString());
      }

      parentNode.addContent(attribute);
   }

   protected void addItem(Element parentNode, PropertyDescriptor prop, Object object, Element structure) {
      logger.debug("addItem()");

      Method readMethod = prop.getReadMethod();
      Element newElement = new Element(prop.getName());
      try {
         Object newObject = readMethod.invoke(object, (Object[]) null);
         parentNode.addContent(newElement);
         addObjectNodeInfo(newElement, newObject, structure.getChild(prop.getName()));
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      } catch (IntrospectionException e) {
         logger.error("could not get attribute", e);
      }
   }


   protected void addArtifactItem(Element parentNode, PropertyDescriptor prop, Object object) {
      logger.debug("addArtifactItem()");

      Method readMethod = prop.getReadMethod();

      Id artifactId = null;

      try {
         artifactId = (Id) readMethod.invoke(object, (Object[]) null);
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      }

      logger.debug("finding artifact with id=" + artifactId);

      Artifact art = getArtifactFinder().load(artifactId);
      if (art.getHome() instanceof PresentableObjectHome) {
         PresentableObjectHome home = (PresentableObjectHome) art.getHome();
         Element node = home.getArtifactAsXml(art);
         node.setName("artifact");
         parentNode.addContent(node);
      }
   }

   protected void addCollectionItems(Element parentNode, PropertyDescriptor prop, Object object, Element structure){
      logger.debug("addCollectionItems()");

      Method readMethod = prop.getReadMethod();
      Element newListElement = new Element(prop.getName());
      try {
         Object newObject = readMethod.invoke(object, (Object[]) null);
         parentNode.addContent(newListElement);
         Collection items = (Collection) newObject;
         for (Iterator i= items.iterator(); i.hasNext();){
            Element newElement = new Element(getCollectionItemName(prop.getName()));
            newListElement.addContent(newElement);
            addObjectNodeInfo(newElement, i.next(), structure.getChild(prop.getName()));
         }
      } catch (IllegalAccessException e) {
         logger.error("could not get attribute", e);
      } catch (InvocationTargetException e) {
         logger.error("could not get attribute", e);
      } catch (IntrospectionException e) {
         logger.error("could not get attribute", e);
      }

   }

   protected String getCollectionItemName(String listName){
      if (listName.endsWith("s")) {
         return listName.substring(0, listName.length() -1 );
      }
      return listName;
   }

   protected boolean isArtifact(PropertyDescriptor prop, Element structure) {
      Element child = structure.getChild(prop.getName());
      String typeAttribute = child.getAttributeValue("type");
      if (typeAttribute != null && typeAttribute.equals("artifact")){
         return true;
      }
      return false;
   }

   protected boolean isCollection(PropertyDescriptor prop, Element structure) {
      Element elementStructure = structure.getChild(prop.getName());
      String typeAttribute = elementStructure.getAttributeValue("type");
      if (typeAttribute != null && typeAttribute.equals("collection")){
         return true;
      }
      return false;
   }


   public Element getArtifactAsXml(Artifact artifact) {
      try {
         Class supportedType = Class.forName(getSupportedType());
         if (supportedType.isAssignableFrom(artifact.getClass())) {
            return getXml(artifact);
         }
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(getSupportedType() + " is not a valid class: " + e.getMessage(),e);
      }

      throw new OspException("Expecting object of type: "  + getSupportedType() + " but found object of type "  +
            artifact.getClass());
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public Resource getObjectStructure() {
      return objectStructure;
   }

   public String getRootName() {
      return rootName;
   }

   public void setRootName(String rootName) {
      this.rootName = rootName;
   }

   public void setObjectStructure(Resource objectStructure) {
      this.objectStructure = objectStructure;
   }

   public String getSupportedType() {
      return supportedType;
   }

   public void setSupportedType(String supportedType) {
      this.supportedType = supportedType;
   }

   public String getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(String artifactType) {
      this.artifactType = artifactType;
   }
}
