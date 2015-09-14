package com.genomen.tools;

import com.genomen.core.Configuration;
import com.genomen.utils.database.XMLExporter;

/**
 * Convenience class for exporting the database.
 * @author ciszek
 */
public class DatabaseExporter {

    private static final String SCHEMA_NAME = Configuration.getConfiguration().getDatabaseSchemaName();

    public static void main ( String[] args ) {

        String filePath = "ExportedXML.xml";
        XMLExporter.export(SCHEMA_NAME, filePath);
    }

}
