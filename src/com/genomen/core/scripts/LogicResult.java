
package com.genomen.core.scripts;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of an applied rule.
 * @author ciszek
 */
public class LogicResult {

    public static final int NUMERIC = 0;
    public static final int BOOLEAN = 1;
    public static final int TEXT = 2;
    public static final int ALLELE = 3;

    private ArrayList<String> missingGenotypes = new ArrayList<String>();
    private ArrayList<String> missingPhenotypes = new ArrayList<String>();
    private ArrayList<LogicResult> associatedResults = new ArrayList<LogicResult>();

    private static final double UNRESOLVED_NONZERO = 1;
    private static final double UNRESOLVED_ZERO = 0;

    private boolean result;
    private String value;
    private int type;
    private String note = "";
    private double interestLevel = 0;
    private boolean unresolvable = false;

    /**
     * Returns a list containing the names of missing genotypes.
     * @return list of missing genotypes.
     */
    public List<String> getMissingGenotypes() {
        return missingGenotypes;
    }
    
    /**
     * Returns a list containing the names of missing phenotypes.
     * @return list of missing phenotypes.
     */
    public ArrayList<String> getMissingPhenotypes() {
        return missingPhenotypes;
    }
    /**
     * Adds the name of a missing genotype to the list of missing genotypes.
     * @param genotype Name of the missing genotype.
     */
    public void addMissingGenotype( String genotype ) {
        missingGenotypes.add(genotype);
    }
    /**
     * Adds the name of a missing genotype to the list of missing phenotypes.
     * @param phenotype Name of the missing phenotype.
     */
    public void addMissingPhenotype( String phenotype ) {
        missingPhenotypes.add(phenotype);
    }
    /**
     * Adds the contents of a ArrayList of missing genotypes to the list of missing genotypes.
     * @param genotypes ArrayList of missing genotypes.
     */
    public void addMissingGenotypes( List<String> genotypes ) {
        missingGenotypes.addAll(genotypes);
    }
    /**
     * Adds the contents of a ArrayList of missing phenotypes to the list of missing phenotypes.
     * @param phenotypes ArrayList of missing phenotypes.
     */
    public void addMissingPhenotypes( List<String> phenotypes ) {
        missingPhenotypes.addAll(phenotypes);
    }
    /**
     * Sets the result of the comparison.
     * @param p_result Result of the comparison.
     */
    public void setResult( boolean p_result) {
        result = p_result;
    }
    /**
     * Returns the result of the comparison.
     * @return Result of the comparison.
     */
    public boolean getResult() {
        return result;
    }
    /**
     * Returns the list of results that have been previously associated with
     * this result.
     * @return List of associated results.
     */
    public List<LogicResult> getAssociatedResults() {
        return associatedResults;
    }

    /**
     * Sets the result type
     * @param i Result type.
     */
    public void setType( int i ) {
        type = i;
    }
    /**
     * Returns the type of the result.
     * @return Result type.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the value of the result.
     * @param p_value Value of the result.
     */
    public void setValue( String p_value ) {
        value = p_value;
    }
    /**
     * Sets the value of the result.
     * @param p_value Value of the result.
     */
    public void setValue( double p_value ) {
        value = String.valueOf(p_value);
    }
    
    /**
     * Returns the  value of the result.
     * @return Value of the result.
     */
    public String getValue() {
        return value;
    }
    /**
     * Returns the numeric value of the result.
     * @return
     */
    public double getNumericValue() {
        return Double.parseDouble(value);
    }
    /**
     * Adds note to this result.
     * @param p_note
     */
    public void setNote( String p_note ) {
        note = p_note;
    }
    /**
     * Returns note associated with this result.
     * @return
     */
    public String getNote() {
        return note;
    }

    public void setInterestLevel( double p_interestLevel ) {
        interestLevel = p_interestLevel;
    }

    public double getInterestLevel() {
        return interestLevel;
    }

    public boolean isUnresolvable() {
        return unresolvable;
    }

    public boolean hasMissingData() {
        if (missingGenotypes.isEmpty() && missingPhenotypes.isEmpty()) {
            return false;
        }
        return true;
    }

    public void setUnresolvable( boolean p_unresolved ) {
        unresolvable = p_unresolved;
    }
    
    public void merge( LogicResult logicResult ) {

        addMissingGenotypes( logicResult.getMissingGenotypes() );
        addMissingPhenotypes( logicResult.getMissingPhenotypes() );
        associatedResults.add(logicResult);

        if ( logicResult.isUnresolvable() ) {
            this.setUnresolvable(true);
        }

    }

    public void add( LogicResult p_logicResult ) {
        defineUnresolvedAs( p_logicResult, UNRESOLVED_ZERO );
        this.setValue( this.getValue() + p_logicResult.getValue() );
        merge(p_logicResult);
        this.setType( LogicResult.NUMERIC);
        this.setResult( this.getResult() || p_logicResult.getResult() );
    }

    public void subtract( LogicResult p_logicResult ) {
        defineUnresolvedAs( p_logicResult, UNRESOLVED_ZERO );
        this.setValue( this.getNumericValue() - p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.NUMERIC);
        this.setResult( this.getResult() || p_logicResult.getResult() );
    }

    public void multiply( LogicResult p_logicResult ) {
        defineUnresolvedAs( p_logicResult, UNRESOLVED_NONZERO );
        this.setValue( this.getNumericValue() * p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.NUMERIC);
        this.setResult( this.getResult() || p_logicResult.getResult() );
    }

    public void divide( LogicResult p_logicResult ) {
        defineUnresolvedAs( p_logicResult, UNRESOLVED_NONZERO );
        this.setValue( this.getNumericValue() / p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.NUMERIC);
        this.setResult( this.getResult() || p_logicResult.getResult() );
    }

