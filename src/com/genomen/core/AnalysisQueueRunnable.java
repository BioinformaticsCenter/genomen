package com.genomen.core;

/**
 * Waits in the queue until run by AnalysisExecutor
 * @author ciszek
 */
public class AnalysisQueueRunnable implements Runnable {


    private final AnalysisRequest analysisRequest;

    /**
     * Gets the queued analysis request 
     * @return analysis request
     */
    public AnalysisRequest getAnalysisRequest() {
        return analysisRequest;
    }

    /**
     * Constructs a runnable for queuing the analysis request given
     * @param p_analysisRequest analysis request to be queued
     */
    public AnalysisQueueRunnable( AnalysisRequest p_analysisRequest ) {
        analysisRequest = p_analysisRequest;
    }


    public void run() {
        //Starts an analysis.
        Analyzer.analyze(analysisRequest);
    }

}