package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

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
import org.xml.sax.ext.LexicalHandler;


/**
 * @author Markus Gylling
 */

abstract class XmlFileImpl extends FilesetFileImpl implements XmlFile, EntityResolver, ErrorHandler, ContentHandler, DTDHandler, LexicalHandler {        
	static SAXParserFactory saxFactory;
	static SAXParser saxParser;
	
	static DocumentBuilderFactory domFactory = null;
	static DocumentBuilder domBuilder = null;
	
	private Map idMap = new HashMap(); //<idvalue>,<carrierQname>
	protected ErrorHandler listeningErrorHandler = null;
	
	private boolean isWellformed = true;
	private boolean isDTDValid = true;
	private boolean isDTDValidated = false;
	private boolean isParsed = false;
	
	protected String attrValue;
	protected String attrName;
	
	
	
	XmlFileImpl(URI uri) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException { 
		super(uri);	
		initialize();
	}
	
	XmlFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		if (errh instanceof FilesetErrorHandler) {
		  this.listeningErrorHandler = (FilesetErrorHandler)errh;
		}  else {
			this.listeningErrorHandler = errh;
		}
		initialize();
	}
	
	private void initialize() throws ParserConfigurationException, SAXException {
		if (saxFactory==null) {
			saxFactory = SAXParserFactory.newInstance();	
			saxFactory.setValidating(getValidatingProperty());			
			saxFactory.setNamespaceAware(true);
			
			saxParser = saxFactory.newSAXParser();
			try {
				saxFactory.setFeature("http://xml.org/sax/features/string-interning",true);	
			} catch (Exception e) {
				System.err.println("string-interning setfeature failed in xmlfileimpl");
			}
			
			//setFeatures if nonvalidating
			if(!saxFactory.isValidating()) {
				try {
					saxFactory.setFeature("http://xml.org/sax/features/validation", false);
					saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
					saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);							
				} catch (Exception e) {
					 System.err.println("sax setfeature failed in xmlfileimpl");
				}
			}
		}   
		saxParser.getXMLReader().setContentHandler(this);
		saxParser.getXMLReader().setEntityResolver(this);		
		saxParser.getXMLReader().setDTDHandler(this);		
		saxParser.getXMLReader().setErrorHandler(this);	
		
		//System.err.println(saxParser.getClass().getCanonicalName());
		
		//************** debug ***************		
		//System.err.println(parser.getProperty("http://apache.org/xml/properties/input-buffer-size"));
		//System.err.println(parser.getProperty("http://apache.org/xml/properties/security-manager"));
		//parser.getXMLReader().setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(2048));
		//************* end debug **************
		
	}
	
	private boolean getValidatingProperty() {		
		try {		  
		  if (System.getProperty("org.daisy.util.fileset.validating").equals("true")) {
		  	//System.err.println("validation on");
		  	return true;
		  }
		}catch (Exception e) {
		
		}  
		//System.err.println("validation off");
		return false;
	}
	
	public void parse() throws IOException, SAXParseException, SAXException {
		saxParser.getXMLReader().parse(new InputSource(this.getAbsolutePath()));
		isParsed = true;
		if (saxFactory.isValidating()) isDTDValidated = true;
	}
	
	public boolean isWellformed() throws FilesetException {
		if (isParsed) {
			return isWellformed;
		}else{
			throw new FilesetException("Property not set: file not parsed");
		}		
	}
	
	public boolean isDTDValid() throws FilesetException {
		if (isDTDValidated) {
			return isDTDValid;
		}else{
			throw new FilesetException("Property not set: file not validated");
		}				
	}
	
	public boolean isParsed() {
		return isParsed;
	}
	
	public boolean isDTDValidated() {
		return isDTDValidated;
	}
		
//	public boolean hasIDValue(String value) {
//		return idValues.contains(value);
//	}
	
	public boolean hasIDValue(String value) {
		return idMap.containsKey(value);
	}

	public boolean hasIDValueOnQName(String idval, String QName) {
		String test = (String)idMap.get(idval);
		if(test!=null) {
			return QName.equals(test);
		}		
		return false;
	}
	
	public void fatalError(SAXParseException spe) throws SAXException {		
		isWellformed=false;
		if (this.listeningErrorHandler != null) {
			this.listeningErrorHandler.fatalError(spe);
		}else{
			printMessage("Fatal error", spe);
		}		
	}
	
	public void error(SAXParseException spe) throws SAXException {		
		isDTDValid=false;
		if (this.listeningErrorHandler != null) {
			this.listeningErrorHandler.error(spe);
		}else{
			printMessage("Error", spe);
		}		
	}
	
	public void warning(SAXParseException spe) throws SAXException {		
		if (this.listeningErrorHandler != null) {
			this.listeningErrorHandler.warning(spe);
		}else{
			printMessage("Warning", spe);
		}
		
	}
	
	private void printMessage(String type,SAXParseException spe) {
		StringBuffer sb = new StringBuffer();
		sb.append(type);
		sb.append(" in ");
		sb.append(spe.getSystemId());
		sb.append(": ");
		sb.append(spe.getMessage());
		sb.append(". Line:" + spe.getLineNumber());
		sb.append(" Column:" + spe.getColumnNumber());
		System.err.println(sb.toString());
	}
	
