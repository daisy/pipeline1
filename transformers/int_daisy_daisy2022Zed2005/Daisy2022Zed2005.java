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
package int_daisy_daisy2022Zed2005;

import int_daisy_daisy2022Zed2005.resources.ResourceProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.GifFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.JpgFile;
import org.daisy.util.fileset.Mp2File;
import org.daisy.util.fileset.Mp3File;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.PngFile;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.TextualContentFile;
import org.daisy.util.fileset.WavFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986ResourceFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.i18n.CharUtils.FilenameRestriction;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Creates a Z3986-2005 DTB fileset from a Daisy 2.02 DTB fileset.
 * <p>Current known prerequisites:</p>
 * <ul>
 *  <li>Input DTB must be single volume (merge first!)</li>
 *  <li>Input DTB must contain zero or one content documents (merge first!)</li>
 * </ul> 
 * @author Brandon Nelson
 * @author Markus Gylling
 */

@SuppressWarnings("unchecked")
public class Daisy2022Zed2005 extends Transformer implements FilesetErrorHandler {
	
	//TODO add support for uid as inparam, overriding ncc:dc:identifier
	//TODO: XSLT does not adress repointing SMIL text frag destinations to their timecontainer parent
	
	private static final String XSLT_FACTORY = TransformerFactoryConstants.SAXON8;
	
	// Main progress values
	private static final double FILESET_DONE = 0.01;
	private static final double SMIL_DONE = 0.65;
	private static final double DTBOOK_DONE = 0.68;
	private static final double NCX_DONE = 0.85;
	private static final double OPF_DONE = 0.85;
	private static final double COPY_DONE = 0.99;
	
	private EFolder mOutputDir = null;
	private File mOutputOpf = null;
	private D202NccFile mInputNcc = null;
	private D202TextualContentFile inputContentDoc = null;
	private Fileset mInputFileset = null;
	private Fileset mAddedCssFileset = null;
	private Fileset mAddedResourceFileFileset = null;
	private int mInputContentDocCount = 0;
	private Map<String,FilesetFile> mManifestItems = new HashMap<String,FilesetFile>();
	private FilesetFileFactory mFilesetFileFactory = FilesetFileFactory.newInstance();
	private ResourceProvider mResourceProvider = null; 
			
	public Daisy2022Zed2005(InputListener inListener,  Boolean isInteractive) {
		super(inListener, isInteractive);
		mResourceProvider = new ResourceProvider(this);
	}
	
	protected boolean execute(Map parameters) throws TransformerRunException {
		String inparamNccPath = (String)parameters.remove("ncc");
		String inparamOutOpfPath = (String)parameters.remove("opf");
		String inparamCssPath = (String)parameters.remove("css");
		String inparamResourceFilePath = (String)parameters.remove("resourcefile");
		
		String identifier = "dc-identifier-unset";
		String title = "dc-title-unset";
						
		try {
			// Build input fileset
			this.sendMessage(i18n("BUILDING_FILESET"),MessageEvent.Type.DEBUG);    
			
			mInputFileset = buildFileSet(inparamNccPath, FilesetType.DAISY_202);				
			mInputNcc = (D202NccFile) mInputFileset.getManifestMember();            
			
			this.progress(FILESET_DONE);      
			
			// check for properties that should make us 
			// abort before even attempting the transform                  
			inputContentDoc = this.checkInputDtbState();
			
			//temporary: if not true ncc-only: bail out
			if(mInputContentDocCount>0) {
				throw new IllegalArgumentException("input DTB must be true NCC only (no content document(s))");
			}
			
			// create output directory    
			mOutputOpf = new File(inparamOutOpfPath);
			// make sure the output opf name is ok
			if(!CharUtils.isFilenameCompatible(mOutputOpf.getName(), FilenameRestriction.Z3986)) {
				String message = i18n("INVALID_FILENAME", mOutputOpf.getName());
				this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT);
				throw new TransformerRunException(message);
			}
			
			mOutputDir = (EFolder)FileUtils.createDirectory(new EFolder(mOutputOpf.getParentFile())); 
			
			if(mInputContentDocCount>0) {
				//prepare a css
				mAddedCssFileset = setAuxilliaryFileset(inparamCssPath, FilesetType.CSS);
			}	
			// prepare a resource file			
			mAddedResourceFileFileset = setAuxilliaryFileset(inparamResourceFilePath, FilesetType.Z3986_RESOURCEFILE);
			
			//collect variables from input dtb that need to be sent as parameters to the XSLTs   
			FilesetLabelProvider labelProvider = new FilesetLabelProvider(mInputFileset);
			String tempIdentifier = labelProvider.getFilesetIdentifier();
			String tempTitle = labelProvider.getFilesetTitle();
			if(tempIdentifier!=null) identifier = tempIdentifier;
			if(tempTitle!=null) title = tempTitle;	
			
			//create zed smil from the 2.02 smil input docs
			SmilClock dtbTotalTime = this.createZedSmil(identifier, title, inputContentDoc);
			
			//create ncx from the input ncc
			this.createZedNcx(mInputNcc,identifier);
			
			//create dtbook from the input xhtml
			if (this.mInputContentDocCount >= 1) {
				this.createZedDtbook(inputContentDoc,identifier,title);
			}
			
			//copy things that move to output unchanged
			this.copyMembers();
			
			//finally, the output topology is getting stable, create the opf			                            	
			this.createZedOpf(mInputNcc,identifier,dtbTotalTime);
				
		} catch (Exception e) {
			this.sendMessage(i18n("ERROR_ABORTING",e.getMessage()), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
			throw new TransformerRunException(e.getMessage(), e);
		}finally {
			mResourceProvider.clean();
		}
		
		return true;
		
	}
	


