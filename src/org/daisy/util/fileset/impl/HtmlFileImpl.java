/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.fileset.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;

import org.ccil.cowan.tagsoup.AutoDetector;
import org.ccil.cowan.tagsoup.Parser;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.sgml.HtmlFile;
import org.daisy.util.i18n.CharsetDetector;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * uses TagSoup
 * as the underlying XMLReader
 * @author Markus Gylling
 */

class HtmlFileImpl extends SgmlFileImpl implements HtmlFile, ManifestFile, ContentHandler, DTDHandler, ErrorHandler, EntityResolver, AutoDetector {
	private static Parser parser;
	//protected ErrorHandler listeningErrorHandler = null;
	
	HtmlFileImpl(URI uri) throws IOException, FileNotFoundException {
		super(uri,HtmlFile.mimeStringConstant);
		initialize();
	}
		 
	private void initialize(){
		parser = new Parser();
		parser.setContentHandler(this);
		parser.setDTDHandler(this);
		parser.setErrorHandler(this);
		parser.setEntityResolver(this);
	}
	
	public void parse() throws IOException, SAXException {					
		parser.parse(this.asInputSource());
		this.isParsed = true;
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		System.err.println("resolveEntity in HtmlFileImpl: " + publicId + "::" + systemId );
		return null;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {		
		for (int i = 0; i < attrs.getLength(); i++) {
			if(regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrs.getQName(i))) {
				putUriValue(attrs.getValue(i));
			}						
		} //for (int i
	}
	
	public void warning(SAXParseException spe) throws SAXException {
		myExceptions.add(new FilesetFileWarningException(this,spe));
	}

	public void error(SAXParseException spe) throws SAXException {
		myExceptions.add(new FilesetFileErrorException(this,spe));
	}

	public void fatalError(SAXParseException spe) throws SAXException {
		myExceptions.add(new FilesetFileFatalErrorException(this,spe));
	}

	public Reader autoDetectingReader(InputStream i) {
		//detect charset of self
		//return a reader with encoding prop set.
		//ignore the inputstream inparam...

		CharsetDetector det = new CharsetDetector();
		String charset = null;
		Charset cs = null;

		try {
			charset = det.detect(this.toURL());
			if (null == charset) {
				charset = det.getProbableCharsetUsingLocale();
			}
			if (null != charset) {
				if (Charset.isSupported(charset)) {
					cs = Charset.forName(charset);
				}
			}
			return new BufferedReader(new InputStreamReader(new FileInputStream(this), cs));
		} catch (Exception e) {
			myExceptions.add(new FilesetFileWarningException(this,e));
		}
		return null;
	}

	public void startDocument() throws SAXException {}
	public void endDocument() throws SAXException {}
	public void endElement(String uri, String localName, String qName) throws SAXException {}
	public void characters(char[] ch, int start, int length) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	public void processingInstruction(String target, String data) throws SAXException {}
	public void skippedEntity(String name) throws SAXException {}
	public void notationDecl(String name, String publicId, String systemId) throws SAXException {}
	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {}
	public void setDocumentLocator(Locator locator) {}
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}
	public void endPrefixMapping(String prefix) throws SAXException {}
		
    private static final long serialVersionUID = 6885823468454961127L;
}