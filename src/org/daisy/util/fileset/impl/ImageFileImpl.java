package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.ImageFile;
import org.daisy.util.mime.MIMETypeException;

/**
 * @author Markus Gylling
 */
abstract class ImageFileImpl extends FilesetFileImpl implements ImageFile {

    ImageFileImpl(URI uri, String mimeStringConstant) throws FileNotFoundException, IOException, MIMETypeException {
        super(uri,mimeStringConstant);
    }

	public void parse(){}

}
