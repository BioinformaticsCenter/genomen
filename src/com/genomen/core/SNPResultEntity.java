package com.genomen.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Presents a result of a single SNP decision rule.
 * @author ciszek
 */
public class SNPResultEntity extends ResultEntity {

    private String effect;
    private String effectType;
    private String traitSymbolicName;
    private double interestLevel;

    private String ruleID;

    private List<String> undefinedGenotypes = new ArrayList<String>();
    private List<String> undefinedPhenotypes = new ArrayList<String>();

    /**
     * Gets the value of the effect caused by this result.
     * @return size of the effect
     */
    public String getEffect() {
        return effect;
    }
    /**
     * Gets the type of the effect caused by this result.
     * @return type of the effect
     */
    public String getEffectType() {
        return effectType;
    }
    /**
     * Gets the name of the trait defined by this result.
     * @return trait name
     */
    public String getTraitSymbolicName() {
        return traitSymbolicName;
    }
    /**
     * Gets the interest level of this result.
     * @return the interest level of this rule.
     */
    public double getInterestLevel() {
        return interestLevel;
    }
    /**
     * Gets the id of the rule that created this result.
     * @return ruleID
     */
    public String getRuleID() {
        return ruleID;
    }

    /**
     * Gets the names of genotypes that could not be found from the genotype data sources during decision making.
     * @return undefined genotypes.
     */
    public List<String> getUndefinedGenotypes() {
        return undefinedGenotypes;
    }
    /**
     * Gets the names of phenotypes that could not be found from the phenotype data sources during decision making.
     * @return undefined phenotypes
     */
    public List<String> getUndefinedPhenotypes() {
        return undefinedPhenotypes;
    }
    /**
     * If one or more genotype or phenotype required for the trait represented by this result could not be found, it is not possible to verify that the trait exists.
     * @return True if one or more genotype or phenotype were found to be undefined during the analysis.
     */
    public boolean isResolved() {

        if( undefinedGenotypes.isEmpty() && undefinedPhenotypes.isEmpty() ) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Constructs a snp result with the given attributes
     * @param p_effect the value of the effect presented by this result
     * @param p_effectType the type of the effect presented by this result
     * @param p_traitSymbolicName the symbolic name of the trait associated with this result
     * @param p_interestLevel the interest level of this result
     * @param p_ruleID the id of the rule that generated this result
     * @param p_undefinedGenotypes a list of genotypes that were undefined during the decision making
     * @param p_undefinedPhenotypes a list of phenotypes that were undefined during the decision making
     */
    public SNPResultEntity( String p_effect, String p_effectType, String p_traitSymbolicName, double p_interestLevel, String p_ruleID, List<String> p_undefinedGenotypes, List<String> p_undefinedPhenotypes ) {

        effect = p_effect;
        effectType = p_effectType;
        traitSymbolicName = p_traitSymbolicName;
        interestLevel = p_interestLevel;
        ruleID = p_ruleID;
        undefinedGenotypes = p_undefinedGenotypes;
        undefinedPhenotypes = p_undefinedPhenotypes;

    }

}
