package int_daisy_filesetRenamer.segment;

import java.util.HashMap;
import java.util.Map;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * Represents a collective identifier (UID) segment of a filename. The UID represents the fileset, not the file.
 * @author Markus Gylling
 */
public class FilesetUIDSegment extends Segment {
	private static Map mUidCache = new HashMap(); //<Fileset>,<String>
	
	private FilesetUIDSegment(String content) {
		super(content);		
	}
	
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static FilesetUIDSegment create(String content) {
		return new FilesetUIDSegment(content);
	}
	
	/**
	 * Create a new segment, using the inparam as source to build the segment
	 */
	public static FilesetUIDSegment create(FilesetLabelProvider labelProvider) {
		String uid = (String)mUidCache.get(labelProvider);
		if(uid==null) {
			//a uid has not been stored for this fileset
			uid = getUID(labelProvider);
			mUidCache.put(labelProvider, uid);
		}			
		return new FilesetUIDSegment(uid);					
	}

	private static String getUID(FilesetLabelProvider labelProvider) {
		//if we cant find a uid in the fileset, create a fallback name		
		try{			
			String uid = labelProvider.getFilesetIdentifier();
			if(uid!=null)return uid;
		}catch (Exception e) {
			if(mDebugMode) System.err.println("DEBUG: FilesetUIDSegment#getUID exception"); 
		}
		return "uid";			
	}
	
}
