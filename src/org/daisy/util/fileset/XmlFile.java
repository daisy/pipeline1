/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public interface XmlFile extends FilesetFile, Referring {		
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> in the document, false otherwise
	 *@see #hasIDValueOnQName(String, String)
	 */
	public boolean hasIDValue(String idval);
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> on an element <code>qName</code> in the document, false otherwise
	 *@see #hasIDValue(String)
	 */
	public boolean hasIDValueOnQName(String idval, String qName);
	
	/**
	 *@return true if the document has been parsed and found wellformed; false if the document has been parsed and found malformed
	 *@throws FilesetException if the document has not been parsed
	 *@see #isParsed()
	 */
	public boolean isWellformed() throws FilesetException;
	
	/**
	 *@return true if the document has been parsed and found DTD valid; false if the document has been parsed and found DTD invalid
	 *@throws FilesetException if the document has not been parsed, or has been parsed but with validation disabled
	 *@see #isParsed()
	 */
	public boolean isDTDValid() throws FilesetException;
	
	/**
	 *@return true if the document has been parsed
	 *@see #isDTDValid()
	 *@see #isWellformed()
	 */
	public boolean isParsed();
	
	/**
	 *@return true if the document has been DTD validated, regardless of the result of the validation
	 *@see #isDTDValid()
	 *@see #isWellformed()
	 */
	public boolean isDTDValidated();
	
	/**
	 *@return this file as an {@link org.w3c.dom.Document} instance
	 */
	public Document asDocument() throws ParserConfigurationException, SAXException, IOException;
	
	/**
	 **@return this file as an {@link org.xml.sax.InputSource}
	 */
	public InputSource asInputSource() throws FileNotFoundException;	

	/**
	 **@return this file as a {@link javax.xml.transform.dom.DOMSource}
	 */
	public DOMSource asDOMSource() throws ParserConfigurationException, SAXException, IOException;	
	
	/**
	 **@return this file as a {@link javax.xml.transform.sax.SAXSource}
	 */
	public SAXSource asSAXSource() throws FileNotFoundException;	
	
	/**
	 **@return this file as a {@link javax.xml.transform.stream.StreamSource}
	 */
	public StreamSource asStreamSource() throws FileNotFoundException;
	
}
