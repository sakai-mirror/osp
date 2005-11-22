/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/component/InstructionMessageComponent.java $
 * $Id: XHeaderDrawerComponent.java,v 1.2 2005/10/03 21:46:03 andersjb Exp $
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

import org.theospi.jsf.intf.InitObjectContainer;

import javax.faces.component.UIOutput;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.ArrayList;


public class XHeaderDrawerComponent extends UIOutput implements InitObjectContainer
{

	private String divId = null;
	private List initScripts = null;

	/**
	 * Constructor-
	 * Indicates the component that this class is linked to
	 *
	 */
	public XHeaderDrawerComponent()
	{
		super();
      setInitScripts(new ArrayList());
		this.setRendererType("org.theospi.XHeaderDrawer");
	}
	
	public void setDivId(String inDivId)
	{
		divId = inDivId;
	}
	
	public String getDivId()
	{
		return divId;
	}

   public void addInitScript(String script) {
      getInitScripts().add(script);
   }

   public List getInitScripts() {
      return initScripts;
   }

   public void setInitScripts(List initScripts) {
      this.initScripts = initScripts;
   }

}



