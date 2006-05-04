package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.BmpFile;

/**
 * @author Markus Gylling
 */
final class BmpFileImpl extends ImageFileImpl implements BmpFile {

	BmpFileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri, BmpFile.mimeStringConstant);
	}

	public void parse() {}

	private static final long serialVersionUID = -8955821974643327349L;
	
}
