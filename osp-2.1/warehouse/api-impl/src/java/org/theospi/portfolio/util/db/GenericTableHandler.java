
/**********************************************************************************
* $URL$
* $Id$
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
package org.theospi.portfolio.util.db;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

/**
 * Generic table handler will work for most databases. If not,
 * then you can create database specific handlers having this as the
 * starting point.
 *
 * This code was inspired from the Dbloader.java from uPortal by JASIG
 *
 * @author <a href="felipeen@udel.edu">Luis F.C. Mendes</a> - University of Delaware
 * @version $Revision 1.0 $
 */
public class GenericTableHandler implements ContentHandler{

   private static final int UNSET = -1;
   private static final int DROP = 0;
   private static final int CREATE = 1;
   private static final int ALTER = 2;
   private static final int INDEX = 3;
   private static int mode = UNSET;
   private static StringBuffer stmtBuffer;
   private int treeLevel;
   private String tmpType;
   private String tmpParm; 
   private DbLoader loader;

   public GenericTableHandler(DbLoader loader){
	  this.loader = loader; 
	  System.out.println("Generic table handler for " + this.loader.getDbName() + " ...");
   }
	
	
   public void startDocument ()
   {
   }

   public void endDocument ()
   {
      System.out.println();
   }	
	
   public void startElement (String namespaceURI, String localName,
							 String qName, Attributes atts)
   {
      if (qName.equals("statement"))
      {
		 stmtBuffer = new StringBuffer(1024);
		 String statementType = atts.getValue("type");

		 if (mode == UNSET || mode != DROP
			 && statementType != null && statementType.equals("drop"))
		 {
			mode = DROP;

			System.out.print("Dropping tables...");

			if (!this.loader.isDropTables())
			   System.out.print("disabled.");
		 }
		 else if (mode == UNSET || mode != CREATE
				  && statementType != null && statementType.equals("create"))
		 {
			mode = CREATE;

			System.out.print("\nCreating tables...");

			if (!this.loader.isCreateTables())
			   System.out.print("disabled.");
		 }
		 else if (mode == UNSET || mode != ALTER
				  && statementType != null && statementType.equals("alter"))
		 {
			mode = ALTER;

			System.out.print("\nAltering tables...");

			if (!this.loader.isAlterTables())
			   System.out.print("disabled.");
		 }
		 else if (mode == UNSET || mode != INDEX
				  && statementType != null && statementType.equals("index"))
		 {
			mode = INDEX;

			System.out.print("\nIndexing tables...");

			if (!this.loader.isIndexTables())
			   System.out.print("disabled.");
		 }

      }
		
	  if (qName.equals("column-type")){
		 ++treeLevel;
		 tmpType = "";
	  }
	  if (qName.equals("type-param")){
		 ++treeLevel;
		 tmpParm = "";
	  }
   }

   public void endElement (String namespaceURI, String localName, String qName)
   {
      if (qName.equals("statement"))
      {
		 treeLevel = 0;
		 String statement = stmtBuffer.toString();

		 switch (mode)
		 {
			case DROP:
			   if (this.loader.isDropTables())
					this.loader.dropTable(Cascade.cascadeConstraint(statement));
			   //System.out.println(statement);
			   break;
			case CREATE:
			   if (this.loader.isCreateTables())
				  this.loader.createTable(statement);
			   //System.out.println(statement);
			   break;
			case ALTER:
			   if (this.loader.isAlterTables())
				  this.loader.alterTable(statement);
			   //System.out.println(statement);
			   break;
			case INDEX:
			   if (this.loader.isIndexTables())
				  this.loader.indexTable(statement);
			   //System.out.println(statement);
			   break;					
			default:
			   break;
		 }
      }
		
	  if(qName.equals("column-type"))
		 --treeLevel;
	  if(qName.equals("type-param"))
		 --treeLevel;
	  if(treeLevel == 0)
		 parseParamToDatabase();
   }

   public void characters (char ch[], int start, int length)
   {
	  if(treeLevel == 0)
		 stmtBuffer.append(ch, start, length);
	  else
		 if(treeLevel == 1)
			tmpType =  new String(ch, start, length);
		 else
			if(treeLevel == 2)
			   tmpParm =  new String(ch, start, length);	
   }

   protected void parseParamToDatabase(){
	  int parm = 0;
		
	  if(tmpParm != null && tmpParm.length() > 0){
		 stmtBuffer.append(tmpType.trim());
		 stmtBuffer.append("(" + tmpParm.trim() + ")");
	  }
	  else
		 if(tmpType != null && tmpType.length() > 0)
			stmtBuffer.append(tmpType.trim());

	  tmpParm = "";
	  tmpType = "";
   }
	

	
   public void setDocumentLocator (Locator locator)
   {
   }

   public void processingInstruction (String target, String data)
   {
   }

   public void ignorableWhitespace (char[] ch, int start, int length)
   {
   }

   public void startPrefixMapping (String prefix, String uri)
	  throws SAXException {};
   public void endPrefixMapping (String prefix) throws SAXException  {};
   public void skippedEntity(String name) throws SAXException {};
}
