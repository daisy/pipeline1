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
package se_tpb_dtbSplitterMerger.split;
/*
 * 
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.stream.XMLInputFactory;

import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.TextualContentFile;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

import se_tpb_dtbSplitterMerger.DtbPromptFiles;
import se_tpb_dtbSplitterMerger.DtbSplitterMergerConstants;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.DtbVolume;
import se_tpb_dtbSplitterMerger.DtbVolumeSet;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;


/**
 * <p>An abstract class defining a public interface for splitting Daisy DTB file sets.</p>
 * <p>It also implements some basic functionality that can be used or overridden by its subclasses.</p> 
 * 
 * @author Piotr Kiernicki
 */
public abstract class DtbSplitter {
	
	public static String DEFAULT_PROMPT_FILES_MANIFEST_PATH = "promptFiles/manifest.xml";
	
	//obligatory input arguments' values
	private File outputDir = null;
    private long maxVolumeSizeInBytes = 0;
    private File promptManifestFile = null;
    
	//optional input arguments' values
    private boolean keepInputDtb = true;
	private int maxSplitLevel = 6;
	private boolean userPromptOn = false;
	//end of input arguments' values.

	private DtbTransformationReporter reportGenerator = null;
	private DtbVolumeSet volumeSet = new DtbVolumeSet();
	private DtbPromptFiles promptFiles = null;
    private XMLInputFactory factory;
	
	
    public DtbSplitter(File outDir, File promptManifest, long maxVolSize, DtbTransformationReporter reportGen){
        this.outputDir = outDir;
        this.promptManifestFile = promptManifest;
        this.maxVolumeSizeInBytes = maxVolSize;
    	this.setReportGenerator(reportGen);
    }	

    public abstract void executeSplitting() throws TransformationAbortedByUserException,XmlParsingException, MaxVolumeSizeExceededException, IOException;
	
