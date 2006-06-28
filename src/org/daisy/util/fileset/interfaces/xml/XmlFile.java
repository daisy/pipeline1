package org.daisy.util.fileset.interfaces.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.mime.MIMEConstants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public interface XmlFile extends FilesetFile, Referring {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_XML;

	/**
	 *@return a collection&lt;String&gt; of all xml:lang values in this XML document. 
	 * If no xml:lang values exist in this document, the return will
	 * be an emtpy list, not null. The collection contains only unique items, and
	 * values are the untampered-with values of the xml:lang attribute.
	 */
	public Collection getXmlLangValues();
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> in the document, false otherwise
	 *@see #hasIDValueOnQName(String, QName)
	 */
	public boolean hasIDValue(String idval);
	
	/**
	 *@return true if <code>idval</code> exists as the value of an attribute named <code>id</code> on an element <code>qName</code> in the document, false otherwise
	 *@see #hasIDValue(String)
	 */
	public boolean hasIDValueOnQName(String idval, QName qName);
	
	/**
	 *@return true if the document has been parsed and found wellformed; false if the document has been parsed and found malformed
	 *@throws IllegalStateException if the document has not been parsed
	 *@see #isParsed()
	 */
	public boolean isWellformed() throws IllegalStateException;
	
	/**
	 *@return true if the document has been parsed and found DTD valid; false if the document has been parsed and found DTD invalid
	 *@throws IllegalStateException if the document has not been parsed, or has been parsed but with validation disabled
	 *@see #isParsed()
	 */
	public boolean isDTDValid() throws IllegalStateException;
		
	/**
	 *@return true if the document has been DTD validated, regardless of the result of the validation
	 *@see #isDTDValid()
	 *@see #isWellformed()
	 */
	public boolean isDTDValidated();
	
	/**
	 *@return this file as an {@link org.w3c.dom.Document} instance
	 */
	public Document asDocument(boolean namespaceAware) throws ParserConfigurationException, SAXException, IOException;
	
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
