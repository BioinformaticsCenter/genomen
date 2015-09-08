package com.genomen.ui.cli;

import com.genomen.core.AnalysisRequest;
import com.genomen.core.DataSet;
import com.genomen.core.Logics;
import com.genomen.core.reporter.ReportFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Creates a valid analysis request based on command-line arguments.
 * @author ciszek
 */
public class ArgumentProcessor {

    private static final String COMMAND_OUTPUT = "-o";
    private static final String VALID_OUTPUT_REG_EXP = "^\\w+";

    private static final String COMMAND_OUTPUT_FORMAT = "-f";
    private static final String VALID_OUTPUT_FORMAT_REG_EXP = "^\\w+";    
    private static final String COMMAND_ARGUMENT_SEPARATOR = ",";   
    
    private static final String COMMAND_ANALYSES = "-a";
    private static final String VALID_ANALYSES_REG_EXP = "^[\\w\\.]+";    
    private static final String ANALYSES_ARGUMENT_SEPARATOR = ",";       
    
    private static final String COMMAND_FILE = "-i";
    private static final String VALID_FILE_REG_EXP = "^[A-Za-z0-9_/.]+(:[A-Za-z0-9_/.]+)*,\\w+(,\\w+)";
    private static final String DATASET_SEPARATOR = ";";
    private static final String DATASET_FILE = ":";    
    private static final String DATASET_ARGUMENT_SEPARATOR = ",";

    private static final String COMMAND_LANGUAGE = "-l";
    private static final String VALID_LANGUAGE_REG_EXP = "^\\w+";

    private static final String COMMAND_HELP = "-help";
    private static final String COMMAND_DESTROY_DATABASE = "-destroy-database";

    private static final String ERROR_MESSAGE = "Invalid syntax";

    /**
     * Creates an analysis request based on the given command-line arguments
     * @param args command-line arguments.
     * @return an analysis request
     * @throws InvalidCLIArgumentException
     */
    public static AnalysisRequest createRequest( String[] args ) throws InvalidCLIArgumentException {

        String outputName = parseOutputName(args);
        List<String> requiredAnalyses = parseRequiredAnalyses(args);
        List<String> requiredFormats = parseFormats(args);        
        String language = parseLanguage(args);
        List<DataSet> dataSets = parseDataSets(args);

        AnalysisRequest analysisRequest = null;
        analysisRequest = new AnalysisRequest( dataSets, requiredAnalyses, language, requiredFormats  );
        analysisRequest.setName(outputName);
        

        return analysisRequest;
    }

    private static String parseOutputName( String[] args ) throws InvalidCLIArgumentException {

        return findParameter( COMMAND_OUTPUT, VALID_OUTPUT_REG_EXP, args );
    }

    private static List<DataSet> parseDataSets( String[] args ) throws InvalidCLIArgumentException {

        List<DataSet> dataSetList = new LinkedList<DataSet>();

        int parameterIndex = findParameterIndex(COMMAND_FILE,args);
        
        if ( parameterIndex >= args.length || parameterIndex < 0 ) {
            return dataSetList;
        }
        
        String parameters = args[parameterIndex];
        
        String[] dataSets = parameters.split(DATASET_SEPARATOR);
  
        for ( int dataSetIndex = 0; dataSetIndex < dataSets.length; dataSetIndex++) {
            
            if ( dataSets[dataSetIndex].matches(VALID_FILE_REG_EXP)) {
                     
                DataSet dataSet = createDataSet(dataSets[dataSetIndex]);
                dataSetList.add(dataSet);              
            }        
        }
        
        return dataSetList;

    }
    
    private static List<String> parseFormats( String[] args) throws InvalidCLIArgumentException {
        List<String> formats = new ArrayList<String>();
        int parameterIndex = findParameterIndex(COMMAND_OUTPUT_FORMAT,args);
        
        if ( parameterIndex >= args.length || parameterIndex < 0 ) {
            formats.add( ReportFormat.HTML.getName());
            return formats;
        }        
        
        String parameters = args[parameterIndex];
        
        String[] requestedFormats = parameters.split(ANALYSES_ARGUMENT_SEPARATOR);
  
        for ( int formatIndex = 0; formatIndex < requestedFormats.length; formatIndex++) {
            
            if ( requestedFormats[formatIndex].matches(VALID_OUTPUT_FORMAT_REG_EXP)) {
                     
                formats.add(requestedFormats[formatIndex]);    

            }        
        }        
        return formats;
    }
    
    private static List<String> parseRequiredAnalyses(  String[] args ) throws InvalidCLIArgumentException {
        List<String> analyses = new ArrayList<String>();
        int parameterIndex = findParameterIndex(COMMAND_ANALYSES,args);
        
        if ( parameterIndex >= args.length || parameterIndex < 0 ) {
            return Logics.getInstance().getAnalyzationLogics();
        }        
        
        String parameters = args[parameterIndex];
        
        String[] requestedAnalyses = parameters.split(ANALYSES_ARGUMENT_SEPARATOR);
  
        for ( int analysisIndex = 0; analysisIndex < requestedAnalyses.length; analysisIndex++) {
            
            if ( requestedAnalyses[analysisIndex].matches(VALID_ANALYSES_REG_EXP)) {
                     
                analyses.add(requestedAnalyses[analysisIndex]);    
            }        
        }        
        return analyses;   
    }
    
    private static DataSet createDataSet( String parameterSet ) {
        
        String name = "";
        String format = "";
        String[] parameters = parameterSet.split(DATASET_ARGUMENT_SEPARATOR);
      
        format = parameters[1];
        
        if ( parameters.length == 3) {
            name = parameters[2];
        }
               
        String[] files = parameters[0].split(DATASET_FILE);

        return new DataSet( name, files, format );
        
        
    }

    private static String parseLanguage( String[] args) throws InvalidCLIArgumentException {

       return findParameter( COMMAND_LANGUAGE, VALID_LANGUAGE_REG_EXP, args );
    }

    private static String findParameter( String command, String parameterRegExp, String[] parameters) throws InvalidCLIArgumentException {

        int parameterIndex = findParameterIndex( command, parameters );
        if ( !validArgument( parameters, parameterIndex, parameterRegExp )) {
            throw new InvalidCLIArgumentException(ERROR_MESSAGE);
        }
        
        return parameters[parameterIndex];
    }
    
    private static int findParameterIndex( String command, String[] parameters ) {

        int index = -1;

        for ( int i = 0; i < parameters.length; i++) {

            if ( parameters[i].equals(command)) {
                index = i+1;
                break;
            }
        }

        return index;
    }

    private static boolean validArgument( String[] args, int index, String regexp ) {

        if ( index >= args.length || index < 0 ) {
            return false;
        }
        return args[index].matches(regexp);
    }

    public static boolean helpRequired( String[] args ) {

        for ( int i = 0; i < args.length; i++) {

            if ( args[i].equals(COMMAND_HELP)) {
                return true;
            }

        }

        return false;
    }

    public static boolean databaseDestructionRequired( String[] args) {

        for ( int i = 0; i < args.length; i++) {

            if ( args[i].equals(COMMAND_DESTROY_DATABASE)) {
                return true;
            }

        }
        return false;
    }
    
}
