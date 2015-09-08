package com.genomen.core;


/**
 * Presents identification data of a single person
 * @author ciszek
 */
public class Individual {


    private String id = "";
    private String familyId = "";

    /**
     * Returns the id of this individual.
     * @return the id of this individual
     */
    public String getId() {
        return id;
    }
    /**
     * Returns the family id of this individual.
     * @return the family id of this individual
     */
    public String getFamilyId() {
        return familyId;
    }

    /**
     * Constructs a presentation of an individual with the id given.
     * @param p_id the id of this individual
     */
    public Individual( String p_id ) {
        id = p_id;
    }

    /**
     * Constructs a presentation of an individual with the individual id and the family id given.
     * @param p_id the id of this individual
     * @param p_familyID the family id of this individual
     */
    public Individual( String p_id, String p_familyID ) {
        id = p_id;
        familyId = p_familyID;
    }





}