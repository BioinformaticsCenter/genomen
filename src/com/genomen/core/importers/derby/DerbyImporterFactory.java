package com.genomen.core.importers.derby;

import com.genomen.core.importers.Importer;
import com.genomen.core.importers.ImporterFactory;

/**
 * Factory that creates importers specific for Derby.
 * @author ciszek
 */
public class DerbyImporterFactory extends ImporterFactory{
    
    public Importer getImporter( String fileType ) {

        Importer parser = null;
        if ( fileType.equalsIgnoreCase(TWENTYTHREEANDME)) {

                parser = new DerbyTwentyThreeandMeImporter();
        }
        if ( fileType.equalsIgnoreCase(PED)) {

                parser = new DerbyPEDImporter();
        }        

    return parser;
    }
    
}
