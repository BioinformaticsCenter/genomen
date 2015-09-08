package com.genomen.utils.database;

/**
 * Directs error messages.
 * @author ciszek
 */
public class ErrorManager {


    public static void reportError( String error ) {
 
        if ( error != null ) {
            System.out.println(error);
        }
    }

}
