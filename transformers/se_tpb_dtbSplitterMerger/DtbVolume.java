/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package se_tpb_dtbSplitterMerger;
/*
 * 
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.Referable;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.TextualContentFile;
import org.daisy.util.fileset.XmlFile;

/**
 * @author Piotr Kiernicki
 */
public class DtbVolume {
	
	private List smilFiles = new ArrayList();
    private List audioFiles = new ArrayList();
	private List resourceFiles = new ArrayList();
	private List fullTextFiles = new ArrayList();
	private List promptFiles = new ArrayList();
	private List redundantFiles = new ArrayList();
	
	private int volumeNr;
	private File volumeOutputDir = null;
	private long volumeSize;
	
	private SmilFile titleSmil = null;
	
	private int volumeType;
	private final int SPLIT_VOL_TYPE = 1;
	private final int MERGE_VOL_TYPE = 2; 
	private DtbTransformationReporter  reportGenerator;
	
	private static boolean askWhetherOverwrite = true;
	
    
	/*
	 * Splitting output constructor
	 */
	public DtbVolume(int volNr, File outputDir, DtbTransformationReporter rg, String dirPrefix){
		this.volumeNr = volNr;
		this.reportGenerator = rg;
		/* outputDir may come as null when we are creating a temporary volume 
		 * for collecting non splittable elements
		 */
		if(outputDir!=null)
			this.volumeOutputDir = new File(outputDir.getAbsolutePath()+ File.separator + dirPrefix + volNr);
		
		this.volumeType = this.SPLIT_VOL_TYPE;	
		
		/* Set the safety margin size for the volume, since 
		 * the output volumes transformed documents, such as 
		 * ncc or full text, become usually bigger in size.
		 */
		this.incrementVolumeSize(DtbSplitterMergerConstants.volumeSizeMeasureUnit/2);
		
	}
	
	/*
	 * Merging output constructor
	 */
	public DtbVolume(File outputDir, DtbTransformationReporter rg){
		this.volumeOutputDir = outputDir;
		this.reportGenerator = rg;
		this.volumeType = this.MERGE_VOL_TYPE;
	}

	public List copyFilesIntoVolume(Collection inputFiles,  
                                    boolean userPromptOn,
									boolean keepInput, 
									String inputVolumeDir, 
									boolean checkRelativePath) throws TransformationAbortedByUserException, IOException{
		
		List outputFiles = new ArrayList();
		
		//Copy each file to the new directory.
		Iterator i = inputFiles.iterator();
		while(i.hasNext()){
			File inputFile = (File)i.next();
			File outputDir = this.createOutputDir(inputFile,inputVolumeDir,checkRelativePath);
            File targetFile = new File(outputDir.getAbsolutePath()+File.separator+inputFile.getName());
            //check if target file exists
            if(userPromptOn && targetFile.exists() && DtbVolume.askWhetherOverwrite 
                    && !inputFile.getName().equals(DtbPromptFiles.TITLE_SMIL_FILE_NAME)){//title smil is serialized when created
            	this.reportGenerator.sendTransformationMessage(Level.INFO,"FILE_EXISTS",targetFile.getAbsolutePath());
            	String userResponse = this.reportGenerator.getTransformationUserInput(Level.INFO, "OVERWRITE_FILES", "a");
            	/* "a" - default value, overwrite this and all subsequent files
            	 * "x" - abort execution
            	 * "y" - overwrite file and continue
            	 */
            	if(userResponse!=null && userResponse.equalsIgnoreCase("x")){
            		throw new TransformationAbortedByUserException("");
            	}else if(userResponse!=null && userResponse.equalsIgnoreCase("a")){
            		DtbVolume.askWhetherOverwrite = false;
            	}
            }
            
            FileUtils.copyFile(inputFile,targetFile);
            this.reportGenerator.reportCopiedFile(targetFile.length());
            outputFiles.add(targetFile);

//          Delete the original file if the user wishes so.
            if(!keepInput){
                inputFile.deleteOnExit();       
            }			
		}
		return outputFiles;
	}
	
	private File createOutputDir(File inputFile, String inputVolumeDir, boolean checkRelativePath) {
		File outputDir = null;
		if(checkRelativePath){
			//extract the file's relative path 
			int relPathStart = inputVolumeDir.length()+1;
			int relPathEnd = inputFile.getAbsolutePath().length();
			String fileRelativePath = inputFile.getAbsolutePath().substring(relPathStart, relPathEnd);
			
			//Instantiate the subdirectory file (it is just a handle not a file on disk yet!)
			File f = new File(this.volumeOutputDir.getAbsolutePath()+File.separator+ fileRelativePath);
			
			//create the subdirectory if it does not exist yet
			outputDir = f.getParentFile();
			if(!outputDir.exists())
				outputDir.mkdirs();
		
		}else{
			outputDir = this.volumeOutputDir;			
		}
		return outputDir;
	}

