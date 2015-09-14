package com.genomen.reporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Used to transform XML reports into HTML
 * @author ciszek
 */
public class XSLTTransformer {

    /**
     * Transforms XML report into HTML
     * @param sourceXMLpath path of the report
     * @param XSLTpath path of the XSL file
     * @param outputPath path of the result
     */
    public static void transform( String sourceXMLpath, String XSLTpath, String outputPath ) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(XSLTpath));
            transformer.transform(new javax.xml.transform.stream.StreamSource(sourceXMLpath), new StreamResult(new FileOutputStream(outputPath)));
        }
        catch (TransformerConfigurationException ex) {
            Logger.getLogger(XSLTTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (TransformerException ex) {
            Logger.getLogger(XSLTTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(XSLTTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