	protected Collection<FilesetFile> getNonSmilReferences(XmlFile filesetFile){
		Collection<FilesetFile> resources = new ArrayList<FilesetFile>();
		if(filesetFile!=null){
			for(Iterator<FilesetFile> i=filesetFile.getReferencedLocalMembers().iterator();i.hasNext();){
				FilesetFile file = i.next();
				if(!(file instanceof SmilFile)){
					resources.add(file);
				}
			}	
		}
		return resources;
	}
	abstract protected Collection<SmilFile> getBasePlaySequenceSmilCollection();
	abstract protected int getFileReferenceLevel(String fileName) throws XmlParsingException;
    abstract protected DtbVolume initializeNewVolume(int volumeNr);
	abstract protected void saveVolumes() throws TransformationAbortedByUserException,XmlParsingException, IOException;
	abstract protected String retrieveBookLanguage();
	//TODO Remove RG
	//abstract protected void reportBookInfo() throws DtbException;
		
		
	protected void createVolumes(Collection<SmilFile> a_smilPlaySequence) throws TransformationAbortedByUserException, MaxVolumeSizeExceededException, XmlParsingException, IOException{
        //long start = new Date().getTime();
		int referenceLevel = 0;
		boolean collecting = false;
		boolean closingCollecting = false;
		DtbVolume savedVolume = null;

		//Create the very first volume.
		int volumeNr = 1;
		DtbVolume volume = this.initializeNewVolume(volumeNr);
		// the volume has been created and placed in the volume set.
		
		Iterator<SmilFile> fSet = a_smilPlaySequence.iterator();		
		boolean isTitleSmilSet = false;
		while(fSet.hasNext()){	
			SmilFile smilFile = fSet.next();					
			if(!isTitleSmilSet){
				volume.setTitleSmil(smilFile);
				isTitleSmilSet = true;
			}
			referenceLevel = this.getFileReferenceLevel(smilFile.getName());
			
			
			/* Set the flags describing collecting, depending on 
			 * the max split level allowed.
			 * 
			 * If maxSplitLevel is set to, for instance, 2 then 
			 * it is first at the level 3 (maxSplitLevel+1) 
			 * that we are not allowed to split the document any more 
			 * and therefore should start collecting.
			 */ 
			if(!collecting && referenceLevel == this.maxSplitLevel+1){
				collecting = true;
				savedVolume = volume;
				volume = new DtbVolume(0, null, this.reportGenerator, "");
                isTitleSmilSet = false;
			}else if(collecting && referenceLevel < this.maxSplitLevel+1 ){
				collecting = false;
				closingCollecting = true;
			}
			
			long potentialFileSetSize = this.getSmilFileSetSize(smilFile, volume);
			volume.incrementVolumeSize(potentialFileSetSize);			
			
			if(volume.getVolumeSize() < this.maxVolumeSizeInBytes){
				
				if(closingCollecting){
					long size = savedVolume.getVolumeSize()+volume.getVolumeSize();
					if(size<this.maxVolumeSizeInBytes){
						savedVolume.addResourcesFrom(volume);
						savedVolume.incrementVolumeSize(volume.getVolumeSize());
						volume = savedVolume;
                        isTitleSmilSet = true;
					}else{
						DtbVolume newVolume = this.initializeNewVolume(++volumeNr);
						newVolume.addResourcesFrom(volume);
						newVolume.incrementVolumeSize(volume.getVolumeSize());
						volume = newVolume;
                        isTitleSmilSet = false;
					}
					closingCollecting = false;
				}
            }else if(!collecting){
			/* Since the resource does not fit and we are not collecting,
			 * decrement the volume size
			 */
				volume.decrementVolumeSize(potentialFileSetSize);
                DtbVolume tmp = new DtbVolume(0,null, this.reportGenerator, "");
                if(closingCollecting){
                    tmp.addResourcesFrom(volume);
                    tmp.incrementVolumeSize(volume.getVolumeSize());
                }
			// and then create a new volume and reasign the volume variable.
				volume = this.initializeNewVolume(++volumeNr);
                isTitleSmilSet = false;
                if(closingCollecting){
                    volume.addResourcesFrom(tmp);
                    volume.incrementVolumeSize(tmp.getVolumeSize());
                    closingCollecting = false;
                }
				volume.incrementVolumeSize(this.getSmilFileSetSize(smilFile, volume));//we must calculate it anew here since it is a new volume

				//check whether maxVolumeSizeInBytes has not been exceeded
				if(!(volume.getVolumeSize()<this.maxVolumeSizeInBytes)){
//					String errorMsgText = "Splitting into "
//											+ this.maxVolumeSizeInBytes/DtbSplitterMergerConstants.volumeSizeMeasureUnit+" "
//											+ DtbSplitterMergerConstants.volumeSizeMeasureUnitName 
//											+ " big volumes is not possible. Some files are too big to fit.";
					throw new MaxVolumeSizeExceededException("");
				}
                
			}else{
			/*
			 * The resource does not fit and we ARE collecting.
			 * When collecting we are not allowed to split, 
			 * that is start up a new volume here, therefore:
			 */
			 	throw new MaxVolumeSizeExceededException("");
				/* TODO perhaps one should point to the place in ncc 
				 * where the current smil is referred from.
				 */
			}
			
			/* Now we can add the smil and its resources 
			 * (to the volume in which they still fit or 
			 * to the newly created volume)
			 */
			volume.addSmilResources(smilFile,false);
			volume.addSmilFile(smilFile);
							
		}// End of the while loop,
		 // We have gone through all the referrents in the ncc document. 
		 //The volumes have been created and placed in the volumeSet.
		 
		this.fillVolumesWithPrompts();//non title prompts
        //long end = new Date().getTime();
        //long timeSpanInMillisSec = end - start; 
 //       this.reportGenerator.sendMessage(Level.INFO, "VOLUMES_CREATION_TIME_IN_MILLIS", new Long(timeSpanInMillisSec));
		/* Ask the user to confirm the number and the sizes of the volumes.
		 * The user chooses whether to abort the application.
		 */
		if(this.isUserPromptOn()){
			this.confirmCalculatedVolumes();
		}
		
	}

    /*
	 * getSmilFileSetSize sums up the size of all files 
	 * referenced directly from the smil file and
	 * when it handles a full text resource it 
	 * checks first whether the volume contains the resource.
	 * If the resource has not yet been added to the volume,
	 * the fileSetsize is increased by the size of all non-smil files
	 * referenced from the resource.
	 * 
	 */
	private long getSmilFileSetSize(SmilFile smiFile, DtbVolume volume) {
		long fileSetSize = 0;
		fileSetSize += ((File)smiFile).length();

		Iterator<FilesetFile> i = smiFile.getReferencedLocalMembers().iterator();
		while (i.hasNext()) {
			FilesetFile file = i.next();
			if (file instanceof TextualContentFile && !volume.getFullTextFiles().contains(file)){
				TextualContentFile txtFile = (TextualContentFile)file;
				/*
				 * Full text resource has not yet been put into the volume.
				 * Increment the smil file set size.
				 */
				fileSetSize += txtFile.getFile().length();
				//Add the size of non-smil files from the full text resource.
				Iterator<FilesetFile> fullTxtReferencedLocalMembers = txtFile.getReferencedLocalMembers().iterator();
				while (fullTxtReferencedLocalMembers.hasNext()) {
					FilesetFile f = fullTxtReferencedLocalMembers.next();
					if(!(f instanceof SmilFile)){
						fileSetSize += ((File)f).length();
					}	
				}
				
			}else if(!(file instanceof XmlFile)){
				//it is not a full text nor ncc resource
				if(!volume.getResourceFiles().contains(file)){
					fileSetSize += ((File)file).length();	
				}	
			}
			
		}
		return fileSetSize;
	}

