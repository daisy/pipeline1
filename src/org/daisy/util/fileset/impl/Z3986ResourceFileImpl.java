package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986ResourceFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986ResourceFileImpl extends XmlFileImpl implements Z3986ResourceFile {
	
	Z3986ResourceFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri,Z3986ResourceFile.mimeStringConstant);		
	}
	
	Z3986ResourceFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, errh,Z3986ResourceFile.mimeStringConstant);		
	}
		
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();		
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();	
			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			}else if (attrName=="src") {
				putUriValue(attrValue);
			}					
		} //for (int i							
	}
}
