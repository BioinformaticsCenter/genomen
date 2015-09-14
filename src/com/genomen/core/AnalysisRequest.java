package com.genomen.core;

import com.genomen.reporter.Report;
import com.genomen.reporter.ReportFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Contains information about the requested analyses, data which are to be analyzed and the current state of the analysis.
 * @author ciszek
 */
public class AnalysisRequest extends Observable {

    private TaskState state = TaskState.INITIALIZED;
    
    private ArrayList<Report> reports = new ArrayList<Report>();

    //List containing the class names of the required analyses.
    private ArrayList<String> requiredAnalyses = new ArrayList<String>();
    
    //List containing the report formats requested.
    private ArrayList<String> requiredFormats = new ArrayList<String>();    
    
    //Paths of files to containing data to be analyzed.
    private ArrayList<DataSet> datasets = new ArrayList<DataSet>();
    //Required name of the report
    private String name = "";
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
     * Returns a report from a specified index.
     * @param index index of the required report.
     * @return <code>Report</code> from the specified index or null if the index is invalid.
     */
    public Report getReport( int index ) {

        if ( index < reports.size() && index >= 0 && reports.size() > 0 ) {
            return reports.get(index);
        }
        else {
            return null;
        }

    }
    /**
     * Adds a <code>Report</code> to this request
     * @param p_report report to be added.
     */
    public void addReport( Report p_report ) {
        reports.add(p_report);
    }
    /**
     * Returns the total amount of reports in this request.
     * @return the total amount of reports.
     */
    public int getTotalReports() {
        return reports.size();
    }
    /**
     * Gets an error from a specified index.
     * @param index index of the required Error.
     * @return error from the specified index, or null if the index is invalid.
     */
    public Error getError( int index) {
        if ( index < errors.size() && index >= 0 && errors.size() > 0 ) {
            return errors.get(index);
        }
        else {
            return null;
        }
    }
    /**
     * Adds an <code>Error</code> to the this request
     * @param errorEntity
     */
    public void addError( Error errorEntity ) {
        errors.add(errorEntity);
    }
    /**
     * Returns the total amount of errors listed.
     * @return the total amount of errors.
     */
    public int getTotalErrors() {
        return errors.size();
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
     * Constructs an analysis request from given datasets, analyses and language report language id.
     * @param p_datasets the datasets used in this analysis
     * @param p_requiredAnalyses the analyses to be performed
     * @param p_language the language used in the report
     * @param p_formats a list of report formats requested.
     */
    public AnalysisRequest( List<DataSet> p_datasets, List<String> p_requiredAnalyses, String p_language, List<String> p_formats ) {
        datasets.addAll(p_datasets);
        requiredAnalyses.addAll(p_requiredAnalyses);
        requiredFormats.addAll(p_formats);
        language = p_language;
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


}
