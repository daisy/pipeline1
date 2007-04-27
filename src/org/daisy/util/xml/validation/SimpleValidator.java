package org.daisy.util.xml.validation;

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
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple validator using the <code>org.daisy.util.xml.validation.jaxp</code> APIs.
 * @author Linus Ericson
 * @author Markus Gylling
 */
public class SimpleValidator {

	private Map<Source, String> mSchemaSources;
	private ErrorHandler mErrorHandler;
	private EntityResolver mResolver = null;
	private LSResourceResolver mLSResolver = null;
	
	/**
	 * Constructor. Use this to validate resources against inline schemas and additional schema resources given as param.
	 * @param schemas A collection of schema identifiers 
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator(Collection<String> schemas, ErrorHandler handler) throws SAXException, TransformerException {
		mSchemaSources = new HashMap<Source, String>();
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
		// Loop through supplied schemas
		for (String schema : schemas) {
			Map<Source,String> map = ValidationUtils.toSchemaSources(schema);
			mSchemaSources.putAll(map);
		}
	}

	
	/**
	 * Constructor. Use this to validate resources against inline schemas and additional schema resources given as param.
	 * @param schemas A collection of schema URLs 
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator(ErrorHandler handler, Collection<URL> schemas) throws SAXException, TransformerException {
		mSchemaSources = new HashMap<Source, String>();
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
		// Loop through supplied schemas
		for (URL schema : schemas) {
			Map<Source,String> map = ValidationUtils.toSchemaSources(schema);
			mSchemaSources.putAll(map);
		}
	}
	
	/**
	 * Constructor. Use this to validate resources against inline schemas and an additional schema resource given as param.
	 * @param schema A schema identifier (PID, SID, pathspec, URL)
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator(String schema, ErrorHandler handler) throws SAXException, TransformerException {		
		this(toCollection(schema), handler);
	}

	/**
	 * Constructor. Use this to validate resources against inline schemas and an additional schema resource given as param.
	 * @param schema A schema identifier (PID, SID, pathspec, URL)
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator(URL schema, ErrorHandler handler) throws SAXException, TransformerException {		
		this(handler, toCollection(schema));
	}
	
	/**
	 * Constructor. Use this to validate resources against inline schemas only.
	 * @param schema A schema identifier (PID, SID, pathspec, URL)
	 * @param handler ErrorHandler to report validation errors to
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public SimpleValidator(ErrorHandler handler) throws SAXException {		
		mErrorHandler = handler;
		mResolver = CatalogEntityResolver.getInstance();
		mLSResolver = CatalogEntityResolver.getInstance();
	}
	
	/**
	 * Validate a resource.
	 * <p>Validation will include inline DTD and XSD schemas if available, and any schemas set in constructor.</p>
	 */
	public boolean validate(URL url) throws ValidationException {
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
				
			// Do DTD validation?
			if ((inputFilePeekResult != null) 
					&& (inputFilePeekResult.getPrologSystemId()!=null
							||inputFilePeekResult.getPrologPublicId()!=null)){
					doSAXDTDValidation(url);	
			}
						
			// Do inline XSD validation?
			if (inputFilePeekResult!=null){
				Set<String> xsis = inputFilePeekResult.getXSISchemaLocationURIs();
				for (String str : xsis) {			
					Map<Source,String> map = ValidationUtils.toSchemaSources(str);					
					mSchemaSources.putAll(map);
				}
			}
		
			// Apply schemas	
			result = doJAXPSchemaValidation(url);
		
		} catch (Exception e) {
			throw new ValidationException(e.getMessage(),e);
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
	    	saxParser.getXMLReader().setEntityResolver(mResolver);
	    	saxParser.getXMLReader().parse(new InputSource(url.openStream()));	    	
		}catch (Exception e) {
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
		if(mSchemaSources==null||mSchemaSources.isEmpty()) return result;
		
		HashMap<String,SchemaFactory> factoryMap = new HashMap<String,SchemaFactory>();		//cache to not create multiple identical factories     	     	 
    	SchemaFactory anySchemaFactory = null;												//Schema language neutral jaxp.validation driver

    	for (Source source : mSchemaSources.keySet()) {
			try{				
				String schemaNsURI = mSchemaSources.get(source);				
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
			}        
		}
    	return result;
	}
		
	/**
	 * Set an LSResolver. If this method not called, the SimpleValidator instance defaults to CatalogEntityResolver
	 */
	public void setResolver(LSResourceResolver resolver) {
		this.mLSResolver = resolver;
	}

	/**
	 * Set an EntityResolver. If this method not called, the SimpleValidator instance defaults to CatalogEntityResolver
	 */
	public void setResolver(EntityResolver resolver) {
		this.mResolver = resolver;
	}
	
	private static Collection<String> toCollection(String str) {
		Collection<String> coll = new ArrayList<String>();
		coll.add(str);
		return coll;
	}

	private static Collection<URL> toCollection(URL url) {
		Collection<URL> coll = new ArrayList<URL>();
		coll.add(url);
		return coll;
	}
}