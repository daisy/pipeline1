package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotRecognizedException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Abstract base that a concrete impl of org.daisy.util.fileset.validation.Validator may or may not extend
 * @author Markus Gylling
 */
abstract class ValidatorImplAbstract implements Validator, FilesetErrorHandler, ErrorHandler {
	protected ArrayList mSupportedFilesetTypes = new ArrayList();	//<FilesetType>	
	protected ValidatorListener mValidatorListener = null;			//message handler
	protected Fileset mFileset = null;								//fileset to validate		
	protected boolean mDebugMode = false;
	
	ValidatorImplAbstract(ArrayList supportedFilesetTypes) {
		if(System.getProperty("org.daisy.debug")!=null) mDebugMode = true;
		mSupportedFilesetTypes.addAll(supportedFilesetTypes);
	}
	
	ValidatorImplAbstract(FilesetType supportedFilesetType) {
		if(System.getProperty("org.daisy.debug")!=null) mDebugMode = true;
		mSupportedFilesetTypes.add(supportedFilesetType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		mFileset = fileset;
		checkSupport();
		checkState();
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
		checkSupport();
		checkState();
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
		mValidatorListener = null;
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
	public void setReportListener(ValidatorListener listener) {
		mValidatorListener = listener;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#getValidatorListener()
	 */
	public ValidatorListener getValidatorListener() {
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
	 * javax.xml.validation uses ErrorHandler so we recieve here if 
	 * a subclass happens to use javax.xml.validation and doesnt override. 
	 * We redirect to ValidatorListener.report.
	 */
	public void error(SAXParseException exception) throws SAXException {
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
			(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR));		
	}

	/**
	 * javax.xml.validation uses ErrorHandler so we recieve here if 
	 * a subclass happens to use javax.xml.validation and doesnt override. 
	 * We redirect to ValidatorListener.report.
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_FATALERROR));		
	}

	/**
	 * javax.xml.validation uses ErrorHandler so we recieve here if 
	 * a subclass happens to use javax.xml.validation and doesnt override. 
	 * We redirect to ValidatorListener.report.
	 */
	public void warning(SAXParseException exception) throws SAXException {
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
				(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_WARNING));		
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
}
