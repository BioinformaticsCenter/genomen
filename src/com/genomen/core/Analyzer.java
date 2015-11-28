package com.genomen.core;

import com.genomen.core.Error.ErrorType;
import com.genomen.importers.Importer;
import com.genomen.importers.ImporterFactory;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.DataSetDAO;
import com.genomen.dao.TaskDAO;
import com.genomen.importers.ImporterException;
import com.genomen.reporter.Report;
import com.genomen.reporter.ReportCreator;
import com.genomen.utils.RandomStringGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.log4j.Logger;


/**
 * Executes analyzes
 * @author ciszek
 */
public class Analyzer {


    /**
     * Performs an analysis based on the request given as a parameter.
     * @param analysisRequest request containing necessary information for the analysis.
     */
    protected static void analyze( AnalysisRequest analysisRequest ) {
        
        TaskDAO taskDAO = DAOFactory.getDAOFactory().getTaskDAO();

        //Create a list of requested analyses that match the list of actual available analyses.
        List performableAnalyses = createPerformableAnalysisList(analysisRequest);
        analysisRequest.changeState( TaskState.LOADING_DATASETS );
        //Create the analysis task to contain and faciliate the exchange of analysis related data.
        AnalysisTask analysisTask = createAnalysisTask(analysisRequest);
        
        //If analysistask could not be created, 
        if ( analysisTask == null) {
            analysisRequest.addError( new Error( Error.ErrorType.UNABLE_TO_IMPORT));
            analysisRequest.changeState( TaskState.FINISHED ); 
            return;
        }
               
        taskDAO.changeTaskState( Configuration.getConfiguration().getDatabaseTempSchemaName(), analysisTask.getTaskID(), TaskState.PERFORMING_ANALYSIS);
        analysisRequest.changeState( TaskState.PERFORMING_ANALYSIS );
        //Perform analyses
        performAnalyses( analysisTask, performableAnalyses);
        
        //Create reports based on the results
        taskDAO.changeTaskState( Configuration.getConfiguration().getDatabaseTempSchemaName(), analysisTask.getTaskID(), TaskState.CREATING_REPORTS);        
        analysisRequest.changeState( TaskState.CREATING_REPORTS );
        createReports( analysisRequest, analysisTask );

        //If data persistence is required, remove the task but leave the data.
        if ( analysisRequest.isPersistDatasets() ) {
            analysisTask.removeTask();
        }
        //Otherwise remove all task related data.
        else {
            taskDAO.changeTaskState( Configuration.getConfiguration().getDatabaseTempSchemaName(), analysisTask.getTaskID(), TaskState.CLEARING_DATA);        
            analysisRequest.changeState( TaskState.CLEARING_DATA );
            analysisTask.clearData();       
        }
           
        //Finish
        taskDAO.changeTaskState( Configuration.getConfiguration().getDatabaseTempSchemaName(), analysisTask.getTaskID(), TaskState.FINISHED);          
        analysisRequest.changeState( TaskState.FINISHED ); 
 
    }
    

    /*
     * Performs the requested analyzes
     */
    private static void performAnalyses( AnalysisTask analysisTask, List<AnalyzationLogic> performableAnalyses ) {

        for ( int i = 0; i < performableAnalyses.size(); i++) {
            performableAnalyses.get(i).analyze(analysisTask);
        }
    }

    /**
     * Returns a list of performable analyzes based on a the list of requested analyzes
     */
    private static List<AnalyzationLogic> createPerformableAnalysisList( AnalysisRequest analysisRequest ) {

        List<String> requestedAnalyses = analysisRequest.getRequiredAnalyses();
        ArrayList<AnalyzationLogic> performableAnalyses = new ArrayList<AnalyzationLogic>();

        for ( int i = 0; i < requestedAnalyses.size(); i++) {

            if ( Logics.getInstance().analyzationLogicAvailable(requestedAnalyses.get(i)) ) {
                performableAnalyses.add( Logics.getInstance().getAnalyzationLogic(requestedAnalyses.get(i)) );
            }
            else {
                analysisRequest.addError(new Error( ErrorType.ANALYSIS_NOT_AVAILABLE, requestedAnalyses.get(i) ) );
            }
                
               

        }
        return performableAnalyses;
    }
    
