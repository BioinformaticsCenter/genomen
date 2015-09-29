package com.genomen.readers.vcfreader;

import java.util.Map;

/**
 * Presents a row in VCF file
 * @author ciszek
 */
public class VCFRow {
 
    private final String chrom;
    private final int pos;
    private final String id;
    private final String[] ref;
    private final String[] alt;
    private final String quality;
    private final String[] filter;
    private final String[][] info;
    private final String[] format;
    private final Map<String, String[]> genotypes;
    
    public VCFRow( String chrom, int pos, String id, String ref[], String alt[], String quality, String[] filter, String[][] info, String[] format, Map<String, String[]> genotypes ) {
        this.chrom = chrom;   
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.quality = quality;
        this.filter = filter;
        this.info = info;
        this.format = format;
        this.genotypes = genotypes;
    }
    
    /**Gets the CHROM column of this row.
     * @return the CHROM column 
     */
    public String getChrom() {
        return chrom;
    }

    /**Gets the POS column of this row.
     * @return the POS column   
     */
    public int getPos() {
        return pos;
    }

    /**Gets the ID column of this row.
     * @return the ID column
     */
    public String getId() {
        return id;
    }

    /**Gets the REF column of this row.
     * @return the REF column
     */
    public String[] getRef() {
        return ref;
    }

    /**Gets the ALT column of this row.
     * @return the ALT column
     */
    public String[] getAlt() {
        return alt;
    }

    /**Gets the QUALITY column of this row.
     * @return the QUALITY column.
     */
    public String getQuality() {
        return quality;
    } 

    /**Gets the FILTER column of this row.
     * @return the FILTER column.
     */
    public String[] getFilter() {
        return filter;
    }

    /**Gets the INFO column of this row presented as String array of key-value pairs.
     * @return the INFO column
     */
    public String[][] getInfo() {
        return info;
    }

    /**Gets the format definition of this row.
     * @return format definition
     */
    public String[] getFormat() {
        return format;
    }

    /**Gets the mapping of sample ids to genotype values presented in this row.
     * @return a mapping of sample ids to gebnotypes.
     */
    public Map<String, String[]> getGenotypes() {
        return genotypes;
    }
    
}
