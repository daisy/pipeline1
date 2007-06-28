package int_daisy_prettyPrinter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.NotSupposedToHappenException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventExposer;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Main transformer class. Pretty print a fileset with special care taking for DTB player compatibility.
 * @author Markus Gylling
 */
public class PrettyPrinter extends Transformer implements FilesetErrorHandler, FilesetManipulatorListener, XMLEventConsumer {

	private EFile mInputManifest = null;
	private Fileset mInputFileset = null;
	private FilesetManipulator mFilesetManipulator = null;
	private EFolder mOutputDir;
	private XMLEventFactory mEventFactory = null;
	private FilesetFile mCurrentInputFile = null;
	private static Characters mLineBreak = null;
	private static Characters mIndent = null;
	private static String mTempFileRegex = null;

	public PrettyPrinter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map parameters) throws TransformerRunException {
		try {  				
			
			mEventFactory = StAXEventFactoryPool.getInstance().acquire();
			mLineBreak = mEventFactory.createSpace("\r\n");
			mIndent = mEventFactory.createSpace("\u0009"); 
			mTempFileRegex = ".+\\.prettyPrinted$";
			
			// set the input manifest
			mInputManifest  = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));
			// set input fileset
			mInputFileset = new FilesetImpl(mInputManifest.toURI(),this,false,false);
			//output dir						
			mOutputDir = (EFolder)FileUtils.createDirectory(new EFolder(FilenameOrFileURI.toFile((String)parameters.remove("output")))); 
			//linebreak style
			String lb = (String)parameters.remove("linebreak");
			if(lb.equals("UNIX")) mLineBreak = mEventFactory.createSpace("\n");									
			this.sendMessage(0.10);
			//get a FilesetManipulator instance
			mFilesetManipulator = new FilesetManipulator();
			//implement FilesetManipulatorListener
			mFilesetManipulator.setListener(this);
			//listen only to xml files
			mFilesetManipulator.setFileTypeRestriction(XmlFile.class);
			//set input fileset
			mFilesetManipulator.setInputFileset(mInputFileset);
			//set destination
			mFilesetManipulator.setOutputFolder(mOutputDir); 
			//roll through the fileset			
			mFilesetManipulator.iterate();
			//done pretty printing to temporary files
			realize();
			this.sendMessage(1.0);
		} catch (Exception e) {													
			String message = i18n("ERROR_ABORTING", e.getMessage());
			try {
				//clean out only the temp files
				mFilesetManipulator.getOutputFolder().deleteContents(true, mTempFileRegex);
			} catch (Exception e1) {
			} 
			
			this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
			throw new TransformerRunException(e.getMessage(), e);
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
	 * Settle filenames to final state. All files that have been prettyPrinted exist as '*.*.prettyPrinted' and may be in the same dir
	 * as the originals.
	 */
	private void realize() throws IOException {
		
		Collection<File> coll = mFilesetManipulator.getOutputFolder().getFiles(true, mTempFileRegex);

		for (File file : coll) {						
			File original = new File(file.getParentFile(), file.getName().replace(".prettyPrinted", ""));
			
			File doubleTemp = null;
			
			if(original.exists()) {
				doubleTemp = new File(original.getParentFile(), original.getName()+".doubleTemp");				
				if(!original.renameTo(doubleTemp)){
					String message=i18n("IO_LOCK", original.getName());
					sendMessage(message, MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);				
					throw new IOException(message);
				}
			}
			
			if(!file.renameTo(original)) {
				String message=i18n("IO_LOCK", file.getName());
				sendMessage(message, MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);				
				throw new IOException(message);				
			}
			
			if(doubleTemp!=null) {
				if(!doubleTemp.delete()) {
					String message=i18n("IO_LOCK", doubleTemp.getName());
					sendMessage(message, MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM);				
					throw new IOException(message);		
				}
			}				
			
		}
		
	}

	private int mPrettyPrintedFileCount = 0;
	private double filesetSize = -1.0; 
	
	public FilesetFileManipulator nextFile(FilesetFile inFile) throws FilesetManipulationException {
		if(filesetSize == -1.0) filesetSize = Double.parseDouble(Integer.toString(mInputFileset.getLocalMembers().size())+".0");
		mPrettyPrintedFileCount++;
		try {
			mCurrentInputFile  = inFile;			
			this.sendMessage(0.1 + ((mPrettyPrintedFileCount/mInputFileset.getLocalMembers().size())*0.9)); 
			XMLEventExposer xee = new XMLEventExposer(this, null, inFile.getName()+".prettyPrinted", true, false, false);
			xee.setEventTypeRestrictions(getEventTypeRestrictions());			
			return xee;
		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/*
	 * If we return null here, nothing will be written
	 */
	
	private List<XMLEvent> retEvents = new LinkedList<XMLEvent>();
	private int previousEvent = XMLEvent.START_DOCUMENT;
	private QName previousStartOrEndElementQName = null;
	private int depth = 0;
	
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
			
			Iterator iterator = null;			
			if(needsAttributeOrdering(se)){
				iterator = orderAttributes(se);
			}else{
				iterator = se.getAttributes();
			}
			
			while(iterator.hasNext()) {
				Attribute attr = (Attribute) iterator.next();
				retEvents.add(attr);
			}
						
			previousEvent = XMLEvent.START_ELEMENT;
			previousStartOrEndElementQName = se.getName();
			depth++;
			
			
		}else if (xe.getEventType() == XMLEvent.END_ELEMENT) {
			
			--depth;
			//if(previousEvent!=XMLEvent.CHARACTERS && previousEvent!=XMLEvent.ATTRIBUTE) {
			if(previousEvent!=XMLEvent.CHARACTERS && previousEvent!=XMLEvent.ATTRIBUTE && previousEvent!=XMLEvent.START_ELEMENT) {
				if 	((!(mCurrentInputFile instanceof D202NccFile)) || (!previousStartOrEndElementQName.getLocalPart().equals("a"))) {
					if(previousEvent==XMLEvent.SPACE||previousEvent==XMLEvent.END_ELEMENT) {				
						retEvents.add(mLineBreak);
					}
					retEvents = addIndent(retEvents, depth);
				}
			}

			retEvents.add(xe);			
			if(previousEvent==XMLEvent.START_ELEMENT) {	
				retEvents.add(mLineBreak);
			}
			previousEvent = XMLEvent.END_ELEMENT;
			previousStartOrEndElementQName = xe.asEndElement().getName();
			
			
		}else if (xe.getEventType() == XMLEvent.ATTRIBUTE) {
			//we write them in start element, just register them here.
			previousEvent = XMLEvent.ATTRIBUTE;			
		}else{
			throw new NotSupposedToHappenException("PrettyPrinter.nextEvent else");
		}
		
		return retEvents;
	}
	
	private Iterator orderAttributes(StartElement se) {
		List<Attribute> returnList = new LinkedList<Attribute>();
		Iterator inputIterator = se.getAttributes();
		
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
					contentAttr = a;
				}else{
					otherAttrs.add(a);
				}
			}	

			//write them out in order
			if(nameAttr!=null) returnList.add(nameAttr);
			if(httpEquivAttr!=null) returnList.add(httpEquivAttr);
			if(contentAttr!=null) returnList.add(contentAttr);
			for (Iterator iterator = otherAttrs.iterator(); iterator.hasNext();) {
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
			for (Iterator iterator = otherAttrs.iterator(); iterator.hasNext();) {
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

	private static Set<Integer> mEventTypeRestrictions = null;

	private Set<Integer> getEventTypeRestrictions() {
		if(mEventTypeRestrictions == null){
			mEventTypeRestrictions = new HashSet<Integer>();
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.START_ELEMENT));
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.END_ELEMENT));
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.ATTRIBUTE));
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.DTD));
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.CHARACTERS));
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.SPACE));
			mEventTypeRestrictions.add(Integer.valueOf(XMLEvent.PROCESSING_INSTRUCTION));
		}	
				
		return mEventTypeRestrictions;
	}

}
