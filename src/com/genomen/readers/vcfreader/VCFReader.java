package com.genomen.readers.vcfreader;

import com.genomen.utils.StringUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple reader for reading VCF files row by row.
 * @author ciszek
 */
public class VCFReader {
    
    //Regexp patterns for row indentification and preliminary validation.
    private static final Pattern METADATA_ROW_REGEXP = Pattern.compile("^##.+");
    private static final Pattern HEADER_ROW_REGEXP = Pattern.compile("^#CHROM\\tPOS\\tID\\tREF\\tALT\\tQUAL\\tFILTER\\tINFO(\\tFORMAT)?(\\t[a-zA-Z0-9_]+)*");
    private static final Pattern VALID_FORMAT_REGEXP = Pattern.compile("^##fileformat=VCFv.*");
    private static final Pattern INFO_REGEXP = Pattern.compile("^##INFO=<ID=[a-zA-Z0-9_]+,Number=[0-9.ARG]+,Type=[a-zA-Z]+,Description=\".+\"(,Source=[a-zA-Z0-9_:/]+)?(,Version=[a-zA-Z0-9.]+)?>");
    private static final Pattern FORMAT_REGEXP = Pattern.compile("^##FORMAT=<ID=[a-zA-Z0-9_]+,Number=[0-9]+,Type=[a-zA-Z]+,Description=\".+\">");
    private static final Pattern FILTER_REGEXP = Pattern.compile("^##FILTER=<ID=[a-zA-Z0-9_]+,Description=\".+\">");
    private static final Pattern ALT_REGEXP = Pattern.compile("^##ALT=<ID=[a-zA-Z0-9_:-]+,Description=\".+\">");    
    private static final Pattern ASSEMBLY_REGEXP = Pattern.compile("^##assembly=[a-zA-Z0-9.:/]+");
    private static final Pattern CONTIG_REGEXP = Pattern.compile("^##contig=<ID=[a-zA-Z0-9_]+,URL=[a-zA-Z0-9./:]+(,ID=[a-zA-Z0-9_]+,URL=[a-zA-Z0-9./:]+)*");
    private static final Pattern PEDIGREE_REGEXP = Pattern.compile("^##PEDIGREE=<([a-zA-Z0-9_-]+=[\\d\\w-]+)(,[a-zA-Z0-9_-]+=[\\d\\w-]+)*>");
    private static final Pattern PEDIGREE_DB_REGEXP = Pattern.compile("^##pedigreeDB=[a-zA-Z0-9./:]+");
    private static final Pattern COMMON_COLUMNS_PATTERN = Pattern.compile(
                                                                            "^[a-zA-Z0-9_]+" // CHROM
                                                                            +"\\t[0-9]+" //POS
                                                                            +"\\t[a-zA-Z0-9]+((;[a-zA-Z0-9]+)+)*|[.]" //ID
                                                                            +"\\t[ACTG]+|<[a-zA-Z0-9]+((,[ACTG]+|<[a-zA-Z0-9]+)+)*>" //REF
                                                                            +"\\t[ACTG*]+|<[a-zA-Z0-9]+((,[ACTG*]+|<[a-zA-Z0-9]+)+)*>" //ALT
                                                                            +"\\t([0-9]+)|([0-9_]+[.][0-9]+)" //QUAL
                                                                            +"\\t.|PASS|[a-zA-Z0-9]+((;[a-zA-Z0-9]+)+)?" //FILTER
                                                                            +"\\t([a-zA-Z0-9]+=[a-zA-Z0-9.]+)|[a-zA-Z0-9.]+((;([a-zA-Z0-9]+=[a-zA-Z0-9.]+)|[a-zA-Z0-9.]+)+)*.*"//INFO
                                                                        );   
    private static final Pattern REGEXP_FLOAT = Pattern.compile("[.]|[0-9]+|([0-9]+[.][0-9]+)");
    private static final Pattern REGEXP_STRING = Pattern.compile("[a-zA-Z0-9|./]+");
    private static final Pattern REGEXP_INTEGER = Pattern.compile("[0-9.]+");
    private static final Pattern REGEXP_CHAR = Pattern.compile("[a-zA-Z.]");   
    
    private static final String ID = "ID";
    private static final String TYPE = "Type";
    private static final String NUMBER = "Number";
    private static final String DESCRIPTION = "Description";
    private static final String SOURCE = "Source";
    private static final String VERSION = "Version";
    
