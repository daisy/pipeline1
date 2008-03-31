package int_daisy_xukCreator;

import java.util.Map;

import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.util.FilesetFileFilter;

/**
 * Abstract base for all XUK creators. <p>Any implementation 
 * must provide a zero-argument constructor as its default constructor.</p>
 * @author Markus Gylling
 */
public abstract class XukFilter {

	/**
	 * Method called during factory implementation discovery.
	 */
	public abstract boolean supports(Fileset inputFileset, Map<String,String> parameters);
	
	/**
	 * Perform the conversion between input and XUK.
	 */
	public abstract void createXuk(Fileset inputFileset, Map<String,String> parameters, EFolder destination) throws XukFilterException;
	
	/**
	 * Provide a FilesetFileFilter to the owner transformer to decide which members of
	 * the input fileset to move to destination after the XUK has been created.
	 */	
	public abstract FilesetFileFilter getCopyFilter();

}
