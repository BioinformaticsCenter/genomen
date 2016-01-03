package com.genomen.ui.cli;

import com.genomen.core.AnalysisExecutor;
import com.genomen.utils.database.SchemaTruncator;
import com.genomen.core.AnalysisRequest;
import com.genomen.core.Configuration;
import com.genomen.core.DataSet;
import com.genomen.core.Sample;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Command-line UI for GenOmen
 * @author ciszek
 */
public class Genomen implements Observer {

    private static final String HELP_FILE_PATH = "CLIHelp.txt";

    private static final String MESSAGE_GENOMEN_VERSION = "genomen version: ";
    private static final String MESSAGE_TASK_STARTED = "Task started: ";
    private static final String MESSAGE_TASK_COMPLETED = "Task completed: ";
    private static final String MESSAGE_LOADING_DATASETS = "Loading datasets...";
    private static final String MESSAGE_CREATING_REPORTS = "Creating reports...";
    private static final String MESSAGE_PERFORMING_ANALYSIS = "Performing analysis...";
    private static final String MESSAGE_CLEARING_DATA = "Clearing data...";
    private static final String MESSAGE_UNABLE_TO_READ_HELP = "Unable to read CLIHelp.txt";
    
    private static final String MESSAGE_CREATING_DATABASE = "Creating database...";
    private static final String MESSAGE_DATABASE_CREATED = "Database created!";
    
    private static final String MESSAGE_IMPORTING_DATABASE = "Importing database...";
    private static final String MESSAGE_DATABASE_IMPORTED = "Database imported!";    
    
    private static final String MESSAGE_EXPORTING_DATABASE = "Exporting database...";
    private static final String MESSAGE_DATABASE_EXPORTED = "Database exported!";    
    
    private static final String MESSAGE_CREATING_TEMPLATE = "Creating template...";
    private static final String MESSAGE_TEMPLATE_CREATED = "Template created!";
    
    private static final String MESSAGE_IMPORTING_DATASETS = "Importing dataset...";
    private static final String MESSAGE_DATASET_IMPORTING_COMPLETED = "Importing completed.";
    
    private static final String MESSAGE_NO_DATASETS_IMPORTED = "No datasets stored.";
    private static final String MESSAGE_LISTING_DATASETS = "Listing currently stored datasets:";
    
    private static final String MESSAGE_REMOVING_SAMPLES = "Removing samples...";
    private static final String MESSAGE_SAMPLES_TO_REMOVE = "Samples to be removed: "; 
    private static final String MESSAGE_REMOVING_ALL_SAMPLES = "No samples specified, removing all samples.";     
    private static final String MESSAGE_SAMPLES_REMOVED = "Samples removed!";
    
    private static final String MESSAGE_DELETING_DATABASE = "Deleting database";
    private static final String MESSAGE_DATABASE_DELETED = "Database deleted";
    private static final String MESSAGE_DATABASE_DOES_NOT_EXIST = "Unable to delete database. Database does not exist.";
    
    private static final String MESSAGE_DATASET_IMPORT_ERROR = "Unable to import dataset: ";
    private static final String MESSAGE_ERROR_OCCURRED = "ERROR: ";  
      
    private static final float VERSION = 0.8f;
    
    public static void main ( String[] args ) {

        Genomen cli = new Genomen();
        
        try {   
            
            if ( ArgumentProcessor.helpRequired( args ) || ! ArgumentProcessor.knownInput(args)) {
                cli.printHelp();
                return;
            }
            cli.printStartMessage(); 
            cli.performMaintainance(args);  
            if ( ArgumentProcessor.taskRequired(args)) {
                cli.initializeAnalysis(args);     
            }
            else {
                cli.printCompletedMessage();     
            }

        }
        catch (InvalidCLIArgumentException ex) {
            cli.printHelp();
        }    
        
    }

    private AnalysisRequest analysisRequest;

