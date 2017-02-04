
package com.genomen.readers.vcfreader;

/**
 * Presents an INFO entry in a VCF file.
 * @author ciszek
 */
public class VCFInfo extends VCFEntry{
    
    private final String number;
    private final String description;
    private final String source;
    private final String version;
    
    public VCFInfo( String id, String number, String type, String description, String source, String version) {
        
        super(id, type);
        this.number = number;
        this.description = description;
        this.source = source;
        this.version = version;
        
    }

    /**Gets the number of expected values for this INFO type.
     * @return the number of expected values
     */
    public String getNumber() {
        return number;
    }

    /**Gets the description of this INFO entry.
     * @return the description this INFO entry
     */
    public String getDescription() {
        return description;
    }

    /**Gets the source of this file.
     * @return the file source
     */
    public String getSource() {
        return source;
    }

    /**Gets the version of this file.
     * @return the file version
     */
    public String getVersion() {
        return version;
    }
    
}
