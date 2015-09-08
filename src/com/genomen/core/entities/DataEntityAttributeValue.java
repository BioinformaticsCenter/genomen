package com.genomen.core.entities;

/**
 * Presents the value of an attribute of a data entity
 * @author ciszek
 */
public final class DataEntityAttributeValue {
    
    private final double numericValue;
    private final String textValue;
    
    /**
     * Gets the <code>int</code> value of this attribute;
     * @return attribute value
     */
    public int getInt() {
        return (int)numericValue;
    }
    
    /**
     * Gets the <code>double</code> value of this attribute.
     * @return attribute value
     */
    public double getDouble() {
        return numericValue;
    }
    
    /**
     * Gets the <code>String</code> value of this attribute.
     * @return attribute value
     */
    public String getString() {
        return textValue;
    }
    
    /**
     * Constructs an attribute value with the given numeric value.
     * @param p_numericValue value of this attribute as a </code>double</code>
     */
    public DataEntityAttributeValue( double p_numericValue ) {
        numericValue = p_numericValue;
        textValue = Double.toString(p_numericValue);
    }
    
    /**
     * Constructs an attribute value with the given numeric value.
     * @param p_numericValue value of this attribute as a <code>int</code>
     */
    public DataEntityAttributeValue( int p_numericValue ) {
        numericValue = p_numericValue;
        textValue = Integer.toString(p_numericValue);        
    }
    
    /**
     * Constructs an attribute value with the given text.
     * @param p_textValue value of this attribute as <code>String</code>
     */
    public DataEntityAttributeValue( String p_textValue ) {        
        textValue = p_textValue;
        numericValue = 0;      
    }
       
    
}
