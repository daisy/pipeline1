package org.daisy.util.fileset.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.events.Attribute;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegate;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotRecognizedException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Abstract base that a concrete impl of org.daisy.util.fileset.validation.Validator may or may not extend
 * @author Markus Gylling
 */
abstract class ValidatorImplAbstract implements org.daisy.util.fileset.validation.Validator, FilesetErrorHandler, ErrorHandler {
	protected ArrayList<FilesetType> mSupportedFilesetTypes = null; 						//<FilesetType>	
	protected ValidatorListener mValidatorListener = null;									//message handler
	protected Fileset mFileset = null;														//fileset to validate		
	protected boolean mDebugMode = false;													//system property
	private ArrayList<ValidatorDelegate> mDelegates = null; 								//registered ValidatorDelegates
	private Map<URL,TypeRestriction> mSchemas = null;	
	
	ValidatorImplAbstract(ArrayList<FilesetType> supportedFilesetTypes ) {
		initialize(supportedFilesetTypes);		
	}
	
	ValidatorImplAbstract(FilesetType supportedFilesetType) {
		List<FilesetType> list = new ArrayList<FilesetType>();
		list.add(supportedFilesetType);
		initialize(list);
	}
	
	private void initialize(List<FilesetType> supportedFilesetTypes) {
		if(System.getProperty("org.daisy.debug")!=null) mDebugMode = true;
		mSupportedFilesetTypes = new ArrayList<FilesetType>();	
		mSupportedFilesetTypes.addAll(supportedFilesetTypes);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		mFileset = fileset;
		validate();
	}


	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		try {
			mFileset = new FilesetImpl(manifest,this,true,false);
		} catch (FilesetFatalException e) {
			throw new ValidatorException("Could not create input fileset: " + e.getMessage(),e);
		}		
		validate();
	}

	
	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		checkSupport();
		checkState();
		executeSchemas();
		executeDelegates();		
	}
	
	private void executeDelegates() throws ValidatorNotSupportedException, ValidatorException {
		if(mDelegates!=null) {
			for (Iterator iter = mDelegates.iterator(); iter.hasNext();) {
				ValidatorDelegate vd = (ValidatorDelegate) iter.next();
				if(vd.isFilesetTypeSupported(mFileset.getFilesetType())) {
					vd.execute(mFileset);
				}else{
					throw new ValidatorNotSupportedException("Fileset type " 
							+ mFileset.getFilesetType().toNiceNameString() 
							+ " is not supported by " + vd.getClass().getSimpleName());
				}
			} 		
		}
	}
	
	private void executeSchemas() throws ValidatorNotSupportedException, ValidatorException {
		/*
		 * mSchemas = <SchemaURL, FilesetFileClassTypeToApplySchemaOn)
		 * find out what kind of schema it is, get its canonical NS URI
		 * get a factory for that kind of schema
		 * create a validator with that schema
		 * then loop through fileset to validate all type matches
		 */
		
			if(mSchemas!=null) {
				for (Iterator iter = mSchemas.keySet().iterator(); iter.hasNext();) {
					URL schemaURL = (URL) iter.next();
					try {						
						//instead of Peeker to be sure we dont miss non-root decls:
						NamespaceReporter nsr = new NamespaceReporter(schemaURL);				
						Set<String> nsURIs = nsr.getNamespaceURIs();
						//since schemas may be compound, cover all possibilities.
						int nsURIsFound = 0;
						if(nsURIs!=null){
							for (String uri : nsURIs) {
								if(SchemaLanguageConstants.hasEntry(uri)) {
									++nsURIsFound;
									try{
										SchemaFactory factory = SchemaFactory.newInstance(uri);
										factory.setErrorHandler(this);
										factory.setResourceResolver(CatalogEntityResolver.getInstance());
										//go via StreamSource and explicitly set the system id to be safe
										StreamSource ss = new StreamSource(schemaURL.openStream());
										ss.setSystemId(schemaURL.toExternalForm());									
										Schema schema = factory.newSchema(ss);
										if(mDebugMode){
											//inform a little
											File schemaFile = new File(schemaURL.toURI());									
											mValidatorListener.inform(this, "Validating using the " 
												+ SchemaLanguageConstants.toNiceNameString(uri) 
												+ " " + schemaFile.getName() +".");
										}
										//and continue
										javax.xml.validation.Validator validator = schema.newValidator();
										validate(validator,mSchemas.get(schemaURL));
										//and clean up
										if(ss.getReader()!=null) ss.getReader().close();
										if(ss.getInputStream()!=null) ss.getInputStream().close();					
									}catch (Exception e) {
										mValidatorListener.exception(this, e);
									}														
								}							
							}
						}
						if(nsURIsFound==0) {
							throw new ValidatorNotSupportedException("no recognized schema type in " + schemaURL);
						}
					}catch (Exception e) {
						mValidatorListener.exception(this, e);
					}
				} 		
			}		
	}
	
	private void validate(javax.xml.validation.Validator validator, TypeRestriction restriction) throws FileNotFoundException, SAXException, IOException {
		for (Iterator iter = mFileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile ffile = (FilesetFile) iter.next();
			if(ffile.getClass().getName().equals(restriction.mFilesetFileType)) { 
				XmlFile xf = (XmlFile)ffile;
				if(xf.isWellformed()) {	
					if(restriction.mRootAttributes==null || matchesRootAttributes(xf,restriction.mRootAttributes)){
						StreamSource ss = xf.asStreamSource();					
						validator.validate(ss);
						if(ss.getReader()!=null) ss.getReader().close();
						if(ss.getInputStream()!=null) ss.getInputStream().close();
					}					
				}
			}			
		}		
	}

	/**
	 * @return true if all attributes in the rootAttributes set occur on xf root, else false.
	 */
	private boolean matchesRootAttributes(XmlFile xf, Set<Attribute> rootAttributes) {
		Attributes instanceAttrs = xf.getRootElementAttributes();
		for (Attribute shouldExist : rootAttributes) {
			String shouldExistLocalName = shouldExist.getName().getLocalPart();
			String shouldExistValue = shouldExist.getValue();
			String shouldExistNSURI = shouldExist.getName().getNamespaceURI();			
			String value = instanceAttrs.getValue(shouldExistNSURI, shouldExistLocalName);
			if(value==null || !value.equals(shouldExistValue)) {
				return false;	
			}						
		}
		return true;
	}

	/**
	 * Verifies that the current configuration meets minimal requirements.
	 * @throws ValidatorException if the current configuration does not meet minimal requirements.
	 */
	private void checkState() throws ValidatorException {
		if(mValidatorListener == null) 
			throw new ValidatorException("No registered ValidatorListener");
		if(mFileset == null)
			throw new ValidatorException("No registered fileset");
		
	}

	/**
	 * Check if the currently registered fileset is validatable by this validator.
	 * @throws ValidatorNotSupportedException if the currently registered fileset is not validatable by this validator
	 */
	private void checkSupport() throws ValidatorException, ValidatorNotSupportedException{
		if(mFileset!=null) {
			if(!mSupportedFilesetTypes.contains(mFileset.getFilesetType())) {
				throw new ValidatorNotSupportedException("This validator does not support validation of " 
						+ mFileset.getFilesetType().toNiceNameString() + " filesets.");
			}
			if(mDelegates!=null) {
				for (Iterator iter = mDelegates.iterator(); iter.hasNext(); ) {
					ValidatorDelegate delegate = (ValidatorDelegate) iter.next();
					if (!delegate.isFilesetTypeSupported(mFileset.getFilesetType())) {
						throw new ValidatorNotSupportedException("The validator delegate does not support validation of "
								+ mFileset.getFilesetType().toNiceNameString() + " filesets.");
					}
				}
			}
		}else{
			throw new ValidatorException("No registered fileset");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		mFileset = null;
		mSchemas.clear();
		mDelegates.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */	
	public void error(FilesetFileException ffe) throws FilesetFileException {
		/*
		 * These are errors that the fileset instance emits during instantiation.
		 * Note that they are not FilesetFatalException, which is terminating and thrown,
		 * as opposed to these that are reported to an impl of FilesetErrorHandler.
		 * We capture them and wrap them in a validator message.
		 */
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage(ffe));		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#setValidatorListener(org.daisy.util.fileset.validation.ValidatorListener)
	 */
	public void setListener(ValidatorListener listener) {
		mValidatorListener = listener;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#getValidatorListener()
	 */
	public ValidatorListener getListener() {
		return mValidatorListener;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#isFilesetTypeSupported(org.daisy.util.fileset.FilesetType)
	 */
	public boolean isFilesetTypeSupported(FilesetType type) {
		return mSupportedFilesetTypes.contains(type);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#getSupportedFilesetTypes()
	 */
	public List getSupportedFilesetTypes() {		
		return mSupportedFilesetTypes;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#getFileset()
	 */
	public Fileset getFileset() {
		return mFileset;
	}
	
	/**
	 * javax.xml.validation uses ErrorHandler so we receive here if 
	 * a subclass happens to use javax.xml.validation and doesnt override. 
	 * We redirect to ValidatorListener.report.
	 */
	public void error(SAXParseException exception) throws SAXException {
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
			(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR));		
	}

	/**
	 * javax.xml.validation uses ErrorHandler so we receive here if 
	 * a subclass happens to use javax.xml.validation and doesnt override. 
	 * We redirect to ValidatorListener.report.
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_FATALERROR));		
	}

	/**
	 * javax.xml.validation uses ErrorHandler so we receive here if 
	 * a subclass happens to use javax.xml.validation and doesnt override. 
	 * We redirect to ValidatorListener.report.
	 */
	public void warning(SAXParseException exception) throws SAXException {
		if(!exception.getMessage().contains("XSLT 1.0")) {
			//temp hack to avoid saxon 8 version warning messages
			mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
					(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_WARNING));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#setDelegate(org.daisy.util.fileset.validation.ValidatorUtils)
	 */
	public void setDelegate(ValidatorDelegate delegate) throws ValidatorException, ValidatorNotSupportedException {		
		addDelegate(delegate);
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#setDelegate(java.lang.String)
	 */
	public void setDelegate(String delegateClassName) throws ValidatorException, ValidatorNotSupportedException {
		ValidatorDelegate delegate = null;
		//we dont use listener.exception to report here, 
		//since we are still in configuration time. 
		try {
			Class klass = Class.forName(delegateClassName);
			Object o = klass.newInstance();
            if(o instanceof ValidatorDelegate) {
            	delegate = (ValidatorDelegate)o;
            } else{
            	throw new ValidatorException(o.getClass().getSimpleName() + " is not a ValidatorDelegate");         	
            }
		} catch (Throwable t) {
			throw new ValidatorException(t.getMessage(),t);
		} 		
		addDelegate(delegate);				
	}
	
	private void addDelegate(ValidatorDelegate delegate) throws ValidatorException, ValidatorNotSupportedException {		
		if (mDelegates == null) mDelegates = new ArrayList<ValidatorDelegate>();
		delegate.setValidator(this);
		mDelegates.add(delegate);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#setSchema(java.net.URL, java.lang.String)
	 */
	public void setSchema(URL schema, String filesetFileType) throws ValidatorException, ValidatorNotSupportedException {
		this.setSchema(schema, filesetFileType, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#setSchema(java.net.URL, java.lang.String)
	 */
	public void setSchema(URL schema, String filesetFileType, Set<Attribute> rootAttributes) throws ValidatorException, ValidatorNotSupportedException {
		if(mSchemas==null) mSchemas = new HashMap<URL,TypeRestriction>();
		TypeRestriction restriction = new TypeRestriction(filesetFileType,rootAttributes);
		mSchemas.put(schema, restriction);
	}
	
	public void setFeature(String name, boolean value) throws ValidatorNotRecognizedException, ValidatorNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new ValidatorNotRecognizedException(name);	                
	}

	public boolean getFeature(String name) throws ValidatorNotRecognizedException, ValidatorNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new ValidatorNotRecognizedException(name);	         
	}
	
	public void setProperty(String name, Object object) throws ValidatorNotRecognizedException, ValidatorNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");        
       	throw new ValidatorNotRecognizedException(name);	 
	}

	public Object getProperty(String name) throws ValidatorNotRecognizedException, ValidatorNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new ValidatorNotRecognizedException(name);
    }
	
	class TypeRestriction {
		String mFilesetFileType = null;
		Set<Attribute> mRootAttributes = null;
		
		TypeRestriction(String filesetFileType, Set<Attribute> rootAttributes) {
			mFilesetFileType = filesetFileType;
			mRootAttributes = rootAttributes; 
		}
	}
}
