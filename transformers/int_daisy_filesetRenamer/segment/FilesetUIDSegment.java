package int_daisy_filesetRenamer.segment;

import java.util.HashMap;
import java.util.Map;

import org.daisy.util.fileset.interfaces.Fileset;
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
	public static FilesetUIDSegment create(Fileset fileset) {
		String uid = (String)mUidCache.get(fileset);
		if(uid==null) {
			//a uid has not been stored for this fileset
			uid = getUID(fileset);
			mUidCache.put(fileset, uid);
		}			
		return new FilesetUIDSegment(uid);					
	}

	private static String getUID(Fileset fileset) {
		//if we cant find a uid in the fileset, create a fallback name		
		try{
			FilesetLabelProvider flp = new FilesetLabelProvider(fileset);
			String uid = flp.getFilesetIdentifier();
			if(uid!=null)return uid;
		}catch (Exception e) {
			if(mDebugMode) System.err.println("DEBUG: FilesetUIDSegment#getUID exception"); 
		}
		return "uid";			
	}
	
}
