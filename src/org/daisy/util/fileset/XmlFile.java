/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
	public Document getDocument() throws ParserConfigurationException, SAXException, IOException;
}
