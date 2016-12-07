package com.genomen.tools.snpedia;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jussi
 */
public class TemplateContent {

    private String id;
    // TODO: For example population diversity tables contain duplicate values (i.e. allele frequencies).
    // These duplicates are removed when inserting them as keys.
    private LinkedHashMap<String, String> elements = new LinkedHashMap<String, String>();

    public HashMap<String, String> getElements() {
        return elements;
    }

    public void setElements(LinkedHashMap<String, String> elements) {
        this.elements = elements;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {

        String returnString = "";

        returnString += "ID: " + this.id + "\n";
        Set set = this.elements.entrySet();

        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            returnString += "-- " + (me.getKey() + ": " + me.getValue() + "\n");
        }


        return returnString;
    }
}
