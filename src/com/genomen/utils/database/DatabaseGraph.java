package com.genomen.utils.database;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A graph of table relations.
 * @author ciszek
 */
public class DatabaseGraph {

    private HashMap< String, TableNode > databaseGraph = new HashMap<String, TableNode>();

    /**
     * Gets the node of a specific table
     * @param tableName table name
     * @return tabline node
     */
    public TableNode getTableNode( String tableName ) {
        return databaseGraph.get(tableName);
    }

    /**
     * Adds a table node to the graph.
     * @param tableNode
     */
    public void addTableNode( TableNode tableNode ) {
        databaseGraph.put(tableNode.getName(), tableNode );
    }

    /**
     * Checks if a table node is already processed.
     * @param tableName table name
     * @return <code>true</code> if table is processed, <code>false</code> otherwise.
     */
    public boolean isNodeProcessed( String tableName ) {
        return databaseGraph.get(tableName).isProcessed();
    }

    /**
     * Checks if all nodes are processed.
     * @return <code>true</code> if all nodes are processed, <code>false</code> otherwise.
     */
    public boolean allNodesProcessed() {

        LinkedList<TableNode> tableNodes = new LinkedList( databaseGraph.values() );

        for ( int i = 0; i < tableNodes.size(); i++) {

            if ( !tableNodes.get(i).isProcessed() ) {
                return false;
            }

        }
        
        return true;

    }

    /**
     * Sets the processed status of a given table.
     * @param tableName table name
     * @param p_processed is the table processed
     */
    public void setNodeProcessed( String tableName, boolean p_processed ) {

        TableNode tableNode = databaseGraph.get(tableName);

        if ( tableNode != null ) {
            tableNode.setProcessed(p_processed);
        }

    }

}
