package org.daisy.util.xml.stax;

import javax.xml.stream.Location;

/**
 * A barebones impl of <code>javax.xml.stream.Location</code>
 * @author Markus Gylling
 */
public class LocationImpl implements Location {		
	    private int mCharacterOffset = -1;
	    private int mColumnNumber = -1;
	    private int mLineNumber = -1;
	    private String mPublicId = null;
	    private String mSystemId = null;
	    
		public LocationImpl () {
			
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

		void setCharacterOffset(int characterOffset) {
			mCharacterOffset = characterOffset;
		}

		void setColumnNumber(int columnNumber) {
			mColumnNumber = columnNumber;
		}

		void setLineNumber(int lineNumber) {
			mLineNumber = lineNumber;
		}

		void setPublicId(String publicId) {
			mPublicId = publicId;
		}

		void setSystemId(String systemId) {
			mSystemId = systemId;
		}

	}