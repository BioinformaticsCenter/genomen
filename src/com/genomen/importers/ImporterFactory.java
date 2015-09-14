package com.genomen.importers;

import com.genomen.importers.derby.DerbyImporterFactory;
import com.genomen.core.Configuration;

/**
 * Factory class that produces importers for different formats.
 * @author ciszek
 */
public abstract class ImporterFactory {

    public static final int DERBY = 1;
    public static final String TWENTYTHREEANDME = "23andme";
    public static final String PED = "ped";   
    
    /**
     * Returns an importer for a certain format
     * @param fileType File format.
     * @return Importer.
     */
    public abstract Importer getImporter(String fileType );
    
    /**
     * 
     * @return
     */
    public static ImporterFactory getDatasetImporterFactory() {
        int dbID = Configuration.getConfiguration().getDBType();

        switch (dbID) {

            case DERBY:
                return new DerbyImporterFactory();
            default:
                return null;
        }
    
    } 
    
}
