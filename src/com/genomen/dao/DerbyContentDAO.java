package com.genomen.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Derby based implementation of ContentDAO
 * @author ciszek
 */
public class DerbyContentDAO extends DerbyDAO implements ContentDAO {

    public List<String[]> getTableContents( String schemaName, String tableName) {

        LinkedList<String[]> rows = new LinkedList<String[]>();
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return rows;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schemaName + "." + tableName );
            ResultSet results = statement.executeQuery();

            int columnCount = results.getMetaData().getColumnCount();

            while ( results.next() ) {

                String[] row = new String[columnCount];

                for ( int i = 0; i < columnCount; i++) {
                    row[i] = results.getString(i+1);
                }

                rows.add(row);
            }
            

            results.close();
            statement.close();



        } catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }
        
        return rows;

    }

    public String[] getAttributeNames(String schemaName, String tableName) {

        String[] attributeNames = null;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return attributeNames;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schemaName + "." + tableName );
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();

            attributeNames = new String[columnCount];
            for ( int i = 0; i < columnCount; i++ ) {
                attributeNames[i] = results.getMetaData().getColumnLabel(i+1);
            }

            results.close();
            statement.close();


        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

        return attributeNames;
    }

    public String[] getPrimaryKeys(String schemaName, String tableName) {

        String[] primaryKeys = null;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return primaryKeys;
        }

        try {
            ResultSet results = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);

            ArrayList<String> keys = new ArrayList<String>();

            while( results.next() ) {
                keys.add( results.getString("COLUMN_NAME"));
            }

            //Close the connection.
            results.close();
            connection.close();

            primaryKeys = new String[keys.size()];
            keys.toArray(primaryKeys);

        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

        return primaryKeys;
    }

    public int[] getColumnDisplaySizes( String schemaName, String tableName ) {

        int[] columnLengths = null;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return columnLengths;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schemaName + "." + tableName );
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();

            columnLengths = new int[columnCount];
            for ( int i = 0; i < columnCount; i++ ) {
                columnLengths[i] = results.getMetaData().getColumnDisplaySize(i+1);
            }


            results.close();
            statement.close();



        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

        return columnLengths;

    }

    public String[] getTables(String schemaName) {

        String[] tableNames = null;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return tableNames;
        }

        try {
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet results = metadata.getTables(null, schemaName, null, null );
            ArrayList<String> tableNameList = new ArrayList<String>();

            while ( results.next()) {
                tableNameList.add( results.getString("TABLE_NAME"));
            }
            tableNames = new String[tableNameList.size()];
            tableNameList.toArray(tableNames);


            results.close();


        }
        catch (SQLException ex) {

           Logger.getLogger( DerbyContentDAO.class ).error(ex);
           
        }
        finally {
            closeConnection( connection );
        }

        return tableNames;

    }

    public String[] getAttributeTypeNames( String schemaName, String tableName ) {

        String[] attributeTypes = null;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return attributeTypes;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schemaName + "." + tableName);
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();
            attributeTypes = new String[columnCount];
            for ( int i = 0; i < columnCount; i++ ) {

                attributeTypes[i] = results.getMetaData().getColumnTypeName(i+1);

            }

            results.close();
            statement.close();


        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

        return attributeTypes;

    }

    public int[] getAttributeTypes( String schemaName, String tableName ) {

        int[] attributeTypes = null;
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return attributeTypes;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + schemaName + "." + tableName);
            ResultSet results = statement.executeQuery();
            int columnCount = results.getMetaData().getColumnCount();
            attributeTypes = new int[columnCount];
            for ( int i = 0; i < columnCount; i++ ) {

                attributeTypes[i] = results.getMetaData().getColumnType(i+1);
            }

            results.close();
            statement.close();


        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

        return attributeTypes;

    }

    public void deleteTuple(String schemaName, String tableName, String[] values ) {

        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return;
        }

        try {

            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet primaryKeys = metadata.getPrimaryKeys(null, schemaName, tableName);

            ArrayList<String> keyColumns = new ArrayList<String>();

            while ( primaryKeys.next()) {
                keyColumns.add(primaryKeys.getString("COLUMN_NAME") );
            }

            String[] attributeNames = getAttributeNames( schemaName, tableName );

            ArrayList<String> keyAttributes = new ArrayList<String>();
            ArrayList<String> keyValues = new ArrayList<String>();

            for ( int valueIndex = 0; valueIndex < values.length; valueIndex++ ) {

                boolean isPrimaryKey = false;

                for ( int keyIndex = 0; keyIndex < keyColumns.size(); keyIndex++ ) {

                    if ( keyColumns.get(keyIndex).equals( attributeNames[valueIndex])) {
                        isPrimaryKey = true;
                    }
                }
                if ( isPrimaryKey ) {

                    keyAttributes.add(attributeNames[valueIndex]);
                    keyValues.add(values[valueIndex]);
                }
            }

            String rowDefinition = "";

            for ( int i = 0; i < keyAttributes.size(); i++  ) {

                rowDefinition = rowDefinition + keyAttributes.get(i) + " = " + keyValues.get(i) + " ";

                if ( i < keyAttributes.size()-1 ) {
                    rowDefinition = rowDefinition + " AND ";
                }
                
            }

            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + schemaName + "." + tableName +" WHERE ?");
            statement.setString(1, rowDefinition);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException ex) {

           Logger.getLogger( DerbyContentDAO.class ).error(ex);
           
        }
        finally {
            closeConnection( connection );
        }

    }

    public void addTuple(String schemaName, String tableName, String[] attributes ) {

        String values = "";
        Connection connection = null;

        for ( int i = 0; i < attributes.length; i++ ) {
            values = values + attributes[i];

            if ( i < attributes.length-1 ) {
               values = values + ", ";
            }
        }

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return;
        }

        try {

            Statement statement = connection.createStatement();
            String insertionStatement = "INSERT INTO " + schemaName + "." + tableName + " VALUES ( " + values + ")";
            statement.executeUpdate( insertionStatement );
            statement.close();
        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
           
        }
        finally {
            closeConnection( connection );
        }   

    }

    public void addTuple(String schemaName, String tableName, String[] columNameArray, String[] valueArray ) {

        String columns = "";
        String values = "";
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return;
        }

        for ( int i = 0; i < columNameArray.length; i++ ) {

            columns = columns.concat( columNameArray[i] );
            values = values.concat( valueArray[i] );

            if ( i != columNameArray.length -1 ) {
                columns = columns.concat(", ");
                values = values.concat(", ");
            }

        }

        try {

            Statement statement = connection.createStatement();
            String insertionStatement = "INSERT INTO " + schemaName + "." + tableName + " (" + columns + ") VALUES ( " + values + ")";
            statement.executeUpdate( insertionStatement );
            statement.close();
        }
        catch (SQLException ex) {
           System.out.println(ex);
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }
        
    }

    public String[][] getTablesRefered( String schemaName, String tableName ) {

        List<String[]> tablesRefered = new ArrayList<String[]>();
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return tablesRefered.toArray( new String[0][0]);
        }

        try {
            ResultSet results = connection.getMetaData().getImportedKeys(null, schemaName, tableName);

            while ( results.next() ) {
                String[] reference = new String[2];
                reference[0] = results.getString("PKTABLE_SCHEM");
                reference[1] = results.getString("PKTABLE_NAME");
                tablesRefered.add(reference);
            }
            results.close();
        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

        return tablesRefered.toArray( new String[0][0]);
    }

    public Hashtable<String, String[]> getForeignKeyValues(String schemaName, String tableName, boolean ignorePrimaryKeys ) {

        Hashtable referencetable = new Hashtable();
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return referencetable;
        }

        try {

            ResultSet results = connection.getMetaData().getImportedKeys(null, schemaName, tableName);

            ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
            ArrayList<String> primaryKeyList = new ArrayList<String>();

            while (primaryKeys.next()) {
                primaryKeyList.add(primaryKeys.getString("COLUMN_NAME"));
            }

            PreparedStatement foreignKeyDataStatement = connection.prepareStatement("SELECT ? FROM ?.?");

            while (results.next()) {

                String foreignKeyColumn = results.getString("FKCOLUMN_NAME");
                String primaryKeySchema = results.getString("PKTABLE_SCHEM");
                String primaryKeyTable = results.getString("PKTABLE_NAME");
                String primaryKeyColumn = results.getString("PKCOLUMN_NAME");

                foreignKeyDataStatement.setString(1, primaryKeyColumn.toLowerCase());
                foreignKeyDataStatement.setString(2, primaryKeySchema);
                foreignKeyDataStatement.setString(3, primaryKeyTable);
                ResultSet foreignKeyDataresults = foreignKeyDataStatement.executeQuery();

                ArrayList<String> foreignKeyData = new ArrayList<String>();

                while (foreignKeyDataresults.next()) {
                        foreignKeyData.add( foreignKeyDataresults.getString(1) );
                }

                if ( (ignorePrimaryKeys && !isPrimaryKey(primaryKeyList,foreignKeyColumn)) || !ignorePrimaryKeys) {
                    String[] foreignKeys = new String[foreignKeyData.size()];
                    foreignKeyData.toArray(foreignKeys);
                    referencetable.put(foreignKeyColumn, foreignKeys);
                }
            }

            results.close();

        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

    return referencetable;
    }

    private boolean isPrimaryKey( ArrayList<String> primaryKeyList, String attribute) {

        for ( int i = 0; i < primaryKeyList.size(); i++) {
            if ( primaryKeyList.get(i).equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    public boolean truncate(String schemaName, String tableName) {

        Connection connection = null;
        boolean truncated = true;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM " + schemaName + "." + tableName );
        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
           truncated = false;
        }
        finally {
            closeConnection( connection );
        }

        return truncated;
    }

    public void compressTable(String schemaName, String tableName) {

        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?,?,1)");
            statement.setString(1, schemaName );
            statement.setString(2, tableName );
            statement.executeUpdate();
        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

    }

    public void dropTable( String schemaName, String tableName ) {

        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DROP TABLE " + schemaName +"." + tableName );
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
        }
        finally {
            closeConnection( connection );
        }

    }

    public boolean createTable( String schemaName, String tableName, String values ) {
  
        Connection connection = null;
        boolean success = true;
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return false;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE " + schemaName +"." + tableName + " ( " + values + " )" );
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException ex) {
           Logger.getLogger( DerbyContentDAO.class ).error(ex);
           return false;
        }
        finally {
            closeConnection( connection );
        }
        return success;
    }

    public boolean tableExists(String schemaName, String tableName) {

        boolean isCreated = false;
        String[] tables = getTables(schemaName);

        for ( int i = 0; i < tables.length; i++) {
            if ( tables[i].equals(tableName) ) {
                isCreated = true;
            }
        }

        return isCreated;
    }

}
