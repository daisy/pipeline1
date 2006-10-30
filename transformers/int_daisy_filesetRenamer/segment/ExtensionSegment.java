package int_daisy_filesetRenamer.segment;

import org.daisy.util.file.EFile;

/**
 * Represents an extension segment of a filename, including the separator (period).
 * @author Markus Gylling
 */
public class ExtensionSegment extends Segment {
	
	private ExtensionSegment(String content) {
		super(content);		
	}
		
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static ExtensionSegment create(String content) {
		if(content==null || content.length()==0) return new ExtensionSegment("");
		if(content.length()>0 && !content.startsWith(".")) content = "."+content;
		return new ExtensionSegment(content);
	}
	
	/**
	 * Create a new segment, regarding the inparam files extension as content
	 */
	public static ExtensionSegment create(EFile file) {
		if(file.getExtension()!=null && file.getExtension().length()>0) {
			return new ExtensionSegment("."+file.getExtension());
		}
		return new ExtensionSegment("");
	}
}
