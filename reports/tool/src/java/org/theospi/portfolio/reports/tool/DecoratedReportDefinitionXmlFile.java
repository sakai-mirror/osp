package org.theospi.portfolio.reports.tool;

import org.theospi.portfolio.reports.model.*;
import org.w3c.dom.Document;


/**
 * This class allows the ReportResult to interact with the view
 *
 */
public class DecoratedReportDefinitionXmlFile {


    /** The link to the main tool */
    private ReportDefinitionXmlFile	reportDefinitionXmlFile = null;
    private Document xmlFile = null;


    public Document getXmlFile() {
        return xmlFile;
    }


    public DecoratedReportDefinitionXmlFile(Document xmlFile)
    {
       this.xmlFile = xmlFile;
    }


}
