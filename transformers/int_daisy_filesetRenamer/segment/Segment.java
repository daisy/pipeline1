/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package int_daisy_filesetRenamer.segment;

import java.nio.CharBuffer;

public abstract class Segment { 
	protected static boolean mDebugMode = false;
	protected CharBuffer mSegmentChars = null;
	    		
	protected Segment(String content) {		
		if(content==null) content="";
		//TODO can we wrap the empty string?
		mSegmentChars = CharBuffer.wrap(content.toCharArray());
		if(System.getProperty("org.daisy.debug")!=null) mDebugMode = true;
	}
	
	/**
	 * Retrieve the characters that make up this segment, or null if the segment has not yet been populated.
	 */
	public char[] getChars() {
		return mSegmentChars.array();
	}
	
}
