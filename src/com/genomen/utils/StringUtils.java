package com.genomen.utils;

import java.nio.charset.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 *
 * @author jussi
 */
public class StringUtils {

    /**
     * Converts a given string to use the given encoding, e.g. UTF-8. Replaces non-compliant chracters with a space i.e. " "
     * @param inputString Input string.
     * @param targetCharset Charset to use for conversion, e.g. UTF-8
     * @return
     * @throws CharacterCodingException
     */
    public static String forceEncoding(String inputString, String targetCharset) throws CharacterCodingException {

        String returnString = "";

        Charset charset = Charset.forName(targetCharset);
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        // Replace non-compliant characters with " "
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        encoder.replaceWith(" ".getBytes());

        // Convert the string to targetCharset bytes in a ByteBuffer
        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(inputString));

        // Convert bytes in a ByteBuffer to a character ByteBuffer and then back to a string.
        CharBuffer cbuf = decoder.decode(bbuf);
        returnString = cbuf.toString();

        return returnString;
    }

    /**
     * Replace line feeds and carriage returns with a space.
     * @param text
     * @return
     */
    public static String removeLineFeeds(String text) {

        return text.replaceAll("\\r\\n|\\r|\\n", " ");
    }
}
