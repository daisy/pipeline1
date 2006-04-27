package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.AnonymousFile;
import org.daisy.util.mime.MIMETypeException;

/**
 * @author Markus Gylling
 */
class AnonymousFileImpl extends FilesetFileImpl {

	public AnonymousFileImpl(URI uri) throws FileNotFoundException, IOException, MIMETypeException {
		super(uri, AnonymousFile.mimeStringConstant);		
	}

	public void parse() {
		//Yeah, right.
	}
	
	private static final long serialVersionUID = 7068705853470610906L;
}
