package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Represents the Navigation Control Center (NCC) file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
class D202NccFileImpl extends Xhtml10FileImpl implements D202NccFile {
    
    D202NccFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
        super(uri);  
		parse();
    }
     
}
