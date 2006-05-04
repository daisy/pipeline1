package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.AnonymousFile;

/**
 * @author Markus Gylling
 */
final class AnonymousFileImpl extends FilesetFileImpl {

	public AnonymousFileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri, AnonymousFile.mimeStringConstant);		
	}

	public void parse() {

	}
	
	private static final long serialVersionUID = 7068705853470610906L;
}
