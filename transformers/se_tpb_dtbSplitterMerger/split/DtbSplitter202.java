


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

package se_tpb_dtbSplitterMerger.split;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.Referable;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202MasterSmilFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se_tpb_dtbSplitterMerger.DtbParsingInitializer;
import se_tpb_dtbSplitterMerger.DtbProcessingSpan;
import se_tpb_dtbSplitterMerger.DtbPromptFiles;
import se_tpb_dtbSplitterMerger.DtbSerializer;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.DtbValidator;
import se_tpb_dtbSplitterMerger.DtbVolume;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;

/**
 * DtbSplitter202 is meant for splitting DTB file sets in Daisy 2.02.
 * 
 * @author Piotr Kiernicki
 */
public class DtbSplitter202 extends DtbSplitter {
	
	private Map bookInfo = null;
	private final static String DC_FORMAT = "dc:format";
	private final static String DC_IDENTIFIER ="dc:identifier";
	private final static String DC_TITLE = "dc:title";
	private final static String DC_CREATOR = "dc:creator";
	private D202NccFile nccFile;
	private Document nccFileDocument;
	private D202MasterSmilFile masterSmilFile;
	private String dirPrefix = null;
	
    public DtbSplitter202(Fileset inputFileset, File outDir, File promptManifest, long maxVolSize, DtbTransformationReporter reportGen, String dirPrefix) throws XmlParsingException{
        super(outDir, promptManifest, maxVolSize, reportGen);
		try {
			for(Iterator i=inputFileset.getLocalMembers().iterator();i.hasNext();){
				Object file = i.next();
				if(file instanceof D202NccFile){
					this.nccFile = (D202NccFile)file;
					this.nccFileDocument = this.nccFile.asDocument(false);
				}else if(file instanceof D202MasterSmilFile){
					this.masterSmilFile = (D202MasterSmilFile)file;
				}
			}
			this.dirPrefix = dirPrefix;
		} catch (Exception e) {
            throw new XmlParsingException(this.nccFile.getFile().getAbsolutePath());
		}

    }	
    
	public void executeSplitting() throws TransformationAbortedByUserException, XmlParsingException, MaxVolumeSizeExceededException, IOException{
		DtbTransformationReporter rg = super.getReportGenarator();
		if(this.isUserPromptOn()){	
            rg.sendTransformationMessage(Level.INFO,"INITIALIZING_SPLITTING");
        }
		//rg.startTiming();////moved to DtbSpltterTransformer
		super.createVolumes(this.getBasePlaySequenceSmilCollection());
        
        //The volumes have been created and placed in the volumeSet.
		// Inform the report generator about the created volume objects
		//TODO Remove RG
		//rg.setOutputVolumes(this.getVolumeSet());
		this.saveVolumes();
		//TODO Remove RG
		//this.reportBookInfo(); 
		//rg.stopTiming();////moved to DtbSplitterTransformer
	}

	/* (non-Javadoc)
	 * @see org.daisy.dtbsm.DtbSplitter#getBasePlaySequenceCollection()
	 */
	protected Collection getBasePlaySequenceSmilCollection() {
		Collection smilFiles = new ArrayList();
		for(Iterator i=this.nccFile.getReferencedLocalMembers().iterator();i.hasNext();){
			Object file = i.next();
			if(file instanceof D202SmilFile){
                smilFiles.add(file);
			}
		}
		return smilFiles;
	}

