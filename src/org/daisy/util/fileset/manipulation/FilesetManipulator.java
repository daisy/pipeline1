/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.util.fileset.manipulation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.manipulation.manipulators.UnalteringCopier;

/**
 * <p>The centre class of the fileset manipulation package.</p>
 * <p>This class handles i/o operations, listener exposure and filtering.</p>
 * @author Markus Gylling
 */
public class FilesetManipulator implements FilesetErrorHandler {
	protected Fileset inputFileset = null;
	protected List typeRestrictions = null;
	protected EFolder outFolder = null;
	protected FilesetManipulatorListener listener = null;
	private EFolder inputBaseDir = null; //the parent folder of input manifest file
	private boolean allowDestinationOverwrite = true;
	
	/**
	 * Default constructor.
	 * @see #FilesetManipulator(boolean)
	 */
	public FilesetManipulator() {

	}

	/**
	 * Extended constructor.
	 * @param overwriteDestinations 
	 * 	whether this manipulator should allow existing destination files
	 * to be overwritten. If this is not allowed and a destination exists, an exception will be thrown.
	 * Default value (by using parameterless constructor) is true (overwrite allowed).
	 */
	public FilesetManipulator(boolean overwriteDestinations) {
		this.allowDestinationOverwrite = overwriteDestinations;
	}
	
	/**
	 * Set the org.daisy.util.fileset instance that 
	 * represents the Fileset to be modified
	  @see #setInputFileset(URI)
	 */
	public void setInputFileset(Fileset fileset) throws FilesetManipulationException {
		 this.inputFileset = fileset;	
		 try {
			this.inputBaseDir = new EFolder(this.inputFileset.getManifestMember().getFile().getParentFile());
		 } catch (IOException e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		 }
	}

