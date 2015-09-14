package com.genomen.utils;

import java.util.Random;

/**
 * Convenience class for generating Strings composed of random numbers.
 * @author ciszek
 */
public class RandomStringGenerator {
    
    public static String generateRandomString( int length ) {
        
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        
        for ( int i = 0; i < length; i++ ) {
            
            int charValue = 65 + random.nextInt(25);
            stringBuilder.append( (char)charValue );
        }
        return stringBuilder.toString();
    }
    
}
