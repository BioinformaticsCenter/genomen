package com.genomen.reporter;

import com.genomen.core.AnalysisTask;
import com.genomen.core.Individual;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates analysis reports
 * @author ciszek
 */
public class ReportCreator {

    private static final String REPORTER_LIST_FILE_PATH = "config/Reporters.xml";
    private static List<Reporter> reporters = new ReportersReader().getReporters(REPORTER_LIST_FILE_PATH);

    /**
     * Creates a report using all available reporters
     * @param analysisTask task on which results the report is based
     * @param name name of the report
     * @param language report language
     * @return analysis report
     */
    public static Report createReport( AnalysisTask analysisTask, String name ,String language ) {

        Report report = new Report(name);
        List<IndividualEntry> individualEntries = createIndividualEntries(analysisTask.getIndividuals());

        for ( int individualIndex = 0; individualIndex < individualEntries.size(); individualIndex++) {
            report.addComponent(individualEntries.get(individualIndex));

            for ( int reporterIndex = 0; reporterIndex < reporters.size(); reporterIndex++ ) {
                reporters.get(reporterIndex).createReportComponent(analysisTask, individualEntries.get(individualIndex), language );
            }
        }
        return report;
    }

    private static List<IndividualEntry> createIndividualEntries( List<Individual> individuals ) {

        List<IndividualEntry> individualEntries = new LinkedList<IndividualEntry>();

        for ( int individualIndex = 0; individualIndex < individuals.size(); individualIndex++) {
            individualEntries.add( new IndividualEntry( individuals.get(individualIndex) ));
        }
        return individualEntries;
    }

}
