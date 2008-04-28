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
