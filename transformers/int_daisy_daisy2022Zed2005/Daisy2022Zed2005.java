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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetFileFactory;
import org.daisy.util.fileset.FilesetImpl;
import org.daisy.util.fileset.GifFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.JpgFile;
import org.daisy.util.fileset.Mp2File;
import org.daisy.util.fileset.Mp3File;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.PngFile;
import org.daisy.util.fileset.SmilClock;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.WavFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.fileset.Z3986ResourceFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Creates a Z3986-2005 DTB fileset from a Daisy 2.02 DTB fileset.
 * 
 * Current known assumptions and limitations:
 * <ul>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 *  <li></li>
 * </ul>
 * @author Brandon Nelson
 * @author Markus Gylling
 */
public class Daisy2022Zed2005 extends Transformer {
	
	//TODO add support for uid as inparam, overriding ncc:dc:identifier
	
	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	
	// Main progress values
	private static final double FILESET_DONE = 0.01;
	private static final double SMIL_DONE = 0.65;
	private static final double DTBOOK_DONE = 0.68;
	private static final double NCX_DONE = 0.85;
	private static final double OPF_DONE = 0.85;
	private static final double COPY_DONE = 0.99;
	
	private File outputDir = null;
	private D202NccFile inputNcc = null;
	private D202TextualContentFile inputContentDoc = null;
	private Fileset inputFileset = null;
	private Fileset addedCssFileset = null;
	private Fileset addedResourceFileFileset = null;
	private int inputContentDocCount = 0;
	private Map manifestItems = new HashMap(); //<FileAbsolutePath>,<FilesetFile>
	
	public Daisy2022Zed2005(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}
	
	protected boolean execute(Map parameters) throws TransformerRunException {
		String inparamNccPath = (String)parameters.remove("ncc");
		String inparamOutDir = (String)parameters.remove("outDir");
		String inparamCssPath = (String)parameters.remove("css");
		String inparamResourceFilePath = (String)parameters.remove("resourcefile");
		
		String nccDcIdentifier = "dc-identifier-unset";
		String nccDcTitle = "dc-title-unset";
		
		CssFile sourceCss = null;
		
		try {
			// Build input fileset
			this.sendMessage(Level.INFO, i18n("BUILDING_FILESET"));            
			inputFileset = this.buildFileSet(inparamNccPath);			
			inputNcc = (D202NccFile) inputFileset.getManifestMember();            
			this.progress(FILESET_DONE);      
			
			// check for properties that should make us 
			// abort before even attempting the transform                  
			inputContentDoc = this.checkInputDtbState();
			
			// create output directory            
			outputDir = FileUtils.createDirectory(new File(inparamOutDir)); 
			
			// prepare a css
			sourceCss = this.setCss(inparamCssPath);
			
			// prepare a resource file
			this.setResourceFile(inparamResourceFilePath);
			
			//collect variables from input ncc that need to be sent as parameters to the XSLTs   
			if(inputNcc.getDcIdentifier()!=null)nccDcIdentifier = inputNcc.getDcIdentifier();
			if(inputNcc.getDcTitle()!=null) nccDcTitle = inputNcc.getDcTitle();	
			
			//create zed smil from the 2.02 smil input docs
			SmilClock dtbTotalTime = this.createZedSmil(nccDcIdentifier, nccDcTitle, inputContentDoc);
			
			//create ncx from the input ncc
			this.createZedNcx(inputNcc,nccDcIdentifier);
			
			//create dtbook from the input xhtml
			this.createZedDtbook(inputContentDoc,nccDcIdentifier,nccDcTitle,sourceCss.getName());  
			
			//copy things that move to output unchanged
			this.copyMembers();
			
			//finally, the output topology is getting stable, create the opf			                            	
			this.createZedOpf(inputNcc,nccDcIdentifier,dtbTotalTime);
			
			
		} catch (FilesetException e) {            
			throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (XSLTException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);		}
		System.err.println("----------> zedification done.");
		return true;
		
	}
	
