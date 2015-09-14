package com.genomen.utils.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Presents a table in a database graph.
 * @author ciszek
 */
public class TableNode {

    private HashMap<String, Integer> attributeTypes = new HashMap<String, Integer>();

    private String name;
    private String schemaName;
    private List<TableNode> referedTables = new ArrayList<TableNode>();
    private List<TableNode> dependingTables = new ArrayList<TableNode>();
    private boolean processed;

    /**
     * Gets the name of this table.
     * @return table name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the name of the schema.
     * @return schema name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Gets the tables to which this table refers.
     * @return referred tables
     */
    public List<TableNode> getReferedTables() {
        return referedTables;
    }

    /**
     * Gets the tables that depend on this table.
     * @return depending tables
     */
    public List<TableNode> getDependingTables() {
        return dependingTables;
    }

    /**
     * Checks if this table is already processed.
     * @return <code>true</code> if this table is already processed, <code>false</code> otherwise.
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets the processed status of this table.
     * @param p_processed is this table processed
     */
    public void setProcessed( boolean p_processed ) {
        processed = p_processed;
    }

    /**
     * Adds a table to which this table refers
     * @param tableNode referred table
     */
    public void addReferedTable( TableNode tableNode ) {
        referedTables.add(tableNode);
    }

    /**
     * Adds a table which depends on this table.
     * @param tableNode depending table
     */
    public void addDependingTable( TableNode tableNode ) {
        dependingTables.add(tableNode);
    }

    /**
     * Adds an attribute to this table.
     * @param attributeName name of the attribute
     * @param attributeType type of the attribute
     */
    public void addAttribute( String attributeName,  int attributeType ) {
        attributeTypes.put(attributeName, attributeType);
    }

    /**
     * Is the given attribute numeric
     * @param attributeName name of the attribute
     * @return <code>true</code> if the attribute is numeric, <code>false</code> otherwise
     */
    public boolean isNumeric( String attributeName ) {

        if( attributeTypes.get(attributeName) == null ){
            ErrorManager.reportError("ERROR: Table " + name + " does not have attribute " +  attributeName );
            return false;
        }

        int attributeType = attributeTypes.get(attributeName).intValue();

        if (
                
             attributeType == java.sql.Types.BINARY ||
             attributeType == java.sql.Types.BIT ||
             attributeType == java.sql.Types.BIGINT ||
             attributeType == java.sql.Types.DECIMAL ||
             attributeType == java.sql.Types.DECIMAL ||
             attributeType == java.sql.Types.DOUBLE ||
             attributeType == java.sql.Types.FLOAT ||
             attributeType == java.sql.Types.INTEGER ||
             attributeType == java.sql.Types.NUMERIC ||
             attributeType == java.sql.Types.SMALLINT

                ) {
            return true;
        }
        return false;
    }

    /**
     * Constructs a table node with the given attributes
     * @param p_name name of the table 
     * @param p_schemaName schema to which this table belongs
     */
    public TableNode( String p_name, String p_schemaName ) {

        name = p_name;
        schemaName = p_schemaName;

    }



}
