package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986NcxFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
final class Z3986NcxFileImpl extends XmlFileImpl implements Z3986NcxFile {
		
	Z3986NcxFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri,Z3986NcxFile.mimeStringConstant);
	}
		
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {		
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason			
			if (attrName=="id") {
				QName q = new QName(namespaceURI,sName);
				this.putIdAndQName(attrValue,q);				
			}else if (attrName=="src") {
				this.putUriValue(attrValue);
			}			
		} //for (int i							
	}	
	
	private static final long serialVersionUID = -8034851986960529626L;
}
