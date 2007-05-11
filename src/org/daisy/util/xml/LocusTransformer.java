package org.daisy.util.xml;

import javax.xml.stream.Location;

import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.w3c.css.sac.CSSParseException;
import org.xml.sax.SAXParseException;

/**
 *  Translate between different API expressions of an XML event locus.
 *  <p>See also {@link org.daisy.util.exception.ExceptionTransformer}</p>
 *  @author Markus Gylling
 */

public class LocusTransformer  {
	
	public static Location newLocation(ValidatorMessage message) {		
		LocationImpl loc =  new LocusTransformer().new LocationImpl();	
		try{
			loc.setColumnNumber(message.getColumn());
			loc.setLineNumber(message.getLine());
			loc.setSystemId(message.getFile().toString());
		}catch (Exception e) {}
		return loc;
	}
	
	public static Location newLocation(SAXParseException spe) {		
		LocationImpl loc =  new LocusTransformer().new LocationImpl();	
		try{
			loc.setColumnNumber(spe.getColumnNumber());
			loc.setLineNumber(spe.getLineNumber());
			loc.setSystemId(spe.getSystemId());
		}catch (Exception e) {}
		return loc;
	}
	
	public static Location newLocation(CSSParseException cpe) {		
		LocationImpl loc =  new LocusTransformer().new LocationImpl();	
		try{
			loc.setColumnNumber(cpe.getColumnNumber());
			loc.setLineNumber(cpe.getLineNumber());
			loc.setSystemId(cpe.getURI());
		}catch (Exception e) {}
		
		return loc;
	}
	
	public static Location newLocation(FilesetFileException ffe) {
		LocationImpl loc =  new LocusTransformer().new LocationImpl();	
		try{
		    Throwable cause = ffe.getRootCause();
		    if (cause==null) cause = ffe.getCause();
		    
		    Location innerLocation = null;
			if(cause instanceof CSSParseException) {
			  innerLocation = LocusTransformer.newLocation((CSSParseException)cause);
			}else if(cause instanceof SAXParseException) {
			  innerLocation = LocusTransformer.newLocation((SAXParseException)cause);
			}
			
			loc.setSystemId(ffe.getOrigin().getFile().toURI().toString());
			if(innerLocation!=null){
				loc.setLineNumber(innerLocation.getLineNumber());
				loc.setColumnNumber(innerLocation.getColumnNumber());
			}
		}catch (Exception e) {}
		
		return loc;
	}
	
	private LocusTransformer() {
		
	}
	
	
	private class LocationImpl implements Location {		
	    private int mCharacterOffset = -1;
	    private int mColumnNumber = -1;
	    private int mLineNumber = -1;
	    private String mPublicId = null;
	    private String mSystemId = null;
	    
		private LocationImpl () {
			
		}
		
		public int getCharacterOffset() {		
			return mCharacterOffset;
		}

		public int getColumnNumber() {
			return mColumnNumber;
		}

		public int getLineNumber() {
			return mLineNumber;
		}

		public String getPublicId() {
			return mPublicId;
		}

		public String getSystemId() {
			return mSystemId;
		}

		private void setCharacterOffset(int characterOffset) {
			mCharacterOffset = characterOffset;
		}

		private void setColumnNumber(int columnNumber) {
			mColumnNumber = columnNumber;
		}

		private void setLineNumber(int lineNumber) {
			mLineNumber = lineNumber;
		}

		private void setPublicId(String publicId) {
			mPublicId = publicId;
		}

		private void setSystemId(String systemId) {
			mSystemId = systemId;
		}

	}

}