    /*
     * Returns the reference level of a file 
     * in the structure of the current navigation control file 
     */
    protected int getFileReferenceLevel(String fileName) throws XmlParsingException{
        int refLevel = 0;
        if(fileName.equals("vale008.smil")){
            System.err.println("vvv");
        }
        try {
            XMLInputFactory factory = super.getXMLInputFactoryInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream((File)this.nccFile));
            while (parser.hasNext()) {
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    
                    if(parser.getLocalName().equalsIgnoreCase("h1")){
                        refLevel = 1;
                    }else if(parser.getLocalName().equalsIgnoreCase("h2")){
                        refLevel = 2; 
                    }else if(parser.getLocalName().equalsIgnoreCase("h3")){
                        refLevel = 3;
                    }else if(parser.getLocalName().equalsIgnoreCase("h4")){
                        refLevel = 4;
                    }else if(parser.getLocalName().equalsIgnoreCase("h5")){
                        refLevel = 5;
                    }else if(parser.getLocalName().equalsIgnoreCase("h6")){
                        refLevel = 6;
                    }
                    
                    if(this.isFileNameInChildAnchorHref(parser, fileName)){
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new XmlParsingException(this.nccFile.getFile().getAbsolutePath());
        }
        return refLevel;        
    }
    private boolean isFileNameInChildAnchorHref(XMLStreamReader parser, String fileName) throws XMLStreamException{
        boolean retVal = false;
        if(parser.next() == XMLStreamConstants.START_ELEMENT
            && parser.getLocalName().equalsIgnoreCase("a")
            && parser.getAttributeValue(null, "href").startsWith(fileName)){
            retVal = true;
        }

        return retVal;
    }

    protected String retrieveBookLanguage(){
		
		String language = null;
		
		NodeList metaElementList = this.getNccFileDocument().getElementsByTagName("meta");
		
		if(metaElementList!=null){
			for(int i=0; i<metaElementList.getLength(); i++){
				Element meta = (Element)metaElementList.item(i);
		
				if( meta.getAttribute("name").equalsIgnoreCase("dc:language")){
					language = meta.getAttribute("content").toLowerCase();
					
					if(language.length()>2){
						language = language.substring(0,2);//in case it is sth like en-US
					}
				}
			}
		}
		
		return language;
	}
	/*
	 * Adds the files that follow with each volume 
	 */
	protected DtbVolume initializeNewVolume(int volumeNr){

		DtbVolume vol = new DtbVolume(volumeNr, super.getOutputDir(), super.getReportGenarator(), this.dirPrefix);
		if(this.masterSmilFile!=null){//master.smil is optional
            //TODO is this ok?
			// do not add master.smil as the FilsetImpl will fail when merging
			//vol.incrementVolumeSize(((File)this.masterSmilFile).length());
			//vol.addResourceFile(this.masterSmilFile);	
		}
		//add ncc and its non-smil references
		vol.incrementVolumeSize(((File)this.nccFile).length());
		Collection nccResources = super.getNonSmilReferences(this.nccFile);
		for(Iterator i=nccResources.iterator(); i.hasNext();){
            Referable file = (Referable)i.next();
			vol.incrementVolumeSize(file.getFile().length());
			vol.addResourceFile(file);
		}
			
		super.getVolumeSet().add(vol);
		return vol;
	}
	protected void saveVolumes() throws TransformationAbortedByUserException, XmlParsingException, IOException{
		
		for(Iterator volumes = super.getVolumeSet().iterator();volumes.hasNext();){
			DtbVolume volume = (DtbVolume)volumes.next();		
			//Save each volume to a new directory.
			//Create the output directory for the volume.
			File outputVolumeDir = volume.getVolumeOutputDir();
			outputVolumeDir.mkdirs();
		
			this.modifyCopyNavigationFile(this.nccFile, volume);
			/* QUESTION Can there be more than one full text file???
			 * if it is always just one file, one should change the
			 * type of fullTextFiles from List to File.
			 */
			for(Iterator fullTxt = volume.getFullTextFiles().iterator(); fullTxt.hasNext();){
				this.modifyCopyNavigationFile(((TextualContentFile)fullTxt.next()), volume);
			}	
			String inputVolumeDir = this.nccFile.getFile().getParent();
			boolean checkRelativePath = true;
			
			//copy and assign the output file references (smil, resource, prompt) for the current volume
			List outputSmilFiles = volume.copyFilesIntoVolume(volume.getSmilFiles(), super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
			volume.setSmilFiles(outputSmilFiles);
			List outputResourceFiles = volume.copyFilesIntoVolume(volume.getResourceFiles(),super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
			volume.setResourceFiles(outputResourceFiles);
			//reset the checkRelativePath value to false for the prompt files
			checkRelativePath = false;
			boolean doNotDeleteSourcePromptFiles = true;
			
			List outputPromptFiles = volume.copyFilesIntoVolume(volume.getPromtFiles(),super.isUserPromptOn(), doNotDeleteSourcePromptFiles, inputVolumeDir, checkRelativePath);
			volume.setPromptFiles(outputPromptFiles);
			this.addBookInfoToSmilPrompts(outputPromptFiles);
		
		}

	}

	/*
	 * modifyCopyNavigationFile creates a modified copy of the html file, 
	 * that is a copy of ncc.html or a full text html file, 
	 * and puts it in the output directory.
	 */
	private void modifyCopyNavigationFile(XmlFile inputNavigationFile, DtbVolume volume) throws XmlParsingException, IOException{
		
		File volumeOutputDir = volume.getVolumeOutputDir();
		Document doc = null;
		try {
			doc = inputNavigationFile.asDocument(false);
		} catch (Exception e) {
			throw new XmlParsingException(inputNavigationFile.getFile().getAbsolutePath(),e);
		}
	
		//Change the appropriate attribute values to prompt file names.
		Document modifiedDoc = (Document)this.modifyNavigationDoc(doc, volume);
	
		String outputFilePath = volumeOutputDir.getAbsolutePath() + File.separator + inputNavigationFile.getName();
	
		File navigationFile = DtbSerializer.serializeDaisy202DocToFile(modifiedDoc, outputFilePath);
		navigationFile.setReadOnly();
		
		//validate the output document
		DtbValidator v = new DtbValidator(super.getReportGenarator());
		v.dtdSaxValidate(new File(outputFilePath));
		/* Delete the original file when the application exits
		 * if the user wishes so.
		 */
		if(!super.isKeepInputDtb()){
			inputNavigationFile.getFile().deleteOnExit();	
		}
		
	}
	
	/* 
	 * modifyNavigationDoc
	 * 1. modifies <meta name="ncc:setInfo"...> element 
	 * 2. modifies <a> elements by changing the appropriate href attribute values 
	 *    to the respective announce*.smil.
	 */

	private Document modifyNavigationDoc(Document navigationDoc, DtbVolume volume) throws  XmlParsingException, IOException{
		Document modifiedDoc = null;
		/* 1.
		 * Modify the appropriate <meta> element's content attribute:
		 * <meta name="ncc:setInfo" content="... of ..." />
		 */
		modifiedDoc = this.modifyElement(	navigationDoc, "meta", 
												"name", "ncc:setInfo",
												"content", volume.getVolumeNr()+" of "+ super.getVolumeSet().size());		
		/*2.
		 * Modify href values in appropriate <a> elements.
		 */
		modifiedDoc = this.distributePromptAttributes(navigationDoc, volume, "a", "href");
			
		return modifiedDoc;		
	}

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
	 * Modifies given element type instances in the document 
	 * by changing the given attribute type value to an appropriate prompt smil.
	 * 
	 * I do apologize for the algorithm's complexity but this is the best I could do :).
	 * Any suggestions of a simpler way will be most welcome.
	 *  
	 */
	private Document distributePromptAttributes(Document navigationDoc, 
												DtbVolume volume, 
												String navigationPointElement, 
												String attributeName) throws XmlParsingException, IOException{
		//Extract all the navigation elements into a node list.
		NodeList navElemList = navigationDoc.getElementsByTagName(navigationPointElement);
	
		StringBuffer currentSmilAttrValue = new StringBuffer();
		int currentSmilVolNr = 0;
		/* 
		 * For every navigation point element in the nodeList do the following:
		 * 
		 * 1 extract the currentSmil from  the (href/src) attr value,
		 *  
		 * 2 find to which volume the currentSmil belongs, 
		 *   in other words in which smilSet it can be found,
		 *   and set the volume number for currentSmil 
		 *   (set currentSmilVolNr),
		 * 
		 * 3 change the attr value if the currentSmil belongs 
		 *   to another volume than the one we are building now.
		 */ 		
		for(int i=0;i<navElemList.getLength();i++){
		 //1.
			Element currentNavNode = (Element)navElemList.item(i);
			Attr attrNode = currentNavNode.getAttributeNode(attributeName);
		
			currentSmilAttrValue.append(attrNode.getNodeValue());
			//clip XPath from the attr value
            int index = currentSmilAttrValue.toString().indexOf("#");
            if(index>-1){currentSmilAttrValue.setLength(index);}
            //clip relative path prefix
            index = currentSmilAttrValue.toString().lastIndexOf("/");
            if(index>-1){currentSmilAttrValue = currentSmilAttrValue.delete(0,index+1);}
			//currentSmil contains only the file name now.
	 
		 //2.
			/* Check to which volume currentSmil belongs.
			 */
			// Get smilSets.
			HashMap volSmilSets = super.getVolumeSet().extractSmilSet();
			
			/* volSmilSets is a HashMap collection of Lists with smil files 
			 * for each volume of the book.
			 * 
			 * The keys in the HashMap are Integer instances corresponding 
			 * to the respective volume numbers.
			 */
	
			//For every volume smil set do the following:
			for(int volNr=1; volNr<=volSmilSets.size(); volNr++){
				
				List aVolumeSmilSet = (List)(volSmilSets.get(new Integer(volNr)));
				Iterator set = aVolumeSmilSet.iterator();
			
				while(set.hasNext()){
					String aVolSmil = ((File)set.next()).getName();
					if(aVolSmil.equals(currentSmilAttrValue.toString())){
						/* Now that we have found the volume to which the currentSmil belongs,
						 * currentSmilVolNr can be set.
						 */
						currentSmilVolNr = volNr;
					}
				}
			
			}
			/* We have now traversed through the volume smil sets and found
			 * to which volume the currentSmil belongs, that is we have
			 * set the currentSmilVolNr to an appropriate value.
			 * 
			 * If the currentSmil does not belong to the volume we are building now,
			 * change the value of the href to an appropriate prompt smil.
			 * 
			 * Otherwise do nothing and leave the href value as it is. 
			 */ 
	  
		  
			if(volume.getVolumeNr()!= currentSmilVolNr){
		// 3. change the attr value to a prompt	
				DtbPromptFiles promptFiles = super.getPromptFiles();
				
				//handle title navigation element
				if(i==0){
					String titlePrompt = this.handleTitlePrompt(currentSmilAttrValue.toString(), volume);
					attrNode.setNodeValue(titlePrompt);
					//move to the next anchor element
					continue;
				}
				//handle other navigation elements
				// Extract the smilPromtFile appropriate for the currentSmilVolNr.
				File smilPromptFile = promptFiles.getSmilPromt(currentSmilVolNr);

				//Modify the attrNode's value.
				attrNode.setNodeValue(smilPromptFile.getName() + "#" + promptFiles.getSmilParId(currentSmilVolNr));
			
				//Add a rel attribute to the currentNavNode.
				currentNavNode.setAttribute("rel", currentSmilVolNr + " of " + super.getVolumeSet().size());
			
				/*
				 * Build a collection with prompt files.
				 * Add a smil file and its corresponding audio file.
				 *
				if (!volume.getPromtFiles().contains(smilPromptFile)){
					smilPromptFile.setType(DtbFile.PROMPT_FILE);
					volume.addPromptFile(smilPromptFile);	
				
					DtbFile audioPromptFile = promptFiles.getAudioPromt(currentSmilVolNr);
					audioPromptFile.setType(DtbFile.PROMPT_FILE);
					volume.addPromptFile(audioPromptFile);
				}*/			
			}//attr value changed
		
			currentSmilAttrValue.setLength(0);
		}//we have looped through all the navigation point elements.
	
		return navigationDoc;
	}
	
	private String handleTitlePrompt(String titleSmilName, DtbVolume volume) throws XmlParsingException, IOException{
		
		String titlePromtHrefValue = super.getVolumeSet().getTitlePromptHrefValue();
		
		if(titlePromtHrefValue==null){
			/* It is the first volume so we have to generate the titlePromtHrefValue
			 * and set it for the volume set and further reusage in the following volumes.
			 * 
			 * Via this.handleTitlePromptAudio and this.handleTitlePromptSmil
			 * we set the title audio and smil for the volume set and further reusage.
			 */ 
			AudioFile titleAudio = null;
			D202SmilFile titleSmil = null;
			
			//Get a handle to the title smil file, which is in volume one
			DtbVolume firstVolume = ((DtbVolume)super.getVolumeSet().get(0));
			titleSmil = (D202SmilFile)firstVolume.getTitleSmil();
			
			//handle smil prompt file ==========
			titlePromtHrefValue = this.handleTitlePromptSmil(titleSmil, volume);
			super.getVolumeSet().setTitlePromptHrefValue(titlePromtHrefValue);			

			//handle audio prompt file ==========
			Collection smilResources = titleSmil.getReferencedLocalMembers();
			Iterator i = smilResources.iterator();
			while(i.hasNext()){
				Object f = i.next();
				if(f instanceof AudioFile){
					titleAudio = (AudioFile)f;
					//the first audio file is found, break the loop
					break;
				}
			}
			this.handleTitlePromptAudio(titleAudio, volume);					
				
		}else{
			/* It is one of the following volumes we can just 
			 * get the prompts from the volume set and 
			 * pass them on to the volume. 
			 */
			volume.addPromptFile(super.getVolumeSet().getTitlePromptSmil());
			volume.addPromptFile(super.getVolumeSet().getTitlePromptAudio());
	
		}
		
		return titlePromtHrefValue;
	}
	
	private void handleTitlePromptAudio(AudioFile titleAudio, DtbVolume volume) throws IOException{
		File titlePromptAudio = titleAudio.getFile();
        //it will be copied together with ther promt files
		//File outTitlePromptAudio = new File(volume.getVolumeOutputDir().getAbsolutePath()+File.separator+titlePromptAudio.getName());
        //FileUtils.copyFile(titlePromptAudio, outTitlePromptAudio);
		super.getVolumeSet().setTitlePromptAudio(titlePromptAudio);	
	}
	
	private String handleTitlePromptSmil(D202SmilFile titleSmil, DtbVolume volume) throws XmlParsingException, IOException{
	/* 1. Parse titleSmil with DOM
	 * 2. Remove all par nodes except the title par 
	 * 3. Save what is left into a new title prompt file
	 * 4. Generate the titlePromtHrefValue 
	 */

	//1.
		DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());
		Document titleSmilDoc = parseInit.parseDocWithDOM(titleSmil.getFile());
		Element smilElement = titleSmilDoc.getDocumentElement();
	//get body element
		NodeList nodeList = smilElement.getElementsByTagName("body");
		Element bodyElement = (Element)nodeList.item(0);
	//get seq element (the direct and 'only' child of body)
		nodeList = bodyElement.getElementsByTagName("seq");
		Element seq = (Element)nodeList.item(0);
		
	//set a new value for the dur attribute of the seq element
		nodeList = bodyElement.getElementsByTagName("audio");
		Element titleAudio = (Element)nodeList.item(0);
		String clipEndValue = titleAudio.getAttribute("clip-end");
		//Since clip-end value looks like this: clip-end="npt=9.775s"
		//we must extract only what follows the 'npt=' part of the attribute value, 
		//that is from the fifth (index 4) character inclusive onwards
		String newDur = clipEndValue.substring(4, clipEndValue.length());
		
		seq.setAttribute("dur", newDur);

	//modify meta element where name="ncc:timeInThisSmil" and 
	//set a new value for the content attribute
		double d = 0;
		try {										//clip the 's' suffix
			d = Double.parseDouble(newDur.substring(0, newDur.length() - 1));
		} catch (NumberFormatException e) {
			super.getReportGenarator().sendTransformationMessage(Level.WARNING, "WRONG_CLIP_END_VALUE", titleSmil.getFile().getAbsolutePath(), clipEndValue);
		}

		//convert milliseconds to hh:mm:ss format
		String timeInThisSmil = DtbProcessingSpan.timeInHhMmSs(Math.round(d));
		this.modifyElement(	titleSmilDoc, "meta",
							"name", "ncc:timeInThisSmil", 
							"content", timeInThisSmil);
							
	/* 2. 
	 * Go through the list and remove all the par nodes
	 * except for the first one.
	 */
		nodeList = seq.getChildNodes();
		boolean firstParFound = false;
		for(int i=0; i<nodeList.getLength(); i++){
			Node n = nodeList.item(i);
			/*It is VERY likely that apart from 'par' nodes
			* the node list l contains empty #text nodes, 
			* therefore check whether it is a par node.
			*/
			if(!firstParFound && n.getNodeName().equals("par")){
				firstParFound = true;
			}else if(n.getNodeName().equals("par")){
				n.getParentNode().removeChild(n);
			}
	
		}

	//3.		
		
		//serialize the modified document to a file on disk
		/* check first though whether the document contains a link back
		 * text element with a relative path value in the 'src' attribute
		 */
		if(!this.nccFile.getFile().getParent().equalsIgnoreCase(titleSmil.getFile().getParent())){
			NodeList txtNodes = titleSmilDoc.getElementsByTagName("text");
			Node textNode = txtNodes.item(0);
			//clip the relative path and leave the direct file reference
			String srcValue = textNode.getAttributes().getNamedItem("src").getNodeValue();
			String srcNewValue = srcValue.substring(srcValue.lastIndexOf("/")+1, srcValue.length());
			this.modifyElement(titleSmilDoc, textNode.getNodeName(), "src",srcValue,"src", srcNewValue);
		}
		//get the title prompt file name 
		String titlePromptSmilName = DtbPromptFiles.TITLE_SMIL_FILE_NAME;
		
		String filePath = volume.getVolumeOutputDir().getAbsolutePath() + File.separator + titlePromptSmilName;
        File titlePromptSmil = null;
		
		titlePromptSmil = DtbSerializer.serializeDaisy202DocToFile(titleSmilDoc, filePath);
		titlePromptSmil.setReadOnly();
		
		//validate titlePromptSmil
		DtbValidator v = new DtbValidator(super.getReportGenarator());
		v.dtdSaxValidate(titlePromptSmil);
		
		super.getVolumeSet().setTitlePromptSmil(titlePromptSmil);
		
	//4.
		//get XPath
		NodeList a = titleSmilDoc.getElementsByTagName("text");
		Element audio = (Element)a.item(0);
		String promptXPath = "#" + audio.getAttribute("id");
			
		String titlePromtHrefValue = titlePromptSmilName + promptXPath;
			
		return titlePromtHrefValue;
			
	}
//TODO Remove RG
//	protected void reportBookInfo() throws DtbException{
//		super.getReportGenarator().setBookInfo(this.getBookInfo());		
//	}

	private void setBookInfo(){
	
		this.bookInfo = new HashMap();
		Document doc = this.getNccFileDocument();
			
		NodeList metaElemList = doc.getElementsByTagName("meta");
		for(int i=0; i<metaElemList.getLength(); i++){
			Element m = (Element)metaElemList.item(i);
			String nameValue = m.getAttribute("name");
			
			if(nameValue.length()>0){
				String contentValue = m.getAttribute("content");
				
				if(contentValue.length()==0){
					//there is no value specified for the content attribute
					//move to the next element in the loop
					continue;
				}
				
				if(nameValue.equalsIgnoreCase(DtbSplitter202.DC_FORMAT)){
					bookInfo.put(DtbSplitter202.DC_FORMAT, contentValue);
				
				}else if(nameValue.equalsIgnoreCase(DtbSplitter202.DC_IDENTIFIER)){
					bookInfo.put(DtbSplitter202.DC_IDENTIFIER, contentValue);
				
				}else if(nameValue.equalsIgnoreCase(DtbSplitter202.DC_TITLE)){
					bookInfo.put(DtbSplitter202.DC_TITLE, contentValue);
					
				}else if(nameValue.equalsIgnoreCase(DtbSplitter202.DC_CREATOR)){
					bookInfo.put(DtbSplitter202.DC_CREATOR, contentValue);
					
				}

			}
		}//move to the next element in the loop

	}
	
	private Map getBookInfo(){
		if(this.bookInfo==null){
			this.setBookInfo();
		}
		return this.bookInfo;
	}
	
	private void addBookInfoToSmilPrompts(List promptFiles) throws XmlParsingException, IOException{
		Map bookInfo = this.getBookInfo();
		
		String dcIdentifier = (String)bookInfo.get(DtbSplitter202.DC_IDENTIFIER);
		String dcFormat = (String)bookInfo.get(DtbSplitter202.DC_FORMAT);
		
		DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());
		
		for(Iterator prompts=promptFiles.iterator(); prompts.hasNext();){
			File file = (File)prompts.next();
			String fileName = file.getName().toLowerCase();
			
			if(fileName.endsWith(".smil") && !fileName.equals(DtbPromptFiles.TITLE_SMIL_FILE_NAME)){
				/* Handle only the smil prompt files from the resources folder.
				 * The title.smil is generated from the book's title smil 
				 * and as such should already contain the book info data.
				 */
				Document smilPromptDoc = parseInit.parseDocWithDOM(file);
				
				Element dcFormatMetaElem = smilPromptDoc.createElement("meta");
				Element dcIdentifierMetaElem = smilPromptDoc.createElement("meta");
				
				NodeList headList = smilPromptDoc.getElementsByTagName("head");
				Node headNode = headList.item(0);
				if(headNode!=null){
					dcFormatMetaElem.setAttribute("name", DtbSplitter202.DC_FORMAT);
					dcFormatMetaElem.setAttribute("content", dcFormat);
					headNode.insertBefore(dcFormatMetaElem, headNode.getFirstChild());
										
					dcIdentifierMetaElem.setAttribute("name", DtbSplitter202.DC_IDENTIFIER);
					dcIdentifierMetaElem.setAttribute("content", dcIdentifier);
					headNode.insertBefore(dcIdentifierMetaElem, headNode.getFirstChild());
				}
				DtbSerializer.serializeDaisy202DocToFile(smilPromptDoc, file.getAbsolutePath());
			}
		}//move to the next prompt file
		
	}

	private Document getNccFileDocument() {
		return nccFileDocument;
	}
	

}
