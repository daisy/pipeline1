package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public class Mp3FileImpl extends AudioFile implements Mp3File {

    Mp3FileImpl(URI uri) throws SAXException, IOException {
        super(uri);
    }    
}
