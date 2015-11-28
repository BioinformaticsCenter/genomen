package com.genomen.ui.cli;

import com.genomen.core.AnalysisExecutor;
import com.genomen.utils.database.SchemaTruncator;
import com.genomen.core.AnalysisRequest;
import com.genomen.core.Configuration;
import com.genomen.core.DataSet;
import com.genomen.core.Individual;
import com.genomen.core.TaskState;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.DataSetDAO;
import com.genomen.importers.Importer;
import com.genomen.importers.ImporterException;
import com.genomen.importers.ImporterFactory;
import com.genomen.reporter.CSVReportCreator;
import com.genomen.reporter.ReportFormat;
import com.genomen.reporter.XMLReportCreator;
import com.genomen.reporter.XSLTTransformer;
import com.genomen.tools.DatabaseRecreator;
import com.genomen.utils.database.XMLExporter;
import com.genomen.utils.database.XMLImporter;
import com.genomen.utils.database.XMLTemplateCreator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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
    private static final String MESSAGE_UNABLE_TO_READ_HELP = "Unable to read CLIHelp.txt";
    
    public static void main ( String[] args ) {

        GDCLI cli = new GDCLI();
        
        try {   
            
            if ( ArgumentProcessor.helpRequired( args )) {
                cli.printHelp();
                return;
            }
            cli.performMaintainance(args);  
            if ( ArgumentProcessor.taskRequired(args)) {
                cli.initializeAnalysis(args);     
            }
        
        }
        catch (InvalidCLIArgumentException ex) {
            System.out.print(ex);
            cli.printHelp();

        }    
        
    }

    private AnalysisRequest analysisRequest;

    private void performMaintainance( String[] args ) throws InvalidCLIArgumentException {
        
        if ( ArgumentProcessor.databaseDestructionRequired( args )) {
            SchemaTruncator.truncate(Configuration.getConfiguration().getDatabaseSchemaName(),true);
        }
        if ( ArgumentProcessor.databaseCreationRequired(args)) {
            System.out.println("Creating database...");
            DatabaseRecreator.recreateDatabase(args[1]);
            System.out.println("Database created!");              
        }    
        if ( ArgumentProcessor.datasetImportRequired(args)) {
            importDatasets(args);
        }        
        if ( ArgumentProcessor.databaseImportRequired(args)) {
            XMLImporter importer = new XMLImporter();
            System.out.println("Importing database...");            
            importer.importToDatabase(ArgumentProcessor.getImportedDbFile(args), Configuration.getConfiguration().getDatabaseSchemaName()); 
            System.out.println("Database imported!");   
        }
        if ( ArgumentProcessor.databaseExportRequired(args)) {
            System.out.println("Exporting database...");
            XMLExporter.export(Configuration.getConfiguration().getDatabaseSchemaName(), ArgumentProcessor.getExportedFile(args));
            System.out.println("Database exported!");            
        }
        if ( ArgumentProcessor.databaseTemplateRequired(args)) {
            System.out.println("Creating template...");
            XMLTemplateCreator.createXMLTemplate(Configuration.getConfiguration().getDatabaseSchemaName(), ArgumentProcessor.getTemplateFile(args));  
            System.out.println("Template created!");              
        }
        if ( ArgumentProcessor.datasetListingRequired(args)) {
            listDatasets(); 
        } 
        if ( ArgumentProcessor.sampleRemovalRequired(args)) {
            removeSamples(args);
        }
        
        
  
    }
    
    /*
     * Creates the analysis request and initializes the analyzation process..
     */
    private void initializeAnalysis( String[] args) throws InvalidCLIArgumentException {

        analysisRequest = ArgumentProcessor.createRequest(args);
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
                XSLTTransformer.transform(analysisRequest.getReport(i).getName() + ".xml", Configuration.getConfiguration().getXSLTFilePath(), analysisRequest.getReport(i).getName() + ".html");                       
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
     * Prints the contents of CLIHelp.txt
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
            System.out.println(MESSAGE_UNABLE_TO_READ_HELP);
        }
        catch (IOException ex) {
            System.out.println(MESSAGE_UNABLE_TO_READ_HELP);
        }


    }
    
    private void importDatasets(String[] args) throws InvalidCLIArgumentException {
        System.out.println("Importing dataset...");      
        List<DataSet> datasets = ArgumentProcessor.parseDataSets(args);
        for ( int i = 0; i < datasets.size(); i++ ) {
            Importer dataSetImporter = ImporterFactory.getDatasetImporterFactory().getImporter(datasets.get(i).getFormat());

            if ( dataSetImporter == null ) {
                continue;
            }

            try {
                dataSetImporter.importDataSet( Configuration.getConfiguration().getDatabaseTempSchemaName(), datasets.get(i).getName(), datasets.get(i).getFiles());
            } catch (ImporterException ex) {          
                System.out.println(ex);
            }

        }
        System.out.println("Importing completed.");  
    }
    
    private void listDatasets() {
        
        DataSetDAO datasetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        List<Individual> individuals = datasetDAO.getIndividuals();
        
        if ( individuals.isEmpty() ) {
            System.out.println("No datasets imported");
        }
        else {
            System.out.println("Listing currently stored datasets:");  
            for ( Individual individual : individuals ) {
                System.out.print( individual.getId() + "\t");
                List<String> dataTypes = datasetDAO.getDataTypes(individual.getId());
                for ( String type: dataTypes) {
                    System.out.print(type+"\t");
                }
                System.out.print("\n");
            }
            System.out.println("Total: " + individuals.size() + " samples");
        }    
    }
    
    private void removeSamples( String[] args ) throws InvalidCLIArgumentException {
        
        DataSetDAO datasetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        System.out.println("Removing samples...");      
        List<String> samples = ArgumentProcessor.parseRequiredSamples(args);
        datasetDAO.removeIndividuals(samples);
        System.out.println("Samples removed.");          
    }
    
    
}
