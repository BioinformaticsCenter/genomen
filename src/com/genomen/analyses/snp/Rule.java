package com.genomen.analyses.snp;


/**
 * Defines a single rule 
 * @author ciszek
 */
public class Rule {

    private String id;
    private String effectType;
    private String traitSymbolicName;
    private int interestLevel;
    private String logic;

    public static final String NUMERIC = "NUMERIC";
    public static final String RISK = "RISK";
    public static final String TEXT = "TEXT";
    
    
    /**
     * Gets the database id of this rule.
     * @return id.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the type of the effect caused by the trait defined by this rule.
     * @return effect type
     */
    public String getEffectType() {
        return effectType;
    }
    /**
     * Gets the symbolic name of the trait defined by this rule.
     * @return trait name
     */
    public String getTraitSymbolicName() {
        return traitSymbolicName;
    }
    /**
     * Gets the interest level of the trait defined by this rule.
     * @return interest level
     */
    public int getInterestLevel() {
        return interestLevel;
    }

    /**
     * Gets the script that contains the actual rule logic
     * @return rule logic
     */
    public String getLogic() {
        return logic;
    }

    /**
     * Constructs a rule with the given attributes.
     * @param p_id the id of this rule
     * @param p_effectType the type of the effect resulting from this rule
     * @param p_traitSymbolicName the symbolic name of the trait associated with this rule
     * @param p_interestLevel the interest level of this rule
     * @param p_logic a script containing this rules decision logic
     */
    public Rule( String p_id, String p_effectType, String p_traitSymbolicName, int p_interestLevel, String p_logic ) {
        id = p_id;
        effectType = p_effectType;
        traitSymbolicName = p_traitSymbolicName;
        interestLevel = p_interestLevel;
        logic = p_logic;
    }



}
