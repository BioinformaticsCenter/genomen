package com.genomen.core;

import java.util.ArrayList;
import java.util.List;


/**
 * Base class for result entities
 * @author ciszek
 */
public abstract class ResultEntity {

    private List<String> tags = new ArrayList<String>();

    /**
     * Adds a tag for this result.
     * @param tag a tag name
     */
    public void addTag( String tag) {
        tags.add(tag);
    }
    
    /**
     * Checks if this result has a given tag
     * @param tagName a tag name
     * @return <code>true</code> if this result has the tag specified, <code>false</code> otherwise.
     */
    public boolean hasTag( String tagName ) {
        return tags.contains(tagName);
    }

}
