package com.genomen.dao;

import com.genomen.core.TaskState;

/**
 * Interface defining methods for manipulating task related data.
 * @author ciszek
 */
public interface TaskDAO {

    /**
     * Adds a task to a database.
     * @param schema schema name
     * @param taskID task id
     * @return <code>true</code> if the task was added, <code>false</code> otherwise
     */
    public abstract boolean addTask( String schema, String taskID );
    
    /**
     * Changes the state of a task.
     * @param schema schema name
     * @param taskID id of the task
     * @param taskState new state for the task
     * @return  <code>true</code> if the task task state was changed, <code>false</code> otherwise
     */
    public abstract boolean changeTaskState( String schema, String taskID, TaskState taskState );

    /**
     * Removes a task from a database.
     * @param schema schema name
     * @param taskID task id
     * @return <code>true</code> if the task was deleted, <code>false</code> otherwise
     */
    public abstract boolean deleteTask( String schema, String taskID );    

    /**
     * Deletes all table entries associated with the given task.
     * @param schema schema name
     * @param taskID task id
     * @return <code>true</code> if the entries were deleted, <code>false</code> otherwise
     */
    public abstract boolean deleteTaskTables( String schema, String taskID ); 
    
    /**
     * Removes all task related data from the database and drops temporary tables.
     * @param schema schema name
     * @param taskID task id
     * @return <code>true</code> if the deletion succeed, <code>false</code> otherwise
     */
    public abstract boolean deleteAllTaskData( String schema, String taskID );
    
    /**
     * Checks if a task is associated with the given table.
     * @param schema schema name
     * @param taskID task id
     * @param tableName table name
     * @return <code>true</code> if table-task association exists, <code>false</code> otherwise
     */
    public abstract boolean hasDataTable( String schema, String taskID, String tableName ); 
    
    /**
     * Associates a table presenting dataset with a task.
     * @param schema schema name
     * @param taskID task id
     * @param tableName dataset table name
     */
    public abstract void addDataTable( String schema, String taskID, String tableName );
    
    /**
     * Checks if the given task exists currently in the database.
     * @param schemaName schema name
     * @param taskID task id
     * @return <code>true</code> if the task exists, <code>false</code> otherwise
     */
    public abstract boolean taskExists( String schemaName, String taskID );
}
