package com.genomen.ui.cli;

import com.genomen.core.AnalysisExecutor;
import com.genomen.utils.database.SchemaTruncator;
import com.genomen.core.AnalysisRequest;
import com.genomen.core.Configuration;
import com.genomen.core.Analyzer;
import com.genomen.core.TaskState;
import com.genomen.core.reporter.CSVReportCreator;
import com.genomen.core.reporter.Report;
import com.genomen.core.reporter.ReportFormat;
import com.genomen.core.reporter.XMLReportCreator;
import com.genomen.core.reporter.XSLTTransformer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;


/**
 * Command-line UI for GenOmen
 * @author ciszek
 */
public class GDCLI implements Observer {


    private static final String MESSAGE_LOADING_DATASETS = "Loading datasets...";
    private static final String MESSAGE_CREATING_REPORTS = "Creating reports...";
    private static final String MESSAGE_PERFORMING_ANALYSIS = "Performing analysis...";
    private static final String MESSAGE_CLEARING_DATA = "Clearing data...";

    public static void main ( String[] args ) {

        GDCLI cli = new GDCLI();
        cli.initializeAnalysis(args);
    }

    private AnalysisRequest analysisRequest;

    /*
     * Creates the analysis request and initializes the analyzation process..
     */
    private void initializeAnalysis( String[] args) {

        if ( ArgumentProcessor.helpRequired( args )) {
            printHelp();
            return;
        }

         if ( ArgumentProcessor.databaseDestructionRequired( args )) {
            SchemaTruncator.truncate(Configuration.getConfiguration().getDatabaseSchemaName(),true);
            return;
        }

        try {
            analysisRequest = ArgumentProcessor.createRequest(args);
        }
        catch (InvalidCLIArgumentException ex) {

            System.out.println(ex);
            printHelp();
            return;

        }
        analysisRequest.addObserver(this);
        performAnalysis();

    }
    /*
     * Performs the analysis.
     */
    private void performAnalysis() {
        System.out.println("Task started");
        AnalysisExecutor.start();
        AnalysisExecutor.requestAnalysis(analysisRequest);
    }
    /*
     * Ends the analysis.
     */
    private void endAnalysis() {

        AnalysisExecutor.shutDown();
        //Loop through all the reports
        for ( int i = 0; i < analysisRequest.getTotalReports(); i++) {

            if ( analysisRequest.getRequiredFormats().contains( ReportFormat.CSV.getName() ) ) {
                CSVReportCreator.createCSV(analysisRequest.getReport(i));
            }
            if ( analysisRequest.getRequiredFormats().contains( ReportFormat.XML.getName() ) ) {
                XMLReportCreator.createXML(analysisRequest.getReport(i));
            }                
            if ( analysisRequest.getRequiredFormats().contains( ReportFormat.HTML.getName() ) ) {
                XMLReportCreator.createXML(analysisRequest.getReport(i));
                XSLTTransformer.transform(analysisRequest.getReport(i).getName() + ".xml", "config/HTMLTransform.xsl", analysisRequest.getReport(i).getName() + ".html");                       
            }  
    

            
        }
        System.out.println("Task completed");
    }
    

    public synchronized void update(Observable o, Object arg) {

        if ( analysisRequest.isFinished()) {
             endAnalysis();
        }
        if ( analysisRequest.getState() == TaskState.LOADING_DATASETS ) {
            System.out.println( MESSAGE_LOADING_DATASETS );
        }
        if ( analysisRequest.getState() == TaskState.PERFORMING_ANALYSIS) {
            System.out.println( MESSAGE_PERFORMING_ANALYSIS );
        }
        if ( analysisRequest.getState() == TaskState.CLEARING_DATA) {
            System.out.println( MESSAGE_CLEARING_DATA );
        }
        if ( analysisRequest.getState() == TaskState.CREATING_REPORTS) {
            System.out.println( MESSAGE_CREATING_REPORTS );
        }
        
    }

    /**
     * Prints the contents of CLIHelp-txt
     */
    private void printHelp() {

        File file = new File("CLIHelp.txt");
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader( new InputStreamReader( new FileInputStream(file)));
 
            String line;
            while ( ( line = bufferedReader.readLine() ) != null) {
                System.out.println(line);
            }


            bufferedReader.close();
        }
        catch (FileNotFoundException ex) {

        }
        catch (IOException ex) {

        }


    }

}
