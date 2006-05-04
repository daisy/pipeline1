package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.d202.D202TextualContentFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Represents the textual content file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */

final class D202TextualContentFileImpl extends Xhtml10FileImpl implements D202TextualContentFile  {

	D202TextualContentFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
        super(uri,D202TextualContentFile.mimeStringConstant);          
    }
    	
	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		//override the XmlFileImpl method in order to substitute DTDs
		//from xhtml to the subset one
		if (publicId.startsWith("-//W3C//DTD XHTML")) {
			publicId = "-//DAISY//DTD content v2.02//EN";
		}	
		return super.resolveEntity(publicId,systemId);
	}
    
	private static final long serialVersionUID = 4658421573363931119L;
	
}