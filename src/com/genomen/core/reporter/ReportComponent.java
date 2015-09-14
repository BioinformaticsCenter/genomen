package com.genomen.core.reporter;

import java.io.BufferedWriter;
import java.io.PrintStream;

/**
 * Base class for building blocks of reports
 * @author ciszek
 */
public abstract class ReportComponent {
    

    /**
     * Writes a XML presentation of the data contained in this component.
     * @param bufferedWriter writer used to write the data
     */
    public abstract void writeXML( BufferedWriter bufferedWriter );   
    
    
    /**
     * Writes a plain text presentation of the data contained in this component.
     * @param printStream a stream into which the data will be written
     */
    public abstract void print(PrintStream printStream);

}
