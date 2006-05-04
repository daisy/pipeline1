package org.daisy.util.fileset.exception;

import org.daisy.util.fileset.interfaces.FilesetFile;

public class FilesetFileFatalErrorException extends FilesetFileException {

	public FilesetFileFatalErrorException(FilesetFile origin, Throwable exc) {
		super(origin, exc);
	}
	
	private static final long serialVersionUID = -1437030785817486118L;
}
