package com.genomen.importers.derby;

import com.genomen.core.Configuration;
import com.genomen.importers.derby.DerbySNPImporter;
import com.genomen.core.Sample;
import com.genomen.entities.DataEntityAttributeValue;
import com.genomen.importers.Importer;
import com.genomen.importers.ImporterException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import com.genomen.utils.ResourceReleaser;
import java.util.ArrayList;


/** 
 * Importer for SNP data stored in the format used by 23AndMe.
 * @author ciszek
 */
public class DerbyTwentyThreeandMeImporter extends DerbySNPImporter implements Importer {

    private static final String FORMAT_REGEXP = "^\\w+\\s\\w+\\s\\d+\\s[\\w-]+";
    private static final String COLUMN_SEPARATOR = "\\s";
    private static final String TEMP_FILE_NAME = "23AndMe.temp";
    private static final String TWENTYTHREEANDME_ENDING = "txt";


    @Override
    public List<Sample> importDataSet( String schemaName, String individualID, String[] fileNames ) throws ImporterException {

        List<Sample> individualList = new LinkedList<Sample>();        
        
        if ( fileNames.length != 1 ) {
            return individualList;
        }         
        
        File file = new File(fileNames[0]);
        


        //Create Individual for the data presented in the file
        Sample individual = new Sample(individualID);  
        individualList.add(individual);
        
        //Add individual to database
        List<String> individualIDs = new ArrayList<String>();
        individualIDs.add(individualID);
        //If an individual with the same id already exists or database command can not be established, abort process.
        if ( !insertIndividuals( individualIDs ) ) {
            throw new ImporterException(  ImporterException.INDIVIDUAL_ID_ERROR, individualID );
        }
        
        if ( !file.exists() || !file.canRead() ) {
            throw new ImporterException(  ImporterException.UNABLE_TO_READ_DATASET, file.getName() );
        }
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        
        File tempFile = null;
        try {

            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader( fileReader );

            tempFile = new File( Configuration.getConfiguration().getTmpFolderPath() + individualID.concat(TEMP_FILE_NAME));
            tempFile.getParentFile().mkdirs();
            bufferedWriter = new BufferedWriter( new FileWriter(tempFile));

            String line;

            int id = getCurrentId( individualID, DerbySNPImporter.VARIANT);   
            
            if ( id == DerbyImporter.INVALID_ID) {
                throw new ImporterException( ImporterException.DATA_TABLE_INDEX_ERROR, DerbySNPImporter.VARIANT);
            }

            while ( ( line = bufferedReader.readLine() ) != null )  {

               if ( line.matches(FORMAT_REGEXP) ) {

                    String tuple = createTuple( id, extractSNPData(line), DerbySNPImporter.VARIANT );
                    bufferedWriter.write(tuple);
                    bufferedWriter.newLine();
                    id++;
               }
               else if ( !line.startsWith("#")) {
                    Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).error( "Corrupted line in " + file.getPath() + " : " + line );
               }

            }
            
            ResourceReleaser.close(bufferedWriter);
            this.bulkImport(schemaName, individualID,DerbySNPImporter.VARIANT, tempFile);
            tempFile.delete();
        }
 
        catch (FileNotFoundException ex) {
            Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).debug(ex);
            throw new ImporterException( ImporterException.UNABLE_TO_READ_DATASET);
        }        
        catch ( IOException ex ) {
            Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).debug(ex);
            throw new ImporterException( ImporterException.TEMP_FILE_ERROR);
        }
        finally {
            ResourceReleaser.close(bufferedReader);
            if ( tempFile != null && tempFile.exists() ) {
                tempFile.delete();
            } 
        }
        
        
        

        return individualList;
    }
    
    private HashMap<String, DataEntityAttributeValue> extractSNPData( String line ) {

        String[] columns = line.split(COLUMN_SEPARATOR);

        if ( columns.length != 4 ) {
            return null;
        }

        HashMap<String, DataEntityAttributeValue> attributes = new HashMap<String, DataEntityAttributeValue>();
        
        String id = columns[0];
        String chromosome = columns[1];
        int start = Integer.parseInt(columns[2]);
        String allele = "";
        allele = allele.concat( String.valueOf( columns[3].charAt(0)));
        allele = allele.concat("/");

        if (columns[3].length() > 1 ) {
            allele = allele.concat( String.valueOf( columns[3].charAt(1)));
        }
        else {
            allele = allele.concat("-");
        }
        
        attributes.put(DerbySNPImporter.ID, new DataEntityAttributeValue(id) );
        attributes.put(DerbySNPImporter.CHROMOSOME, new DataEntityAttributeValue(chromosome) );        
        attributes.put(DerbySNPImporter.SEQUENCE_START, new DataEntityAttributeValue(start) );           
        attributes.put(DerbySNPImporter.ALLELE, new DataEntityAttributeValue(allele) );         
        attributes.put(DerbySNPImporter.STRAND, new DataEntityAttributeValue(-1) );     
        
        return attributes;
        
    }

}
