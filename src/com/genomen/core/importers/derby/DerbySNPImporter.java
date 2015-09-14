package com.genomen.core.importers.derby;



/**
 * Base class for importers that import SNP data.
 * @author ciszek
 */
public abstract class DerbySNPImporter extends DerbyImporter {

    public static final String ID = "ID";
    public static final String CHROMOSOME = "CHROMOSOME";
    public static final String SEQUENCE_START = "SEQUENCE_START";    
    public static final String SEQUENCE_END = "SEQUENCE_END";     
    public static final String STRAND = "STRAND";      
    public static final String ALLELE = "ALLELE";        

    public DerbySNPImporter() {
        super("SNP");
    }



}
