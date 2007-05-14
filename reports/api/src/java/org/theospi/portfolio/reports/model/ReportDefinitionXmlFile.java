package org.theospi.portfolio.reports.model;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.content.api.ContentResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;


public class ReportDefinitionXmlFile {
    private String reportDefId = null;
    private byte[] xmlFile;
    private Set reportXslFiles;
    Document xml;


    public ReportDefinitionXmlFile() {
    }

    public ReportDefinitionXmlFile(ContentResource resource) {
        SAXBuilder builder = new SAXBuilder();


        try {
            InputStream in = resource.streamContent();
            setXmlFile(readStreamToBytes(resource.streamContent()));
            setXml(builder.build(in));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getReportDefId() {
        return reportDefId;
    }

    public void setReportDefId(String reportDefId) {
        this.reportDefId = reportDefId;
    }

    public Document getXml() {
        if (xml == null) {
            ByteArrayInputStream in = new ByteArrayInputStream(getXmlFile());
            SAXBuilder builder = new SAXBuilder();
            try {
                Document doc = builder.build(in);
                setXml(doc);
            }
            catch (Exception e) {

            }


        }
         return xml;
    }
        public void setXml
        (Document
        xml) {
        this.xml = xml;
    }

        public byte[] getXmlFile
        ()
        {
            return xmlFile;
        }

        public void setXmlFile
        (
        byte[] xmlFile) {
        this.xmlFile = xmlFile;
    }
        private byte[] readStreamToBytes
        (InputStream
        inStream) throws IOException
        {
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

    public Set getReportXslFiles() {
        return reportXslFiles;
    }

    public void setReportXslFiles(Set reportXslFiles) {
        this.reportXslFiles = reportXslFiles;
    }
}