package com.genomen.ui.cli;

import com.genomen.core.AnalysisRequest;
import com.genomen.core.Configuration;
import com.genomen.core.DataSet;
import com.genomen.core.Analyses;
import com.genomen.reporter.ReportFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Interprets command-line arguments and creates a valid analysis request based on command-line arguments.
 * @author ciszek
 */
public class ArgumentProcessor {

    private static final String COMMAND_GENERIC = "^[-]+[\\w]+";
    
    private static final String COMMAND_OUTPUT = "-o";
    private static final String VALID_OUTPUT_REGEXP = "^[\\/\\w]+";
    private static final String VALID_OUTPUT_PATH_REGEXP = "^[\\/]?([\\w_]+[\\/])*";

    private static final String COMMAND_OUTPUT_FORMAT = "-f";
    private static final String VALID_OUTPUT_FORMAT_REGEXP = "^\\w+";     
    
    private static final String COMMAND_ANALYSES = "-a";
    private static final String VALID_ANALYSES_REG_EXP = "^[\\w\\.]+";    
    private static final String ANALYSES_ARGUMENT_SEPARATOR = ",";    
      
    private static final String COMMAND_FILE = "-i";
    private static final String VALID_FILE_REG_EXP = "^[A-Za-z0-9_/\\.]+(:[A-Za-z0-9_/\\.]+)*,\\w+(,\\w+)";
    private static final String DATASET_SEPARATOR = ";";
    private static final String DATASET_FILE = ":";    
    private static final String DATASET_ARGUMENT_SEPARATOR = ",";
    
    private static final String COMMAND_IMPORT_SAMPLE = "-s";
    private static final String VALID_SAMPLE_REG_EXP = "^[A-Za-z0-9_/\\.]+(,[A-Za-z0-9_/\\.]+)*";
    private static final String SAMPLE_SEPARATOR = ",";    
    
    private static final String COMMAND_REMOVE_SAMPLE = "--remove";     

    private static final String COMMAND_LANGUAGE = "-l";
    private static final String VALID_LANGUAGE_REG_EXP = "^\\w+";

    private static final String COMMAND_HELP = "--help";
    private static final String COMMAND_DESTROY_DATABASE = "--destroy-db";
    
    private static final String COMMAND_IMPORT_DATABASE = "--import-db";
    private static final String VALID_IMPORT_DATABASE_REGEXP = "^[A-Za-z0-9_/\\.]*\\.xml";
             
    private static final String COMMAND_EXPORT_DATABASE = "--export";
    private static final String VALID_EXPORT_DATABASE_REGEXP = "^[A-Za-z0-9_/\\.]*";
    
    private static final String COMMAND_CREATE_DATABASE_TEMPLATE = "--template";
    private static final String VALID_DATABASE_TEMPLATE_REGEXP = "^[A-Za-z0-9_/\\.]*";
  
    private static final String COMMAND_CREATE_DATABASE = "--create-db";
    private static final String VALID_CREATE_DATABASE_REGEXP = "^[A-Za-z0-9_/\\.]*";    
    
    private static final String COMMAND_PERSIST_DATASETS = "--persist";
    
    private static final String COMMAND_LIST = "--list";  
    private static final String VALID_LIST_REGEXP = "[svdr]+";
    private static final String VALID_LIST_SAMPLES = "s";   
    private static final String VALID_LIST_DATASETS = "d";     
    private static final String VALID_LIST_VARIANTS = "v";    
    private static final String VALID_LIST_RULES = "r";       
    
    private static final String ERROR_MESSAGE = "Invalid command syntax";

    /**
     * Creates an analysis request based on the given command-line arguments
     * @param args command-line arguments.
     * @return an analysis request
     * @throws InvalidCLIArgumentException
     */
    public static AnalysisRequest createRequest( String[] args ) throws InvalidCLIArgumentException {

        String outputName = parseOutputName(args);
        String outputPath = parseOutputPath(args);        
        List<String> requiredAnalyses = parseRequiredAnalyses(args);
        List<String> requiredFormats = parseFormats(args);        
        String language = parseLanguage(args);
        
        if ( language == null) {
            language = Configuration.getConfiguration().getLanguage();
        }
        
        List<DataSet> dataSets = parseDataSets(args);
        List<String> requiredSamples = parseRequiredSamples(args);
        
        //If output file is specified but no alayses are listed, perform all analyses.
        if ( outputName != null && requiredAnalyses.isEmpty()) {         
            requiredAnalyses=  Analyses.getInstance().getAnalyzationLogics();
        }
        
        AnalysisRequest analysisRequest = null;
        analysisRequest = new AnalysisRequest( dataSets, requiredAnalyses, requiredSamples,language, requiredFormats  );
        analysisRequest.setName(outputName); 
        analysisRequest.setOutputPath(outputPath);
        
        //Skip dataset removal if persistence is required or no input dataset is defined, 
        if ( datasetPersistenceRequired(args) ) {
            analysisRequest.setPersistDatasets(true);
        }     
        else if (dataSets.isEmpty() ) {
            analysisRequest.setPersistDatasets(true);
        }           
        
        return analysisRequest;
    }

