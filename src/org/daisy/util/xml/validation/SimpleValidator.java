package org.daisy.util.xml.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.xml.XMLUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Linus Ericson
 */
public class SimpleValidator {

	private Map<Source, String> mSchemaSources;
	private ErrorHandler mErrorHandler;
	private EntityResolver mResolver = null;
	private LSResourceResolver mLSResolver = null;
	
	
	public SimpleValidator(Collection<String> schemas, ErrorHandler handler) throws IOException, SAXException {
		mSchemaSources = new HashMap<Source, String>();
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
		// Loop through supplied schemas
		for (String schema : schemas) {
			Map<Source,String> aSchema = this.toSchemaSource(schema, null);
			if (aSchema != null) {
				mSchemaSources.putAll(aSchema);
			} else {
				// FIXME throw could not instantiate schema
			}
		}
	}
	
	/**
	 * Set an EntityResolver. If this method not called, defaults to CatalogEntityResolver
	 */
	public void setResolver(LSResourceResolver resolver) {
		this.mLSResolver = resolver;
	}

	/**
	 * Set an EntityResolver. If this method not called, defaults to CatalogEntityResolver
	 */
	public void setResolver(EntityResolver resolver) {
		this.mResolver = resolver;
	}
	
	public SimpleValidator(String schema, ErrorHandler handler) throws IOException, SAXException {		
		this(toCollection(schema), handler);
	}
	
	private static Collection<String> toCollection(String str) {
		Collection<String> coll = new ArrayList<String>();
		coll.add(str);
		return coll;
	}
	
