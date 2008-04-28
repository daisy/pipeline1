/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
