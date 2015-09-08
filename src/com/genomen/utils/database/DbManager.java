package com.genomen.utils.database;


/**
 * Convenience class for managing the database. 
 * @author ciszek
 */
public class DbManager {

    private static final String COMMAND_IMPORT = "--import";
    private static final String COMMAND_EXPORT = "--export";
    private static final String COMMAND_CLEAR = "--destroy-database";
    private static final String COMMAND_TEMPLATE = "--template";
    
    public static void main ( String[] args ) {


        if ( args.length == 0) {
            return;
        }

        if ( args[0].equals(COMMAND_TEMPLATE) ) {
            createTemplate( args );
        }

        if ( args[0].equals(COMMAND_IMPORT) ) {
            importXML( args );
        }

        if ( args[0].equals(COMMAND_CLEAR) ) {
            clearDatabase( args );
        }

        if ( args[0].equals(COMMAND_EXPORT) ) {
            exportXML( args );
        }
        
    }

    private static void importXML( String[] args ) {

        //If argument is not COMMAND_IMPORT <schema name> <file name>
        if ( args.length != 3 ) {
            return;
        }

        String schemaName = args[1];
        String fileName = args[2];

        XMLImporter importer = new XMLImporter();
        importer.importToDatabase(schemaName, fileName);

    }

    private static void clearDatabase( String[] args ) {

        //If argument is not COMMAND_CLEAR <schema name>
        if ( args.length != 2 ) {
            return;
        }

        SchemaTruncator.truncate(args[1],true);

    }

    private static void exportXML( String[] args ) {

        //If argument is not COMMAND_EXPORT <schema name> <file name>
        if ( args.length != 3) {
            return;
        }

        XMLExporter.export(args[1], args[2]);
    }

    private static void createTemplate( String[] args ) {

        //If argument is not COMMAND_TEMPLATE <schema name> <file name>
        if ( args.length != 3) {
            return;
        }
        
        String schemaName = args[1];
        String fileName = args[2];

        XMLTemplateCreator.createXMLTemplate( schemaName, fileName );
        
    }
    
}
