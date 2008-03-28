package int_daisy_dtbMigrator.impl.d202_z2005;

import int_daisy_dtbMigrator.BookStruct;
import int_daisy_dtbMigrator.DtbDescriptor;
import int_daisy_dtbMigrator.DtbVersion;
import int_daisy_dtbMigrator.Migrator;
import int_daisy_dtbMigrator.MigratorException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.css.stylesheets.Css;
import org.daisy.util.dtb.resource.ResourceFile;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.AnonymousFile;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
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
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986ResourceFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;
import org.daisy.util.fileset.util.URIStringParser;
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
 * if equal then assume this is NCC only. 
 * 
 */

public class MigratorImpl implements Migrator, FilesetErrorHandler, ErrorListener {
	
	/** Owning Transformer */
	private TransformerDelegateListener mTransformer = null;
	
	/** A list of all files in output. Used ultimately when writing the package manifest */	
	private ManifestItems mManifestItems = null;
	
	/** Destination directory */
	private EFolder mOutputDir = null;

	/** Size of input fileset */
	private int mInputSize = 0;

	/** Counter for completed transforms/copies */
	private int mCompletionCounter = 0;
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#migrate(int_daisy_dtbMigrator.DtbDescriptor, 
	 * 		int_daisy_dtbMigrator.DtbDescriptor, java.util.Map, org.daisy.util.fileset.interfaces.Fileset, org.daisy.util.file.EFolder)
	 */	
	public void migrate(DtbDescriptor input, 
						DtbDescriptor output, 
						Map<String,String> parameters, 
						Fileset inputFileset, 
						EFolder destination) throws MigratorException {

		mOutputDir = destination;
		mManifestItems = new ManifestItems();
		mInputSize  = inputFileset.getLocalMembers().size();
		
		
		try{	
			/*
			 * Since we maintain SMIL file names in input and output,
			 * the input and output directories cannot be the same.
			 */
			checkIO(inputFileset, destination);
			
			
			/*
			 * Collect variables from input dtb that need to be sent as parameters to the XSLTs
			 */   
			InputProperties inputProperties = new InputProperties(inputFileset, input, parameters);
				
			
			/*
			 * Create zed smil from the 2.02 smil input docs
			 */
			SmilClock dtbTotalTime = createZedSmil(inputFileset, inputProperties);
			
			/*
			 * Parse all created SMIL files
			 */
			for(FilesetFile f : mManifestItems) {
				f.parse();
			}
			
		
			/*
			 * Create NCX from the input NCC
			 */
			createZedNcx((D202NccFile)inputFileset.getManifestMember(),inputProperties,parameters);
		
			/*
			 * Create dtbook from the input xhtml
			 */
			if (!inputProperties.isNccOnly()) {				
				createZedDtbook(inputFileset,inputProperties,parameters);
			}
		
			
			/*
			 * Add a resource file
			 */
			addResourceFile(parameters);

						
			/*
			 * Copy things that move to output unchanged
			 */			
			copyStaticMembers(inputFileset);
			
		
			/*
			 * Finally, the output topology is getting stable, create the opf
			 */			                            			
			createZedOpf((D202NccFile)inputFileset.getManifestMember(),inputProperties,dtbTotalTime);
		
		}catch (Exception e) {
			throw new MigratorException(e.getLocalizedMessage(),e);
		}
	}

	/**
	 * Locally report the completion of the transform or copy of one single file.
	 */
	private void reportCompleted(File file) {
		mTransformer.delegateMessage(this, mTransformer.delegateLocalize(
				"COMPLETED", new String[]{file.getAbsolutePath()}), 
					MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM, null);
		
		mCompletionCounter++;
		//+3 = add 1 opf and 1 resource and 1 css
		mTransformer.delegateProgress(this, mCompletionCounter/(mInputSize+3)); 
		
	}
	
	/**
	 * Transform all SMIL files of input 2.02 fileset into Z2005 SMIL files.
	 * <p>SMIL file names are maintained unchanged.</p>
	 * @return the total playback time
	 */
	
	private SmilClock createZedSmil(Fileset inputFileset, InputProperties properties) throws CatalogExceptionNotRecoverable, XSLTException {
				
		mTransformer.delegateMessage(this, mTransformer.delegateLocalize("CREATING_SMIL", null), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM, null);
				
		URL xsltURL = this.getClass().getResource("./xslt/d202smil_Z2005smil.xsl");

		//TODO use a cached transformer
		
		long totalElapsedTimeMillis = 0;
		for (Iterator<?> it = inputFileset.getManifestMember().getReferencedLocalMembers().iterator(); it.hasNext(); ) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof D202SmilFile) {
				D202SmilFile in = (D202SmilFile) fsf;
				File out = new File(mOutputDir, in.getFile().getName());		
								
				//remember: the XSLTs are written context unaware, 
				//so they need to get all necessary context info as inparams.
				Map<String,String> parameters = new HashMap<String,String>();
				//unique identifier of the publication
				parameters.put("uid", properties.getIdentifier());
				parameters.put("title", properties.getTitle());
				//time value of time elapsed until the onset of this smil file
				parameters.put("totalElapsedTime",new SmilClock(totalElapsedTimeMillis).toString(SmilClock.FULL));
				//duration of this smil file
				parameters.put("timeinThisSmil",in.getCalculatedDuration().toString(SmilClock.FULL));
				//default state to use for skippables, if that skippable appears at all.
				parameters.put("defaultStatePagenumbers",properties.getDefaultState(BookStruct.PAGE_NUMBER).toString());
				parameters.put("defaultStateSidebars",properties.getDefaultState(BookStruct.OPTIONAL_SIDEBAR).toString());
				parameters.put("defaultStateFootnotes",properties.getDefaultState(BookStruct.NOTE).toString());
				parameters.put("defaultStateProdnotes",properties.getDefaultState(BookStruct.OPTIONAL_PRODUCER_NOTE).toString());
				if (properties.isNccOnly()) {
					//send this so that XSLT knows that it should drop text elements
					//the presence of this param means "drop text elems"
					//the absence of this param means "dont drop text elems"
					parameters.put("isNcxOnly","true");
				}						
				// Whether the NCC points to pars in this SMIL file. If all targets point to par, value is 'true', else 'false'.
				parameters.put("NCCPointsToPars",Boolean.toString(getSmilTargetType(in, (D202NccFile)inputFileset.getManifestMember())));
								
				Stylesheet.apply(in.getFile().getAbsolutePath(), xsltURL, out.getAbsolutePath(),
						TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
				
				mManifestItems.add(out.toURI(), Z3986SmilFile.class);
								
				totalElapsedTimeMillis += in.getCalculatedDuration().millisecondsValue();
				
				reportCompleted(out);
				
			} //if (fsf instanceof D202SmilFile)
		}
		
		return new SmilClock(totalElapsedTimeMillis);
		
	}
		
	/**
	 * Does the NCC point to par or text in incoming smil file?
	 * If all targets point to par, return true, else return false.
	 */
	private boolean getSmilTargetType(D202SmilFile smil, D202NccFile ncc) {
		QName par = new QName("par");
		try{
			Collection<?> uris = ncc.getUriStrings();
			for(Object o : uris) {
				String uri = (String)o;
				if(uri.startsWith(smil.getName())) {
					String id = URIStringParser.getFragment(uri);	
					if(!smil.hasIDValueOnQName(id, par)){
						return false;
					}
				}
			}
			return true;			
		}catch (Exception e) {
			System.err.println("d202_z2005.MigratoImpl#getSmilTargetType: " + e.getMessage());			
		}
		return false;
	}

	private void createZedNcx(D202NccFile ncc, InputProperties properties, Map<?,?> params) throws XSLTException, SAXException {
				  		
		String ncxFileName = "navigation.ncx";
		
		mTransformer.delegateMessage(this, 
				mTransformer.delegateLocalize("CREATING_NCX", new String[]{ncxFileName}), 
				MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM,null);
		
		File ncxOut = new File(mOutputDir, ncxFileName);

		URL xsltURL = this.getClass().getResource("./xslt/d202ncc_Z2005ncx.xsl");
				
		Map<String,String> parameters = new HashMap<String,String>();
		//unique identifier of the publication
		parameters.put("uid", properties.getIdentifier());
		
		//a list of customTests that appears in the DTB.
		String customTestIDs = getSmilCustomTests();
		if(customTestIDs!=null&&customTestIDs.length()>0) {
			parameters.put("smilCustomTests",customTestIDs);
		}	
		//default state to use for skippables, if that skippable appears at all.
		parameters.put("defaultStatePagenumbers", properties.getDefaultState(BookStruct.PAGE_NUMBER).toString());
		parameters.put("defaultStateSidebars",properties.getDefaultState(BookStruct.OPTIONAL_SIDEBAR).toString());
		parameters.put("defaultStateFootnotes",properties.getDefaultState(BookStruct.NOTE).toString());
		parameters.put("defaultStateProdnotes",properties.getDefaultState(BookStruct.OPTIONAL_PRODUCER_NOTE).toString());
				
		// A user preference: whether to add audio elements to navLabel by opening the reffed smil and get the closest audio
		parameters.put("addNavLabelAudio", (String)params.get("ncxAddNavLabelAudio"));
		// A user preference: the minimum length (in millisec) for audioclips used in NavLabel elements
		parameters.put("minNavLabelAudioLength", (String)params.get("ncxMinNavLabelAudioLength"));
		
		// The location of the ncc folder, assumed in the style sheet to also be the location of the SMIL files. A bit risky?
		parameters.put("nccFolder",ncc.getFile().getParent());
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltURL, ncxOut.getAbsolutePath(), 
				TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.add(ncxOut.toURI(),Z3986NcxFile.class);
		
		reportCompleted(ncxOut);
		
	}
	
	/**
	 * Get a list of the customTests that are actually used in the book.
	 * This method requires that mManifestItems contain the output zed SMIL files.
	 * These IDs are one of the following, and map naturally to one bookstruct each:
	 * pagenumber,sidebar,footnote,prodnote
	 */
	private String getSmilCustomTests() {
		Set<String> customTestIDs = new HashSet<String>();
		for(FilesetFile f : mManifestItems) {
			if(f instanceof Z3986SmilFile) {
				Z3986SmilFile smil = (Z3986SmilFile)f;				
				customTestIDs.addAll(smil.getCustomTestIDs());				
			}
		}
		StringBuilder builder = new StringBuilder();
		for (String id : customTestIDs) {
			builder.append(id);
			builder.append(' ');
		}
		if(builder.length()>0)
			return builder.deleteCharAt(builder.length()-1).toString();
		return "";
	}

	/**
	 * Add a resource file
	 */
	private void addResourceFile(Map<String, String> parameters) {
		
		String resUri = parameters.remove("outputResourceFile");
		File res = null;
		if(resUri!=null && resUri.length()>0) {
			//try to add the users resource file to output directory.
			try{
				res = FilenameOrFileURI.toFile(resUri);				 
				Fileset resFileset = new FilesetImpl(res.toURI(),this,false,false);
				mOutputDir.addFileset(resFileset, true);
				for (Iterator<?> iterator = resFileset.getLocalMembers().iterator(); iterator.hasNext();) {
					mManifestItems.add((FilesetFile) iterator.next());					
				}
			}catch (Exception e) {
				mTransformer.delegateMessage(this, 
						mTransformer.delegateLocalize(
								"RES_ERROR", new String[]{e.getMessage()}), 
									MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, null);
				res = null;
			}			
		}
		
		if(res==null) {
			//add a default resource
			try{
				Set<URL> resources = ResourceFile.get(ResourceFile.Type.TEXT_ONLY);
				
				for(URL u : resources) {												
					InputStream is = u.openStream();
					String localName = new File(u.toURI()).getName();
					EFile out = new EFile(mOutputDir.writeToFile(localName, is));
					is.close();
					if(out.getExtension().equals("res")) res = out;					
				}
				Fileset resourceFileset = new FilesetImpl(res.toURI(),this,false,false);
				for (Iterator<?> iterator = resourceFileset.getLocalMembers().iterator(); iterator.hasNext();) {
					mManifestItems.add((FilesetFile) iterator.next());					
				}
			}catch (Exception e) {
				mTransformer.delegateMessage(this, 
						mTransformer.delegateLocalize(
								"RES_ERROR", new String[]{e.getMessage()}), 
									MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, null);
				
			}				
		}	
		
		if(res!=null)reportCompleted(res);
	}
	
	/**
	 * Transform each occurence (1-n) of XHTML content docs to Dtbook.
	 * <p>The output base file names are maintained, only extension change (foo.html into foo.xml)</p>
	 * <p>We also know that SMIL file names are maintained, so smilref values do not change at all.</p>
	 * <p>We add a CSS to output dir using user inparams or a default CSS.
	 * <p>This method is not called if the output is to be NCX-only.</p>
	 */	
	private void createZedDtbook(Fileset inputFileset, InputProperties properties, Map<String,String> params) throws CatalogExceptionNotRecoverable, XSLTException {
		
		/*
		 * Find out what CSS to use.
		 */		
		String cssUri = params.remove("outputCSS");
		File css = null;
		if(cssUri!=null && cssUri.length()>0) {
			//try to add the users CSS to output directory.
			try{
				css = FilenameOrFileURI.toFile(cssUri);				 
				Fileset cssFileset = new FilesetImpl(css.toURI(),this,false,false);
				mOutputDir.addFileset(cssFileset, true);
				for (Iterator<?> iterator = cssFileset.getLocalMembers().iterator(); iterator.hasNext();) {
					mManifestItems.add((FilesetFile) iterator.next());					
				}
				//prep the value for the XSLT below
				cssUri = css.getName();
			}catch (Exception e) {
				mTransformer.delegateMessage(this, 
						mTransformer.delegateLocalize(
								"CSS_ERROR", new String[]{e.getMessage()}), 
								MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT, null);
				css = null;
			}			
		}
		
		if(css==null) {
			//add a default stylesheet
			try{
				URL url = Css.get(Css.DocumentType.Z3986_DTBOOK);
				css = new File(url.toURI());
				String name = css.getName();
				InputStream is = url.openStream();
				mOutputDir.writeToFile(name, is);
				is.close();
				//prep the value for the XSLT below
				cssUri = css.getName();
			}catch (Exception e) {
				mTransformer.delegateMessage(this, 
						mTransformer.delegateLocalize(
								"CSS_ERROR", new String[]{e.getMessage()}), 
								MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
				css = null;
			}				
		}		
		if(css!=null)reportCompleted(css);
		
		/*
		 * Transform the docs.
		 */		
		for (Iterator<?> iterator = inputFileset.getLocalMembers().iterator(); iterator.hasNext();) {		
			
			FilesetFile ff = (FilesetFile)iterator.next();
			if(ff instanceof D202TextualContentFile) {
				
				String dtbookFileName = ff.getNameMinusExtension()+".xml";
	
				mTransformer.delegateMessage(this, 
						mTransformer.delegateLocalize("CREATING_DTBOOK", new String[]{dtbookFileName}), 
							MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM,null);
				
				File dtbookOut = new File(mOutputDir, dtbookFileName);
				
				URL xsltURL = Stylesheets.get("xhtml2dtbook.xsl");
				
				Map<String,String> parameters = new HashMap<String,String>();
				parameters.put("uid", properties.getIdentifier());
				parameters.put("title", properties.getTitle());
				parameters.put("cssUri", cssUri); 
				// A user preference: shall meta data be transfered from the ncc file to the DTBook
				parameters.put("transferDcMetadata", (String)params.get("dtbookTransferNCCMetadata"));
				
				// The location of the ncc file (assuming that it is the same folder as the content doc. Risky?)
				parameters.put("nccURI",inputFileset.getManifestMember().getFile().toURI().toString());
				
				Stylesheet.apply(ff.getFile().getAbsolutePath(), xsltURL, dtbookOut.getAbsolutePath(), 
						TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
				
				mManifestItems.add(dtbookOut.toURI(), Z3986DtbookFile.class);
				
				reportCompleted(dtbookOut);
				
			} //if(ff instanceof D202TextualContentFile) {
		}			
		
	}
	
	/**
	 * The xslt creates metadata, the spine, and adds the smilfiles to manifest.
	 * This method adds the other stuff to manifest (in .finalizeManifest).
	 */
	private void createZedOpf(D202NccFile ncc, InputProperties properties, SmilClock dtbTotalTime) throws XSLTException, IOException, ParserConfigurationException, SAXException {

		String opfFileName = "package.opf";         
				
		mTransformer.delegateMessage(this, 
				mTransformer.delegateLocalize("CREATING_OPF", new String[]{opfFileName}), 
					MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM,null);
		
		File opfOut = new File(mOutputDir, opfFileName);

		URL xsltURL = this.getClass().getResource("./xslt/d202ncc_Z2005opf.xsl");
				
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("dtbTotalTime", dtbTotalTime.toString(SmilClock.FULL));
		parameters.put("dtbMultimediaContent", getTopLevelMediaTypes());
		parameters.put("uid", properties.getIdentifier());
		
		Stylesheet.apply(ncc.getFile().getAbsolutePath(), xsltURL, opfOut.getAbsolutePath(), 
				TransformerFactoryConstants.SAXON8, parameters, CatalogEntityResolver.getInstance());
		
		mManifestItems.add(opfOut.toURI(), Z3986OpfFile.class);
		
		//run finalize after manifestItems.put so that the opf gets included in the manifest itemlist
		finalizeOpf(opfOut);
		
		reportCompleted(opfOut);
		
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
		for (Iterator<FilesetFile> i = mManifestItems.iterator(); i.hasNext(); ) {		  
			try{
				FilesetFile fsf = i.next();
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
				mTransformer.delegateMessage(this, e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM,null);
			}						
		}
		
		/*
		 * Move the root xmlns:dc decl, since the DTD doesnt allow it.
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
	private void copyStaticMembers(Fileset mInputFileset) throws IOException {		
		Set<FilesetFile> inputFilesToCopy = new HashSet<FilesetFile>();		
		for (Iterator<?> it = mInputFileset.getLocalMembers().iterator(); it.hasNext();) {
			FilesetFile fsf = (FilesetFile)it.next();
			if (fsf instanceof AudioFile || fsf instanceof ImageFile) {
				inputFilesToCopy.add(fsf);
			} 
		}												
		this.copyFiles(inputFilesToCopy, mInputFileset);					
	}
	
	/**
	 * Copy a set of files belonging to a Fileset, relative to the manifest member.
	 * @param files the set of files to copy
	 * @throws IOException
	 */
	private void copyFiles(Set<FilesetFile> files, Fileset fileset) throws IOException {				
		for (Iterator<FilesetFile> it = files.iterator(); it.hasNext(); ) {			
			FilesetFile fsf = it.next();
			URI relativeURI = fileset.getManifestMember().getRelativeURI(fsf);
			File out = new File(mOutputDir.toURI().resolve(relativeURI));
			FileUtils.copy(fsf.getFile(), out);
			//populate the mManifestItems set for use when creating the the package manifest 
			if(fsf instanceof Mp3File) {
				mManifestItems.add(out.toURI(),Mp3File.class);
			}else if(fsf instanceof Mp2File) {
				mManifestItems.add(out.toURI(),Mp2File.class);                
			}else if(fsf instanceof WavFile) {
				mManifestItems.add(out.toURI(),WavFile.class);  
			}else if (fsf instanceof JpgFile) {
				mManifestItems.add(out.toURI(),JpgFile.class); 
			}else if (fsf instanceof PngFile) {
				mManifestItems.add(out.toURI(),PngFile.class); 
			}else if (fsf instanceof GifFile) {
				mManifestItems.add(out.toURI(),GifFile.class); 
			}else if (fsf instanceof CssFile) {
				mManifestItems.add(out.toURI(),CssFile.class); 
			}else{
				System.err.println("unknown filetype encountered in " + this.getClass().getSimpleName() + " .copyFiles");
				mManifestItems.add(out.toURI(),AnonymousFile.class);
			} 
			reportCompleted(out);
		}
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
	public boolean supports(DtbDescriptor input, Fileset inputFileset, DtbDescriptor output, Map<String,String> parameters) {
		if(input.getVersion() == DtbVersion.D202 
				&& output.getVersion() == DtbVersion.Z2005
					&& isSingleVolume(inputFileset)) {			
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_dtbMigrator.DtbMigrator#getNiceName()
	 */
	public String getNiceName() {		
		return mTransformer.delegateLocalize("D202_TO_Z39862005", null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */	
	public void error(FilesetFileException ffe) throws FilesetFileException {
		mTransformer.delegateMessage(this, ffe.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT, null);		
	}
	
	/**
	 * Determine whether a 2.02 fileset is single volume
	 */
	private boolean isSingleVolume(Fileset inputFileset) {
		return !((D202NccFile)inputFileset.getManifestMember()).hasMultiVolumeIndicators();
	}
	
	/**
	 * @throws MigratorException if input and output directories are the same.
	 */
	private void checkIO(Fileset inputFileset, EFolder destination) throws IOException, MigratorException {
		if(destination.getCanonicalPath().equals(
				inputFileset.getManifestMember().getFile()
					.getParentFile().getCanonicalPath())) {			
			throw new MigratorException(
					mTransformer.delegateLocalize(
							"INPUT_OUTPUT_DIRS_SAME", new Object[]{destination}));				
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
		
		for (Iterator<FilesetFile> iter = mManifestItems.iterator(); iter.hasNext();) {
			FilesetFile f =  iter.next();
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

	public void error(TransformerException exception) throws TransformerException {
		throw exception;
	}

	public void fatalError(TransformerException exception)throws TransformerException {
		throw exception;		
	}

	public void warning(TransformerException exception)throws TransformerException {
				
	}
	
//	private Transformer createTransformer(Source xslt, Map<String,String> parameters) throws XSLTException {
//		javax.xml.transform.Transformer transformer = null;
//		try {
//		    String property = "javax.xml.transform.TransformerFactory";
//		    String oldFactory = System.getProperty(property);		    
//		    System.setProperty(property, TransformerFactoryConstants.SAXON8);
//		    
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			try {
//				transformerFactory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
//			} catch (IllegalArgumentException iae) {
//				
//			}
//			transformerFactory.setErrorListener(this);
//						
//			// Reset old factory property
//			System.setProperty(property, (oldFactory==null?"":oldFactory));			
//	
//			// Create transformer
//	        transformer = transformerFactory.newTransformer(xslt);
//	
//	        // Set any parameters to the XSLT
//	        if (parameters != null) {
//	            for (Iterator<?> it = parameters.entrySet().iterator(); it.hasNext(); ) {
//	                Map.Entry paramEntry = (Map.Entry)it.next();
//	                transformer.setParameter((String)paramEntry.getKey(), paramEntry.getValue());
//	            }
//	        }
//	        
//	        transformer.setURIResolver(new CatalogURIResolver());  
//	        
//	    } catch (TransformerConfigurationException e) {
//	        throw new XSLTException(e.getMessageAndLocation(), e);                               
//	    } catch (CatalogExceptionNotRecoverable e) {
//	        throw new XSLTException(e.getMessage(), e);
//	    }
//    
//	    return transformer;
//	}
}
