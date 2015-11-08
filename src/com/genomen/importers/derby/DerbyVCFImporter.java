package com.genomen.importers.derby;

import com.genomen.core.Configuration;
import com.genomen.core.Individual;
import com.genomen.entities.DataEntityAttributeValue;
import com.genomen.importers.Importer;
import com.genomen.importers.ImporterException;
import com.genomen.readers.vcfreader.VCFException;
import com.genomen.readers.vcfreader.VCFInfo;
import com.genomen.readers.vcfreader.VCFReader;
import com.genomen.readers.vcfreader.VCFRow;
import com.genomen.readers.vcfreader.VCFEntry;
import com.genomen.utils.ResourceReleaser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Derby specific importer for VCF files.
 * @author ciszek
 */
public class DerbyVCFImporter extends DerbySNPImporter implements Importer{


    private static final String TEMP_VARIANT_FILE_NAME = "VCFVariant.temp";    
    private static final String TEMP_VARIANT_INDO_FILE_NAME = "VCFVariantInfo.temp";     
    private static final String GENOTYPE = "GT";
    private static final String DELETED = "-";

    
    private Map<String,Integer> variantIndexes = new HashMap<String, Integer>();
    private Map<String,Integer> variantInfoIndexes = new HashMap<String, Integer>();;
    private VCFReader vcfReader;
    
