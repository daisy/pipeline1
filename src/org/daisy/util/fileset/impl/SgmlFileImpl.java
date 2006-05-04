package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.sgml.SgmlFile;

abstract class SgmlFileImpl extends FilesetFileImpl implements SgmlFile{

	SgmlFileImpl(URI uri, String mimeStringConstant) throws IOException, FileNotFoundException {
		super(uri,mimeStringConstant);
	}

}
