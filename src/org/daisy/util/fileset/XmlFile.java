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
	public boolean hasIDValue(String value);	
	public boolean isWellformed() throws FilesetException;
	public boolean isDTDValid() throws FilesetException;
	public boolean isParsed();
	public boolean isDTDValidated();
	public Document asDocument() throws ParserConfigurationException, SAXException, IOException;
	public InputSource asInputSource() throws FileNotFoundException;	
	public DOMSource asDOMSource() throws ParserConfigurationException, SAXException, IOException;	
	public SAXSource asSAXSource() throws FileNotFoundException;	
	public StreamSource asStreamSource() throws FileNotFoundException;
	
}
