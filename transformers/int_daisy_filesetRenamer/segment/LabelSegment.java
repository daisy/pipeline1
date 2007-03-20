package int_daisy_filesetRenamer.segment;

import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.interfaces.FilesetFile;
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