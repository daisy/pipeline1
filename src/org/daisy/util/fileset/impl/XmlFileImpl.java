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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.xml.XMLUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogException;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogExceptionRecoverable;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.AttributesCloner;
import org.daisy.util.xml.sax.SAXConstants;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

/**
 * @author Markus Gylling
 */

abstract class XmlFileImpl 
	extends 	FilesetFileImpl 
	implements 	XmlFile, EntityResolver, EntityResolver2, ErrorHandler, 
				ContentHandler, LexicalHandler { //, DTDHandler, DeclHandler {
    
    static DocumentBuilderFactory domFactory = null;
    static DocumentBuilder domBuilder = null;    
    
    protected Set mXmlLangValues = new HashSet();						//collected in subclasses... can change that
    
    private boolean isWellformed = true;
    private boolean isDTDValid = true;
    private boolean isDTDValidated = false;
    protected String attrValue = null;									//used by subclasses
    protected String attrName = null;									//used by subclasses
        
    private boolean mDebugMode = false;									//system prop    
    private static Boolean mValidating = null;							//config flag

    private SAXParser mSAXParser = null;								//The parser on loan from the pool
    private static HashMap mSAXParserFeatures = null;					//parser config map (used for pool)
    private static HashMap mSAXParserProperties = null;					//parser config map (used for pool)
    private static SAXParserPool mPool = null;							//static convenience pointer    

    private Attributes mRootElementAttributes = null;					//caught in first startElement call    
    private String mRootElementNsUri = null;							//caught in first startElement call
    private String mRootElementLocalName = null;						//caught in first startElement call
    private String mRootElementqName = null;							//caught in first startElement call
    protected boolean mRootElementReported = false;  					//requires subclasses to do super on StartElement
    
    protected Set mNamespaces = new HashSet(); 							//<QName>, caught in startPrefixMapping
    
    private String mPrologPublicId = null;								//caught by LexicalHandler, regardless of DTD load config
    private String mPrologSystemId = null;								//caught by LexicalHandler, regardless of DTD load config
    private String mPrologEncoding = null;								//caught in setDocumentLocator(Locator)
    private String mPrologXmlVersion = null;							//caught in setDocumentLocator(Locator)

    private Map mIdQNameMap = new HashMap(); 							// <idvalue>,<carrierQname>, populated by subclasses
    
    public static XMLGrammarPoolImpl mGrammarPool = null;
    
    XmlFileImpl(URI uri) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        super(uri, XmlFile.mimeStringConstant);
        initialize();
    }
    
    XmlFileImpl(URI uri, String mimeStringConstant) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        super(uri, mimeStringConstant);
        initialize();
    }
    
    /**
     * Configure the saxparser object, and get it from the pool.
     */
    private void initialize() throws ParserConfigurationException, SAXException {
		if (System.getProperty("org.daisy.debug") != null) {
			mDebugMode = true;			
		}
    	
    	if(mSAXParserFeatures==null) {
    		//first class load 
    		mPool = SAXParserPool.getInstance();
    		//configure maps for the SAXParserPool.    		
    		mSAXParserFeatures = new HashMap();
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_NAMESPACE_PREFIXES, Boolean.TRUE);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_STRING_INTERNING, Boolean.TRUE);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_LEXICAL_HANDLER_PARAMETER_ENTITIES, Boolean.TRUE);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_USE_ENTITY_RESOLVER2, Boolean.TRUE);
    		if(mValidating == null) mValidating = Boolean.valueOf(getValidatingProperty());    		 
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_VALIDATION, mValidating);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_EXTERNAL_GENERAL_ENTITIES, mValidating);
    		mSAXParserFeatures.put(SAXConstants.SAX_FEATURE_EXTERNAL_PARAMETER_ENTITIES, mValidating);
    		mSAXParserFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR, mValidating);
    		mSAXParserFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD, mValidating);
    		mSAXParserProperties = new HashMap();
    		mSAXParserProperties.put(SAXConstants.SAX_PROPERTY_LEXICAL_HANDLER, this);
    					
