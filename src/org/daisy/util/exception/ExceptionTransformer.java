/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.exception;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * 
 * @author Markus Gylling
 */
public class ExceptionTransformer {

	/**
	 * Create a new SAXParseException from an arbitrary Exception
	 * @param e The exception to transform into a SAXException.
	 */
	public static SAXParseException newSAXParseException(Exception e){
		
		if(e instanceof SAXParseException) return (SAXParseException)e;
		
		LocatorImpl loc = new LocatorImpl();				
		try{
			if(e instanceof TransformerException) {			
				TransformerException te = (TransformerException) e;		
				loc.setLineNumber(te.getLocator().getLineNumber());
				loc.setColumnNumber(te.getLocator().getColumnNumber());
				loc.setPublicId(te.getLocator().getPublicId());
				loc.setSystemId(te.getLocator().getSystemId());			
			} else if (e instanceof XMLStreamException) {
				XMLStreamException se = (XMLStreamException)e;
				loc.setLineNumber(se.getLocation().getLineNumber());
				loc.setColumnNumber(se.getLocation().getColumnNumber());
				loc.setPublicId(se.getLocation().getPublicId());
				loc.setSystemId(se.getLocation().getSystemId());
			}
		}catch (Exception e2) {
			//silence
		}

		return new SAXParseException(e.getMessage(), loc, e);
	}
	

	public static final int SAX_ERRHANDLER_TYPE_WARNING = 1;
	public static final int SAX_ERRHANDLER_TYPE_ERROR = 2;
	public static final int SAX_ERRHANDLER_TYPE_FATALERROR = 3;

	/**
	 * Create a {@link org.daisy.util.fileset.validation.message.ValidatorMessage} from a SAXParseException.
	 * @param spe The SAXParseException to transform
	 * @param errHandlerType Whether the exception was received in ErrorHandler error, fatalerror or warning  
	 */
	public static ValidatorMessage newValidatorMessage(SAXParseException spe, int errHandlerType) {
		return newValidatorMessage(spe, errHandlerType, null);
	}
	
	/**
	 * Create a {@link org.daisy.util.fileset.validation.message.ValidatorMessage} from a SAXParseException.
	 * @param spe The SAXParseException to transform
	 * @param errHandlerType Whether the exception was received in ErrorHandler error, fatalerror or warning  
	 * @param file The URI of the file which caused the exception to be raised 
	 * 		(this also occurs in SAXParseException.getSystemId(), but sometimes this is null). 
	 */
	public static ValidatorMessage newValidatorMessage(SAXParseException spe, int errHandlerType, URI file) {
					
		if(file==null) {
			if(spe.getSystemId()!=null){
				try {
					file = new URI(spe.getSystemId());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}	
			}else{
				if(System.getProperty("org.daisy.debug")!=null) {
					System.out.println("DEBUG: URI is null in ExceptionTransformer#newValidatorMessage");
				}
						
			}
		}
		
				
		Object vm;
		switch(errHandlerType) {
				case SAX_ERRHANDLER_TYPE_WARNING:
					vm = new ValidatorWarningMessage(file, spe.getMessage(),spe.getLineNumber(),spe.getColumnNumber());
					break;
				case SAX_ERRHANDLER_TYPE_FATALERROR:
					vm = new ValidatorSevereErrorMessage(file, spe.getMessage(),spe.getLineNumber(),spe.getColumnNumber());
					break;
				default:
					vm = new ValidatorErrorMessage(file, spe.getMessage(),spe.getLineNumber(),spe.getColumnNumber());
					break;
		}
		
		return (ValidatorMessage)vm;	
		
	}

	/**
	 * Create an {@link org.daisy.util.fileset.validation.message.ValidatorMessage} 
	 * from an {@link org.daisy.util.fileset.exception.FilesetFileException}.
	 */
	public static ValidatorMessage newValidatorMessage(FilesetFileException ffe) {
		URI fileURI = ffe.getOrigin().getFile().toURI();
		String message = ffe.getCause().getMessage();
		int line = -1;
		int column = -1;
		
		if(ffe.getCause() instanceof SAXParseException) {
			SAXParseException spe = (SAXParseException) ffe.getCause();
			line = spe.getLineNumber();
			column = spe.getColumnNumber();			
		}
			
		if(ffe instanceof FilesetFileFatalErrorException) {
			return new ValidatorSevereErrorMessage(fileURI,message,line,column);
		} else if (ffe instanceof FilesetFileErrorException) {
			return new ValidatorErrorMessage(fileURI,message,line,column);
		} else if (ffe instanceof FilesetFileWarningException) {
			return new ValidatorWarningMessage(fileURI,message,line,column);
		} else {
			return new ValidatorMessage(fileURI,message,line,column);
		}
	}

	public static SAXException newSAXException(Exception e) {
		return ExceptionTransformer.newSAXParseException(e);
	}
	
}
