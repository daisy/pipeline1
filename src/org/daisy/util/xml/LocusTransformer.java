package org.daisy.util.xml;

import javax.xml.stream.Location;

import org.daisy.util.fileset.validation.message.ValidatorMessage;

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
