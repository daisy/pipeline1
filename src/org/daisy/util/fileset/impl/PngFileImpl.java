/*
 * Created on 2006-feb-23
 */
package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.image.PngFile;
import org.daisy.util.mime.MIMETypeException;

/**
 * @author Markus Gylling
 */
class PngFileImpl extends ImageFileImpl implements PngFile {

	PngFileImpl(URI uri) throws FileNotFoundException, IOException, MIMETypeException {
		super(uri,PngFile.mimeStringConstant);
	}

	public void parse() {}
	
	private static final long serialVersionUID = 534181705433151407L;
}
