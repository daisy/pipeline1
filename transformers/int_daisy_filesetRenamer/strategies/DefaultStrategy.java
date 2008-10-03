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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.daisy.util.file.Directory;
import org.daisy.util.file.EFile;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.util.FilesetLabelProvider;
import org.daisy.util.i18n.CharUtils;

/**
 * A base renaming strategy
 * @author Markus Gylling
 */
public class DefaultStrategy implements RenamingStrategy {
	private Fileset mInputFileset = null;
	private SegmentedFileName mTemplateName = null;
	private Map<URI,URI> namingStrategy = new HashMap<URI,URI>(); 	// <URI>,<URI>
	private List<Class<?>> typeExclusions = new ArrayList<Class<?>>();	// Interface names
	private int mMaxFilenameLength = 64;				
	private String mSegmentSeparator = "_";							//default, may wanna enable changing this
	private boolean mForceAsciiSubset = false;
	//private boolean isValidated = false;
	private FilesetLabelProvider mLabelProvider = null;
	private int mFileCount = 0;
	private Directory mOutputDirectory;
	
	public DefaultStrategy(Fileset fileset, SegmentedFileName templateName, boolean forceAsciiSubset, Directory outputDirectory) {
		mInputFileset = fileset;
		mTemplateName = templateName;
		mForceAsciiSubset = forceAsciiSubset;
		mLabelProvider = new FilesetLabelProvider(mInputFileset);
		mOutputDirectory = outputDirectory;
		
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
		
		for (Iterator<URI> iter = this.mInputFileset.getLocalMembersURIs().iterator(); iter.hasNext();) {
			URI inputURI = iter.next();
			FilesetFile f = this.mInputFileset.getLocalMember(inputURI);
			if(!this.isTypeDisabled(f)) {
				//create a new name: createNewName(f) method implemented by subclass
				//subclass may return the same name is input, we dont care here
				
				//mg20081003: if filesystemsafe, make sure subdirs are also safe
				File ret = null;
				
				if(isInFilesetRootDir(f)) {					
					ret = new File(mOutputDirectory,createNewName(f));	
				}else{					
					String parentPath = f.getFile().getParentFile().getName();
					if(mForceAsciiSubset && !CharUtils.isFilenameCompatible(parentPath, CharUtils.FilenameRestriction.Z3986)) {
						parentPath = CharUtils.toRestrictedSubset(CharUtils.FilenameRestriction.Z3986, parentPath);
					}
					ret = new File(mOutputDirectory.getPath() + File.separatorChar + parentPath,createNewName(f));					
				}
											
				this.namingStrategy.put(inputURI,ret.toURI());
			}else{
				//let name be the same
				this.namingStrategy.put(inputURI,inputURI);
			}			
		}
		//System.err.println("stop");
	}
	
	private boolean isInFilesetRootDir(FilesetFile f) {
		try {
			if(f.getFile().getParentFile().getCanonicalPath().equals(
					mInputFileset.getManifestMember().getFile().getParentFile().getCanonicalPath())) {
				return true;
			}
		} catch (IOException e) {
			return true;
		}
		return false;
	}

