package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URI;

import org.daisy.util.fileset.interfaces.text.PlayList;

abstract class PlayListImpl extends FilesetFileImpl implements PlayList {
	LineNumberReader reader;
	
	PlayListImpl(URI uri, String mimeStringConstant) throws IOException, FileNotFoundException {
        super(uri,mimeStringConstant);          
        reader = new LineNumberReader(new FileReader(this));
    }

}
