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

import javax.xml.parsers.SAXParser;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.MessageEvent;
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
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.XMLUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.daisy.util.xml.validation.ValidationUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An input type agnostic multi schema type multi layered validator.
 * @author Markus Gylling
 */

@SuppressWarnings("unused")
public class ValidatorDriver extends Transformer implements FilesetErrorHandler, ValidatorListener, ErrorHandler {

	private static final double PROGRESS_PEEK = 0.01;
	private static final double PROGRESS_FILESET_INSTANTIATION = 0.10;
	private static final double PROGRESS_FILESET_VALIDATION = 0.20;
	private static final double PROGRESS_SAX_VALIDATION = 0.90;
	private static final double PROGRESS_JAXP_VALIDATION = 0.95;

	private EFile mInputFile = null;												//from paramaters
	private String mRequiredInputType = null;										//whether to abort if not a certain type of fileset
	private Fileset mInputFileset = null;											//based in inputfile
	private PeekResult mInputFilePeekResult = null;									//a global peek on inputfile
	private Map<Source, String> mSchemaSources = new HashMap<Source, String>();		//<Source>,<SchemaNSURI> 
	private HashSet mValidatorMessageCache = new HashSet();							//to avoid identical messages
	private FilesetRegex mRegex = FilesetRegex.getInstance();						//convenience shortcut
	private StateTracker mStateTracker = new StateTracker();						//inner class
	private CompletionTracker mCompletionTracker = new CompletionTracker();			//inner class
	private XMLReporter mXmlReporter = null;										// validator xml output
	private String mForcedValidatorImpl = null;										//whether user overrides default impl
	
