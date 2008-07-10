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
package org.daisy.util.xml.peek;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.exception.SAXStopParsingException;
import org.daisy.util.file.EFile;
import org.daisy.util.xml.XMLUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.sax.AttributesCloner;
import org.daisy.util.xml.sax.SAXConstants;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

/**
 * 
 * @author Markus Gylling
 */
class PeekerImpl implements Peeker, ContentHandler, EntityResolver, ErrorHandler, LexicalHandler  { 

	private SAXParser mSAXParser = null;			//underlying parser
	private PeekResult mPeekResult = null;			//result of the peek
	private boolean mDebugMode = false;						

	/**
	 * Default constructor.
	 * @param p a preconfigured SAXParser, ideally using org.xml.sax.ext.Locator2 
	 */
	/*package*/ PeekerImpl(SAXParser p) {
       this.mSAXParser = p;
       if(System.getProperty("org.daisy.debug")!=null) {
    	   this.mDebugMode = true;
       }
	}

	/**
	 * Retrieve access to the SAXParser used by this Peeker instance.
	 */
	/*package*/ SAXParser getSAXParser() {
		return this.mSAXParser;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(java.net.URL)
	 */
	public PeekResult peek(URL document) throws SAXException, IOException {
		//redirect to this.peek(InputSource)
		StreamSource ss = new StreamSource(document.openStream());
		ss.setSystemId(document.toString());
		return peek(SAXSource.sourceToInputSource(ss));			
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(java.net.URI)
	 */
	public PeekResult peek(URI document) throws SAXException, IOException {
		//redirect to this.peek(InputSource)
		StreamSource ss = new StreamSource(document.toURL().openStream());
		ss.setSystemId(document.toString());		
		return peek(SAXSource.sourceToInputSource(ss));						
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(javax.xml.transform.Source)
	 */
	public PeekResult peek(Source document) throws SAXException, IOException {
		//redirect to this.peek(InputSource)	
		return peek(SAXSource.sourceToInputSource(document));				
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(java.io.File)
	 */
	public PeekResult peek(File document) throws SAXException, IOException {
		//rd 20080502: don't use FileReader to prevent problems with BOM 
		//InputSource is = new InputSource(new FileReader(document));
		InputSource is = new InputSource(document.toString());
		is.setSystemId(document.toURI().toString());
		return peek(is);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(org.daisy.util.file.EFile)
	 */
	public PeekResult peek(EFile document) throws SAXException, IOException {
		//redirect to this.peek(InputSource)		
		return peek(document.asInputSource());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(java.io.InputStream)
	 */
	public PeekResult peek(InputStream document) throws SAXException, IOException {
		//redirect to this.peek(InputSource)	
		return peek(SAXSource.sourceToInputSource(new StreamSource(document)));				
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.peek.Peeker#peek(org.xml.sax.InputSource)
	 */
	public PeekResult peek(InputSource document) throws SAXException, IOException {
		if(mDebugMode) System.out.println("DEBUG: peek.PeekerImpl#peek on " + document.getSystemId());		
		try {
			mSAXParser.getXMLReader().setContentHandler(this);
			mSAXParser.getXMLReader().setEntityResolver(this);
			mSAXParser.getXMLReader().setErrorHandler(this);
			//since we sometimes have loadDTD turned off,
			//we use lexical handler to get the pub and sys id of prolog
			mSAXParser.getXMLReader().setProperty(SAXConstants.SAX_PROPERTY_LEXICAL_HANDLER, this);			
			mPeekResult = new PeekResult(document.getSystemId());			
			mSAXParser.getXMLReader().parse(document);			
		}catch (SAXStopParsingException sspe) {
			//expected: everything went fine			
		}
		if(document.getByteStream()!=null)document.getByteStream().close();
		if(document.getCharacterStream()!=null)document.getCharacterStream().close();
		return mPeekResult;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        mPeekResult.setRootElementLocalName(localName);
        mPeekResult.setRootElementNsUri(uri);
        mPeekResult.setRootElementAttributes(AttributesCloner.clone(atts)); 
        try{
        	mPeekResult.setIsStandalone(mSAXParser.getXMLReader().getFeature(SAXConstants.SAX_FEATURE_IS_STANDALONE));
        }catch (SAXNotRecognizedException snre){
        	if(mDebugMode) System.out.println("DEBUG: peek.PeekerImpl#startElement SAXNotRecognizedException");
        }
        mPeekResult.setXSISchemaLocationURIs(XMLUtils.getXSISchemaLocationURIs(uri, localName, qName, atts));
        throw new SAXStopParsingException("");		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
		try{
		  Locator2 locator2 = (Locator2) locator;		  
		  mPeekResult.setPrologEncoding(locator2.getEncoding());
		  mPeekResult.setPrologXmlVersion(locator2.getXMLVersion());		  
		}catch (ClassCastException cce) {
			//we didnt have a SAXParser configured to use Locator2
			if(mDebugMode) {
				System.out.println("DEBUG: PeekerImpl#setDocumentLocator(Locator) couldnt cast Locator to Locator2");
			}
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void startPrefixMapping(String prefix, String uri) throws SAXException {		
		mPeekResult.addPrefixMapping(prefix, uri);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if(mDebugMode) {
			System.out.println("DEBUG: PeekerImpl#resolveEntity -- loadDTD is on");
		}    	        
        mPeekResult.setPrologPublicId(publicId);                           
        mPeekResult.setPrologSystemId(systemId);        
        return CatalogEntityResolver.getInstance().resolveEntity(publicId, systemId);
    }

    /*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
	 */
    @SuppressWarnings("unused")
	public void startDTD(String name, String publicId, String systemId) throws SAXException {
		if(mDebugMode) {
			System.out.println("DEBUG: PeekerImpl#startDTD -- LexicalHandler is reporting");
		}  
        mPeekResult.setPrologPublicId(publicId);                           
        mPeekResult.setPrologSystemId(systemId);        				
	}

    /*
     * (non-Javadoc)
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
	@SuppressWarnings("unused")
	public void processingInstruction(String target, String data) throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@SuppressWarnings("unused")
	public void characters(char[] ch, int start, int length) throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */	
	@SuppressWarnings("unused")
	public void endDocument() throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@SuppressWarnings("unused")
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@SuppressWarnings("unused")
	public void startDocument() throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void skippedEntity(String name) throws SAXException {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void endPrefixMapping(String prefix) throws SAXException {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@SuppressWarnings("unused")
	public void error(SAXParseException exception) throws SAXException {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@SuppressWarnings("unused")
	public void fatalError(SAXParseException exception) throws SAXException {
				
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@SuppressWarnings("unused")
	public void warning(SAXParseException exception) throws SAXException {
				
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
	 */
	@SuppressWarnings("unused")
	public void comment(char[] ch, int start, int length) throws SAXException {

	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
	 */
	@SuppressWarnings("unused")
	public void endCDATA() throws SAXException {

	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#endDTD()
	 */
	@SuppressWarnings("unused")
	public void endDTD() throws SAXException {

	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void endEntity(String name) throws SAXException {

	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
	 */
	@SuppressWarnings("unused")
	public void startCDATA() throws SAXException {

	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
	 */
	@SuppressWarnings("unused")
	public void startEntity(String name) throws SAXException {

	}

}
