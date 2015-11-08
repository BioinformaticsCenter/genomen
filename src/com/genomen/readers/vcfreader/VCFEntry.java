package com.genomen.readers.vcfreader;


/**
 * Base class for VCF entries.
 * @author ciszek
 */
public class VCFEntry {

    private final String id;
    private final String type;
    
    public static final String FLAG = "Flag";
    public static final String INTEGER = "Integer";
    public static final String CHARACTER = "Character";
    public static final String STRING = "String";
    public static final String FLOAT = "Float";

    /**Gets the id of this entry.
     * @return entry id
     */
    public String getId() {
        return id;
    }

    /**Gets the type of this entry
     * @return entry type
     */
    public String getType() {
        return type;
    }    
    
    public VCFEntry( String id, String type ) {
        this.id = id;
        this.type = type;
    }
    
}
