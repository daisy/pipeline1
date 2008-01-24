package org.daisy.util.file.detect;

import java.net.URL;
import java.util.Set;

/**
 * A singleton instance of the bundled signature library.
 * @author Markus Gylling
 */
/*package*/  class DefaultSignatureLibrary extends SignatureLibrary {
	
	private static URL mXMLDocURL = null;
	private static Set<Signature> mSignatures = null;
	private static DefaultSignatureLibrary mInstance = new DefaultSignatureLibrary();
	
	private DefaultSignatureLibrary() throws SignatureLibraryException {
		mXMLDocURL = this.getClass().getResource("DefaultSignatureLibrary.xml");
		mSignatures = SignatureLibraryLoader.load(mXMLDocURL);
	}

	/**
	 * Singleton retrieval.
	 * @return The DefaultSignatureLibrary instance
	 */
	/*package*/ static DefaultSignatureLibrary getInstance() {		
		return mInstance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.file.signature.SignatureLibrary#getURL()
	 */
	public URL getURL() {		
		return mXMLDocURL;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.file.signature.SignatureLibrary#getSignatures()
	 */
	public Set<Signature> getSignatures() {
		return mSignatures;
	}
}