	private SmilClock createZedSmil(String uid,String title,D202TextualContentFile inputContentDoc) throws CatalogExceptionNotRecoverable, XSLTException, FilesetException {
		D202SmilFile d202_smil = null;
		long totalElapsedTime = 0; //in milliseconds
		
		this.sendMessage(Level.INFO, i18n("CREATING_SMIL", ""));
		
		for (Iterator it = inputFileset.getLocalMembers().iterator(); it.hasNext(); ) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof D202SmilFile) {
				d202_smil = (D202SmilFile) fsf;
				File smilOut = new File(outputDir, d202_smil.getName());
				File xsltFile = new File(this.getTransformerDirectory(), "d202smil_Z2005smil.xsl");		
				//remember: the XSLTs are written context unaware, 
				//so they need to get all necessary context info as inparams.
				Map parameters = new HashMap();
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
				Stylesheet.apply(d202_smil.getFile().getAbsolutePath(), xsltFile.getAbsolutePath(), smilOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());		
				manifestItems.put(smilOut.getAbsolutePath(), FilesetFileFactory.newZ3986SmilFile(smilOut.toURI()));								
				totalElapsedTime += d202_smil.getCalculatedDurationMillis();								                	              	
			} 
		}
		
		this.progress(SMIL_DONE);
		return new SmilClock(totalElapsedTime);
	}
	
	private void createZedNcx(D202NccFile ncc, String uid) throws CatalogExceptionNotRecoverable, XSLTException, FilesetException {
		String ncxFileName = this.getAsciiFilename(uid,"ncx");
		
		this.sendMessage(Level.INFO, i18n("CREATING_NCX", ncxFileName));                            	
		
		File ncxOut = new File(outputDir, ncxFileName);
		File xsltFile = new File(this.getTransformerDirectory(), "d202ncc_Z2005ncx.xsl");
		
		Map parameters = new HashMap();
		parameters.put("uid", uid);
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltFile.getAbsolutePath(), ncxOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		
		manifestItems.put(ncxOut.getAbsolutePath(), FilesetFileFactory.newZ3986NcxFile(ncxOut.toURI()));
		
		this.progress(NCX_DONE);
	}
	
	private void createZedDtbook(D202TextualContentFile xhtml, String uid, String title, String cssUri) throws CatalogExceptionNotRecoverable, XSLTException, FilesetException {
		
		String dtbookFileName = this.getAsciiFilename(uid,"xml");
		
		this.sendMessage(Level.INFO, i18n("CREATING_DTBOOK", dtbookFileName));
		
		File dtbookOut = new File(outputDir, dtbookFileName);
		File xsltFile = new File(this.getTransformerDirectory(), "d202xhtml_Z2005dtbook.xsl");
		
		Map parameters = new HashMap();
		parameters.put("uid", uid);
		parameters.put("title", title);
		parameters.put("cssUri", cssUri);
		
		File inFile = (File)xhtml;            
		Stylesheet.apply(inFile.getAbsolutePath(), xsltFile.getAbsolutePath(), dtbookOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		
		manifestItems.put(dtbookOut.getAbsolutePath(), FilesetFileFactory.newZ3986DtbookFile(dtbookOut.toURI()));
		
		this.progress(DTBOOK_DONE);
	}
	
	private void createZedOpf(D202NccFile ncc, String uid, SmilClock dtbTotalTime) throws XSLTException, IOException, ParserConfigurationException, SAXException, FilesetException {
		//the xslt creates metadata, the spine, and adds the smilfiles to manifest
		//this java object adds the other stuff to manifest (in .finalizeManifest).
		
		String opfFileName = this.getAsciiFilename(uid,"opf");
		
		this.sendMessage(Level.INFO, i18n("CREATING_OPF", opfFileName));                            	
		
		File opfOut = new File(outputDir, opfFileName);
		File xsltFile = new File(this.getTransformerDirectory(), "d202ncc_Z2005opf.xsl");
		
		Map parameters = new HashMap();
		parameters.put("dtbTotalTime", dtbTotalTime.toString(SmilClock.FULL));
		parameters.put("dtbMultimediaContent", this.getTopLevelMediaTypes());
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltFile.getAbsolutePath(), opfOut.getAbsolutePath(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		
		manifestItems.put(opfOut.getAbsolutePath(), FilesetFileFactory.newOpfFile(opfOut.toURI()));
		
		//run finalize after manifestItems.put so that the opf gets included in the manifest itemlist
		finalizeManifest(opfOut);
		
		this.progress(OPF_DONE);
	}
	
	private void finalizeManifest(File unfinishedOpf) throws IOException, ParserConfigurationException, SAXException {
//		the xslt has already added the smilfiles to manifest
//		note: since this function relativizes item URIs, 
//		opf and its friends must be placed in final form in relation to eachoter
//		ie cant have stuff in temp locations.    	
		
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
		for (Iterator i = manifestItems.keySet().iterator(); i.hasNext(); ) {		  
			try{
				FilesetFile fsf = (FilesetFile)manifestItems.get(i.next());
				String mime = fsf.getMimeType();		
				
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
						id="dmfc_"+k;
					}
					item.setAttribute("id", id);		    
					manifest.appendChild(item);		    
					k++;
				}	
			}catch (Exception e){
				System.err.println("err:: " + i.toString());	
			}						
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
	
	private void copyMembers() throws FilesetException, IOException {
		
		Set inputFilesToCopy = new HashSet();
		
		//copy remainder of input fileset
		for (Iterator it = inputFileset.getLocalMembers().iterator(); it.hasNext();) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof AudioFile) {
				inputFilesToCopy.add(fsf);
			} else if (fsf instanceof ImageFile) {
				inputFilesToCopy.add(fsf);
			} 
		}												
		this.copyFiles(inputFilesToCopy, inputFileset);
		
		//copy added css fileset, if existing
		if(addedCssFileset!=null) {
			Set addedCssFilesToCopy = new HashSet();
			addedCssFilesToCopy.addAll(addedCssFileset.getLocalMembers());
			this.copyFiles(addedCssFilesToCopy, addedCssFileset);
		}  			
		
		//copy added resourcefile fileset, if existing
		if(addedResourceFileFileset!=null) {
			Set addedResFilesToCopy = new HashSet();
			addedResFilesToCopy.addAll(addedResourceFileFileset.getLocalMembers());
			this.copyFiles(addedResFilesToCopy, addedResourceFileFileset);
		} 
		
		this.progress(COPY_DONE);
	}
	
	/**
	 * Copy a set of files belonging to a Fileset, relative to the manifest member
	 * @param files the set of files to copy
	 * @throws IOException
	 * @throws FilesetException 
	 */
	private void copyFiles(Set files, Fileset fileset) throws IOException, FilesetException {
		int fileNum = files.size();
		int fileCount = 0;
		for (Iterator it = files.iterator(); it.hasNext(); ) {
			fileCount++;
			FilesetFile fsf = (FilesetFile)it.next();
			Object[] params = {new Integer(fileNum), new Integer(fileCount), fsf.getName()};
			this.sendMessage(Level.INFO, i18n("COPYING_FILE", params));
			URI relativeURI = fileset.getRelativeURI(fsf);
			File out = new File(outputDir.toURI().resolve(relativeURI));
			FileUtils.copy(fsf.getFile(), out);
			if(fsf instanceof Mp3File) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newMp3File(out.toURI()));
			}else if(fsf instanceof Mp2File) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newMp2File(out.toURI()));                
			}else if(fsf instanceof WavFile) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newWavFile(out.toURI()));  
			}else if (fsf instanceof JpgFile) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newJpgFile(out.toURI())); 
			}else if (fsf instanceof PngFile) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newPngFile(out.toURI())); 
			}else if (fsf instanceof GifFile) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newGifFile(out.toURI())); 
			}else if (fsf instanceof CssFile) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newCssFile(out.toURI())); 
			}else if (fsf instanceof Z3986ResourceFile) {
				manifestItems.put(out.getAbsolutePath(), FilesetFileFactory.newZ3986ResourceFile(out.toURI())); 
			}else{
				System.err.println("unknown filetype encountered in " + this.getClass().getSimpleName() + " .copyFiles");
				manifestItems.put(out.getAbsolutePath(), out); 
			}                        
			this.progress(0.85 + (0.99-0.85)*((double)fileCount/fileNum));
		}
	}
	
	private Fileset buildFileSet(String manifest) throws FilesetException {
		return new FilesetImpl(FilenameOrFileURI.toFile(manifest).toURI(), false, true);
	}
	
	private String getTopLevelMediaTypes() {
		//use manifestItems
		return "audio,text,image"; //TODO
	}
	
	private D202TextualContentFile checkInputDtbState() throws TransformerRunException {
		D202TextualContentFile xhtFile = null;
		
		//abortcause: 2.02 DTB with > 1 content doc                        
		for (Iterator it = inputFileset.getLocalMembers().iterator(); it.hasNext(); ) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof D202TextualContentFile) {              
				inputContentDocCount ++;
				xhtFile = (D202TextualContentFile)fsf;
			}            	
		}            
		if (inputContentDocCount > 1) {
			//more than one content doc
			throw new TransformerRunException("more than one XHTML content doc in input DTB - merge first!");
		}	
		
		//abortcause: fileset is part of multivolume
		if(inputNcc.hasMultiVolumeIndicators()){
			//rel attrs in body && setInfo with value other than '1 of 1'
			throw new TransformerRunException("input DTB indicates it is multivolume - merge first!");
		}
		
		//messagecause: fileset had errors
		if(inputFileset.hadErrors()) {
			//since it was built nonvalidating, this is prolly a serious error (malformedness, files missing)
			String errors=null;
			for(Iterator i = inputFileset.getErrorsIterator(); i.hasNext();) {
				Exception e = (Exception )i.next();
				errors = errors + "\n" +  e.getMessage();            		
			}
			this.sendMessage(Level.WARNING, "input fileset had " 
					+ inputFileset.getErrors().size() 
					+ " errors: "
					+ errors
			);
		}	
		
		return xhtFile;
	}
	
	private CssFile setCss (String inparamCssPath) throws IOException {    				
		if (this.inputContentDocCount < 1) {
			return null; //there will be no dtbook in output
		}    	
		
		//first take the one provided in the dmfc install
		File css = new File(this.getTransformerDirectory().getCanonicalPath()+"/resources/dtbook-dmfc-default.css");         	
		
		//try to get user inparam css, fall back to install provided if fail            
		if(inparamCssPath!=null) {
			try{
				File temp = new File(inparamCssPath);
				if (temp.exists() && temp.canRead()) {
					css = temp;		   
				}
			}catch (Exception e) {
				
			}
		}
		//now we know which css to use
		try {
			addedCssFileset = this.buildFileSet(css.getAbsolutePath());
		} catch (FilesetException e) {
			return (CssFile)css;
		}		
		return (CssFile)addedCssFileset.getManifestMember();		
	}
	
	private void setResourceFile(String inparamResourceFilePath) throws IOException {
		//first take the one provided in the dmfc install
		File res = new File(this.getTransformerDirectory().getCanonicalPath()+"/resources/dmfc.res");         	
		
		//try to get user inparam css, fall back to install provided if fail            
		if(inparamResourceFilePath!=null) {
			try{
				File temp = new File(inparamResourceFilePath);
				if (temp.exists() && temp.canRead()) {
					res = temp;		   
				}
			}catch (Exception e) {
				
			}
		}
		//now we know which resource file to use
		try {
			addedResourceFileFileset = this.buildFileSet(res.getAbsolutePath());
		} catch (FilesetException e) {
			
		}						
	}
	
	private String getAsciiFilename(String name, String extension) {
		return truncateToAscii(name) + "." + extension;		
	}
	
	private String truncateToAscii(String characters) {
		return characters;		//TODO
	}		
}