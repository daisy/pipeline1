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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Referable;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986OpfFile;
import org.daisy.util.fileset.Z3986ResourceFile;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se_tpb_dtbSplitterMerger.DtbParsingInitializer;
import se_tpb_dtbSplitterMerger.DtbPromptFiles;
import se_tpb_dtbSplitterMerger.DtbSerializer;
import se_tpb_dtbSplitterMerger.DtbTransformationReporter;
import se_tpb_dtbSplitterMerger.DtbVolume;
import se_tpb_dtbSplitterMerger.DtbVolumeSet;
import se_tpb_dtbSplitterMerger.TransformationAbortedByUserException;
import se_tpb_dtbSplitterMerger.XmlParsingException;

/**
 * DtbSplitter3 is meant for splitting DTB file sets in Daisy 3 (ANSI/NISO Z39.86-2002, ANSI/NISO Z39.86-2005) format.
 * 
 * @author Piotr Kiernicki
 */

@SuppressWarnings("unchecked")
public class DtbSplitter3 extends DtbSplitter {
    
    private final static String NCX_VERSION_Z3986_2002 = "1.1.0";
    private final static String NCX_VERSION_Z3986_2005 = "2005";
    
	private Z3986OpfFile opfFile;
	private Z3986NcxFile ncxFile;
    private Document opfDoc;
    private String dirPrefix = null;
	
    public DtbSplitter3(Fileset inputFileset, File outDir, File promptManifest, long maxVolSize, DtbTransformationReporter reportGen, String dirPrefix) throws XmlParsingException{
        super(outDir, promptManifest, maxVolSize, reportGen);
        
		try {
			for(Iterator<FilesetFile> i=inputFileset.getLocalMembers().iterator();i.hasNext();){
				Object file = i.next();
				if(file instanceof Z3986OpfFile){
					this.opfFile = (Z3986OpfFile)file;
					this.opfDoc = this.opfFile.asDocument(false);
				}else if(file instanceof Z3986NcxFile){
					this.ncxFile = (Z3986NcxFile)file;
				}
			}
			this.dirPrefix = dirPrefix;
		} catch (Exception e) {
			throw new XmlParsingException(this.opfFile.getFile().getAbsolutePath());
		}
    }
	
	public void executeSplitting() throws TransformationAbortedByUserException, MaxVolumeSizeExceededException, XmlParsingException, IOException{
		DtbTransformationReporter rg = super.getReportGenarator();
		if(this.isUserPromptOn()){	
            rg.sendTransformationMessage(Level.INFO,"INITIALIZING_SPLITTING");
        }
		//rg.startTiming();////moved to DtbSpltterTransformer
		super.createVolumes(this.getBasePlaySequenceSmilCollection());
        
        //The volumes have been created and placed in the volumeSet.
		// Inform the report generator about the created volume objects
//		TODO Remove RG
		//rg.setOutputVolumes(this.getVolumeSet());
		this.saveVolumes();
		//TODO Remove RG
		//this.reportBookInfo(); 
		//rg.stopTiming();////moved to DtbSplitterTransformer
	}
	
	protected Collection getBasePlaySequenceSmilCollection(){
        return this.opfFile.getSpineItems();
	}
    