	/**
	 * Create a new name using the template that was provided in the constructor.
	 */
	private String createNewName(FilesetFile f) throws FilesetRenamingException {
		
		String returnName = null;
		List<Segment> templateSegments = mTemplateName.getSegments();
		SegmentedFileName newName = new SegmentedFileName();
		newName.setSegmentSeparator(mSegmentSeparator);
		
		mFileCount++;
		try{			
			for (Iterator<Segment> iter = templateSegments.iterator(); iter.hasNext();) {
				Segment templateSegment = iter.next();
				if(templateSegment instanceof FilesetUIDSegment) {
					//template value should be 'uid'
					newName.addSegment(FilesetUIDSegment.create(mLabelProvider));
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
					newName.addSegment(LabelSegment.create(f,mLabelProvider));
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
			//if (newName.getSegments().size()<2) return f.getName();
			
			//check whether we should go filename safe; TODO use ISO 9660 which in level 1 is a subset of the z3986 restriction
			if(mForceAsciiSubset) {
				returnName = CharUtils.toNonWhitespace(newName.getFileName(), '_');
				returnName = CharUtils.toPrintableAscii(returnName);
				
				if(!CharUtils.isFilenameCompatible(returnName, CharUtils.FilenameRestriction.Z3986)) {
					StringBuilder truncName = new StringBuilder();
					char prevChar = ' ';
					for (int i = 0; i < returnName.length(); i++) {
						//remove any nonallowed chars
						//remove dual underscores
						char ch = returnName.charAt(i);
						if(!CharUtils.isFilenameCompatible(ch, CharUtils.FilenameRestriction.Z3986)) {
							//System.err.println("ignoring filenameincompatible: " + ch);
						}else{
							if(!(ch=='_' && prevChar == '_')) {
								truncName.append(ch);
							}	
						}	
						prevChar = ch;
					}
					returnName = truncName.toString();
				}
								
			}else{
				returnName = newName.getFileName();
			}
			
			
			//make sure we have something usable
			String test = returnName.replace("_", "");
			if(test.length()<1) {
				returnName = "file" + mFileCount;
			}
			
			EFile efile = new EFile(returnName);
			if(efile.getNameMinusExtension().length()>mMaxFilenameLength){
				try{				
					returnName = efile.getNameMinusExtension().substring(0, mMaxFilenameLength)+"."+efile.getExtension();
				}catch (Exception e) {
					e.printStackTrace();
				}
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
				Class<?> test = typeExclusions.get(i);
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
	public void setTypeExclusion(Class<?> filesetFileInterface) {
		typeExclusions.add(filesetFileInterface);				
	}

	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#setTypeExclusion(java.util.List)
	 */
	public void setTypeExclusion(List<Class<?>> filesetFileInterfaces) {
		typeExclusions.addAll(filesetFileInterfaces);				
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#validate()
	 */
	public boolean validate() throws FilesetRenamingException {		
		boolean foundProblem = false;
		
		if (!namingStrategy.isEmpty()) {
			URI curValue;
			URI curKey;
			int i = -1;
			for (Iterator<URI> iter = namingStrategy.keySet().iterator(); iter.hasNext();) {
				i++;
				URI value = namingStrategy.get(iter.next());
				int k = -1;
				for (Iterator<URI> iter2 = namingStrategy.keySet().iterator(); iter2.hasNext();) {					
					k++;
					curKey = iter2.next();
					curValue = namingStrategy.get(curKey);
					if(i!=k) {
						if(value.equals(curValue)) {
							//throw new FilesetRenamingException("duplicate output name: " + value.toString());
							foundProblem = true;
							namingStrategy.put(curKey, tweakName(curValue));
							break;
						}						
						if(value.equals(curKey)) {
							//throw new FilesetRenamingException("output name collides with input name of other member: " + value.toString());
							foundProblem = true;
							namingStrategy.put(curKey, tweakName(curValue));
							break;
						}
					}
				}
				if(foundProblem) break;
			}
			if(foundProblem) {
				return false;
			}
		} else { 
			throw new FilesetRenamingException("strategy not set");
		}//if (!strategy.isEmpty())
		
		return true;
	}
	
	private URI tweakName(URI uri) {
		EFile file = new EFile(uri);
		String newName =
			file.getNameMinusExtension() + '_' + '.' + file.getExtension();
		
		return new File(file.getParentFile(),newName).toURI();
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
		URI newURI = namingStrategy.get(filesetFileURI);
		if(null != newURI) {
			return (new File(newURI)).getName();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#getNewLocalName(java.net.URI)
	 */
	public URI getNewURI(URI filesetFileURI) {		
		return namingStrategy.get(filesetFileURI);
	}
	
	/*
	 * (non-Javadoc)
	 * @see int_daisy_filesetRenamer.strategies.RenamingStrategy#getIterator()
	 */
	public Iterator<URI> getIterator(){
		return this.namingStrategy.keySet().iterator();
	}

	public void setMaxFilenameLength(int maxFilenameLength) {
		mMaxFilenameLength = maxFilenameLength;		
	}
}
