package com.genomen.core;

import com.genomen.core.Error.ErrorType;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Manages analysis queues and executes analyses
 * @author ciszek
 */
public class AnalysisExecutor {
    private static final int MAX_CONCURRENT_ANALYSES_PER_TASK = Configuration.getConfiguration().getMaxConcurrentAnalysesPerTask();
    private static final int MAX_CONCURRENT_INDIVIDUALS_PER_TASK = Configuration.getConfiguration().getMaxConcurrentIndividualsPerTask();
    private static final int MAX_CONCURRENT_ANALYSES = Configuration.getConfiguration().getMaxConcurrentAnalyses();
    private static final int MAX_CONCURRENT_REQUESTS = Configuration.getConfiguration().getMaxConcurrentRequests();
    private static final int MAX_QUEUE_TIME = Configuration.getConfiguration().getMaxQueueTime();

    //A list of analyzation tasks currently in progress or waiting to be started.
    private static final LinkedBlockingQueue<Runnable> analysisQueue = new LinkedBlockingQueue<Runnable>();

    private static final ExecutorService analysisExecutorService = Executors.newFixedThreadPool(MAX_CONCURRENT_ANALYSES);
    private static final ThreadPoolExecutor requestQueueThreadPoolExecutor = new ThreadPoolExecutor(MAX_CONCURRENT_REQUESTS, MAX_CONCURRENT_REQUESTS, MAX_QUEUE_TIME, TimeUnit.MINUTES, analysisQueue);

    private static boolean running = false;

    /**
     * Returns the maximum number of analyzes allowed to be performed parallel per task.
     * @return maximum number of analyzes allowed per task.
     */
    public static int getMaxConcurrentAnalysesPerTask() {
        return MAX_CONCURRENT_ANALYSES_PER_TASK;
    }
    /**
     * Returns  the maximum number of individuals allowed to be processed simultaneously per task.
     * @return maximum number of individuals processed per task.
     */
    public static int getMaxConcurrentIndividualsPerTask() {
        return MAX_CONCURRENT_INDIVIDUALS_PER_TASK;
    }

    /**
     * Returns the current length of the analysis queue.
     * @return the size of the queue
     */
    public static int getQueueLength() {
        return analysisQueue.size();
    }

    /**
     * Submits an analysis request.
     * @param analysisRequest <code>AnalysisRequest</code> containing necessary information for the analysis.
     */
    public static void requestAnalysis( AnalysisRequest analysisRequest ) {

        if ( !running) {
            analysisRequest.addError(new Error( ErrorType.CORE_SHUTDOWN ) );
            return;
        }

        //If the queue thread pool is not shut down, add the requested analysis to the queue.
        if ( !requestQueueThreadPoolExecutor.isShutdown() ) {
            //Create a new runanble to wait in queue while the thread submitting the request continues running.
            AnalysisQueueRunnable analysisQueueRunnable = new AnalysisQueueRunnable( analysisRequest );
            //Add the request to the queu
            requestQueueThreadPoolExecutor.execute(analysisQueueRunnable);
        }
        else {
            analysisRequest.addError(new Error( ErrorType.CORE_SHUTDOWN ) );
        }
    }


    /**
     *Starts up the executor and enables analysis requests to be submitted.
     */
    public static void start() {

        running = true;
        Executors.newFixedThreadPool(MAX_CONCURRENT_ANALYSES);

    }
   
    /**
     * Shuts down the analyzer.
     */
    public static void shutDown() {

        running = false;

        List<Runnable> queue = requestQueueThreadPoolExecutor.shutdownNow();
        //Loop through the list of pending analyses.
        for ( int i = 0; i < queue.size(); i++ ) {
            AnalysisRequest analysisRequest = ((AnalysisQueueRunnable)queue.get(i)).getAnalysisRequest();
            analysisRequest.addError( new Error( ErrorType.CORE_SHUTDOWN) );
            analysisRequest.cancel();
        }
        analysisExecutorService.shutdown();

    }
 
}
