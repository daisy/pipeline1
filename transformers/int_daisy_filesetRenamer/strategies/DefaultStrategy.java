package int_daisy_filesetRenamer.strategies;

import int_daisy_filesetRenamer.FilesetRenamingException;
import int_daisy_filesetRenamer.segment.EchoSegment;
import int_daisy_filesetRenamer.segment.ExtensionSegment;
import int_daisy_filesetRenamer.segment.FilesetUIDSegment;
import int_daisy_filesetRenamer.segment.FixedSegment;
import int_daisy_filesetRenamer.segment.LabelSegment;
import int_daisy_filesetRenamer.segment.RandomUniqueSegment;
import int_daisy_filesetRenamer.segment.Segment;
import int_daisy_filesetRenamer.segment.SegmentedFileName;
import int_daisy_filesetRenamer.segment.SequenceSegment;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202MasterSmilFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.i18n.CharUtils;

/**
 * A base renaming strategy
 * @author Markus Gylling
 */
public class DefaultStrategy implements RenamingStrategy {
	private Fileset mInputFileset = null;
	private SegmentedFileName mTemplateName = null;
	private HashMap namingStrategy = new HashMap(); 	// <URI>,<URI>
	private List typeExclusions = new ArrayList();		// Interface names
	private String mSegmentSeparator = "_";				//default, may wanna enable changing this
	private boolean mForceAsciiSubset = false;
	private boolean isValidated = false;
	
