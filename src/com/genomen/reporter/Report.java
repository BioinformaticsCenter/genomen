package com.genomen.reporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Presents the results of an analysis
 * @author ciszek
 */
public class Report extends CompositeReportComponent {


    private String name;

    /**
     * Gets the name of the analysis of which results the report presents
     * @return name of the analysis
     */
    public String getName() {
        return name;
    }

    /**
     * Constructs a report with the given name
     * @param p_name name of the report
     */
    public Report( String p_name ) {
        name = p_name;
    }   
    
    public List<IndividualEntry> getIndividualEntries() {
       
        List<IndividualEntry> individuals = new ArrayList<IndividualEntry>();
        
        for ( ReportComponent reportComponent : this.getComponents()) {
            
            if ( reportComponent.getClass() == IndividualEntry.class ) {
                individuals.add( (IndividualEntry)reportComponent ); 
            }
        }        
        return individuals;
    }
    
    @Override
    public void writeXML(BufferedWriter bufferedWriter) {
        try {
            bufferedWriter.write("<report date=\"" + getDate() + "\">\n");
                for ( int i = 0; i < this.getComponents().size(); i++) {
                    this.getComponents().get(i).writeXML(bufferedWriter);
                }
            bufferedWriter.write("\n</report>");
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    @Override
    public void print(PrintStream printStream) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
