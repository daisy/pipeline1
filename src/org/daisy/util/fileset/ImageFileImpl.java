package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class ImageFileImpl extends FilesetFileImpl implements ImageFile {

    ImageFileImpl(URI uri) throws SAXException, IOException {
        super(uri);
    }    
}
