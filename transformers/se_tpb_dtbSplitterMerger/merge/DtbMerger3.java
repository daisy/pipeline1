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

package se_tpb_dtbSplitterMerger.merge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se_tpb_dtbSplitterMerger.DtbParsingInitializer;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.DtbVolume;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;

/**
 * DtbMerger3 is meant for merging DTB file sets in Daisy 3 (ANSI/NISO Z39.86-2002, ANSI/NISO Z39.86-2005) format.
 * 
 * @author Piotr Kiernicki
 */
public class DtbMerger3 extends DtbMerger {
	
	private static final String DIST_INFO_FILE_NAME = "distInfo.dinf";
	private File distInfoFile = null;
	
	private List<String> redundantFilesNames = new ArrayList<String>();
	
    public DtbMerger3(List<File> inFiles, File outDir, DtbTransformationReporter r){
        super(inFiles,outDir,r);
    }

	public void executeMerging() throws IOException, XmlParsingException, TransformationAbortedByUserException{
		//DtbTransformationReporter rg = super.getReportGenarator(); 
				 	
		//rg.startTiming();//moved to DtbMergerTransformer
	 	
		//Initialize the output volume.
		File outputVolumeDir = super.getOutputDir();
		outputVolumeDir.mkdirs();
		DtbVolume outputVolume = new DtbVolume(outputVolumeDir, super.getReportGenarator());
		
		this.handleRedundantFiles(outputVolume);
		
		/*
		* For each volume copy the files into the output volume directory 
		*/
		List<File> inputFiles = super.getInputFiles();
        for(Iterator<File> input = inputFiles.iterator(); input.hasNext();){
            Directory inputVolumeDir = new Directory((input.next()).getParentFile());
            Collection<File> files = inputVolumeDir.getFiles(true);
            outputVolume.copyFilesIntoVolume(files,super.isUserPromptOn(),super.isKeepInputDtb(),inputVolumeDir.getAbsolutePath(),true);
        }
        
//		TODO remove RG
		//
//		//Inform the report generator about the created merged volume
//		List outputVolumes = new ArrayList();
//		outputVolumes.add(outputVolume);
//		rg.setOutputVolumes(outputVolumes);
//		File packageFile = (File)inputFiles.get(0);
//		this.reportBookInfo(packageFile);
				
		if(!super.isKeepInputDtb()){
			super.removeInputVolumesFiles();
		}
		//stop the merge processing screen promt
		//rg.stopTiming();//moved to DtbMergerTransformer
	}
	
	private List<File> retrievePromptFiles() throws XmlParsingException{
		List<File> promptFiles = new ArrayList<File>();
		
		File volumeOneDir = super.getInputFiles().get(0).getParentFile();
		File volumeTwoDir = super.getInputFiles().get(1).getParentFile();
		
		this.distInfoFile = new File(volumeOneDir + File.separator + DtbMerger3.DIST_INFO_FILE_NAME);

		
		if(this.distInfoFile.exists()){
			
			DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());
			Document distInfoDoc = parseInit.parseDocWithDOM(distInfoFile);
			
			NodeList audioElements = distInfoDoc.getElementsByTagName("audio");
			for(int i=0; i<audioElements.getLength(); i++){
				
				String audioFileName = ((Element)audioElements.item(i)).getAttribute("src");
				File audioFile = null;
				if(i==0){//prompt nr 1 can be found in volume two
					audioFile = new File(volumeTwoDir + File.separator + audioFileName);
				}else{//all prompts but the first one should be found in the first volume
					audioFile = new File(volumeOneDir + File.separator + audioFileName);
				}
				
				if(audioFile.exists()){
					promptFiles.add(audioFile);
				}else{
//					DtbErrorMessage errorMsg = new DtbErrorMessage("File Not Found.", 
//																	audioFile.getAbsolutePath() 
//																	+" referred to in " + distInfoFile.getAbsolutePath()
//																	+ " could not be found.");
//					super.getReportGenarator().addErrorMessage(errorMsg);
                    super.getReportGenarator().sendTransformationMessage(Level.INFO,"MISSING_PROMPT_FILE",audioFile.getAbsolutePath());
				}
			}
			 
		}
		
		return promptFiles;
	}
	

	@SuppressWarnings("unchecked")
	private void handleRedundantFiles(DtbVolume outputVolume) throws XmlParsingException, IOException{
		List redundantFiles = outputVolume.getRedundantFiles();
		 	
		File redundantDir = new File(outputVolume.getVolumeOutputDir().getAbsolutePath() + File.separator + DtbMerger.REDUNDANT_FILES_DIR_NAME);
		if(super.isKeepRedundantFiles()){
			redundantDir.mkdir();
		}

		/* Retrieve a collection  with prompt files
		 * and copy them to the redundant files folder.
		 */ 
		List<File> promptFiles = this.retrievePromptFiles();//also sets this.distInfoFile
		for(Iterator<File> prompts=promptFiles.iterator(); prompts.hasNext();){
			File prompt = prompts.next();
			this.redundantFilesNames.add(prompt.getName());
						
			if(super.isKeepRedundantFiles()){
            	File outputRedundantFile = new File(redundantDir.getAbsolutePath()+File.separator+prompt.getName());
            	FileUtils.copyFile(prompt,outputRedundantFile);
                redundantFiles.add(outputRedundantFile);
			}
			
		}
		//copy the distInfo file
		if(super.isKeepRedundantFiles()){
            File redundantDistInfo = new File(redundantDir.getAbsolutePath()+File.separator+this.distInfoFile.getName());
            FileUtils.copyFile(this.distInfoFile,redundantDistInfo);
            redundantFiles.add(redundantDistInfo);//this.distInfoFile set by this.retrievePromptFiles()
		}
		
		this.redundantFilesNames.add(this.distInfoFile.getName()); 

	}
	

//	TODO remove RG
	//
	/*
	/*
	 * Overrides DtbSplitter.reportBookInfo()
	 *
	private void reportBookInfo(File packageFile) throws DtbException{
		
		DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());
		Document doc = parseInit.parseDocWithDOM(packageFile);
		
		HashMap bookInfo = new HashMap();
		
		this.setBookInfoItem("dc:Format", doc, bookInfo);
		this.setBookInfoItem("dc:Identifier", doc, bookInfo);
		this.setBookInfoItem("dc:Title", doc, bookInfo);
		this.setBookInfoItem("dc:Creator", doc, bookInfo);
		
		super.getReportGenarator().setBookInfo(bookInfo);
	}
	
	private void setBookInfoItem(String itemName, Document doc, HashMap bookInfo){

		NodeList list = doc.getElementsByTagName(itemName);
		
		if(list!=null){
			Element dcTitle = (Element)list.item(0);
			Node dcTitleText = dcTitle.getChildNodes().item(0);
			String title =  dcTitleText.getNodeValue();
			bookInfo.put(itemName.toLowerCase(), title);	
			
		}			
	}
	*/
}
