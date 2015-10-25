package com.genomen.dao;

import com.genomen.core.Individual;
import com.genomen.entities.DataEntity;
import com.genomen.entities.DataType;
import java.util.List;


/**
 * Interface for accessing datasets
 * @author ciszek
 */
public interface DataSetDAO {

    /**
     * Returns a <code>DataEntity</code> presenting the requested data.
     * @param schemaName Schema from which the data is to be retrieved
     * @param individualID Id of the individual to whom the data is associated
     * @param attribute Name of the attribute used to distinguish the data
     * @param value Value of the attribute that distinguishes the data
     * @param dataType Type of the data to be retrieved.
     * @return <code>DataEntity</code> presenting the requested data, or <code>null</code>
     */
    public abstract DataEntity getDataEntity( String schemaName, String individualID, String attribute, String value, DataType dataType );
    
    /**
     * Creates a table to hold the type of data specified.
     * @param schemaName Schema to which the table is to be placed.
     * @param individualID ID of the individual to whom the data is associated.
     * @param dataType <code>DataType</code> specifying the data which the table created is to hold
     */
    public abstract void createDataTable( String schemaName, String individualID, DataType dataType );
    
    /**
     * Creates a valid table name based on the ID of an individual and data type definition.
     * @param individualID ID of an individual
     * @param dataType Data type definition
     * @return A valid table name
     */
    public abstract String createTableName( String individualID, DataType dataType );
    
    /**
     * Removes the listed individuals from the database.
     * @param individuals A list of individuals to be removed
     */
    public abstract void removeIndividuals( List<Individual> individuals );
    
    /**
     * Gets the current valid ID number that can be used to insert a new value to the table.
     * @param schemaName Schema used to store the table.
     * @param individualID ID of the individual to whom the data table is associated.
     * @param dataType Data type definition of the table in question
     * @return current valid id number
     */
    public abstract int getCurrentId( String schemaName, String individualID, DataType dataType );
}
