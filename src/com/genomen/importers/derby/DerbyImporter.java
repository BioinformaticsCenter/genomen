package com.genomen.importers.derby;

import com.genomen.core.Configuration;
import com.genomen.dao.ContentDAO;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.DataSetDAO;
import com.genomen.dao.DerbyDAO;
import com.genomen.dao.DerbyDAOFactory;
import com.genomen.dao.TaskDAO;
import com.genomen.entities.DataEntityAttributeValue;
import com.genomen.entities.DataType;
import com.genomen.entities.DataTypeManager;
import com.genomen.importers.ImporterException;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * Base class for importers importing to Derby database
 * @author ciszek
 */
public abstract class DerbyImporter extends DerbyDAO {

    
    public static final int INVALID_ID = -1;

    /**
     * Inserts a list of samples into the sample table.
     * @param names a list of sample names
     * @return <code>true</code> if individuals were successfully inserted, <code>false</code> otherwise.
     */
    protected void insertIndividuals( List<String> names ) throws ImporterException {   
        
        String insertStatement = "INSERT INTO " + Configuration.getConfiguration().getDatabaseTempSchemaName() + ".Individuals ( INDIVIDUAL_ID)  " + " VALUES ( ? )";
              
        Connection connection = null;
        PreparedStatement statement = null;
             
        try {
            connection = DerbyDAOFactory.createConnection();

            statement = connection.prepareStatement(insertStatement);
            
            for ( String name : names) {
                statement.setString(1, name);
                statement.addBatch();
            }
         
            statement.executeBatch();
            statement.close();


        } catch (SQLException exception) {
            Logger.getLogger( DerbyImporter.class ).debug(exception);
            ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
            if ( contentDAO.tableExists(Configuration.getConfiguration().getDatabaseTempSchemaName(), "Individuals") ) {
                throw new ImporterException(ImporterException.INDIVIDUAL_ID_ERROR);     
            }
            else {
             throw new ImporterException(ImporterException.MALFORMED_DATABASE); 
            }


        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger( DerbyImporter.class ).debug(ex);
            throw new ImporterException(ImporterException.CONNECTION_FAILURE);
        }

        finally {
            closeConnection(connection);
        }

    }

    /**
     * Bulk imports a preformed table presentation of a dataset into the database.
     * @param schemaName Name of the schema used for the dataset
     * @param individualID id of the sample to which the inserted dataset is associated
     * @param type Type of the data
     * @param file File that stores the table presentation of the dataset 
     */
    public void bulkImport( String schemaName, String individualID, String type, File file) {
        
        DataSetDAO dataSetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
        DataType dataType = DataTypeManager.getDataType(type);
        String tableName = dataSetDAO.createTableName(individualID.toUpperCase(), dataType );

        
        //If table for this data type has not been already created
        if ( !contentDAO.tableExists(schemaName, tableName)) {
            //Create a new table for this data type.
            dataSetDAO.createDataTable(schemaName, individualID, dataType );               
        }
        
        Connection connection = null;
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger(DerbyImporter.class ).debug(ex);           
            return;
        }

        try {           
            Statement statement = connection.createStatement();
            statement.executeUpdate("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE ('" + schemaName + "', '" + tableName + "', '" + file.getAbsolutePath() + "'" + ",'\t',null,null,0)");
            statement.close();
            connection.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(DerbyImporter.class ).debug(ex);          
        }
        
        createIndexes(schemaName, individualID, dataType);

    }
    
    /**
     * Creates a tab separated tuple that presents a data entity as a single row.
     * @param id An unique ID for the data
     * @param attributes Attributes of a data entity presented as a set of <code>DataEntityAttributeValue</code> instances
     * @return String representation of a data entity
     */
    protected String createTuple( int id, HashMap<String, DataEntityAttributeValue> attributes, String type ) {

        StringBuilder tuple = new StringBuilder();
       
        DataType dataType = DataTypeManager.getDataType(type);
        
        List<String> dataTypeAttributes = dataType.getAttributeNames();
        for ( int i = 0; i < dataTypeAttributes.size(); i++) {
            DataEntityAttributeValue dataTypeAttribute = attributes.get(dataTypeAttributes.get(i));

            String value = dataTypeAttribute.getString();

            tuple.append(value);
            tuple.append("\t");            
        }
        tuple.append(id);

        return tuple.toString();

    }    
    
    protected String findFile( String[] fileArray, String ending ) {
        
        for ( int i = 0; i < fileArray.length; i++ ) {
            String regexp = ".+".concat(ending).concat("$");
            if ( fileArray[i].matches(regexp)) {
                return fileArray[i];
            }
            
        }
        return null;
    }
    
    protected int getCurrentId( String individualID, String type ) {
        
        DataType dataType = DataTypeManager.getDataType(type);
        
        if ( dataType == null ) {
            return INVALID_ID;
        }
        
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
        DataSetDAO datasetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        if ( !contentDAO.tableExists( Configuration.getConfiguration().getDatabaseTempSchemaName(),  datasetDAO.createTableName( individualID,dataType))) {
            return 0;
        }
        

        return datasetDAO.getCurrentId(Configuration.getConfiguration().getDatabaseTempSchemaName(), individualID.toUpperCase(), dataType);
    }

    protected void createIndexes( String schemaName, String sampleID, DataType dataType) {
        
        String tableName = DAOFactory.getDAOFactory().getDataSetDAO().createTableName(sampleID, dataType);
        
        List<String> indexAttributes = new ArrayList<String>();
        
        for ( int i = 0; i < dataType.getAttributeNames().size(); i++) {
            if ( dataType.isRequiredAttribute(dataType.getAttributeNames().get(i) ) ) {
                indexAttributes.add(dataType.getAttributeNames().get(i));                
            }
        }      
        
        String indexStatement = "";
        
        for ( int i = 0; i < indexAttributes.size(); i++) {
            indexStatement = indexStatement + indexAttributes.get(i);
            
            if ( i != indexAttributes.size()-1) {
                indexStatement = indexStatement + ", ";
            }
            
        }     
        
        Connection connection = null;
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger(DerbyImporter.class ).debug(ex);           
            return;
        }

        try {           
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE INDEX " + dataType.getId() + "_index ON " + schemaName + "." + tableName + " ( " + indexStatement + " )" );
            statement.close();
            connection.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(DerbyImporter.class ).debug(ex);          
        }        
        
    }
    
}
