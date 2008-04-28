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
package int_daisy_filesetRenamer;

import int_daisy_filesetRenamer.segment.EchoSegment;
import int_daisy_filesetRenamer.segment.FilesetUIDSegment;
import int_daisy_filesetRenamer.segment.FixedSegment;
import int_daisy_filesetRenamer.segment.LabelSegment;
import int_daisy_filesetRenamer.segment.RandomUniqueSegment;
import int_daisy_filesetRenamer.segment.SegmentedFileName;
import int_daisy_filesetRenamer.segment.SequenceSegment;
import int_daisy_filesetRenamer.strategies.DefaultStrategy;
import int_daisy_filesetRenamer.strategies.RenamingStrategy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Referring;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.Xhtml10File;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.RenamingCopier;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueExposer;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Rename select members of a fileset using a pattern based token algorithm. See Pipeline doc/transformers.
 * @author Markus Gylling
 */

public class FilesetRenamer extends Transformer implements FilesetManipulatorListener, XMLEventValueConsumer {
   
	private FilesetManipulator mFilesetManipulator = null;
	private EFile mInputManifest = null;
	private Fileset mInputFileset = null;
	private Directory mOutputDir = null;				//final output destination
	private Directory mRoundtripOutputDir = null; 	//for temporary storage, not always used
	private SegmentedFileName mTemplateName = null;
	private RenamingStrategy mStrategy = null;
	private List<Class<?>> mTypeExclusions = null;
	private boolean mFilesystemSafe = true;
	private FilesetFile mCurrentFile = null;
	private String oldName = null;	
	private FilesetRegex rgx = null;
	private int mMaxFilenameLength = -1;
			
	
	/**
	 * Constructor.
	 * @param inListener
	 * @param isInteractive
	 */
	public FilesetRenamer(InputListener inListener, Boolean isInteractive) {
		super(inListener,  isInteractive);
		rgx = FilesetRegex.getInstance();
	}
	
	
	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		/*
		 * First, create the renaming strategy using the input tokens.
		 * Validate this strategy. 
		 * If valid,
		 *   render, return true.
		 * If invalid and fixable (ie not a multiple new identical names error),
		 *   do a first pass of scrambling renaming to a subfolder of out
		 *   and then rerun the input strategy
		 * Validate this strategy, 
		 * If valid
		 *   render, return true.
		 * If invalid,
		 *   render unrenamed fileset (unless in and out are the same)
		 *   send error, return true.    
		 */
		
		FilesetManipulator fman = null;
		try {  				
			//set the input manifest
			mInputManifest = new EFile(FilenameOrFileURI.toFile(parameters.remove("input")));
			//set input fileset
			mInputFileset = new FilesetImpl(mInputManifest.toURI(),this,false,false);			
			//parse the renamingPattern param, create a template filename
			//to use while creating the new names
			mTemplateName = parsePatternTokens(parameters.remove("renamingPattern"));			
			//create the type exclusion list
			mTypeExclusions = setExclusions(parameters.remove("exclude"));
			//whether to force ascii subset in output
			mFilesystemSafe = Boolean.parseBoolean(parameters.remove("filesystemSafe"));
			//max filename length
			mMaxFilenameLength = Integer.parseInt(parameters.remove("maxFilenameLength"));
			//set/create output dir
			mOutputDir = (Directory)FileUtils.createDirectory(new Directory(FilenameOrFileURI.toFile(parameters.remove("output"))));
			//if input and output dir are the same, skip and return true
			if(mOutputDir.getCanonicalPath().equals(mInputFileset.getManifestMember().getFile().getParentFile().getCanonicalPath())) {
				String message = i18n("IN_OUT_SAME_SKIPPING", mOutputDir.getCanonicalPath());
				this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT);
				throw new TransformerRunException(message);
			}
			
