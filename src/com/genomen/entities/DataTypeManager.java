package com.genomen.entities;

import com.genomen.core.Configuration;
import java.util.Map;

/**
 * Provides listing of known data types.
 * @author ciszek
 */
public class DataTypeManager {
    
    private static Map<String, DataType> dataTypes = DataTypeReader.readDataTypes(Configuration.getConfiguration().getDataTypeListPath() );
    
    public static DataType getDataType( String dataTypeID ) {
        return dataTypes.get(dataTypeID);
    }
    
}
