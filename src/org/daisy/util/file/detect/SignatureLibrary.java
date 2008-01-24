package org.daisy.util.file.detect;

import java.net.URL;
import java.util.Set;

/**
 *
 * @author Markus Gylling
 */
/*package*/ abstract class SignatureLibrary {

	/**
	 * Retrieve the set of Signatures contained within this library. 
	 */
	/*package*/ abstract Set<Signature> getSignatures();	
	
	/**
	 * Retrieve the locus of the XML representation of this library. 
	 */
	/*package*/ abstract URL getURL();
	
}
