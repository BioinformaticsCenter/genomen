package com.genomen.readers.vcfreader;

/**
 * Presents a FILTER entry in VCF file.
 * @author ciszek
 */
public class VCFFilter {
 
    private final String id;
    private final String description;
    
    public VCFFilter( String id, String description ) {
        this.id = id;
        this.description = description;
    }

    /**Gets the filter id.
     * @return the filter id
     */
    public String getId() {
        return id;
    }

    /**Gets the filter description.
     * @return the filter description.
     */
    public String getDescription() {
        return description;
    }
    
}
