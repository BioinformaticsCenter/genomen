package com.genomen.entities;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import com.genomen.utils.DOMDocumentCreator;
import com.genomen.core.Error;
import com.genomen.core.Error.ErrorType;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Reads datatype definitions from an XML file.
 * @author ciszek
 */
public class DataTypeReader {
    
    private static final String DATA_TYPE_TAG = "dataType";
    private static final String ID_TAG = "id";
    private static final String ATTRIBUTE_TAG = "attribute";
    private static final String ATTRIBUTE_NAME_NAME = "name";
    private static final String ATTRIBUTE_TYPE_NAME = "type";    
    private static final String ATTRIBUTE_SIZE_NAME = "size";        
    private static final String ATTRIBUTE_REQUIRED_NAME = "required";  
    
    
    /**
     * Provides a <code>Map</code> of different types of data specified in the
     * XML file given as a parameter.
     * @param filePath path to the XML containing datatype definitions
     * @return a <code>Map</code> of datatype names mapped to instances of <code>Datatype</code>
     */
    public static Map<String, DataType> readDataTypes( String filePath ) {
        
        HashMap<String, DataType> dataTypeMap = new HashMap<String, DataType>();
        
        Document document = DOMDocumentCreator.createDocument(filePath);
        
        if ( document == null ) {
            Logger.getLogger( DataTypeReader.class ).debug(Error.getMessage( ErrorType.FILE_NOT_FOUND, filePath));
            return dataTypeMap;
        }        
        
        Element rootNode = document.getDocumentElement();
        NodeList dataTypeList = rootNode.getElementsByTagName(DATA_TYPE_TAG);    
        
        for ( int dataTypeIndex = 0; dataTypeIndex < dataTypeList.getLength(); dataTypeIndex++ ) {
            
            String id = "";

            
            Element dataTypeElement = (Element)dataTypeList.item(dataTypeIndex);                   
            NodeList idNode = dataTypeElement.getElementsByTagName(ID_TAG);
            id = idNode.item(0).getFirstChild().getNodeValue();

            //Create a hashmap for attributes
            HashMap<String, DataTypeAttribute> attributes = new HashMap<String, DataTypeAttribute>();   
            
            NodeList attributeList = dataTypeElement.getElementsByTagName(ATTRIBUTE_TAG);   
            //Loop through the values and add them as attri
            for ( int attributeIndex = 0; attributeIndex < attributeList.getLength(); attributeIndex++ ) {

                Element valueElement = (Element)attributeList.item(attributeIndex);
                String name = valueElement.getAttribute(ATTRIBUTE_NAME_NAME);
                String type = valueElement.getAttribute(ATTRIBUTE_TYPE_NAME);
                String valueRequired = valueElement.getAttribute(ATTRIBUTE_REQUIRED_NAME);
                int size = 0;
                boolean required = false;
                
                if ( valueRequired.equalsIgnoreCase("true") ) {
                    required = true;
                }

                if ( !valueElement.getAttribute(ATTRIBUTE_SIZE_NAME).equals("") ) {
                    size = Integer.parseInt(valueElement.getAttribute(ATTRIBUTE_SIZE_NAME));
                }
                
                DataTypeAttribute dataTypeAttribute = new DataTypeAttribute( name, type, size, required );
                attributes.put(name, dataTypeAttribute);
            }
            
            DataType dataType = new DataType(id,attributes);
            dataTypeMap.put(id, dataType);
            
        }
        
        
        return dataTypeMap;
    }
    
}
