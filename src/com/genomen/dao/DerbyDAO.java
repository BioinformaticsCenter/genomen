package com.genomen.dao;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * Base class for DAOs accessing Derby database
 * @author ciszek
 */
public class DerbyDAO {

    /**
     * Convenience method for closing database connections.
     * @param connection
     */
    public void closeConnection( Connection connection ) {

        if ( connection != null ) {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DerbyDAO.class ).debug( ex );
            }
        }

    }

}
