package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * @author Markus Gylling
 */

abstract class AudioFileImpl extends FilesetFileImpl {

    AudioFileImpl(URI uri, String mimeStringConstant) throws FileNotFoundException, IOException {
        super(uri,mimeStringConstant);
    }
    
}