    private static final String VALUE_SEPARATOR = ",";
    private static final String PARAMETER_SEPARATOR = ";";
    private static final String GENOTYPE_SEPARATOR = ":";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String ANY_VALUE = ".";
    
    private static final int GENOTYPE_START = 9;
    
    private BufferedReader bufferedReader;
    
    //Metadata
    private Map<String, VCFInfo> info = new HashMap<String, VCFInfo>();
    private Map<String, VCFFormat> format = new HashMap<String, VCFFormat>();
    private Map<String, VCFFilter> filter = new HashMap<String, VCFFilter>();
    private Map<String, VCFAlt> alt = new HashMap<String, VCFAlt>();
    private Map<String, String> contigs = new HashMap<String, String>();   
    private Map<String, String> pedigree = new HashMap<String, String>();
    
    private String pedigreeDB = "";
    private String[] sampleIDs;
    private String assemblyURL = "";

    private int currentRow = 0;
    
    /**
     * Gets the INFO entries of this file as a list.
     * @return a list of INFO entries.
     */
    public List<VCFInfo> getInfoList() {
        return new ArrayList<VCFInfo>(info.values());
    }
    
    /**
     * Gets an info entry matching the given id or <code>null</code> if no INFO entry with such id exists.
     * @param id id of the required INFO entry.
     * @return INFO entry matching the specified id, or <code>null</code> no matching entry exists
     */
    public VCFInfo getInfo( String id ) {
        return info.get(id);
    }
    
    /**
     * Gets the FORMAT entries of this file as a list.
     * @return a list of format entries
     */
    public List<VCFFormat> getFormatList() {
        return new ArrayList<VCFFormat>(format.values());
    }
    
    /**
     * Gets a format entry matching the given id or <code>null</code> if no FORMAT entry with such id exists.
     * @param id id of the required FORMAT entry
     * @return FORMAT entry matching the specified id, or <code>null</code> if no matching entry exists
     */
    public VCFFormat getFormat( String id) {
        return format.get(id);
    }

    /**
     * Gets the FILTER entries of this file as a list.
     * @return a list of FILTER entries.
     */
    public List<VCFFilter> getFilterList() {
        return new ArrayList<VCFFilter>(filter.values());
    }
    
    /**
     * Gets a FILTER entry matching the given id or <code>null</code> if no FILTER entry with such id exists.
     * @param id id of the required FILTER entry
     * @return FILTER entry matching the specified id, or <code>null</code> if no matching entry exists.
     */
    public VCFFilter getFilter( String id ) {
        return filter.get(id);
    }
    
    /**
     * Gets the ALT entries of this file as a list.
     * @return a list of ALT entries.
     */
    public List<VCFAlt> getAltList() {
        return new ArrayList<VCFAlt>(alt.values());
    }
    
    /**
     * Gets an ALT entry matching the given id or <code>null</code> if no ALT entry with such id exists.
     * @param id id of the required ALT entry.
     * @return ALT entry matching the given id, or <code>null</code> if no matching entry exists.
     */
    public VCFAlt getAlt( String id ) {
        return alt.get(id);
    }
    
    /**
     * Gets the list of contigs referred to in this file.
     * @return a list of contigs.
     */
    public List<String> getContigList() {
        return new ArrayList<String>(contigs.values());
    }
    
    /**
     * Gets the contig with a given id or <code<null</code> if no such contig exists
     * @param id the id of the required contig.
     * @return contig entry matching the given id, or <code>null</code> if no matching contig exists.
     */
    public String getContig( String id ) {
        return contigs.get(id);
    }
    
    /**
     * Gets the list of pedigrees referred to in this file.
     * @return a list of pedigrees.
     */
    public List<String> getPedigreeList() {
        return new ArrayList<String>(pedigree.values());
    }
    
    /**
     * Gets the pedigree matching a given id or <code>null</code> if no such pedigree exists.
     * @param id the pedigree id
     * @return a pedigree matching the given id or <code>null</code> if no matching pedigree exists.
     */
    public String getPedigree( String id ) {
        return pedigree.get(id);
    }
    
     /**Gets the address to the pedigree database referred to in this file.
     * @return the pedigree database URL
     */
    public String getPedigreeDB() {
        return pedigreeDB;
    }

