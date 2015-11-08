package com.genomen.importers.derby;



/**
 * Base class for importers that import SNP data.
 * @author ciszek
 */
public abstract class DerbySNPImporter extends DerbyImporter {

    public static final String ID = "VARIANT_ID";
    public static final String CHROMOSOME = "CHROMOSOME";
    public static final String SEQUENCE_START = "SEQUENCE_START";    
    public static final String SEQUENCE_END = "SEQUENCE_END";     
    public static final String STRAND = "STRAND";      
    public static final String ALLELE = "ALLELE";        
    
    public static final String VARIANT_ID = "VARIANT_ID";    
    public static final String TYPE = "TYPE";    
    public static final String NAME = "NAME";      
    public static final String VALUE = "VALUE";      
    
    public static final String VARIANT = "VARIANT";
    public static final String VARIANT_INFO = "VARIANT_INFO";



}
