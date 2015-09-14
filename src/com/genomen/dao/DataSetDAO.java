package com.genomen.dao;

import com.genomen.core.entities.DataEntity;
import com.genomen.core.entities.DataType;


/**
 * Interface for accessing datasets
 * @author ciszek
 */
public interface DataSetDAO {

    public abstract DataEntity getDataEntity( String schemaName, String taskID, String individualID, String dataID, DataType dataType );
    public abstract void createDataTable( String schemaName, String taskID, DataType dataType );
    public abstract String createTableName( String taskID, DataType dataType );
    
}