	/**
	 * Constructor.
	 */
	public ValidatorDriver(InputListener inListener,  Boolean isInteractive) {
		super(inListener, isInteractive);        
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

		try {

			long start = System.nanoTime();
			
			// Martin Blomberg 2006-11-28
			// initialize the optional xml reporter
			try {
				String outputPath = (String) parameters.remove("xmlReport");
				if (outputPath != null && outputPath.length()>0 ) {
					File reportFile = new File(outputPath);
					
					String xmlStylesheet = (String) parameters.remove("xmlStylesheet");
					if (xmlStylesheet != null) {
						mXmlReporter = new XMLReporter(reportFile, xmlStylesheet);
					} else {
						mXmlReporter = new XMLReporter(reportFile);						
					}
				}
			} catch (Exception e) {
				throw new TransformerRunException(e.getMessage(), e);
			}
			
			try{			
				mInputFile = new EFile(FilenameOrFileURI.toFile((String)parameters.remove("input")));			

				String type = (String)parameters.remove("requireInputType");
				if(!type.toLowerCase().equals("off"))mRequiredInputType = type; //else leave as null
				
				Peeker peeker = null;
				try{
					peeker = PeekerPool.getInstance().acquire();
					mInputFilePeekResult = peeker.peek(mInputFile);
					this.sendMessage(PROGRESS_PEEK);
					this.checkAbort();
				}catch (Exception e) {
					//input isnt XML, malformed at root, or something else went wrong
					//A fileset manifest can be non-XML so be silent, try to continue
				}finally{
					PeekerPool.getInstance().release(peeker);
				}

				try{				
					mInputFileset = new FilesetImpl(mInputFile.toURI(),this,true,false);
															
					if(mRequiredInputType!=null) {
						//the user has specified that an error+abort should be thrownn if
						//input fileset is not of the given type
						if (!mInputFileset.getFilesetType().toNiceNameString().equals(mRequiredInputType)) {
							String message = i18n("NOT_REQUIRED_INPUT_TYPE",mRequiredInputType);
							this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT);
							throw new TransformerRunException(message);
						}
					}
					
					mForcedValidatorImpl = (String) parameters.remove("forceImplementation");
					if (mForcedValidatorImpl != null && mForcedValidatorImpl.length()>0 ) {
						//set the system property for the ValidatorFactory to access later
						String key = "org.daisy.util.fileset.validation:http://www.daisy.org/fileset/"
							+ mInputFileset.getFilesetType().toString();						
						System.setProperty(key,mForcedValidatorImpl);
						
					}
					
										
					this.sendMessage(PROGRESS_FILESET_INSTANTIATION);
					this.checkAbort();
					mCompletionTracker.mCompletedFilesetInstantiation = true;
					
					ValidatorFactory validatorFactory = ValidatorFactory.newInstance(); 
					try{
						Validator filesetValidator = validatorFactory.newValidator(mInputFileset.getFilesetType());
						filesetValidator.setListener(this);	

						String delegates = (String)parameters.remove("delegates");
						this.setDelegates(filesetValidator, delegates);
						
						//TODO set schemas on validator
						
						String message = i18n("VALIDATING_FILESET", mInputFileset.getFilesetType().toNiceNameString());
						this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
						filesetValidator.validate(mInputFileset);
						
						this.sendMessage(PROGRESS_FILESET_VALIDATION);
						this.checkAbort();
						mCompletionTracker.mCompletedFilesetValidation = true;
					}catch (ValidatorNotSupportedException e) {
						//the factory could not produce a validator for this fileset type											
						String message = i18n("NO_FILESET_VALIDATOR", mInputFileset.getFilesetType().toNiceNameString());
						this.sendMessage(message, MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
						// add exception to xml report
						xmlReport(e);
					}catch (ValidatorException ve) {
						//another error than nonsupported type occured
						mStateTracker.mHadCaughtException = true;
						String message = i18n("FILESET_VALIDATION_FAILURE", ve.getMessage());		
						this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
						// add exception to xml report
						xmlReport(ve);
					}
				}catch (FilesetTypeNotSupportedException e) {
					//org.daisy.util.fileset did not recognize the input type					
					
					if(mRequiredInputType!=null) {
						//the user has specified that an error+abort should be thrown if
						//input is not of the given Fileset type
						//Since we got this exception, its not a fileset at all
						String message = i18n("NOT_REQUIRED_INPUT_TYPE",mRequiredInputType);												
						this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.INPUT);
						throw new TransformerRunException(message);						
					}
										
					// add exception to xml report
					xmlReport(e);
					//since no fileset, no dtd validation yet
					if((mInputFilePeekResult != null) 
							&& (mInputFilePeekResult.getPrologSystemId()!=null
									||mInputFilePeekResult.getPrologPublicId()!=null)){
						String message = i18n("SAX_DTD_VAL");
						this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
						doSAXDTDValidation();	
						this.sendMessage(PROGRESS_SAX_VALIDATION);
						this.checkAbort();
						mCompletionTracker.mCompletedInlineDTDValidation = true;
					}
				}	

				if((!(mSchemaSources = setSchemaSources(parameters)).isEmpty()) 
						&& (mInputFilePeekResult != null)) {
					String message = i18n("JAXP_SCHEMA_VAL", Integer.toString(mSchemaSources.size()));
					this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
					doJAXPSchemaValidation();
					this.sendMessage(PROGRESS_JAXP_VALIDATION);
					this.checkAbort();
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

			String abortOnExceptionParam = ((String)parameters.remove("abortOnException"));
			if(abortOnExceptionParam==null)abortOnExceptionParam="true";
			boolean abortOnException = abortOnExceptionParam.equals("true");
			
			String abortThreshold = (String)parameters.remove("abortThreshold");
			if(abortThreshold == null) abortThreshold = "NONE";

			if(abortOnException && mStateTracker.mHadCaughtException) {
				throw new TransformerRunException(i18n("ABORTING_EXCEPTIONS_OCCURED"));
			}

			if(!mCompletionTracker.completedAnyProcess()) {
				throw new TransformerRunException(i18n("ABORTING_NO_VALIDATION_PERFORMED"));
			}

			if(mStateTracker.thresholdBreached(abortThreshold)) {					
				String message = i18n("ABORTING_THRESHOLD_BREACHED");
				this.sendMessage(message, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
				return false;
			}

			//else, we are about to exit gracefully. Give some info.

			if(mCompletionTracker.mCompletedFilesetValidation) {
				String message = i18n("COMPLETED_FILESET_VALIDATION");		
				this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
			}else{
				if(mCompletionTracker.mCompletedFilesetInstantiation) {					
					String message = i18n("COMPLETED_FILESET_INSTANTIATION");		
					this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);

				}else{
					if(mCompletionTracker.mCompletedInlineDTDValidation) {						
						String message = i18n("COMPLETED_DTD_VALIDATION");		
						this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
					}
				}
			}

			if(mCompletionTracker.mCompletedJAXPSchemaValidation) {
				String message = i18n("COMPLETED_JAXP_VALIDATION");		
				this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
			}
			
			String message = i18n("DURATION", Double.toString((end-start)/1000000000));		
			this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);

			message = i18n("MESSAGES_FROM_VALIDATOR",Integer.toString(mValidatorMessageCache.size()));		
			this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
			
			if(mValidatorMessageCache.size()==0) {				
				message = i18n("CONGRATS");		
				this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
			}
			
			
			if (mForcedValidatorImpl  != null && mForcedValidatorImpl.length()>0 && mInputFileset!=null ) {
				//we reset the system property so that default impl is used next time
				System.clearProperty("org.daisy.util.fileset.validation:http://www.daisy.org/fileset/"
							+ mInputFileset.getFilesetType().toString());
			}
			
			return true;

		} finally {
			// finish the optional xml report
			if (mXmlReporter != null) {
				try {
					mXmlReporter.finishReport();
				} catch (XMLStreamException e) {
					throw new TransformerRunException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Adds all specified delegates to the validator. 
	 * @param validator the Validator to add the delegates to.
	 * @param delegates the String containing the names of the delegates.
	 */
	private void setDelegates(Validator validator, String delegates) {		
		if (delegates!=null && !"".equals(delegates)) {
			String[] array = delegates.split(",");
			for (int i = 0; i < array.length; i++) {
				String delegate = array[i].trim();
				try {
					validator.setDelegate(delegate);
				} catch (ValidatorNotSupportedException e) {
					mStateTracker.mHadCaughtException = true;					
					String message = i18n("DELEGATE_INSTANTIATION_FAILURE", array[i]);
					this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
				} catch (ValidatorException e) {
					mStateTracker.mHadCaughtException = true;
					String message = i18n("DELEGATE_INSTANTIATION_FAILURE", array[i]);
					this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
				}
			}			
		}
	}


	/**
	 * Collects all schemas that the input document should be validated against;
	 * the schemas may occur inlined in input document, or in the schemas inparam.
	 * @return a map of schemas (Source,SchemaNSURI).  
	 * @throws SAXException 
	 * @throws IOException 
	 */
	
	
	private Map<Source,String> setSchemaSources(Map parameters) throws IOException, SAXException {

		//get schemas from inparams
		String schemas = (String)parameters.remove("schemas");
		if(schemas!=null && schemas.length()>0) {
			String[] array = schemas.split(",");
			for (int i = 0; i < array.length; i++) {
				try{
					Map<Source,String> map = ValidationUtils.toSchemaSources(array[i]);
					mSchemaSources.putAll(map);
				}catch (Exception e) {
					mStateTracker.mHadCaughtException = true;					
					String message =i18n("SCHEMA_INSTANTIATION_FAILURE", array[i]+ " " + e.getMessage());
					this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
				}
			}						
		}//if(schemas!=null && schemas.length()>0)

		//get schemas from doc inline
		if(mInputFilePeekResult!=null){
			Set xsis = mInputFilePeekResult.getXSISchemaLocationURIs();
			for (Iterator iter = xsis.iterator(); iter.hasNext();) {
				String str = (String) iter.next();						
				try{
					Map<Source,String> map = ValidationUtils.toSchemaSources(str);
					mSchemaSources.putAll(map);
				}catch (Exception e) {
					String message = i18n("SCHEMA_INSTANTIATION_FAILURE", str);
					this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
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
	@SuppressWarnings("unchecked")
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
			String message = i18n("DTD_VALIDATION_FAILURE", e.getMessage());
			this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
		}finally{
			try {
				SAXParserPool.getInstance().release(saxParser,features,null);
			} catch (PoolException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Attempt to validate the input file using javax.xml.validation against a set of schema Sources
	 */
	@SuppressWarnings("unchecked")
	private void doJAXPSchemaValidation() {

		HashMap factoryMap = new HashMap();		//cache to not create multiple identical factories     	     	 
		SchemaFactory anySchemaFactory = null;	//Schema language neutral jaxp.validation driver

		double count = mSchemaSources.keySet().size();
		double num = 0;
		for (Iterator iter = mSchemaSources.keySet().iterator(); iter.hasNext();) {
			Source source = (Source)iter.next();
			num++;
			try{				
				String schemaNsURI = mSchemaSources.get(source);
				//send a message
				String schemaType = SchemaLanguageConstants.toNiceNameString(schemaNsURI);
				String fileName = source.getSystemId();				
				if (fileName != null && fileName.lastIndexOf("/") > 0) {
					fileName = fileName.substring(fileName.lastIndexOf("/"));
				}				
				String message = i18n("VALIDATING_USING_SCHEMA",schemaType, fileName);
				this.sendMessage(message, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);

				//then do it
				if(!factoryMap.containsKey(schemaNsURI)) {
					factoryMap.put(schemaNsURI,SchemaFactory.newInstance(schemaNsURI));
				}
				anySchemaFactory = (SchemaFactory)factoryMap.get(schemaNsURI);
				anySchemaFactory.setErrorHandler(this);
				anySchemaFactory.setResourceResolver(CatalogEntityResolver.getInstance());
				Schema schema = anySchemaFactory.newSchema(source);													
				javax.xml.validation.Validator jaxpValidator = schema.newValidator();
				StreamSource ss = new StreamSource(mInputFile.toURI().toURL().openStream());
				jaxpValidator.validate(ss);
				if(ss.getInputStream()!=null) ss.getInputStream().close();
				this.sendMessage(PROGRESS_FILESET_VALIDATION + (PROGRESS_JAXP_VALIDATION - PROGRESS_FILESET_VALIDATION) * (num / count));
				this.checkAbort();
			}catch (Exception e) {
				mStateTracker.mHadCaughtException = true;				
				String message = i18n("SCHEMA_VALIDATION_FAILURE",source.getSystemId());
				this.sendMessage(message, MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
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
		//we redirect anything received here to ValidatorListener#message just to be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR));				
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		//we redirect anything received here to ValidatorListener#message just to be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_FATALERROR));		
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException exception) throws SAXException {
		//we redirect anything received here to ValidatorListener#message just to be consistent.
		this.report(null,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_WARNING));		

	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#report(org.daisy.util.fileset.validation.message.ValidatorMessage)
	 */
	@SuppressWarnings("unchecked")
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
			xmlReport(validator, message);
			mValidatorMessageCache.add(message);
			
			MessageEvent.Type type = null;
			if(message instanceof ValidatorWarningMessage) {
				mStateTracker.mHadValidationWarning = true;
				type = MessageEvent.Type.WARNING;
			}else if (message instanceof ValidatorSevereErrorMessage) {
				mStateTracker.mHadValidationSevereError = true;
				type = MessageEvent.Type.ERROR;
			}else {
				mStateTracker.mHadValidationError = true;
				type = MessageEvent.Type.ERROR;
			}		
			Location loc = LocusTransformer.newLocation(message);
			this.sendMessage(message.toString(), type, MessageEvent.Cause.INPUT,loc);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#progress(org.daisy.util.fileset.validation.Validator, double)
	 */
	public void progress(Validator validator, double progress) {
		//cant do much here since the validators progres is not equal to the transformers progress..		
	}	

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#exception(org.daisy.util.fileset.validation.Validator, java.lang.Exception)
	 */
	public void exception(Validator validator, Exception e) {
		mStateTracker.mHadCaughtException = true;
		xmlReport(e);		
		String message = i18n("FILESET_VALIDATION_ERROR", e.getMessage());
		this.sendMessage(message, MessageEvent.Type.ERROR);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#inform(org.daisy.util.fileset.validation.Validator, java.lang.String)
	 */
	public void inform(Validator validator, String information) {
		this.sendMessage(information, MessageEvent.Type.INFO);
	}

	/**
	 * Verify that we have system properties that identify default impls for
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
		"javax.xml.validation.SchemaFactory:http://purl.oclc.org/dsdl/schematron");
		if(test==null){
			System.setProperty(
					"javax.xml.validation.SchemaFactory:http://purl.oclc.org/dsdl/schematron",
			"org.daisy.util.xml.validation.jaxp.ISOSchematronSchemaFactory");
		}
		
		test = System.getProperty(
			"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986");
		if(test==null){
			System.setProperty(
					"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986",
			"org.daisy.util.fileset.validation.ValidatorImplZedVal");
			/*
			 * The basic (non-ZedVal) validator would be
			 * System.setProperty(
			 * "org.daisy.util.fileset.validation:http://www.daisy.org/fileset/Z3986",
			 * "org.daisy.util.fileset.validation.impl.ValidatorImplZedBasic");
			 */
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

		test = System.getProperty(
		"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/OPS_20");
		if(test==null){
			System.setProperty(
					"org.daisy.util.fileset.validation:http://www.daisy.org/fileset/OPS_20",
			"org.daisy.util.fileset.validation.ValidatorImplOPS2x");
		}
		

	}

	// martin blomberg 2006-12-22
	/**
	 * Adds the exception to the xml output if the XMLReporter instance
	 * is not null.
	 * @param e the exception
	 */
	private void xmlReport(Exception e) {
		if (mXmlReporter != null) {
			try {
				mXmlReporter.report(e);
			} catch (XMLStreamException e1) {
				e1.printStackTrace();
				throw new RuntimeException(e1.getMessage(), e1);
			}
		}
	}
	
	// martin blomberg 2006-12-22
	/**
	 * Adds the validator message to the xml output if the XMLReporter instance
	 * is not null.
	 * @param validator the validator who sends the message
	 * @param message the validator message
	 */
	private void xmlReport(Validator validator, ValidatorMessage message) {
		if (mXmlReporter != null) {
			try {
				mXmlReporter.report(validator, message);
			} catch (XMLStreamException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

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
		static final String WARNING = "WARNING";
		static final String ERROR = "ERROR";
		static final String SEVERE = "SEVERE";

		/**
		 * Was a ValidatorMessage of any type received?
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
			if (setThreshold.equals(WARNING) && (mHadValidationWarning||mHadValidationError||mHadValidationSevereError)) return true;
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