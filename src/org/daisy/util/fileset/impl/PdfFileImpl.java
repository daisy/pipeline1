package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.binary.PdfFile;

final class PdfFileImpl extends FilesetFileImpl implements PdfFile {

	PdfFileImpl(URI uri) throws IOException, FileNotFoundException {
		super(uri, PdfFile.mimeStringConstant);
	}

	public void parse() {
		// TODO		
	}

	private static final long serialVersionUID = 4102195541143304701L;

}