    /**Gets the sample ids present in this file as a list or empty <code>String</code> if no samples are listed in the file.
     * @return the sampleIDs present in this file or empty <code>String</code> if no samples are listed in the file.
     */
    public List<String> getSampleIDs() {
        
        if ( sampleIDs == null ) {
            return null;
        }
        
        return Arrays.asList(sampleIDs);
    }

    /**Gets the breakpoint assemblies file referred to by this file or empty <code>String</code> if no such file is specified.
     * @return the URL of the assemblies file or an empty <code>String</code> if no assemblies file is specified.
     */
    public String getAssemblyURL() {
        return assemblyURL;
    }
    
    
    /**
     * Opens a VCF-file for reading. Parses the metadata and sets the internal pointer to the first row of the dataset. 
     * @param filePath VCF-file to be opened.
     * @return <code>true</code> if file was opened successfully, <code>false</code> otherwise.
     */
    public boolean open( String filePath) {
        try {
            InputStream inputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            currentRow = 0;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VCFReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
       
        if ( !readMetadata() ) {
            return false;
        }
        
        return true;
    }
    
    //Reads the next row and increment the row counter.
    private String readNext() throws IOException {
        
        if ( bufferedReader == null ) {
            return null;
        }
        String nextLine = bufferedReader.readLine();
        currentRow++;
        return nextLine;
    }
    
    //Check if the the row is an valid format definition.
    private boolean isValidFormat() throws IOException {
        
        String nextLine = StringUtils.removeNonQuotedWhitespace(readNext());
        
        if ( VALID_FORMAT_REGEXP.matcher(nextLine).matches()) {
            return true;
        }
        return false;
    }
    
    //Read through all the metadata rows.
    private boolean readMetadata() {
        try {
            
            if ( !isValidFormat() ) {
                return false;
            }
            
            String nextLine = readNext();
            
            if ( nextLine == null) {
                return false;
            }
            
            //Remove non-quoted whitespace
            nextLine = StringUtils.removeNonQuotedWhitespace(nextLine);
            
            //Process all metadata rows.
            while ( isMetadataRow(nextLine)) {
                
                String preprocessedMetadata = preprocessMetadataRow(nextLine);

                Map<String, String> valueMap = null;
                
                //Try matching the current row to known metadata definitions.
                if ( INFO_REGEXP.matcher(nextLine).matches()) {
                    valueMap = extractValueMap(preprocessedMetadata);
                    
                    //If the number attribute does not have a definite value use -1 to present this.
                    String numberString = valueMap.get(NUMBER);
                    info.put(valueMap.get(ID), new VCFInfo(valueMap.get(ID), numberString, valueMap.get(TYPE), valueMap.get(DESCRIPTION), valueMap.get(SOURCE), valueMap.get(VERSION)));
                }
                if ( FORMAT_REGEXP.matcher(nextLine).matches()) {
                    valueMap = extractValueMap(preprocessedMetadata);
                    format.put(valueMap.get(ID), new VCFFormat(valueMap.get(ID), valueMap.get(NUMBER), valueMap.get(TYPE), valueMap.get(DESCRIPTION)) );
                }
                if (FILTER_REGEXP.matcher(nextLine).matches()) {
                    valueMap = extractValueMap(preprocessedMetadata);
                    filter.put(valueMap.get(ID), new VCFFilter(valueMap.get(ID), valueMap.get(DESCRIPTION) ));
                }  
                if (ALT_REGEXP.matcher(nextLine).matches()) {
                    valueMap = extractValueMap(preprocessedMetadata);
                    alt.put(valueMap.get(ID), new VCFAlt(valueMap.get(ID), valueMap.get(DESCRIPTION) ));     
                }
                if (CONTIG_REGEXP.matcher(nextLine).matches()) {
                    String[][] keyValuePairs = extractKeyValuePairs(nextLine);
                    
                    for ( int i = 0; i < keyValuePairs.length; i++ ) {
                        contigs.put(keyValuePairs[i][0], keyValuePairs[i][1]);
                    } 
                }
                if (PEDIGREE_REGEXP.matcher(nextLine).matches()) {
                    valueMap = extractValueMap(preprocessedMetadata);
                    pedigree.putAll(valueMap);
                }
                if (PEDIGREE_DB_REGEXP.matcher(nextLine).matches()) {
                    pedigreeDB = preprocessedMetadata;
                }       
                if (ASSEMBLY_REGEXP.matcher(nextLine).matches()) {

                    assemblyURL = preprocessedMetadata;        
                }
                nextLine = readNext();
            }
            //Header follows metadata. If the row starts with properly formed header, accept the metadata definitions.
            if ( HEADER_ROW_REGEXP.matcher(nextLine).matches()) {
                String[] headerValues = nextLine.split("\\t");
                if ( headerValues.length >= GENOTYPE_START ) {
                    sampleIDs = Arrays.copyOfRange(headerValues, GENOTYPE_START, headerValues.length);
                }
                
                return true;
            }
            else {
                return false;
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(VCFReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VCFException ex) {
            Logger.getLogger(VCFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean isMetadataRow( String row ) {
        if ( METADATA_ROW_REGEXP.matcher(row).matches()) {
            return true;
        }
        return false;
    }
    
    private String preprocessMetadataRow(String unprocessedMetadataRow) throws VCFException {
        //Split the string to variable name and metadata by the first occurence of equal sign
        String[] split = unprocessedMetadataRow.split("=",2); 
        if (split.length < 2 ) {
            throw new VCFException(VCFException.INVALID_SYNTAX, currentRow);  
        }
        String preprocessedMetadata = split[1];

        //Remove the last greater than sign from the row
        int greaterThanIndex = split[1].lastIndexOf(">");
        if ( greaterThanIndex >= 0 ) {
            preprocessedMetadata = preprocessedMetadata.substring(0, greaterThanIndex);
        }   
        
        //Remove the first less than sign from the row 
        int lessThanIndex = preprocessedMetadata.indexOf("<");
        if ( lessThanIndex >= 0 && preprocessedMetadata.length() > 1) {        
            preprocessedMetadata = preprocessedMetadata.substring(lessThanIndex+1);
            
        }        
        return preprocessedMetadata;
    }
    
    //Extract key-value pairs from the given metadata line and return them as a map.
    private Map<String, String> extractValueMap( String row ) throws VCFException {
    
        HashMap<String, String> keyValuePairs = new HashMap<String, String>();
                            
        String[] tuples = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
               
        for (String tuple : tuples) {
            String[] split = tuple.split(KEY_VALUE_SEPARATOR);
            if (split.length < 2) {
                throw new VCFException(VCFException.INVALID_SYNTAX, currentRow);
            }             
            String value = split[1].replaceAll("\"", "");
            keyValuePairs.put(split[0], value);
        }
        return keyValuePairs;
    }
       
    //Extract key-value pairs from the given row and return them as an array.
    private String[][] extractKeyValuePairs( String row ) throws VCFException {
        
        row = row.replaceAll("[<>]", "");
        String[] tuples = row.split(VALUE_SEPARATOR);
        
        String[][] keyValuePairs = new String[tuples.length][2];
        
        for( int i = 0; i < tuples.length; i++ ) {
            String[] split = tuples[i].split(KEY_VALUE_SEPARATOR);
            if (split.length < 2) {
                throw new VCFException(VCFException.INVALID_SYNTAX, currentRow);
            }
            keyValuePairs[i][0] = split[0];
            keyValuePairs[i][1] = split[1];            
        }
        return keyValuePairs;
    }    
    
    
    /**
     * Returns the next data row from the file or <code>null</code> if no more rows exist.
     * @return the next data row or <code>null</code> if no more rows exist.
     */
    public VCFRow readNextRow() throws VCFException {
        try {
            String nextLine = readNext();
            
            if ( nextLine == null ) {
                return null;
            } 
            //If the row does not match the proper format, throw an exception.
            if ( !COMMON_COLUMNS_PATTERN.matcher(nextLine).matches() ) {
                throw new VCFException(VCFException.INVALID_SYNTAX, currentRow);
            }
            
            return createVCFRow(nextLine);
            
        } catch (IOException ex) {
            Logger.getLogger(VCFReader.class.getName()).log(Level.SEVERE, null, ex);
        }  
        catch ( NumberFormatException ex) {
                throw new VCFException(VCFException.INVALID_SYNTAX, currentRow);
        }
        
        return null;
    }
    
    //Parses a data row from a VCF file and returns it as an instance of <code>VCFRow</code>
    private VCFRow createVCFRow(String line ) throws VCFException {
        
        String[] split = line.split("\\t");
        
        //We can assume that the format for the first eight columns is verified.
        String chromosome = split[0];
        int position = Integer.parseInt(split[1]);
        String id = split[2];
        String[] refValues = split[3].split(VALUE_SEPARATOR);
        String[] altValues = split[4].split(VALUE_SEPARATOR);
        String qualityValues = split[5];
        
        String[] filterValues = split[6].split(";");
        
        String[] infoArray = split[7].split(";");
        String[][] infoValues = new String[infoArray.length][2];
        
        for ( int i = 0; i < infoArray.length; i++) {
            String[] infoKeyValue = infoArray[i].split(KEY_VALUE_SEPARATOR);
            if ( infoKeyValue.length > 1) {
                infoValues[i][0] = infoKeyValue[0];
                infoValues[i][1] = infoKeyValue[1];
            }
            if ( infoKeyValue.length == 1) {
                infoValues[i][0] = infoKeyValue[0];
                infoValues[i][1] = infoKeyValue[0];
            }
        }
        

        String[] rowFormat = null;
        
        if ( split.length >= GENOTYPE_START-1) {
            rowFormat = split[GENOTYPE_START-1].split(GENOTYPE_SEPARATOR);
        }
        
        Map<String, String[]> genotypes = null;
        
        //Read genotypes using the format specified for this row.
        if ( split.length >= GENOTYPE_START-1) {
            genotypes = parseGenotypes(split, rowFormat);
        }        
        
        return  new VCFRow( chromosome, position, id, refValues, altValues, qualityValues, filterValues, infoValues, rowFormat, genotypes);
        
    }
    
    private Map<String,String[]> parseGenotypes( String[] line, String[] format ) throws VCFException {
        
        Map<String,String[]>  genotypes = new HashMap<String,String[]>();
        
        for ( int i = GENOTYPE_START; i < line.length; i++ ) {
            
            String[] genotype = line[i].split(GENOTYPE_SEPARATOR);
            
            if ( genotype.length != format.length) {
                throw new VCFException( VCFException.INVALID_SYNTAX, currentRow);
            }
            
            //Make sure that all values match format definitions
            for ( int j = 0; j < genotype.length; j++) {
                
                String[] genotypeValues = genotype[j].split(VALUE_SEPARATOR);
                //If multiple values for one key are present, verify them individually.
                for ( int k = 0; k < genotypeValues.length; k++) {
                    if ( !validGenotypeFormat( format[j], genotypeValues[k]) ) {
                        throw new VCFException( VCFException.VALUE_MISMATCH, currentRow, i);
                    }                     
                }
                               
            }
            //If there are more samples on this row than there are row names defined, throw an exception.
            if ( i -  GENOTYPE_START > sampleIDs.length) {
                throw new VCFException( VCFException.INVALID_SYNTAX, currentRow);
            }
            
            genotypes.put(sampleIDs[i-GENOTYPE_START], genotype);
            
        }
        
        return genotypes;
        
    }
    
    //Check if the genotype value has a format that matches it's definitions.
    private boolean validGenotypeFormat( String id, String genotypeEntry) {
        
        VCFFormat format = this.format.get(id);
        
        if ( format == null ) {
           return false;
        }
        
        if ( format.getType().equals(VCFFormat.FLOAT)) {
            return REGEXP_FLOAT.matcher(genotypeEntry).matches();
        }
        if ( format.getType().equals(VCFFormat.INTEGER)) {
            return REGEXP_INTEGER.matcher(genotypeEntry).matches();
        }
        if ( format.getType().equals(VCFFormat.STRING) ) {
            return REGEXP_STRING.matcher(genotypeEntry).matches();
        }
        if ( format.getType().equals(VCFFormat.CHAR)) {
            return REGEXP_CHAR.matcher(genotypeEntry).matches();
        }
        
        return true;
    }
    
    
    /**
     * Closes the file and free associated resources.
     * @return <code>true</code> if document was closed successfully, <code>false</code> otherwise.
     */
    public boolean close() {
        try {
            bufferedReader.close();
        } catch (IOException ex) {
            Logger.getLogger(VCFReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
       
}
