package com.genomen.utils.database;

import com.genomen.dao.ContentDAO;
import com.genomen.dao.DAOFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.genomen.core.Error;
import com.genomen.core.Error.ErrorType;
import java.util.LinkedList;
import com.genomen.utils.StringUtils;

/**
 * Imports data from XML file into the database
 * @author ciszek
 */
public class XMLImporter {


    private HashMap< String, List<Tuple> > tupleListMap = new HashMap< String, List<Tuple> >();
    private DatabaseGraph databaseGraph;
    private LinkedList<String> tupleNameList;

    /**
     * Imports the data presented as a XML file to the database.
     * @param xmlFilePath location of the XML file
     * @param schemaName schema into which the data is to be imported.
     */
    public synchronized void importToDatabase( String xmlFilePath, String schemaName ) {

        //Create a graph of the database structure
        databaseGraph = DatabaseGraphBuilder.buildDatabaseGraph(schemaName);
        //Store all tables listed in the XML file into lists and place the list into a HashMap
        tablesToMap( xmlFilePath );
        //Create a list of all unique table names present in the XML file
        tupleNameList = new LinkedList<String>( tupleListMap.keySet() );
  
        while ( !tupleNameList.isEmpty() ) {

            String tableCurrentlyInserted = tupleNameList.getFirst();
            TableNode tableNode = databaseGraph.getTableNode(tableCurrentlyInserted);

            //If the table being inserted does not exist in the database graph
            if ( tableNode == null) {
                ErrorManager.reportError("ERROR: Table " + tableCurrentlyInserted + " does not exist in the database" );
            }

            insertTables(tableNode);
        }
    }

    //Creates a list of Tuples for each unique table in the XML file and stores the lists to a HashMap
    private void tablesToMap( String xmlFilePath ) {

        Document importedDocument = createDocument( xmlFilePath );

        if ( importedDocument == null ) {
            Logger.getLogger( XMLImporter.class ).debug(Error.getMessage( ErrorType.FILE_NOT_FOUND, "TST"));
            return;
        }

        NodeList nodeList = importedDocument.getDocumentElement().getChildNodes();

        for ( int i = 0; i < nodeList.getLength(); i++) {

            if ( nodeList.item(i).getNodeType() != Node.TEXT_NODE ) {


                    if ( databaseGraph.getTableNode( nodeList.item(i).getNodeName() ) == null ) {
                        ErrorManager.reportError("ERROR: Table " + nodeList.item(i).getNodeName() + " does not exist" );
                        continue;
                    }

                    Tuple tuple = createTuple( nodeList.item(i) );
                    correctValueSyntax(tuple);
                    addTableToMap( tuple );
            }

        }

    }
    
    //Creates an instance of a Document from the XML file
    private Document createDocument( String xmlFilePath ) {


        File importedFile = new File(xmlFilePath);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(importedFile);
        }
        catch (ParserConfigurationException ex) {
            Logger.getLogger( XMLImporter.class ).debug(ex);
        }
        catch (IOException ex ) {
            Logger.getLogger( XMLImporter.class ).debug(ex);
        }
        catch( SAXException ex ) {
            Logger.getLogger( XMLImporter.class ).debug(ex);
        }

        if (importedFile.exists() && importedFile.canRead()) {
            document.normalize();

        }
        else {
            Logger.getLogger( XMLImporter.class).error( Error.getMessage( ErrorType.FILE_NOT_FOUND, xmlFilePath) );
        }

