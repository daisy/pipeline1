package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.sgml.HtmlFile;
import org.daisy.util.mime.MIMETypeException;

public class HtmlFileImpl extends FilesetFileImpl implements HtmlFile{

	HtmlFileImpl(URI uri) throws IOException, FileNotFoundException, MIMETypeException {
		super(uri,HtmlFile.mimeStringConstant);
	}

	public void parse() {
		// TODO		
	}

}
