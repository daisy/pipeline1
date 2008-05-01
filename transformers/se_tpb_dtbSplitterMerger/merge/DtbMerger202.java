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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Referable;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se_tpb_dtbSplitterMerger.DtbParsingInitializer;
import se_tpb_dtbSplitterMerger.DtbPromptFiles;
import se_tpb_dtbSplitterMerger.DtbSerializer;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.DtbValidator;
import se_tpb_dtbSplitterMerger.DtbVolume;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;

/**
 * DtbMerger202 is meant for merging DTB file sets in Daisy 2.02.
 * 
 * @author Piotr Kiernicki
 */
@SuppressWarnings("unchecked")
public class DtbMerger202 extends DtbMerger {

    public DtbMerger202(List<File> inFiles, File outDir, DtbTransformationReporter rg){
        super(inFiles,outDir,rg);
    }

	public void executeMerging() throws TransformationAbortedByUserException, IOException, XmlParsingException, FilesetFatalException{
	 	
	   //Initialize the output volume.
	   super.getOutputDir().mkdirs();
	   DtbVolume outputVolume = new DtbVolume(super.getOutputDir(), super.getReportGenarator());

	   //1. Merge input NCCs.
	   this.mergeXhtmlFilesWithDOM(super.getInputFiles());
	   /*2.
		* For each volume: 
		*  2.1 Load the ncc with its referrents and and retrieve its resources
		*      into the appropriate collections of the output volume.
		*  2.2 Copy smil and resource files into the output volume directory. 
		*/

	   int inputVolNr = 0;
	 	
	   List<File> outputD202SmilFiles = new ArrayList<File>();
       List<File> outputResourceFiles = new ArrayList<File>();
	   List<File> outputPromptFiles = new ArrayList<File>();
       
		
	   for(Iterator NCCs = super.getInputFiles().iterator();NCCs.hasNext();){
		   DtbVolume tmpVolume = new DtbVolume(super.getOutputDir(), super.getReportGenarator());
		   inputVolNr++;
		   File inputVolNcc = (File)NCCs.next();
		   // 2.1 Fill the output volume's collections with smil, full text, resource and prompt files,
		   //     duplicates are excluded.
		   this.fillTmpVolume(inputVolNr, inputVolNcc, tmpVolume);
		   if( tmpVolume.getFullTextFiles().size()!=0){
			   outputVolume.addFullTextFile(tmpVolume.getFullTextFiles().get(0));
		   }
			
		   /* 2.2 Copy the files here for the sake of the possible subdirectory resources in the input volume.
			*     To be able to deal with subfolders we will need the current source volume's directory.
			*/
		   String inputVolumeDir = inputVolNcc.getParent(); 
		   boolean checkRelativePath = true;
			
		   List<File> smils = tmpVolume.copyFilesIntoVolume(tmpVolume.getSmilFiles(), super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
		   outputD202SmilFiles.addAll(smils);
							
		   List<File> audioFiles = tmpVolume.copyFilesIntoVolume(tmpVolume.getAudioFiles(), super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
		   outputVolume.addAudioFiles(audioFiles);
           
           outputResourceFiles.addAll(tmpVolume.getResourceFiles());
		   outputPromptFiles.addAll(tmpVolume.getPromtFiles());
			
	   }//move to the next inputVolNcc and do the same
		
	   outputVolume.setSmilFiles(outputD202SmilFiles);
	   outputVolume.setResourceFiles(outputResourceFiles);
	   outputVolume.setPromptFiles(outputPromptFiles);
	   

	   //3. Now we can merge the full text documents.
	   File outputFullTextFile = this.mergeXhtmlFilesWithDOM(outputVolume.getFullTextFiles());
	   if(outputFullTextFile!=null){
		   List fullTextFiles = new ArrayList();
		   fullTextFiles.add(outputFullTextFile);
		   outputVolume.setFullTextFiles(fullTextFiles);
	   }
	   //4. Handle resource files
	   this.handleDuplicatedResourceFiles(super.getInputFiles(), outputVolume);
	  // this.handleDuplicatedResourceFiles(outputVolume.getFullTextFiles(), outputVolume);
	   
	   //5. Finally, handle redundnant files.
	   if(super.isKeepRedundantFiles()){
		   this.handleRedundantFiles(outputVolume);
	   }
	//TODO Remove RG	
//	   //Inform the report generator about the created merged volume
//	   List outputVolumes = new ArrayList();
//	   outputVolumes.add(outputVolume);
//	   super.getReportGenarator().setOutputVolumes(outputVolumes);
//	   this.reportBookInfo();
				
	   if(!super.isKeepInputDtb()){
		   this.removeInputVolumesFiles();
	   }
   }
 
	private void handleDuplicatedResourceFiles(List<File> navigationFiles, DtbVolume outputVolume) throws TransformationAbortedByUserException, IOException, FilesetFatalException {
		
		if(navigationFiles!=null && navigationFiles.size()>0){
			List<FilesetFile> files = new ArrayList<FilesetFile>();
			File navigationFile = navigationFiles.get(0);
			String inputVolumeDir = navigationFile.getParentFile().getAbsolutePath(); 
			Fileset navigationFileset = null;
			try {
				boolean throwExceptionOnError = false;
                boolean dtdValidate = true;
                navigationFileset = new FilesetImpl(navigationFile.toURI(), new DefaultFilesetErrorHandlerImpl(throwExceptionOnError),dtdValidate);
			} catch (FilesetFatalException e) {
				this.getReportGenarator().sendTransformationMessage(Level.SEVERE, "FILESET_COULD_NOT_BE_BUILT", navigationFile.getAbsolutePath());
                throw e;
			}
			Iterator<FilesetFile> members=navigationFileset.getLocalMembers().iterator();
			while(members.hasNext()){
				Object f = members.next();
				if(!(f  instanceof D202NccFile 
						|| f  instanceof D202TextualContentFile 
						|| f  instanceof SmilFile 
						|| f instanceof AudioFile)){
					files.add((FilesetFile)f);
				}
			}
			List resources = outputVolume.copyFilesIntoVolume(files, super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, true);
			outputVolume.getResourceFiles().addAll(resources);
		}

	}

	private File mergeXhtmlFilesWithDOM(List<?> inputXhtmlFiles) throws XmlParsingException, IOException{
		//mg inparam list here seems to be either FilesetFile or File
		File outputXhtmlFile = null;

		File firstVolumeXhtmlFile = null;
		
		//Get an input xhtml document 
		if(inputXhtmlFiles.size()>0){
			if(inputXhtmlFiles.get(0) instanceof File) {
				firstVolumeXhtmlFile = (File)inputXhtmlFiles.get(0);
			}else{
				firstVolumeXhtmlFile = ((FilesetFile)inputXhtmlFiles.get(0)).getFile();
			}	
		}else{
			//There are no full text documents
			return null;
		}
	
		//and instantiate a handle to the output document.
		String outputXhtmlFilePath = super.getOutputDir() + File.separator + firstVolumeXhtmlFile.getName();
			
		DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());

	   /* Retrieve the non prompt anchor elements
		* and put them in a Map where the keys are 
		* the id values of the parent of the anchor element. 
		*/
		HashMap<String,Element> nonPromptAnchorElements = new HashMap<String,Element>();
		for(Iterator<?> input=inputXhtmlFiles.iterator(); input.hasNext();){
			Object o = input.next();
			File inputFile = null;
			if(o instanceof File) {
				inputFile = (File)o;
			} else{
				inputFile =((FilesetFile)o).getFile();
			}
			
			Document doc = parseInit.parseDocWithDOM(inputFile);
			
			NodeList anchorList = doc.getElementsByTagName("a");
		
			for(int i=0; i<anchorList.getLength(); i++){
				Element a = (Element)anchorList.item(i);
				Node aParent = a.getParentNode();
				String id = ((Element)aParent).getAttribute("id");
		
				if(a.getAttribute("rel").length()==0){
					nonPromptAnchorElements.put(id, a);
				}
			}
		}

	   /* Take an xhtml document and reset the attributes in all prompt anchor elements;
		* reset the href value and remove the rel attribute.
		*/
		Document firstVolumeDoc = parseInit.parseDocWithDOM(firstVolumeXhtmlFile);

		//modify the meta tag with name="ncc:setInfo"
		this.modifyElement(firstVolumeDoc, "meta", "name", "ncc:setInfo", "content", "1 of 1");

		NodeList firstVolAnchorList = firstVolumeDoc.getElementsByTagName("a");
		for(int i=0; i<firstVolAnchorList.getLength(); i++){
		
			Element anchor = (Element)firstVolAnchorList.item(i);
			Node anchorParent = anchor.getParentNode();
			String id = ((Element)anchorParent).getAttribute("id");
			
			if(anchor.getAttribute("rel").length()>0){
				//get the matching non prompt anchor from the HashMap
				Element a = nonPromptAnchorElements.get(id);
				String hrefValue = a.getAttribute("href");
				
				anchor.setAttribute("href", hrefValue);
				anchor.removeAttribute("rel");
			}
			
		}

		outputXhtmlFile = DtbSerializer.serializeDaisy202DocToFile(firstVolumeDoc, outputXhtmlFilePath);
		outputXhtmlFile.setReadOnly();

		//validate outputFile
		DtbValidator v = new DtbValidator(super.getReportGenarator());
		v.dtdSaxValidate(new File(outputXhtmlFilePath));		
	
		return outputXhtmlFile;
   }

   /* QUESTION
	* modifyElement is exactly the same as in DtbSplitter202 
	* any idea of a class to put it into ???  
	*/
	private Document modifyElement(	Document doc, String elementName,
									String searchAttrName, String searchAttrValue, 
									String attrName, String attrNewValue){

		NodeList nodeList = doc.getElementsByTagName(elementName);
		
		for(int i=0; i<nodeList.getLength(); i++){
			Element currentNode = (Element)(nodeList.item(i));  
			
			//if elmemnt found, set the attrNewValue
			if(currentNode.getAttribute(searchAttrName).equalsIgnoreCase(searchAttrValue)){
				currentNode.setAttribute(attrName, attrNewValue);
			}
		}
		return doc;
		
	}

	/*
	 * Gives the output volume object references to its resources. 
	 */
	private void fillTmpVolume(int volNr, File inputVolumeNcc, DtbVolume tmpVolume) throws FilesetFatalException{
		Fileset inputVolNccFileset = null;
		try {
			boolean throwExceptionOnError = false;
			boolean dtdValidate = true;
			inputVolNccFileset = new FilesetImpl(inputVolumeNcc.toURI(), new DefaultFilesetErrorHandlerImpl(throwExceptionOnError),dtdValidate);
		} catch (FilesetFatalException e) {
            this.getReportGenarator().sendTransformationMessage(Level.SEVERE, "FILESET_COULD_NOT_BE_BUILT", inputVolumeNcc.getAbsolutePath());
            throw e;
		}
		
		List<AudioFile> titlePromptsAudio = new ArrayList<AudioFile>();//we catch the below via title promt smil and then remove from audio files collection
		Iterator<FilesetFile> i = inputVolNccFileset.getLocalMembers().iterator();
				
		while(i.hasNext()){
			Object file = i.next();
			
			//put the file in the appropriate collection
            if(file instanceof D202NccFile){
                //skip it
            }else if(file instanceof D202TextualContentFile){
                tmpVolume.addFullTextFile((D202TextualContentFile)file);
            }
            else if(file instanceof D202SmilFile && !(file instanceof D202MasterSmilFile)){ 
                D202SmilFile f = (D202SmilFile)file;
				if(f.getName().equals(DtbPromptFiles.TITLE_SMIL_FILE_NAME) && volNr>1){
					//this is just a prompt title smil
					tmpVolume.addPromptFile(f);
                    Collection<FilesetFile> members = f.getReferencedLocalMembers();
                    for(Iterator<FilesetFile> j=members.iterator();j.hasNext();){
                        Object o = j.next();
                        if(o instanceof AudioFile) {
                            titlePromptsAudio.add((AudioFile)o) ;
                        }
                    }
				}else if(f.getName().startsWith("cd_")||f.getName().startsWith("skiva_")){//FIXME rename all prompts with some prefix, e.g. dtbsm_prompt_cd1.smil
					tmpVolume.addPromptFile(f);
					tmpVolume.addSmilResources((D202SmilFile)file, true);
				}else{
                    tmpVolume.addSmilFile(f);
                    //tmpVolume.addSmilResources((D202SmilFile)file, false);
                }
				
			}
			else if(file instanceof AudioFile){
				tmpVolume.addAudioFile((AudioFile)file);
			}else{
			    tmpVolume.addResourceFile((Referable)file);         
            }

		}
       tmpVolume.getAudioFiles().removeAll(titlePromptsAudio);
       tmpVolume.getAudioFiles().removeAll(tmpVolume.getPromtFiles());

	}
	
	/*
	 * Copies the redundant files to the output volume redundant directory
	 * and adds the files to the output volume DtbVolume.redundnantFiles collection
	 */
	private void handleRedundantFiles(DtbVolume outputVolume) throws IOException{

		List outputRedundantFiles = outputVolume.getRedundantFiles();
		
		File outputVolumeDir = outputVolume.getVolumeOutputDir();
		File redundantFilesDir = new File(outputVolumeDir.getAbsolutePath()+File.separator+ DtbMerger.REDUNDANT_FILES_DIR_NAME);
		redundantFilesDir.mkdir();
	
		
		List inputPromptFiles = outputVolume.getPromtFiles();
		//merged outputVolume will not need the prompt files any more	
		outputVolume.setPromptFiles(null);

		for(Iterator promptFiles = inputPromptFiles.iterator();promptFiles.hasNext();){
			File f = (File)promptFiles.next();
            File outputRedundantFile = new File(redundantFilesDir.getAbsolutePath()+File.separator+f.getName());
            FileUtils.copyFile(f,outputRedundantFile);
            outputRedundantFiles.add(outputRedundantFile);
		}
	
	}

	//TODO Remove RG
	//
//	private void reportBookInfo() throws DtbException{
//		File ncc = (File)super.getInputFiles().get(0);;
//		
//		final String DC_FORMAT = "dc:format";
//		final String DC_IDENTIFIER ="dc:identifier";
//		final String DC_TITLE = "dc:title";
//		final String DC_CREATOR = "dc:creator";
//		
//		
//		HashMap bookInfo = new HashMap();
//		
//		DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());
//		Document doc = parseInit.parseDocWithDOM(ncc);
//			
//		NodeList metaElemList = doc.getElementsByTagName("meta");
//		for(int i=0; i<metaElemList.getLength(); i++){
//			Element m = (Element)metaElemList.item(i);
//			String nameValue = m.getAttribute("name");
//			
//			if(nameValue.length()>0){
//				String contentValue = m.getAttribute("content");
//				
//				if(contentValue.length()==0){
//					//there is no value specified for the content attribute
//					//move to the next element in the loop
//					continue;
//				}
//				
//				if(nameValue.equalsIgnoreCase(DC_FORMAT)){
//					bookInfo.put(DC_FORMAT, contentValue);
//				
//				}else if(nameValue.equalsIgnoreCase(DC_IDENTIFIER)){
//					bookInfo.put(DC_IDENTIFIER, contentValue);
//				
//				}else if(nameValue.equalsIgnoreCase(DC_TITLE)){
//					bookInfo.put(DC_TITLE, contentValue);
//					
//				}else if(nameValue.equalsIgnoreCase(DC_CREATOR)){
//					bookInfo.put(DC_CREATOR, contentValue);
//					
//				}
//
//			}
//		}//move to the next element in the loop
//		super.getReportGenarator().setBookInfo(bookInfo);		
//	}
}
