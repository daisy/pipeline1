package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.daisy.util.xml.dtdcatalog.CatalogEntityResolver;
import org.daisy.util.xml.dtdcatalog.CatalogException;
import org.daisy.util.xml.dtdcatalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.dtdcatalog.CatalogExceptionRecoverable;
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
 * @author Markus Gylling
 */
abstract class XmlFileImpl extends FilesetFileImpl implements XmlFile, EntityResolver, ErrorHandler, ContentHandler, DTDHandler {        
	static SAXParserFactory factory;
	static SAXParser parser;
	protected HashSet myIDValues = new HashSet();
	protected URI cache = null; //for optimization in loops
		
	XmlFileImpl(URI uri) throws ParserConfigurationException, SAXException { 
		super(uri);
		if (this.exists() && this.canRead()){
			if (factory==null) {
				//only do this first time on the static factory
				factory = SAXParserFactory.newInstance();		
				factory.setValidating(FilesetObserver.getInstance().getCurrentListener().doDTDValidation());
				factory.setNamespaceAware(true);				
			}   									
			parser = factory.newSAXParser();									
			parser.getXMLReader().setContentHandler(this);
			parser.getXMLReader().setErrorHandler(this);
			parser.getXMLReader().setEntityResolver(this);
			parser.getXMLReader().setDTDHandler(this);		
			
//			try {
//				parser.getXMLReader().parse(new InputSource(this.getAbsolutePath()));
//			} catch (IOException ioe){
//				FilesetObserver.getInstance().errorEvent(this.toURI(), ioe);
//			} catch (SAXException se) {
//				FilesetObserver.getInstance().errorEvent(this.toURI(), se);
//			}
			
		} //(this.exists() && this.canRead()) --> else parent AbstractFile already reported nonexistance or notreadable
	}
	
	protected void parse() {	
		if (this.exists() && this.canRead()){
		try {
			parser.getXMLReader().parse(new InputSource(this.getAbsolutePath()));
		} catch (IOException ioe){
			FilesetObserver.getInstance().errorEvent(ioe);
		} catch (SAXException se) {
			FilesetObserver.getInstance().errorEvent(se);
		}
		}
	}
	
	public boolean hasIDValue(String value) {
		return myIDValues.contains(value);
	}
	
	public void fatalError(SAXParseException spe) throws SAXException {
		FilesetObserver.getInstance().errorEvent(spe);	
	}
	
	public void error(SAXParseException spe) throws SAXException {
		FilesetObserver.getInstance().errorEvent(spe);
	}
	
	public void warning(SAXParseException spe) throws SAXException {
		//dont populate error map with warnings
		//FilesetObserver.getInstance().errorEvent(this.toURI(),spe);
	}
	
	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		//TODO handle local and remote sets here
		//		System.err.println(publicId);
		//		System.err.println(systemId);
		//		System.err.println("...");
		
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
						FilesetObserver.getInstance().errorEvent(e);
					}
					if (!matches(Regex.getInstance().URI_REMOTE,value)) {
						putLocalURI(value);
						URI uri = resolveURI(value);							
						Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
						if (o!=null) {
							//already added to listener fileset, so only put to local references collection
							putReferencedMember(uri, o);
						}else{
							try {
								if (matches(Regex.getInstance().FILE_CSS,value)) {
									putReferencedMember(uri, new CssFileImpl(uri));	
								}else if (matches(Regex.getInstance().FILE_XSL,value)) {
									putReferencedMember(uri, new XslFileImpl(uri));	
								}														
							} catch (Exception e) {
								throw new SAXException(e);
							} 
						}				  					  					  	
					}else{
						putRemoteURI(value);				  	
					}				  				  
				}				
			}						
		}else{
			FilesetObserver.getInstance().errorEvent(new FilesetExceptionRecoverable("unsupported processingInstruction" + target + " encountered in" + this.getName()));
		}				
	}
	
	public void setDocumentLocator(Locator arg0) {}
	
	public void skippedEntity(String arg0) throws SAXException {}
	
	public void startDocument() throws SAXException {}
	
	public void startPrefixMapping(String arg0, String arg1)throws SAXException {}
	
	public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {}
	
	public void unparsedEntityDecl(String arg0, String arg1, String arg2, String arg3) throws SAXException {}
	
}
