package org.daisy.util.fileset.exception;

import org.daisy.util.fileset.interfaces.FilesetFile;

public class FilesetFileWarningException extends FilesetFileException {

	public FilesetFileWarningException(FilesetFile origin, Throwable exc) {
		super(origin, exc);
	}

	private static final long serialVersionUID = 4173500821582906609L;	
}
