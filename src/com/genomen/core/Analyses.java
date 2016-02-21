package com.genomen.core;

import com.genomen.analyses.Analysis;
import com.genomen.utils.DOMDocumentCreator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Provides access to available analyzation logics.
 * @author ciszek
 */
public class Analyses {

    private static Analyses instance = new Analyses();
    
    //A map of available analyses.
    private Map<String, Analysis> availableAnalyses;
    
    /**
     * Gets the instance <code>Logics</code> currently available for the system.
     * @return instance of logics.
     */
    public static Analyses getInstance() {
        return instance;
    }

    private Analyses() {
        
        availableAnalyses = getLocics(Configuration.getConfiguration().getAnalyzationLogicListPath());
        
    }

    /**
     * Creates and returns a a list containing the class names of all the
     * available AnalyzationLogics.
     * @return a list containing class names of available AnalyzationLogics
     */
    public List<String> getAnalyzationLogics() {

        return new ArrayList( availableAnalyses.keySet() );

    }

    /**
     * Checks if the logic specified is currently available.
     * @param analyzationLogicName the name of the logic
     * @return <code>true</code> if the logic is available, <code>false</code> otherwise
     */
    public boolean analyzationLogicAvailable( String analyzationLogicName ) {

       return availableAnalyses.containsKey( analyzationLogicName );

    }

    /**
     * Gets the specified logic. Returns <code>null</code> if the specified logic is not available
     * @param analyzationLogicName the name of the logic
     * @return instance of the specified logic or <code>null</code>
     */
    public Analysis getAnalyzationLogic( String analyzationLogicName ) {
        return availableAnalyses.get(analyzationLogicName);
    }
    
    /**
     * Parses AnalyzationLogics from a XML file.
     */
    private Map<String, Analysis> getLocics( String logicListFilePath ) {

        HashMap<String, Analysis> returnedLogics = new  HashMap<String, Analysis>();

        Document logicsDocument = DOMDocumentCreator.createDocument(logicListFilePath);

        if ( logicsDocument == null ) {
            Logger.getLogger(Analyses.class ).debug(Error.getMessage( Error.ErrorType.FILE_NOT_FOUND, logicListFilePath));
            return returnedLogics;
        }

        Element rootNode = logicsDocument.getDocumentElement();
        NodeList logicList = rootNode.getElementsByTagName("logic");
        //Loop through logics
        for ( int logicIndex = 0; logicIndex < logicList.getLength(); logicIndex++) {

            Analysis analyzationLogic = null;

            //Extract class data from logic
            Element logic = (Element)logicList.item(logicIndex);
            try {
                Class logicClass = Class.forName(logic.getAttribute("class"));
                analyzationLogic = (Analysis)logicClass.newInstance();
            }
            catch (ClassNotFoundException ex) {
                Logger.getLogger(Analyses.class ).debug(ex);
            }
            catch (InstantiationException ex) {
                Logger.getLogger(Analyses.class).error(ex);
            }
           catch (IllegalAccessException ex) {
                Logger.getLogger(Analyses.class).error(ex);
            }

            //Extract parameters
            NodeList parameterList = logic.getElementsByTagName("parameters");
            //Element given as a parameter to analyzationLogic's initialization
            //method is the first occurence of eleemnt with tag "parameter"

            Element parameters = null;

            if ( parameterList != null) {
                parameters = (Element)parameterList.item(0);
            }
            if ( analyzationLogic != null ) {
                analyzationLogic.initialize( parameters );
                returnedLogics.put(analyzationLogic.getClass().getName(), analyzationLogic);
            }
        }

        return returnedLogics;
        
    }    

}
