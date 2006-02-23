/*
 * Created on 2006-feb-23
 */
package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * @author Markus Gylling
 */
class GifFileImpl extends ImageFileImpl implements GifFile {
	
	GifFileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri);
	}

	public void parse() {}

	public String getMimeType() {		
		return FilesetConstants.MIMETYPE_IMAGE_GIF;		
	}
}