	/**
	 * Set the URI to the manifest member of the fileset to be modified
	 * @see #setInputFileset(Fileset)
	 */
	public void setInputFileset(URI manifest) throws FilesetManipulationException {
		try { 
			this.inputFileset = new FilesetImpl(manifest,this,false,false);				 
			this.inputBaseDir = new EFolder(this.inputFileset.getManifestMember().getFile().getParentFile());
		} catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}

	}
	
	/**
	 * Set the destination of the manipulated Fileset
	 */
	public void setOutputFolder(EFolder folder) throws IOException {		
		FileUtils.createDirectory(folder);		
		this.outFolder = folder;
	}
	
	/**
	 * Get the destination of the manipulated Fileset, null if not set
	 */
	public EFolder getOutputFolder() throws IOException {
		return this.outFolder;
	}
	
	/**
	 * Get the org.daisy.util.fileset instance that 
	 * represents the fileset to be modified
	 * @return a Fileset instance or null
	 */
	public Fileset getInputFileset() {
 		return this.inputFileset;
	}

	/**
	 * Apply a restriction on which types to expose to listener for manipulation.
	 * @param filesetFileInterfaces a List of FilesetFile subclass interface Class objects.
	 * <p>At #iterate, any match in the restriction list will result in a call to the #nextFile method on a registered listener.</p>
	 * <p>At #iterate, any nonmatch in the restriction list will result in the file being copied to destination dir unaltered without notifying 
	 * the listener.</p>
	 * <p>If this property is not set on the instance, no exclusions are made. This is
	 * the same as calling this method with a list containing a FilesetFile Class entry.</p> 
	 */
	public void setTypeRestriction(List filesetFileInterfaces) {
		if(this.typeRestrictions == null) this.typeRestrictions = new ArrayList();
		this.typeRestrictions.addAll(filesetFileInterfaces);
	}

	/**
	 * Apply a restriction on which types to expose to listener for manipulation.
	 * @param filesetFileInterface a FilesetFile subclass interface Class object.
	 * <p>At #iterate, any match in the restriction list will result in a call to the #nextFile method on a registered listener.</p>
	 * <p>At #iterate, any nonmatch in the restriction list will result in the file being copied to destination dir unaltered without notifying 
	 * the listener.</p>
	 * <p>If this property is not set on the instance, no exclusions are made. This is
	 * the same as calling this method with a list containing a FilesetFile entry.</p> 
	 */
	public void setFileTypeRestriction(Class filesetFileInterface) {
		if(this.typeRestrictions == null) this.typeRestrictions = new ArrayList();
		this.typeRestrictions.add(filesetFileInterface);
	}
	
	/**
	 * Empties the type restriction list
	 */
	public void clearTypeRestriction() {
		if(this.typeRestrictions != null) this.typeRestrictions.clear();		
	}
	
	/**
	 * Start the manipulation process. This will push #nextFile method calls to 
	 * the registered FilesetManipulatorListener. 
	 * @throws FilesetManipulationException
	 */
	public boolean iterate() throws FilesetManipulationException {
		if(this.listener != null) {
			if(this.inputFileset != null && (this.outFolder!=null && this.outFolder.exists())) {
				try{
					FilesetFileManipulator copier = new UnalteringCopier(); //for all silent moves
					for (Iterator iter = this.inputFileset.getLocalMembers().iterator(); iter.hasNext();) {
						FilesetFile file = (FilesetFile) iter.next();
						if(this.isTypeEnabled(file)) {
							//notify listener, get action impl back
							FilesetFileManipulator ffm = listener.nextFile(file);
							if(null!=ffm) {
								ffm.manipulate(file, getDestination(file),allowDestinationOverwrite);
							}else{
								//copy over unaltered								
								copier.manipulate(file,getDestination(file),allowDestinationOverwrite);								
							}					
						} else{//if(this.isTypeEnabled(file))
							//copy over unaltered
							copier.manipulate(file,getDestination(file),allowDestinationOverwrite);
						}//if(this.isTypeEnabled(file))
					} //for iter
				}catch (Exception e) {
					throw new FilesetManipulationException(e.getMessage(),e);
				}
			}else{ //this.inputFileset != null
				throw new FilesetManipulationException("input or output is null");
			}//this.inputFileset != null
		}else{ //this.inputFileset != null
			throw new FilesetManipulationException("manipulator listener is null");
		}//this.listener != null
		
		return true;
	}

	
	/**
	 * the incoming file is a member of input Fileset
	 * determine where in outFolder it should be located
	 * return a file describing the location 
	 */
	private File getDestination(FilesetFile file) throws IOException {		
		
		if(inputBaseDir!=null) {
			if(file.getFile().getParentFile().getCanonicalPath().equals(inputBaseDir.getCanonicalPath())) {
				//file is in same dir as manifestfile
				return new File(this.outFolder, file.getName());
			}
			//file is in subdir
			URI relative = inputBaseDir.toURI().relativize(file.getFile().getParentFile().toURI());
			if(relative.toString().startsWith("..")) throw new IOException("fileset member "+file.getName()+" does not live in a sibling or descendant folder of manifest member");
			EFolder subdir = new EFolder(this.outFolder,relative.getPath());
			FileUtils.createDirectory(subdir);
			return new File(subdir, file.getName());			
		}
		throw new IOException("inputBaseDir is null");							
	}

	/**
	 * @return true if inparam file is enabled for manipulation 
	 * (depends on setTypeRestriction)
	 */
	protected boolean isTypeEnabled(FilesetFile file) {				
		//if no restriction set, always true
		if(this.typeRestrictions==null)return true;			
		//cast to see if inparam file is related to a member
		//of the restriction list
		for (int i = 0; i < typeRestrictions.size(); i++) {			
			try{
				Class test = (Class)typeRestrictions.get(i);
				Object cast = test.cast(file);
				return true;  //we didnt get an exception...				
			}catch (Exception e) {
				//just continue the loop	
			}					
		}				
		return false;		
	}

	/**
	 * Set the FilesetManipulatorListener.
	 */
	public void setListener(FilesetManipulatorListener listener) {
		this.listener = listener;		
	}
	
	/**
	 * Get the FilesetManipulatorListener.
	 */
	public FilesetManipulatorListener getListener() {
		return this.listener;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		// this method when the setInputFileset(URI is used)
		//just pass on to listener who extends the same interface
		this.listener.error(ffe);
		
	}
}