			this.sendMessage(0.1);
			this.checkAbort();			
			String message = i18n("ANALYZING_INPUT", mInputFileset.getFilesetType().toNiceNameString());
			this.sendMessage(message, MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
								
			try{		
				//create a renaming strategy using the template name
				mStrategy = createStrategy(mInputFileset, mTemplateName, mTypeExclusions,mMaxFilenameLength);
			}catch (FilesetRenamingException e) {
				//do a roundtrip
				//TODO analyze the exception to find out if a roundtrip helps at all
				//TODO means improving the validate method
				
				//create a new strategy with heavy random
				SegmentedFileName randomizedName = new SegmentedFileName();
				randomizedName.addSegment(FixedSegment.create("temp"));
				randomizedName.addSegment(RandomUniqueSegment.create(mInputFileset, 6));				
				mStrategy = createStrategy(mInputFileset, randomizedName, mTypeExclusions, 96);				
				//render the randomized fileset to a subfolder of user output folder
				mRoundtripOutputDir = (Directory)FileUtils.createDirectory(new Directory(new File(mOutputDir,"dmfc_temp")));
								
				message = i18n("RENDER_ROUNDTRIP", e.getMessage(), mRoundtripOutputDir.getAbsolutePath());
				this.sendMessage(message, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);

				
				renderStrategy(mInputFileset,mRoundtripOutputDir);
				
				this.sendMessage(0.15);
				this.checkAbort();

				//reset the inputfileset to the randomized output
				mInputFileset = new FilesetImpl(getTempManifest(),this,false,false); 
				//redo strategy with original template, using the randomized output				
				mStrategy = createStrategy(mInputFileset, mTemplateName, mTypeExclusions, mMaxFilenameLength);
			}
			

			this.sendMessage(0.2);
			this.checkAbort();
			
			//render the final output			
			message = i18n("RENDERING_RESULT_TO", mOutputDir.getCanonicalPath());
			this.sendMessage(message, MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
			renderStrategy(mInputFileset,mOutputDir);
												
			//clean up the temp traces if utilized
			if(mRoundtripOutputDir!=null) {
				mRoundtripOutputDir.deleteContents(true);
				mRoundtripOutputDir.delete();
			}
					
			this.checkAbort();
			
		} catch (Exception e) {						
			String message = i18n("RENDERING_RESULT_TO", i18n("ERROR_COPYING_UNRENAMED", e.getMessage()));
			this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);

			try {		
				fman.getOutputFolder().addFileset(fman.getInputFileset(),true);
			} catch (IOException ioe) {				
				message = i18n("ERROR_ABORTING", ioe.getMessage());
				this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
				throw new TransformerRunException(ioe.getMessage(), ioe);
			}
		}	

