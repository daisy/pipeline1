package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Represents the textual content file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */

public class D202TextualContentFile extends XHTML10File {
    
    public D202TextualContentFile(URI uri) throws ParserConfigurationException, SAXException, IOException {
        super(uri);            
    }
    
}