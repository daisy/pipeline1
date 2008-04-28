/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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