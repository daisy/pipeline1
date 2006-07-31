package int_daisy_filesetRenamer.strategies;

import int_daisy_filesetRenamer.FilesetRenamingException;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * <p>Interface that any renaming strategy must implement.</p>
 * <p>AbstractStrategy.java provides a base abstract convenience class for implementations.</p>
 * @author Markus Gylling
 */
public interface RenamingStrategy {

	/**
	 * set the input fileset to create a strategy for
	 */
	public void setInputFileset(Fileset fileset);

	/**
	 * get the input fileset 
	 */
	public Fileset getInputFileset();
	
	/**
	 * create the renaming strategy (oldname&lt;URI&gt;/newname&lt;URI&gt; table).
	 */
	public void createStrategy();
	
	/**
	 * Create a single new name within the strategy
	 */
	public String createNewName(FilesetFile f);
	
	/**
	 * Enable one or several specific FilesetFile interface Classes for renaming.
	 * This equals disabling all FilesetFile interface Classes not registered here.
	 * However, if this method is never called on an instance, all members are enabled.  
	 */
	public void setTypeRestriction(Class filesetFileInterface);

	/**
	 * Enable one or several specific FilesetFile interface Classes for renaming.
	 * This equals disabling all FilesetFile interface Classes not registered here.
	 * However, if this method is never called on an instance, all members are enabled.  
	 */
	public void setTypeRestriction(List filesetFileInterfaces);
	
	/**
	 * Set an optional default prefix to use in new names.
	 */
	public void setDefaultPrefix(String prefix);
	
	/**
	 * Checks whether there are name collisions in naming strategy;
	 * two output files with same path+name, or the case where a new name 
	 * is created that happens to coincide with the old name of another member.
	 * @return true if no naming collisions exist
	 * @throws FilesetRenamingException if collisions do exist
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
	 * get an iterator for the keyset of the URI(old),URI(new) HashMap
	 */
	public Iterator getIterator();
	
}
