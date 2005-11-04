/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/component/InstructionMessageComponent.java $
 * $Id: XHeaderComponent.java,v 1.4 2005/10/13 17:28:14 chmaurer Exp $
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/

package org.theospi.jsf.component;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIOutput;
import javax.faces.component.UIComponent;

import org.sakaiproject.jsf.util.RendererUtil;


public class XHeaderComponent extends UIOutput
{

	/**
	 * Constructor-
	 * Indicates the component that this class is linked to
	 *
	 */
	public XHeaderComponent()
	{
		super();
		this.setRendererType("org.theospi.XHeader");
	}
	
	
	/**
	 * pulls out the drawer component
	 *
	 */
	public XHeaderDrawerComponent getDrawerComponent()
	{
      for (Iterator iter = this.getChildren().iterator(); iter.hasNext();)
		{ 
	    	UIComponent child = (UIComponent) iter.next();

	    	if (child instanceof XHeaderDrawerComponent) {
				return (XHeaderDrawerComponent)child;
	        }
	    }
	    return null;
	}
}



