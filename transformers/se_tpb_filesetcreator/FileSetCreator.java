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

package se_tpb_filesetcreator;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.UserAbortEvent;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.execution.ProgressObserver;
import org.daisy.util.file.FileBunchCopy;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Creates a z39.86-fileset given output from the transformer 
 * se_tpb_speechgenerator.SpeechGenerator.
 * 
 * @author Martin Blomberg
 *
 */
public class FileSetCreator extends Transformer {
	
	public static String DEBUG_PROPERTY = "org.daisy.debug";

	private static double SMILS_DONE = 0.43;	// time proportion of the smil generation.
	private static double NCX_DONE = 0.48;		// time proportion of the ncx generation.
	private static double OPF_DONE = 0.09;		// time proportion of the opf generation.
	private static double COPY_DONE = 0;		// time proportion of the file copying, may change further down
	
	private Map mimeTypes = new HashMap();		// file name suffixes -> mime types

	public FileSetCreator(InputListener inputListener, Set eventListeners, Boolean bool) {
		super(inputListener, eventListeners, bool);
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.dmfc.core.transformer.Transformer#execute(java.util.Map)
	 */
	/**
	 * Transformer enterpoint. 
	 * @param parameters the parameters supplied to this transformer
	 * @see org.daisy.dmfc.core.transformer.Transformer#execute(java.util.Map)
	 */
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		String outputDirectory = (String) parameters.remove("outputDirectory");
		String manuscriptFilename = (String) parameters.remove("manuscriptFilename");
		String outputDTBFilename = (String) parameters.remove("outputDTBFilename");
		String resourceFilename = (String) parameters.remove("resourceFilename");
		String smilTemplateFilename = (String) parameters.remove("smilTemplateFilename");
		String ncxTemplateFilename = (String) parameters.remove("ncxTemplateFilename");
		String opfTemplateFilename = (String) parameters.remove("opfTemplateFilename");
		String fscConfigFilename = (String) parameters.remove("fscConfigFilename");
		
		File outputDir = new File(outputDirectory);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
		
		//*****************************************************************************************************
		sendMessage(Level.CONFIG, i18n("USING_INPUT_FILE", manuscriptFilename));
		sendMessage(Level.CONFIG, i18n("USING_OUTPUT_DIR", outputDirectory));
		
		File finalDTBFile = new File(outputDTBFilename);
		File tmp = new File(changeSuffix(manuscriptFilename, ".ncx"));
		String ncxFilename = tmp.getName();
		tmp = new File(changeSuffix(manuscriptFilename, ".opf"));
		String opfFilename = tmp.getName();
		
		// the sets of element namnes to handle
		Set navListHeadings = new HashSet();
		Set customNavLists = new HashSet();
		Set escapable = new HashSet();
		Set skippable = new HashSet();
		Set forceLink = new HashSet();
		Set levels = new HashSet();
		
		Set generatedFiles = new HashSet();
		Set references = new HashSet();		
		File manuscriptFile = new File(manuscriptFilename);
		
		// will we have to copy files or are they already in the right directory?
		if (!outputDir.equals(manuscriptFile.getParentFile())) {
			COPY_DONE = 0.45;
			SMILS_DONE *= (1 - COPY_DONE);
			NCX_DONE *= (1 - COPY_DONE);
			OPF_DONE *= (1 - COPY_DONE);
		}
		

		SmilMaker sm;
		NCXMaker ncx;
		OPFMaker opf;
		
		try {
			
			//*****************************************************************************************************
			// Collect the src-attributes
			sendMessage(Level.FINEST, i18n("SEARCHING_FOR_REFERRED_FILES"));
					
			// get the files referred from the resource file + the resource file itself
			File resourceFile = new File(resourceFilename);
			SrcExtractor srcex = new SrcExtractor(resourceFile);
			Set files = srcex.getSrcValues();
			File inputBaseDir = srcex.getBaseDir();
			FileBunchCopy.copyFiles(inputBaseDir, outputDir, files, null, false);
			references.addAll(files);
			files.clear();
			files.add(resourceFile.getName());
			references.add(resourceFile.getName());
			FileBunchCopy.copyFiles(resourceFile.getParentFile(), outputDir, files);
			
			// get the files referred from the manuscript
			srcex = new SrcExtractor(manuscriptFile);
			files = srcex.getSrcValues();
			inputBaseDir = srcex.getBaseDir();
			FileBunchCopy.copyFiles(inputBaseDir, outputDir, files, null, true);
			references.addAll(files);
			
			sendMessage(Level.FINEST, i18n("DONE"));

			
			//*****************************************************************************************************
			// Parse the user-defined configuration
			parseConfigFile(fscConfigFilename, 
					navListHeadings, 
					customNavLists, 
					forceLink, 
					escapable, 
					skippable, 
					levels);
			
			//*****************************************************************************************************
			// Create the smil files
			sendMessage(Level.FINEST, i18n("GENERATING_SMIL"));			
			
			ProgressObserver pr = new ProgressObserver() {
				public void reportProgress(double progress) {
					progress(progress * SMILS_DONE);
				}
			};
			sm = new SmilMaker(
					manuscriptFile, 
					outputDir, 
					new File(smilTemplateFilename),
					skippable, 
					escapable,
					forceLink,
					pr,
					this);
		
			//addAbortListener(sm);
			//mg20070327: use the new event api
			EventBus.getInstance().subscribe(sm, UserAbortEvent.class);
			sm.setFinalDTBookFilename(finalDTBFile.getName());
			sm.makeSmils();
			//removeAbortListener(sm);
			EventBus.getInstance().unsubscribe(sm, UserAbortEvent.class);
			
						
			// collect usable data from the smilmaker
			Set allCustomTests = sm.getAllCustomTests();
			String totalTime = sm.getStrTotoalTime();
			Vector smilFiles = sm.getAllGeneratedSmilFiles();
			File modifiedManuscriptFile = sm.getModifiedManuscriptFile();
			references.addAll(sm.getAdditionalFiles());
			sendMessage(Level.FINEST, i18n("DONE"));
			
					
			//*****************************************************************************************************
			// Create the ncxmaker and put it to work
			sendMessage(Level.FINEST, i18n("GENERATING_NCX"));
			
			pr = new ProgressObserver() {
				public void reportProgress(double progress) {
					progress((progress * NCX_DONE) + SMILS_DONE);
				}
			};

			
			
			ncx = new NCXMaker(
					modifiedManuscriptFile, 
					levels,
					navListHeadings,
					customNavLists,
					finalDTBFile,
					new File(ncxTemplateFilename),
					pr,
					this);
			
			ncx.setCustomTests(allCustomTests, getBookStructs());
			ncx.setNCXOutputFile(new File(outputDir, ncxFilename));
			//addAbortListener(ncx);
			//mg 20070327: use the new event api
			EventBus.getInstance().subscribe(ncx, UserAbortEvent.class);
			ncx.makeNCX();
			//removeAbortListener(ncx);
			EventBus.getInstance().unsubscribe(ncx, UserAbortEvent.class);
						
			sendMessage(Level.FINEST, i18n("DONE"));
			
			generatedFiles.add(finalDTBFile.getName());
			generatedFiles.add(ncxFilename);
			generatedFiles.add(opfFilename);
			
			
			//*****************************************************************************************************
			// Create the opfmaker and put it to work			
			sendMessage(Level.FINEST, i18n("GENERATING_OPF"));
					
			Map dcElements = ncx.getDCElements();		
			if (null == dcElements.get("dc:Date")) {
				Date now = new Date();			
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");	
				dcElements.put("dc:Date", dateFormat.format(now));
			}
			Map metaElements = new HashMap();
			mimeTypes = getMimeTypes();
			
			String mediaContent = "";
			if (containsMimeType(references, mimeTypes, "image")) {
				mediaContent = ",image";
			}
			
			metaElements.put("dtb:totalTime", totalTime);
			metaElements.put("dtb:multimediaContent", "audio,text" + mediaContent);
			
			opf = new OPFMaker(
					mimeTypes,
					dcElements, 
					metaElements, 
					smilFiles, 
					generatedFiles, 
					references,
					new File(opfTemplateFilename), 
					new File(outputDir, opfFilename), 
					new File(manuscriptFilename).getParentFile());
			
			opf.makeOPF();
			checkAbort();
			
			sendMessage(Level.FINEST, i18n("DONE"));
			
			sendMessage(Level.FINEST, i18n("AUDIO_FILE_COPY"));
			progress(SMILS_DONE + NCX_DONE + OPF_DONE);
			
			pr = new ProgressObserver() {
				public void reportProgress(double progress) {
					progress(progress * COPY_DONE + SMILS_DONE + NCX_DONE + OPF_DONE);
				}
			};
			FileBunchCopy.copyFiles(manuscriptFile.getParentFile(), outputDir, sm.getAdditionalFiles(), pr, false);
			sendMessage(Level.FINEST, i18n("DONE"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransformerRunException(e.getMessage(), e);
		}
		
		return true;
	}



	/**
	 * Returns the book structures.
	 * @return the book structures.
	 */
	private Map getBookStructs() {
		Map structs = new HashMap();
		
		structs.put("linenum", "LINE_NUMBER");
		structs.put("note", "NOTE");
		structs.put("noteref", "NOTE_REFERENCE");
		structs.put("annotation", "ANNOTATION");
		structs.put("pagenum", "PAGE_NUMBER");
		structs.put("sidebar", "OPTIONAL_SIDEBAR");
		structs.put("prodnote", "OPTIONAL_PRODUCER_NOTE");
		
		return structs;
	}

	/**
	 * Returns the MIME-types.
	 * @return the MIME-types.
	 */
	private Map getMimeTypes() {
		
		mimeTypes.put(".mp4", "audio/mpeg4-generic");
		mimeTypes.put(".mp3", "audio/mpeg");
		mimeTypes.put(".wav", "audio/x-wav");
		mimeTypes.put(".jpg", "image/jpeg");
		mimeTypes.put(".png", "image/png");
		mimeTypes.put(".svg", "image/svg+xml");
		mimeTypes.put(".smil", "application/smil");
		mimeTypes.put(".opf", "text/xml");
		mimeTypes.put(".ncx", "application/x-dtbncx+xml");
		mimeTypes.put(".xml", "application/x-dtbook+xml");
		mimeTypes.put(".res", "application/x-dtbresource+xml");
		
		return mimeTypes;
	}
	
	
	/**
	 * Parses the config file. Populates the input Sets with names of elements.
	 * @param filename the config file.
	 * @param navListHeadings Set to hold the names of the elements 
	 * representing navlist headings in the ncx.
	 * @param customNavLists Set to hold the names of the elements 
	 * representing custom navlists in the ncx.
	 * @param forceLink Set to hold the names of elements 
	 * that always should be linked together with their references, example note (-noteref) and
	 * annotation (-annoref).
	 * @param escapable Set to hold the names of the escapable elements.
	 * @param skippable Set to hold the names of the skippable elements.
	 * @param levels Set to hold the names of the level chaning elements.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void parseConfigFile(
			String filename, 
			Set navListHeadings, 
			Set customNavLists,
			Set forceLink, 
			Set escapable, 
			Set skippable, 
			Set levels) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document config = db.parse(new File(filename));
		Node root = config.getDocumentElement();
		
		
		getConfigItems(navListHeadings, root, "//navListHeading");
		getConfigItems(customNavLists, root, "//customNavList");		
		getConfigItems(forceLink, root, "//forceLink");
		getConfigItems(escapable, root, "//escapable");		
		getConfigItems(skippable, root, "//skippable");
		getConfigItems(levels, root, "//levels");
	}
	
	
	/**
	 * Gets the element names for a certain category of the configuration. 
	 * @param set the container in which the element names will be stored.
	 * @param root	the root of the DOM 
	 * @param xPath	xpath selecting the parent element
	 */
	private void getConfigItems(Set set, Node root, String xPath) {
		Set elemNames = new HashSet();
		Node parent = XPathUtils.selectSingleNode(root, xPath);
		NodeList items = parent.getChildNodes();
		DEBUG("xPath: " + xPath);
		for (int i = 0; i < items.getLength(); i++) {
			Node node = items.item(i);
			String content = node.getTextContent().trim();
			if (content.length() == 0) {
				continue;
			}
			elemNames.add(node.getTextContent());
			set.add(node.getTextContent().trim());
			DEBUG("tc: " + node.getTextContent());
		}
	}
	
	/**
	 * Changes the suffix.
	 * @param filename the original filename.
	 * @param desiredSuffix the desired suffix.
	 * @return the filename with the changed prefix.
	 */
	private String changeSuffix(String filename, String desiredSuffix) {
		int i = filename.lastIndexOf('.');
		return filename.substring(0, i) + desiredSuffix;
	}
	
	
	/**
	 * Returns <tt>true<tt> if the set of files contains a file with
	 * a mime type that starts with <tt>prefix</tt>.
	 * @param filenames
	 * @param mimetypes
	 * @param prefix
	 * @return
	 */
	private boolean containsMimeType(Set filenames, Map mimetypes, String prefix) {
		for (Iterator it = filenames.iterator(); it.hasNext(); ) {
			String filename = (String) it.next();
			String suffix = filename.substring(filename.lastIndexOf('.'));
			String mime = (String) mimetypes.get(suffix);
			if (null == mime) {
				continue;
			}
			
			if (mime.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Prints debug messages on System.out iff the system property 
	 * represented by <tt>FileSetCreator.DEBUG_PROPERTY</tt> is defined.
	 * Debug messages are prefixed with "<tt>DEBUG: </tt>".
	 * @param msg the message.
	 */
	private void DEBUG(String msg) {
		if (System.getProperty(DEBUG_PROPERTY) != null) {
			System.err.println("DEBUG: " + msg);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.daisy.dmfc.core.transformer.Transformer#progress(double)
	 */
	public void progress(double progressProportion) {
		super.progress(progressProportion);
	}
	
	
	/**
	 * Called by other parts of the program to see if the user has
	 * aborted the program. If so, this method will throw an exception.
	 * @throws TransformerAbortException if program has been aborted by user.
	 */
	public void checkTransformerAborted() throws TransformerAbortException {
		super.checkAbort();
	}
	
	
	/** Returns the version of the dtbook document.
	 * @param dtbook  the dtbook file.
	 * @return  the version of the dtbook document.
	 */
	public String getDTBookVersion(File dtbook) {
		// peek into the document to determine the version.
		String version = null;
		PeekerPool pp = PeekerPool.getInstance();
		Peeker p = null;
		
		try {
			p = pp.acquire(false);
			PeekResult pr = p.peek(dtbook);
			Attributes attrs = pr.getRootElementAttributes();
			version = attrs.getValue("version");
		} catch (SAXException e) {
			
		} catch (IOException e) {
			
		} catch (PoolException e) {
			
		} finally {
			try {
				pp.release(p);
			} catch (PoolException e) {
				// nada
			}
		}

		if (null == version) {
			sendMessage(Level.WARNING, i18n("VERSION_NOT_FOUND"));
			sendMessage(Level.WARNING, i18n("2005-1_FALLBACK"));
		}
		
		return version;
	}
}
