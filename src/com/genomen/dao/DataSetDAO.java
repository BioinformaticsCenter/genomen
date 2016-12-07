package com.genomen.dao;

import com.genomen.core.Sample;
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
     * @param sampleID Id of the sample to whom the data is associated
     * @param attribute Name of the attribute used to distinguish the data
     * @param value Value of the attribute that distinguishes the data
     * @param dataType Type of the data to be retrieved.
     * @return <code>DataEntity</code> presenting the requested data, or <code>null</code>
     */
    public abstract DataEntity getDataEntity( String schemaName, String sampleID, String attribute, String value, DataType dataType );
    
    /**
     * Creates a table to hold the type of data specified.
     * @param schemaName Schema to which the table is to be placed.
     * @param sampleID ID of the sample to whom the data is associated.
     * @param dataType <code>DataType</code> specifying the data which the table created is to hold
     */
    public abstract void createDataTable( String schemaName, String sampleID, DataType dataType );
    
    /**
     * Creates a valid table name based on the ID of an sample and data type definition.
     * @param sampleID ID of an sample
     * @param dataType Data type definition
     * @return A valid table name
     */
    public abstract String createTableName( String sampleID, DataType dataType );
    
    /**
     * Removes the listed samples from the database.
     * @param samples A list of samples to be removed
     */
    public abstract void removeSamples( List<String> samples );
    
    /**
     * Retrieves an sample with the given id from the database.
     * @param id ID of a sample
     * @return instance of <code>Sample</code> or <code>null</code> if no matching sample is found.
     */
    public abstract Sample getSample( String id);  
    
    /**
     * Lists currently stored samples
     * @return
     */
    public abstract List<Sample> getSamples();
    
    /**
     * Lists the datatypes currently associated with a certain sample.
     * @param sampleID id of a sample
     * @return a list of samp,e ids
     */
    public abstract List<String> getDataTypes( String sampleID );
    

    /**
     * Gets the current valid ID number that can be used to insert a new value to the table.
     * @param schemaName Schema used to store the table.
     * @param sampleID ID of the sample to whom the data table is associated.
     * @param dataType Data type definition of the table in question
     * @return current valid id number
     */
    public abstract int getCurrentId( String schemaName, String sampleID, DataType dataType );
}