    private static String parseOutputName( String[] args ) throws InvalidCLIArgumentException {
        String outputDefinition = findParameter( COMMAND_OUTPUT, VALID_OUTPUT_REGEXP, args );
        
        if (outputDefinition == null ) {
            throw new InvalidCLIArgumentException(ERROR_MESSAGE);
        }
        
        String[] outputName = outputDefinition.split("[\\/]");
        
        return outputName[outputName.length-1];
    }
    
    private static String parseOutputPath( String[] args ) throws InvalidCLIArgumentException {
        String outputDefinition = findParameter( COMMAND_OUTPUT, VALID_OUTPUT_REGEXP, args );
        
        if (outputDefinition == null ) {
            throw new InvalidCLIArgumentException(ERROR_MESSAGE);
        }
        
        Pattern pattern = Pattern.compile(VALID_OUTPUT_PATH_REGEXP);

        Matcher matcher = pattern.matcher(outputDefinition);
        String path = "";
        
        if (matcher.find()) {
            path = matcher.group();  
        }

        return path;
    }    
    
    protected static boolean knownInput( String[] args) throws InvalidCLIArgumentException {
        if ( databaseDestructionRequired( args ) || 
                databaseCreationRequired(args) ||
                datasetImportRequired(args) ||
                databaseExportRequired(args) ||
                databaseImportRequired(args) || 
                databaseTemplateRequired(args) ||
                listingRequired(args) ||
                sampleRemovalRequired(args) ||
                helpRequired( args ) ||
                taskRequired(args)
                ) {
            return true;
        }
        return false;
    }

