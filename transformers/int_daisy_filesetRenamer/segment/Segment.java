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