	public boolean validate(URL url) {
		boolean result = false;
		PeekResult inputFilePeekResult;	
		Peeker peeker = null;
		try {
			try {
				peeker = PeekerPool.getInstance().acquire();
				inputFilePeekResult = peeker.peek(url);
			} finally{
				PeekerPool.getInstance().release(peeker);
			}
		
		
			// Do DTD validation
			if ((inputFilePeekResult != null) 
					&& (inputFilePeekResult.getPrologSystemId()!=null
							||inputFilePeekResult.getPrologPublicId()!=null)){
					doSAXDTDValidation(url);	
			}
			
			
			// Do inline XSD validation?
			if (inputFilePeekResult!=null){
				Set<String> xsis = inputFilePeekResult.getXSISchemaLocationURIs();
				for (String str : xsis) {			
					Map<Source,String> map = toSchemaSource(str,SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI);
					if (map!=null){
						mSchemaSources.putAll(map);
					}//else{
						//this.sendMessage(Level.WARNING,i18n("SCHEMA_INSTANTIATION_FAILURE", str));
					//}																	
				}//for
			}
		
			// Apply schemas	
			result = doJAXPSchemaValidation(url);
		
		} catch (PoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
		
	
	/**
	 * Run a SAXParse with DTD validation turned on.
	 * @throws PoolException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private boolean doSAXDTDValidation(URL url) {
		boolean result = true;
    	Map<String, Boolean> features = new HashMap<String, Boolean>();
    	SAXParser saxParser = null;
	    try{
	    	features.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
	    	features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.TRUE);        	
	    	saxParser = SAXParserPool.getInstance().acquire(features,null);
	    	saxParser.getXMLReader().setErrorHandler(mErrorHandler);    	
	    	saxParser.getXMLReader().setContentHandler(new DefaultHandler());
	    	saxParser.getXMLReader().setEntityResolver((EntityResolver)mResolver);
	    	saxParser.getXMLReader().parse(new InputSource(url.openStream()));	    	
		}catch (Exception e) {
			//this.sendMessage(Level.WARNING,i18n("DTD_VALIDATION_FAILURE", e.getMessage()));	
			result = false;
		}finally{
			try {
				SAXParserPool.getInstance().release(saxParser,features,null);
			} catch (PoolException e) {

			}
		}
		return result;
	}
	
	/**
	 * Attempt to validate the input file using javax.xml.validation against a set of schema Sources
	 */
	private boolean doJAXPSchemaValidation(URL url) {
		boolean result = true;
		HashMap<String,SchemaFactory> factoryMap = new HashMap<String,SchemaFactory>();		//cache to not create multiple identical factories     	     	 
    	SchemaFactory anySchemaFactory = null;	//Schema language neutral jaxp.validation driver

    	for (Source source : mSchemaSources.keySet()) {
			try{				
				String schemaNsURI = mSchemaSources.get(source);
				
				//then do it
				if(!factoryMap.containsKey(schemaNsURI)) {
					factoryMap.put(schemaNsURI,SchemaFactory.newInstance(schemaNsURI));
				}
				anySchemaFactory = factoryMap.get(schemaNsURI);
				anySchemaFactory.setErrorHandler(mErrorHandler);
				anySchemaFactory.setResourceResolver(mLSResolver);
				Schema schema = anySchemaFactory.newSchema(source);													
				Validator jaxpValidator = schema.newValidator();		
				StreamSource ss = new StreamSource(url.openStream());
				ss.setSystemId(url.toExternalForm());
				jaxpValidator.validate(ss);
			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			//	mStateTracker.mHadCaughtException = true;
			//	this.sendMessage(Level.WARNING,i18n("SCHEMA_VALIDATION_FAILURE",source.getSystemId()) + " " +e.getMessage());
			}        
		}
    	return result;
	}
	
	/**
	 * Converts an identifier string into one or several Source objects
	 * @param 
	 * 		identifier a resource identifier consisting of an absolute or relative filespec, or a prolog Public or System Id.
	 * @param
	 * 		schemaLanguageConstant the schema NS URI identifier, if null, this method will attempt to set the value 
	 * @return a Map (Source, NSURI) representing the input resource, null if identification failed
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private Map<Source,String> toSchemaSource(String identifier, String schemaLanguageConstant) throws IOException, SAXException {
		/*
		 * examples of inparams here:
		 *   http://www.example.com/example.dtd
		 *   http://www.example.com/example.rng
		 *   http://www.example.com/example.xsd
		 *   example.dtd 
		 *   example.rng 
		 *   ../stuff/example.rng
		 *   ./stuff/example.rng
		 *   D:/example.sch
		 *   file://D:/example.sch
		 *   -//NISO//DTD dtbook 2005-1//EN
		 */
		 
		Map<Source,String> map = new HashMap<Source,String>();		
		File localSchemaFile = null; 
		URL schemaURL = null;
		identifier = identifier.trim();
		FilesetRegex regex = FilesetRegex.getInstance();
		
		//first try to resolve a physical file		
		boolean isRemote = regex.matches(regex.URI_REMOTE, identifier);
		try{			
			if(!isRemote){
				localSchemaFile = FilenameOrFileURI.toFile(identifier);
				if(localSchemaFile!=null && localSchemaFile.exists()) {
					schemaURL = localSchemaFile.toURI().toURL();
				}
			}
		}catch (Exception e) {
			//carry on
		}
				
		//if physical file resolve didnt work, or isRemote, try catalog
		if(schemaURL == null) {
			//file resolve didnt work above, or its remote try catalog
			URL url = CatalogEntityResolver.getInstance().resolveEntityToURL(identifier);
			if(url!=null){
				schemaURL = url;											
			}
		}
		
		//if catalog didnt work
		if(schemaURL == null) {
			try{
				schemaURL = new URL(identifier);
			}catch (Exception e) {
				/// FIXME this.sendMessage(Level.WARNING, i18n("SCHEMA_INSTANTIATION_FAILURE", identifier));
				return null;
			}	
		}
		
		if(schemaURL != null) {
			//prepare return
		    //set Source
			StreamSource ss = new StreamSource(schemaURL.openStream());
		    ss.setSystemId(schemaURL.toExternalForm());
		    
		    //set schematype
		    String nsuri = null;; 
		    if(schemaLanguageConstant==null) {
		    	//it didnt come as inparam
		    	try{
		    		nsuri = XMLUtils.getSchemaType(schemaURL);
		    		if(nsuri==null) {
		    			// FIXME this.sendMessage(Level.WARNING, i18n("SCHEMA_TYPE_NOT_SUPPORTED", schemaURL.toString()));
		    		}
		    	}catch (Exception e) {
					//mStateTracker.mHadCaughtException = true;
					//FIXME this.sendMessage(Level.WARNING, i18n("SCHEMA_IDENTIFICATION_FAILURE", schemaURL.toString()));
				}			    	
		    }else{
		    	//it came as inparam
		    	nsuri = schemaLanguageConstant;
		    }
		    	    
		    if(nsuri!=null) {
		    	if(nsuri.equals(SchemaLanguageConstants.RELAXNG_NS_URI)) {
		    		SchematronFinder finder = new SchematronFinder();
		    		if (finder.find(schemaURL)) {
			    		//need to check for schematron islands FIXME may occur in XSD as well
			    		//FIXME check first, or make sure this doesnt break when no sch in rng
			    		StreamSource schss = new StreamSource(schemaURL.openStream());
			    	    schss.setSystemId(schemaURL.toExternalForm());
			    	    //FIXME may be ISO schematron, have to check first
			    	    map.put(schss,SchemaLanguageConstants.SCHEMATRON_NS_URI);
		    		}
		    	}	    		    	
		    	map.put(ss,nsuri);
		    	return map;
		    }
		}
	    return null;
	}
	
	private class SchematronFinder extends DefaultHandler {
		
		private boolean schematronFound = false;

		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
			return CatalogEntityResolver.getInstance().resolveEntity(publicId, systemId);
		}

		@Override
		public void startElement(String uri, String local, String qName, Attributes attributes) throws SAXException {
			if (SchemaLanguageConstants.SCHEMATRON_NS_URI.equals(uri)) {
				schematronFound = true;				
			}
		}
		
		public boolean find(URL url) throws SAXException, IOException {
			Map<String,Boolean> features = new HashMap<String, Boolean>();
			features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.FALSE);
			features.put(SAXConstants.SAX_FEATURE_EXTERNAL_GENERAL_ENTITIES, Boolean.FALSE);
			features.put(SAXConstants.SAX_FEATURE_EXTERNAL_PARAMETER_ENTITIES, Boolean.FALSE);
			features.put(SAXConstants.SAX_FEATURE_LEXICAL_HANDLER_PARAMETER_ENTITIES, Boolean.TRUE);
			features.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
			features.put(SAXConstants.SAX_FEATURE_NAMESPACE_PREFIXES, Boolean.TRUE);
			SAXParserPool pool = SAXParserPool.getInstance();
			SAXParser parser = null;
			try {
				parser = pool.acquire(features, null);
				parser.getXMLReader().setContentHandler(this);
				parser.getXMLReader().setEntityResolver(this);
				parser.getXMLReader().setDTDHandler(this);
				parser.getXMLReader().setErrorHandler(this);
				parser.parse(url.openStream(), this);
			} catch (PoolException e) {
				e.printStackTrace();
			} finally {
				try {
					pool.release(parser, features, null);
				} catch (PoolException e) {					
				}
			}
			return schematronFound;
		}
		
	}
	
}
