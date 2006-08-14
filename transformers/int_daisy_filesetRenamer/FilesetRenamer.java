package int_daisy_filesetRenamer;

import int_daisy_filesetRenamer.strategies.TokenStrategy;
import int_daisy_filesetRenamer.strategies.RenamingStrategy;
import int_daisy_filesetRenamer.strategies.ScramblingStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.events.XMLEvent;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.fileset.interfaces.xml.Xhtml10File;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.fileset.manipulation.FilesetManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulatorListener;
import org.daisy.util.fileset.manipulation.manipulators.RenamingCopier;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueConsumer;
import org.daisy.util.fileset.manipulation.manipulators.XMLEventValueExposer;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.xml.stax.ContextStack;

/**
 * Renames select members of a fileset using namedroppable algorithms.
 * @author Markus Gylling
 */
public class FilesetRenamer extends Transformer implements FilesetManipulatorListener, XMLEventValueConsumer {

	RenamingStrategy strategy = null;
	FilesetFile currentFile = null;
	private StringBuilder sb = null;
	private String oldName = null;
	private int start = -1;
	private FilesetRegex rgx = null;
		
	public FilesetRenamer(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
		sb = new StringBuilder();
		rgx = FilesetRegex.getInstance();
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		FilesetManipulator fm = null;
		try {
			//get a FilesetManipulator instance
			fm = new FilesetManipulator();
			//implement FilesetManipulatorListener
			fm.setListener(this);
			//set input fileset
			fm.setInputFileset(new File((String) parameters.remove("manifest")).toURI());
			//set destination
			fm.setOutputFolder((EFolder) FileUtils.createDirectory(new EFolder((String) parameters.remove("outDir"))));
			fm.getOutputFolder().deleteContents(true); //TODO remove
			//determine which renaming strategy to use		
			strategy = setStrategy(fm, parameters);
			//roll through the fileset			
			fm.iterate();
			//done.
		} catch (Exception e) {
			this.sendMessage(Level.SEVERE, e.getMessage());
			//try a clean copy here instead of throwing
//			TODO reenable			
//			try {				
//				//fm.getOutputFolder().addFileset(fm.getInputFileset(),true);
//			} catch (IOException ioe) {
//				throw new TransformerRunException(ioe.getMessage(), ioe);
//			}			
		}

		return true;
	}

	/**
	 * @return an implementation of NamingStrategy with all needed properties set, and validated.
	 */
	private RenamingStrategy setStrategy(FilesetManipulator fm, Map parameters) throws Exception {		
		
		String scheme = ((String)parameters.remove("renamingType")).intern();		
		RenamingStrategy rs = null;
		
		try{		
			//determine what scheme to use
			if(scheme==("scramble")||scheme==("unique")) {				
				rs = new ScramblingStrategy(fm.getInputFileset());											
				if(scheme==("unique")){
					rs.setDefaultPrefix(ScramblingStrategy.scramble(8));
				}			
			}else {
				//we assume its a token strategy
				rs = new TokenStrategy(fm.getInputFileset(),scheme);
			}			
			
			//set filetype exclusions per users request
				//TODO
			
			//set constant exclusions
			rs.setTypeExclusion(D202NccFile.class); 
			
			//set prefix
			String pfx = (String)parameters.remove("prefix");
			if(pfx!=null) rs.setDefaultPrefix(pfx);
			
			//move along
			rs.createStrategy();
			rs.validate();
			
		}catch (Exception e) {
			this.sendMessage(Level.SEVERE, e.getMessage());
			this.sendMessage(Level.INFO, "skipping rename");			
		}
		//if we are here, we have a working strategy	
		return rs;
	}


	/**
	 * FilesetManipulatorListener impl
	 * @throws FilesetManipulationException
	 */
	public FilesetFileManipulator nextFile(FilesetFile file) throws FilesetManipulationException {
		currentFile = file; //for checking filetype in nextValue() below
		try{
			if (file instanceof Referring) {
				//this file may have a new name
				//and may refer to other members that may have new names
				if(file instanceof XmlFile) {
					//use the constructor of xmleventfeeder that allows localname change				
					XMLEventValueExposer xeve = new XMLEventValueExposer(this,flatten(strategy.getNewLocalName(file)));					
					//default is to only replace in attributes (they typically carry URIs)
					xeve.setEventTypeRestriction(XMLEvent.ATTRIBUTE);				
					return xeve;
				}
				//FIXME if not xmlfile but still referring (css,html)
			}
			//else, this file cannot refer to other members
			//but may have a new name
			return new RenamingCopier(flatten(strategy.getNewLocalName(file)));												
			
		}catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}
		
	}

		
	/**
	 * Tries to assure that the name contains only ascii characters [A-Za-z0-9_-]
	 */
	private String flatten(String newLocalName) {
		//TODO implement
		//TODO make defeatable via inparam
		return newLocalName;
	}

	/**
	 * XMLEventValueConsumer impl
	 */
	public String nextValue(String value, ContextStack context) {
		//by default we get attribute values only here
		
		if(isUriCarrier(context, currentFile)) {
			//replace oldNames with newNames and return
			sb.delete(0,sb.length());
			sb.append(value);		
			Iterator it = strategy.getIterator();
			while(it.hasNext()) {
				try{
					URI oldNameURI = (URI)it.next();			
					oldName = (new File(oldNameURI)).getName();
					start = sb.indexOf(oldName);
					if(start > -1){
						//this value carries the old name
						sb.replace(start,start+oldName.length(),strategy.getNewLocalName(oldNameURI));
						break; //REVISIT are we sure first found is enough? values may contain several references...
					}	
				}catch (Exception e) {
					this.sendMessage(Level.WARNING,"exception when replacing values with new name: " + e.getMessage());
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

	/**
	 * FilesetErrorHandler impl
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		if (ffe instanceof FilesetFileFatalErrorException
				&& !(ffe.getCause() instanceof FileNotFoundException)) {
			this.sendMessage(Level.SEVERE, ffe.getCause()
					+ " in " + ffe.getOrigin());
		} else {
			this.sendMessage(Level.WARNING, ffe.getCause()
					+ " in " + ffe.getOrigin());
		}
	}

}
