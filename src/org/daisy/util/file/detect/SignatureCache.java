package org.daisy.util.file.detect;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * A runtime Signature cache, with no XML representation. 
 * <p>Exists for optimization purposes, assuming that a SignatureDetector during its lifetime
 * will be exposed to recurring resource types.</p>
 * @author Markus Gylling
 */
/*package*/ class SignatureCache extends SignatureLibrary {	
	private HashSet<Signature> mSignatures = null;
	private URL dummyURL = null;
	
	/*package*/ SignatureCache(URL url) {
		mSignatures = new HashSet<Signature>();
		dummyURL = url;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.file.detect.SignatureLibrary#getSignatures()
	 */
	/*package*/ Set<Signature> getSignatures() {
		return mSignatures;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.file.detect.SignatureLibrary#getURL()
	 */
	/*package*/ URL getURL() {
		return dummyURL;
	}
	
	/*package*/ void addSignature(Signature sig) {
		mSignatures.add(sig);
	}

	/**
	 * Empty the cache
	 */
	/*package*/ void clearSignatures() {
		mSignatures.clear();
	}
}
