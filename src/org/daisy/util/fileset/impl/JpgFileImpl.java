package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.JpgFile;
import org.daisy.util.mime.MIMETypeException;

/**
 * @author Markus Gylling
 */
class JpgFileImpl extends ImageFileImpl implements JpgFile {

	JpgFileImpl(URI uri) throws FileNotFoundException, IOException, MIMETypeException {
		super(uri, JpgFile.mimeStringConstant);
	}

	public void parse() {}
	
	private static final long serialVersionUID = 8741812017180142590L;
}