	/* 
	 * Adding a file:
	 *  - when creating a split output volume, we just check whether the file already exists 
	 *    in the given collection.
	 * 
	 *  - when merging, some files, such as prompt files, may recur in the input volumes. 
	 *    They have the same names and contents, yet they differ by location. Therefore, 
	 * 	  in order to detect them we check them by name.
	 *    The only exception is made for the full text files as they differ also by their contents.
	 *    That is why addFile is not used in addFullTextFile.
	 */
	private void addFile(File file, List collection){
		
		if(this.volumeType==this.SPLIT_VOL_TYPE && !collection.contains(file)){
			
			collection.add(file);
						
		}else if(this.volumeType==this.MERGE_VOL_TYPE){

			Iterator i = collection.iterator();
			boolean fileNameFound = false;
		
			while(i.hasNext()){
				File fileInCollection = (File)i.next();
				if(file.getName().equalsIgnoreCase(fileInCollection.getName())){
					fileNameFound = true;
                    break;
				}
			}
		
			if(!fileNameFound){
				collection.add(file);
			}
		}
		
	}
	public void addPromptFile(File f){
        this.addFile(f, this.promptFiles);
    }
    
	public void addPromptFile(Referable f){
		this.addFile(f.getFile(), this.promptFiles);
	}
	
	public void addSmilFile(SmilFile f) {
		this.addFile(f.getFile(), this.smilFiles);
	}
    public void addAudioFile(AudioFile f){
        this.addFile(f.getFile(),this.audioFiles);
    }
    public void addAudioFiles(List files){
        this.audioFiles.addAll(files);
    }
    
    public List getAudioFiles(){
        return this.audioFiles;
    }
	
	public void addSmilResources(SmilFile SmilFile, boolean isPromptFile){
		
		Iterator i = SmilFile.getReferencedLocalMembers().iterator();
		while(i.hasNext()){
			Referable file = (Referable)i.next();
			if(file instanceof TextualContentFile){
				if(this.addFullTextFile((TextualContentFile)file)){
					this.addFullTextResources((TextualContentFile)file);
				}
					
			}else if(!(file instanceof XmlFile)){
				//Add non-xml resources.
				/*
				 * When collecting the resources into a merged volume we need check
				 * whether it is a prompt smil.
				 */
				if(isPromptFile){
					this.addPromptFile(file);
				}else{
					this.addResourceFile(file);
				}
						
			}
			
		}
	}
	
	private void addFullTextResources(TextualContentFile fText){
		Iterator i = fText.getReferencedLocalMembers().iterator();
		while(i.hasNext()){
			Referable file = (Referable)i.next();
			if(!(file instanceof XmlFile)){
				this.addResourceFile(file);
			}
		}
	}
	
	//Adds non-smil and non-html files.
	public void addResourceFile(Referable f) {
		this.addFile(f.getFile(), this.resourceFiles);
	}
	
	public boolean addFullTextFile(TextualContentFile file) {
		boolean success = false;
		if(!this.fullTextFiles.contains(file)){
			this.fullTextFiles.add(file);
			success = true;
		}
		return success;
			
	}
	
	public void addResourcesFrom(DtbVolume vol){
		List smils = vol.getSmilFiles();
		List resources = vol.getResourceFiles();
		List fullText = vol.getFullTextFiles();
		
		Iterator s = smils.iterator();
		while(s.hasNext()){
			this.addSmilFile((SmilFile)s.next());
		}
		
		Iterator r = resources.iterator();
		while(r.hasNext()){
			this.addResourceFile((Referable)r.next());
		}
		
		Iterator f = fullText.iterator();
		while(f.hasNext()){
			this.addFullTextFile((TextualContentFile)f.next());
		}
	}
		
	public List getSmilFiles() {
		return this.smilFiles;
	}
	
	public void setSmilFiles(List files){
		this.smilFiles = files;
	}
	
	/**
	 * Returns a collection with <code>java.io.File</code> instances.
	 * @return a collection with <code>java.io.File</code> instances.
	 */
	public List getResourceFiles() {
		return this.resourceFiles;
	}

	public void setResourceFiles(List files){
		this.resourceFiles = files;
	}

	
	/**
	 * Returns a collection with <code>java.io.File</code> instances.
	 * @return a collection with <code>java.io.File</code> instances.
	 */
	public List getFullTextFiles() {
		return this.fullTextFiles;
	}
	
	public void setFullTextFiles(List files) {
		this.fullTextFiles = files;
	}
		
	/**
	 * Returns a collection with <code>java.io.File</code> instances.
	 * @return a collection with <code>java.io.File</code> instances.
	 */
	public List getPromtFiles() {
			return this.promptFiles;
	}
	
	public void setPromptFiles(List files){
		this.promptFiles = files;
	}

	/**
	 * Returns a collection with <code>java.io.File</code> instances.
	 * @return a collection with <code>java.io.File</code> instances.
	 */
	public List getRedundantFiles() {
		return redundantFiles;
	}

	public void setRedundantFiles(List vector) {
		redundantFiles = vector;
	}		

	public int getVolumeNr() {
		return volumeNr;
	}

	public long getVolumeSize() {
		return this.volumeSize;
	}
	
	public void incrementVolumeSize(long l) {
		this.volumeSize += l;
	}
	
	public void decrementVolumeSize(long l) {
			this.volumeSize -= l;
	}
	
	public File getVolumeOutputDir() {
		return volumeOutputDir;
	}

	public SmilFile getTitleSmil() {
		return titleSmil;
	}

	public void setTitleSmil(SmilFile file) {
		titleSmil = file;
	}
	
	/*
	 * TODO does not handle subfolders
	 */
	public long calculateVolumeRealSize(){
		long size = 0;
		
		File[] files = this.getVolumeOutputDir().listFiles();
		
		for(int i=0; i<files.length; i++){
			size+=files[i].length();
		}
		
		return size;
	}


}
