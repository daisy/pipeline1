package int_daisy_filesetRenamer.strategies;

import int_daisy_filesetRenamer.FilesetRenamingException;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.interfaces.FilesetFile;

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
	public void setTypeExclusion(Class filesetFileInterface);

	/**
	 * Disable one or several specific FilesetFile interface Classes for renaming.
	 * This equals enabling all FilesetFile interface Classes not registered here.
	 * However, if this method is never called on an instance, all members are enabled.  
	 */
	public void setTypeExclusion(List filesetFileInterfaces);
	
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
	public Iterator getIterator();
	
}