    @Override
    public List<Individual> importDataSet(String schemaName, String taskID, String individualID, String[] fileNames) throws ImporterException {

        List<Individual> individualList = new LinkedList<Individual>();        
        
        if ( fileNames.length != 1 ) {
            return individualList;
        }         
              
        vcfReader = new VCFReader();
        vcfReader.open(fileNames[0]);
        
        //List avaialble samples and create an entry to the the list of individuals for each one.
        List<String> sampleIDs = vcfReader.getSampleIDs();
        for ( String id : sampleIDs ) {
            individualList.add( new Individual(id));
        }

        //Create tmp files for each individual 
        List<BufferedWriter> variantWriters = new ArrayList<BufferedWriter>();
        List<BufferedWriter> infoWriters = new ArrayList<BufferedWriter>();
        
        List<File> tempVariantFiles = new ArrayList<File>();
        List<File> tempVariantInfoFiles = new ArrayList<File>();  
        
        for ( String id: sampleIDs) {
            tempVariantFiles.add( new File( Configuration.getConfiguration().getTmpFolderPath() + taskID.concat(id).concat(TEMP_VARIANT_FILE_NAME)));
            tempVariantInfoFiles.add( new File( Configuration.getConfiguration().getTmpFolderPath() + taskID.concat(id).concat(TEMP_VARIANT_INDO_FILE_NAME)));
        }
          
        //Find valid indexes for all tables
        for ( String id: sampleIDs) {
            int variantIndex = getCurrentId( individualID, DerbySNPImporter.VARIANT);
            if ( variantIndex == DerbyImporter.INVALID_ID) {
                throw new ImporterException( ImporterException.DATA_TABLE_INDEX_ERROR, DerbySNPImporter.VARIANT);
            }            
            variantIndexes.put(id, variantIndex);

            int variantInfoIndex = getCurrentId( individualID, DerbySNPImporter.VARIANT_INFO);
            if ( variantInfoIndex == DerbyImporter.INVALID_ID) {
                throw new ImporterException( ImporterException.DATA_TABLE_INDEX_ERROR, DerbySNPImporter.VARIANT_INFO);
            }            
            variantInfoIndexes.put(id, variantInfoIndex);
        }
        
        try {
            
            //Create writer for every tmp file.
            for ( int i = 0; i < sampleIDs.size(); i++ ) {
                variantWriters.add( new BufferedWriter( new FileWriter( tempVariantFiles.get(i) )) );
            }
            for ( int i = 0; i < sampleIDs.size(); i++ ) {
                infoWriters.add( new BufferedWriter( new FileWriter( tempVariantInfoFiles.get(i) )) );
            }            
            
            VCFRow row;  
                  
            //Loop through all variants and write variants to sample specific tmp files.
            while ( ( row = vcfReader.readNextRow() ) != null )  {
                //Write genotypes
                for ( int i = 0; i < sampleIDs.size(); i++) {
                    writeGenotype( sampleIDs.get(i), row, vcfReader, variantWriters.get(i), infoWriters.get(i) );
                    writeVariantInfo( sampleIDs.get(i),row, vcfReader, infoWriters.get(i) );
                }        
            }
            
            //Close all writers
            for ( BufferedWriter writer : variantWriters ) {
                ResourceReleaser.close(writer);
            }
            for ( BufferedWriter writer : infoWriters ) {
                ResourceReleaser.close(writer);
            }    
            //Import all tmp files and delete files after import
            for ( int i = 0; i < tempVariantFiles.size(); i++) {
                this.bulkImport(schemaName, taskID, sampleIDs.get(i), DerbySNPImporter.VARIANT, tempVariantFiles.get(i));
                tempVariantFiles.get(i).delete();
            }
            for ( int i = 0; i < tempVariantInfoFiles.size(); i++ ) {
                this.bulkImport(schemaName, taskID, sampleIDs.get(i), DerbySNPImporter.VARIANT_INFO, tempVariantInfoFiles.get(i));
                tempVariantInfoFiles.get(i).delete();
            }

        }         
        catch (VCFException ex) {
            Logger.getLogger(DerbyVCFImporter.class ).error( ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(DerbyVCFImporter.class ).error( ex.getMessage());
        }
        finally {
            vcfReader.close();
        }
        return individualList;
    }
    
    private void writeGenotype( String sampleID, VCFRow row, VCFReader reader, BufferedWriter variantWriter, BufferedWriter infoWriter ) throws IOException {
        
        String chromosome = row.getChrom();
        int start = row.getPos();
        String id = row.getId();
        Alleles alleles = null;
                
        for ( int f = 0; f < row.getFormat().length; f++ ) {
            
            String formatName = row.getFormat()[f];
            String genotype = row.getGenotypes().get(sampleID)[f];

            //If the format is GT, write it to the list of variants
            if ( formatName.equals(GENOTYPE)) {
                alleles = extractAlleles( row.getRef(), row.getAlt(), genotype);
                
                HashMap<String, DataEntityAttributeValue> attributes = new HashMap<String, DataEntityAttributeValue>();
                attributes.put(DerbySNPImporter.ID, new DataEntityAttributeValue(id) );
                attributes.put(DerbySNPImporter.CHROMOSOME, new DataEntityAttributeValue(chromosome) );        
                attributes.put(DerbySNPImporter.SEQUENCE_START, new DataEntityAttributeValue(start) );   
                attributes.put(DerbySNPImporter.SEQUENCE_END, new DataEntityAttributeValue(start+alleles.getLength()) );          
                attributes.put(DerbySNPImporter.ALLELE, new DataEntityAttributeValue(alleles.toString()) );         
                attributes.put(DerbySNPImporter.STRAND, new DataEntityAttributeValue(-1) );    
                
                String tuple = createTuple( variantIndexes.get(sampleID), attributes, DerbySNPImporter.VARIANT );
                variantWriter.write(tuple);
                variantWriter.newLine();
                incrementIndex(variantIndexes, sampleID);
            }
            //Otherwise write it as additional variant related data
            else {
                
                HashMap<String, DataEntityAttributeValue> attributes = new HashMap<String, DataEntityAttributeValue>();
                attributes.put(DerbySNPImporter.VARIANT_ID, new DataEntityAttributeValue(variantIndexes.get(sampleID)) );      
                attributes.put(DerbySNPImporter.NAME, new DataEntityAttributeValue(formatName) );          
                attributes.put(DerbySNPImporter.VALUE, new DataEntityAttributeValue(genotype) );           
                
                String tuple = createTuple( variantInfoIndexes.get(sampleID), attributes, DerbySNPImporter.VARIANT_INFO );
                infoWriter.write(tuple);
                infoWriter.newLine();
                incrementIndex(variantInfoIndexes, sampleID);     
            }  
        }
    }

    private void writeVariantInfo( String sampleID, VCFRow row, VCFReader reader, BufferedWriter infoWriter ) throws IOException {
                
        for ( int f = 0; f < row.getInfo().length; f++ ) {
            
            String infoId = row.getInfo()[f][0];
            String infoValue = row.getInfo()[f][1];
            VCFInfo vcfInfo = reader.getInfo(infoId);

            HashMap<String, DataEntityAttributeValue> attributes = new HashMap<String, DataEntityAttributeValue>();
            attributes.put(DerbySNPImporter.VARIANT_ID, new DataEntityAttributeValue(variantIndexes.get(sampleID)) );

            //Flags have no value, therefore the name of the flag is used as a value of the flag.
            if ( vcfInfo.getType().equals( VCFEntry.FLAG)) {      
                attributes.put(DerbySNPImporter.NAME, new DataEntityAttributeValue(infoId) );          
                attributes.put(DerbySNPImporter.VALUE, new DataEntityAttributeValue(infoId) );        
            }
            //Other INFO entries are handled as normal key-value pairs.
            else {       
                attributes.put(DerbySNPImporter.NAME, new DataEntityAttributeValue(infoId) );          
                attributes.put(DerbySNPImporter.VALUE, new DataEntityAttributeValue(infoValue) ); 
            }
        
            String tuple = createTuple( variantInfoIndexes.get(sampleID), attributes, DerbySNPImporter.VARIANT_INFO );
            infoWriter.write(tuple);
            infoWriter.newLine();
            incrementIndex(variantInfoIndexes, sampleID);
 
        }
    }    
    
    private Alleles extractAlleles( String[]ref, String[] alt, String genotype) {
     
        String separator = "";
        String[] sequences = null;
        
        if ( genotype.contains("/") ) {
            separator = "/";
            sequences = genotype.split("/");
        }
        if ( genotype.contains("|") ) {
            separator = "|";
            sequences = genotype.split("\\|");
        }
        
        Alleles alleles = new Alleles(separator);
    
        for ( int i = 0; i < sequences.length; i++ ) {

            int alleleNumber = Integer.parseInt( sequences[i] );

            if (alleleNumber == 0) {
                alleles.addSequence(ref[0]);
            }
            else {   
                alleles.addSequence( decodeAllele( ref[0], alt[alleleNumber-1] ));      
            }
            
        }
        return alleles;
    }
    
    private String decodeAllele( String ref, String allele ) {
        
        String decoded = "";
        
        int refIndex = 0;
        int alleleIndex = 0;
        while ( alleleIndex < allele.length()  || refIndex < ref.length()) {
                               
            if ( refIndex >= ref.length()-1 && alleleIndex < allele.length() -1 ) {
                decoded = decoded.concat(allele.substring(alleleIndex, alleleIndex+1));
                alleleIndex++;
                continue;
            }
            
            if ( alleleIndex >= allele.length() -1 && refIndex < ref.length()-1) {
                decoded = decoded.concat(DELETED);
                refIndex++;
                alleleIndex++;
                continue;
            }
            if ( ( alleleIndex < allele.length()  && refIndex < ref.length()) && !ref.substring(refIndex, refIndex+1 ).equals(allele.substring(alleleIndex, alleleIndex+1)) ) {
                decoded = decoded.concat(allele.substring(alleleIndex, alleleIndex+1));
                alleleIndex++;     
                continue;
            }  
            alleleIndex++;
            refIndex++; 

        }
        return decoded;
    }
    
    class Alleles {
        private List<String> sequences = new ArrayList();
        private String phased = "";
        
        public Alleles( String phased) {
            this.phased = phased;
        }
        
        public void addSequence( String sequence ) {
            sequences.add(sequence);
        }
        
        public String toString() {
            String combined = "";
            for ( int i = 0; i < sequences.size(); i++) {
                combined = combined.concat(sequences.get(i));
                if ( i < sequences.size()-1) {
                    combined = combined.concat(phased);
                }
            }
            return combined;
        }
        
        public int getLength() {
            
            int max = 0;
            
            for ( String sequence : sequences ) {
                if ( sequence.length() > max ) {
                    max = sequence.length();
                }
            }
            return max;
        }
        
    }
    
    private void incrementIndex( Map<String,Integer> indexMap, String id) {
        Integer index = indexMap.get(id);
        indexMap.replace(id, index +1);
    }
       
    
}
