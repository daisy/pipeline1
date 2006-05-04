package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.ImageFile;

/**
 * @author Markus Gylling
 */
abstract class ImageFileImpl extends FilesetFileImpl implements ImageFile {

    ImageFileImpl(URI uri, String mimeStringConstant) throws FileNotFoundException, IOException {
        super(uri,mimeStringConstant);
    }

	public void parse(){}

}
