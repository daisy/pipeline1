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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * A DTB volume.
 * @author Linus Ericson
 */
public class Volume {

	private Set<FilesetFile> filesetFiles;
	private long diskUsage;
	private int volumeNumber;
	private Directory volumeDir;
	
	/**
	 * Creates a new DTB volume.
	 * @param volumeNumber the number of the volume
	 * @param baseOutputDir the base output directory
	 * @param dcIdentifier the identifier of the book
	 * @param useIdSubdir use subdir(s) containing the book identifier
	 * @throws IOException
	 */
	public Volume(int volumeNumber, Directory baseOutputDir, String dcIdentifier, boolean useIdSubdir) throws IOException {
		this.filesetFiles = new HashSet<FilesetFile>();
		this.diskUsage = 0;
		this.volumeNumber = volumeNumber;
		String volumeDirName = String.valueOf(volumeNumber);
		if (useIdSubdir) {
			volumeDirName = dcIdentifier + "_" + volumeDirName;
		}
		volumeDir = new Directory(baseOutputDir, volumeDirName);
	}
	
	/**
	 * Adds a SMIL group to the volume. This method assumes the <code>willItFit</code>
	 * method has been called first to make sure the SMIL group fits within the volume.
	 * @param smilGroup the SMIL group to add
	 * @see #willItFit(SmilGroup, long)
	 */
	public void addSmilGroup(SmilGroup smilGroup) {
		Set<FilesetFile> newFiles = new HashSet<FilesetFile>(smilGroup.getAllFiles());
		newFiles.removeAll(filesetFiles);
		diskUsage += getDiskUsage(newFiles);
		filesetFiles.addAll(newFiles);
	}
	
	/**
	 * Adds the fileset files referenced from the title smil to the volume
	 * @param titleMediaFiles
	 */
	public void addTitleSmilMediaFiles(Set<FilesetFile> titleMediaFiles) {
	    Set<FilesetFile> newFiles = new HashSet<FilesetFile>(titleMediaFiles);
        newFiles.removeAll(filesetFiles);
        diskUsage += getDiskUsage(newFiles);
        filesetFiles.addAll(newFiles);
	}
	
	/**
	 * Will the given SMIL group fit in the volume?
	 * @param smilGroup the SMIL group to test
	 * @param maxVolumeSize the maximum allowed volume size
	 * @return <code>true</code> if the SMIL group fits within the volume, <code>false</code> otherwise
	 */
	public boolean willItFit(SmilGroup smilGroup, long maxVolumeSize) {
		Set<FilesetFile> newFiles = new HashSet<FilesetFile>(smilGroup.getAllFiles());
		newFiles.removeAll(filesetFiles);
		long newSize = diskUsage + getDiskUsage(newFiles);
		return newSize <= maxVolumeSize;
	}
	
	/**
	 * Gets the volume number
	 * @return the volume number
	 */
	public int getVolumeNumber() {
	    return volumeNumber;
	}
	
	/**
	 * Gets the volume directory
	 * @return the volume directory
	 */
	public Directory getVolumeDir() {
	    return volumeDir;
	}
	
	/**
	 * Gets the amount of disk space used by the specified files
	 * @param files the files
	 * @return the amount of disk space used by the files
	 */
	private long getDiskUsage(Set<FilesetFile> files) {
		long size = 0;
		for (FilesetFile fsf : files) {
			size += fsf.getFile().length();
		}
		return size;
	}
	
	/**
	 * Adds all SMIL files in the volume to the SMIL-&gt;volumeNumber map
	 * @param smilVolumeNumberMap
	 */
	public void fillSmilVolumeNumberMap(Map<FilesetFile,Integer> smilVolumeNumberMap) {
	    for (FilesetFile fsf : filesetFiles) {
	        if (fsf instanceof D202SmilFile) {
	            smilVolumeNumberMap.put(fsf, volumeNumber);
	        }
	    }
	}
	
	/**
	 * Write most files (i.e. not the NCC) in the volume to the output dir
	 * @param manifestDir the input manifest directory
	 * @param xif an XMLInputFactory
	 * @param xof an XMLOutputFactory
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public void writeNonNccToOutput(Directory manifestDir, PromptSet promptSet, Map<FilesetFile,Integer> smilVolumeNumberMap, XMLInputFactory xif, XMLOutputFactory xof) throws IOException, XMLStreamException {
		URI manifestDirUri = manifestDir.toURI();
		for (FilesetFile fsf : filesetFiles) {
			// Resolve output file
		    URI fsfUri = fsf.getFile().toURI();
            URI relative = manifestDirUri.relativize(fsfUri);
            File outputFile = new File(volumeDir, relative.toString());
            FileUtils.createDirectory(outputFile.getParentFile());
            
            if (fsf instanceof D202TextualContentFile) {
            	// Copy content document.
            	D202TextualContentFile contentDoc = (D202TextualContentFile)fsf;            	
                XMLEventReader reader = xif.createXMLEventReader(new FileInputStream(contentDoc.getFile()));
			    OutputStream outputStream = new FileOutputStream(outputFile);
			    StaxFilter filter = new ContentDocFilter(reader, xof, outputStream, promptSet, smilVolumeNumberMap, contentDoc, volumeNumber);
			    filter.filter();
			    filter.close();
			    reader.close();
            } else if (fsf instanceof D202NccFile) {
				// Don't copy this just yet
			} else {
				// Copy file (smil, audio, images)
	            FileUtils.copyFile(fsf.getFile(), outputFile);
			}
		}
	}
	
}
