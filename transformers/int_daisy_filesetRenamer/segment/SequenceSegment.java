package int_daisy_filesetRenamer.segment;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.ManifestFile;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.Referring;

/**
 * Represents a sequential segment of a filename, ie 
 * this files position in an ordered list of files of the same tye.
 * @author Markus Gylling
 */

public class SequenceSegment extends Segment {
	private static Map mFilesetTypeCounter = new HashMap(); 			//fileset, Map<ClassName, Long> (number of files of a certain class in a certain fileset
	private static Map mFormatCache = new HashMap();
	
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
	private static long getPosition(FilesetFile toFind, Fileset fileset) {
		
		
		try{
			//deal with the case where inparam file is manifest first
			ManifestFile manifest = fileset.getManifestMember();			
			if(manifest.getFile().getCanonicalPath().equals(toFind.getFile().getCanonicalPath())) {
				return 0;
			}
			
			//then deal with discovery using manifest as the starting point
			Collection loop = null;		
			//if input fileset is a DTB, us the spine collection as a starting point.		
			if(fileset.getFilesetType()==FilesetType.DAISY_202) {
				loop = ((D202NccFile)manifest).getSpineItems();
			}else if(fileset.getFilesetType()==FilesetType.Z3986) {
				loop = ((OpfFile)manifest).getSpineItems();
			}else{
				//if not a DTB, use the manifest as a starting point	
				loop = manifest.getReferencedLocalMembers();
			}
			
			//loop through the FilesetFile collection
			//break the loop when inparam file is found,
			//return the postincremented counter
			long counter = 0;
			for (Iterator iter = loop.iterator(); iter.hasNext();) {
				Set counterCache = new HashSet(); 
				Set alreadySearchedCache = new HashSet();
				FilesetFile toCheck = (FilesetFile)iter.next();	
				counter = search(toCheck,toFind,counterCache,alreadySearchedCache);
				if(counter>-1) break;
			}
			return counter;
		}catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Call exhaustively and recursively until toFind is found. Up the type counter in the counterCache
	 * each time a file of the same type as toFind is found, including toFind itself. 
	 * @return counterCache value when toFind is found, or -1 if toFind was not found within toCheck nor among the referenced members within toCheck  
	 */
	private static long search(FilesetFile toCheck, FilesetFile toFind, Set counterCache, Set alreadySearchedCache) {
		try{
			//if toCheck is of our current type and not already counted, up the counter
			if(toCheck.getClass().getName().equals(toFind.getClass().getName())) {
				String value = toCheck.getFile().getCanonicalPath();
				if (!counterCache.contains(value)){
					counterCache.add(value);						
				}
			}		
			
			//if toCheck has the identify we are looking for
			if (toCheck.getFile().getCanonicalPath().equals(toFind.getFile().getCanonicalPath())) { 				
				return counterCache.size();
			}
			
			if(toCheck instanceof Referring) {
				Referring referer = (Referring) toCheck;
				for (Iterator iter = referer.getReferencedLocalMembers().iterator(); iter.hasNext();) {
					FilesetFile ffile = (FilesetFile) iter.next();
					if(!alreadySearchedCache.contains(ffile.getFile().getCanonicalPath())){
						alreadySearchedCache.add(ffile.getFile().getCanonicalPath());
						long found = search(ffile,toFind,counterCache,alreadySearchedCache);
						if(found>-1) {
							return counterCache.size();
						}			
					}
				}//for
			}else{
				//this is a leaf
			}			
		}catch (Throwable e) {
			System.err.println("SequenceSegment.search: " + e.getMessage());
		}
		return -1;
	}	
	
	
	private static String format(long value, long count) {
		/*
		 * if there are 9999 files, we want 000n syntax, etc
		 * a minimum of three digits
		 */

		String format = Long.toString(count);
		if(format.length()<3)format="999";
		DecimalFormat df =  (DecimalFormat)mFormatCache.get(format);
		if(df==null){
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < format.length(); ++i) {
				sb.append('0');
			}		
			df = new DecimalFormat(sb.toString());
			mFormatCache.put(format, df);
		}				
		return df.format(value);
	}
}