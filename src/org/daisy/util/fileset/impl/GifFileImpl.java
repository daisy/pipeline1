package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.GifFile;
import org.daisy.util.mime.MIMETypeException;

/**
 * @author Markus Gylling
 */
class GifFileImpl extends ImageFileImpl implements GifFile {

	GifFileImpl(URI uri) throws FileNotFoundException, IOException, MIMETypeException {
		super(uri,GifFile.mimeStringConstant);
	}

	public void parse() {}
	
	private static final long serialVersionUID = 2210226835509136820L;
}
