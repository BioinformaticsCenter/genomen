package com.genomen.readers.vcfreader;

/**
 * Presents a value in VCF file
 * @author ciszek
 */
class VCFValue extends VCFEntry {
    
    private String value;

    public VCFValue( String id, String type, String value  ) {
        super(id,type);
        this.value = value;
    }
    
    public int getIntegerValue() {
        return Integer.parseInt(value);
    }
    
    public float getFloatValue() {
        return Float.parseFloat(value);
    }
    
    public String getStringValue() {
        return value;
    }
    
}
