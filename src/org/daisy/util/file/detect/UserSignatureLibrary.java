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
 * A Signature Library provided by user at runtime.
 * @author Markus Gylling
 */
/*package*/ class UserSignatureLibrary extends SignatureLibrary /*implements ErrorHandler*/ {
	private URL mXmlDocUrl = null;
	private Set<Signature> mSignatures = null;
	
	/*package*/ UserSignatureLibrary(URL doc) throws SignatureLibraryException {
		mXmlDocUrl = doc;
		try{
//			URL schema = this.getClass().getResource("SignatureLibrary.rng");
//			SimpleValidator validator = new SimpleValidator(schema, this);
//			validator.validate(doc);			
			mSignatures = SignatureLibraryLoader.load(mXmlDocUrl);			
		} catch (Exception e) {
			throw new SignatureLibraryException(e.getMessage(),e);
		}	
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.file.detect.SignatureLibrary#getURL()
	 */
	/*package*/ URL getURL() {		
		return mXmlDocUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.file.detect.SignatureLibrary#getSignatures()
	 */
	/*package*/ Set<Signature> getSignatures() {
		return mSignatures;
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
//	 */
//	public void error(SAXParseException exception) throws SAXException {
//		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Error ", exception));
//		throw exception;		
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
//	 */
//	public void fatalError(SAXParseException exception)throws SAXException {
//		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Fatal error ", exception));
//		throw exception;		
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
//	 */
//	public void warning(SAXParseException exception)throws SAXException {
//				
//	}
}