    /*
     * Returns the reference level of a file 
     * in the structure of the current navigation control file 
     */
    protected int getFileReferenceLevel(String fileName) throws XmlParsingException{
        int refLevel = 0;
        
        try {
            XMLInputFactory factory = super.getXMLInputFactoryInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream((File)this.ncxFile));
            while (parser.hasNext()) {
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT 
                        && parser.getLocalName().equals("navPoint")) {
                    refLevel++;
                }else if(event == XMLStreamConstants.START_ELEMENT 
                        && parser.getLocalName().equals("content")
                        && parser.getAttributeValue(null, "src").toLowerCase().startsWith(fileName.toLowerCase())) {
                    break;
                }
                if (event == XMLStreamConstants.END_ELEMENT 
                        && parser.getLocalName().equals("navPoint")) {
                    refLevel--;
                }
            }
        } catch (Exception e) {
            throw new XmlParsingException(this.ncxFile.getFile().getAbsolutePath());
        }
        return refLevel;
    }
    
	protected DtbVolume initializeNewVolume(int volumeNr){

		DtbVolume vol = new DtbVolume(volumeNr, super.getOutputDir(),super.getReportGenarator(), this.dirPrefix);
		vol.incrementVolumeSize(((File)this.opfFile).length());
		vol.addResourceFile(this.opfFile);
		//add all resources except for smil audio and full text 
        for(Iterator<FilesetFile> i=this.opfFile.getReferencedLocalMembers().iterator();i.hasNext();){
            Referable file = i.next();
            if(file instanceof Z3986ResourceFile){
                vol.incrementVolumeSize(file.getFile().length());
                vol.addResourceFile(file);
                //add resource file's referables
                for(Iterator<FilesetFile> r=((Z3986ResourceFile)file).getReferencedLocalMembers().iterator();r.hasNext();){
                    Referable f = r.next();
                    vol.incrementVolumeSize(f.getFile().length());
                    vol.addResourceFile(f);
                }
            }
        }
        
		vol.incrementVolumeSize(this.ncxFile.getFile().length());
		vol.addResourceFile(this.ncxFile);
		Collection ncxResources = super.getNonSmilReferences(this.ncxFile);
		for(Iterator i=ncxResources.iterator(); i.hasNext();){
            Referable file = (Referable)i.next();
			vol.incrementVolumeSize(file.getFile().length());
			vol.addResourceFile(file);				
		}
		
		this.getVolumeSet().add(vol);
		return vol;
	}
    
	private Document getOpfDoc(){
		return this.opfDoc;
	}

	protected String retrieveBookLanguage(){
		String language = null;
		
		Document inputPackageFileDoc = this.getOpfDoc();
		
		NodeList list = inputPackageFileDoc.getElementsByTagName("dc:Language");
		if(list!=null && list.getLength()>0){
			Element dcLanguage = (Element)list.item(0);
			Node dcLanguageText = dcLanguage.getChildNodes().item(0);
			language = dcLanguageText.getNodeValue().toLowerCase();
			
			if(language.length()>2){
				language = language.substring(0,2);//in case it is sth like en-US
			}
			
		}
		
		return language;
	}
	
	private Document generateDistInfoDoc(DtbVolumeSet volumeSet) throws XmlParsingException{
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domBuilder = null;
		try {
			domBuilder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
            //TODO ignore?
			pce.printStackTrace();
		}
		String version = this.ncxFile.getRootElementAttributes().getValue("version");
		DOMImplementation domImpl = domBuilder.getDOMImplementation();
        DocumentType docType = null;
        if(version.equals(NCX_VERSION_Z3986_2002)){
            docType = domImpl.createDocumentType("distInfo","-//NISO//DTD distInfo v1.1.0//EN",
                                            "http://www.loc.gov/nls/z3986/v100/distInfo110.dtd");
        }else if(version.startsWith(NCX_VERSION_Z3986_2005)){
            //2005-1 or 2005-2
            docType = domImpl.createDocumentType("distInfo","-//NISO//DTD distInfo "+version+"//EN",
            "http://www.daisy.org/z3986/2005/distInfo-"+version+".dtd");
        }
        
		Document distInfoDoc = domImpl.createDocument(null, "distInfo", docType);
		distInfoDoc.setXmlVersion("1.0");
		//distInfoDoc.setXmlEncoding(this.opfDoc.getXmlEncoding());//TODO ???
		Element distInfoElement = distInfoDoc.getDocumentElement();		
		distInfoElement.setAttribute("version", version);
		
        Element bookElement = distInfoDoc.createElement("book");
        distInfoElement.appendChild(bookElement);
        if(version.startsWith("2005")){
		    distInfoElement.setAttribute("xmlns","http://www.daisy.org/z3986/2005/distInfo/");
            //set docTitle and docAuthor 
            Document ncxDoc = null;
            try {
                ncxDoc = this.ncxFile.asDocument(false);
            } catch (Exception e) {
                throw new XmlParsingException(this.ncxFile.getFile().getAbsolutePath(),e);
            }
            Element docTitleElement = distInfoDoc.createElement("docTitle");
            String docTitleElementTxt = ncxDoc.getElementsByTagName("docTitle").item(0).getFirstChild().getNextSibling().getTextContent();
            docTitleElement.appendChild(distInfoDoc.createTextNode(docTitleElementTxt));
            
            Element docAuthorElement = distInfoDoc.createElement("docAuthor");
            String docAuthorElementTxt = ncxDoc.getElementsByTagName("docAuthor").item(0).getFirstChild().getNextSibling().getTextContent();
            docAuthorElement.appendChild(distInfoDoc.createTextNode(docAuthorElementTxt));
            
            bookElement.appendChild(docTitleElement);
            bookElement.appendChild(docAuthorElement);
        }
        
		//set the book element's atributes ----------------------
		//get the text content of the element with an id="uid"
        Element uidElem = this.getOpfDoc().getElementById("uid");
        String uid = "";
		if(uidElem!=null){
            uid = uidElem.getChildNodes().item(0).getNodeValue();
        }else{
            //super.getReportGenarator().addErrorMessage(new DtbErrorMessage("uid not found in opf."));
            super.getReportGenarator().sendTransformationMessage(Level.WARNING,"UID_NOT_FOUND_IN_OPF",this.opfFile.getFile().getAbsolutePath());
        }
        bookElement.setAttribute("uid", uid);
		bookElement.setAttribute("pkgRef", "./" + this.opfFile.getName());
		int numberOfVolumes = volumeSet.size();
		bookElement.setAttribute("media", "1:" + numberOfVolumes);

		Element distMapElement = distInfoDoc.createElement("distMap");
		bookElement.appendChild(distMapElement);

		for(Iterator vols=volumeSet.iterator(); vols.hasNext();){
			DtbVolume volume = (DtbVolume)vols.next();
			int volNr = volume.getVolumeNr();
			List smilSet = volume.getSmilFiles();

			for(Iterator smils=smilSet.iterator(); smils.hasNext();){
				File smilFile = (File)smils.next();
				Element smilRefElement = distInfoDoc.createElement("smilRef");
				distMapElement.appendChild(smilRefElement);
				//TODO subfolders not handled
				smilRefElement.setAttribute("file", smilFile.getName());
				smilRefElement.setAttribute("mediaRef", volNr + ":" + numberOfVolumes);
			}
		}
		//build changeMsg elements
		List aVolumePrompts = volumeSet.get(0).getPromtFiles();
		if(aVolumePrompts!=null && aVolumePrompts.size()>0){		
			
			DtbPromptFiles promptFiles = super.getPromptFiles(); 
			HashMap smilPromtSet = promptFiles.getSmilSet(numberOfVolumes);
			HashMap audioPromtSet = promptFiles.getAudioSet(numberOfVolumes);
			final String INSERT_DISC = "Insert disc "; 
			
			for(int volNr=1; volNr<=numberOfVolumes; volNr++){
				File smilPrompt = (File)smilPromtSet.get(new Integer(volNr));
				
				DtbParsingInitializer parseInit = new DtbParsingInitializer(super.getReportGenarator());
				Document smilDoc = parseInit.parseDocWithDOM(smilPrompt);
				NodeList audioList = smilDoc.getElementsByTagName("audio");
				Element audioElement = (Element)audioList.item(0);//there should be only one
				String clipBeginAttrValue = null;
				if(audioElement.getAttribute("clip-begin").length()>0){//SMIL 1.0
					clipBeginAttrValue = audioElement.getAttribute("clip-begin"); 
					//handle npt=0.000s
					clipBeginAttrValue = clipBeginAttrValue.substring(4, clipBeginAttrValue.length()-1);
				}else if(audioElement.getAttribute("clipBegin").length()>0){//SMIL 1.1 
					clipBeginAttrValue = audioElement.getAttribute("clipBegin");
				}
				
				String clipEndAttrValue = null;
				if(audioElement.getAttribute("clip-end").length()>0){//SMIL 1.0
					clipEndAttrValue = audioElement.getAttribute("clip-end"); 
					//handle npt=0.000s
					clipEndAttrValue = clipEndAttrValue.substring(4, clipEndAttrValue.length()-1);
				}else if(audioElement.getAttribute("clipEnd").length()>0){//SMIL 1.1 
					clipEndAttrValue = audioElement.getAttribute("clipEnd");
				}
				
				Element changeMsgElement = distInfoDoc.createElement("changeMsg");
				bookElement.appendChild(changeMsgElement);
				
				changeMsgElement.setAttribute("mediaRef", volNr +":"+ numberOfVolumes);
				
				Element textElement = distInfoDoc.createElement("text");
				bookElement.appendChild(textElement);
				
				Node textElementTxtNode = distInfoDoc.createTextNode(INSERT_DISC + volNr);
				textElement.appendChild(textElementTxtNode);
				changeMsgElement.appendChild(textElement);
								
				Element distInfoAudioElement = distInfoDoc.createElement("audio");
				File audioPrompt = (File)audioPromtSet.get(new Integer(volNr));
				distInfoAudioElement.setAttribute("src", audioPrompt.getName());
				distInfoAudioElement.setAttribute("clipBegin", clipBeginAttrValue);
				distInfoAudioElement.setAttribute("clipEnd", clipEndAttrValue);
				
				changeMsgElement.appendChild(distInfoAudioElement);											
				
			}
		}//end of changeMsg elements building		
		
/*
 * 		<changeMsg mediaRef="1:3">
		   <text>Insert disc one.</text>
		   <audio src="insert.wav" clipBegin="00:00" clipEnd="00:02.256"/>
		</changeMsg>
		<changeMsg mediaRef="2:3">
		   <text>Insert disc two.</text>
		   <audio src="insert.wav" clipBegin="00:03.002"
		   clipEnd="00:05.881"/>
		</changeMsg>
		<changeMsg mediaRef="3:3">
		   <text>Insert disc three.</text>
		   <audio src="insert.wav" clipBegin="00:06.901"
		   clipEnd="00:10.003"/>
		</changeMsg>


 */
		return distInfoDoc;
		
	}

