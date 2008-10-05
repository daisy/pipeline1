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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.CssFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Referring;
import org.daisy.util.fileset.SmilFile;
import org.daisy.util.fileset.Xhtml10File;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.Z3986DtbookFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.DoctypeParser;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Rename select members of a fileset using a pattern based token algorithm. See Pipeline doc/transformers.
 * @author Markus Gylling
 */

public class FilesetRenamer extends Transformer implements FilesetErrorHandler {
   
	//private FilesetManipulator mFilesetManipulator = null;
	private EFile mInputManifest = null;
	private Fileset mInputFileset = null;
	private Directory mOutputDir = null;				//final output destination
//	private Directory mRoundtripOutputDir = null; 		//for temporary storage, not always used
	private SegmentedFileName mTemplateName = null;
	private RenamingStrategy mStrategy = null;
	private List<Class<?>> mTypeExclusions = null;
	private boolean mFilesystemSafe = true;
	private FilesetFile mCurrentFile = null;	
	private FilesetRegex rgx = null;
	private int mMaxFilenameLength = -1;
	private int nextFileCallCount = 0;
	private double filesetSize = -1.0; 

	
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
		
		//FilesetManipulator fman = null;
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
								
			//create a renaming strategy using the template name
			mStrategy = createStrategy(mInputFileset, mTemplateName, mTypeExclusions,mMaxFilenameLength);

			this.sendMessage(0.2);
			this.checkAbort();
			
			//render the final output			
			message = i18n("RENDERING_RESULT_TO", mOutputDir.getCanonicalPath());
			this.sendMessage(message, MessageEvent.Type.INFO_FINER, MessageEvent.Cause.SYSTEM);
			renderStrategy(mInputFileset,mOutputDir);
																	
