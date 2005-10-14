/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/jsf/widgets/src/java/org/sakaiproject/jsf/tag/InstructionMessageTag.java $
* $Id: OspxTagHelper.java,v 1.1 2005/09/30 19:10:04 andersjb Exp $
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.theospi.jsf.util;



public class OspxTagHelper {

	public static boolean isVertical(String inValue)
		throws OspxTagAttributeValueException
	{
		if(inValue.equalsIgnoreCase("vertical") || 
				inValue.equalsIgnoreCase("y"))
			return true;
		if(inValue.equalsIgnoreCase("horizontal") || 
				inValue.equalsIgnoreCase("x"))
			return false;
			
		throw new OspxTagAttributeValueException(
					"A direction 'vertical' or 'horizontal' was expected but got '" + inValue + "'"
				);
	}
	
	
	public static boolean parseBoolean(String inValue)
		throws OspxTagAttributeValueException
	{
		if(inValue.equalsIgnoreCase("true") || 
				inValue.equalsIgnoreCase("yes") || 
				inValue.equalsIgnoreCase("1"))
			return true;
		if(inValue.equalsIgnoreCase("false") || 
				inValue.equalsIgnoreCase("no") || 
				inValue.equalsIgnoreCase("0"))
			return false;
			
		throw new OspxTagAttributeValueException(
					"A direction 'vertical' or 'horizontal' was expected but got '" + inValue + "'"
				);
	}
}