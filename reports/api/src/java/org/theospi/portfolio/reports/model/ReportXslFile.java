package org.theospi.portfolio.reports.model;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.IdUnusedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.theospi.portfolio.shared.model.OspException;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


public class ReportXslFile
{

      protected final transient Log logger = LogFactory.getLog(getClass());
    /** the link to the report definition */
    private String reportDefId = null;

    private String reportXslFileRef = null;

   private byte[] xslFile;
    /**
     * the getter for the reportId property
     */
    public ReportXslFile(){
        
    }



    public ReportXslFile(ReportXsl reportXsl, ContentHostingService contentHosting, String reportDefId)
    {
       try { String id = reportXsl.getXslLink();
        ContentResource resource = contentHosting.getResource(id);
        setXslFile(readStreamToBytes(resource.streamContent()));
        setReportXslFileRef(reportXsl.getXslLink());
           setReportDefId(reportDefId);

       }
        catch(PermissionException pe) {
         logger.warn("Failed loading content: no permission to view file", pe);
         throw new OspException("Permission Error loading the following xsl file:" + reportXsl.getXslLink());
      } catch(TypeException te) {
         logger.warn("Wrong type", te);
           throw new OspException("Error loading the following xsl file:" + reportXsl.getXslLink());
      } catch(IdUnusedException iue) {
         logger.warn("UnusedId: ", iue);
           throw new OspException("Error loading the following xsl file:" + reportXsl.getXslLink());
      }
        catch (Exception e) {
            e.printStackTrace();
            throw new OspException("Error loading the following xsl file:" + reportXsl.getXslLink());
        }
    }



    public String getReportDefId() {
        return reportDefId;
    }

    public void setReportDefId(String reportDefId) {
        this.reportDefId = reportDefId;
    }

    public String getReportXslFileRef() {
        return reportXslFileRef;
    }

    public void setReportXslFileRef(String reportXslFileRef) {
        this.reportXslFileRef = reportXslFileRef;
    }

    public byte[] getXslFile() {
        return xslFile;
    }

    public void setXslFile(byte[] xslFile) {
        this.xslFile = xslFile;
    }

     private byte[] readStreamToBytes(InputStream inStream) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte data[] = new byte[10 * 1024];

      int count;
      while ((count = inStream.read(data, 0, 10 * 1024)) != -1) {
         bytes.write(data, 0, count);
      }
      byte[] tmp = bytes.toByteArray();
      bytes.close();
      return tmp;
   }
}
