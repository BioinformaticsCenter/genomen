package com.genomen.readers.vcfreader;


/**
 * Base class for VCF entries.
 * @author ciszek
 */
class VCFEntry {

    private final String id;
    private final String type;

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