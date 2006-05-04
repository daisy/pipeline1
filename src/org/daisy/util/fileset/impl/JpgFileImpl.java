package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.JpgFile;

/**
 * @author Markus Gylling
 */
final class JpgFileImpl extends ImageFileImpl implements JpgFile {

	JpgFileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri, JpgFile.mimeStringConstant);
	}

	public void parse() {}
	
	private static final long serialVersionUID = 8741812017180142590L;
}
