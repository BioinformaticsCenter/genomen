
package com.genomen.entities;

/**
 * Translates database attribute types
 * @author ciszek
 */
public class DataAttributeConverter {
    
    public static final int TEXT = 0;
    public static final int INTEGER = 1;
    public static final int DOUBLE = 2;
    public static final int BOOLEAN = 3;    
    
    private static final String CHAR = "CHAR";
    private static final String VARCHAR = "VARCHAR";
    private static final String CLOB = "CLOB";
    
    private static final String INT = "INTEGER";
    private static final String SMALLINT = "SMALLINT";
    
    private static final String SQL_BOOLEAN = "BOOLEAN";
    
    /**
     * Translates SQL attribute types by classifying it into three possible classes: "text, "integer" or double.
     * @param sql SQL attribute type
     * @return integer id of the translated attribute class
     */
    public static int sqlTypeToJava( String sql ) {
        
        int type = 0;
        
        if ( sql.matches("^".concat(CHAR) ) || sql.matches("^".concat(VARCHAR)) || sql.equals(CLOB) ) {
            type = TEXT;
        }
        else if ( sql.equals(INT) || sql.equals(SMALLINT) ) {
            type = INTEGER;
        }
        else if ( sql.equals(SQL_BOOLEAN)  ) {
            type = BOOLEAN;
        }
        
        return type;
    }
    
}
