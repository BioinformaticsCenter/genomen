package com.genomen.core.importers.derby;

import com.genomen.core.Configuration;
import com.genomen.core.importers.derby.DerbySNPImporter;
import com.genomen.core.Individual;
import com.genomen.core.entities.DataEntityAttributeValue;
import com.genomen.core.importers.Importer;
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
    public List<Individual> importDataSet( String schemaName, String taskID, String individualID, String[] fileNames ) {

        List<Individual> individualList = new LinkedList<Individual>();        
        
        if ( fileNames.length != 1 ) {
            return individualList;
        }         
        
        File file = new File(fileNames[0]);
        


        //Create Individual for the data presented in the file
        Individual individual = new Individual(individualID);
        individualList.add(individual);
        if ( !file.exists() || !file.canRead() ) {
            Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).error( "Unable to open file" + file.getAbsolutePath() );
            return individualList;
        }
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {

            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader( fileReader );

            File tempFile = new File( Configuration.getConfiguration().getTmpFolderPath() + taskID.concat(individualID).concat(TEMP_FILE_NAME));
            bufferedWriter = new BufferedWriter( new FileWriter(tempFile));

            String line;



            while ( ( line = bufferedReader.readLine() ) != null )  {

               if ( line.matches(FORMAT_REGEXP) ) {

                    String tuple = createTuple( individualID, extractSNPData(line) );
                    bufferedWriter.write(tuple);
                    bufferedWriter.newLine();

               }
               else if ( !line.startsWith("#")) {
                    Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).error( "Corrupted line in " + file.getPath() + " : " + line );
               }

            }
            
            ResourceReleaser.close(bufferedWriter);
            this.bulkImport(schemaName, taskID, getType(), tempFile);
            tempFile.delete();
        }
 
        catch (FileNotFoundException ex) {
            Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).debug(ex);
        }        catch ( IOException ex ) {
            Logger.getLogger(DerbyTwentyThreeandMeImporter.class ).debug(ex);
        }
        finally {
            ResourceReleaser.close(bufferedReader);
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
        attributes.put(DerbySNPImporter.SEQUENCE_END, new DataEntityAttributeValue(start+1) );          
        attributes.put(DerbySNPImporter.ALLELE, new DataEntityAttributeValue(allele) );         
        attributes.put(DerbySNPImporter.STRAND, new DataEntityAttributeValue(-1) );     
        
        return attributes;
        
    }

}
