package com.genomen.dao;

import com.genomen.core.Configuration;
import com.genomen.core.Individual;
import com.genomen.entities.DataAttributeConverter;
import com.genomen.entities.DataEntity;
import com.genomen.entities.DataEntityAttributeValue;
import com.genomen.entities.DataType;
import com.genomen.entities.DataTypeManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * DataSetDAO for accessing Derby database.
 * @author ciszek
 */
public class DerbyDataSetDAO extends DerbyDAO implements DataSetDAO {

    public DataEntity getDataEntity(String schemaName, String individualID, String attribute, String dataID, DataType dataType ) {

        Connection connection = null;
        DataEntity dataEntity = null;
        
        String tableName = createTableName(individualID, dataType);
             
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return dataEntity;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schemaName+ "." + tableName + " WHERE " + attribute + " = ?");
            statement.setString( 1, dataID );           
            ResultSet results = statement.executeQuery();

            dataEntity = createDataEntity(results, dataType);

            //Close the connection.
            results.close();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
        
        return dataEntity;

    }
    
    public String createTableName( String taskID, DataType dataType ) {
        
        return dataType.getId().concat("_").concat(taskID);
        
    }
    
    private DataEntity createDataEntity( ResultSet resultSet, DataType dataType ) throws SQLException {
        
        DataEntity dataEntity = null;
        
        HashMap<String, DataEntityAttributeValue> dataEntityAttributes = new HashMap<String, DataEntityAttributeValue>();
        List<String> dataTypeAttributes = dataType.getAttributeNames();
        
        if ( resultSet.next() ) {
       
        
            for ( int i = 0; i < dataTypeAttributes.size(); i++ ) {

                String attributeName = dataTypeAttributes.get(i);
                String attributeType = dataType.getAttributeType(attributeName);

                if ( DataAttributeConverter.sqlTypeToJava(attributeType) == DataAttributeConverter.TEXT ) {
                    String attribute = resultSet.getString(attributeName);
                    dataEntityAttributes.put(attributeName, new DataEntityAttributeValue(attribute));
                }
                if ( DataAttributeConverter.sqlTypeToJava(attributeType) == DataAttributeConverter.INTEGER ) {
                    int attribute = resultSet.getInt(attributeName);
                    dataEntityAttributes.put(attributeName, new DataEntityAttributeValue(attribute));
                    continue;
                }  
                if ( DataAttributeConverter.sqlTypeToJava(attributeType) == DataAttributeConverter.INTEGER ) {
                    double attribute = resultSet.getDouble(attributeName);
                    dataEntityAttributes.put(attributeName, new DataEntityAttributeValue(attribute));
                    continue;
                }       
                if ( DataAttributeConverter.sqlTypeToJava(attributeType) == DataAttributeConverter.BOOLEAN ) {
                    boolean attribute = resultSet.getBoolean(attributeName);
                    dataEntityAttributes.put(attributeName, new DataEntityAttributeValue(attribute));
                    continue;
                }                  

            }
            dataEntity = new DataEntity(dataType, dataEntityAttributes );
        }    
            

        
        return dataEntity;
    }


    @Override
    public void createDataTable(String schemaName, String taskID, DataType dataType) {

        String tableName = createTableName( taskID, dataType );
        StringBuilder valuesBuilder = new StringBuilder();
        List<String> attributeNames = dataType.getAttributeNames();
                   
        for ( int i = 0; i < attributeNames.size(); i++ ) {
            
            String attributeType = dataType.getAttributeType(attributeNames.get(i));
            String attributeName = attributeNames.get(i);
            valuesBuilder.append(attributeName);
            valuesBuilder.append(" ");
            valuesBuilder.append(attributeType);
            
            int attributeSize = dataType.getAttributeSize(attributeName);
            
            if ( attributeSize > 0) {
                valuesBuilder.append( "(");
                valuesBuilder.append( attributeSize );  
                valuesBuilder.append( ")");                
            }
            
            if ( dataType.isRequiredAttribute(attributeName)) {
                valuesBuilder.append( " NOT NULL");
            }
                        
            valuesBuilder.append(", ");            
        }            
        
        valuesBuilder.append( " ID BIGINT NOT NULL,");
         valuesBuilder.append( "PRIMARY KEY (ID) ");
     
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();
        contentDAO.createTable(schemaName, tableName, valuesBuilder.toString());

    }
    
    public void removeIndividuals( List<Individual> individuals ) {
        
        Connection connection = null;
     
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + Configuration.getConfiguration().getDatabaseTempSchemaName() + ".Individuals WHERE INDIVIDUAL_ID = ?");
            
            for ( Individual individual : individuals ) {
                statement.setString( 1, individual.getId() );
                statement.addBatch();
            }

            statement.executeBatch();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
              
    }

    @Override
    public int getCurrentId(String schemaName, String individualID, DataType dataType) {

        Connection connection = null;
        int currentID = -1;
        
        String tableName = createTableName(individualID.toUpperCase(), dataType );
             
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return -1;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(ID) FROM " + Configuration.getConfiguration().getDatabaseTempSchemaName() +"."+tableName);        
            ResultSet results = statement.executeQuery();

            results.next();
            currentID = 1 + results.getInt(1);
                    

            //Close the connection.
            results.close();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
        
        return currentID;
  
        
    }
      

}
