/*
 * Created on 2005-jun-19
 */
package org.daisy.util.fileset;

/**
 * @author Markus Gylling
 */
public interface FilesetErrorHandler extends org.xml.sax.ErrorHandler, org.w3c.css.sac.ErrorHandler {

	public void error(FilesetException exception) throws FilesetException;
		
	public void warning(FilesetException exception) throws FilesetException;
	
}
