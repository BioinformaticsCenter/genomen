package com.genomen.core.reporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Table presenting traits
 * @author ciszek
 */
public class TraitTable extends ReportTable {
    
    private static final String HEADER_SHORT_DESCRIPTION = "Short_description";
    private static final String HEADER_LONG_DESCRIPTION = "Long_description";
    
    public TraitTable(String[] headers, String[] headerDescriptions, String title) {
        super(headers, headerDescriptions, title);
    }
    
    @Override
    public void writeXML( BufferedWriter bufferedWriter) {
        try {
            bufferedWriter.write("<traitTable>");
            for ( TableRow row : this.getRows() ) {
                row.writeXML(bufferedWriter);
            }
            bufferedWriter.write("</traitTable>");            
        } catch (IOException ex) {
            Logger.getLogger(TraitTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void writeDelimited(BufferedWriter bufferedWriter, String delimiter) {
        
        try {
            
            bufferedWriter.write(HEADER_SHORT_DESCRIPTION);
            bufferedWriter.write(delimiter);
            bufferedWriter.write(HEADER_LONG_DESCRIPTION);            
            bufferedWriter.write(delimiter);            
            for ( int i = 0; i < this.getHeaders().length; i++ ) {

                bufferedWriter.write( this.getHeaders()[i]);
                if ( i != this.getHeaders().length-1) {
                    bufferedWriter.write(delimiter);
                }
            }
            bufferedWriter.write( "\n");           
            
            for ( TableRow entry : this.getRows() ) {
                entry.writeDelimited(bufferedWriter, delimiter);
                bufferedWriter.write( "\n");                    
            }
            
        } catch (IOException ex) {
             Logger.getLogger(ReportTable.class.getName()).log(Level.SEVERE, null, ex);
         } 
    }    
    
}
