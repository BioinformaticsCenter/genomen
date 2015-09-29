package com.genomen.readers.vcfreader;

/**
 * 
 * @author ciszek
 */
public class VCFFormat extends VCFEntry{
   
    private final String number;
    private final String description;
    
    public static final String INTEGER = "Integer";
    public static final String FLOAT = "Float";
    public static final String STRING = "String";
    public static final String FLAG = "Flag";
    public static final String CHAR = "Char";
    
    public VCFFormat( String id, String number, String type, String description ) {
        super(id, type);
        this.number = number;
        this.description = description;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
}
