package com.genomen.reporter;

import java.io.BufferedWriter;

/**
 * Interface defining the methods common to all components that can be presented as tables.
 * @author ciszek
 */
public interface TableComponent {
    
    /**
     * Writes a CSV presentation of this component
     * @param bufferedWriter writer used to write the CSV presentation of this component
     */    
    public void writeCSV(BufferedWriter bufferedWriter );
}
