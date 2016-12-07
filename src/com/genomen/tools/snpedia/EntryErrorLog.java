package com.genomen.tools.snpedia;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jussi
 */
public class EntryErrorLog {

    private HashMap<String,String> entryErrors = new HashMap<String, String>();

    public HashMap<String, String> getEntryErrors() {
        return entryErrors;
    }

    public void setEntryErrors(HashMap<String, String> entryErrors) {
        this.entryErrors = entryErrors;
    }

    /**
     * Log error for the given entryName. If the entryName already exits in the log, append to that error log.
     * Otherwise create a new log entry.
     * @param entryName
     * @param error
     */
    public void logError(String entryName, String error) {

        if (entryErrors.containsKey(entryName)) {
            entryErrors.put(entryName, entryErrors.get(entryName) + "\n" + error);
        } else {
            entryErrors.put(entryName, error);
        }

    }

    @Override
    public String toString() {

        String returnString = "";
        Set set = this.entryErrors.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            returnString += (me.getKey() + ": " + me.getValue() + "\n");
        }


        return returnString;
    }

     public String toWikiMediaString() {

        String returnString = "== Possible problems detected while parsing the pages ==\n";
        Set set = this.entryErrors.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            returnString += "[[" + (me.getKey() + "]] " + me.getValue() + "\n\n");
        }
        return returnString;
    }
}
