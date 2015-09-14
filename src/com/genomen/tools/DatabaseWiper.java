package com.genomen.tools;

import com.genomen.utils.database.SchemaTruncator;

/**
 * Convenience class for wiping the database
 * @author ciszek
 */
public class DatabaseWiper {

    private static final String SCHEMA_NAME = "GENOMEN";

    public static void main ( String[] args ) {

        SchemaTruncator.truncate(SCHEMA_NAME,true);

    }

}
