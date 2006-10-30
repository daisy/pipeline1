package int_daisy_filesetRenamer.segment;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * Represents a label segment of a filename, ie a label of the content the file represents. 
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
	 * Create a new segment, using the inparam as source to build the segment.
	 * @return The label segment, if a label cannot be located for the input file, 
	 * valued with the empty string.
	 */
	public static LabelSegment create(FilesetFile file, Fileset fileset) {

		//try internal label
		String label = getInherentLabel(file);
		if(label!=null) return new LabelSegment(label);		
		//try label carried by referring fileset member
		label = getInferredLabel(file, fileset);
		if(label!=null) return new LabelSegment(label);
		//we failed finding a label
		return new LabelSegment("");
	}

	/**
	 * @return a label if one can be inferred from fileset surroundings, else null.
	 */
	private static String getInferredLabel(FilesetFile file, Fileset fileset) {
		//TODO implement
		return "labelTODO";
	}

	/**
	 * @return a label if inparam file carries one internally, else null.
	 */
	private static String getInherentLabel(FilesetFile file) {
		//TODO implement
		return "labelTODO";
	}
}