package org.daisy.util.xml;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;

/**
 * @author Markus Gylling
 */
public class XMLUtils {

	/**
	 * Delegate for XSI retrieval from a SAX StartElement event.
	 * @return a Set of Strings (equalling the unaltered xsd uris), or an empty set
	 * if no XSI information was found in this StartElement. Parameters are a pure echo 
	 * of those fed to the SAX StartElement event. 
	 */
	public static final Set getXSISchemaLocationURIs(String uri, String localName, String qName, Attributes atts) {
		Set set = new HashSet();
		String xsi = null;
		   
		//we dont know how namespace handling is configured in SAX parser, 
		//so need to try all alternatives.
        xsi = atts.getValue("http://www.w3.org/2001/XMLSchema-instance","noNamespaceSchemaLocation");
        if(xsi==null) {	
        	xsi = atts.getValue("xsi:noNamespaceSchemaLocation");
        	if(xsi==null) {	
        		xsi = atts.getValue("noNamespaceSchemaLocation");
        	}
        }                
        if(xsi!=null) set.add(xsi);
        
        
        xsi=null;        
        xsi = atts.getValue("http://www.w3.org/2001/XMLSchema-instance","schemaLocation");
        if(xsi==null) {	
        	xsi = atts.getValue("xsi:schemaLocation");
        	if(xsi==null) {	
        		xsi = atts.getValue("schemaLocation");
        	}
        }       
        if(xsi!=null) {
        	String[] array = xsi.split(" ");
        	for (int i = 0; i < array.length; i++) {
        		//get only uneven				
        		if(i % 2 != 0) {
        			set.add(array[i]);
        		}        		
			}        	
        }
        
        return set;
	}
	
}
