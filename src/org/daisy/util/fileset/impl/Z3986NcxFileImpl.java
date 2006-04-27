package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986NcxFile;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986NcxFileImpl extends XmlFileImpl implements Z3986NcxFile {
		
	Z3986NcxFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri,Z3986NcxFile.mimeStringConstant);
	}
	
	Z3986NcxFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, errh,Z3986NcxFile.mimeStringConstant);
	}
		
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {		
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason			
			if (attrName=="id") {
				this.putIdAndQName(attrValue,qName);
			}else if (attrName=="src") {
				this.putUriValue(attrValue);
			}			
		} //for (int i							
	}	
	
	private static final long serialVersionUID = -8034851986960529626L;
}
