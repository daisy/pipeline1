package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.daisy.util.fileset.interfaces.binary.BinaryFile;

public abstract class BinaryFileImpl extends FilesetFileImpl implements BinaryFile {


	BinaryFileImpl(URI uri, String mimeString) throws IOException, FileNotFoundException {
		super(uri, mimeString);
	}
	
	private static final long serialVersionUID = 4307662099415981983L;

}
