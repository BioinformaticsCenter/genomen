package com.genomen.utils.database;

import com.genomen.dao.ContentDAO;
import com.genomen.dao.DAOFactory;

/**
 * Used to build a graph presenting the table relations in the database.
 * @author ciszek
 */
public class DatabaseGraphBuilder {

    /**
     * Builds a graph presenting the table relations in the database.
     * @param schemaName schema
     * @return database graph
     */
    public static DatabaseGraph buildDatabaseGraph( String schemaName ) {

        DatabaseGraph databaseGraph = new DatabaseGraph();
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();

        String[] tableNames = contentDAO.getTables(schemaName);
        
        if ( tableNames == null ) {
            return databaseGraph;
        }
        
        //Add a TableNode representing each table in the database to the graph
        for ( int tableIndex = 0; tableIndex < tableNames.length; tableIndex++ ) {

            databaseGraph.addTableNode( new TableNode(tableNames[tableIndex], schemaName));
        }
        //For each table in the database
        for ( int tableIndex = 0; tableIndex < tableNames.length; tableIndex++ ) {

            String[][] tablesRefered = contentDAO.getTablesRefered(schemaName, tableNames[tableIndex]);
            TableNode tableNode = databaseGraph.getTableNode(tableNames[tableIndex]);
            //For each table in the database that the table refers
            for ( int foreignKeyIndex = 0; foreignKeyIndex < tablesRefered.length; foreignKeyIndex++ ) {

                tableNode.addReferedTable( databaseGraph.getTableNode(tablesRefered[foreignKeyIndex][1]));
                databaseGraph.getTableNode(tablesRefered[foreignKeyIndex][1]).addDependingTable(tableNode);
            }

            String[] attributeNames = contentDAO.getAttributeNames(schemaName, tableNames[tableIndex]);
            int[] attributeTypes = contentDAO.getAttributeTypes(schemaName, tableNames[tableIndex]);
            //For each attribute of the table
            for ( int i = 0; i < attributeNames.length; i++) {
                tableNode.addAttribute( attributeNames[i] , new Integer( attributeTypes[i]));
            }

        }
        return databaseGraph;
    }



}
