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

import java.util.HashMap;
import java.util.Map;

import org.daisy.util.fileset.util.FilesetLabelProvider;

/**
 * Represents a collective identifier (UID) segment of a filename. The UID represents the fileset, not the file.
 * @author Markus Gylling
 */
public class FilesetUIDSegment extends Segment {
	private static Map<FilesetLabelProvider, String> mUidCache = new HashMap<FilesetLabelProvider, String>(); //<Fileset>,<String>
	
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
		String uid = mUidCache.get(labelProvider);
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
