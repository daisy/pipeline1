package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986ResourceFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
final class Z3986ResourceFileImpl extends XmlFileImpl implements Z3986ResourceFile {
		
	Z3986ResourceFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri,Z3986ResourceFile.mimeStringConstant);		
	}
			
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();		
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();	
			if (attrName=="id") {
				QName q = new QName(namespaceURI,sName);
				this.putIdAndQName(attrValue,q);				
			}else if (attrName=="src") {
				putUriValue(attrValue);
			}					
		} //for (int i							
	}
	
	private static final long serialVersionUID = -7996003750752610422L;
}
