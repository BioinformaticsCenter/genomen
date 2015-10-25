package com.genomen.importers;

/**
 * Exception thrown by dataset importers
 * @author ciszek
 */
public class ImporterException extends Exception {
    
    public static final String TEMP_FILE_ERROR = "Unable to access tmp file";
    public static final String CONNECTION_FAILURE = "Failed to establish database connection";
    public static final String DATA_TABLE_INDEX_ERROR = "Valid database index for dataset can not be retrieved: ";
    public static final String INDIVIDUAL_ID_ERROR = "Individual Id already exists: ";
    public static final String UNABLE_TO_READ_DATASET = "Unable to read dataset: ";
     
    public ImporterException( String message ) {
        super(message);
    }
    
    public ImporterException( String message, String value ) {
        super(message + value);
    }    
    
}
