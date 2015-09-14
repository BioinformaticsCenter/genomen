package com.genomen.reporter;

import com.genomen.core.Error;
import com.genomen.core.Error.ErrorType;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.genomen.utils.DOMDocumentCreator;


/**
 * Provides a <code>List</code> of available reporters. 
 * @author ciszek
 */
public class ReportersReader {

    /**
     * Returns a list of containing instances available reporters specified in the XML file given as 
     * a parameter.
     * @param reporterListFilePath XMl file path
     * @return a <code>List</code> of instances of classes inheriting <code>Reporter</code>
     */
    public List<Reporter> getReporters( String reporterListFilePath ) {

        List<Reporter> returnedReporters = new ArrayList<Reporter>();

        Document reportersDocument = DOMDocumentCreator.createDocument( reporterListFilePath );

        if ( reportersDocument == null ) {
            Logger.getLogger(ReportersReader.class ).debug(Error.getMessage( ErrorType.FILE_NOT_FOUND, reporterListFilePath));
            return returnedReporters;
        }

        Element rootNode = reportersDocument.getDocumentElement();
        NodeList reporterList = rootNode.getElementsByTagName("reporter");
        //Loop through reporters
        for ( int reporterIndex = 0; reporterIndex < reporterList.getLength(); reporterIndex++) {

            Reporter reporter = null;

            //Extract class data from reporter
            Element reporterElement = (Element)reporterList.item(reporterIndex);
            try {
                Class reporterClass = Class.forName(reporterElement.getAttribute("class"));
                reporter = (Reporter)reporterClass.newInstance();
            }
            catch (ClassNotFoundException ex) {
                Logger.getLogger(ReportersReader.class ).debug(ex);
            }
            catch (InstantiationException ex) {
                Logger.getLogger(ReportersReader.class).error(ex);
            }
           catch (IllegalAccessException ex) {
                Logger.getLogger(ReportersReader.class).error(ex);
            }

            if ( reporter != null ) {

                returnedReporters.add( reporter );
            }
        }

        return returnedReporters;

    }

}
