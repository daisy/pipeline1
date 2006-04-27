package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.d202.D202TextualContentFile;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Represents the textual content file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */

class D202TextualContentFileImpl extends Xhtml10FileImpl implements D202TextualContentFile  {

	D202TextualContentFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
        super(uri,D202TextualContentFile.mimeStringConstant);          
    }

    D202TextualContentFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
        super(uri, errh,D202TextualContentFile.mimeStringConstant);  
    }
    	
	private static final long serialVersionUID = 4658421573363931119L;
}