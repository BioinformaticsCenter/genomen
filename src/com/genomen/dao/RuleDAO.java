package com.genomen.dao;

import com.genomen.core.Rule;
import java.util.List;

/**
 * Interface defining methods for reading rule data from a database.
 * @author ciszek
 */
public interface RuleDAO {

    /**
     * Gets all available rule sets from a database. Each rule set and associated rules are stored in an instance of <code>Rule</code>.
     * @param ignoreSubrules <code>true</code> if sub rules are to be fetched, <code>false</code> otherwise.
     * @return list of available rule sets
     */    
    public abstract List<Rule> getRules( boolean ignoreSubrules );
    
    /**
     * Gets a rule from the database
     * @param ruleID id of the rule
     * @return a rule
     */    
    public abstract Rule getRule( String ruleID );
    
    /**
     * Gets the logic of a rule associated with the given trait.
     * @param ruleID Symbolic name of a trait
     * @return a logic script
     */    
    public abstract String getRuleLogic( String ruleID );
    
    /**
     * Gets the description of a rule.
     * @param ruleID rule id
     * @param languageID language id
     * @return description of a rule
     */    
    public abstract String getRuleDescription( String ruleID, String languageID );
    
    /**
     * Gets the description of a result
     * @param resultID result id
     * @param languageID language id
     * @return result description
     */    
    public abstract String getResultDescription( String resultID, String languageID );
}
