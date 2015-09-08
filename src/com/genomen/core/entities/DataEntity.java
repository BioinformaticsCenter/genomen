package com.genomen.core.entities;

import java.util.HashMap;

/**
 * Presents an unit of data.
 * @author ciszek
 */
public final class DataEntity  {

    private final DataType dataType;
    private final HashMap<String, DataEntityAttributeValue> dataEntityAttributes;
    
    /**
     * Gets a <code>DataType</code> that specifies the attributes of this entity.
     * @return an instance of <code>Data</code>
     */
    public DataType getDataType() {
        return dataType;
    }
    
    /**
     * Gets the value of a specific attribute of this entity.
     * @param attributeName name of a attribute
     * @return value of an attribute
     */
    public DataEntityAttributeValue getDataEntityAttribute( String attributeName ) {
        return dataEntityAttributes.get(attributeName);
    }
    
    /**
     * Constructs a data entity with the given attributes and values.
     * @param p_dataType specification for the type of this entity
     * @param p_dataEntityAttributes attribute values of this entity
     */
    public DataEntity ( DataType p_dataType, HashMap<String, DataEntityAttributeValue> p_dataEntityAttributes ) {
        
        dataType = p_dataType;
        dataEntityAttributes = p_dataEntityAttributes;
        
    }   

}
