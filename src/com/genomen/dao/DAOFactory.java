package com.genomen.dao;

import com.genomen.core.Configuration;

/**
 * Factory class for DAOs
 * @author ciszek
 */

public abstract class DAOFactory {

    public static final int DERBY = 1;

    public abstract RuleDAO getRuleDAO();
    public abstract TraitDAO getTraitDAO();
    public abstract ErrorDAO getErrorDAO();
    public abstract ContentDAO getContentDAO();
    public abstract DataSetDAO getDataSetDAO();
    public abstract TaskDAO getTaskDAO();
    
    /**
     * Returns DAO factory class of the specified type.
     * @param factoryID Type of the DAO required.
     * @return Data access object factory.
     */
    public static DAOFactory getDAOFactory( int factoryID ) {

        switch (factoryID) {

            case DERBY:
                return new DerbyDAOFactory();
            default:
                return null;
        }
    }
    
    /**
     * Returns DAO factory class of a type specified in the configuration file.
     * @return DAO factory class.
     */
    public static DAOFactory getDAOFactory() {

        int dbID = Configuration.getConfiguration().getDBType();

        switch (dbID) {

            case DERBY:
                return new DerbyDAOFactory();
            default:
                return null;
        }
    }
    


}