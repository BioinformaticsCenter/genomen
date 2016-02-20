
package com.genomen.dao;

import com.genomen.core.Configuration;
import com.genomen.analyses.snp.Rule;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * RuleDAO for Derby database.
 * @author ciszek
 */
public class DerbyRuleDAO extends DerbyDAO implements RuleDAO {


    public ArrayList<Rule> getRules( boolean ignoreSubrules ) {

        Connection connection = null;

        ArrayList<Rule> ruleList = new ArrayList<Rule>();

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
            return ruleList;
        }
        try {

            Statement statement = connection.createStatement();
            String query = "SELECT SYMBOLIC_NAME_ID, trait_symbolic_name_id, effect_type, interest_level, logic  FROM " + Configuration.getConfiguration().getDatabaseSchemaName() +".RULE";

            if ( ignoreSubrules ) {
                query = "SELECT SYMBOLIC_NAME_ID, trait_symbolic_name_id, effect_type, interest_level, logic  FROM " +  Configuration.getConfiguration().getDatabaseSchemaName()  + ".RULE WHERE subresult = 'N'";
            }
            
            
            ResultSet results = statement.executeQuery(query);


            while (results.next() )
            {
                String id = results.getString("SYMBOLIC_NAME_ID");
                String traitSymbolicName = results.getString("trait_symbolic_name_id");
                String effectType = results.getString("effect_type");
                int interestLevel = results.getInt("interest_level");
                String logic = results.getString("logic");

                Rule rule = new Rule( id, effectType, traitSymbolicName, interestLevel, logic  );

                ruleList.add( rule );
            }
            statement.close();
            results.close();

        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }

        return ruleList;

    }

    public Rule getRule( String ruleID ) {

        Connection connection = null;
        Rule rule = null;

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
            return rule;
        }

        try {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT SYMBOLIC_NAME_ID, trait_symbolic_name_id, effect_type, interest_level, logic  FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() +".rule WHERE SYMBOLIC_NAME_ID = '" + ruleID + "'");

            while ( results.next() ) {
                String id = results.getString("SYMBOLIC_NAME_ID");
                String traitSymbolicName = results.getString("trait_symbolic_name_id");
                String effectType = results.getString("effect_type");
                int interestLevel = results.getInt("interest_level");
                String logic = results.getString("logic");

                rule = new Rule( id, effectType, traitSymbolicName, interestLevel, logic  );
            }

            statement.close();
            results.close();

        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }

        return rule;

    }

    public String getRuleLogic( String ruleID ) {

        Connection connection = null;

        String logic = "";
        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
            return logic;
        }
        try {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT logic  FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() +".rule WHERE SYMBOLIC_NAME_ID  = '" + ruleID + "'");


            while (results.next() )
            {
                logic = results.getString("logic");
            }
            statement.close();
            results.close();
        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
        return logic;
    }

    public String getRuleDescription( String ruleID, String languageID ) {

        Connection connection = null;

        String description = "";

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
            return description;
        }

        try {

            PreparedStatement statement = connection.prepareStatement("SELECT description FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() +".rule_description WHERE symbolic_name_id  = ? AND language_symbolic_name_id = ?");

            statement.setString( 1, ruleID );
            statement.setString( 2, languageID );

            ResultSet results = statement.executeQuery();


            while (results.next() )
            {
                description = results.getString("description");
            }
            statement.close();
            results.close();
        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }
        
        return description;

    }

    public String getResultDescription(String resultID, String languageID) {
        
        Connection connection = null;

        String description = "";

        try {
            connection = DerbyDAOFactory.createConnection();
        }
        catch (Exception ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
            return description;
        }

        try {

            PreparedStatement statement = connection.prepareStatement("SELECT description FROM "+ Configuration.getConfiguration().getDatabaseSchemaName() +".result_description WHERE symbolic_name_id  = ? AND language_symbolic_name_id = ?");

            statement.setString( 1, resultID );
            statement.setString( 2, languageID );

            ResultSet results = statement.executeQuery();


            while (results.next() )
            {
                description = results.getString("description");
            }
            statement.close();
            results.close();
        }
        catch (SQLException ex) {
            Logger.getLogger( DerbyRuleDAO.class ).debug(ex);
        }
        finally {
            closeConnection( connection );
        }

        return description;

    }
}
