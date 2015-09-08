package com.genomen.dao;

import com.genomen.core.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Factory class for DAOs accessing Derby databases
 * @author ciszek
 */
public class DerbyDAOFactory extends DAOFactory {

    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    /**
     * Gets a new instance of a Derby based RuleDAO
     * @return Derby based RuleDAO
     */
    public RuleDAO getRuleDAO() {

        return new DerbyRuleDAO();
    }
    /**
     * Gets a new instance of a Derby based TraitDAO
     * @return Derby based TraitDAO
     */
    public TraitDAO getTraitDAO() {

        return new DerbyTraitDAO();
    }
    /**
     * Gets a new instance of a Derby based ErrorDAO.
     * @return Derby based ErrorDAO
     */
    public ErrorDAO getErrorDAO() {

        return new DerbyErrorDAO();

    }
    /**
     * Gets a new instance of a Derby based ContentDAO.
     * @return Derby based ContentDAO
     */
    public ContentDAO getContentDAO() {
        return new DerbyContentDAO();
    }
 
    /**
     * Gets a new instance of a Derby based DataSetDAO
     * @return
     */
    public DerbyDataSetDAO getDataSetDAO() {
        return new DerbyDataSetDAO();
    }
    
    /**
     * Gets a new instance of a Derby based TaskDAO
     * @return
     */
    public DerbyTaskDAO getTaskDAO() {
        return new DerbyTaskDAO();
    }

    /**
     * Creates a new properly configured instance of Connection
     * @return An instance of Connection.
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.sql.SQLException
     */
    public static Connection createConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

        Connection connection = null;

        Class.forName(DRIVER).newInstance();
        connection = DriverManager.getConnection("jdbc:derby:" + Configuration.getConfiguration().getDatabaseAddress() + ";create=false");

        return connection;
  }


}
