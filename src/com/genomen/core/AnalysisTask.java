package com.genomen.core;


import com.genomen.entities.DataEntity;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.DataSetDAO;
import com.genomen.dao.TaskDAO;
import com.genomen.entities.DataType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides access to data related to a specific analysis process. Holds both the raw source data and results of analyzes.
 * @author ciszek
 */
public class AnalysisTask {

    private String taskID;

    private HashMap<String, Sample> sampleMap = new HashMap<String, Sample>();

    //Two maps used to map the results of the analysis.
    private HashMap<String, HashMap<String, Results>> individualResultsMap = new HashMap<String, HashMap<String, Results>>();
    private HashMap<String, Results> resultsMap = new HashMap<String, Results>();

    /**
     * Adds samples to this task.
     * @param samples list of individuals
     */
    public void addSamples( List<Sample> samples) {

        for ( int i = 0; i < samples.size(); i++ ) {
            sampleMap.put( samples.get(i).getId(), samples.get(i ));
        }

    }

    /**
     * Gets the identifier of this task.
     * @return id of this task
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * Sets the identifier for this task.
     * @param p_taskID id for this task
     */
    public void setTaskID( String p_taskID ) {
        taskID = p_taskID;
    }

    /**
     * Adds a single sample to this task.
     * @param sample a sample
     */
    public void addIndividual( Sample sample ) {
        sampleMap.put(sample.getId(), sample);
    }

    /**
     * Gets all the samples associated with this task.
     * @return list of individuals associated with this task
     */
    public List<Sample> getSamples() {
        return new LinkedList<Sample>( sampleMap.values());
    }   

    /**
     * Gets a sample identified with the id given as a parameter
     * @param id id of the sample
     * @return a sample identified by the given id
     */
    public Sample getIndividual( String id ) {
        return sampleMap.get(id);
    }

    /**
     * Gets the data of a specific type with the id given as parameter associated with an individual identified with the id given as a parameter.
     * @param dataType the type of data
     * @param individualID id of an individual
     * @param dataID id of the data
     * @return data that matches the definition
     */
    public DataEntity getData( DataType dataType, String individualID, String attribute, String dataID ) {

        DataSetDAO dataSetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        return dataSetDAO.getDataEntity( Configuration.getConfiguration().getDatabaseTempSchemaName(), individualID, attribute, dataID, dataType);
    }

    /**
     *Removes all data related to this analysis.
     */
    public void clearData() {
        TaskDAO taskDAO = DAOFactory.getDAOFactory().getTaskDAO();
        taskDAO.deleteAllTaskData( Configuration.getConfiguration().getDatabaseTempSchemaName(),taskID);
    }
    
    public void removeTask() {
        
        TaskDAO taskDAO = DAOFactory.getDAOFactory().getTaskDAO();
        taskDAO.deleteTaskIndividuals(Configuration.getConfiguration().getDatabaseTempSchemaName(), taskID);
        taskDAO.deleteTask( Configuration.getConfiguration().getDatabaseTempSchemaName(), taskID);
    
    }

    /**
     * Associates a result concerning individual given as a parameter with this analysis task.
     * @param individualId individual associated with the result
     * @param logicId id of the analysis logic that produced this result
     * @param results the result to be added
     */
    public synchronized void addResults( String individualId, String logicId, Results results ) {

        if ( !individualResultsMap.containsKey(individualId)) {
            individualResultsMap.put( individualId, new HashMap<String, Results>());
        }

        HashMap<String, Results> targetMap = individualResultsMap.get( individualId );

        targetMap.put(logicId, results);

    }

    /**
     * Associates a result not specific to a certain individual with this analysis task.
     * @param logicId id of the analysis logic that produced this result
     * @param results the result to be added
     */
    public synchronized void addResults( String logicId, Results results ) {

        if ( !resultsMap.containsKey(logicId)) {
            resultsMap.put( logicId, results );
        }

        resultsMap.put(logicId, results);

    }

    /**
     * Gets the results associated with the individual id and logic id given as the parameters. 
     * @param individualId id of the individual
     * @param logicId id of the logic
     * @return results matching the identifiers
     */
    public Results getResults( String individualId, String logicId ) {

        if ( !sampleMap.containsKey( individualId ) || !individualResultsMap.get( individualId ).containsKey(logicId) ) {
            return new Results(logicId, false);
        }

        return individualResultsMap.get(individualId).get(logicId);
    }

    /**
     * Gets the results produced by the logic with the id given as a parameter
     * @param logicId id of the logic
     * @return results produced by the specified logic
     */
    public Results getResults( String logicId) {

        if ( !resultsMap.containsKey( logicId ) ) {
            return new Results(logicId, false);
        }
        return resultsMap.get(logicId);
    }

    /**
     * Gets all the results associated with the individual id given as a parameter
     * @param individualId id of an individual
     * @return results associated with the specified individual
     */
    public List<Results> getResultsList( String individualId ){

        if ( !individualResultsMap.containsKey(individualId) ) {
            return new LinkedList<Results>();
        }
        return new LinkedList( individualResultsMap.get( individualId).values());

    }

}

