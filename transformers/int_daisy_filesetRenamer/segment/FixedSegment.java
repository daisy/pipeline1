package int_daisy_filesetRenamer.segment;

public class FixedSegment extends Segment {
		
	private FixedSegment(String content) {
		super(content);		
	}
		
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static FixedSegment create(String content) {
		if(content==null)return new FixedSegment("");
		return new FixedSegment(content);
	}
	
}
