package org.daisy.util.xml;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.exception.SAXStopParsingException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author Markus Gylling
 */
public class PeekerImpl
		implements
			ContentHandler,
			ErrorHandler,
			DTDHandler,
			LexicalHandler,
			EntityResolver,
			Peeker {
	//private static SAXParserFactory factory;
	private SAXParser parser;
	private static Map saxParserFeatures;
	private static Map staxParserProperties;
	//private static XMLInputFactory xif = null;
	private XMLStreamReader xer = null;
	//private URI currentURI = null;
	private URL currentURL = null;

	private String rootElementNsUri = "";
	private String rootElementLocalName = "";
	private String firstPublicId = "";
	private String firstSystemId = "";

	private String xmlEncoding = "";
	private String xmlVersion = "";
	private String xmlStandalone = "";
	private Attributes rootElementAttributes = null;

	public PeekerImpl() {

		if(saxParserFeatures == null){
			saxParserFeatures = new HashMap();
			saxParserFeatures.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
			saxParserFeatures.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.FALSE);     
		}
		    	    	
		try {
			parser = SAXParserPool.getInstance().acquire(saxParserFeatures,null);   	
	    	parser.getXMLReader().setErrorHandler(this);
	    	parser.getXMLReader().setContentHandler(this);
	    	parser.getXMLReader().setDTDHandler(this);
	    	parser.getXMLReader().setEntityResolver(CatalogEntityResolver.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void peek(URI uri) throws SAXException, IOException {
		peek(uri.toURL());
	}

	public void peek(URL url) throws SAXException, IOException {
		reset();
		currentURL = url;
		if (parser != null) {
			try {				
				parser.getXMLReader().parse(SAXSource.sourceToInputSource(new StreamSource(url.openStream())));
				// we never get here since SAXStopParsingException is thrown
			} catch (SAXStopParsingException sspe) {
				try {
					SAXParserPool.getInstance().release(parser,saxParserFeatures,null);
				} catch (PoolException e) {
					throw new SAXException(e.getMessage(),e);
				}
			}
		} else {
			throw new SAXException("peeker parser is null");
		}
	}
	
	public void reset() {
		xer = null;
		//currentURI = null;
		currentURL = null;
		// make the getter vars the empty string to allow for less tests when
		// retreiving value
		rootElementNsUri = "";
		rootElementLocalName = "";
		firstPublicId = "";
		firstSystemId = "";
		xmlEncoding = "";
		xmlVersion = "";
		xmlStandalone = "";
	}

	public String getFirstPublicId() {
		return firstPublicId;
	}

	public String getFirstSystemId() {
		return firstSystemId;
	}

	public String getRootElementLocalName() {
		return rootElementLocalName;
	}

	public String getRootElementNsUri() {
		return rootElementNsUri;
	}

	public Attributes getRootElementAttributes() {
		return this.rootElementAttributes;
	}
	
	public QName getRootElementQName() {
		QName q = null;
		if (rootElementNsUri.length() > 0
				&& rootElementLocalName.length() > 0) {
			q = new QName(rootElementNsUri, rootElementLocalName);
		}
		return q;
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		this.rootElementLocalName = localName;
		this.rootElementNsUri = uri;
		this.rootElementAttributes  = atts;
		throw new SAXStopParsingException();
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (publicId != null) {
			this.firstPublicId = publicId;
		}
		if (systemId != null) {
			this.firstSystemId = systemId;
		}

		return CatalogEntityResolver.getInstance().resolveEntity(publicId, systemId);
	}

	// ********************************************************
	// below methods that force reparse using XMLStreamReader
	// since SAX doesnt support this until Locator2 (in sax 2.02)
	// only do reparse at request to keep the peeker fast
	// ********************************************************

	public String getXMLVersion() {
		try {
			if (xer == null)
				readStream();
			return xmlVersion;
		} catch (XMLStreamException e) {
			return null;
		}
	}

	public String getEncoding() {
		try {
			if (xer == null)
				readStream();
			return xmlEncoding;
		} catch (XMLStreamException e) {
			return null;
		}
	}

	public boolean getStandalone() {
		try {
			if (xer == null)
				readStream();
			return xmlStandalone.equals("true")
					? true
					: false;
		} catch (XMLStreamException e) {
			return false;
		}
	}

	private void readStream() throws XMLStreamException {
		try {
			
			if(staxParserProperties == null){
				staxParserProperties = new HashMap();				
				staxParserProperties.put("javax.xml.stream.isCoalescing",  Boolean.TRUE);
				staxParserProperties.put("javax.xml.stream.isNamespaceAware",  Boolean.TRUE);
				staxParserProperties.put("javax.xml.stream.isReplacingEntityReferences",  Boolean.FALSE);
				staxParserProperties.put("javax.xml.stream.isSupportingExternalEntities",  Boolean.FALSE);
				staxParserProperties.put("javax.xml.stream.isValidating",  Boolean.FALSE);
				staxParserProperties.put("javax.xml.stream.supportDTD",  Boolean.FALSE);
			}	
			
			XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(staxParserProperties); 
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));																
			XMLStreamReader xer  = xif.createXMLStreamReader(new StreamSource(currentURL.openStream()));
			while (true) {
				if (xer.getEventType() == XMLStreamConstants.START_DOCUMENT) {
					readProps();
					xer.close();
					break;
				}
				int event = xer.next();
				if (event == XMLStreamConstants.END_DOCUMENT) {
					xer.close();
					break;
				} else if (event == XMLStreamConstants.START_DOCUMENT
						|| event == XMLStreamConstants.START_ELEMENT) {
					readProps();
					xer.close();
					break;
				}
			}
		} catch (Exception e) {
			throw new XMLStreamException(e.getMessage(), e);
		}
	}

	private void readProps() {
		xmlVersion = xer.getVersion();
		xmlEncoding = xer.getCharacterEncodingScheme();
		boolean sa = xer.isStandalone();
		if (sa) {
			xmlStandalone = "true";
		} else {
			xmlStandalone = "false";
		}
	}

	public void endElement(String uri,String localName, String qName)throws SAXException {}
	public void setDocumentLocator(Locator locator) {}
	public void startDocument()throws SAXException {}
	public void endDocument() throws SAXException {}
	public void startPrefixMapping(String prefix,String uri) throws SAXException {}
	public void endPrefixMapping(String prefix)throws SAXException {}
	public void characters(char[] ch, int start,int length) throws SAXException {}
	public void ignorableWhitespace(char[] ch,int start, int length)throws SAXException {}
	public void processingInstruction(String target, String data)throws SAXException {}
	public void skippedEntity(String name)throws SAXException {}
	public void startDTD(String name,String publicId, String systemId)throws SAXException {}
	public void endDTD() throws SAXException {}
	public void startEntity(String name)throws SAXException {}
	public void endEntity(String name)throws SAXException {}
	public void startCDATA() throws SAXException {}
	public void endCDATA() throws SAXException {}
	public void comment(char[] ch, int start,int length) throws SAXException {}
	public void warning(SAXParseException exception)throws SAXException {}
	public void error(SAXParseException exception)throws SAXException {}
	public void fatalError(SAXParseException exception)throws SAXException {}
	public void notationDecl(String name,String publicId, String systemId)throws SAXException {}
	public void unparsedEntityDecl(String name,String publicId, String systemId,String notationName)throws SAXException {}
	
}
