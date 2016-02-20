package com.genomen.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Provides singleton access point to base jython script used by analysis rules
 * @author ciszek
 */
public class JythonBaseScript {

    
    private static final JythonBaseScript instance = new JythonBaseScript();
    
    private String baseScript;

    /**
     * Gets the base jython script
     * @return base jython script
     */
    public  String getBaseScript() {
        return baseScript;
    }

    public JythonBaseScript() {
        baseScript = readScript();
    }
    
    /**
     * Returns singleton instance of JythonBaseScriptReader
     * @return instance of JythonBaseScriptReader
     */
    public static JythonBaseScript getInstance() {
        return instance;
    }
    
    private static String readScript() {

        File file = new File("scripts/JythonBase.py");
        String script = "";
        BufferedReader bufferedReader;
        FileReader fileReader;

        try {

            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ( ( line = bufferedReader.readLine() ) != null) {

                script = script.concat(line.concat("\n"));

            }

        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(JythonBaseScript.class ).debug(ex);
        }
        catch (IOException ex) {
            Logger.getLogger(JythonBaseScript.class ).debug(ex);
        }

        return script;

    }

}
