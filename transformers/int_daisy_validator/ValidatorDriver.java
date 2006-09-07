/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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

package int_daisy_validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.file.EFile;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetTypeNotSupportedException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorFactory;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.util.xml.XMLUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Markus Gylling
 */
public class ValidatorDriver extends Transformer implements FilesetErrorHandler, ValidatorListener, ErrorHandler {
	private EFile mInputFile = null;									//from paramaters
	private Fileset mInputFileset = null;								//based in inputfile
	private PeekResult mInputFilePeekResult = null;						//a global peek on inputfile
	private Map mSchemaSources = new HashMap();  						//<Source>,<SchemaNSURI> 
	private HashSet mValidatorMessageCache = new HashSet();				//to avoid identical messages
	private FilesetRegex mRegex = FilesetRegex.getInstance();			//convenience shortcut
	private StateTracker mStateTracker = new StateTracker();			//inner class
	private CompletionTracker mCompletionTracker 
		= new CompletionTracker();										//inner class
		
	/**
	 * Constructor.
	 */
    public ValidatorDriver(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);        
        //System.setProperty("org.daisy.debug", "true");
        //System.clearProperty("org.daisy.debug");
        checkSystemProperties();        
    }
    
	protected boolean execute(Map parameters) throws TransformerRunException {
		
		/*
		 * Try to create a Fileset instance on the input file 
		 * 	with DTD validation turned on.
		 * If Fileset can represent this type of fileset,
		 * 	check if fileset.validator.ValidatorFactory can produce
		 * 	a Validator for the type of fileset.
		 * If it can produce a validator, 
		 *  run a fileset validation.
		 * If a fileset instance could not be created,
		 *   and if input is xml and has a DTD (prolog identifiers), DTD validate.  
		 * If additional schema resources were supplied as inparam,
		 * or if inline (non-DTD) schemas were present,
		 *   attempt an anonymous jaxp.validation run
		 *   using javax.xml.validation.SchemaFactory 
		 * Inform the user on what kind of validation was actually done,
		 * and what the result was.     
		 */
		
		long start = System.nanoTime();
		
		try{			
			mInputFile = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));			

			Peeker peeker = null;
			try{
				peeker = PeekerPool.getInstance().acquire();
				mInputFilePeekResult = peeker.peek(mInputFile);
			}catch (Exception e) {
				//input isnt XML, malformed at root, or something else went wrong
				//A fileset manifest can be non-XML so be silent, try to continue
			}finally{
				PeekerPool.getInstance().release(peeker);
			}
			
			try{
				mInputFileset = new FilesetImpl(mInputFile.toURI(),this,true,false);
				mCompletionTracker.mCompletedFilesetInstantiation = true;
				ValidatorFactory validatorFactory = ValidatorFactory.newInstance(); 
				try{
					org.daisy.util.fileset.validation.Validator filesetValidator = 
						validatorFactory.newValidator(mInputFileset.getFilesetType());					
					filesetValidator.setReportListener(this);					
					this.sendMessage(Level.INFO, i18n("VALIDATING_FILESET", mInputFileset.getFilesetType().toNiceNameString()));
					filesetValidator.validate(mInputFileset);	
					mCompletionTracker.mCompletedFilesetValidation = true;
				}catch (ValidatorNotSupportedException e) {
					//the factory could not produce a validator for this fileset type
					this.sendMessage(Level.INFO, i18n("NO_FILESET_VALIDATOR", mInputFileset.getFilesetType().toNiceNameString()));					
				}catch (ValidatorException ve) {
					//another error than nonsupported type occured
					mStateTracker.mHadCaughtException = true;
					this.sendMessage(Level.INFO, i18n("FILESET_VALIDATION_FAILURE", ve.getMessage()));
				}
			}catch (FilesetTypeNotSupportedException e) {
				//org.daisy.util.fileset did not recognize the input type
				this.sendMessage(Level.INFO, i18n("NO_FILESET_SUPPORT", mInputFile.getName()));	
				//since no fileset, no dtd validation yet
				if((mInputFilePeekResult != null) 
						&& (mInputFilePeekResult.getPrologSystemId()!=null
								||mInputFilePeekResult.getPrologPublicId()!=null)){
					    this.sendMessage(Level.INFO, i18n("SAX_DTD_VAL"));
						doSAXDTDValidation();		
						mCompletionTracker.mCompletedInlineDTDValidation = true;
				}
			}	
															
			if((!(mSchemaSources = setSchemaSources(parameters)).isEmpty()) 
					&& (mInputFilePeekResult != null)) {
				this.sendMessage(Level.INFO, i18n("JAXP_SCHEMA_VAL", Integer.toString(mSchemaSources.size())));
				doJAXPSchemaValidation();
				mCompletionTracker.mCompletedJAXPSchemaValidation = true;
			}
			
		}catch (Exception e) {
			//something happened that we are not yet handling gracefully
			throw new TransformerRunException(e.getMessage(),e);
		}
		
		long end = System.nanoTime();
		
		/*
		 * finally, check the result state and completion situation.
		 * based primarily on inparams, select an appropriate exit strategy.
		 */
		
		boolean abortOnException = ((String)parameters.remove("abortOnException")).equals("true");
		String abortThreshold = (String)parameters.remove("abortThreshold");
		
		if(abortOnException && mStateTracker.mHadCaughtException) {
			throw new TransformerRunException(i18n("ABORTING_EXCEPTIONS_OCCURED"));
		}

		if(!mCompletionTracker.completedAnyProcess()) {
			throw new TransformerRunException(i18n("ABORTING_NO_VALIDATION_PERFORMED"));
		}

		if(mStateTracker.thresholdBreached(abortThreshold)) {
			throw new TransformerRunException(i18n("ABORTING_THRESHOLD_BREACHED"));			
		}
		
		//else, we are about to exit gracefully. Give some info.
		
		if(mCompletionTracker.mCompletedFilesetValidation) {
			this.sendMessage(Level.INFO,i18n("COMPLETED_FILESET_VALIDATION"));	
		}else{
			if(mCompletionTracker.mCompletedFilesetInstantiation) {
				this.sendMessage(Level.INFO,i18n("COMPLETED_FILESET_INSTANTIATION"));
			}else{
				if(mCompletionTracker.mCompletedInlineDTDValidation) {
					this.sendMessage(Level.INFO,i18n("COMPLETED_DTD_VALIDATION"));	
				}
			}
		}
		
		if(mCompletionTracker.mCompletedJAXPSchemaValidation) {
			this.sendMessage(Level.INFO,i18n("COMPLETED_JAXP_VALIDATION"));
		}
				
		this.sendMessage(Level.INFO,i18n("DURATION", Double.toString((end-start)/1000000000)));
		this.sendMessage(Level.INFO,i18n("MESSAGES_FROM_VALIDATOR",Integer.toString(mValidatorMessageCache.size())));
		if(mValidatorMessageCache.size()==0) this.sendMessage(Level.INFO,i18n("CONGRATS"));		
		return true;
	}

	/**
	 * Collects all schemas that the input document should be validated against;
	 * the schemas may occur inlined in input document, or in the schemas inparam.
	 * @return a map of schemas (Source,SchemaNSURI).  
	 * @throws SAXException 
	 * @throws IOException 
	 */
	private Map setSchemaSources(Map parameters) throws IOException, SAXException {
		
		//get schemas from inparams
		String schemas = (String)parameters.remove("schemas");
		if(schemas!=null && schemas.length()>0) {
			String[] array = schemas.split(",");
			for (int i = 0; i < array.length; i++) {
				try{
					Map aSchema = toSchemaSource(array[i],null);
					if(aSchema!=null) {
						mSchemaSources.putAll(aSchema);
					}else{
						this.sendMessage(Level.WARNING,i18n("SCHEMA_INSTANTIATION_FAILURE", array[i]));							
					}
				}catch (Exception e) {
					mStateTracker.mHadCaughtException = true;
					this.sendMessage(Level.WARNING, i18n("SCHEMA_INSTANTIATION_FAILURE", array[i]) + e.getMessage());
				}
			}						
		}//if(schemas!=null && schemas.length()>0)
		
		//get schemas from doc inline
		if(mInputFilePeekResult!=null){
			Set xsis = mInputFilePeekResult.getXSISchemaLocationURIs();
			for (Iterator iter = xsis.iterator(); iter.hasNext();) {
				String str = (String) iter.next();						
				Map map = toSchemaSource(str,SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI);
				if (map!=null){
					mSchemaSources.putAll(map);
				}else{
					this.sendMessage(Level.WARNING,i18n("SCHEMA_INSTANTIATION_FAILURE", str));
				}																	
			}//for
		}
		return mSchemaSources;
	}

	/**
	 * Run a SAXParse with DTD validation turned on.
	 * @throws PoolException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void doSAXDTDValidation() {
		
    	Map features = new HashMap();
    	SAXParser saxParser = null;
	    try{
	    	features.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
	    	features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.TRUE);        	
	    	saxParser = SAXParserPool.getInstance().acquire(features,null);
	    	saxParser.getXMLReader().setErrorHandler(this);    	
	    	saxParser.getXMLReader().setContentHandler(new DefaultHandler());
	    	saxParser.getXMLReader().setEntityResolver(CatalogEntityResolver.getInstance());
	    	saxParser.getXMLReader().parse(mInputFile.asInputSource());	    	
		}catch (Exception e) {
			mStateTracker.mHadCaughtException = true;
			this.sendMessage(Level.WARNING,i18n("DTD_VALIDATION_FAILURE", e.getMessage()));			
		}finally{
			try {
				SAXParserPool.getInstance().release(saxParser,features,null);
			} catch (PoolException e) {

			}
		}
	}
	
	/**
	 * Attempt to validate the input file using javax.xml.validation against a set of schema Sources
	 */
	private void doJAXPSchemaValidation() {
		
		HashMap factoryMap = new HashMap();		//cache to not create multiple identical factories     	     	 
    	SchemaFactory anySchemaFactory = null;	//Schema language neutral jaxp.validation driver

		for (Iterator iter = mSchemaSources.keySet().iterator(); iter.hasNext();) {
			Source source = (Source)iter.next();
			try{				
				String schemaNsURI = (String)mSchemaSources.get(source);
				//send a message
				String schemaType = SchemaLanguageConstants.toNiceNameString(schemaNsURI);
				String fileName = null;
				try{
					fileName = FilenameOrFileURI.toFile(source.getSystemId()).getName();
				}catch (Exception e) {}
				this.sendMessage(Level.INFO,i18n("VALIDATING_USING_SCHEMA",schemaType, fileName));

				//then do it
				if(!factoryMap.containsKey(schemaNsURI)) {
					factoryMap.put(schemaNsURI,SchemaFactory.newInstance(schemaNsURI));
				}
				anySchemaFactory = (SchemaFactory)factoryMap.get(schemaNsURI);
				anySchemaFactory.setErrorHandler(this);
				anySchemaFactory.setResourceResolver(CatalogEntityResolver.getInstance());
				Schema schema = anySchemaFactory.newSchema(source);													
				javax.xml.validation.Validator jaxpValidator = schema.newValidator();																
				jaxpValidator.validate(new StreamSource(mInputFile.toURI().toURL().openStream()));
			}catch (Exception e) {
				mStateTracker.mHadCaughtException = true;
				this.sendMessage(Level.WARNING,i18n("SCHEMA_VALIDATION_FAILURE",source.getSystemId()) + " " +e.getMessage());
			}        
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */
	public void error(FilesetFileException ffe) throws FilesetFileException {
		//we redirect anything recoverable that is reported during 
		//fileset instantiation to ValidatorListener#message just to
		//be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage(ffe));
	}

    /*
     * (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
	public void error(SAXParseException exception) throws SAXException {
		//we redirect anything recieved here to ValidatorListener#message just to be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR));				
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		//we redirect anything recieved here to ValidatorListener#message just to be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_FATALERROR));		
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException exception) throws SAXException {
		//we redirect anything recieved here to ValidatorListener#message just to be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_WARNING));		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#report(org.daisy.util.fileset.validation.message.ValidatorMessage)
	 */
	public void report(Validator validator, ValidatorMessage message) {
		/*
		 * Everything reported regarding validity from 
		 *  - fileset.validator#validate 
		 *  - FilesetErrorHandler#error 
		 *  - SAXErrorHandler 
		 *  is reported here, either redirected or directly.
		 *  When its not a fileset.validator, the validator param is null.
		 */  		
		
		//avoid identical messages
		if (mValidatorMessageCache.contains(message)) {
			System.out.println("ValidatorMessageCache already contains " + message.toString());
		}else{
			mValidatorMessageCache.add(message);
			if(message instanceof ValidatorWarningMessage) {
				mStateTracker.mHadValidationWarning = true;
			}else if (message instanceof ValidatorSevereErrorMessage) {
				mStateTracker.mHadValidationSevereError = true;
			}else {
				mStateTracker.mHadValidationError = true;
			}		
			//TODO, logger level must be fixed
			this.sendMessage(Level.WARNING, message.toString());	
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#progress(org.daisy.util.fileset.validation.Validator, double)
	 */
	public void progress(Validator validator, double progress) {
		this.progress(progress);		
	}	
 
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#exception(org.daisy.util.fileset.validation.Validator, java.lang.Exception)
	 */
	public void exception(Validator validator, Exception e) {
		mStateTracker.mHadCaughtException = true;
		this.sendMessage(Level.INFO, i18n("FILESET_VALIDATION_ERROR", e.getMessage()));		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#inform(org.daisy.util.fileset.validation.Validator, java.lang.String)
	 */
	public void inform(Validator validator, String information) {
		this.sendMessage(Level.INFO, information);		
	}
	
	/**
	 * Verify that we have system properties that identify impls for
	 * retrieval by javax.xml.validation.SchemaFactory and 
	 * org.daisy.util.fileset.validation.ValidatorFactory.
	 */
    private void checkSystemProperties() {
    	
    	//only set if the system doesnt carry values already
    	
    	String test = System.getProperty(
    			"javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0");
    	if(test==null){
    		System.setProperty(
				"javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0",
				"org.daisy.util.xml.validation.jaxp.RelaxNGSchemaFactory");
    	}
    	
    	test = System.getProperty(
    			"javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron");
    	if(test==null){
    		System.setProperty(
				"javax.xml.validation.SchemaFactory:http://www.ascc.net/xml/schematron",
				"org.daisy.util.xml.validation.jaxp.SchematronSchemaFactory");
    	}
    	
    	test = System.getProperty(
			"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986");
    	if(test==null){
    		System.setProperty(
    				"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986",
    				"org.daisy.util.fileset.validation.ValidatorImplZedVal");
    	}
    	
    	test = System.getProperty(
			"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DAISY_202");
    	if(test==null){
    		System.setProperty(
				"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DAISY_202",
				"org.daisy.util.fileset.validation.ValidatorImplD202");
    	}

    	test = System.getProperty(
			"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DTBOOK_DOCUMENT");
    	if(test==null){
		System.setProperty(
			"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/DTBOOK_DOCUMENT",
			"org.daisy.util.fileset.validation.ValidatorImplDtbook");
	}

    	
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
	private Map toSchemaSource(String identifier, String schemaLanguageConstant) throws IOException, SAXException {
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
		 
		Map map = new HashMap();		
		File localSchemaFile = null; 
		URL schemaURL = null;
		identifier = identifier.trim();
		
		//first try to resolve a physical file		
		boolean isRemote = mRegex.matches(mRegex.URI_REMOTE, identifier);
		try{			
			if(!isRemote){
				localSchemaFile = FilenameOrFileURI.toFile(identifier);
				if(localSchemaFile==null||!localSchemaFile.exists()) {
						//we couldnt find an absolute file, try relative to input document					    
						URI u = mInputFile.getParentFolder().toURI().resolve(identifier);
					    localSchemaFile = new File(u);
					    if(localSchemaFile.exists()) {
						    schemaURL = localSchemaFile.toURI().toURL();
					    }					    
				} //if(!localSchemaFile.exists()) 
				else{
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
			if(isRemote){
				this.sendMessage(Level.WARNING, i18n("SCHEMA_IDENTIFIER_ONLINE", identifier));
			}	
			try{
				schemaURL = new URL(identifier);
			}catch (Exception e) {
				this.sendMessage(Level.WARNING, i18n("SCHEMA_INSTANTIATION_FAILURE", identifier));
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
		    			this.sendMessage(Level.WARNING, i18n("SCHEMA_TYPE_NOT_SUPPORTED", schemaURL.toString()));
		    		}
		    	}catch (Exception e) {
					mStateTracker.mHadCaughtException = true;
					this.sendMessage(Level.WARNING, i18n("SCHEMA_IDENTIFICATION_FAILURE", schemaURL.toString()));
				}			    	
		    }else{
		    	//it came as inparam
		    	nsuri = schemaLanguageConstant;
		    }
		    	    
		    if(nsuri!=null) {
		    	if(nsuri.equals(SchemaLanguageConstants.RELAXNG_NS_URI)) {
		    		//need to check for schematron islands FIXME may occur in XSD as well
		    		//FIXME check first, or make sure this doesnt break when no sch in rng
		    		StreamSource schss = new StreamSource(schemaURL.openStream());
		    	    schss.setSystemId(schemaURL.toExternalForm());
		    	    //FIXME may be ISO schematron, have to check first
		    	    map.put(schss,SchemaLanguageConstants.SCHEMATRON_NS_URI);	    		
		    	}	    		    	
		    	map.put(ss,nsuri);
		    	return map;
		    }
		}
	    return null;
	}
    	
//	/**
//	 * @return a SchemaLanguageConstant NS URI, or null if schema type was not detected
//	 */
//	private String getSchemaType(URL url) {
//		try{
//			PeekResult schemaPeekResult = PeekerPool.getInstance().acquire().peek(url); 
//			String rootName = schemaPeekResult.getRootElementLocalName();		
//			String rootNsUri = schemaPeekResult.getRootElementNsUri();
//			
//			if(rootName == "schema") {
//				if(rootNsUri == SchemaLanguageConstants.SCHEMATRON_NS_URI){
//					return SchemaLanguageConstants.SCHEMATRON_NS_URI;
//				}else if(rootNsUri == SchemaLanguageConstants.ISO_SCHEMATRON_NS_URI){
//					return SchemaLanguageConstants.ISO_SCHEMATRON_NS_URI;
//				}else if(rootNsUri == SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI){
//					return SchemaLanguageConstants.W3C_XML_SCHEMA_NS_URI;
//				}							
//			}else if(rootName == "grammar" 
//				&& rootNsUri == SchemaLanguageConstants.RELAXNG_NS_URI) {
//				return SchemaLanguageConstants.RELAXNG_NS_URI;
//			}else{				
//				//... it may be a DTD or something completey other...
//				this.sendMessage(Level.WARNING, i18n("SCHEMA_TYPE_NOT_SUPPORTED", url.toString()));
//			}
//		}catch (Exception e) {
//			//peeker parse failure, or peeker getters returning null
//			mStateTracker.mHadCaughtException = true;
//			this.sendMessage(Level.WARNING, i18n("SCHEMA_IDENTIFICATION_FAILURE", url.toString()));
//		}
//		return null;
//	}
	
	/**
	 * Track the state of the validation process.
	 * @author Markus Gylling
	 */
	class StateTracker {
		boolean mHadValidationWarning = false;						//tracked in this.ValidatorListener.report(ValidatorMessage)
		boolean mHadValidationError = false;						//tracked in this.ValidatorListener.report(ValidatorMessage)
		boolean mHadValidationSevereError = false;					//tracked in this.ValidatorListener.report(ValidatorMessage)
		boolean mHadCaughtException = false;						//whether an unexpected exception was caught
		
		static final String NONE = "NONE";							
		static final String WARNING = "NONE";
		static final String ERROR = "NONE";
		static final String SEVERE = "NONE";

		/**
		 * Was a ValidatorMessage of any type recieved?
		 * If not, the input is valid, assuming that CompletionTracker reports that
		 * the intended processes were actually run.
		 */
		boolean hadValidatorMessage() {
			return(mHadValidationWarning 
					|| mHadValidationError 
					|| mHadValidationSevereError);
		}
		
		/**
		 * @return true if the input threshold was breached during the validation pass.
		 */
		boolean thresholdBreached(String setThreshold) {
			if(!hadValidatorMessage()) return false;
			if (setThreshold.equals(WARNING) && mHadValidationWarning) return true;
			if (setThreshold.equals(ERROR) && (mHadValidationError||mHadValidationSevereError)) return true;
			if (setThreshold.equals(SEVERE) && mHadValidationSevereError) return true;
			if (setThreshold.equals(NONE)) return false;															
			throw new InvalidParameterException(setThreshold);
		}
		
	}
	
	/**
	 * Track what validation processes are succesfully completed.
	 * Note - success means completed, regardless of whether ValidatorMessages were issued or not. 
	 * @author Markus Gylling
	 */
	class CompletionTracker {
		boolean mCompletedFilesetInstantiation = false;				//whether a fileset was built (which implies some validation)
		boolean mCompletedFilesetValidation = false;				//whether a fileset validation was completed 
		boolean mCompletedInlineDTDValidation = false;				//whether inline DTD validation was completed (only done if fileset val was not done)
		boolean mCompletedJAXPSchemaValidation = false;				//whether JAXP validation was completed (done if inparam schemas or inline non-DTD schemas)

		/**
		 * Was any validation process successfully performed?
		 * If not, we cannot make any statements at all about the validity of the input 
		 */
		boolean completedAnyProcess() {
			return(mCompletedFilesetInstantiation 
					|| mCompletedFilesetValidation 
					|| mCompletedInlineDTDValidation 
					|| mCompletedJAXPSchemaValidation);
		}
		
	}


}