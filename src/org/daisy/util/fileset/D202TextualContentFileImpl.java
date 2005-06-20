package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Represents the textual content file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */

class D202TextualContentFileImpl extends Xhtml10FileImpl implements TextualContentFile  {
    
    D202TextualContentFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
        super(uri);          
    }

    D202TextualContentFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
        super(uri, errh);  
    }
    
}