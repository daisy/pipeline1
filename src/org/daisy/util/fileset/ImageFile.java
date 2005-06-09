package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public class ImageFile extends AbstractFile {

    public ImageFile(URI uri) throws SAXException, IOException {
        super(uri);
    }    
}
