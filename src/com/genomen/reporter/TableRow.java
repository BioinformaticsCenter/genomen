package com.genomen.reporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Interface for objects that can be presented as rows in a table
 * @author ciszek
 */
public class TableRow extends ReportComponent {
    
    private String[] values;    

    @Override
    public void writeXML(BufferedWriter bufferedWriter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(PrintStream printStream) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**Gets the values of the columns of this row
     * @return the values
     */
    public String[] getValues() {
        return values;
    }

    /**Sets the values of the columns of this row.
     * @param values the values to set
     */
    public void setValues(String[] values) {
        this.values = values;
    }
    
    
    /**
     * Constructs a table row with the given column values
     * @param values the column values of this table.
     */
    public TableRow( String[] values ) {
        this.values = values;
    }
    
    public void writeDelimited( BufferedWriter bufferedWriter, String delimiter ) throws IOException {
        
        for ( int i = 0; i < values.length; i++ ) {
            bufferedWriter.write( StringEscapeUtils.escapeCsv(values[i].replaceAll("\n", "\\\\n") ) );
            
            if ( i != values.length-1) {
                bufferedWriter.write(delimiter);
            }
        }
       
        
    }
    
}
