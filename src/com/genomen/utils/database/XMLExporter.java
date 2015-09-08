package com.genomen.utils.database;

import com.genomen.dao.ContentDAO;
import com.genomen.dao.DAOFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import org.apache.log4j.Logger;
import com.genomen.utils.ResourceReleaser;

/**
 * Provides a method for exporting the contents of a database.
 * @author ciszek
 */
public class XMLExporter {

    /**
     * Exports the contents of a given schema
     * @param schemaName schema name
     * @param filePath output file path
     */
    public static void export( String schemaName, String filePath) {

        File file = new File(filePath);

        if ( file.exists() ) {
            file.delete();
        }


        BufferedWriter bufferedWriter = null;
        try {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));

            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <TABLES>\n");
            writeTables( schemaName, bufferedWriter);
            bufferedWriter.write("\n</TABLES>");
            bufferedWriter.close();

        } catch (IOException ex) {
            Logger.getLogger( XMLExporter.class ).debug( ex );
        }
        finally {
            ResourceReleaser.close(bufferedWriter);
        }
        
    }

    private static void writeTables( String schemaName, BufferedWriter bufferedWriter ) {

        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();

        String[] tables = contentDAO.getTables(schemaName);

        for ( int i = 0; i < tables.length; i++) {
            writeTable( schemaName, tables[i], bufferedWriter );
        }


    }

    private static void writeTable( String schemaName, String tableName, BufferedWriter bufferedWriter ) {

        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
        List<String[]> tableContents = contentDAO.getTableContents(schemaName, tableName);
        String[] attributeNames = contentDAO.getAttributeNames(schemaName, tableName);

        for ( int i = 0; i < tableContents.size(); i++) {

            try {
                bufferedWriter.write("\n\t<" + tableName + ">");
                bufferedWriter.write(createRow(tableContents.get(i), attributeNames));
                bufferedWriter.write("\n\t</" + tableName + ">\n");
            }
            catch (IOException ex) {
                Logger.getLogger( XMLExporter.class ).debug( ex );
            }
            
        }


    }

    private static String createRow( String[] values, String[] attributeNames ) {

        String row = "";

        for ( int i = 0; i < values.length; i++) {

            row = row + "\n\t\t<" + attributeNames[i] + ">";
            row = row + values[i];
            row = row + "</" + attributeNames[i] + ">";
        }

        return row;

    }
    
}
