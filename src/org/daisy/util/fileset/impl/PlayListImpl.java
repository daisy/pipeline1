package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.io.LineNumberReader;

import org.daisy.util.fileset.interfaces.text.PlayList;
import org.daisy.util.mime.MIMETypeException;

abstract class PlayListImpl extends FilesetFileImpl implements PlayList {
	LineNumberReader reader;
	
	PlayListImpl(URI uri, String mimeStringConstant) throws FileNotFoundException, IOException, MIMETypeException {
        super(uri,mimeStringConstant);          
        reader = new LineNumberReader(new FileReader(this));
    }

}
