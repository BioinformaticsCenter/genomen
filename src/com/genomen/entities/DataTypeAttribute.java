package com.genomen.entities;

/**
 * Presents the qualities of an attribute of a datatype.
 * @author ciszek
 */
public final class DataTypeAttribute {
    
    private final String name;
    private final String type;
    private final int size;
    private final boolean required;
    
    /**
     * Gets the name of this attribute.
     * @return attribute name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the type of this attribute.
     * @return attribute type.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets the size of this attribute.
     * @return attribute size
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Is this attribute required.
     * @return <code>true</code> if this attribute is required, <code>false</code> otherwise.
     */
    public boolean isRequired() {
        return required;
    }
    
    /**
     * Constructs a <code>DataTypeAttribute</code> with the given qualities.
     * @param p_name name of this attribute
     * @param p_type type of this attribute
     * @param p_size size of this attribute
     * @param p_required is this attribute required
     */
    public DataTypeAttribute( String p_name, String p_type, int p_size, boolean p_required ) {
        name = p_name;
        type = p_type;
        size = p_size;
        required = p_required;
    }
    
}
