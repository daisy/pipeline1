/*
 * Daisy Pipeline (C) 2005-2009 Daisy Consortium
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
package se_tpb_daisy202splitter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.FilesetFile;

/**
 * A group of SMIL files. A DTB can be split into volumes between SMIL groups.
 * @author Linus Ericson
 */
public class SmilGroup {

	private List<D202SmilFile> smilFiles;
	private long diskUsage;
	private Set<FilesetFile> allFiles;
	
	/**
	 * Creates a new SMIL group.
	 */
	public SmilGroup() {
		smilFiles = new LinkedList<D202SmilFile>();
		diskUsage = -1;
		allFiles = new HashSet<FilesetFile>();
		//System.err.println("new group");
	}
	
	/**
	 * Adds a SMIL file to the SMIL group. All files referenced from the SMIL file
	 * (and the SMIL file itself) are added to the <code>allFiles</code> member.
	 * @param smilFile
	 */
	public void add(D202SmilFile smilFile) {
		smilFiles.add(smilFile);
		// For all files referenced from the SMIL file
		for (FilesetFile fsf : smilFile.getReferencedLocalMembers()) {
			
			// Handle content doc(s) and NCC
			if (fsf instanceof D202TextualContentFile) {
				// Add everything that is not SMIL
				D202TextualContentFile contentFile = (D202TextualContentFile)fsf;
				for (FilesetFile fsf2 : contentFile.getReferencedLocalMembers()) {
					if (!(fsf2 instanceof D202SmilFile)) {
						allFiles.add(fsf2);
					}
				}
			} else if (fsf instanceof D202NccFile) {
                // Add everything that is not SMIL
                D202NccFile nccFile = (D202NccFile)fsf;
                for (FilesetFile fsf2 : nccFile.getReferencedLocalMembers()) {
                    if (!(fsf2 instanceof D202SmilFile)) {
                        allFiles.add(fsf2);
                    }
                }
            }
			allFiles.add(fsf);
		}
		// Add the SMIL file itself
		allFiles.add(smilFile);		
	}
	
	/**
	 * Gets the number of SMIL files in the SMIL group
	 * @return the number of SMIL files in the SMIL group
	 */
	public int getSize() {
		return smilFiles.size();
	}
	
	/**
	 * Calculates the amount of disk space used by all files in the SMIL group (for future use)
	 */
	public void calculateDiskUsage() {
		diskUsage = 0;
		// FIXME the speed of this can be improved by using a static map of content/ncc doc sizes 
		for (FilesetFile fsf : allFiles) {
			diskUsage += fsf.getFile().length();
		}		
	}
	
	/**
	 * Gets the amount of disk space used by all files in the SMIL group (for future use)
	 * @return the amount of disk space used
	 */
	public long getDiskUsage() {
		return diskUsage;
	}
	
	/**
	 * Gets a set of all files in the SMIL group
	 * @return a set of all files in the SMIL group
	 */
	public Set<FilesetFile> getAllFiles() {
		return allFiles;
	}
}
