package com.genomen.reporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

/**
 * Used to create XML presentations of reports
 * @author ciszek
 */
public class XMLReportCreator {

    /**
     * Creates an XML presentation of a report
     * @param report a report to be presented as XML document
     */
    public static void createXML( Report report ) {

        String fileName = report.getName();
        String filePath = fileName.concat(".xml");
        File file = new File(filePath);

        if ( file.exists() ) {
            file.delete();
        }


        BufferedWriter bufferedWriter = null;
        try {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n");
            report.writeXML(bufferedWriter);

 
        } catch (IOException ex) {
            Logger.getLogger( XMLReportCreator.class ).debug( ex );
        } finally {
            if (bufferedWriter != null) {
                try {
                      bufferedWriter.close();
                      }
                catch (Exception e) {

                }
           }

        }
    }

}
