package com.genomen.reporter;

/**
 * Available report formats. 
 * @author ciszek
 */
public enum ReportFormat {
    
    XML("XML"),
    HTML("HTML"),
    CSV("CSV");
    
    private final String name;
    
    public String getName() {
        return name;
    }
    
    private ReportFormat(String p_name) {
        name = p_name;
    }
}