	private SmilClock createZedSmil(String uid,String title,D202TextualContentFile inputContentDoc) throws CatalogExceptionNotRecoverable, XSLTException, FilesetFatalException {
		D202SmilFile d202_smil = null;
		long totalElapsedTime = 0; //in milliseconds
		
		this.sendMessage(i18n("CREATING_SMIL"),MessageEvent.Type.DEBUG);
		
		URL xsltURL = this.getClass().getResource("d202smil_Z2005smil.xsl");
				
		for (Iterator it = mInputFileset.getManifestMember().getReferencedLocalMembers().iterator(); it.hasNext(); ) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof D202SmilFile) {
				d202_smil = (D202SmilFile) fsf;
				File smilOut = new File(mOutputDir, d202_smil.getFile().getName());				
				//remember: the XSLTs are written context unaware, 
				//so they need to get all necessary context info as inparams.
				Map<String,String> parameters = new HashMap<String,String>();
				parameters.put("uid", uid);
				parameters.put("title", title);
				parameters.put("totalElapsedTime",new SmilClock(totalElapsedTime).toString(SmilClock.FULL));
				parameters.put("timeinThisSmil",d202_smil.getCalculatedDuration().toString(SmilClock.FULL));
				parameters.put("dtbookFileName",this.getAsciiFilename(uid,"xml"));		
				if (inputContentDoc == null) {
					//send this so that XSLT knows that it should drop text elements
					//the presence of this param means "drop text elems"
					//the absence of this param means "dont drop text elems"
					parameters.put("isNcxOnly","true");
				}						
				
				Stylesheet.apply(d202_smil.getFile().getAbsolutePath(), xsltURL, smilOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
				
				mManifestItems.put(smilOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986SmilFile",smilOut.toURI()));								
				totalElapsedTime += d202_smil.getCalculatedDuration().millisecondsValue();						                	              	
			} //if (fsf instanceof D202SmilFile)
		}
		
		this.progress(SMIL_DONE);
		return new SmilClock(totalElapsedTime);
	}
	