	public DefaultStrategy(Fileset fileset, SegmentedFileName templateName, boolean forceAsciiSubset) {
		mInputFileset = fileset;
		mTemplateName = templateName;
		mForceAsciiSubset = forceAsciiSubset;
		//set some omnipresent hardcoded type exclusions (never rename these)
		setTypeExclusion(D202NccFile.class);
		setTypeExclusion(D202MasterSmilFile.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#create()
	 */
	public void create() throws FilesetRenamingException {
		//populate the URI(old), URI(new) map.
		this.namingStrategy.clear();
		for (Iterator iter = this.mInputFileset.getLocalMembersURIs().iterator(); iter.hasNext();) {
			URI uri = (URI)iter.next();
			FilesetFile f = this.mInputFileset.getLocalMember(uri);
			if(!this.isTypeDisabled(f)) {
				//create a new name: createNewName(f) method implemented by subclass
				//subclass may return the same name is input, we dont care here
				File ret = new File(f.getFile().getParentFile(),createNewName(f));				
				this.namingStrategy.put(uri,ret.toURI());
			}else{
				//let name be the same
				this.namingStrategy.put(uri,uri);
			}			
		}
	}
	
	/**
	 * Create a new name using the template that was provided in the constructor.
	 */
	private String createNewName(FilesetFile f) throws FilesetRenamingException {

		String returnName = null;
		List templateSegments = mTemplateName.getSegments();
		SegmentedFileName newName = new SegmentedFileName();
		newName.setSegmentSeparator(mSegmentSeparator); 
		try{			
			for (Iterator iter = templateSegments.iterator(); iter.hasNext();) {
				Segment templateSegment = (Segment) iter.next();
				if(templateSegment instanceof FilesetUIDSegment) {
					//template value should be 'uid'
					newName.addSegment(FilesetUIDSegment.create(mInputFileset));
				}else if(templateSegment instanceof RandomUniqueSegment) {
					//template value should be 'rnd(n)' where n is a positive integer
					newName.addSegment(RandomUniqueSegment.create(
							mInputFileset, Integer.parseInt(
									parseTokenParam(templateSegment.getChars()))));					
				}else if(templateSegment instanceof FixedSegment) {
					//template value should be 'prefix(str)' 
					newName.addSegment(FixedSegment.create(
							parseTokenParam(templateSegment.getChars())));
				}else if(templateSegment instanceof LabelSegment) {
					//template value should be 'label' 
					newName.addSegment(LabelSegment.create(f,mInputFileset));
				}else if(templateSegment instanceof SequenceSegment) {
					//template value should be 'label' 
					newName.addSegment(SequenceSegment.create(f,mInputFileset));
				}else if(templateSegment instanceof EchoSegment) {
					//template value should be 'echo' 
					newName.addSegment(EchoSegment.create(f));
				}  
			}
			//finally, add the extension (not represented in template)
			newName.addSegment(ExtensionSegment.create(f.getExtension()));
			
			//TODO if we have zero segments or just the extension segment, do something.			
			
			//check whether we should force ascii subset
			if(mForceAsciiSubset) {
				returnName = CharUtils.toNonWhitespace(newName.getFileName(), '_');
				returnName = CharUtils.toPrintableAscii(returnName);
			}else{
				returnName = newName.getFileName();
			}
		}catch (Exception e) {
			throw new FilesetRenamingException(e.getMessage(),e);
		}
		return returnName;
	}
	
	/**
	 * Some input tokens have parens (e.g. 'rnd(4)').
	 * @return the value of the paren as a string. If no paren or something else goes wrong, returns input value.
	 */
	private String parseTokenParam(char[] chars) {
		StringBuilder sb = new StringBuilder();

		boolean open = false;		
		for (int i = 0; i < chars.length; i++) {			
			if(chars[i]=='(') {
				open = true; continue;
			}else if(chars[i]==')' && open) {
				return sb.toString();
			}
			if(open)sb.append(chars[i]);
		}
	    return String.copyValueOf(chars);	
	}

	/**
	 * @param file a FilesetFile that may or may not be excluded from the renaming process
	 * @return true if the inparam file should be excluded from 
	 * renaming process, false if it is allowed to be renamed.
	 */
	private boolean isTypeDisabled(FilesetFile file) {
		//if no exclusions set, there are no disabled types at all
		if(this.typeExclusions.isEmpty()) return false;			

		//cast to see if inparam file is related to a member
		//of the restriction list
		for (int i = 0; i < typeExclusions.size(); i++) {			
			try{
				Class test = (Class)typeExclusions.get(i);
				test.cast(file);
				return true;  //we didnt get an exception...				
			}catch (Exception e) {
				//just continue the loop	
			}					
		}				
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#setTypeExclusion(java.lang.Class)
	 */
	public void setTypeExclusion(Class filesetFileInterface) {
		typeExclusions.add(filesetFileInterface);				
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#setTypeExclusion(java.util.List)
	 */
	public void setTypeExclusion(List filesetFileInterfaces) {
		typeExclusions.addAll(filesetFileInterfaces);				
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#validate()
	 */
	public boolean validate() throws FilesetRenamingException {		
		isValidated = true;		
		if (!namingStrategy.isEmpty()) {
			URI curValue;
			URI curKey;
			int i = -1;
			for (Iterator iter = namingStrategy.keySet().iterator(); iter.hasNext();) {
				i++;
				URI value = (URI) namingStrategy.get(iter.next());
				int k = -1;
				for (Iterator iter2 = namingStrategy.keySet().iterator(); iter2.hasNext();) {					
					k++;
					curKey = (URI)iter2.next();
					curValue = (URI) namingStrategy.get(curKey);
					if(i!=k) {
						if(value.equals(curValue)) {
							throw new FilesetRenamingException("duplicate output name: " + value.toString());														
						}						
						if(value.equals(curKey)) {
							throw new FilesetRenamingException("output name collides with input name of other member: " + value.toString());													
						}
					}
				}								
			}			
		} else { 
			throw new FilesetRenamingException("strategy not set");
		}//if (!strategy.isEmpty())
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#getNewLocalName(org.daisy.util.fileset.interfaces.FilesetFile)
	 */	
	public String getNewLocalName(FilesetFile file) {		
		return this.getNewLocalName(file.getFile().toURI());
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#getNewLocalName(java.net.URI)
	 */
	public String getNewLocalName(URI filesetFileURI) {		
		URI newURI = (URI)namingStrategy.get(filesetFileURI);
		if(null != newURI) {
			return (new File(newURI)).getName();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#getIterator()
	 */
	public Iterator getIterator(){
		return this.namingStrategy.keySet().iterator();
	}
}
