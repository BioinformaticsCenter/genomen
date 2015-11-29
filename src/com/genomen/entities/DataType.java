package com.genomen.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Used to present qualities of different data types.
 * @author ciszek
 */
public final class DataType {
    
    private final String id;
    private final HashMap<String, DataTypeAttribute> values;
    
    /**
     * Gets the identifier of this datatype. 
     * @return datatype identifier
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the names of the attributes associated with this datatype.
     * @return a list of attribute names
     */
    public List<String> getAttributeNames() {
        return new LinkedList( values.keySet() );
    } 
    
    /**
     * Gets the type of an attribute that belongs to the set this datatype's attributes.
     * @param attributeName name of an attribute
     * @return type of an attribute
     */
    public String getAttributeType( String attributeName ) {
        DataTypeAttribute attribute = values.get(attributeName);
        if ( attribute == null ) {
            Logger.getLogger( DataType.class ).error( "Attribute " + attributeName + " does not exist in " + id );
        }
        return attribute.getType();
    }
    
    /**
     * Gets the size of an attribute that belongs to the set this datatype's attributes.
     * @param attributeName name of an attribute
     * @return size of an attribute
     */
    public int getAttributeSize( String attributeName ) {
        DataTypeAttribute attribute = values.get(attributeName);
        if ( attribute == null ) {
            Logger.getLogger( DataType.class ).error( "Attribute " + attributeName + " does not exist in " + id );            
            return -1;
        }        
        return attribute.getSize();
    }
    
    /**
     * Determines whether this attribute required.
     * @param attributeName name of an attribute
     * @return <code>false</code> if the attribute is not required, <code>true</code> otherwise.
     */
    public boolean isRequiredAttribute( String attributeName ) {
        DataTypeAttribute attribute = values.get(attributeName);
        if ( attribute == null ) {
            Logger.getLogger( DataType.class ).error( "Attribute " + attributeName + " does not exist in " + id );
            return true;
        }              
        return attribute.isRequired();
    }    
    
    /**
     * Constructs a DataType with the given id and attributes.
     * @param p_id id for this datatype
     * @param p_values attributes of this datatype
     */
    public DataType( String p_id, HashMap<String, DataTypeAttribute> p_values ) {
        id = p_id;
        values = p_values;
    }
    
}
