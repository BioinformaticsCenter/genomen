package com.genomen.reporter;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for report container report components
 * @author ciszek
 */
public abstract class CompositeReportComponent extends ReportComponent {

    private final LinkedList<ReportComponent> reportComponents = new LinkedList<ReportComponent>();

    /**
     * Gets the list of subcomponents contained in this component
     * @return a list of subcomponents
     */
    public List<ReportComponent> getComponents() {
        return reportComponents;
    }
    
    /**
     * Adds a new report component to this entry
     * @param reportComponent report component to be added
     */
    public void addComponent( ReportComponent reportComponent ) {
        reportComponents.add(reportComponent);
    }


}
