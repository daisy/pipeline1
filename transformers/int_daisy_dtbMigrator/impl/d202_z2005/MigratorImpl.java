package int_daisy_dtbMigrator.impl.d202_z2005;

import int_daisy_dtbMigrator.DtbDescriptor;
import int_daisy_dtbMigrator.Migrator;
import int_daisy_dtbMigrator.MigratorException;
import int_daisy_dtbMigrator.DtbVersion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.dtb.resource.ResourceFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.interfaces.audio.Mp2File;
import org.daisy.util.fileset.interfaces.audio.Mp3File;
import org.daisy.util.fileset.interfaces.audio.WavFile;
import org.daisy.util.fileset.interfaces.image.GifFile;
import org.daisy.util.fileset.interfaces.image.ImageFile;
import org.daisy.util.fileset.interfaces.image.JpgFile;
import org.daisy.util.fileset.interfaces.image.PngFile;
import org.daisy.util.fileset.interfaces.text.CssFile;
import org.daisy.util.fileset.interfaces.xml.OpfFile;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986NcxFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986ResourceFile;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * An implementation of DtbMigrator that supports upgrading Daisy 2.02 DTBs to z2005.
 * <ul>
 *  <li>Ncc-only and Full Text input DTBs are supported, given the documented caveats re upgrading XHTML to DTBook.</li>
 *  <li>This implementation does not support text-only 2.02 DTBs (if they ever existed).</li>
 *  <li>This implementation only supports single volume input. The idea is to merge first.</li>
 * </ul>  
 * @author Markus Gylling, Brandon Nelson, Per Sennels
 */

public class MigratorImpl implements Migrator {
	/** Owning Transformer */
	private TransformerDelegateListener mTransformer = null;
	/** A list of all files in output. Used ultimately when writing the package manifest */
	private Map<String,FilesetFile> mManifestItems = null;
	/** Factory to create the objects in mManifestItems */
	private FilesetFileFactory mFilesetFileFactory = null;
	/** Destination directory */
	private EFolder mOutputDir = null;
	
	// Main progress values
	