        return document;

    }
    
    //Creates an instance of a Tuple
    private Tuple createTuple( Node node ) {

        String tableName = node.getNodeName();
        String schemaName = databaseGraph.getTableNode(tableName).getSchemaName();
        List<Attribute> attributes = new ArrayList<Attribute>();
        NodeList nodeList = node.getChildNodes();

        for ( int i = 0; i < nodeList.getLength(); i++ ) {

            if ( nodeList.item(i).getNodeType() != Node.TEXT_NODE ) {
                String attributeName = nodeList.item(i).getNodeName();
                String attributeValue = "";

                if ( nodeList.item(i).getFirstChild() != null) {
                    attributeValue = nodeList.item(i).getFirstChild().getNodeValue();
                }

                attributes.add( new Attribute( attributeName, attributeValue ) );
            }
        }

        return new Tuple( tableName, schemaName, attributes );
    }

    //Adds a tuple to a correct list in the tupleListMap
    private void addTableToMap( Tuple table ) {

        if ( !tupleListMap.containsKey( table.getTableName() ) ) {
            List<Tuple> tableList = new ArrayList<Tuple>();
            tupleListMap.put( table.getTableName(), tableList );
        }

        tupleListMap.get( table.getTableName() ).add(table);

    }
    
    private void insertTables(  TableNode tableNode ) {

        //Insert the table if the table is not already processed.
        if ( !tableNode.isProcessed() ) {

            for ( int i = 0; i < tableNode.getReferedTables().size(); i++ ) {

                if ( !tableNode.getReferedTables().get(i).isProcessed()) {

                    insertTables( tableNode.getReferedTables().get(i) );
                }
            }

            String tableName = tableNode.getName();

            if ( tupleListMap.get(tableName) != null ) {

                insertAllTuples(tableName);
            }
            databaseGraph.getTableNode(tableName).setProcessed(true);
            tupleNameList.remove( tableNode.getName() );
        }
    }
    
    //Inserts all tuples of a certain relation into the database.
    private void insertAllTuples( String tableName ) {

        List<Tuple> tupleList = tupleListMap.get(tableName);
        ContentDAO contentDAO = DAOFactory.getDAOFactory().getContentDAO();

        if ( tupleList != null ) {
            for ( int i = 0; i < tupleList.size(); i++) {
                Tuple tuple = tupleList.get(i);
                contentDAO.addTuple(tuple.getSchemaName(), tuple.getTableName(), tuple.getAttributeNames(), tuple.getValues());
          
            }
        }
    }

    //Corrects the syntax of the values inserted by adding '' around textual data and escaping quotation marks.
    private void correctValueSyntax( Tuple tuple) {

        for ( int i = 0; i < tuple.getAttributes().size(); i++ ) {
            //Escape quotation marks
            Attribute attribute = tuple.getAttributes().get(i);
            attribute.setValue(attribute.getValue().replaceAll("'", "''"));
            try {
                //Force encoding
                attribute.setValue(StringUtils.forceEncoding(attribute.getValue(), "UTF-8"));
            }
            catch (CharacterCodingException ex) {
                ErrorManager.reportError(ex.getMessage());
            }

            TableNode tableNode = databaseGraph.getTableNode( tuple.getTableName() );
            //If attribute is textual
            if ( !tableNode.isNumeric( tuple.getAttributes().get(i).getAttributeName() ) ) {
                //Add quotation marks
                String correctedValue = "'".concat(tuple.getAttributes().get(i).getValue()).concat("'");
                tuple.getAttributes().get(i).setValue(correctedValue);               
            }
        }
    }


    private class Tuple {

        private String tableName;
        private String schemaName;
        private List<Attribute> attributes = new ArrayList<Attribute>();

        public Tuple( String p_tableName, String p_schemaName, List<Attribute> p_attributes ) {
            tableName = p_tableName;
            schemaName = p_schemaName;
            attributes = p_attributes;
        }


        public String getTableName() {
            return tableName;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public String[] getAttributeNames() {

            List<String> attributeNames = new ArrayList<String>();
            for ( int i = 0; i < attributes.size(); i++) {
                attributeNames.add(attributes.get(i).getAttributeName());

            }
            return attributeNames.toArray( new String[0]);
        }

        public String[] getValues() {

            List<String> values = new ArrayList<String>();
            for ( int i = 0; i < attributes.size(); i++) {
                values.add(attributes.get(i).getValue());

            }
            return values.toArray( new String[0]);
        }
    }

    
    private class Attribute {

        private String attributeName;
        private String value;

        public Attribute( String p_attributeName, String p_value ) {

            attributeName = p_attributeName;
            value = p_value;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public String getValue() {
            return value;
        }

        public void setValue( String p_value ) {
            value = p_value;
        }

    }
  
}