    public void and( LogicResult p_logicResult ) {

        this.setResult( this.getResult() && p_logicResult.getResult() );
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN);
        this.setValue(0);
    }

    public void or( LogicResult p_logicResult ) {

        this.setResult( this.getResult() || p_logicResult.getResult() );
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN);
        this.setValue(0);
    }

    public void equals( LogicResult p_logicResult ) {

        if ( this.getType() == LogicResult.ALLELE ) {
            compareAlleles( p_logicResult );

        }
        else {
            this.setResult( compareToLogicResult( p_logicResult ));
            merge(p_logicResult);
            this.setType( LogicResult.NUMERIC);
            this.setValue(0);
        }
    }

    public void compareToAllele( String allele ) {

        boolean comparisonResult = false;

        String[] genotypeAlleles = this.getValue().split("/");
        String[] ruleAlleles = allele.split("/");
        int matchCount = 0;
        for ( int genotypeIndex = 0; genotypeIndex < genotypeAlleles.length; genotypeIndex++ ) {

            for ( int ruleIndex = 0; ruleIndex < ruleAlleles.length; ruleIndex++ ) {
                if ( genotypeAlleles[genotypeIndex].matches(ruleAlleles[ruleIndex].replaceAll("N", "\\\\w")) ) {
                    matchCount++;
                    break;
                }
            }
        }
        if ( matchCount >= ruleAlleles.length ) {
           comparisonResult =  true;
        }

        this.setResult(comparisonResult);
        this.setType( LogicResult.BOOLEAN );
        this.setValue(0);
    }

    private void compareAlleles(  LogicResult p_logicResult ) {

        boolean comparisonResult = false;

        String[] genotypeAlleles = this.getValue().split("/");
        String[] ruleAlleles = p_logicResult.getValue().split("/");
        int matchCount = 0;
        for ( int genotypeIndex = 0; genotypeIndex < genotypeAlleles.length; genotypeIndex++ ) {

            for ( int ruleIndex = 0; ruleIndex < ruleAlleles.length; ruleIndex++ ) {
                if ( genotypeAlleles[genotypeIndex].matches(ruleAlleles[ruleIndex].replaceAll("N", "\\\\w")) ) {
                    matchCount++;
                    break;
                }
            }
        }
        if ( matchCount >= ruleAlleles.length ) {
           comparisonResult =  true;
        }

        this.setResult(comparisonResult);
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN );
        this.setValue(0);
    }

    private boolean compareToLogicResult( LogicResult result ) {

        boolean comparisonResult = false;

        if ( this.getType() == LogicResult.BOOLEAN ) {
            comparisonResult = equalsBoolean(result);
        }
        if ( this.getType() == LogicResult.NUMERIC) {
            comparisonResult = equalsNumeric(result);
        }
        if ( this.getType() == LogicResult.TEXT) {
            comparisonResult = equalsText(result);
        }
        return comparisonResult;

    }

    private boolean equalsBoolean( LogicResult result ) {

        if ( result.getType() == LogicResult.BOOLEAN) {
            return this.getResult() == result.getResult();
        }

        if ( result.getType() == LogicResult.NUMERIC) {

            if ( this.getResult() && ( result.getNumericValue() == 1 ) ) {
                return true;
            }

            if ( !this.getResult() && ( result.getNumericValue() == 0 ) ) {
                return true;
            }
        }
        if ( result.getType() == LogicResult.TEXT) {
            if ( this.result && result.getValue().equals("true")) {
                return true;
            }
            if ( !this.result && result.getValue().equals("false")) {
                return true;
            }
        }

        return false;

    }

    private boolean equalsNumeric( LogicResult result ) {

        if ( result.getType() == LogicResult.BOOLEAN) {
            if ( result.getResult() && ( this.getNumericValue() == 1 ) ) {
                return true;
            }

            if ( !result.getResult() && ( this.getNumericValue() == 0 ) ) {
                return true;
            }
        }
        if ( result.getType() == LogicResult.NUMERIC) {

            return this.getNumericValue() == result.getNumericValue();

        }
        if ( result.getType() == LogicResult.TEXT ) {

            return result.getNumericValue()  == this.getNumericValue();
        }
        return false;
    }

    private boolean equalsText( LogicResult result ) {

        if ( result.getType() == LogicResult.BOOLEAN ) {

            if ( result.result && this.getValue().equals("true")) {
                return true;
            }
            if ( !result.result && this.getValue().equals("false")) {
                return true;
            }
        }
        if ( result.getType() == LogicResult.NUMERIC) {

            return this.getNumericValue()  == result.getNumericValue();

        }
        if (result.getType() == LogicResult.TEXT ) {
            return this.getValue().equals(result.getValue() );
        }
        return false;
    }

    public void lessThan( LogicResult p_logicResult ) {

        this.setResult( this.getNumericValue() < p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN);
        this.setValue(0);
    }

    public void lessThanOrEqual( LogicResult p_logicResult ) {

        this.setResult( this.getNumericValue() <= p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN);
        this.setValue(0);
    }

    public void greaterThan( LogicResult p_logicResult ) {

        this.setResult( this.getNumericValue() > p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN);
        this.setValue(0);
    }

    public void greaterOrEqual( LogicResult p_logicResult ) {

        this.setResult( this.getNumericValue() >= p_logicResult.getNumericValue() );
        merge(p_logicResult);
        this.setType( LogicResult.BOOLEAN);
        this.setValue(0);
    }

    public void defineUnresolvedAs( LogicResult result, double d ) {

        if ( result.isUnresolvable() ) {
            result.setValue(d);
        }
    }
}
