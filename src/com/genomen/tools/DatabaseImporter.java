package com.genomen.tools;

import com.genomen.utils.database.XMLImporter;

/**
 * Convenience class for importing the database
 * @author ciszek
 */
public class DatabaseImporter {

    private static final String SCHEMA_NAME = "GENOMEN";

    public static void main ( String[] args ) {

        String xmlFilePath = "test_resources/import3.xml";
        String schemaName = "GENOMEN";
        XMLImporter importer = new XMLImporter();
        importer.importToDatabase(xmlFilePath, schemaName);
    }

}