	private static final double SMIL_DONE = 0.35;
	private static final double DTBOOK_DONE = 0.37;
	private static final double NCX_DONE = 0.39;
	private static final double OPF_DONE = 0.45;
	private static final double COPY_DONE = 0.99;
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#migrate(int_daisy_dtbMigrator.DtbDescriptor, 
	 * 		int_daisy_dtbMigrator.DtbDescriptor, java.util.Map, org.daisy.util.fileset.interfaces.Fileset, org.daisy.util.file.EFolder)
	 */
	@SuppressWarnings("unused")
	public void migrate(DtbDescriptor input, DtbDescriptor output, Map<String,String> parameters, 
			Fileset inputFileset, EFolder destination) throws MigratorException {
		
		/* 
		 * Input 2.02 DTB is one of these types:
		 * a) True NCC-Only (SMIL <text/> points back to NCC
		 * b) Untrue NCC-Only (SMIL <text/> points to a contentual clone of the NCC 
		 *    (a clone which may be distributed into several content docs).
		 * c) Text+Audio.
		 * 
		 * In the cases of a and b, we will drop the content doc(s) (and hence the <text/> elems in SMIL).
		 * In the case of c, we will attempt an upgrade XHTML->DTBook using the XSLT developed in no_hks_xhtml2dtbook
		 * 
		 * One central problem lies in detecting whether a DTB is of types b or c. Solution used is:
		 * a)(preferred) User statement on whether this is a 2.02 DTB with text that should be maintained
		 * b) In the absence of user statement, count the number of element children of ncc and content doc(s) body, 
		 * if equal then assume this is NCC only. (TODO What about skippable DTBs?)
		 * 
		 * 
		 * TODO
		 * Support skippability (for NCC-only as well as Full Text input types)
		 * Add DTBook CSS (unless XSLT does this)
		 * 
		 */

		mOutputDir = destination;
		mManifestItems = new HashMap<String,FilesetFile>();
		mFilesetFileFactory = FilesetFileFactory.newInstance();
		
		try{	
			/*
			 * Collect variables from input dtb that need to be sent as parameters to the XSLTs
			 */   
			FilesetLabelProvider labelProvider = new FilesetLabelProvider(inputFileset);
			String identifier = labelProvider.getFilesetIdentifier();
			String title = labelProvider.getFilesetTitle();
			boolean isNccOnly = isNccOnly(inputFileset, parameters);
		
			/*
			 * Create zed smil from the 2.02 smil input docs
			 */
			SmilClock dtbTotalTime = createZedSmil(identifier, title, inputFileset, isNccOnly);
		
			/*
			 * Create NCX from the input NCC
			 */
			createZedNcx((D202NccFile)inputFileset.getManifestMember(),identifier);
		
			/*
			 * Create dtbook from the input xhtml
			 */
			if (!isNccOnly) {
				this.createZedDtbook(inputFileset,identifier,title);
			}
		
			/*
			 * Add a resource file, default is a one-member text-only resource file
			 * TODO allow user to override the default, use fileset etc
			 */
			Set<URL> res = ResourceFile.get(ResourceFile.Type.TEXT_ONLY);
			for(URL u : res) {  //now length always == 1				
				String localName = new File(u.toURI()).getName();
				InputStream is = u.openStream();
				File out = mOutputDir.writeToFile(localName, is);
				is.close();
				mManifestItems.put(out.getAbsolutePath(), 
						mFilesetFileFactory.newFilesetFile("Z3986ResourceFile", out.toURI()));
			}
			
			/*
			 * Copy things that move to output unchanged
			 */
			copyMembers(inputFileset);
		
			/*
			 * Finally, the output topology is getting stable, create the opf
			 */			                            			
			createZedOpf((D202NccFile)inputFileset.getManifestMember(),identifier,dtbTotalTime);
		
		}catch (Exception e) {
			throw new MigratorException(e.getLocalizedMessage(),e);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Transform all SMIL files of input 2.02 fileset into Z2005 SMIL files.
	 * <p>SMIL file names are maintained unchanged.</p>
	 * @return the total playback time
	 */
	private SmilClock createZedSmil(String uid,String title,Fileset inputFileset, boolean isNccOnly) 
		throws CatalogExceptionNotRecoverable, XSLTException, FilesetFatalException {
		
		D202SmilFile d202_smil = null;
		long totalElapsedTimeMillis = 0;
				
		mTransformer.delegateMessage(this, 
				mTransformer.delegateLocalize("CREATING_SMIL", null), 
				MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM, null);
		
		URL xsltURL = this.getClass().getResource("./xslt/d202smil_Z2005smil.xsl");
				
		for (Iterator<FilesetFile> it = inputFileset.getManifestMember().getReferencedLocalMembers().iterator(); it.hasNext(); ) {
			FilesetFile fsf = it.next();
			if (fsf instanceof D202SmilFile) {
				d202_smil = (D202SmilFile) fsf;
				File smilOut = new File(mOutputDir, d202_smil.getFile().getName());		
								
				//remember: the XSLTs are written context unaware, 
				//so they need to get all necessary context info as inparams.
				Map<String,String> parameters = new HashMap<String,String>();
				parameters.put("uid", uid);
				parameters.put("title", title);
				parameters.put("totalElapsedTime",new SmilClock(totalElapsedTimeMillis).toString(SmilClock.FULL));
				parameters.put("timeinThisSmil",d202_smil.getCalculatedDuration().toString(SmilClock.FULL));				
				if (isNccOnly) {
					//send this so that XSLT knows that it should drop text elements
					//the presence of this param means "drop text elems"
					//the absence of this param means "dont drop text elems"
					parameters.put("isNcxOnly","true");
				}						
				
				Stylesheet.apply(d202_smil.getFile().getAbsolutePath(), xsltURL, smilOut.getAbsolutePath(),
						TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
				
				mManifestItems.put(smilOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986SmilFile",smilOut.toURI()));								
				totalElapsedTimeMillis += d202_smil.getCalculatedDuration().millisecondsValue();						                	              	
			} //if (fsf instanceof D202SmilFile)
		}
		
		mTransformer.delegateProgress(this, SMIL_DONE);
		return new SmilClock(totalElapsedTimeMillis);
	}
	
	private void createZedNcx(D202NccFile ncc, String uid) throws CatalogExceptionNotRecoverable, XSLTException, FilesetFatalException {
				  		
		String ncxFileName = "navigation.ncx";
		
		mTransformer.delegateMessage(this, 
				mTransformer.delegateLocalize("CREATING_NCX", new String[]{ncxFileName}), 
				MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM,null);
		
		File ncxOut = new File(mOutputDir, ncxFileName);

		URL xsltURL = this.getClass().getResource("./xslt/d202ncc_Z2005ncx.xsl");
				
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("uid", uid);
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltURL, ncxOut.getAbsolutePath(), 
				TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.put(ncxOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986NcxFile", ncxOut.toURI()));
		
		mTransformer.delegateProgress(this, NCX_DONE);		
	}
	
	/**
	 * Transform each occurence (1-n) of XHTML content docs to Dtbook.
	 * <p>The output base file names are maintained, only extension change (foo.html into foo.xml)</p>
	 * <p>We also know that SMIL file names are maintained, so smilref values do not change at all.</p>
	 * <p>This method is not called if the output is to be NCX-only.</p>
	 */
	@SuppressWarnings("unchecked")
	private void createZedDtbook(Fileset inputFileset, String uid, String title) throws CatalogExceptionNotRecoverable, XSLTException, FilesetFatalException {
			
		Iterator i = inputFileset.getLocalMembers().iterator();
		while(i.hasNext()) {
			FilesetFile ff = (FilesetFile)i.next();
			if(ff instanceof D202TextualContentFile) {
				String dtbookFileName = ff.getNameMinusExtension()+".xml";
	
				mTransformer.delegateMessage(this, 
						mTransformer.delegateLocalize("CREATING_DTBOOK", new String[]{dtbookFileName}), 
						MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM,null);
				
				File dtbookOut = new File(mOutputDir, dtbookFileName);  //TODO relativize
				URL xsltURL = Stylesheets.get("pers_stylesheet"); //TODO
				
				Map<String,String> parameters = new HashMap<String,String>();
				parameters.put("uid", uid);
				parameters.put("title", title);
				parameters.put("cssUri", "cssUri"); //TODO
				
				Stylesheet.apply(ff.getFile().getAbsolutePath(), xsltURL, dtbookOut.getAbsolutePath(), 
						TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
				mManifestItems.put(dtbookOut.getAbsolutePath(), 
						mFilesetFileFactory.newFilesetFile("Z3986DtbookFile",dtbookOut.toURI()));
				
			}
		}			
		mTransformer.delegateProgress(this, DTBOOK_DONE);		
	}
	
	/**
	 * The xslt creates metadata, the spine, and adds the smilfiles to manifest.
	 * This method adds the other stuff to manifest (in .finalizeManifest).
	 */
	private void createZedOpf(D202NccFile ncc, String uid, SmilClock dtbTotalTime) throws XSLTException, IOException, ParserConfigurationException, SAXException, FilesetFatalException {

		String opfFileName = "package.opf";         

		mTransformer.delegateMessage(this, 
				mTransformer.delegateLocalize("CREATING_OPF", new String[]{opfFileName}), 
				MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM,null);
		
		File opfOut = new File(mOutputDir, opfFileName);

		URL xsltURL = this.getClass().getResource("./xslt/d202ncc_Z2005opf.xsl");
				
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("dtbTotalTime", dtbTotalTime.toString(SmilClock.FULL));
		parameters.put("dtbMultimediaContent", getTopLevelMediaTypes());
		parameters.put("uid", uid);
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltURL, opfOut.getAbsolutePath(), 
				TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.put(opfOut.getAbsolutePath(), mFilesetFileFactory.newFilesetFile("Z3986OpfFile",opfOut.toURI()));
		
		//run finalize after manifestItems.put so that the opf gets included in the manifest itemlist
		finalizeOpf(opfOut);
		
		mTransformer.delegateProgress(this, OPF_DONE);
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
		for (Iterator<String> i = mManifestItems.keySet().iterator(); i.hasNext(); ) {		  
			try{
				FilesetFile fsf = mManifestItems.get(i.next());
				String mime = fsf.getMimeType().dropParametersPart();						
				URI opfURI = unfinishedOpf.getParentFile().toURI();
				
				if (!(fsf instanceof SmilFile)) { //xslt added smilfiles already
					
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
						id="dtbook_"+k;
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
			mTransformer.delegateMessage(this, e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM,null);
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
	
	/**
	 * Copy all files that travel from input to output unchanged
	 */
	@SuppressWarnings("unchecked")
	private void copyMembers(Fileset mInputFileset) throws FilesetFatalException, IOException {		
		Set<FilesetFile> inputFilesToCopy = new HashSet<FilesetFile>();		
		for (Iterator<FilesetFile> it = mInputFileset.getLocalMembers().iterator(); it.hasNext();) {
			FilesetFile fsf = it.next();
			if (fsf instanceof AudioFile) {
				inputFilesToCopy.add(fsf);
			} else if (fsf instanceof ImageFile) {
				inputFilesToCopy.add(fsf);
			} 
		}												
		this.copyFiles(inputFilesToCopy, mInputFileset);			
		mTransformer.delegateProgress(this, COPY_DONE);		
	}
	
	/**
	 * Copy a set of files belonging to a Fileset, relative to the manifest member.
	 * @param files the set of files to copy
	 * @throws IOException
	 * @throws FilesetFatalException 
	 */
	private void copyFiles(Set<FilesetFile> files, Fileset fileset) throws IOException, FilesetFatalException {
		int fileNum = files.size();
		int fileCount = 0;
		for (Iterator<FilesetFile> it = files.iterator(); it.hasNext(); ) {
			fileCount++;
			FilesetFile fsf = it.next();
			URI relativeURI = fileset.getManifestMember().getRelativeURI(fsf);
			File out = new File(mOutputDir.toURI().resolve(relativeURI));
			FileUtils.copy(fsf.getFile(), out);
			//populate the mManifestItems set for use when creating the the package manifest 
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
			mTransformer.delegateProgress(this, OPF_DONE + (COPY_DONE-OPF_DONE)*((double)fileCount/fileNum));			
		}
	}
	
	/**
	 * Create the value for the dtb:multiMediaType metadata item.
	 */
	@SuppressWarnings("unchecked")
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

	/**
	 * Determine whether input DTB is NCC-only
	 * @return true of input DTB is true or untrue 2.02 NCC-only, else false
	 */
	@SuppressWarnings("unused")
	private boolean isNccOnly(Fileset fileset, 	Map<String, String> parameters) {
		//TODO
		System.err.println("MigratorImpl#isNccOnly hardcoded to true");
		if(fileset.getFilesetType() != FilesetType.DAISY_202) return false;
//		for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
//			FilesetFile fsf = (FilesetFile)it.next();
//			if (fsf instanceof D202TextualContentFile) {              
//				return false;
//			}            	
//		}  		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#setDelegateListener(org.daisy.pipeline.core.transformer.TransformerDelegateListener)
	 */
	public void setListener(TransformerDelegateListener transformer) {
		mTransformer = transformer;		
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#supports(int_daisy_dtbMigrator.DtbDescriptor, int_daisy_dtbMigrator.DtbDescriptor, java.util.Map)
	 */
	@SuppressWarnings("unused")
	public boolean supports(DtbDescriptor input, Fileset inputFileset, DtbDescriptor output, Map<String,String> parameters) {
		if(input.getVersion() == DtbVersion.D202 
				&& output.getVersion() == DtbVersion.Z2005) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#getNiceName()
	 */
	public String getNiceName() {		
		return mTransformer.delegateLocalize("D202_TO_Z3986", null);
	}

	@SuppressWarnings("unused")
	private String getAsciiFilename(String name, String extension) {
		return truncateToAscii(CharUtils.toNonWhitespace(name)) + "." + extension;		
	}

	private String truncateToAscii(String characters) {
		return CharUtils.toPrintableAscii(characters);		
	}
}