	private void createZedNcx(D202NccFile ncc, String uid) throws CatalogExceptionNotRecoverable, XSLTException, FilesetFatalException {
		String ncxFileName = this.getAsciiFilename(uid,"ncx");
		
		this.sendMessage(i18n("CREATING_NCX", ncxFileName),MessageEvent.Type.DEBUG);                            	
		
		File ncxOut = new File(mOutputDir, ncxFileName);
		URL xsltURL = this.getClass().getResource("d202ncc_Z2005ncx.xsl");
				
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("uid", uid);
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltURL, ncxOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.put(ncxOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986NcxFile", ncxOut.toURI()));
		
		this.progress(NCX_DONE);
	}
	
	private void createZedDtbook(D202TextualContentFile xhtml, String uid, String title) throws CatalogExceptionNotRecoverable, XSLTException, FilesetFatalException {
		
		String dtbookFileName = this.getAsciiFilename(uid,"xml");
		
		this.sendMessage(i18n("CREATING_DTBOOK", dtbookFileName),MessageEvent.Type.DEBUG);
		
		File dtbookOut = new File(mOutputDir, dtbookFileName);
		URL xsltURL = this.getClass().getResource("d202xhtml_Z2005dtbook.xsl");
	
		String cssUri = null;
		if(mAddedCssFileset!=null) {
			cssUri = mAddedCssFileset.getManifestMember().getName(); 
		}
		
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("uid", uid);
		parameters.put("title", title);
		parameters.put("cssUri", cssUri);
				           
		Stylesheet.apply(xhtml.getFile().getAbsolutePath(), xsltURL, dtbookOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.put(dtbookOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986DtbookFile",dtbookOut.toURI()));
		
		this.progress(DTBOOK_DONE);
	}
	
	private void createZedOpf(D202NccFile ncc, String uid, SmilClock dtbTotalTime) throws XSLTException, IOException, ParserConfigurationException, SAXException, FilesetFatalException {
		//the xslt creates metadata, the spine, and adds the smilfiles to manifest
		//this java object adds the other stuff to manifest (in .finalizeManifest).
		
		String opfFileName = mOutputOpf.getName();
		
		this.sendMessage(i18n("CREATING_OPF", opfFileName));                            	
		
		File opfOut = new File(mOutputDir, opfFileName);
		URL xsltURL = this.getClass().getResource("d202ncc_Z2005opf.xsl");
				
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("dtbTotalTime", dtbTotalTime.toString(SmilClock.FULL));
		parameters.put("dtbMultimediaContent", this.getTopLevelMediaTypes());
		parameters.put("uid", uid);
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltURL, opfOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.put(opfOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986OpfFile",opfOut.toURI()));
		
		//run finalize after manifestItems.put so that the opf gets included in the manifest itemlist
		finalizeOpf(opfOut);
		
		this.progress(OPF_DONE);
	}
	
	private void finalizeOpf(File unfinishedOpf) throws IOException, ParserConfigurationException, SAXException {
		/*
		 * the xslt has already added the smilfiles to manifest 
		 * note: since this function relativizes item URIs, 
		 * opf and its friends must be placed in final form in relation to eachother
		 * ie cant have stuff in temp locations.
		 */
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setExpandEntityReferences(false);
		DocumentBuilder parser;
		parser = factory.newDocumentBuilder();
		parser.setEntityResolver(CatalogEntityResolver.getInstance());                
		Document opfDom = parser.parse(unfinishedOpf);		
		NodeList nl = opfDom.getElementsByTagName("manifest");
		Element manifest = (Element)nl.item(0); 
		
		int k = 1;
		for (Iterator i = mManifestItems.keySet().iterator(); i.hasNext(); ) {		  
			try{
				FilesetFile fsf = mManifestItems.get(i.next());
				String mime = fsf.getMimeType().dropParametersPart();		
				
				URI opfURI = unfinishedOpf.getParentFile().toURI();
				
				if (!(fsf instanceof SmilFile)) { //since xslt added smilfiles already
					
					Element item = opfDom.createElement("item");
					//set the mime
					item.setAttribute("media-type", mime);
					//set the href
					URI itemURI = new File(fsf.getFile().getAbsolutePath()).toURI();		    
					URI relative = opfURI.relativize(itemURI);
					item.setAttribute("href", relative.toString());
					//set an id
					String id = null;
					if(fsf instanceof Z3986NcxFile) {
						id="ncx";
					}else if(fsf instanceof OpfFile) {
						id="opf";	
					}else if(fsf instanceof Z3986ResourceFile) {
						id="resource";
					}else if(fsf instanceof Z3986DtbookFile) {
						id="dtbook";
					}else{
						id="pipeline_"+k;
					}
					item.setAttribute("id", id);		    
					manifest.appendChild(item);		    
					k++;
				}	
			}catch (Exception e){
				System.err.println("err:: " + i.toString());	
			}						
		}
		
		/*
		 * Need to move the root xmlns:dc decl, since the DTD doesnt allow it.
		 */		
		try{
			Element root = opfDom.getDocumentElement();
			Attr a = root.getAttributeNode("xmlns:dc");
			if(a!=null) {
				root.removeAttributeNode(a);
			}		
				
			nl = opfDom.getElementsByTagName("dc-metadata");
			Element dcmetadata = (Element)nl.item(0); 
			dcmetadata.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
		}catch (Exception e) {
			this.sendMessage(e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
		}
		//save the dom
		
		OutputFormat outputFormat = new OutputFormat(opfDom);
		outputFormat.setPreserveSpace(false);
		outputFormat.setIndenting(true);
		outputFormat.setIndent(3);
		outputFormat.setOmitComments(false);
		outputFormat.setEncoding("utf-8");        
		OutputStream fout= new FileOutputStream(unfinishedOpf);
		XMLSerializer serializer = new XMLSerializer(fout, outputFormat);
		serializer.serialize(opfDom);
		
	}
	
	
	private void copyMembers() throws FilesetFatalException, IOException {
		
		Set<FilesetFile> inputFilesToCopy = new HashSet<FilesetFile>();
		
		//copy remainder of input fileset
		for (Iterator it = mInputFileset.getLocalMembers().iterator(); it.hasNext();) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof AudioFile) {
				inputFilesToCopy.add(fsf);
			} else if (fsf instanceof ImageFile) {
				inputFilesToCopy.add(fsf);
			} 
		}												
		this.copyFiles(inputFilesToCopy, mInputFileset);
		
		//copy added css fileset, if existing
		if(mAddedCssFileset!=null) {
			Set<FilesetFile> addedCssFilesToCopy = new HashSet<FilesetFile>();
			addedCssFilesToCopy.addAll(mAddedCssFileset.getLocalMembers());
			this.copyFiles(addedCssFilesToCopy, mAddedCssFileset);
		}  			
		
		//copy added resourcefile fileset, if existing
		if(mAddedResourceFileFileset!=null) {
			Set<FilesetFile> addedResFilesToCopy = new HashSet<FilesetFile>();
			addedResFilesToCopy.addAll(mAddedResourceFileFileset.getLocalMembers());
			this.copyFiles(addedResFilesToCopy, mAddedResourceFileFileset);
		} 
		
		this.progress(COPY_DONE);
	}
	
	/**
	 * Copy a set of files belonging to a Fileset, relative to the manifest member
	 * @param files the set of files to copy
	 * @throws IOException
	 * @throws FilesetFatalException 
	 */
	private void copyFiles(Set files, Fileset fileset) throws IOException, FilesetFatalException {
		int fileNum = files.size();
		int fileCount = 0;
		for (Iterator it = files.iterator(); it.hasNext(); ) {
			fileCount++;
			FilesetFile fsf = (FilesetFile)it.next();
			URI relativeURI = fileset.getManifestMember().getRelativeURI(fsf);
			File out = new File(mOutputDir.toURI().resolve(relativeURI));
			FileUtils.copy(fsf.getFile(), out);
			if(fsf instanceof Mp3File) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Mp3File",out.toURI()));
			}else if(fsf instanceof Mp2File) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Mp2File",out.toURI()));                
			}else if(fsf instanceof WavFile) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("WavFile",out.toURI()));  
			}else if (fsf instanceof JpgFile) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("JpgFile",out.toURI())); 
			}else if (fsf instanceof PngFile) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("PngFile",out.toURI())); 
			}else if (fsf instanceof GifFile) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("GifFile",out.toURI())); 
			}else if (fsf instanceof CssFile) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("CssFile",out.toURI())); 
			}else if (fsf instanceof Z3986ResourceFile) {
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986ResourceFile",out.toURI())); 
			}else{
				System.err.println("unknown filetype encountered in " + this.getClass().getSimpleName() + " .copyFiles");
				mManifestItems.put(out.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("AnonymousFile",out.toURI())); 
			}                        
			this.progress(0.85 + (0.99-0.85)*((double)fileCount/fileNum));
		}
	}
	
	private Fileset buildFileSet(String manifest, FilesetType expectedType) throws FilesetFatalException {
		Fileset fileset = new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), this, false, true); 
		if(!fileset.getFilesetType().equals(expectedType)) {
			throw new FilesetFatalException ( "Fileset type " + fileset.getFilesetType().toNiceNameString() + 
					", expecting " + expectedType.toNiceNameString() );
		}
		return fileset;
	}
	
	private String getTopLevelMediaTypes() {
		boolean hasImages = false;
		boolean hasAudio = false;
		boolean hasText = false;
		
		for (Iterator iter = mManifestItems.keySet().iterator(); iter.hasNext();) {
			FilesetFile f =  mManifestItems.get(iter.next());
			if(f instanceof AudioFile) {
				hasAudio = true;
			}else if(f instanceof ImageFile) {
				hasImages = true;		
			}else if(f instanceof TextualContentFile) {
				hasText = true;
			}
		}
		String mediaType = null;
		if(hasAudio) mediaType = "audio,";
		if(hasImages) mediaType += "image,";
		if(hasText) mediaType += "text";	
		if (mediaType.endsWith(",")) {
			mediaType = mediaType.substring(0, mediaType.length()-1);
		}
		return mediaType;
	}
	
	private D202TextualContentFile checkInputDtbState() throws TransformerRunException {
		D202TextualContentFile xhtFile = null;
		
		//abortcause: 2.02 DTB with > 1 content doc                        
		for (Iterator it = mInputFileset.getLocalMembers().iterator(); it.hasNext(); ) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof D202TextualContentFile) {              
				mInputContentDocCount ++;
				xhtFile = (D202TextualContentFile)fsf;
			}            	
		}            
		if (mInputContentDocCount > 1) {
			//more than one content doc
			throw new TransformerRunException("more than one XHTML content doc in input DTB - merge first!");
		}	
		
		//abortcause: fileset is part of multivolume
		if(mInputNcc.hasMultiVolumeIndicators()){
			//rel attrs in body && setInfo with value other than '1 of 1'
			throw new TransformerRunException("input DTB indicates it is multivolume - merge first!");
		}
		
		//messagecause: fileset had errors
		if(mInputFileset.hadErrors()) {
			//since it was built nonvalidating, this is prolly a serious error (malformedness, files missing)
			String errors=null;
			for(Iterator i = mInputFileset.getErrors().iterator(); i.hasNext();) {
				Exception e = (Exception )i.next();
				errors = errors + "\n" +  e.getMessage();            		
			}
			String message = "input fileset had " + mInputFileset.getErrors().size() + " errors: " + errors;
			this.sendMessage(message, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);			
		}	
		
		return xhtFile;
	}
	
	private Fileset setAuxilliaryFileset(String inparamPath, FilesetType type) throws FilesetFatalException, IOException {
		/* 
		 * Auxilliary filesets are of types CSS and ResourceFile.
		 * These are not in the input d202 DTB, but bluntly added to the output.
		 * If inparam is valid, we create a fileset and return.
		 * Else, we use the baked-in default filesets 
		 * 
		 * If using baked-in, remember we are possibly jarred so need to
		 * perform some ugly workarounds, to get physical files.
		 */
		if(inparamPath!=null && inparamPath.length()>0) {
			File inparamFile = new File(inparamPath);
			if(inparamFile.exists() && inparamFile.canRead() ) {
				return this.buildFileSet(inparamFile.getAbsolutePath(), type);
			}
			this.sendMessage(i18n("INPARAM_FILE_NOT_FOUND", inparamFile.getAbsolutePath()), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
		}
				
		return mResourceProvider.getDefaultAuxilliaryFileset(type);
						
	}
		
	private String getAsciiFilename(String name, String extension) {
		return truncateToAscii(CharUtils.toNonWhitespace(name)) + "." + extension;		
	}
	
	private String truncateToAscii(String characters) {
		return CharUtils.toPrintableAscii(characters);		
	}

	//impl of the FilesetErrorHandler interface
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}
	
}