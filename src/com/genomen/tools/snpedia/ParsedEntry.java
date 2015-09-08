package com.genomen.tools.snpedia;

import java.util.ArrayList;

/**
 *
 * @author jussi
 */
public class ParsedEntry {

    private String id;
    private String body;
    ArrayList<TemplateContent> templates = new ArrayList<TemplateContent>();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<TemplateContent> getTemplates() {
        return templates;
    }

    public void setTemplates(ArrayList<TemplateContent> templates) {
        this.templates = templates;
    }

    @Override
    public String toString() {
        String returnString = "";

        returnString += "ID: " + this.id + "\n";
        returnString += "BODY: " + this.body + "\n";

        returnString += "TEMPLATES: ----\n";

        for (TemplateContent template : templates) {
            returnString += template.toString();
        }
        returnString += "----\n";

        return returnString;

    }
}
