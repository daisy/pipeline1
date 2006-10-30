package int_daisy_filesetRenamer.segment;

import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * Represents a segment of a filename, which is an exact copy of the input filename (minus extension segment).
 * @author Markus Gylling
 */
public class EchoSegment extends Segment {
		
	private EchoSegment(String content) {
		super(content);		
	}

	/**
	 * Create a new segment, regarding the inparam string as the value to echo.
	 */
	public static EchoSegment create(String toEcho) {
		if(toEcho==null)return new EchoSegment("");
		return new EchoSegment(toEcho);
	}
	
	/**
	 * Create a new segment, regarding the inparam FilesetFiles name as the value to echo.
	 */
	public static EchoSegment create(FilesetFile toEcho) {
		if(toEcho==null)return new EchoSegment("");
		return new EchoSegment(toEcho.getNameMinusExtension());
	}
	
}
