package com.genomen.core;

/**
 * Presents different states of an analysis task.
 * @author ciszek
 */
public enum TaskState {

    INITIALIZED(0),
    LOADING_DATASETS(1),
    PERFORMING_ANALYSIS(2),
    CREATING_REPORTS(3),
    CLEARING_DATA(4),
    FINISHED(5);

    private int state;

    /**
     * Gets the numeric presentation of this state
     * @return a numeric presentation of this state
     */
    public int getState() {
        return state;
    }

    TaskState( int p_state ) {
        state = p_state;
    }
}