	private void confirmCalculatedVolumes() throws TransformationAbortedByUserException{
         
        this.reportGenerator.sendTransformationMessage(Level.INFO, "NUMBER_OF_VOLUMES", new Integer(this.volumeSet.size()));
		for(Iterator<DtbVolume> i=this.volumeSet.iterator(); i.hasNext();){
		
			DtbVolume v = i.next();
            this.reportGenerator.sendTransformationMessage(Level.INFO,"VOLUME_NR_AND_SIZE", new Integer(v.getVolumeNr()), new Long(v.getVolumeSize()/DtbSplitterMergerConstants.volumeSizeMeasureUnit));
		}
		
		//Ask the user whether to continue or not.
		String userResponse = this.reportGenerator.getTransformationUserInput(Level.INFO, "DO_YOU_WANT_TO_WRITE_VOLUMES_TO_DISK", "y");
        if(userResponse!=null && userResponse.equalsIgnoreCase("n")){
        	this.reportGenerator.sendTransformationMessage(Level.INFO,"VOLUMES_NOT_APPROVED");
            throw new TransformationAbortedByUserException("");
        }
        this.reportGenerator.sendTransformationMessage(Level.INFO,"WRITING_VOLUMES_PLEASE_WAIT", this.getOutputDir());
	}
	//adds the non title prompts to each volume
	private void fillVolumesWithPrompts() throws XmlParsingException, IOException{
		
		String language = this.retrieveBookLanguage();
		
		//retrieve an appropriate number of prompt file pairs
		int numberOfVolumes = this.volumeSet.size();
		this.promptFiles = new DtbPromptFiles(this.promptManifestFile, this.reportGenerator, language);
		
		Map<Integer,File> smilSet = promptFiles.getSmilSet(numberOfVolumes);
		HashMap<Integer,File> audioSet = promptFiles.getAudioSet(numberOfVolumes);
		
		for(Iterator<DtbVolume> volumes=this.volumeSet.iterator(); volumes.hasNext();){
			
			DtbVolume volume = volumes.next();
			int volNr = volume.getVolumeNr();
			
			for(int i=1; i<=numberOfVolumes; i++){
				
				if(i!=volNr){
				File smilPrompt = smilSet.get(new Integer(i));
				volume.addPromptFile(smilPrompt);
				File audioPrompt = audioSet.get(new Integer(i));
				volume.addPromptFile(audioPrompt);
				}
					
			}//move to the next file pair
			
		}//move to the next volume
	}
	
	protected boolean isKeepInputDtb() {
		return this.keepInputDtb;
	}

	protected DtbVolumeSet getVolumeSet() {
		return this.volumeSet;
	}

	protected DtbTransformationReporter getReportGenarator() {
		return this.reportGenerator;
	}
	
	protected void setReportGenerator(DtbTransformationReporter rg) {
		this.reportGenerator =rg;
	}

	protected DtbPromptFiles getPromptFiles(){
		return promptFiles;
	}
	
	protected File getPromptManifestFile() {
		return promptManifestFile;
	}

	/**
	 * @return Returns the outputDir.
	 */
	protected File getOutputDir() {
		return outputDir;
	}

    protected XMLInputFactory getXMLInputFactoryInstance() throws CatalogExceptionNotRecoverable, IllegalArgumentException {
        if(this.factory==null){
            this.factory =  XMLInputFactory.newInstance();
            this.factory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
            this.factory.setProperty("javax.xml.stream.resolver", new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        }
        return this.factory;
    }

	protected boolean isUserPromptOn() {
		return userPromptOn;
	}
	
	
	public void setKeepInputDtb(boolean keepInputDtb) {
		this.keepInputDtb = keepInputDtb;
	}
	public void setMaxSplitLevel(int level){
		this.maxSplitLevel = level;
	}
	public void setPromptManifestFile(File promptManifestFile) {
		this.promptManifestFile = promptManifestFile;
	}
	public void setUserPromptOn(boolean userPromptOn) {
		this.userPromptOn = userPromptOn;
	}
}


