package com.genomen.tools;

import com.genomen.utils.database.XMLTemplateCreator;

/**
 * Convenience class for creating a database template XML.
 * @author ciszek
 */
public class DatabaseXMLTemplateCreator {

    private static final String SCHEMA_NAME = "GENOMEN";

    public static void main ( String[] args ) {

        String schemaName = "GENOMEN";
        String filePath = "Template.xml";
        XMLTemplateCreator.createXMLTemplate(schemaName, filePath);
    }

}
