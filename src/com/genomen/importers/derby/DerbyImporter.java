package com.genomen.importers.derby;

import com.genomen.core.Configuration;
import com.genomen.dao.DAOFactory;
import com.genomen.dao.DataSetDAO;
import com.genomen.dao.DerbyDAOFactory;
import com.genomen.dao.TaskDAO;
import com.genomen.entities.DataEntityAttributeValue;
import com.genomen.entities.DataType;
import com.genomen.entities.DataTypeManager;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Base class for importers importing to Derby database
 * @author ciszek
 */
public abstract class DerbyImporter {

    private String type = "";

    /*
     * Importer for the data type given as a parameter
     * @param p_type type code for imported data.
     */
    public DerbyImporter( String p_type  ) {
        type = p_type;
    }

    
    public String getType() {
        return type;
    }

    public void bulkImport( String schemaName, String taskID, String type, File file) {
        
        TaskDAO taskDAO = DAOFactory.getDAOFactory().getTaskDAO();
        DataSetDAO dataSetDAO = DAOFactory.getDAOFactory().getDataSetDAO();
        DataType dataType = DataTypeManager.getDataType(getType());
        String tableName = dataSetDAO.createTableName(taskID, DataTypeManager.getDataType(getType()) );

        //If table for this data type has not been already created
        if ( !taskDAO.hasDataTable(schemaName, taskID, tableName)) {
            //Create a new table for this data type.
            taskDAO.addDataTable(schemaName, taskID, tableName );
            dataSetDAO.createDataTable(schemaName, taskID, dataType );               
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

    }
    
    protected String createTuple( String individualID, HashMap<String, DataEntityAttributeValue> attributes ) {

        StringBuilder tuple = new StringBuilder();
        tuple.append(individualID);
        tuple.append("\t");        
        DataType dataType = DataTypeManager.getDataType(type);
        
        List<String> dataTypeAttributes = dataType.getAttributeNames();
        for ( int i = 0; i < dataTypeAttributes.size(); i++) {
            DataEntityAttributeValue dataTypeAttribute = attributes.get(dataTypeAttributes.get(i));

            String value = dataTypeAttribute.getString();

            tuple.append(value);
            tuple.append("\t");            
        }
        
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

}