//	protected void putIdValue(String idvalue) {
//		idValues.add(idvalue);
//	}
	
	protected void putIdAndQName(String idvalue, String qName) {
		idMap.put(idvalue,qName);		
	}
	
	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		//TODO handle local and remote sets		
		//redirect to the 202 subset DTDs 
		if(!publicId.startsWith("-//W3C//ENTITIES")){			
			//redirect to 202 dtds
			if (this instanceof D202NccFile) {
				publicId = "-//DAISY//DTD ncc v2.02//EN";
			}else if (this instanceof D202TextualContentFile) {
				publicId = "-//DAISY//DTD content v2.02//EN";
			}else if (this instanceof D202SmilFile) {				
				publicId = "-//DAISY//DTD smil v2.02//EN";
			}else if (this instanceof D202MasterSmilFile) {				
				publicId = "-//DAISY//DTD msmil v2.02//EN";
			}	
		}
		
		//resolve from catalog 
		try {
			return CatalogEntityResolver.getInstance().resolveEntity(publicId, systemId);
		} catch (CatalogException ce) {
			if (ce instanceof CatalogExceptionRecoverable) {
				System.err.println(ce.getMessage());                      
			} else if (ce instanceof CatalogExceptionNotRecoverable) {
				throw new IOException(ce.getMessage());
			}
		}
		return null;
	}
	
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {}
	
	public void endDocument() throws SAXException {}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {}
	
	public void endPrefixMapping(String arg0) throws SAXException {}
	
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {}
	
	public void processingInstruction(String target, String data) throws SAXException {		
		if (target.equals("xml-stylesheet")) { //see: http://www.w3.org/TR/xml-stylesheet/			
			String content[] = data.split(" ");
			for(int i=0;i<content.length;i++){
				if(content[i].startsWith("href")) {
					char doublequote = '"';
					char singlequote = '\'';
					String value = content[i].replace(singlequote,doublequote);
					try {
						value = value.substring(value.indexOf(doublequote)+1,value.lastIndexOf(doublequote));
					} catch (Exception e) {
						String message = "could not grok processing instruction" + target + " " + data + " in " + this.getName();						
						this.error(new SAXParseException(message,null));
					}
					this.putUriValue(value);
				}				
			}						
		}else{
			String message = "did not recognize processing instruction" + target + " " + data + " in " + this.getName();
			this.error(new SAXParseException(message,null));															
		}				
	}
	//	
	public void setDocumentLocator(Locator arg0) {}
	
	public void skippedEntity(String arg0) throws SAXException {}
	
	public void startDocument() throws SAXException {}
	
	public void startPrefixMapping(String arg0, String arg1)throws SAXException {}
	
	public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {}
	
	public void unparsedEntityDecl(String arg0, String arg1, String arg2, String arg3) throws SAXException {}
	
	public void startDTD(String name, String publicId, String systemId) throws SAXException {}
	
	public void endDTD() throws SAXException {}
	
	public void startEntity(String name) throws SAXException {}
	
	public void endEntity(String name) throws SAXException {}
	
	public void startCDATA() throws SAXException {}
	
	public void endCDATA() throws SAXException {}
	
	public void comment(char[] ch, int start, int length) throws SAXException {}
	
	public Document asDocument() throws ParserConfigurationException, SAXException, IOException {
		if (domFactory == null) {
			domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
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
	
	public InputSource asInputSource() throws FileNotFoundException {
		return new InputSource(new FileReader(this));
	}
	
	public DOMSource asDOMSource() throws ParserConfigurationException, SAXException, IOException {
		return new DOMSource(this.asDocument());
	}
	
	public SAXSource asSAXSource() throws FileNotFoundException {
		return new SAXSource(this.asInputSource());
	}
	
	public StreamSource asStreamSource() throws FileNotFoundException {
		return new StreamSource(new FileReader(this));
	}
		
}
