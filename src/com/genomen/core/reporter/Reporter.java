package com.genomen.core.reporter;

import com.genomen.core.AnalysisTask;
/**
 * Defines the basic behavior of all reporters.
 * @author ciszek
 */
public abstract class Reporter {

    /**
     * Creates a component for an analysis report based on the data given as a parameter.
     * @param analysisTask the task on which results the report is based on
     * @param reportComponent entry to which the component is to be added
     * @param language the language to be used in the report.
     */
    public abstract void createReportComponent( AnalysisTask analysisTask, ReportComponent reportComponent, String language );


}
