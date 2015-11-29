package com.genomen.importers;

import com.genomen.core.Sample;
import java.util.List;

/**
 * Interface defining the basic functionality common to all dataset importers.
 * @author ciszek
 */
public interface Importer {
 
    public abstract List<Sample> importDataSet( String schemaName, String individualID, String[] fileNames ) throws ImporterException;
}