//			try {
//				Object o = Class.forName("org.apache.xerces.util.XMLGrammarPoolImpl").newInstance();
//				mGrammarPool = (XMLGrammarPoolImpl)o;
//				mGrammarPool = org.daisy.util.xml.pool.XNIGrammarPool.newInstance();
//				
//				Object mGrammarPool = Class.forName("org.daisy.util.xml.pool.XNIGrammarPool").newInstance();
//				mSAXParserProperties.put(SAXConstants.APACHE_PROPERTY_GRAMMAR_POOL, mGrammarPool);
//				//mGrammarPool = (XNIGrammarPool)o;
//			} catch (Exception e) {				
//				e.printStackTrace();
//			}  		    	
    	}	
    	
    	try {
    		mSAXParser = mPool.acquire(mSAXParserFeatures, mSAXParserProperties);    		
    	} catch (PoolException pe) {
    		//a feature or a prop prolly threw a SAXNotSupportedException			
    		if (mDebugMode) {
    			System.out.println("DEBUG: XmlFileImpl#initialize PoolException, removing Apache features");
    		}			
    		//try a second time with only org.xml.sax features put
    		try {
    			mSAXParserFeatures.remove(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR);
    			mSAXParserFeatures.remove(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD);
    			mSAXParser = mPool.acquire(mSAXParserFeatures, mSAXParserProperties);
    		} catch (PoolException pe2) {
        		if (mDebugMode) {
        			System.out.println("DEBUG: XmlFileImpl#initialize PoolException after remove Apache features");
        		}			
        		throw new ParserConfigurationException(pe2.getMessage());
    		}
    	}
    	
    	if(mSAXParser != null) {          
          mSAXParser.getXMLReader().setContentHandler(this);
          mSAXParser.getXMLReader().setEntityResolver(this);
          mSAXParser.getXMLReader().setErrorHandler(this);
    	}    	  
    }
     
    public void parse() throws IOException, SAXException {
    	try{
//    		Grammar[] grammars = mGrammarPool.retrieveInitialGrammarSet(org.apache.xerces.xni.grammars.XMLGrammarDescription.XML_DTD);
//    		for (int i = 0; i < grammars.length; i++) {
//    			System.err.println("-->" + grammars[i].getGrammarDescription().getPublicId());			    			
//    		}	
    		mSAXParser.getXMLReader().parse(this.asInputSource());
    	}finally{
    		try {
				mPool.release(mSAXParser, mSAXParserFeatures, mSAXParserProperties);
			} catch (PoolException e) {
        		if (mDebugMode) {
        			System.out.println("DEBUG: XmlFileImpl#parse PoolException");
        		}
			}
    	}
        isParsed = true;
        if (mValidating.booleanValue())isDTDValidated = true;
    }

    /**
     * ContentHandler impl. Typically, subclasses override this, and are expected to use a super call
     * This class has the responsibility of flipping the mRootElementReported bool.
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	if(!mRootElementReported){
    		mRootElementReported = true;
    		mRootElementAttributes = AttributesCloner.clone(atts);
    		mRootElementLocalName = localName;
    		mRootElementqName = qName;
    		mRootElementNsUri = uri;    	
    	} //if(!isRootElementReported)   	
    }
    
    /*
     * (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)throws SAXException {
    	//add to the namespaces set
    	this.mNamespaces.add(new QName(uri,"",prefix));
    }
    
    /*
     * (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD(String name, String publicId, String systemId)throws SAXException {
//		if(mDebugMode) {
//			System.out.println("DEBUG: XmlFileImpl#startDTD -- LexicalHandler is reporting");
//		}  
        mPrologPublicId = publicId;                           
        mPrologSystemId = systemId;        
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */    
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
    	//call the EntityResolver2 impl
    	return resolveEntity(null, publicId, null, systemId);
    }

    /**
     * EntityResolver2 impl
     */    	
	public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws IOException {		
		//only of LexicalHandler hasnt already set, 
		//this method isnt called if apache load dtd props are off
		if(mPrologPublicId==null)mPrologPublicId = publicId;
		if(mPrologSystemId==null)mPrologSystemId = systemId;
				
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
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
    public void setDocumentLocator(Locator locator) {
		try{
			Locator2 locator2 = (Locator2) locator;		  
			mPrologEncoding = locator2.getEncoding();
			mPrologXmlVersion = locator2.getXMLVersion();		  
		}catch (ClassCastException cce) {
			//we didnt have a SAXParser configured to use Locator2
			if(mDebugMode) {
				System.out.println("DEBUG: XmlFileImpl#setDocumentLocator(Locator) couldnt cast Locator to Locator2");
			}
		}
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
    
	public String getPrologPublicId(){
		return mPrologPublicId;
	}

	public String getPrologSystemId() {
		return mPrologSystemId;
	}
    
	public String getPrologEncoding() {
		return mPrologEncoding;
	}

	public String getPrologXmlVersion() {
		return mPrologXmlVersion;
	}
    
	public String getRootElementNsUri() {
		return mRootElementNsUri;
	}
	
	public String getRootElementLocalName(){
		return mRootElementLocalName;
	}

	public String getRootElementqName(){
		return mRootElementqName;
	}

	public QName getRootElementQName(){
		if(this.getRootElementPrefix()!=null) {
			return new QName(this.mRootElementNsUri,this.mRootElementLocalName,this.getRootElementPrefix());
		}
		return new QName(this.mRootElementNsUri,this.mRootElementLocalName);
	}

	/**
	 * @return the namespace bound prefix of the root element, which is \"\" (ie the empty string)
	 * when the root element is in a default namespace. The return value is null if the root element is 
	 * not namespace bound.
	 */
	private String getRootElementPrefix() {		
		if(null != mRootElementNsUri) {
			//return a 0-n length string
			for (Iterator iter = mNamespaces.iterator(); iter.hasNext();) {
				QName qn = (QName) iter.next();
				if(qn.getNamespaceURI().equals(mRootElementNsUri)) {
					return qn.getPrefix();
				}
			}			
		}
		return null; //no namespace binding
	}
	
	public Attributes getRootElementAttributes(){
		return mRootElementAttributes;
	}
			
    public Collection getXmlLangValues(){
    	return this.mXmlLangValues;
    }

    public Collection getNamespaces(){
    	return this.mNamespaces;
    }
    
	public Map getInlineSchemaURIs() {
		//note: the fact that this is computed after the fact
		//and not directly at startelement means that
		//the schema uris dont end up in the uris collection
		HashMap map = new HashMap();
		Set xsis = XMLUtils.getXSISchemaLocationURIs(mRootElementNsUri, mRootElementLocalName, mRootElementqName, mRootElementAttributes);
		for (Iterator iter = xsis.iterator(); iter.hasNext();) {
			String str = (String) iter.next();
			map.put(str, SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI);							
		}	
		return map;
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
        return mIdQNameMap.containsKey(value);
    }

    public boolean hasIDValueOnQName(String idval, QName qName) {
    	QName test = (QName) mIdQNameMap.get(idval);
        if (test != null) {
        	//TODO does .equals return correct value?
            return qName.equals(test);
        }
        return false;
    }

    protected void putIdAndQName(String idvalue, QName qName) {
        mIdQNameMap.put(idvalue, qName);
    }
    
    /**
     * EntityResolver2 impl
     * Allows applications to provide an external subset for documents that don't explicitly define one. 
     */
	public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
		return null;
	}
    	        
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
    
    private boolean getValidatingProperty() {
        try {
            if (System.getProperty("org.daisy.util.fileset.validating").equals("true")) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
    
    //empty methods: for subclasses to implement as needed     
	public void endDocument() throws SAXException {}
	public void endPrefixMapping(String prefix) throws SAXException {}
	public void endElement(String uri, String localName, String qName) throws SAXException {}
	public void characters(char[] ch, int start, int length) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
    public void skippedEntity(String arg0) throws SAXException {}
    public void startDocument() throws SAXException {}        
    public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {}
    public void unparsedEntityDecl(String arg0, String arg1, String arg2,String arg3) throws SAXException {}
    public void endDTD() throws SAXException {}
    public void startEntity(String name) throws SAXException {}
    public void endEntity(String name) throws SAXException {}
    public void startCDATA() throws SAXException {}
    public void endCDATA() throws SAXException {}
    public void comment(char[] ch, int start, int length) throws SAXException {}
    //methods of ext.DeclHandler
//	public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {}
//	public void elementDecl(String name, String model) throws SAXException {}
//	public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {}
//	public void internalEntityDecl(String name, String value) throws SAXException {}
    //end methods of ext.DeclHandler

}