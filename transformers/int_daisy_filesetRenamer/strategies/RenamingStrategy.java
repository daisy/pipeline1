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

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;

/**
 * A base interface for fileset renaming strategies.
 * @author Markus Gylling
 */
public interface RenamingStrategy {
	
	/**
	 * Create a renaming strategy (a list of old and new names).
	 */
	public void create() throws FilesetRenamingException;
		
	/**
	 * Disable one or several specific FilesetFile interface Classes for renaming.
	 * This equals enabling all FilesetFile interface Classes not registered here.
	 * However, if this method is never called on an instance, all members are enabled.  
	 */
	public void setTypeExclusion(Class<?> filesetFileInterface);

	/**
	 * Disable one or several specific FilesetFile interface Classes for renaming.
	 * This equals enabling all FilesetFile interface Classes not registered here.
	 * However, if this method is never called on an instance, all members are enabled.  
	 */
	public void setTypeExclusion(List<Class<?>> filesetFileInterfaces);
	
	/**
	 * Checks whether there are name collisions in naming strategy;
	 * two output files with same path+name, or the case where a new name 
	 * is created that happens to coincide with the old name of another member.
	 * @return true if no naming collisions exist
	 * @throws FilesetFileFatalErrorException if collisions do exist
	 */
	public boolean validate() throws FilesetRenamingException;	
	
	/**
	 * @param file an input fileset FilesetFile that may or may not have been given a new name in the renaming strategy
	 * @return the local name of the output FilesetFile - this may be the same name as the inparam file name, depending on strategy  
	 */
	public String getNewLocalName(FilesetFile file);

	/**
	 * @param  filesetFileURI an input fileset URI that may or may not have been given a new name in the renaming strategy
	 * @return the local name of the output FilesetFile - this may be the same name as the inparam file name, depending on strategy 
	 */	
	public String getNewLocalName(URI filesetFileURI);

	
	/**
	 * Get an iterator for the keyset of the URI(old),URI(new) HashMap
	 */
	public Iterator<URI> getIterator();

	/**
	 * Set the maximum numbers of characters in generated filenames.
	 */
	public void setMaxFilenameLength(int maxFilenameLength);
	
}
