/*
 * Created on 2005-jun-19
 */
package org.daisy.util.xml;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.daisy.util.exception.SAXStopParsingException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
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
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author Markus Gylling
 */
public class PeekerImpl extends DefaultHandler implements ContentHandler, ErrorHandler, DTDHandler, LexicalHandler, EntityResolver, Peeker{
	static SAXParserFactory factory;
	static SAXParser parser;
	
	private String rootElementNsUri="";
	private String rootElementQName="";
	private String rootElementLocalName="";
	private String firstPublicId="";
	
	public PeekerImpl() {
				
		if (factory==null) {
			factory = SAXParserFactory.newInstance();		
			factory.setValidating(false);
			factory.setNamespaceAware(true);				
		}   				
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			
			System.err.println("ParserConfigurationException in peeker");
		} catch (SAXException e) {
			System.err.println("SAXException in peeker");
		}	
		
		//System.err.println(parser.getClass().getCanonicalName());
	}
	
	public void peek(URI uri) throws SAXException, IOException {
		rootElementNsUri = "";
		rootElementQName = "";
		rootElementLocalName = "";
		firstPublicId = "";
		try{
			File f = new File(uri);
			parser.parse(f, this);
		}catch (SAXStopParsingException sspe) {
			//parser.reset();	
		}
	}
	
	public void reset() {
		rootElementNsUri="";
		rootElementQName="";
		rootElementLocalName="";
		firstPublicId="";		
	}

	
	public String getFirstPublicId() {
		return firstPublicId;
	}
	
	public String getRootElementLocalName() {
		return rootElementLocalName;
	}
	
	public String getRootElementNsUri() {
		return rootElementNsUri;
	}
	
	public String getRootElementQName() {
		return rootElementQName;
	}
	
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		this.rootElementQName = qName;
		this.rootElementLocalName = localName;
		this.rootElementNsUri = uri;
		throw new SAXStopParsingException();		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {}
	
	public void setDocumentLocator(Locator locator) {}
	
	public void startDocument() throws SAXException {}
	
	public void endDocument() throws SAXException {}
	
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}
	
	public void endPrefixMapping(String prefix) throws SAXException {}
	
	public void characters(char[] ch, int start, int length) throws SAXException {}
	
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	
	public void processingInstruction(String target, String data) throws SAXException {}
	
	public void skippedEntity(String name) throws SAXException {}
	
	public void startDTD(String name, String publicId, String systemId) throws SAXException {}
	
	public void endDTD() throws SAXException {}
	
	public void startEntity(String name) throws SAXException {}
	
	public void endEntity(String name) throws SAXException {}
	
	public void startCDATA() throws SAXException {}
	
	public void endCDATA() throws SAXException {}
	
	public void comment(char[] ch, int start, int length) throws SAXException {}
	
	public void warning(SAXParseException exception) throws SAXException {}
	
	public void error(SAXParseException exception) throws SAXException {}
	
	public void fatalError(SAXParseException exception) throws SAXException {}
	
	public void notationDecl(String name, String publicId, String systemId) throws SAXException {}
	
	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {}
	
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (publicId != null) {
			this.firstPublicId = publicId;
		}
		return CatalogEntityResolver.getInstance().resolveEntity(publicId, systemId);
	}

	
}
