package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986DtbookFileImpl extends XmlFileImpl implements Z3986DtbookFile, ManifestFile {
	
	Z3986DtbookFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri);		
	}
	
	Z3986DtbookFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, errh);		
	}
	
	public String getMimeType() {
      return FilesetConstants.MIMETYPE_DTBOOK_Z2005;
	}

	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {
//			attrName = attrs.getQName(i).intern();
//			attrValue = attrs.getValue(i).intern();
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();
						
			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			} else if (regex.matches(regex.DTBOOK_ATTRIBUTES_WITH_URIS,attrName)) {
			   putUriValue(attrValue);
			}									
		}//for (int i
	}//startElement

}
