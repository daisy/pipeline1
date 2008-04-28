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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a filename constituted by an ordered list of segments.
 * @author Markus Gylling
 */
public class SegmentedFileName {
	private LinkedList<Segment> mSegments = null; 	
	private String mSegmentSeparator = null;
	
	public static final int POSITION_FIRST = 1;
	public static final int POSITION_LAST = 2;
	public static final int POSITION_AFTER_PREFIX_SEGMENT = 3;
	public static final int POSITION_BEFORE_EXTENSION_SEGMENT = 4;
	
	/**
	 * Instantiator.
	 */
	public SegmentedFileName(){
		mSegments = new LinkedList<Segment>();
		mSegmentSeparator = "";		
	}
	
	/**
	 * @return an ordered list of the segments that currently constitute this SegmentedFileName
	 */
	public List<Segment> getSegments() {
		return mSegments;
	}
		
	/**
	 * Set an optional string that will be inserted between segments when segments are rendered as a filename.
	 */
	public void setSegmentSeparator(String separator){
		mSegmentSeparator = separator;
	}

	/**
	 * Get an optional string that will be inserted between segments when segments are rendered as a filename.
	 */
	public String getSegmentSeparator(){
		return mSegmentSeparator;
	}
	
	/**
	 * @return the whole segmented filename represented as a String
	 */
	public String getFileName() {		
		StringBuilder sb = new StringBuilder();
		int length = mSegments.size();
		int i = 0;
		for (Iterator<Segment> iter = mSegments.listIterator(); iter.hasNext();) {
			i++;
			Segment s = iter.next();
			sb.append(s.getChars());
			//dont append separator to two last segments (before and after extension)			
			if(length>i+1)sb.append(mSegmentSeparator);
		}
		return sb.toString();		
	}

	/**
	 * Add a new segment to this segmented filename at the default position (before extension segment)
	 * @see #addSegment(Segment, int)
	 */
	public void addSegment(Segment segment) {
		addSegment(segment, SegmentedFileName.POSITION_BEFORE_EXTENSION_SEGMENT);
	}

	/**
	 * Add a new segment to this segmented filename.
	 * @param segmentPosition a static int availabe on this class
	 */
	public void addSegment(Segment segment, int segmentPosition) {
		switch (segmentPosition) {
			case POSITION_FIRST :
				mSegments.addFirst(segment);
				break;
			case POSITION_LAST :				
				mSegments.addLast(segment);
				break;
			case POSITION_BEFORE_EXTENSION_SEGMENT:
				if(!mSegments.isEmpty()){
					Segment s = mSegments.getLast();
					if(s!=null && s instanceof ExtensionSegment) {
						ExtensionSegment extension = (ExtensionSegment)mSegments.removeLast();
						mSegments.addLast(segment);
						mSegments.addLast(extension);
					}else{
						mSegments.addLast(segment);
					}	
				}else{
					mSegments.addFirst(segment);
				}
				break;
			case POSITION_AFTER_PREFIX_SEGMENT:	
				if(!mSegments.isEmpty()){
					Segment sg = mSegments.getFirst();				
					if(sg!=null && sg instanceof FixedSegment) {
						FixedSegment pfx = (FixedSegment)mSegments.removeFirst();
						mSegments.addFirst(segment);
						mSegments.addFirst(pfx);
					}else{
						mSegments.addFirst(segment);
					}
				}else{
					mSegments.addFirst(segment);
				}
				break;
			default:	
				addSegment(segment, SegmentedFileName.POSITION_BEFORE_EXTENSION_SEGMENT);
		}//switch
	}
}
