package com.genomen.dao;

import com.genomen.core.Configuration;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * ErrorDAO for accessing Derby database.
 * @author ciszek
 */
public class DerbyErrorDAO extends DerbyDAO implements ErrorDAO {
    /**
     * Returns the error message of a specified error.
     * @param id Error id
     * @param language Required language of the error message.
     * @return Error message.
     */
    public String getMessage( int id, String language ) {

        String message = "";
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return message;
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT message FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() +".error_message WHERE id ='" + id +"' AND language = '" + language + "'");
            while ( results.next() ) {
                message = results.getString("message");
            }

            //Close the connection.
            results.close();
            statement.close();
            connection.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }

        return message;
    }

}
