package com.genomen.tools.snpedia;

/**
 *
 * @author jussi
 */
public class Entry {

    private String name;
    private String content;

    public Entry(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Entry(String name) {
        this.name = name;
        this.content = "";
    }

    public Entry() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
