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
package org.theospi.portfolio.admin;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.theospi.portfolio.admin.ImportRepositoryZip.RepositoryEntry;

public class TestXmlRead {
	
	public final static String INFO_FILE="INFO.XML";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Object[] metadata=null;
		try {
			String fileName = "C:\\java\\osp_prod_new\\ospi-export.zip";
			File file=new File(fileName);
	        ZipFile zipFile=new ZipFile(file);
	        XMLDecoder xdec=new XMLDecoder(retrieveFileFromZip(zipFile,INFO_FILE));
	        Map info=(Map)xdec.readObject();
	        //LOG.info("retrieved manifest");

	        // Set agents=(Set)info.get("agents");
	        Map directory=(Map)info.get("directory");
	        
	        Iterator iter=directory.keySet().iterator();

	         while (iter.hasNext()) {
	            String id=(String)iter.next();
	            //LOG.info("id="+id);
	            //RepositoryEntry next=new RepositoryEntry(directory.get(id));
	            Object foo = directory.get(id);
	         }

			//XMLDecoder xdec=new XMLDecoder(new FileInputStream());
			//metadata=(Object[])xdec.readObject();
			int i=0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    private static InputStream retrieveFileFromZip(ZipFile zipFile, String fileName) throws IOException {
        ZipEntry zipEntry=zipFile.getEntry(fileName);
        return zipFile.getInputStream(zipEntry);

     }

}
