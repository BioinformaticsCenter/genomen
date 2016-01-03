package com.genomen.core;

import com.genomen.reporter.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Contains information about the requested analyses, data which are to be analyzed and the current state of the analysis.
 * @author ciszek
 */
public class AnalysisRequest extends Observable {

    private TaskState state = TaskState.INITIALIZED;
    
    private boolean persistDatasets = false;
    
    private ArrayList<Report> reports = new ArrayList<Report>();

    //List containing the class names of the required analyses.
    private ArrayList<String> requiredAnalyses = new ArrayList<String>();
    
    //List containing the report formats requested.
    private ArrayList<String> requiredFormats = new ArrayList<String>();    
    
    //Paths of files to containing data to be analyzed.
    private ArrayList<DataSet> datasets = new ArrayList<DataSet>();
    
    //Identifiers for the samples that are to be analysed
    private ArrayList<String> samples = new ArrayList<String>();
    
    //Required name of the report
    private String name = "";
    //Path for the output
    private String outputPath = "";    
    //Required language of the report.
    private String language = null;
    //Is the analysis finished
    private boolean finished = false;
    //Is the analysis cancelled
    private boolean cancel = false;
    
    /**The list of errors that occured during the analysis. The list contains errors related
    *to all analyses requested in a general level. Errors occured during the analysis that do not
    *terminate the analysis are listed within reports produced by analyses
    **/
    private ArrayList<Error> errors = new ArrayList<Error>();

    /**
     * Adds a <code>Report</code> to this request
     * @param p_report report to be added.
     */
    public void addReport( Report p_report ) {
        reports.add(p_report);
    }
    /**
     * Returns the reports included in this request
     * @return the reports included
     */
    public List<Report> getReports() {
        return reports;
    }

    /**
     * Adds an <code>Error</code> to the this request
     * @param errorEntity
     */
    public void addError( Error errorEntity ) {
        errors.add(errorEntity);
    }
    /**
     * Returns the errors that occurred during the analysis.
     * @return list of errors
     */
    public List<Error> getErrors() {
        return errors;
    }
    /**
     * Returns the total amount of analyses required in this request.
     * @return the total amount of required analyses.
     */
    public int getTotalAnalyses() {
        return requiredAnalyses.size();
    }

    /**
     * Returns an list containing the class names of all analyses required.
     * @return list of class names.
     */
    public List<String> getRequiredAnalyses() {
        return requiredAnalyses;
    }
    
    /**
     * Returns an list containing the report formats requested
     * @return list of report formats
     */
    public List<String> getRequiredFormats() {
        return requiredFormats;
    }    
    
    /**
     * Returns an list of individuals listed in this request.
     * @return a list of individuals.
     */
    public List<DataSet> getDataSets() {
        return datasets;
    }
   
    
    /**
     * Adds a dataset to this request
     * @param p_dataSet dataset to be added.
     */
    public void addDataSet( DataSet p_dataSet ) {
        datasets.add(p_dataSet);
    }
    /**
     * Returns the language in which the results of the analysis are to be reported.
     * @return id of a language.
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * Sets the language in which the results of the analysis are to be reported.
     * @param p_language id of a language.
     */
    public void setLanguage( String p_language ) {
        language = p_language;
    }
    
    /**
     * Return the name given to this analysis run.
     * @return name of the analysis run
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this analysis run.
     * @param p_name
     */
    public void setName( String p_name) {
        name = p_name;
    }
    
    /**
     * Return the output path of the analysis
     * @return output path
     */
    public String getPath() {
        return outputPath;
    }
    
    /**
     * Sets the output path of the analysis
     * @param path output path
     */
    public void setOutputPath( String path)  {
        outputPath = path;
    }    

    /**
     * Constructs an analysis request from given datasets, analyses and language report language id.
     * @param p_datasets the datasets used in this analysis
     * @param p_requiredAnalyses the analyses to be performed
     * @param samples IDs of samples that are to be analysed
     * @param p_language the language used in the report
     * @param p_formats a list of report formats requested.
     */
    public AnalysisRequest( List<DataSet> p_datasets, List<String> p_requiredAnalyses, List<String> samples, String p_language, List<String> p_formats ) {
        this.datasets.addAll(p_datasets);
        this.requiredAnalyses.addAll(p_requiredAnalyses);
        this.requiredFormats.addAll(p_formats);
        this.samples.addAll(samples);
        this.language = p_language;
    }

    /**
     *  Constructs an analysis request from given dataset, analyses and language report language id.
     * @param p_dataset the datasets used in this analysis
     * @param p_requiredAnalyses the analyses to be performed
     * @param p_language the language used in the report
     */
    public AnalysisRequest( DataSet p_dataset, List<String> p_requiredAnalyses, String p_language, List<String> p_formats ) {
        datasets.add(p_dataset);
        requiredAnalyses.addAll(p_requiredAnalyses);
        requiredFormats.addAll(p_formats);
        language = p_language;
    }
    
    /**
     * Is the analysis finished?
     * @return <code>true</code> if the analysis is finished, otherwise <code>false</code>
     */
    public boolean isFinished() {

        if ( state == TaskState.FINISHED ) {
            return true;
        }
        return false;
        
    }
    /**
     * Is the analysis cancelled?
     * @return <code>true</code> if the analysis is cancelled. otherwise <code>false</code>
     */
    public boolean isCancelled() {
        return cancel;
    }
    
    /**
     * Cancels the analysis.
     */
    public void cancel() {
        cancel = true;
    }

    /**
     *Increments the progress counter and notifies UI.
     */
        public void doProgress() {
        setChanged();
        notifyObservers();
    }

    /**
     * Notifies the UI about the change of state
     * @param p_state the new state
     */
        public void changeState( TaskState p_state ) {
        state = p_state;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets the current state of this analysis
     * @return analysis state
     */
    public TaskState getState() {
        return state;
    }

    /**Will imported datasets be left to the database after analysis.
     * @return <code>true</code> if the datasets are to remain in the database, <code>false</code> otherwise.
     */
    public boolean isPersistDatasets() {
        return persistDatasets;
    }

    /**Sets the task to persist the imported datasets
     * @param persistDatasets Are datasets allowed to remain in the database after analysis.
     */
    public void setPersistDatasets(boolean persistDatasets) {
        this.persistDatasets = persistDatasets;
    }

    /**Gets the IDs of the samples that are to be analysed.
     * @return the sample IDs
     */
    public ArrayList<String> getSamples() {
        return samples;
    }

}
