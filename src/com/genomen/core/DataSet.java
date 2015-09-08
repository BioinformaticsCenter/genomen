package com.genomen.core;

/**
 * Presents data stored in one or several files
 * @author ciszek
 */
public final class DataSet {

    private final String name;
    private final String[] fileNames;
    private final String format;

    /**
     * Name of this dataset.
     * @return the name of this dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the files that form this dataset.
     * @return files that form this dataset
     */
    public String[] getFiles() {
        return fileNames;
    }

    /**
     * Gets the format of this dataset-
     * @return the format of this dataset
     */
    public String getFormat() {
        return format;
    }

    /**
     * Constructs a dataset with the given name, files and format
     * @param p_name the name of this dataset
     * @param p_fileNames the files that form this dataset
     * @param p_format format of this dataset
     */
    public DataSet ( String p_name, String[] p_fileNames, String p_format ) {
        name = p_name;
        fileNames = p_fileNames;
        format = p_format;
    }

}
