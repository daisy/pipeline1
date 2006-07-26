package org.daisy.util.xml;

import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.xml.sax.SAXException;
/**
 * Peeks into an XML file, returns 
 * prolog and root information.
 * @author Markus Gylling
 */

public interface Peeker {
    public void peek(URI uri) throws SAXException, IOException;
    /**
     * @return the first encountered public id while parsing the file; that of the document entity
     */
    public String getFirstPublicId();
    /**
     * @return the first encountered system id while parsing the file; that of the document entity
     */
    public String getFirstSystemId();
    
    /**
     * @return the local name of the documents root element
     */
    public String getRootElementLocalName();

    /**
     * @return the namespace URI of the root element
     */
    public String getRootElementNsUri();

    /**
     * @return the root element represented as a QName object
     */
    public QName getRootElementQName();
    
    /**
     * @return the XML version as stated in the XML declaration pseudoattribute
     */ 
    public String getXMLVersion();
    /**
     * @return the encoding if stated in prolog, null if no encoding pseudoattr
     */
    public String getEncoding();
    
    /**
     * @return the standalone value as stated in the XML declaration pseudoattribute
     */ 
    public boolean getStandalone();
    
    
    public void reset();
}