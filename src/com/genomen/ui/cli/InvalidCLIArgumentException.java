package com.genomen.ui.cli;

/**
 * Exception thrown when invalid command-line arguments are inputted.
 * @author ciszek
 */
public class InvalidCLIArgumentException extends Exception {

    /**
     * Creates an exception with the given error message.
     * @param p_message error message
     */
    public InvalidCLIArgumentException( String p_message ) {
        super(p_message);
    }

}
