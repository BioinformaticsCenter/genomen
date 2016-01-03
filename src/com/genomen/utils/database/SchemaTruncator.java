package com.genomen.utils.database;

import com.genomen.dao.ContentDAO;
import com.genomen.dao.DAOFactory;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Used to truncate the database.
 * @author ciszek
 */
public class SchemaTruncator {
    
    /**
     * Truncates a specific schema.
     * @param schemaName schema name
     * @param clearDiscSpace is disk space to be cleared.
     * @return <code>true</code> if schema was truncated, <code>false</code> otherwise.
     */
    public static boolean truncate( String schemaName, boolean clearDiscSpace ) {

        //Create a graph of the database structure
        DatabaseGraph databaseGraph = DatabaseGraphBuilder.buildDatabaseGraph(schemaName);

        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
       
        String[] tables = contentDAO.getTables(schemaName);

        if ( tables == null) {
            return false;
        }
        
        LinkedList<String> tablesLeft = new LinkedList<String>();
        tablesLeft.addAll(Arrays.asList(tables));

        while ( !tablesLeft.isEmpty() ) {

            String tableCurrentlyTruncated = tablesLeft.getFirst();
            TableNode tableNode = databaseGraph.getTableNode(tableCurrentlyTruncated);
            truncateTables(tableNode, databaseGraph, schemaName, tablesLeft );
        }

        if ( clearDiscSpace ) {
            clearUnusedDiscSpace(schemaName);
        }
        return true;

    }
    //Recursively attempts to truncate all tables in the table dependency chain.
    private static void truncateTables( TableNode tableNode, DatabaseGraph databaseGraph, String schemaName, LinkedList<String> tablesLeft ) {

        if ( !tableNode.isProcessed() ) {
            //Loop through the list of tables referencing this table
            for ( int i = 0; i < tableNode.getDependingTables().size(); i++ ) {
                //If the table referencing this table is not already truncated
                if ( !tableNode.getDependingTables().get(i).isProcessed() ) {
                    truncateTables( tableNode.getDependingTables().get(i), databaseGraph, schemaName, tablesLeft );
                }
            }


            String tableName = tableNode.getName();
            //Truncate table
            ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
            boolean tableTruncated = contentDAO.truncate(schemaName, tableName);
           //Mark table truncated
            if ( tableTruncated ) {
                databaseGraph.getTableNode(tableName).setProcessed(true);
            }
            //Remove table from the list of tables requiring truncation.
            tablesLeft.remove(tableName);

        }
    }

    public static void clearUnusedDiscSpace( String schemaName ) {

        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();

        String[] tables = contentDAO.getTables(schemaName);

        for ( int i = 0; i < tables.length; i++ ) {
            contentDAO.compressTable(schemaName, tables[i]);
        }

    }
    

}
