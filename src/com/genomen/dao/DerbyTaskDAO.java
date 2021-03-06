package com.genomen.dao;

import com.genomen.core.Configuration;
import com.genomen.core.Sample;
import com.genomen.core.TaskState;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * TaskDAO for Derby databases.
 * @author ciszek
 */
public class DerbyTaskDAO extends DerbyDAO implements TaskDAO {

    @Override
    public boolean addTask( String schemaName, String taskID) {

        boolean success = true;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + schemaName + ".Task VALUES (?,?)");
            statement.setString(1, taskID);
            statement.setInt(2, TaskState.INITIALIZED.getState() );
            statement.executeUpdate();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            success = false;
        }
        finally {
            closeConnection( connection );
        }

        return success;
    }

    @Override
    public boolean changeTaskState( String schemaName, String taskID, TaskState taskState ) {
        
        boolean success = true;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + schemaName + ".Task SET STATE = ? WHERE TASK_ID = ?");
            statement.setInt(1, taskState.getState() );
            statement.setString(2, taskID);            
            statement.executeUpdate();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            success = false;
        }
        finally {
            closeConnection( connection );
        }

        return success;
    }
    
    
    @Override
    public boolean deleteTask( String schemaName, String taskID ) {
        boolean success = true;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + schemaName + ".Task WHERE TASK_ID = ?");
            statement.setString(1, taskID);
            statement.executeUpdate();
            statement.close();

        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            success = false;
        }
        finally {
            closeConnection( connection );
        }

        return success;        
    }
    
    @Override
    public boolean deleteTaskIndividuals( String schemaName, String individualID ) {
        boolean success = true;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + schemaName + ".Task_Individuals WHERE TASK_ID = ?");
            statement.setString(1, individualID);
            statement.executeUpdate();
            statement.close();

        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            success = false;
        }
        finally {
            closeConnection( connection );
        }

        return success;        
    }    

    @Override
    public boolean deleteAllTaskData( String schemaName, String taskID ) {
        
        boolean success = true;
        
        dropAllTaskTables(schemaName, taskID);
        deleteTaskIndividuals(schemaName, taskID);    
        deleteTask(schemaName, taskID);   
        
        return success;
        
    }
    
    private boolean dropAllTaskTables( String schemaName, String taskID ) {

        boolean success = true;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }


        try {
            PreparedStatement statement = connection.prepareStatement("SELECT INDIVIDUAL_ID FROM " + schemaName + ".Task_Individuals WHERE TASK_ID = ?" );
            statement.setString(1, taskID);
            ResultSet resultSet = statement.executeQuery();

            DataSetDAO datasetDAO = DAOFactory.getDAOFactory().getDataSetDAO();

            List<String> individuals = new ArrayList<String>();
            
            while ( resultSet.next() ) {
                individuals.add( resultSet.getString("INDIVIDUAL_ID"));
            }
            datasetDAO.removeSamples(individuals);

            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }
        finally {
            closeConnection( connection );
        }

        return success;
    }

    public boolean hasIndividual( String schemaName, String taskID, String tableName ) {

        boolean hasTable = false;
     
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }


        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS rowcount FROM " + schemaName + ".Task_Individuals WHERE TASK_ID = ? AND INDIVIDUAL_ID = ?" );
            statement.setString(1, taskID);
            statement.setString(2, tableName);            
            ResultSet resultSet = statement.executeQuery();
            
            resultSet.next();
            
            int rows = resultSet.getInt("rowcount");
            
            if ( rows != 0) {
                hasTable = true;
            }

            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            System.out.println("TaskDAO " + ex);
            return false;
        }
        finally {
            closeConnection( connection );
        }        
        
        return hasTable;
        
    }

    public void addIndividual( String schemaName, String taskID, String individualID ) {

        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + schemaName + ".Task_Individuals VALUES (?,?)");
            statement.setString(1, taskID);
            statement.setString(2, individualID );
            statement.executeUpdate();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }

    }

    public boolean taskExists(String schemaName, String taskID) {
        
        boolean taskExists = false;
     
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            return false;
        }


        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS rowcount FROM " + schemaName + ".Task WHERE TASK_ID = ?" );
            statement.setString(1, taskID);       
            ResultSet resultSet = statement.executeQuery();
            
            resultSet.next();
            
            int rows = resultSet.getInt("rowcount");
            
            if ( rows != 0) {
                taskExists = true;
            }

            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTaskDAO.class ).debug(ex);
            System.out.println("TaskDAO " + ex);
            return false;
        }
        finally {
            closeConnection( connection );
        }        
        
        return taskExists;
    }

}
