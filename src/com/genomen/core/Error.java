package com.genomen.core;

import com.genomen.dao.DAOFactory;
import com.genomen.dao.ErrorDAO;

/**
 * Passes information about errors that occur during the analysis.
 * @author ciszek
 */
public class Error {

    private final ErrorType type;
    private String sourceName = null;
    
    public enum ErrorType {
        
        FILE_NOT_FOUND(1),
        CANCELLED(2),
        CORE_SHUTDOWN(3),
        CAN_NOT_READ_FILE(4),
        ANALYSIS_NOT_AVAILABLE(5),
        UNABLE_TO_IMPORT(6),
        INDIVIDUAL_EXISTS(7);
        
        private final int id;
        
        private ErrorType( final int p_id ) {
            id = p_id;
        }
        
        public int getId() {
            return id;
        }
        
    }
    
    /**
     * Returns the type of error expressed by this instance. The type matches 
     * error descriptions in the database.
     * @return the type of the error.
     */
    public ErrorType getErrorType() {
        return type;
    }
    /**
     * Return the name of the source of the error expressed by this instance.
     * @return the source of the error.
     */
    public String getErrorSource() {
        return sourceName;
    }
    
    /**
     * An error message with a specific type
     * @param p_type error type
     */
    public Error( ErrorType p_type ) {
        type = p_type;
    }

    /**
     * An error message with a specific type and source
     * @param p_type error type
     * @param p_sourceName error source
     */
    public Error( ErrorType p_type, String p_sourceName ) {
        type = p_type;
        sourceName = p_sourceName;
    }

    /**
     * Retrieves the error message associated with the error type given as a 
     * parameter from the systems database. 
     * @param error <code>Error</code> which message is to be retrieved.
     * @return Returns the error message.
     */
    public static String getMessage( ErrorType error ) {
        ErrorDAO errorDAO = DAOFactory.getDAOFactory().getErrorDAO();
        return errorDAO.getMessage(error.getId(), Configuration.getConfiguration().getLanguage());
    }

    /**
     * Retrieves the error message associated with the error type given as a 
     * parameter from the systems database and includes the source of the error in 
     * the returned message..  
     * @param error Error which message is to be retrieved.
     * @param source Source of the error.
     * @return Error message combined with the source of the error..
     */
    public static String getMessage( ErrorType error, String source ) {
        ErrorDAO errorDAO = DAOFactory.getDAOFactory().getErrorDAO();
        return errorDAO.getMessage(error.getId(), Configuration.getConfiguration().getLanguage()).concat( ": ").concat(source);
    }
    

}
