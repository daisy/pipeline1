package int_daisy_filesetRenamer.segment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.Referring;

/**
 * Represents a sequential segment of a filename, ie 
 * this files position in an ordered list of files of the same tye.
 * @author Markus Gylling
 */

public class SequenceSegment extends Segment {
	private static Map mFilesetTypeCounter = new HashMap(); 			//fileset, Map<ClassName, Long> (number of files of a certain class in a certain fileset
	private static Map mOuterCounterCache = new HashMap(); 				//<fileset,HashSet<abspath>>, store identity of members already counted
	
	private SequenceSegment(String content) {
		super(content);		
	}
	
	/**
	 * Create a new segment, regarding the inparam string as readymade content
	 */
	public static SequenceSegment create(String content) {		
		return new SequenceSegment(content);
	}
	
	/**
	 * Create a new segment, using the inparam as source to build the segment
	 * @param file 
	 */
	public static SequenceSegment create(FilesetFile file, Fileset fileset) {		
		Long count = getCount(file, fileset); 				//number of files of this type
		long position = getPosition(file, fileset);			//position of inparam member in type list				
		return new SequenceSegment(format(position,count.longValue()));
	}
	
	/**
	 * @return the number of FilesetFiles in inparam fileset of the same type as 
	 * inparam FilesetFile.
	 */
	private static Long getCount(FilesetFile file, Fileset fileset) {
		Map countMap = (Map)mFilesetTypeCounter.get(fileset);
				
		if(countMap==null) {		
			//this fileset has not been counted before, so do it
			countMap = new HashMap(); //(<ClassName>, <Long>)
			for (Iterator iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
				Object member = iter.next();
				String className = member.getClass().getName();
				Long cnt = (Long)countMap.get(className);
				if(cnt==null) {
					//first occurence of this type
					countMap.put(className, Long.valueOf(1));
				}else{
					//not first occurence of this type
					countMap.put(className, Long.valueOf(cnt.longValue()+1));					
				}
			}
			mFilesetTypeCounter.put(fileset, countMap);
		}
		Long count = ((Long)countMap.get(file.getClass().getName()));
		if(count==null) return Long.valueOf(0);
		return count;
	}

	/**
	 * Loop through a fileset in serial order and up a counter each time a file
	 * of same type as inparam occurs. Return counter at identity match.
	 */
	private static long getPosition(FilesetFile file, Fileset fileset) {
		long counter = 0;
		//start from manifest
		ManifestFile manifest = fileset.getManifestMember();
		
		//TODO if input file is manifest
		
		for (Iterator iter = manifest.getReferencedLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile member = (FilesetFile)iter.next();			
			HashSet counterCache = (HashSet)mOuterCounterCache.get(fileset);
			if(counterCache == null) {
				counterCache = new HashSet();
				mOuterCounterCache.put(fileset, counterCache);
			}
			FilesetFile found = search(member,file,counter,counterCache);
			if(found!=null)break;
		}						
		return counter;
	}
	
	/**
	 *@return null if toFind was not found among the referenced member within toSearch  
	 */
	private static FilesetFile search(FilesetFile toSearch, FilesetFile toFind, long typeCounter, HashSet counterCache) {
		
		try {
			//if toSearch is of our current type and not already counted, up the counter
			if(toSearch.getClass().getName().equals(toFind.getClass().getName())) {
				String value = toSearch.getFile().getCanonicalPath();
				if (!counterCache.contains(value)){
					counterCache.add(value);
					typeCounter++;	
				}
			}
				
			//if toSearch has the identify we are looking for
			if (toSearch.getFile().getCanonicalPath().equals(toFind.getFile().getCanonicalPath())) {
				//we found the one we are looking for directly				
				return toSearch;
			}
			//else, go through the files referenced within toSearch and recurse
			if(toSearch instanceof Referring) {
				Referring referer = (Referring) toSearch;
				for (Iterator iter = referer.getReferencedLocalMembers().iterator(); iter.hasNext();) {
					FilesetFile ffile = (FilesetFile) iter.next();
					FilesetFile found = search(ffile,toFind,typeCounter,counterCache);
					if(found!=null) return found;
				}
			}//toSearch instanceof Referring
		} catch (IOException e) {

		}
		return null;		
	}
	
	private static String format(long value, long count) {
		/*
		 * if there are 999 files, we want 00n syntax, etc
		 */
		StringBuilder sb = new StringBuilder();
		String length = Long.toString(count);
		for (int i = 0; i < length.length()+1; i++) {
			sb.append('0');
		}
		DecimalFormat df = new DecimalFormat(sb.toString());
		return df.format(value);
	}
}