/*
 * Implements DtbSplitter.saveVolumes()
 */
	protected void saveVolumes() throws TransformationAbortedByUserException, XmlParsingException, IOException{
		
		Document distInfoDoc = this.generateDistInfoDoc(super.getVolumeSet());
		String inputVolumeDir = this.opfFile.getFile().getParent();
				
		for(Iterator volumes = super.getVolumeSet().iterator();volumes.hasNext();){
			DtbVolume volume = (DtbVolume)volumes.next();		
			//Save each volume to a new directory.
			//Create the output directory for the volume.
			File outputVolumeDir = volume.getVolumeOutputDir();
			outputVolumeDir.mkdirs();
	
			boolean checkRelativePath = true;
			
			//copy and assign the output file references (smil, resource, text content, prompt) for the current volume
			//smil files
			List outputSmilFiles = volume.copyFilesIntoVolume(volume.getSmilFiles(), super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
			volume.setSmilFiles(outputSmilFiles);
			
			//resource files
			List outputResourceFiles = volume.copyFilesIntoVolume(volume.getResourceFiles(), super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
			
			//distInfo and DTD files for the current volume
			File outputDistInfoFile = DtbSerializer.serializeDocToFile(distInfoDoc, volume.getVolumeOutputDir() + File.separator + "distInfo.dinf");
			outputDistInfoFile.setReadOnly();
			List outputVolumeDtdFiles = this.copyDTDs(this.opfFile.getFile().getParentFile().getAbsoluteFile(), outputVolumeDir);
			
			outputResourceFiles.add(outputDistInfoFile);
			outputResourceFiles.addAll(outputVolumeDtdFiles);
			volume.setResourceFiles(outputResourceFiles);
			
			//text content files
			List outputTextContentFiles = volume.copyFilesIntoVolume(volume.getFullTextFiles(), super.isUserPromptOn(), super.isKeepInputDtb(), inputVolumeDir, checkRelativePath);
			volume.setFullTextFiles(outputTextContentFiles);
			
			//reset the checkRelativePath value to false for the prompt files
			checkRelativePath = false;
			boolean keepPromptFiles = true;//we never delete the input prompt files
			this.copyAudioPrompts(volume, keepPromptFiles, inputVolumeDir, checkRelativePath);
	
		}
	}
	
	
	private void copyAudioPrompts(DtbVolume volume, 
                                boolean keepPromptFiles, 
                                String inputVolDir, 
                                boolean checkRelativePath )throws TransformationAbortedByUserException, IOException{
	
		List inputPromptFiles = volume.getPromtFiles();//both smil and audio
		
		List inputAudioPrompts = new ArrayList();
		//retrieve audio prompts
		//we do not need the smil prompts in Daisy 3
		for(Iterator i=inputPromptFiles.iterator(); i.hasNext();){
			File file = (File)i.next();
			if(!file.getName().toLowerCase().endsWith(".smil")){
				inputAudioPrompts.add(file);		
			}
		}
		
		List outputAudioPrompts = volume.copyFilesIntoVolume(inputAudioPrompts, super.isUserPromptOn(), keepPromptFiles, inputVolDir, checkRelativePath);
		volume.setPromptFiles(outputAudioPrompts);
		
	
	}
	private List copyDTDs(File inputVolumeDir, File outputVolumeDir) throws IOException{
		File[] inputVolumeFiles = inputVolumeDir.listFiles();
		List outputVolumeDtdFiles = new ArrayList();
		
		for(int i=0; i<inputVolumeFiles.length; i++){
			
			File f = inputVolumeFiles[i];
			String fileName = f.getName().toLowerCase();
			
			if(fileName.endsWith(".dtd") || fileName.endsWith(".ent")){
				
				File dtdFile = new File(inputVolumeDir.getAbsolutePath() + File.separator + f.getName());
				//copy the dtd file
				File outputDtdFile = new File(outputVolumeDir.getAbsolutePath() + File.separator + dtdFile.getName());
                FileUtils.copyFile(dtdFile,outputDtdFile);

				outputVolumeDtdFiles.add(outputDtdFile);
				
			}//dtd file copied
			
		}//move to the next file
		
		return outputVolumeDtdFiles;
	}

//TODO Remove RG
//	protected void reportBookInfo() throws DtbException{
//		
//		Document doc = this.getOpfDoc();
//		HashMap bookInfo = new HashMap();
//		
//		this.setBookInfoItem("dc:Format", doc, bookInfo);
//		this.setBookInfoItem("dc:Identifier", doc, bookInfo);
//		this.setBookInfoItem("dc:Title", doc, bookInfo);
//		this.setBookInfoItem("dc:Creator", doc, bookInfo);
//		
//		super.getReportGenarator().setBookInfo(bookInfo);
//	}
//	
//	private void setBookInfoItem(String itemName, Document doc, HashMap bookInfo){
//
//		NodeList list = doc.getElementsByTagName(itemName);
//		
//		if(list!=null){
//			Element dcTitle = (Element)list.item(0);
//			Node dcTitleText = dcTitle.getChildNodes().item(0);
//			String title =  dcTitleText.getNodeValue();
//			bookInfo.put(itemName.toLowerCase(), title);	
//			
//		}			
//	}

}