    private void performMaintainance( String[] args ) throws InvalidCLIArgumentException {
        
        if ( ArgumentProcessor.databaseDestructionRequired( args )) {
            System.out.println(MESSAGE_DELETING_DATABASE);
            if ( SchemaTruncator.truncate(Configuration.getConfiguration().getDatabaseSchemaName(),true) ) {
                System.out.println(MESSAGE_DATABASE_DELETED); 
            }
            else {
                System.out.println( MESSAGE_DATABASE_DOES_NOT_EXIST );
            }
            
        }
        if ( ArgumentProcessor.databaseCreationRequired(args)) {
            System.out.println(MESSAGE_CREATING_DATABASE);
            DatabaseRecreator.recreateDatabase(args[1]);
            System.out.println(MESSAGE_DATABASE_CREATED);              
        }    
        if ( ArgumentProcessor.datasetImportRequired(args)) {
            importDatasets(args);
        }        
        if ( ArgumentProcessor.databaseImportRequired(args)) {
            XMLImporter importer = new XMLImporter();
            System.out.println(MESSAGE_IMPORTING_DATABASE);            
            importer.importToDatabase(ArgumentProcessor.getImportedDbFile(args), Configuration.getConfiguration().getDatabaseSchemaName()); 
            System.out.println(MESSAGE_DATABASE_IMPORTED);   
        }
        if ( ArgumentProcessor.databaseExportRequired(args)) {
            System.out.println(MESSAGE_EXPORTING_DATABASE);
            XMLExporter.export(Configuration.getConfiguration().getDatabaseSchemaName(), ArgumentProcessor.getExportedFile(args));
            System.out.println(MESSAGE_DATABASE_EXPORTED);            
        }
        if ( ArgumentProcessor.databaseTemplateRequired(args)) {
            System.out.println(MESSAGE_CREATING_TEMPLATE);
            XMLTemplateCreator.createXMLTemplate(Configuration.getConfiguration().getDatabaseSchemaName(), ArgumentProcessor.getTemplateFile(args));  
            System.out.println(MESSAGE_TEMPLATE_CREATED);              
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
        AnalysisExecutor.start();
        AnalysisExecutor.requestAnalysis(analysisRequest);
    }
    
    /*
     * Ends the analysis.
     */
    private void endAnalysis() {

        AnalysisExecutor.shutDown();
        //Loop through all the reports
        for ( int i = 0; i < analysisRequest.getReports().size(); i++) {

            if ( analysisRequest.getRequiredFormats().contains( ReportFormat.CSV.getName() ) ) {
                CSVReportCreator.createCSV(analysisRequest.getPath(), analysisRequest.getReports().get(i));
            }
            if ( analysisRequest.getRequiredFormats().contains( ReportFormat.XML.getName() ) ) {
                XMLReportCreator.createXML(analysisRequest.getPath(),analysisRequest.getReports().get(i));
            }                
            if ( analysisRequest.getRequiredFormats().contains( ReportFormat.HTML.getName() ) ) {
                XMLReportCreator.createXML(analysisRequest.getPath(), analysisRequest.getReports().get(i));
                XSLTTransformer.transform( analysisRequest.getPath() + analysisRequest.getReports().get(i).getName() + ".xml", Configuration.getConfiguration().getXSLTFilePath(), analysisRequest.getPath() + analysisRequest.getReports().get(i).getName() + ".html");                       
            }  
           
        }
        if ( !analysisRequest.getErrors().isEmpty()) {
            System.out.println(MESSAGE_ERROR_OCCURRED);
            for ( com.genomen.core.Error error : analysisRequest.getErrors()) {
                System.out.println("\t" + error.getErrorSource());
            }  
        }

        
        printCompletedMessage();
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
    
    private void printStartMessage() {
        System.out.println(MESSAGE_GENOMEN_VERSION + VERSION);
        System.out.println(MESSAGE_TASK_STARTED + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime()));
    }    
    
    private void printCompletedMessage() {
        System.out.println(MESSAGE_TASK_COMPLETED + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Calendar.getInstance().getTime()));
    }     

    /**
     * Prints the contents of CLIHelp.txt
     */
    private void printHelp() {

        File file = new File(HELP_FILE_PATH);
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
        System.out.println(MESSAGE_IMPORTING_DATASETS);      
        List<DataSet> datasets = ArgumentProcessor.parseDataSets(args);
        for ( int i = 0; i < datasets.size(); i++ ) {
            Importer dataSetImporter = ImporterFactory.getDatasetImporterFactory().getImporter(datasets.get(i).getFormat());

            if ( dataSetImporter == null ) {
                continue;
            }

            try {
                dataSetImporter.importDataSet( Configuration.getConfiguration().getDatabaseTempSchemaName(), datasets.get(i).getName(), datasets.get(i).getFiles());
            } catch (ImporterException ex) {          
                System.out.println(MESSAGE_DATASET_IMPORT_ERROR + ex.getMessage() );
            }

        }
        System.out.println(MESSAGE_DATASET_IMPORTING_COMPLETED);  
    }
    
    private void listDatasets() {
        
        DataSetDAO datasetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        List<Sample> individuals = datasetDAO.getIndividuals();
        System.out.println(MESSAGE_LISTING_DATASETS); 
        if ( individuals.isEmpty() ) {
            System.out.println(MESSAGE_NO_DATASETS_IMPORTED);
        }
        else { 
            for ( Sample individual : individuals ) {
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
        System.out.println(MESSAGE_REMOVING_SAMPLES);      
        List<String> samples = ArgumentProcessor.parseRequiredSamples(args);
       
        
        if ( samples.isEmpty()) {
            List<Sample> allSamples = datasetDAO.getIndividuals(); 
            for ( Sample sample : allSamples ) {
                samples.add( sample.getId() );
            }
            System.out.println("\t" + MESSAGE_REMOVING_ALL_SAMPLES);
        }   
        else {
            System.out.println("\t" + MESSAGE_SAMPLES_TO_REMOVE);
            for ( String id : samples) {
                System.out.println("\t"+id);
            }
        }
        datasetDAO.removeIndividuals(samples);

    }
    
    
}
