package com.genomen.utils;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Convenience class for the creation of DOM documents.
 * @author ciszek
 */
public class DOMDocumentCreator {
  
    /**
     * Creates a DOM presentation of a given XML file
     * @param filePath XML file path
     * @return DOM document
     */
    public static Document createDocument( String filePath ) {

        File file = new File(filePath);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(filePath);
        }
        catch (ParserConfigurationException ex) {
            Logger.getLogger( DOMDocumentCreator.class ).debug(ex);
        }
        catch (IOException ex ) {
            Logger.getLogger( DOMDocumentCreator.class ).debug(ex);
        }
        catch( SAXException ex ) {
            Logger.getLogger( DOMDocumentCreator.class ).debug(ex);
        }

        if ( file.exists() && file.canRead()) {

            document.normalize();

        }
        else {
            Logger.getLogger( DOMDocumentCreator.class ).error("File not found") ;
            return document;
        }

        return document;

    }    
    
}
