package com.genomen.dao;

/**
 * Interface defining methods for accessing trait descriptions.
 * @author ciszek
 */
public interface TraitDAO {

    /**
     * Gets the short description of a trait.
     * @param symbolicName symbolic trait name
     * @param language language id
     * @return short description of a trait
     */
    public abstract String getShortDescription( String symbolicName, String language );

    /**
     * Gets the detailed description of a trait.
     * @param symbolicName symbolic trait name
     * @param language language id
     * @return detailed description of a trait
     */
    public abstract String getDetailedDescription( String symbolicName, String language );

    /**
     * Translates the symbolic name of a trait into a trait name in the given language.
     * @param symbolicName symbolic name trait name
     * @param language language id
     * @return translated trait name
     */
    public abstract String getTraitName( String symbolicName, String language );
}
