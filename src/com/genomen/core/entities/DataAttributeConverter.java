
package com.genomen.core.entities;

/**
 * Translates database attribute types
 * @author ciszek
 */
public class DataAttributeConverter {
    
    public static final int TEXT = 0;
    public static final int INTEGER = 1;
    public static final int DOUBLE = 2;
    
    private static final String CHAR = "CHAR";
    private static final String VARCHAR = "VARCHAR";
    private static final String CLOB = "CLOB";
    
    private static final String INT = "INTEGER";
    private static final String SMALLINT = "SMALLINT";
    
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
        else {
            type = DOUBLE;
        }
        
        return type;
    }
    
}
