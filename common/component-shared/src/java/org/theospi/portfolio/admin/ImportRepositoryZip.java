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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.exception.*;
import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.sql.cover.SqlService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;

/**
 * 
 * This imports a zip file created by exporting OSP Repository files into Sakai Resources (via Sakai ContentHosting).
 * 
 * Usage:
 *  
 *    Examine hard-coded constants and verify their values
 *    Install as a Sakai Quartz Scheduler Job
 *    This program respects the osp_repository_lock table, so it may be best to run it before importing that data
 *    Setup Quartz Job to run once (mostly harmless to run multiple times, but be careful about OVERWRITE_PRE_EXISTING_FILES setting)
 * 
 * Known issues:
 * 
 *      All file creation dates show as the date and time that this program is run
 *      Empty folders will not be re-created
 *      Processing stops abruptly when Exceptions occur
 *      Exceptions occur when attempting to delete locked content
 *      Configuration is hard-coded
 *      
 * 
 */
public class ImportRepositoryZip implements Job {
    
  private final static Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportRepositoryZip.class);

  public final static boolean OVERWRITE_PRE_EXISTING_FILES=true;
  public final static String DEFAULT_PATH="/tmp";
  public final static String DEFAULT_FILENAME="ospi-export";
  public final static String DEFAULT_SUFFIX=".zip";
  public final static String INFO_FILE="INFO.XML";
  
  
  /* The trailing slash on the following three constants has been added as a workaround
   * to accommodate the unnecessary slash that was introduced by the export tool.
   */
  public final static String FILE_FOLDER="files/";
  public final static String LOST_FOLDER="lost-and-found/";
  public final static String TECH_FOLDER="tech-metadata/";
  
  public final static String PORTFOLIO_LOST_FILES="PORTFOLIO-Lost-And-Found";
  public final static String DESTINATION_PREFIX="portfolio"; //try to avoid filename collisions by putting stuff in a special folder
  
  Map siteMap;
  
  ContentHostingService contentHostingService;
   
  HashMap nodes; 
  
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        LOG.info("Quartz job started: "+this.getClass().getName());

        setupSiteMap(); // TODO improve this by relying on IoC to set this value 
        
        contentHostingService=(ContentHostingService)ComponentManager.get(ContentHostingService.class);
        try {
            importFromZip(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException(e);
        } finally
        {
        	LOG.info("Quartz job stopped: "+this.getClass().getName());
        }
    }
    
    
    public void importFromZip(String[] args) throws Exception {
		String fileName = (args.length > 0) ? args[0] : (DEFAULT_PATH + "/"
				+ DEFAULT_FILENAME + DEFAULT_SUFFIX);
		importFromZip(fileName);
	}

     public void importFromZip(String fileName) throws Exception {
        LOG.info("reading: "+fileName);
        File file=new File(fileName);
        ZipFile zipFile=new ZipFile(file);
        XMLDecoder xdec=new XMLDecoder(this.retrieveFileFromZip(zipFile,INFO_FILE));
        Map info=(Map)xdec.readObject();
        LOG.info("retrieved manifest");

        // Set agents=(Set)info.get("agents");
        Map directory=(Map)info.get("directory");

        //TODO: it would be better if all the files were not owned by Admin...
        /*
         * By starting a session for admin, all files created will be owned by Administrator. If a user has already logged in, then
         * it is possible to log-in as that user and create files on their behalf. However, if a user has never logged in, then
         * PermissionExceptions are generated when attempting to create files. 
         * 
         */
        UsageSessionService.startSession("admin", "local", "quartzOspRepositoryImport");

        // makeFolders(directory);
        importFiles(directory,zipFile);

        //TODO 9/6/05 - figure out the appropriate way to close the session
        //  see SakaiMailet.java for an example
        //UsageSessionService.closeSession();
        
     }

      private void importFiles(Map directory, ZipFile zipFile) throws Exception {
    	  LOG.info("importFiles(Map directory, ZipFile zipFile)");

          Iterator iter=directory.keySet().iterator();

         while (iter.hasNext()) {
            String id=(String)iter.next();
            LOG.info("id="+id);
            RepositoryEntry next=new RepositoryEntry(directory.get(id));

            if (next.isFile()) {
                
                LOG.info("FILE: "+next.getPath()+"/"+next.getName()+" ("+next.getOwnerId()+")");

               String fileSrc=makeAbsoluteExportPath(FILE_FOLDER,next.getPath(),next.getName());
               String techSrc=makeAbsoluteExportPath(TECH_FOLDER,next.getPath(),next.getName()+".xml");
               
               
               Object[] metadata=null;

               try {
                  XMLDecoder xdec=new XMLDecoder(this.retrieveFileFromZip(zipFile,techSrc));
                  metadata=(Object[])xdec.readObject();
               } catch (Exception e) {
                  System.err.println("unable to retrieve technical metadata for: "+techSrc);
               }
               next.ownerId = (String) metadata[5];
               File newFile=next.getDestinationFile();
               ContentResource resource=createFile(retrieveFileFromZip(zipFile,fileSrc),newFile,next.getMimetype(),metadata);
               
               String id_url=resource.getId();
               setUuid(id_url,id);

            }
         }
      }

      private InputStream retrieveFileFromZip(ZipFile zipFile, String fileName) throws IOException {
         ZipEntry zipEntry=zipFile.getEntry(fileName);
         return zipFile.getInputStream(zipEntry);

      }

      private String makeAbsoluteExportPath(String prefixFolder, String rootDirectory, String nodeName) {
          StringBuffer dest=new StringBuffer();
          dest.append(prefixFolder);
          //dest.append(File.separator);
          dest.append(rootDirectory);
          dest.append("/");
          dest.append(nodeName);
          return dest.toString();
       }
      
      public String combine(String str1, String str2) {
          if (str2.startsWith("/")) str2 = str2.substring(1);
          if (str1.endsWith("/")) return str1 + str2;
          return str1 + "/" + str2;
       }

      // Some hard coded IU data. (Sorry about that! Use Spring/IoC instead?)
      private void setupSiteMap() {
          siteMap=new HashMap();
          siteMap.put("1115229722875-12349","9ada00e0-6adc-47c3-800e-6fccb2eec1f7");
          siteMap.put("1115229894578-12362","28a0431d-78ef-4548-0081-0f5c60de9c0f");
          siteMap.put("1106082688024-502","01ed3cd1-5b2c-435b-80ce-498cb86f5c23");
          siteMap.put("1107659649528-2622","1a8b9e06-0472-40ad-00e6-5aa5f6e6007f");
          siteMap.put("1108350492828-4364","5f2eb35a-2761-4d13-8053-139f29c1b1d5");
          siteMap.put("1107660631303-2642","39b67e9f-74d9-406c-004b-21448116dd97");
          siteMap.put("1109356488281-7423","4f7c73a5-099b-4ff8-009c-015ce95a5e28");
          siteMap.put("1118172372562-13792","8d1463a3-24a0-4164-809b-be85d530e120");
          siteMap.put("1108574143593-4981","632244b8-1d03-4959-0047-e9f55d42b112");
          siteMap.put("1108053741500-3101","4f279ea3-9384-43bd-801d-62928a5438fa");
          siteMap.put("1108072794656-3454","9d6e2738-e505-4aaa-807d-d459f008d01a");
          siteMap.put("1108066961328-3367","6e7aa085-e48b-4f65-8082-78d27d0185cc");
          siteMap.put("1108060528921-3232","f7957bf4-c79d-437a-80aa-0497915a3540");
          siteMap.put("1108060601453-3253","0b71a6e4-6014-4251-80c8-f1784faf54a6");
          siteMap.put("1115233562078-12376","763a8362-c9de-45bb-80fc-5a44e45f19d4");
          siteMap.put("1121789659488-3","31c1c5d8-7d46-4e73-006e-de669fe8ece3"); //harmless testing...
      }

      
    //handy stuff derived from org.theospi.portfolio.admin.ImportResourcesTask
      
    protected ContentCollection createOrGetCollection(String filename)
      throws InconsistentException, PermissionException, IdUsedException, IdInvalidException, TypeException {
        
        File dir=new File(filename);
        return createOrGetCollection(dir);

    }

    protected ContentCollection createOrGetCollection(File dir)
    throws InconsistentException, PermissionException, IdUsedException, IdInvalidException, TypeException {

      LOG.info("createOrGetCollection("+dir.getPath());
      try {
         return contentHostingService.getCollection(getUnixDirPath(dir));
      } catch (IdUnusedException e) {
         // wasn't found, so we need to create it
         return createCollection(getUnixDirPath(dir), dir);
      }
   }
    
    protected void setUuid(String id,String uuid) throws SQLException {
        final Connection connection = SqlService.borrowConnection();
        boolean wasCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        String sql = "update CONTENT_RESOURCE set RESOURCE_UUID = ? where RESOURCE_ID = ?";
        Object[] fields = new Object[2];
        fields[0] = uuid;
        fields[1] = id;
        SqlService.dbWrite(connection, sql, fields);
        
        connection.commit();
        connection.setAutoCommit(wasCommit);
        SqlService.returnConnection(connection);
    }

      
    protected ContentCollection createCollection(String path, File file)
    throws PermissionException, IdUsedException, IdInvalidException, InconsistentException {
          
    ResourcePropertiesEdit resourceProperties = contentHostingService.newResourceProperties();

      resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
      resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, file.getName());

      return contentHostingService.addCollection (path, resourceProperties);
   }
    
    protected String getUnixDirPath(File dir) {
        return getUnixFilePath(dir) + '/';
     }

     protected String getUnixFilePath(File dir) {
        String path = dir.getPath();
        if (!File.separator.equals("/")) {
           path = path.replace(File.separatorChar, '/');
        }
        return path;
     }

     protected ContentResource createFile(InputStream fromStream, File toFile, String fileType, Object[] properties) throws IOException, InconsistentException, PermissionException, IdUsedException, IdInvalidException, TypeException, IdUnusedException, InUseException, OverQuotaException, ServerOverloadException {
         
         boolean overWrite=OVERWRITE_PRE_EXISTING_FILES;
         
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
     
         int c = fromStream.read();

         while (c != -1) {
            bos.write(c);
            c = fromStream.read();
         }

         byte[] fileBytes = bos.toByteArray();

         ResourcePropertiesEdit resourceProperties = contentHostingService.newResourceProperties ();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, toFile.getName());
         resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, toFile.getName());
         resourceProperties.addProperty (ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");

         //TODO: these are not working...
         resourceProperties.addProperty (ResourceProperties.PROP_CREATOR, (String)properties[5]);
         resourceProperties.addProperty (ResourceProperties.PROP_CREATION_DATE, ""+(Date)properties[0]);
         resourceProperties.addProperty (ResourceProperties.PROP_MODIFIED_BY, (String)properties[5]);
         resourceProperties.addProperty (ResourceProperties.PROP_MODIFIED_DATE, ""+(Date)properties[1]);
         
         boolean exists = false;

         try {
            contentHostingService.getResource(getUnixFilePath(toFile));
            exists = true;
         } catch (IdUnusedException e) {
            exists = false;
         }

         if (exists && !overWrite) {
            return null;
         }
         else if(exists) {
            contentHostingService.removeResource(getUnixFilePath(toFile));
         }

         ContentResource resource = contentHostingService.addResource(getUnixFilePath(toFile), fileType,
            fileBytes, resourceProperties, NotificationService.NOTI_NONE);
         
         return resource;
         
      }
     
     class RepositoryEntry {
         private String path;
         private String name;
         private String ownerId;
         private String type;
         private String mimetype;

         Pattern cleanupPath=Pattern.compile("/*(.*?)"); //removes all leading slashes
         
         Pattern lookupElement2=Pattern.compile("/.*?/(.*?)/.*"); //just grabs 2 out of /1/2/3/4/5...
         Pattern lookupElement3=Pattern.compile("/.*?/.*?/(.*?)/.*"); //just grabs 3 out of /1/2/3/4/5...
         
         Pattern ignore2=Pattern.compile("/.*?/.*?/(.*)"); //grabs 3/4/5... out of /1/2/3/4/5...
         Pattern ignore3=Pattern.compile("/.*?/.*?/.*?/(.*)"); //grabs 4/5... out of /1/2/3/4/5...
         

         RepositoryEntry(Object src) {
             Object[] next=(Object[])src;
             type=(String)next[0];
             path=cleanupPath((String)next[1]);
             name=(String)next[2];
             ownerId=(String)next[3];
             mimetype=(String)next[4];
         }
         
         String cleanupPath(String p) {
             return "/" + this.processRegexp(cleanupPath,p);
         }
         
         File getDestinationFile() {
             String userDefinedFolder=this.processRegexp(ignore3,path); //cut out the system stuff, including userid, etc.
             if (userDefinedFolder==null) {
                 userDefinedFolder="/";
             } else {
                 userDefinedFolder="/"+userDefinedFolder+"/";
             }
             String prefix;
             if (isWorksite()) {
                 String wsId=getWorksiteId();
                 String destId=(String)siteMap.get(wsId);
                 if (destId==null) {
                     destId=PORTFOLIO_LOST_FILES+"/"+wsId;
                 }
                 prefix="/group/"+destId+"/";
             } else {
                 prefix="/user/"+ownerId+"/";
             }
             String fileName=prefix+DESTINATION_PREFIX+userDefinedFolder+name;
             LOG.info("new File will be: "+fileName);
             return new File(fileName);
         }
         
         boolean isFile() {
             return type!=null && !"folder".equals(type);
         }
         
         boolean isFolder() {
             return type==null || "folder".equals(type);
         }

        public String getMimetype() {
            return mimetype;
        }

        public String getName() {
            return name;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public boolean isWorksite() {
            return "worksites".equals(processRegexp(lookupElement2,path));
        }
        
        public String getParentFolder() {
            return path.substring("/files".length());
        }

        public String getPath() {
            return path;
        }

        public String getType() {
            return type;
        }

        /*
         *  /files/worksites/WORKSITE-ID/...
         */
        public String getWorksiteId() {
            return processRegexp(lookupElement3,path);
        }

        String processRegexp(Pattern p, String target) {
            String result=null;
            Matcher m=p.matcher(target);
            if (m.matches()) {
                result=m.group(1);
            }
            LOG.info("regexp: "+p.pattern()+" against "+target+" --> "+result);
            return result;
            
        }
        
     }


}
