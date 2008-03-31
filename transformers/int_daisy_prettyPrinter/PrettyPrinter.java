package int_daisy_prettyPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.Xhtml10File;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Main transformer class. Pretty print a fileset with special care taken for DTB player compatibility.
 * @author Markus Gylling
 */
public class PrettyPrinter extends Transformer implements FilesetErrorHandler {

	private EFile mInputManifest = null;
	private Fileset mInputFileset = null;	
	private EFolder mOutputDir;
	private EFolder mInputDir;
	private XMLEventFactory mEventFactory = null;
	private FilesetFile mCurrentInputFile = null;
	private static Characters mLineBreak = null;
	private static Characters mIndent = null;
	private static String mTempFileRegex = null;
	private boolean mFilterAnnoyingDefaultAttributes = true; //filter defaults in xhtml1 and smil1	
	private List<XMLEvent> retEvents = new LinkedList<XMLEvent>();
	private int previousEvent = XMLEvent.START_DOCUMENT;
	private QName previousStartOrEndElementQName = null;
	private int depth = 0;	
	private Pattern annoyingXHTML10AttributeMatcher = Pattern.compile("shape");
	private Pattern annoyingSMIL10AttributeMatcher = Pattern.compile("repeat|fill|skip-content|type|left|top|z-index|fit");
	
	
	public PrettyPrinter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		try {  				
			
			mEventFactory = StAXEventFactoryPool.getInstance().acquire();
			mLineBreak = mEventFactory.createSpace("\r\n");
			mIndent = mEventFactory.createSpace("\u0009"); 
			mTempFileRegex = ".+\\.prettyPrinted$";
			
			/*
			 * Set the input manifest
			 */
			mInputManifest  = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));
			
			/* 
			 * Set input fileset
			 */
			mInputFileset = new FilesetImpl(mInputManifest.toURI(),this,false,false);

			/*
			 * Set input dir
			 */						
			mInputDir = new EFolder(mInputFileset.getManifestMember().getFile().getParentFile());

			
			/*
			 * Set output dir
			 */						
			mOutputDir = (EFolder)FileUtils.createDirectory(new EFolder(FilenameOrFileURI.toFile((String)parameters.remove("output"))));
			
			/*
			 * Set linebreak style
			 */
			String lb = (String)parameters.remove("linebreak");
			if(lb.equals("UNIX")) mLineBreak = mEventFactory.createSpace("\n");
			
			this.sendMessage(0.10);
			
			/*
			 * Pretty print XML members to temporary files,
			 * copy non-XML untouched.
			 * Note: input and output directory may be the same,
			 * in which case copy is cancelled.
			 */
			int count = 0;
			double filesetSize = Double.parseDouble(Integer.toString(mInputFileset.getLocalMembers().size())+".0");
			
			Set<File> tempFiles = new HashSet<File>();
			
			for (Iterator iterator = mInputFileset.getLocalMembers().iterator(); iterator.hasNext();) {				
				checkAbort();
				FilesetFile ffile = (FilesetFile) iterator.next();
				File destination = getDestination(ffile);
				if(ffile instanceof XmlFile) {
					tempFiles.add(prettyPrint((XmlFile)ffile,  new File(destination.getAbsolutePath()+".prettyPrinted")));
				}else{
					copy(ffile,destination);
				}
				count++;
				this.sendMessage(0.1 + ((count/filesetSize)*0.9));
			}
						
			/*
			 * Set changed files to final names
			 */			
			realize(tempFiles);
			
			this.sendMessage(1.0);
			
		} catch (Exception e) {																
			try {
				//clean out the temp files
				mOutputDir.deleteContents(true, mTempFileRegex);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}finally{
			try {
				StAXEventFactoryPool.getInstance().release(mEventFactory);
			} catch (PoolException e) {			
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * @return the pretty printed result
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	private File prettyPrint(XmlFile input, File destination) throws XMLStreamException, IOException {
		
		XMLEventFactory xef = null;
		XMLInputFactory xif = null;
		Map<String, Object> xifProperties = null;
		XMLOutputFactory xof = null;
		Map<String, Object> xofProperties = null;
		InputStream is = null;
		FileOutputStream fos = null;
		XMLEventWriter xew = null;
		
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			xifProperties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(xifProperties);
			xofProperties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();			
			xof = StAXOutputFactoryPool.getInstance().acquire(xofProperties);
			is = input.asInputStream();			
			XMLEventReader xer = xif.createXMLEventReader(is);
			fos = new FileOutputStream(destination);				
			ContextStack stack = new ContextStack();			
			while(xer.hasNext()) {
				XMLEvent event = xer.nextEvent();
				if(event.getEventType() == XMLEvent.START_DOCUMENT) {
					StartDocument sd = (StartDocument) event;
					String enc = sd.getCharacterEncodingScheme();
					if(enc==null||enc.equals(""))enc="utf-8";
					xew = xof.createXMLEventWriter(fos,enc);					
				}				
				stack.addEvent(event);				
				List<XMLEvent> list = nextEvent(event,stack);
				for(XMLEvent x : list) {					
					xew.add(x);
				}
			}			
		}finally{
			xew.flush();
			xew.close();
			if(is!=null)is.close();
			if(fos!=null)fos.close();
			StAXEventFactoryPool.getInstance().release(xef);	
			StAXInputFactoryPool.getInstance().release(xif,xifProperties);
			StAXOutputFactoryPool.getInstance().release(xof,xofProperties);			
		}
		return destination;
	}
	
	/**
	 * @return the copied result
	 * @throws IOException 
	 */
	private File copy(FilesetFile input, File destination) throws IOException {
		if(!(input.getFile().getCanonicalPath().equals(destination.getCanonicalPath()))) {
			FileUtils.copyFile((File)input, destination);
		}
		return destination;
	}
	
	/**
	 * the incoming file is a member of input Fileset
	 * determine where in outFolder it should be located
	 * return a file describing the location 
	 */
	private File getDestination(FilesetFile file) throws IOException {				
		if(mInputDir!=null) {
			if(file.getFile().getParentFile().getCanonicalPath().equals(
					mInputDir.getCanonicalPath())) {
				//file is in same dir as manifestfile
				return new File(mOutputDir, file.getName());
			}
			//file is in subdir
			URI relative = mInputDir.toURI().relativize(file.getFile().getParentFile().toURI());
			if(relative.toString().startsWith("..")) 
				throw new IOException("fileset member "+file.getName()+" " +
						"does not live in a sibling or descendant folder of manifest member");
			EFolder subdir = new EFolder(mOutputDir,relative.getPath());
			FileUtils.createDirectory(subdir);
			return new File(subdir, file.getName());			
		}
		throw new IOException("mInputDir is null");							
	}
	
	/**
	 * The main pretty printing loop. Get one event in, return null or one or many events.
	 * If we return null, nothing will be written to output
	 */		
	public List<XMLEvent> nextEvent(XMLEvent xe, ContextStack context) {
		retEvents.clear();
				
		if (xe.getEventType() == XMLEvent.DTD) {						
			DTD dtd = mEventFactory.createDTD(((DTD)xe).getDocumentTypeDeclaration().replace("[]", "").trim());
			retEvents.add(mLineBreak);
			retEvents.add(dtd);
			previousEvent = XMLEvent.DTD;
		}else if (xe.getEventType() == XMLEvent.PROCESSING_INSTRUCTION) {
			retEvents.add(mLineBreak);
			retEvents.add(xe);
			previousEvent = XMLEvent.PROCESSING_INSTRUCTION;			
		}else if (xe.getEventType() == XMLEvent.CHARACTERS) {
			Characters chars = (Characters) xe;			
			if(CharUtils.isXMLWhiteSpace(chars.getData())) { //chars.isIgnorableWhiteSpace()||
				previousEvent = XMLEvent.SPACE;				
			}else{
				previousEvent = XMLEvent.CHARACTERS;
				retEvents.add(xe);
			}	
			
		}else if (xe.getEventType() == XMLEvent.SPACE) {
			previousEvent = XMLEvent.SPACE;
			
		}else if (xe.getEventType() == XMLEvent.START_ELEMENT) {
			StartElement se = (StartElement) xe;
			//System.err.println(se.getName().getLocalPart());
			if(previousEvent!=XMLEvent.CHARACTERS) {
				if((!(mCurrentInputFile instanceof D202NccFile)) || (!se.getName().getLocalPart().equals("a"))) {
					retEvents.add(mLineBreak);							
					retEvents = addIndent(retEvents, depth);
				}
			}
			
			retEvents.add(mEventFactory.createStartElement(se.getName(),null,se.getNamespaces()));
			
			Iterator<?> iterator = null;			
			if(needsAttributeOrdering(se)){
				iterator = orderAttributes(se);
			}else{
				iterator = se.getAttributes();
			}
								
			while(iterator.hasNext()) {
				Attribute attr = (Attribute) iterator.next();		
				if(mFilterAnnoyingDefaultAttributes) {
					if(mCurrentInputFile instanceof Xhtml10File){
						if(annoyingXHTML10AttributeMatcher.matcher(attr.getName().getLocalPart()).matches()) {
							//System.err.println("prettyprinter skipping nonspecificed attribute " + attr.getName() + " in " + mCurrentInputFile.getName());
							continue;
						}
					}else if (mCurrentInputFile instanceof D202SmilFile){
						if(annoyingSMIL10AttributeMatcher.matcher(attr.getName().getLocalPart()).matches()) {
							//System.err.println("prettyprinter skipping nonspecificed attribute " + attr.getName() + " in " + mCurrentInputFile.getName());
							continue;
						}
					}
				}
				retEvents.add(attr);								
			}
						
			previousEvent = XMLEvent.START_ELEMENT;
			previousStartOrEndElementQName = se.getName();
			depth++;
			
			
		}else if (xe.getEventType() == XMLEvent.END_ELEMENT) {
			
			--depth;
			if(previousEvent!=XMLEvent.CHARACTERS && previousEvent!=XMLEvent.ATTRIBUTE && previousEvent!=XMLEvent.START_ELEMENT) {
				if ((!(mCurrentInputFile instanceof D202NccFile)) || (!previousStartOrEndElementQName.getLocalPart().equals("a"))) {
					if(previousEvent==XMLEvent.SPACE||previousEvent==XMLEvent.END_ELEMENT) {				
						retEvents.add(mLineBreak);
					}
					retEvents = addIndent(retEvents, depth);
				}
			}

			retEvents.add(xe);			
			if(previousEvent==XMLEvent.START_ELEMENT) {	
				//retEvents.add(mLineBreak);
			}
			previousEvent = XMLEvent.END_ELEMENT;
			previousStartOrEndElementQName = xe.asEndElement().getName();
			
			
		}else if (xe.getEventType() == XMLEvent.ATTRIBUTE) {
			//we write them in start element, just register them here.
			previousEvent = XMLEvent.ATTRIBUTE;			
		}else{
			retEvents.add(xe);
		}
		
		return retEvents;
	}
	
	private Iterator<?> orderAttributes(StartElement se) {
		List<Attribute> returnList = new LinkedList<Attribute>();
		Iterator<?> inputIterator = se.getAttributes();
		
		if(se.getName().getLocalPart().equals("meta")) {	
			Attribute nameAttr = null;
			Attribute contentAttr = null;
			Attribute httpEquivAttr = null;
			Set<Attribute> otherAttrs = new HashSet<Attribute>();

			//collect the attrs from source
			while(inputIterator.hasNext()) {
				Attribute a = (Attribute)inputIterator.next();
				String name = a.getName().getLocalPart();
				if(name.equals("name")) {
					nameAttr = a;
				}else if(name.equals("content")) {
					contentAttr = a;
				}else if(name.equals("http-equiv")) {
					httpEquivAttr = a;
				}else{
					otherAttrs.add(a);
				}
			}	

			//write them out in order
			if(nameAttr!=null) returnList.add(nameAttr);
			if(httpEquivAttr!=null) returnList.add(httpEquivAttr);
			if(contentAttr!=null) returnList.add(contentAttr);
			for (Iterator<?> iterator = otherAttrs.iterator(); iterator.hasNext();) {
				returnList.add((Attribute) iterator.next());				
			}
			
		}else if (se.getName().getLocalPart().equals("audio")) {
			Attribute srcAttr = null;
			Attribute beginAttr = null;
			Attribute endAttr = null;
			Set<Attribute> otherAttrs = new HashSet<Attribute>();
			//collect the attrs from source
			while(inputIterator.hasNext()) {
				Attribute a = (Attribute)inputIterator.next();
				String name = a.getName().getLocalPart();
				if(name.equals("src")) {
					srcAttr = a;
				}else if(name.equals("clip-begin")) {
					beginAttr = a;
				}else if(name.equals("clip-end")) {
					endAttr = a;
				}else{
					otherAttrs.add(a);
				}
			}	

			//write them out in order
			if(srcAttr!=null) returnList.add(srcAttr);
			if(beginAttr!=null) returnList.add(beginAttr);
			if(endAttr!=null) returnList.add(endAttr);
			for (Iterator<?> iterator = otherAttrs.iterator(); iterator.hasNext();) {
				returnList.add((Attribute) iterator.next());				
			}
			
		}
		
		return returnList.iterator();
	}

	private boolean needsAttributeOrdering(StartElement se) {
		if(mCurrentInputFile instanceof D202NccFile || mCurrentInputFile instanceof D202SmilFile) {
			String name = se.getName().getLocalPart();
			if(name.equals("meta")||name.equals("audio")) {
				return true;
			}
		}
		return false;
	}

	private List<XMLEvent> addIndent(List<XMLEvent> list, int depth) {
		for (int i = 0; i < depth; i++) {
			list.add(mIndent);
		}
		return list;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	
	public void error(FilesetFileException ffe) throws FilesetFileException {
		this.sendMessage(ffe);	
	}

	/**
	 * Settle filenames to final state. All files that have been prettyPrinted exist as '*.*.prettyPrinted' and may be in the same dir
	 * as the originals.
	 */
	private void realize(Collection<File> tempFiles) throws IOException {
		
		for (File file : tempFiles) {						
			File original = new File(file.getParentFile(), file.getName().replace(".prettyPrinted", ""));
			
			File doubleTemp = null;
			
			if(original.exists()) {
				doubleTemp = new File(original.getParentFile(), original.getName()+".doubleTemp");				
				if(!original.renameTo(doubleTemp)){
					String message=i18n("IOERROR", original.getName());				
					throw new IOException(message);
				}
			}
			
			if(!file.renameTo(original)) {
				String message=i18n("IOERROR", file.getName());				
				throw new IOException(message);				
			}
			
			if(doubleTemp!=null) {
				if(!doubleTemp.delete()) {
					String message=i18n("IOERROR", doubleTemp.getName());				
					throw new IOException(message);		
				}
			}				
		}
	}	
}