			this.checkAbort();
			
		} catch (Exception e) {						
			String message = i18n("RENDERING_RESULT_TO", i18n("ERROR_COPYING_UNRENAMED", e.getMessage()));
			this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);

			try {		
				mOutputDir.addFileset(mInputFileset,true);
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
		RenamingStrategy rs = new DefaultStrategy(fileset,templateName,mFilesystemSafe, mOutputDir);
		rs.setTypeExclusion(typeExclusions);
		rs.setMaxFilenameLength(maxFilenameLength);
		rs.create();
		
		boolean valid = true;
		int count = 0;
		do {
			count++;
			valid = rs.validate();
		} while(!valid && count<1000);
		
		return rs;
	}

	/**
	 * Render a fileset to disk. Register self as listener, 
	 * and use the callbacks to intervene and apply the rename strategy to the output.
	 * @throws TransformerRunException 
	 */
	private void renderStrategy(Fileset fileset, Directory outputDir) throws IOException, TransformerRunException {		
				
		for(FilesetFile ffile : fileset.getLocalMembers()) {
			//progress
			if(filesetSize == -1.0) filesetSize = Double.parseDouble(Integer.toString(mInputFileset.getLocalMembers().size())+".0");
			nextFileCallCount++;				
			this.sendMessage(0.2 + ((nextFileCallCount/filesetSize)*0.8)); //assumes that progress 0.2 was called before first nextFile call 
			
			mCurrentFile = ffile;
			
			//get to the destination			
			URI destination = mStrategy.getNewURI(ffile.getFile().toURI());
			if(ffile instanceof Referring) {
				copyReferring(ffile, destination);
			}else{
				FileUtils.copyFile(ffile.getFile(), new File(destination));
			}	
		}
							
	}
	
	
	/**
	 * Copy a referrer to destination, while replacing its internal references according to the renaming strategy.
	 * @throws TransformerRunException 
	 */
	private void copyReferring(FilesetFile source, URI destination) throws TransformerRunException {
		
		if(source instanceof XmlFile) {
			copyXml(source, destination);
		} else if (source instanceof CssFile) {
			//TODO
			try {
				FileUtils.copyFile(source.getFile(), new File(destination));
			} catch (IOException e) {
				
			}
		}		
	}


	private void copyXml(FilesetFile source, URI destination) throws TransformerRunException {
        Map<String, Object> xifProp = null;
        XMLInputFactory factory = null;
        FileInputStream fis = null;
        try {
            xifProp = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
            factory = StAXInputFactoryPool.getInstance().acquire(xifProp);
            factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
            fis = new FileInputStream(source.getFile());
            XMLEventReader reader = factory.createXMLEventReader(fis);
            OutputStream outstream = new FileOutputStream(new File(destination));
            StaxFilter filter = new UriModderFilter(reader, outstream);
            filter.filter();
            filter.close();
        } catch (Exception e) {
            //this.sendMessage(); //TODO
            try {
                FileUtils.copy(source.getFile(), new File(destination));
            } catch (IOException ioe) {
                throw new TransformerRunException(ioe.getMessage(), ioe);
            }
        }finally{
            if(fis!=null) try {fis.close();} catch (IOException e) {}
            StAXInputFactoryPool.getInstance().release(factory, xifProp);
        }
		
	}

    /**
     * Inner class that modifies any URI attributes to carry new names 
     */
    private class UriModderFilter extends StaxFilter {
    	
        public UriModderFilter(XMLEventReader xer, OutputStream outStream) throws XMLStreamException {
            super(xer, outStream);            
        }

        @Override
        protected StartElement startElement(StartElement event) {
            boolean modded = false;
            Set<Attribute> attributes = new HashSet<Attribute>();
            
            Iterator<?> iter = event.getAttributes();
            while(iter.hasNext()) {
            	Attribute a = (Attribute) iter.next();
            	if(isURICarrier(a)) {
            		modded = true;
            		attributes.add(mod(a));
            	}else{
            		attributes.add(a);
            	}
            }
                        
            if (modded) {
                return getEventFactory().createStartElement(event.getName(), attributes.iterator(), event.getNamespaces());
            }
            return event;
        }

		private Attribute mod(Attribute a) {
						
			try {
				URI oldUri = new URI(a.getValue());
				
				// create the old uri using current state
				URI current = mCurrentFile.getFile().toURI().resolve(oldUri);
				// get the query and fragment
				String fragAndQuery="";
				if (oldUri.getRawFragment()!=null){
					fragAndQuery+='#'+oldUri.getRawFragment();
				}
				if (oldUri.getRawQuery()!=null){
					fragAndQuery+='?'+oldUri.getRawQuery();
				}

				String newValue=a.getValue();
				Iterator<URI> it = mStrategy.getIterator();
				boolean stop = false;
				while (it.hasNext() && !stop) {
					// get the absolute URI of the old name
					URI key = it.next();
					if (key.getPath().equals(current.getPath())) {
						stop=true;
						// the attribute refs oldURI
						// get the new destination
						URI newDestination = mStrategy.getNewURI(key);
						// get the member that will refer to the destination
						URI newReferer = mStrategy.getNewURI(mCurrentFile.getFile().toURI());
						URI newRefererParent = new File(newReferer).getParentFile().toURI();
						// create the new relative URI
						URI newReference = newRefererParent.relativize(newDestination);
						// get the new manifest name
						String newManifestName = mStrategy.getNewLocalName(mInputFileset.getManifestMember());
						if (newReference.toString().equals(newManifestName)){
							// internal link
							newValue="";
						} else{
							newValue = newReference.toASCIIString();
						}
						// add query and fragment
						newValue+=fragAndQuery;
					}

				}
				return getEventFactory().createAttribute(a.getName(), newValue);
			} catch (Exception e) {
				sendMessage("exception when replacing values with new name: "
						+ e.getMessage(), MessageEvent.Type.WARNING,
						MessageEvent.Cause.SYSTEM);
				return a;
			}

		}

		private boolean isURICarrier(Attribute a) {
			if(pattern==null) {				
				pattern = Pattern.compile("(^src$)|(^href$)|(^altimg$)|(^smilref$)");
			}
			return pattern.matcher(a.getName().getLocalPart()).matches();			 
		}
		
		private Pattern pattern;
    
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
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */	
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);
	}
}
