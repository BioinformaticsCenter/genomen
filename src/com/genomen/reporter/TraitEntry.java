package com.genomen.reporter;

import com.genomen.utils.StringUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
 * Presents the descriptions of a specific trait
 * @author ciszek
 */
public class TraitEntry extends TableRow {

    private final String shortDescription;
    private final String longDescription;
    private final ReportTable table;


    /**
     * Constructs a trait table with the given descriptions.
     * @param headers header row of this table
     * @param headerDescriptions descriptions for columns of this table
     * @param title the title of this table
     * @param shortDescription short description of the trait presented in this table
     * @param longDescription long description of the trait presented in this table
     */
    public TraitEntry( String[] headers, String[] headerDescriptions, String title, String shortDescription, String longDescription ) {
        super(null);
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        table = new ReportTable(headers, headerDescriptions, title);

    }

    /**
     * Returns the table contained in this entry.
     * @return a table of trait values
     */
    public ReportTable getTable() {
        return table;
    }
    
    @Override
    public void writeXML(BufferedWriter bufferedWriter) {

        try {
            bufferedWriter.write("\n<traitEntry>");
            bufferedWriter.write("\n\t<title>" + StringEscapeUtils.escapeXml(StringUtils.forceEncoding(table.getTitle(), "UTF-8") ) + "</title>");
            bufferedWriter.write("\n\t<shortDescription>" +  StringEscapeUtils.escapeXml(StringUtils.forceEncoding(getShortDescription(), "UTF-8") ) + "</shortDescription>");
            bufferedWriter.write("\n\t<longDescription>" +  StringEscapeUtils.escapeXml(StringUtils.forceEncoding(getLongDescription(), "UTF-8") ) + "</longDescription>");

            bufferedWriter.write("\n\t\t<table>");
            bufferedWriter.write("\n\t\t\t<headers>");
            for (int i = 0; i < table.getHeaderDescriptions().length; i++) {
                bufferedWriter.write("\n\t\t\t\t<header>" + table.getHeaderDescriptions()[i] + "</header>");
            }
            bufferedWriter.write("\n\t\t\t</headers>");
            bufferedWriter.write("\n\t\t\t<rows>");
            for (int listIndex = 0; listIndex < table.getRows().size(); listIndex++) {
                String[] row = table.getRows().get(listIndex).getValues();
                bufferedWriter.write("\n\t\t\t\t<row>");
                for (int column = 0; column < row.length; column++) {

                    bufferedWriter.write("\n\t\t\t\t\t<" + table.getHeaders()[column] + ">");

                    String content = row[column];
                    // Force UTF-8 encoding
                    content = StringUtils.forceEncoding(content, "UTF-8");
                    // Escape for XML output
                    content = StringEscapeUtils.escapeXml(content);
                    bufferedWriter.write(content);

                    bufferedWriter.write("</" + table.getHeaders()[column] + ">");

                }
                bufferedWriter.write("\n\t\t\t\t</row>");
            }
            bufferedWriter.write("\n\t\t\t</rows>");
            bufferedWriter.write("\n\t\t</table>");



            bufferedWriter.write("\n</traitEntry>");
        } catch (IOException ex) {
            Logger.getLogger(TraitEntry.class).error(ex);
        }

    }

    /**Gets the short description of this trait.
     * @return the shortDescription
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**Gets the long description of this trait.
     * @return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }

    public void writeDelimited( BufferedWriter bufferedWriter, String delimiter ) {
     
        try {                
            for ( int line = 0; line < getTable().getRows().size(); line++) {
                String[] newLine = getTable().getRows().get(line).getValues();

                String[] concatenated = new String[newLine.length+2];
                concatenated[0] = getShortDescription();
                concatenated[1] = getLongDescription();
                System.arraycopy(newLine, 0, concatenated, 2, newLine.length);

                for ( int i = 0; i < concatenated.length; i++ ) {

                    bufferedWriter.write( StringEscapeUtils.escapeCsv(concatenated[i].replaceAll("\n", "\\\\n")));
                    if ( i != table.getHeaders().length+2-1) {
                        bufferedWriter.write(delimiter);
                    }
                }
                if ( line != getTable().getRows().size() -1) {
                    bufferedWriter.write( "\n");                       
                }
      
            }  
        } catch (IOException ex) {
             java.util.logging.Logger.getLogger(TraitEntry.class.getName()).log(Level.SEVERE, null, ex);
         }        
    }    
    
}
