package com.genomen.core;

import com.genomen.utils.DOMDocumentCreator;
import java.io.File;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Provides access to system configuration.
 * @author ciszek
 */
public class Configuration {
    
    private static Configuration instance = new Configuration();
    
    /**
     * Gets the system configuration
     * @return
     */
    public static Configuration getConfiguration() {
        return instance;
    }
    

    private static final String CONFIGURATION_FILE_PATH = "config/Config.XML";

    private final int MAX_CONCURRENT_ANALYSES_PER_TASK;
    private final int MAX_CONCURRENT_INDIVIDUALS_PER_TASK;
    private final int MAX_CONCURRENT_ANALYSES;
    private final int MAX_CONCURRENT_REQUESTS;
    private final int MAX_QUEUE_TIME;
    private final int DB_TYPE;
    private final String ANALYZATIONLOGIC_LIST_PATH;
    private final String TMP_FOLDER_PATH;    
    private final String DATABASE_ADDRESS;
    private final String DATABASE_SCHEMA_NAME;
    private final String DATABASE_TEMP_SCHEMA_NAME;
    private final String DATA_TYPE_LIST_PATH;    
    private final String LANGUAGE;
    private final String XSLT_FILE_PATH;   
    
    /**
     * Gets the maximum allowed number of concurrent analyses per task.
     * @return maximum concurrent analyses
     */
    public int getMaxConcurrentAnalysesPerTask() {
        return MAX_CONCURRENT_ANALYSES_PER_TASK;
    }

    /**
     * Gets the maximum number of individuals analyzed per task.
     * @return maximum individuals per task
     */
    public int getMaxConcurrentIndividualsPerTask() {
        return MAX_CONCURRENT_INDIVIDUALS_PER_TASK;
    }

    /**
     * Gets the maximum number of analysis tasks than can be run simultaneously. 
     * @return maximum concurrent analysis tasks
     */
    public int getMaxConcurrentAnalyses() {
        return MAX_CONCURRENT_ANALYSES;
    }

    /**
     * Gets the maximum number of analysis requests that can be processed simultaneously.
     * @return maximum number of concurrent analysis requests
     */
    public int getMaxConcurrentRequests() {
        return MAX_CONCURRENT_REQUESTS;
    }

    /**
     * Maximum amount of time task is allowed to wait in queue.
     * @return maximum queue time
     */
    public int getMaxQueueTime() {
        return MAX_QUEUE_TIME;
    }

    /**
     * Gets the database type used by this configuration.
     * @return the type of database used
     */
    public int getDBType() {
        return DB_TYPE;
    }

    /**
     * Gets the path to the list of analyzation logics
     * @return the analyzation logic list path
     */
    public String getAnalyzationLogicListPath() {
        return ANALYZATIONLOGIC_LIST_PATH;
    }
    
    /**
     * Gets the tmp folder path
     * @return tmp folder path
     */
    public String getTmpFolderPath() {
        return TMP_FOLDER_PATH;
    }    
    
    /**
     * Gets the XSLT file path
     * @return XSLT file path
     */
    public String getXSLTFilePath() {
        return XSLT_FILE_PATH;
    }     
    
    /**
     * Gets the path to the list of datatypes.
     * @return the datatype list path
     */
    public String getDataTypeListPath() {
        return DATA_TYPE_LIST_PATH;
    }    

    /**
     * Gets the address of the database used by this configuration.
     * @return the database address
     */
    public String getDatabaseAddress() {
        return DATABASE_ADDRESS;
    }

    /**
     * Gets the name of the schema used by this configuration.
     * @return the database schema name
     */
    public String getDatabaseSchemaName() {
        return DATABASE_SCHEMA_NAME;
    }

    /**
     * Gets the name of the schema used for temporary data.
     * @return the temp schema name.
     */
    public String getDatabaseTempSchemaName() {
        return DATABASE_TEMP_SCHEMA_NAME;
    }

    public String getLanguage() {
        return LANGUAGE;
    }
    
    private Document document;
    

    private Configuration( ) {

        File configFile = new File(CONFIGURATION_FILE_PATH);

        if ( configFile.exists() ) {

            document = DOMDocumentCreator.createDocument(CONFIGURATION_FILE_PATH);
        }
        else {
            Logger.getLogger( Configuration.class).fatal("Unable to open file: " + CONFIGURATION_FILE_PATH);
            System.exit(0);
        }

        MAX_CONCURRENT_ANALYSES_PER_TASK = getIntValue("maxConcurrentAnalysesPerTask");
        MAX_CONCURRENT_INDIVIDUALS_PER_TASK = getIntValue("maxConcurrentIndividualsPerTask");
        MAX_CONCURRENT_ANALYSES = getIntValue("maxConcurrentAnalyses");
        MAX_CONCURRENT_REQUESTS = getIntValue("maxConcurrentRequests");
        MAX_QUEUE_TIME = getIntValue("maxQueueTime");
        DB_TYPE = getIntValue("DBType");
        ANALYZATIONLOGIC_LIST_PATH = getStringValue("analysisLogicListPath");
        TMP_FOLDER_PATH = getStringValue("tmpFolderPath");
        DATABASE_ADDRESS = getStringValue("databaseAddress");
        DATABASE_SCHEMA_NAME = getStringValue("schemaName");
        DATABASE_TEMP_SCHEMA_NAME = getStringValue("tempSchemaName");
        DATA_TYPE_LIST_PATH = getStringValue("dataTypeListPath");    
        LANGUAGE = getStringValue("language");        
        XSLT_FILE_PATH = getStringValue("xsltFilePath");    
    }

    /**
     * Returns an integer value from the configuration file.
     * @param tagName Tag name of the configuration value required.
     * @return Configuration value
     */
    private int getIntValue( String tagName ) {

        int value = 0;

        Element element = document.getDocumentElement();

        NodeList childNodeList= element.getElementsByTagName(tagName);

        Element valueNode = (Element)childNodeList.item(0);

        if ( valueNode != null) {
        value = Integer.parseInt( valueNode.getFirstChild().getNodeValue() );
        }
        else {
            Logger.getLogger( Configuration.class).fatal("Unable to read configuration data: " + tagName );
            System.exit(0);
        }

        return value;


    }

    /**
     * Return a String from the configuration file.
     * @param tagName Tag name of the configuration value required.
     * @return Configuration value.
     */
    private String getStringValue( String tagName ) {


        String value = null;

        Element element = document.getDocumentElement();

        NodeList childNodeList= element.getElementsByTagName(tagName);

        Element valueNode = (Element)childNodeList.item(0);

        if ( valueNode != null) {
        value = valueNode.getFirstChild().getNodeValue();
        }
        else {
            Logger.getLogger( Configuration.class).fatal("Unable to read configuration data: " + tagName );
            System.exit(0);
        }

        return value;
    }
        
    
    
}
