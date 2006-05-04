package org.daisy.util.fileset.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;

/*package*/ class FilesetExceptionCollector {
	private Set exceptions = new HashSet();
	private FilesetErrorHandler errH;
	
	FilesetExceptionCollector(FilesetErrorHandler fseh) {
		this.errH = fseh;
	}
	
	/*package*/ void add(FilesetFileException ffe) {
		//store it
		this.exceptions.add(ffe);
		try {
			//send it to listener
			this.errH.error(ffe);			
		} catch (FilesetFileException e) {
			e.printStackTrace();
		}		
	}
	
	/*package*/ Collection getExceptions(){
		return this.exceptions;		
	}
	
	/*package*/ boolean hasExceptions(){
		return (!this.exceptions.isEmpty());
	}
}