		return true;
	}

	/**
	 * @throws FilesetRenamingException if resulting strategy is not valid or something else went wrong.
	 */
	private RenamingStrategy createStrategy(Fileset fileset, SegmentedFileName templateName, List<Class<?>> typeExclusions, int maxFilenameLength) throws FilesetRenamingException {
		RenamingStrategy rs = new DefaultStrategy(fileset,templateName,mFilesystemSafe);
		rs.setTypeExclusion(typeExclusions);
		rs.setMaxFilenameLength(maxFilenameLength);
		rs.create();
		rs.validate();
		return rs;
	}

	/**
	 * Render a fileset to disk. Register self as listener, 
	 * and use the callbacks to intervene and apply the rename strategy to the output.
	 */
	private void renderStrategy(Fileset fileset, Directory outputDir) throws FilesetManipulationException, IOException {		
		//get a FilesetManipulator instance
		mFilesetManipulator = new FilesetManipulator();
		//implement FilesetManipulatorListener
		mFilesetManipulator.setListener(this);
		//set input fileset
		mFilesetManipulator.setInputFileset(fileset);
		//set destination
		mFilesetManipulator.setOutputFolder(outputDir); 
		//roll through the fileset			
		mFilesetManipulator.iterate();
		//done.
	}
	
	/**
	 * Create a List&lt;Class&gt; of excluded file types using an inparam.
	 */
	@SuppressWarnings("unchecked")
	private List<Class<?>> setExclusions(String param) {
		mTypeExclusions = new ArrayList();
		String[] types = param.split(",");
		for (int i = 0; i < types.length; i++) {
			String interfaceName = types[i].trim();
			//create the class instance
			if(!interfaceName.toLowerCase().equals("none")){
				String implName = "org.daisy.util.fileset.impl." + interfaceName + "Impl";					
				try {
					Class implClass = Class.forName(implName);
					mTypeExclusions.add(implClass);
				} catch (ClassNotFoundException e) {						
					this.sendMessage(i18n("EXCLUDE_CLASS_NOT_FOUND", interfaceName), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
				}			
			}
		}
		return mTypeExclusions;		
	}

	private URI getTempManifest() throws TransformerRunException {
		String newName = mStrategy.getNewLocalName(mInputFileset.getManifestMember().getFile().toURI());
		File newManifest = new File(mRoundtripOutputDir, newName);
		if(newManifest!=null && newManifest.exists()){
			return newManifest.toURI();
		}
		throw new TransformerRunException("temporary manifest could not be found");
	}



	/**
	 * Parse a string consisting of plus-separated tokens and create a template SegmentedFileName.
	 * <p>In the template, give each segment the value of the input token, so that later users can
	 * extract possible additional info from the tokens.</p>
	 * <p>Note - template does not include the extension segment.</p>
	 */
	private SegmentedFileName parsePatternTokens(String tokenstring) throws TransformerRunException {		
		SegmentedFileName smf = new SegmentedFileName();
		String[] tokens = tokenstring.split("\\+");
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].trim();
			if(token.equals("uid")) {
				smf.addSegment(FilesetUIDSegment.create(token));				
			}else if(token.startsWith("rnd")){
				smf.addSegment(RandomUniqueSegment.create(token));			
			}else if(token.startsWith("fixed")){
				smf.addSegment(FixedSegment.create(token));			
			}else if(token.equals("label")){
				smf.addSegment(LabelSegment.create(token));			
			}else if(token.equals("seq")){
				smf.addSegment(SequenceSegment.create(token));
			}else if(token.equals("echo")){
				smf.addSegment(EchoSegment.create(token));				
			}
			else {
				//an unrecognized segment
				throw new TransformerRunException("unrecognized segment: " + token);
			}
		}
		return smf;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.manipulation.FilesetManipulatorListener#nextFile(org.daisy.util.fileset.interfaces.FilesetFile)
	 */
	private int nextFileCallCount = 0;
	private double filesetSize = -1.0; 
	
	public FilesetFileManipulator nextFile(FilesetFile file) throws FilesetManipulationException {
		if(filesetSize == -1.0) filesetSize = Double.parseDouble(Integer.toString(mInputFileset.getLocalMembers().size())+".0");
		nextFileCallCount++;				
		this.sendMessage(0.2 + ((nextFileCallCount/filesetSize)*0.8)); //assumes that progress 0.2 was called before first nextFile call 
		
		mCurrentFile = file; //for checking filetype in nextValue() below
		try{
			if (file instanceof Referring) {
				//this file may have a new name
				//and may refer to other members that may have new names
				if(file instanceof XmlFile) {
					//use the constructor of xmleventfeeder that allows localname change				
					XMLEventValueExposer xeve = new XMLEventValueExposer(this,mStrategy.getNewLocalName(file));					
					//default is to only replace in attributes (they typically carry URIs)
					xeve.setEventTypeRestriction(XMLEvent.ATTRIBUTE);				
					return xeve;
				}
				//FIXME if not xmlfile but still referring (css,html)
			}
			//else, this file does not refer to other members
			//but may still have a new name
			return new RenamingCopier(mStrategy.getNewLocalName(file));			
		}catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}
	}


	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.manipulation.manipulators.XMLEventValueConsumer#nextValue(java.lang.String, org.daisy.util.xml.stax.ContextStack)
	 */
	public String nextValue(String value, ContextStack context) {
		//by default we get attribute values only here
		
		if(isUriCarrier(context, mCurrentFile)) {
			int start = -1;
			StringBuilder sb = new StringBuilder();
			//replace oldNames with newNames and return
			sb.append(value);		
			Iterator<URI> it = mStrategy.getIterator();
			while(it.hasNext()) {
				try{
					URI oldNameURI = it.next();			
					//oldName = (new File(oldNameURI)).getName(); 
					//problem: above doesnt take escape sequences into account, gotta keep them, so:
					oldName = URIStringParser.stripPath(oldNameURI.toString());
					start = sb.indexOf(oldName);
					if(start > -1){
						//this value carries the old name
						sb.replace(start,start+oldName.length(),mStrategy.getNewLocalName(oldNameURI));
						break; //REVISIT are we sure first found is enough? values may contain several references...
					}	
				}catch (Exception e) {					
					this.sendMessage("exception when replacing values with new name: " + e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
					return value;
				}
			}				
			return sb.toString();
		}
		return null; //if !isUriCarrier		
	}
	
	/**
	 * Performance enhancing; dont loop through name map if current node is recognized to not be a URI carrier
	 * If we dont recognize the filetype or context, return true. 
	 */
	private boolean isUriCarrier(ContextStack context, FilesetFile currentFile) {
				
		if(context.getLastEvent().getXMLEventType() == XMLEvent.ATTRIBUTE){
			String attrName = context.getLastEvent().getName().getLocalPart();
			if(currentFile instanceof SmilFile && !rgx.matches(rgx.SMIL_ATTRIBUTES_WITH_URIS,attrName)) {
				return false;
			}
			else if(currentFile instanceof Z3986DtbookFile && !rgx.matches(rgx.DTBOOK_ATTRIBUTES_WITH_URIS,attrName)) {
				return false;
			}
			else if(currentFile instanceof Xhtml10File && !rgx.matches(rgx.XHTML_ATTRS_WITH_URIS,attrName)) {
				return false;
			}
			//else its a value we are not sure about
		}
		//else its not an attribute; unexpected to be enabled for checking but not this methods role to
		//have an opinion on that
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */	
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}
}
