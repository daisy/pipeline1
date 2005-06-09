package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */

abstract class AudioFile extends AbstractFile {

    public AudioFile(URI uri) throws SAXException, IOException {
        super(uri);
    }
    
}
