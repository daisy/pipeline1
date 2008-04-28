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

import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * Represents a label segment of a filename, ie a label of the content 
 * the file represents. 
 * @author Markus Gylling
 */
public class LabelSegment extends Segment {
	
	private LabelSegment(String content) {
		super(content);		
	}
	
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static LabelSegment create(String content) {		
		return new LabelSegment(content);
	}
	
	/**
	 * Create a new segment, using the inparams as source to build the segment.
	 * @return The label segment, if a label cannot be located for the input file, 
	 * valued with the empty string.
	 */
	public static LabelSegment create(FilesetFile file, FilesetLabelProvider mLabelProvider) {
		
		try {
			String label = null;
			label = mLabelProvider.getFilesetFileTitle(file);
			//System.err.println(label);
			if(label!=null) return new LabelSegment(label);
		} catch (FilesetFileException e) {
			System.err.println(e.getMessage());
		}
				
		return new LabelSegment("");
	}


}