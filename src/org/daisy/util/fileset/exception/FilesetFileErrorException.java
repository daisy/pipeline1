package org.daisy.util.fileset.exception;

import org.daisy.util.fileset.interfaces.FilesetFile;

public class FilesetFileErrorException extends FilesetFileException {

	public FilesetFileErrorException(FilesetFile origin, Throwable exc) {
		super(origin, exc);
	}

	private static final long serialVersionUID = -3127337538511677420L;	

}
