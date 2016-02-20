package com.genomen.entities;

import com.genomen.core.Configuration;
import java.util.Map;

/**
 * Provides listing of known data types.
 * @author ciszek
 */
public class DataTypeManager {
    
    private static DataTypeManager instance = new DataTypeManager();
    
    public static DataTypeManager getInstance() {
        return instance;
    }
    
    private static Map<String, DataType> dataTypes = DataTypeReader.readDataTypes(Configuration.getConfiguration().getDataTypeListPath() );
    
    private DataTypeManager() {}
    
    public DataType getDataType( String dataTypeID ) {
        return dataTypes.get(dataTypeID);
    }
    
}
