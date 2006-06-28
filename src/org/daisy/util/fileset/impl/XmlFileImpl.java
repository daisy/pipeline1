package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogException;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogExceptionRecoverable;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author Markus Gylling
 */

abstract class XmlFileImpl extends FilesetFileImpl implements XmlFile,
        EntityResolver,ErrorHandler, ContentHandler, DTDHandler,
        LexicalHandler, DeclHandler {
    static SAXParserFactory saxFactory;
    static SAXParser saxParser;
    static DocumentBuilderFactory domFactory = null;
    static DocumentBuilder domBuilder = null;    
    private Map idMap = new HashMap(); // <idvalue>,<carrierQname>
    protected Set xmlLangValues = new HashSet();
    private boolean isWellformed = true;
    private boolean isDTDValid = true;
    private boolean isDTDValidated = false;
    protected String attrValue;
    protected String attrName;

    XmlFileImpl(URI uri) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        super(uri, XmlFile.mimeStringConstant);
        initialize();
    }
    
    XmlFileImpl(URI uri, String mimeStringConstant) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        super(uri, mimeStringConstant);
        initialize();
    }
    
    private void initialize() throws ParserConfigurationException, SAXException  {
        if (saxFactory == null) {
        	        	
    		System.setProperty("org.apache.xerces.xni.parser.Configuration", 
    				"org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
        	
            saxFactory = SAXParserFactory.newInstance();
            saxFactory.setValidating(getValidatingProperty());
            saxFactory.setNamespaceAware(true);
            		
            saxParser = saxFactory.newSAXParser();
            
            //if string interning fails, non of the startElement algos will work, therefore throw
            saxFactory.setFeature("http://xml.org/sax/features/string-interning", true);
                        
            // setFeatures if nonvalidating
            if (!saxFactory.isValidating()) {
                try {
                    saxFactory.setFeature("http://xml.org/sax/features/validation", false);
                    saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",false);
                    saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
                } catch (Exception e) {
                	//dont throw if these fails
                	myExceptions.add(e);
                }
            }       
            
        }
        
        saxParser.getXMLReader().setContentHandler(this);
        saxParser.getXMLReader().setEntityResolver(this);
        saxParser.getXMLReader().setDTDHandler(this);
        saxParser.getXMLReader().setErrorHandler(this);
        
    }

    private boolean getValidatingProperty() {
        try {
            if (System.getProperty("org.daisy.util.fileset.validating").equals("true")) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public void parse() throws IOException, SAXException {
        saxParser.getXMLReader().parse(this.asInputSource());
        isParsed = true;
        if (saxFactory.isValidating())isDTDValidated = true;
    }

    public Collection getXmlLangValues(){
    	return this.xmlLangValues;
    }
    
    public boolean isWellformed() throws IllegalStateException {
        if (isParsed)return isWellformed;
        throw new IllegalStateException("Property not set: file not parsed");
    }

    public boolean isDTDValid() throws IllegalStateException {
        if (isDTDValidated) return isDTDValid;
        throw new IllegalStateException("Property not set: file not validated");
    }

    public boolean isDTDValidated() {
        return isDTDValidated;
    }

    public boolean hasIDValue(String value) {
        return idMap.containsKey(value);
    }

    public boolean hasIDValueOnQName(String idval, QName qName) {
    	QName test = (QName) idMap.get(idval);
        if (test != null) {
        	//TODO does .equals return correct value?
            return qName.equals(test);
        }
        return false;
    }

    public void fatalError(SAXParseException spe) throws SAXException {
        isWellformed = false;
        myExceptions.add(new FilesetFileFatalErrorException(this,spe));
    }

    public void error(SAXParseException spe)throws SAXException {
        isDTDValid = false;
        myExceptions.add(new FilesetFileErrorException(this,spe));
    }

    public void warning(SAXParseException spe) throws SAXException {
        myExceptions.add(new FilesetFileWarningException(this,spe));
    }

    protected void putIdAndQName(String idvalue, QName qName) {
        idMap.put(idvalue, qName);
    }

    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
    	//putUriValue(systemId); //reports 'wrong' systemId for entities etc
        try {
            return CatalogEntityResolver.getInstance().resolveEntity(publicId,systemId);
        } catch (CatalogException ce) {
            if (ce instanceof CatalogExceptionRecoverable) {
            	//dont throw
            	myExceptions.add(new FilesetFileWarningException(this,ce));
            } else if (ce instanceof CatalogExceptionNotRecoverable) {
                throw new IOException(ce.getMessage());
            }
        }
        return null;
    }
    
    
//    public InputSource resolveEntity (String name,String publicId,String baseURI, String systemId) throws SAXException, IOException {
//    	System.err.println("stop");
//    	return null;
//    }
        
        
    public void processingInstruction(String target, String data) throws SAXException {
        if (target.equals("xml-stylesheet")) { 
        	//see: http://www.w3.org/TR/xml-stylesheet/
            String content[] = data.split(" ");
            for (int i = 0; i < content.length; i++) {
                if (content[i].startsWith("href")) {
                    char doublequote = '"';
                    char singlequote = '\'';
                    String value = content[i].replace(singlequote, doublequote);
                    try {
                        value = value.substring(value.indexOf(doublequote) + 1,
                                value.lastIndexOf(doublequote));
                    } catch (Exception e) {
                        String message = "could not grok processing instruction"
                                + target + " " + data + " in " + this.getName();
                        this.error(new SAXParseException(message, null));
                    }
                    this.putUriValue(value);
                }
            }
        } else {
            String message = "did not recognize processing instruction"
                    + target + " " + data + " in " + this.getName();
            this.warning(new SAXParseException(message, null));
        }
    }

    public Document asDocument(boolean namespaceAware) throws ParserConfigurationException, SAXException, IOException {
        if (domFactory == null) {
            domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(namespaceAware);
            domFactory.setValidating(false);
            try {
                domFactory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion",true);
            } catch (Exception e) {
                System.err.println("could not set all features on domFactory");
            }
            domBuilder = domFactory.newDocumentBuilder();
        }
        domBuilder.setEntityResolver(this);
        domBuilder.setErrorHandler(this);        
        return domBuilder.parse(this);
    }

    public DOMSource asDOMSource() throws ParserConfigurationException, SAXException, IOException {
    	DOMSource ds = new DOMSource(this.asDocument(true));
    	ds.setSystemId(this.toString());
        return ds;
    }

    public SAXSource asSAXSource() throws FileNotFoundException {
        return new SAXSource(this.asInputSource());
    }

    public StreamSource asStreamSource() throws FileNotFoundException {
    	StreamSource ss =  new StreamSource(new FileReader(this));
    	ss.setSystemId(this);
        return ss;
    }

    //empty methods: for subclasses to implement as needed 
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {}
	public void endDocument() throws SAXException {}
	public void endPrefixMapping(String prefix) throws SAXException {}
	public void endElement(String uri, String localName, String qName) throws SAXException {}
	public void characters(char[] ch, int start, int length) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
    public void setDocumentLocator(Locator arg0) {}
    public void skippedEntity(String arg0) throws SAXException {}
    public void startDocument() throws SAXException {}
    public void startPrefixMapping(String arg0, String arg1)throws SAXException {}
    public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {}
    public void unparsedEntityDecl(String arg0, String arg1, String arg2,String arg3) throws SAXException {}
    public void startDTD(String name, String publicId, String systemId)throws SAXException {}
    public void endDTD() throws SAXException {}
    public void startEntity(String name) throws SAXException {}
    public void endEntity(String name) throws SAXException {}
    public void startCDATA() throws SAXException {}
    public void endCDATA() throws SAXException {}
    public void comment(char[] ch, int start, int length) throws SAXException {}
    //methods of ext.DeclHandler
	public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {}
	public void elementDecl(String name, String model) throws SAXException {}
	public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {}
	public void internalEntityDecl(String name, String value) throws SAXException {}
    //end methods of ext.DeclHandler
}