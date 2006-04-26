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
	 * @return the first encountered public id while parsing the file
	 */
	public String getFirstPublicId();
	/**
	 * @return the first encountered system id while parsing the file
	 */
	public String getFirstSystemId();
	public String getRootElementLocalName();
	public String getRootElementNsUri();
	public QName getRootElementQName();	
	public String getXMLVersion();
	/**
	 * @return the encoding if stated in prolog, null if no encoding pseudoattr
	 */
	public String getEncoding();
	public boolean getStandalone();
	public void reset();
}