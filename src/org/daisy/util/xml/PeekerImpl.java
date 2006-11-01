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
package org.daisy.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.exception.SAXStopParsingException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
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
 * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
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
    private static SAXParserFactory factory;
    private static SAXParser parser;
    private static XMLInputFactory xif = null;
    private XMLStreamReader xer = null;
    private URI currentURI = null;

    private String rootElementNsUri ="";
    private String rootElementLocalName = "";
    private String firstPublicId = "";
    private String firstSystemId = "";

    private String xmlEncoding = "";
    private String xmlVersion = "";
    private String xmlStandalone = "";

    /**
     * @deprecated Use org.daisy.util.xml.peek.PeekerPool instead
     */
    public PeekerImpl() {

        if (factory == null) {
            factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
        }
        try {
            parser = factory.newSAXParser();
            parser.getXMLReader().setContentHandler(this);
            parser.getXMLReader().setErrorHandler(this);
            parser.getXMLReader().setDTDHandler(this);
            parser.getXMLReader().setEntityResolver(this);
        } catch (ParserConfigurationException e) {
            System.err.println("ParserConfigurationException in peeker: "
                    + e.getMessage());
        } catch (SAXException e) {
            System.err.println("SAXException in peeker: "
                    + e.getMessage());
        }
    }

    public void peek(URI uri)
            throws SAXException, IOException {
        reset();
        currentURI = uri;
        if (parser != null) {
            try {
                File f = new File(uri);
                parser.getXMLReader().parse(new InputSource(new FileInputStream(f)));
                // we never get here since SAXStopParsingException is thrown
            } catch (SAXStopParsingException sspe) {
                //
            }
        } else {
            throw new SAXException("peeker parser is null");
        }
    }

    public void reset() {
        xer = null;
        currentURI = null;
        //make the getter vars the empty string to allow for less tests when retreiving value
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

    public QName getRootElementQName() {
        QName q = null;
        if (rootElementNsUri.length() > 0
                && rootElementLocalName.length() > 0) {
            q = new QName(rootElementNsUri, rootElementLocalName);
        }
        return q;
    }

    public void startElement(String uri,
            String localName, String qName,
            Attributes atts) throws SAXException {
        this.rootElementLocalName = localName;
        this.rootElementNsUri = uri;
        throw new SAXStopParsingException("");
    }

    public void endElement(String uri,
            String localName, String qName)
            throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument()
            throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix,
            String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix)
            throws SAXException {
    }

    public void characters(char[] ch, int start,
            int length) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch,
            int start, int length)
            throws SAXException {
    }

    public void processingInstruction(
            String target, String data)
            throws SAXException {
    }

    public void skippedEntity(String name)
            throws SAXException {
    }

    public void startDTD(String name,
            String publicId, String systemId)
            throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void startEntity(String name)
            throws SAXException {
    }

    public void endEntity(String name)
            throws SAXException {
    }

    public void startCDATA() throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void comment(char[] ch, int start,
            int length) throws SAXException {
    }

    public void warning(
            SAXParseException exception)
            throws SAXException {
    }

    public void error(SAXParseException exception)
            throws SAXException {
    }

    public void fatalError(
            SAXParseException exception)
            throws SAXException {
    }

    public void notationDecl(String name,
            String publicId, String systemId)
            throws SAXException {
    }

    public void unparsedEntityDecl(String name,
            String publicId, String systemId,
            String notationName)
            throws SAXException {
    }

    public InputSource resolveEntity(
            String publicId, String systemId)
            throws SAXException, IOException {
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

    private void readStream()
            throws XMLStreamException {
        try {
            if (xif == null) {
                xif = XMLInputFactory.newInstance();
                xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
                xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
                xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
                xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
                xif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
                xif.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
                xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
            }
            File f = new File(currentURI);
            xer = xif.createXMLStreamReader(new FileInputStream(f));            
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
                        ||event == XMLStreamConstants.START_ELEMENT) {
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

}
