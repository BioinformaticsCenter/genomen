package com.genomen.reporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.log4j.Logger;

/**
 * Creates a separate CSV file for each table in a given report.
 * @author ciszek
 */
public class CSVReportCreator {
    
    private static final String DELIMITER = ",";
    
    public static void createCSV( Report report ) {
        
             
        for ( IndividualEntry individual : report.getIndividualEntries()) {
            
            for ( ReportComponent component : individual.getComponents() ) {
                
                if ( component instanceof TraitTable ) {
                    
                    TraitTable table = (TraitTable)component;
                    String fileName = report.getName() + "_" + individual.getIndividual().getId() + "_" + table.getTitle() + ".csv";
                    writeFile(table, fileName);
                }
            }         
        }     
    }
    
    private static void writeFile(ReportTable reportTable, String fileName) {
        
            File file = new File(fileName);

            if ( file.exists() ) {
                file.delete();
            }

            BufferedWriter bufferedWriter = null;
            try {

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
                reportTable.writeDelimited(bufferedWriter, DELIMITER);

            } catch (IOException ex) {
                Logger.getLogger( CSVReportCreator.class ).debug( ex );
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
