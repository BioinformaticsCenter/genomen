package com.genomen.dao;

import com.genomen.core.Configuration;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;


/**
 * TraitDAO for Derby databases.
 * @author ciszek
 */
public class DerbyTraitDAO extends DerbyDAO implements TraitDAO {
    /**
     * Performs a query and returns a short description of a trait.
     * @param symbolicName Name of the trait of which short description is queried.
     * @param language Language of the description.
     * @return Short description of a trait.
     */
    public String getShortDescription( String symbolicName, String language ) {

        String shortDescription = "";
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return shortDescription;
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT short_desc FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() + ".trait_description WHERE "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description.trait_symbolic_name_id = '" + symbolicName + "' AND "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description.language_symbolic_name_id = '" + language + "'");
            while ( results.next() ) {
                shortDescription = results.getString("short_desc");
            }
            

            results.close();
            statement.close();


        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
        
        return shortDescription;

    }
    /**
     * Performs a query and returns a long description of a trait.
     * @param symbolicName Name of the trait of which description is queried.
     * @param language Language of the description
     * @return Long description of a trait.
     */
    public String getDetailedDescription( String symbolicName, String language ) {

        String longDescription = "";
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return longDescription;
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT description FROM " + Configuration.getConfiguration().getDatabaseSchemaName() + ".trait_description WHERE "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description.trait_symbolic_name_id = '" + symbolicName + "' AND "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description.language_symbolic_name_id = '" + language + "'");

            while ( results.next() ) {
                longDescription = results.getString("description");
            }


            results.close();
            statement.close();



        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
        
        return longDescription;

    }

    public String getTraitName(String symbolicName, String language) {

        String traitName = "";
        Connection connection = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyContentDAO.class ).debug(ex);
            return traitName;
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT name FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description WHERE "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description.trait_symbolic_name_id = '" + symbolicName + "' AND "+ Configuration.getConfiguration().getDatabaseSchemaName() +".trait_description.language_symbolic_name_id = '" + language + "'");

            while ( results.next() ) {
                traitName = results.getString("name");
            }


            results.close();
            statement.close();



        } catch (SQLException ex) {
            Logger.getLogger( DerbyTraitDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }

        return traitName;
        
    }

}
