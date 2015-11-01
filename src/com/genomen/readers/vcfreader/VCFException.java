package com.genomen.readers.vcfreader;

/**
 * Exception thrown by VCFReader
 * @author ciszek
 */
public class VCFException extends Exception {

    protected static final String INVALID_SYNTAX = "Invalid syntax at line ";
    protected static final String VALUE_MISMATCH = "Value does not match definition at line ";
    protected static final String UNKNOWN_VALUE = "Unknown value at line ";

    public VCFException(String message, int line) {
        super(message+line);
    }

    public VCFException(String message, int line, int column) {
        super(message+line+ " column " + column);
    }        
}   
