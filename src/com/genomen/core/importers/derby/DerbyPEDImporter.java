
package com.genomen.core.importers.derby;

import com.genomen.core.importers.derby.DerbySNPImporter;
import com.genomen.dao.DerbyDAOFactory;
import com.genomen.core.entities.DataEntityAttributeValue;
import com.genomen.core.Individual;
import com.genomen.core.importers.Importer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import com.genomen.utils.ResourceReleaser;

/**
 * Importer for PED files.
 * @author ciszek
 */
public class DerbyPEDImporter extends DerbySNPImporter implements Importer {

    private static final String PED_ENDING = "ped";
    private static final String MAP_ENDING = "map";    
    private static final String TEMP_FILE_NAME = "ped.tmp";
    
    public List<Individual> importDataSet( String schemaName, String tableName, String individualID, String[] fileNames ) {

        List<Individual> individualList = new LinkedList<Individual>();

        String PEDFileName = findFile( fileNames, PED_ENDING );
        String MAPFileName = findFile( fileNames, MAP_ENDING );
        
        if ( PEDFileName == null || MAPFileName == null ) {
            return individualList;
        }
        
        File PEDFile = new File( PEDFileName );
        File MAPFile = new File( MAPFileName );

        BufferedReader bufferedReader = null;
        StreamTokenizer PEDTokenizer = null;


        try {
            bufferedReader = new BufferedReader(new FileReader(PEDFile));
            PEDTokenizer = new StreamTokenizer(bufferedReader);
            PEDTokenizer.eolIsSignificant(true);


            while ( PEDTokenizer.nextToken() != StreamTokenizer.TT_EOF) {

                long beforeTime = System.currentTimeMillis();
                Individual individual = parseIndividual(PEDTokenizer);
                individualList.add(individual);

                importSNPs( PEDTokenizer, MAPFile, individual, schemaName, tableName );

                long deltaTime = (System.currentTimeMillis() - beforeTime ) / 1000;
                System.out.println(individual.getId() + " " + deltaTime + "s" );

            }

        }
        catch (FileNotFoundException ex) {
            Logger.getLogger( this.getClass() ).error( ex );
        }
        catch (IOException ex) {
            Logger.getLogger( this.getClass() ).error( ex );
        }
        finally {
            ResourceReleaser.close(bufferedReader);

        }
        return individualList;
    }

    private Individual parseIndividual( StreamTokenizer tokenizer ) {

        String familyID = "";
        String individualID = "";
        String paternalID = "";
        String maternalID = "";
        String sex = "";
        String phenotype = tokenizer.sval;

        try {
            familyID = tokenizer.sval;
            tokenizer.nextToken();
            individualID = String.valueOf( tokenizer.nval );
            tokenizer.nextToken();
            paternalID = String.valueOf( tokenizer.nval );
            tokenizer.nextToken();
            maternalID = String.valueOf( tokenizer.nval );
            tokenizer.nextToken();
            sex = String.valueOf( tokenizer.nval );
            tokenizer.nextToken();
            phenotype = String.valueOf( tokenizer.nval );
        }
        catch (IOException ex) {
            Logger.getLogger( this.getClass() ).error( ex );
        }
        return new Individual(individualID, familyID);
    }

    private void importSNPs( StreamTokenizer PEDTokenizer, File MAPFile, Individual individual, String schemaName, String tableName ) {

        BufferedReader MAPReader = null;
        BufferedWriter bufferedWriter = null;
        try {

            File temp = new File(individual.getId() + TEMP_FILE_NAME);

            bufferedWriter = new BufferedWriter( new FileWriter(temp) );

            MAPReader = new BufferedReader(new FileReader(MAPFile));
            StreamTokenizer MAPTokenizer = new StreamTokenizer(MAPReader);
            MAPTokenizer.wordChars( '_','_' );

            Connection connection = null;

            try {
                connection = DerbyDAOFactory.createConnection();
            }
            catch (Exception ex) {
                return;
            }
            
            String[] alleles = new String[2];

            int index = 0;
            while ( PEDTokenizer.nextToken() != StreamTokenizer.TT_EOL  ) {

                if ( PEDTokenizer.ttype == StreamTokenizer.TT_NUMBER ) {
                    alleles[index] = String.valueOf( PEDTokenizer.nval);
                }
                else {
                    alleles[index] = PEDTokenizer.sval;
                }
                index++;
                if ( index == 2) {

                    String line = createTuple(individual.getId(), createSNP(MAPTokenizer,alleles[0], alleles[1]));
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    index = 0;
                }
            }
            ResourceReleaser.close(bufferedWriter);
            this.bulkImport(schemaName, tableName, getType(), temp);
            temp.delete();
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger( this.getClass() ).error( ex );
        }
        catch (IOException ex) {
            Logger.getLogger( this.getClass() ).error( ex );
        }
        finally {

            ResourceReleaser.close(MAPReader);
        }

    }


    private HashMap<String, DataEntityAttributeValue> createSNP( StreamTokenizer MAPTokenizer, String firstStrand, String secondStrand ) throws IOException {

        MAPTokenizer.nextToken();
        String chromosome = String.valueOf(MAPTokenizer.nval);
        MAPTokenizer.nextToken();
        String snpID = MAPTokenizer.sval;
        MAPTokenizer.nextToken();
        String geneticDistance = String.valueOf(MAPTokenizer.nval);
        MAPTokenizer.nextToken();
        int start = (int) MAPTokenizer.nval;

        String allele = firstStrand.concat("/").concat(secondStrand);
        int end = start + Math.max( firstStrand.length(), secondStrand.length() );
        
        HashMap<String, DataEntityAttributeValue> attributes = new HashMap<String, DataEntityAttributeValue>();
        
        attributes.put(DerbySNPImporter.ID, new DataEntityAttributeValue(snpID) );
        attributes.put(DerbySNPImporter.CHROMOSOME, new DataEntityAttributeValue(chromosome) );        
        attributes.put(DerbySNPImporter.SEQUENCE_START, new DataEntityAttributeValue(start) );   
        attributes.put(DerbySNPImporter.SEQUENCE_END, new DataEntityAttributeValue(end) );          
        attributes.put(DerbySNPImporter.ALLELE, new DataEntityAttributeValue(allele) );         
        attributes.put(DerbySNPImporter.STRAND, new DataEntityAttributeValue(-1) );          
        
        return attributes;

    }

}