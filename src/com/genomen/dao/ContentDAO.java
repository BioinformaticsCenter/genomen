package com.genomen.dao;

import java.util.List;
import java.util.Map;

/**
 * Provides an interface for operations related to the structure of a database
 * @author ciszek
 */
public interface ContentDAO {

    /**
     * Gets the contents of a specific table.
     * @param schema schema name
     * @param tableName table name
     * @return
     */
    public abstract List<String[]> getTableContents( String schema, String tableName);

    /**
     * Deletes a tuple with given values from a specific table.
     * @param schema schema name
     * @param tableName table name
     * @param rowValues values of the row to be deleted
     */
    public abstract void deleteTuple( String schema, String tableName, String[] rowValues );
    
    /**
     * Gets the attribute names of a specific table
     * @param schema schema name
     * @param tableName table name
     * @return attribute names
     */
    public abstract String[] getAttributeNames( String schema, String tableName );
    
    /**
     * Gets the names of tables in a specific.
     * @param schemaName name of the schema
     * @return table names
     */
    public abstract String[] getTables( String schemaName );
    
    /**
     * Gets the column display sizes of a specific table.
     * @param schema schema name
     * @param tableName table name
     * @return column display sizes
     */
    public abstract int[] getColumnDisplaySizes( String schema, String tableName );
    
    /**
     * Adds a tuple into a specific table.
     * @param schema schema name
     * @param tableName table name
     * @param attributes attribute values of the tuple to be added
     */
    public abstract void addTuple( String schema, String tableName, String[] attributes );

    /**
     * Adds a tuple into a specific table
     * @param schema schema name
     * @param tableName table name
     * @param attributeNameList attributes for which values are to be added
     * @param valueList values for the attributes of the tuple to be added
     */
    public abstract void addTuple( String schema, String tableName, String[] attributeNameList, String[] valueList );
       
    /**
     * Gets the attribute type names of a specific table
     * @param schemaName schema name
     * @param tableName table name
     * @return attribute type names
     */
    public abstract String[] getAttributeTypeNames( String schemaName, String tableName );
    
    /**
     * Gets the attribute types of a specific table
     * @param schemaName schema name 
     * @param tableName table name
     * @return attribute types
     */
    public abstract int[] getAttributeTypes( String schemaName, String tableName );

    /**
     * Gets the primary keys of a specific table
     * @param schemaName schema name
     * @param tableName table name
     * @return primary keys
     */
    public abstract String[] getPrimaryKeys( String schemaName, String tableName );

    /**
     * Gets the values of the foreign keys of a specific table
     * @param schemaName schema name
     * @param tableName table name
     * @param ignorePrimaryKeys are primary keys to be ignored
     * @return foreign keys
     */
    public abstract Map<String, String[]> getForeignKeyValues( String schemaName, String tableName, boolean ignorePrimaryKeys );

    /**
     * Gets the names of tables to which a specific table refers
     * @param schemaName schema name
     * @param tableName table name
     * @return referred tables
     */
    public abstract String[][] getTablesRefered( String schemaName, String tableName );

    /**
     * Removes all values from a specific table
     * @param schemaName schema name
     * @param tableName table name
     * @return <code>true</code> if operation succeeds, <code>false</code> otherwise
     */
    public abstract boolean truncate( String schemaName, String tableName );

    /**
     * Compresses a specific table
     * @param schemaName schema name
     * @param tableName table dame
     */
    public abstract void compressTable( String schemaName, String tableName );

    /**
     * Removes a specific table from the database
     * @param schemaName schema name
     * @param tableName table name
     */
    public abstract void dropTable( String schemaName, String tableName );

    /**
     * Creates a table with the given name and attributes
     * @param schemaName schema name
     * @param tableName name of the table 
     * @param attributes table attributes
     * @return <code>true</code> if table creation succeeds, <code>false</code> otherwise
     */
    public abstract boolean createTable( String schemaName, String tableName, String attributes );
    
    /**
     * Checks if a specific table exists in the database
     * @param schemaName schema name
     * @param tableName table name
     * @return <code>true</code> if table exists, otherwise <code>false</code
     */
    public abstract boolean tableExists( String schemaName, String tableName );
}
