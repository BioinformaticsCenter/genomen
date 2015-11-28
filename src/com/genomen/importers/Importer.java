package com.genomen.importers;

import com.genomen.core.Individual;
import java.util.List;

/**
 * Interface defining the basic functionality common to all dataset importers.
 * @author ciszek
 */
public interface Importer {
 
    public abstract List<Individual> importDataSet( String schemaName, String individualID, String[] fileNames ) throws ImporterException;
}
