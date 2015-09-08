package com.genomen.utils.database;

import com.genomen.dao.ContentDAO;
import com.genomen.dao.DAOFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;


/**
 * Used to create a template of a specific database
 * @author ciszek
 */
public class XMLTemplateCreator {
    
    /**
     * Creates a XML template of a specific database
     * @param schemaName schema name
     * @param filePath output file path
     */
    public static void createXMLTemplate( String schemaName, String filePath ) {
       
        File file = new File(filePath);

        if ( file.exists() ) {
            file.delete();
        }

        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write( createDatabaseTemplate( schemaName ) );
            bufferedWriter.close();

        } catch (IOException ex) {
            Logger.getLogger( XMLTemplateCreator.class ).debug( ex );
        }

    }

    private static String createDatabaseTemplate( String schemaName  ) {
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();

        String[] tableNames = contentDAO.getTables(schemaName);

        String databaseTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <TABLES>\n";


        for ( int i = 0; i < tableNames.length; i++) {
            databaseTemplate = databaseTemplate + createTableTemplate( tableNames[i], schemaName ) + "\n";
        }

        databaseTemplate = databaseTemplate + "</TABLES>";

        return databaseTemplate;
    }

    private static String createTableTemplate( String tableName, String schemaName ) {

        String tableTemplate = "";
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
        String[] attributes = contentDAO.getAttributeNames(schemaName, tableName);

        tableTemplate = tableTemplate + "\t" + "<" + tableName + ">" + "\n";
        for ( int i = 0; i < attributes.length; i++) {
            tableTemplate = tableTemplate + "\t\t" + "<" + attributes[i] + ">" + "</" + attributes[i] + ">" + "\n";
        }
        tableTemplate = tableTemplate + "\t" + "</" + tableName + ">\n";

        return tableTemplate;
    }
}