     /**
     * Creates an analysis task identified by unique randomly generated string
     */
     private static AnalysisTask createAnalysisTask( AnalysisRequest analysisRequest ) {

        List<DataSet> datasets = analysisRequest.getDataSets();
         
        String taskID = RandomStringGenerator.generateRandomString(10);

        TaskDAO taskDAO = DAOFactory.getDAOFactory().getTaskDAO();
        DataSetDAO datasetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        
        //Keep generating new ids until unique is found. 
        while ( taskDAO.taskExists(Configuration.getConfiguration().getDatabaseTempSchemaName(), taskID)) {
            taskID = RandomStringGenerator.generateRandomString(10);
        }
        
        taskDAO.changeTaskState(Configuration.getConfiguration().getDatabaseTempSchemaName(), taskID, TaskState.LOADING_DATASETS);
        
        AnalysisTask analysisTask = new AnalysisTask();
        analysisTask.setTaskID( taskID );

        List<Individual> individuals = new ArrayList<>();
        
        for ( int i = 0; i < datasets.size(); i++ ) {
            Importer dataSetImporter = ImporterFactory.getDatasetImporterFactory().getImporter(datasets.get(i).getFormat());

            if ( dataSetImporter == null ) {
                continue;
            }
               
            try {
                individuals = dataSetImporter.importDataSet( Configuration.getConfiguration().getDatabaseTempSchemaName(), datasets.get(i).getName(), datasets.get(i).getFiles());
            } catch (ImporterException ex) {          
                
                if ( ex.getMessage().equals( ImporterException.INDIVIDUAL_ID_ERROR )) {
                    analysisRequest.addError( new Error(Error.ErrorType.INDIVIDUAL_EXISTS));
                }       
                Logger.getLogger(Analyzer.class ).debug(ex);
                return null;
            }

            individuals = selectIndividuals(analysisRequest.getSamples(), individuals, analysisRequest);
            
            analysisTask.addIndividuals(individuals);
            for ( Individual individual : individuals) {
                taskDAO.addIndividual(Configuration.getConfiguration().getDatabaseTempSchemaName(), taskID, individual.getId());
            }
        }
        if (individuals.isEmpty()) {
                  
            for ( String id : analysisRequest.getSamples()) {
                
                taskDAO.addIndividual(Configuration.getConfiguration().getDatabaseTempSchemaName(), taskID, id);
                Individual individual = datasetDAO.getIndividual(id);
                if ( individual != null) {
                    individuals.add( datasetDAO.getIndividual(id) );         
                }

            }
            analysisTask.addIndividuals(individuals);
        }
        
        return analysisTask;
    }   
     
    private static List<Individual> selectIndividuals( List<String> samples, List<Individual> importedSamples, AnalysisRequest analysisRequest ) {
        
        List<Individual> selectedIndividuals = new ArrayList<Individual>();
        
        //If no samples are specified, use all samples in the dataset, 
        if (samples.isEmpty()) {
            return importedSamples;
        }
        
        for ( int s = 0; s < samples.size(); s++ ) {
            for ( int i = 0; i < importedSamples.size(); i++) {
                
                if ( importedSamples.get(i).getId().equals(samples.get(s))) {
                    selectedIndividuals.add(importedSamples.get(i));
                }     
            }
        }
        return selectedIndividuals;
    }
    
    /**
     * Creates a report based on the finished tasks.
     */
    private static void createReports( AnalysisRequest analysisRequest, AnalysisTask analysisTask ) {

        //Create a Report
        Report report = ReportCreator.createReport( analysisTask, analysisRequest.getName(), analysisRequest.getLanguage() );
        //Pass the report to analysisRequest.
        analysisRequest.addReport( report );    

    }

    
}
