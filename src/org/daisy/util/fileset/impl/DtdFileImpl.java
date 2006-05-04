package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import org.daisy.util.fileset.interfaces.xml.DtdFile;


final class DtdFileImpl extends FilesetFileImpl implements DtdFile {

	DtdFileImpl(URI uri, String mimeString) throws IOException, FileNotFoundException {
		super(uri, mimeString);
	}

	DtdFileImpl(URI uri) throws IOException, FileNotFoundException {
		super(uri, DtdFile.mimeStringConstant);
	}

	public void parse()  {
		//could use wutka here		
	}

	private static final long serialVersionUID = 5139283453997192932L;
	
}