    protected static List<DataSet> parseDataSets( String[] args ) throws InvalidCLIArgumentException {

        List<DataSet> dataSetList = new LinkedList<DataSet>();

        int parameterIndex = findParameterIndex(COMMAND_FILE,args);
        
        if ( parameterIndex >= args.length || parameterIndex < 0 ) {
            return dataSetList;
        }
        
        String parameters = args[parameterIndex];
        
        String[] dataSets = parameters.split(DATASET_SEPARATOR);
  
        for ( int dataSetIndex = 0; dataSetIndex < dataSets.length; dataSetIndex++) {
            
            if ( dataSets[dataSetIndex].matches(VALID_FILE_REG_EXP)) {
                     
                DataSet dataSet = createDataSet(dataSets[dataSetIndex].trim());
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
            
            if ( requestedFormats[formatIndex].matches(VALID_OUTPUT_FORMAT_REGEXP)) {
                     
                formats.add(requestedFormats[formatIndex].trim());    

            }        
        }        
        return formats;
    }
    
    private static List<String> parseRequiredAnalyses(  String[] args ) throws InvalidCLIArgumentException {
        List<String> analyses = new ArrayList<String>();
        int parameterIndex = findParameterIndex(COMMAND_ANALYSES,args);  
        
        if ( parameterIndex < 0 ) {
            return analyses;
        }
        
        String parameters = args[parameterIndex];
        
        String[] requestedAnalyses = parameters.split(ANALYSES_ARGUMENT_SEPARATOR);
  
        for ( int analysisIndex = 0; analysisIndex < requestedAnalyses.length; analysisIndex++) {
            
            if ( requestedAnalyses[analysisIndex].matches(VALID_ANALYSES_REG_EXP)) {
                     
                analyses.add(requestedAnalyses[analysisIndex].trim());    
            }        
        }        
        return analyses;   
    }
    
    protected static List<String> parseRequiredSamples(  String[] args ) throws InvalidCLIArgumentException {
        List<String> samples = new ArrayList<String>();
        int parameterIndex = findParameterIndex(COMMAND_IMPORT_SAMPLE,args);  
        
        if ( parameterIndex < 0 ) {
            return samples;
        }
        
        String parameters = args[parameterIndex];
        
        String[] requestedSamples = parameters.split(SAMPLE_SEPARATOR);
  
        for ( int sampleIndex = 0; sampleIndex < requestedSamples.length; sampleIndex++) {
            
            if ( requestedSamples[sampleIndex].matches(VALID_SAMPLE_REG_EXP)) {
                     
                samples.add(requestedSamples[sampleIndex].trim());    
            }        
        }        
        return samples;   
    }    
    
    
    private static DataSet createDataSet( String parameterSet ) {
        
        String name = "";
        String format = "";
        String[] parameters = parameterSet.split(DATASET_ARGUMENT_SEPARATOR);
      
        format = parameters[1].trim();
        
        if ( parameters.length == 3) {
            name = parameters[2].trim();
        }
               
        String[] files = parameters[0].replace("\\s+", "").split(DATASET_FILE);

        return new DataSet( name, files, format );
        
        
    }

    private static String parseLanguage( String[] args) throws InvalidCLIArgumentException {

       return findParameter( COMMAND_LANGUAGE, VALID_LANGUAGE_REG_EXP, args );
    }
    
    /**
     * Gets the name of the imported database file.
     * @param args CL arguments
     * @return  imported database file name
     * @throws InvalidCLIArgumentException
     */
    public static String getImportedDbFile( String[] args ) throws InvalidCLIArgumentException {
        return findParameter( COMMAND_IMPORT_DATABASE, VALID_IMPORT_DATABASE_REGEXP, args );
    }
    
    /**
     * Gets the file name for the exported database.
     * @param args CL arguments
     * @return exported database file name
     * @throws InvalidCLIArgumentException
     */
    public static String getExportedFile( String[] args ) throws InvalidCLIArgumentException {
        return findParameter( COMMAND_EXPORT_DATABASE, VALID_EXPORT_DATABASE_REGEXP, args );
    }    
  
    /**
     * Gets the file name for the database template
     * @param args CL arguments
     * @return database template file name
     * @throws InvalidCLIArgumentException
     */
    public static String getTemplateFile( String[] args ) throws InvalidCLIArgumentException {
        return findParameter( COMMAND_CREATE_DATABASE_TEMPLATE, VALID_DATABASE_TEMPLATE_REGEXP, args );
    }        
    
    /**
     * Gets the name of the file containing commands needed to reconstruct the database.
     * @param args CL arguments
     * @return database creation file name
     * @throws InvalidCLIArgumentException
     */
    public static String getDatabaseCreationFile( String[] args ) throws InvalidCLIArgumentException {
        return findParameter( COMMAND_CREATE_DATABASE, VALID_CREATE_DATABASE_REGEXP, args );
    }  
    
    private static String findParameter( String command, String parameterRegExp, String[] parameters) throws InvalidCLIArgumentException {

        int parameterIndex = findParameterIndex( command, parameters );
        if ( !validArgument( parameters, parameterIndex, parameterRegExp )) {
            return null;
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
    
    private static boolean hasParameters(String command, String[] parameters) {
        
        int index = findParameterIndex( command, parameters );    
        
        //Last parameter cannot have arguments
        if ( index >= parameters.length) {
            return false;
        }
        //If the next argument is another command, the current argument does not have parameters
        if ( parameters[index+1].matches(COMMAND_GENERIC)) {
            return false;
        }
        return true;
        
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

    /**
     * Is the destruction of the database requested in the given arguments?
     * @param args CL arguments
     * @return <code>true</code> if the database is to be destroyed, <code>false</code> otherwise.
     */
    public static boolean databaseDestructionRequired( String[] args) {

        for ( int i = 0; i < args.length; i++) {

            if ( args[i].equals(COMMAND_DESTROY_DATABASE)) {
                return true;
            }

        }
        return false;
    }
    
    /**
     * Is a file requested to be imported to the database in the given arguments?
     * @param args CL arguments.
     * @return <code>true</code> if a file is to be imported to the database, <code>false</code> otherwise.
     * @throws InvalidCLIArgumentException
     */
    public static boolean databaseImportRequired( String[] args) throws InvalidCLIArgumentException {

        if (findParameterIndex(COMMAND_IMPORT_DATABASE, args ) >=  0) {
            return true;
        }
        return false;
    } 
    
    /**
     * Is sample dataset import without further analysis required in the given arguments?
     * @param args CL arguments.
     * @return <code>true</code> if only import is requested, <code>false</code> otherwise.
     * @throws InvalidCLIArgumentException
     */
    public static boolean datasetImportRequired( String[] args) throws InvalidCLIArgumentException {

        if (findParameterIndex(COMMAND_FILE, args ) >=  0 && !taskRequired(args)) {
            return true;
        }
        return false;
    }     
    
    /**
     * Is the database requested to be exported in the given arguments.
     * @param args CL arguments.
     * @return <code>true</code> if the database is to be exported, <code>false</code> otherwise.
     * @throws InvalidCLIArgumentException
     */
    public static boolean databaseExportRequired( String[] args) throws InvalidCLIArgumentException {

        if (findParameterIndex(COMMAND_EXPORT_DATABASE, args ) >=  0) {
            return true;
        }
        return false;
    }   
    
    /**
     * Is the creation of a database template requested in the given arguments.
     * @param args CL arguments
     * @return <code>true</code> if a template is to be created, <code>false</code> otherwise.
     */
    public static boolean databaseTemplateRequired( String[] args ) {
        if (findParameterIndex(COMMAND_CREATE_DATABASE_TEMPLATE, args ) >=  0) {
            return true;
        }
        return false;   
    }
    
    /**
     * Is the (re)creation of the database requested in the given arguments. 
     * @param args CL arguments
     * @return <code>true</code> if the creation of the database is requested, <code>false</code> otherwise.
     */
    public static boolean databaseCreationRequired( String[] args ) {
        if (findParameterIndex(COMMAND_CREATE_DATABASE, args ) >=  0) {
            return true;
        }
        return false;   
    }    
    
    /**
     * Is the persistence of the task related datasets requested in the given arguments. 
     * @param args CL arguments
     * @return <code>true</code> if the persistence of the datasets is required, <code>false</code> otherwise.
     */
    public static boolean datasetPersistenceRequired( String[] args ) {
        if (findParameterIndex(COMMAND_PERSIST_DATASETS, args ) >=  0) {
            return true;
        }
        return false;   
    }   
    
    /**
     * Is listing of the stored datasets required
     * @param args CL arguments
     * @return <code>true</code> if listing of the datasets is required, <code>false</code> otherwise.
     */
    public static boolean listingRequired( String[] args ) {
        if (findParameterIndex(COMMAND_LIST, args ) >=  0) {
            return true;
        }
        return false;   
    }  
    
    public static boolean[] getRequiredListings( String[] args ) throws InvalidCLIArgumentException {
        
        /*
        0 samples
        1 datasets
        2 variants
        4 variants        
        */
        boolean listings[] = new boolean[4];
        
        //If the command has no parameters, list everything
        if ( !hasParameters(COMMAND_LIST, args)) {
            listings[0] = true;
            listings[1] = true;
            listings[2] = true;
            listings[3] = true;  
            return listings;
        }
        
        String listingParameters = findParameter( COMMAND_LIST, VALID_LIST_REGEXP, args );
        
        if ( listingParameters == null ) {
            throw new InvalidCLIArgumentException(ERROR_MESSAGE);
        }
        
        if ( listingParameters.contains(VALID_LIST_SAMPLES) ) {
            listings[0] = true;
        }
        if ( listingParameters.contains(VALID_LIST_DATASETS) ) {
            listings[1] = true;
        }  
        if ( listingParameters.contains(VALID_LIST_VARIANTS) ) {
            listings[2] = true;
        } 
        if ( listingParameters.contains(VALID_LIST_RULES) ) {
            listings[3] = true;
        }         
        
        return listings;
    }
    
    /**
     * Is removal of one or more samples required.
     * @param args CL arguments
     * @return <code>true</code> if removal of samples is required, <code>false</code> otherwise.
     */
    public static boolean sampleRemovalRequired( String[] args ) {
        if (findParameterIndex(COMMAND_REMOVE_SAMPLE, args ) >=  0) {
            return true;
        }
        return false;   
    }      
    
    /**
     * Is a task requested to be performed in the given arguments.
     * @param args CL arguments
     * @return <code>true</code> if a task is to be performed, false otherwise.
     * @throws InvalidCLIArgumentException
     */
    public static boolean taskRequired( String[] args) throws InvalidCLIArgumentException {

        boolean outputDefined = false;
        boolean fileDefined = false;
        
        for ( int i = 0; i < args.length; i++) {

            if ( args[i].equals(COMMAND_OUTPUT) ) {
                outputDefined = true;
            }
            if ( args[i].equals(COMMAND_FILE) ) {
                fileDefined = true;
            }            

        }
        //If output and input are defined, an analysis is to be peformed.
        if ( outputDefined ) {
            return true;
        }
        return false;
    }    
           
    
}
