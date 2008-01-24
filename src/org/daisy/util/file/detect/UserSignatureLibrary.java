package org.daisy.util.file.detect;

import java.net.URL;
import java.util.Set;

//import org.daisy.util.xml.sax.SAXParseExceptionMessageFormatter;
//import org.daisy.util.xml.validation.SimpleValidator;
//import org.xml.sax.ErrorHandler;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;

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
