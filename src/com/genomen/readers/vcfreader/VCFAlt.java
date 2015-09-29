
package com.genomen.readers.vcfreader;

/**
 * Presents ALT definitions in a VCF-file.
 * @author ciszek
 */
public class VCFAlt {
    
    private final String id;
    private final String description;
    
    public VCFAlt( String id, String description ) {
        this.id = id;
        this.description = description;
    }

    /**Gets the id of this ALT
     * @return the ALT id
     */
    public String getId() {
        return id;
    }

    /**Gets the description of this ALT
     * @return the ALT description
     */
    public String getDescription() {
        return description;
    }
    
}
