package com.genomen.reporter;

import com.genomen.utils.StringUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Presents results as a table
 * @author ciszek
 */
public class ReportTable extends ReportComponent {

    private String[] headers;
    private String[] headerDesriptions;
    private List<TableRow> values = new ArrayList<TableRow>();
    private String title;

    /**
     * Gets the headers of this table
     * @return table headers
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Gets the descriptions for this tables headers
     * @return header descriptions
     */
    public String[] getHeaderDescriptions() {
        return headerDesriptions;
    }

    /**
     * Gets the rows of this table
     * @return rows of this table
     */
    public List<TableRow> getRows() {
        return values;
    }
    
     /**
     * Gets the title of this table
     * @return the name of this table
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the name of this table
     * @param name the name to set
     */
    public void setTitle(String name) {
        this.title = name;
    }   

    /**
     * Constructs a table with the given headers and header descriptions
     * @param p_headers headers of the table
     * @param p_headerDescriptions descriptions for the headers
     * @param p_title the title of this table
     */
    public ReportTable(String[] p_headers, String[] p_headerDescriptions, String p_title) {
        headers = p_headers;
        headerDesriptions = p_headerDescriptions;
        title = p_title;
    }

    /**
     * Adds a new row to the table
     * @param row  column values of the row to be added
     */
    public void addRow(String[] row) {
        values.add(new TableRow(row));
    }
    
    /**
     * Adds a new row to the table
     * @param row row to be added
     */    
    public void addRow(TableRow tableRow ) {
        values.add(tableRow);
    }

    @Override
    public void writeXML(BufferedWriter bufferedWriter) {

        try {
            bufferedWriter.write("\t<table>");
            bufferedWriter.write("\n\t\t<headers>");
            for (int i = 0; i < headerDesriptions.length; i++) {
                bufferedWriter.write("\n\t\t\t<header>" + headerDesriptions[i] + "</header>");
            }
            bufferedWriter.write("\n\t\t</headers>");
            bufferedWriter.write("\n\t\t<rows>");
            for (int listIndex = 0; listIndex < values.size(); listIndex++) {
                String[] row = values.get(listIndex).getValues();
                bufferedWriter.write("\n\t\t\t<row>");
                for (int column = 0; column < row.length; column++) {

                    bufferedWriter.write("\n\t\t\t\t<" + headers[column] + ">");

                    String content = row[column];
                    // Force UTF-8 encoding
                    content = StringUtils.forceEncoding(content, "UTF-8");
                    // Escape for XML output
                    content = StringEscapeUtils.escapeXml(content);
                    bufferedWriter.write(content);
                    
                    bufferedWriter.write("</" + headers[column] + ">");

                }
                bufferedWriter.write("\n\t\t\t</row>");
            }
            bufferedWriter.write("\n\t\t</rows>");
            bufferedWriter.write("\n\t</table>");
        } catch (IOException ex) {
            Logger.getLogger(ReportTable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void print(PrintStream printStream) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void writeDelimited(BufferedWriter bufferedWriter, String delimiter) {
        
        try {
            
            for ( int i = 0; i < headers.length; i++ ) {

                bufferedWriter.write( headers[i]);
                if ( i != headers.length-1) {
                    bufferedWriter.write(delimiter);
                }
            }
            bufferedWriter.write( "\n");
            
            for ( int line = 0; line < values.size(); line++) {
                String[] newLine = values.get(line).getValues();
                
                for ( int i = 0; i < newLine.length; i++ ) {
                    bufferedWriter.write( StringEscapeUtils.escapeCsv(newLine[i].replaceAll("\n", "\\n")));
                    if ( i != headers.length-1) {
                        bufferedWriter.write(delimiter);
                    }
                }
                bufferedWriter.write( "\n");
                
            }
            
        } catch (IOException ex) {
             Logger.getLogger(ReportTable.class.getName()).log(Level.SEVERE, null, ex);
         } 
    }

}
