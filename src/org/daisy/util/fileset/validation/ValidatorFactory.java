package org.daisy.util.fileset.validation;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.validation.exception.ValidatorNotRecognizedException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

public class ValidatorFactory {
	private ValidatorListener mValidatorListener = null;
	private static String mSystemPropertyConstant = "org.daisy.util.fileset.validation:http://www.daisy.org/fileset/";
	private boolean mDebugState = false;
	
	public static ValidatorFactory newInstance() {
		return new ValidatorFactory();
	}
	
	/**
	 * Constructor.
	 */
	private ValidatorFactory(){
	  if(System.getProperty("org.daisy.debug")!=null){
		  mDebugState = true;
	  }
	}
	
	
	/**
	 * Produce an implementation of a {@linkorg.daisy.util.fileset.validation.Validator} for the inparam fileset type.
	 * <p>The implementation discovery process is performed as follows:</p>
	 * <ul>
	 * <li>
	 *   First, System properties are checked for identifiers. The property string use syntax :
	 *	 <code>org.daisy.util.fileset.validation:http://www.daisy.org/fileset/FILESETTYPE_STRING_CONSTANT</code>,
	 *   where FILESETTYPE_STRING_CONSTANT equals a toString constant in {@link org.daisy.util.fileset.FilesetType}.
	 * </li>
	 * <li>
	 *   If no matching System property is found, a ValidatorNotSupportedException is thrown.
	 * </li>
	 * </ul>
	 */
	public Validator newValidator(FilesetType type) throws ValidatorNotSupportedException {		
		Validator val = null;
		
		StringBuilder sb = new StringBuilder(mSystemPropertyConstant);
		sb.append(type.toString());		
		String sysprop = System.getProperty(sb.toString());
		
		if(sysprop!=null) {
			try {
				Class klass = Class.forName(sysprop);
				Object o = klass.newInstance();
	            if(o instanceof Validator) {
	                val = (Validator)o;
	            }   				
			} catch (Throwable t) {
				if(mDebugState) {
					System.out.println("DEBUG: ValidatorFactory.newValidator(FilesetType) Exception");
				}
			} 			
		}else{
			//system properties does not identify an impl						
		}
		
		if(null==val) {
			//TODO continue discovery. eg C:\Program\Java\sources\javax\xml\validation\SchemaFactoryFinder.java
			throw new ValidatorNotSupportedException("A validator for " + type.toNiceNameString() + " could not be located.");
		}
						
		//after discovery, polish and return
		if(null!=val) {
			//propagate any features and properties to the produced instance
			val.setListener(mValidatorListener);
			return val;
		}
		
		//we couldnt produce an implementation
		throw new ValidatorNotSupportedException(type.toString()); 					
						
	}
	
	/**
	 * Register a ValidatorListener with this factory.
	 * A ValidatorListener registered here will be
	 * propagated to Validators generated by the factory.
	 */
	public void setValidatorListener(ValidatorListener errh) {
		this.mValidatorListener = errh;
	}
	
	/**
	 * @return the registered ValidatorListener, or null if none registered.
	 */
	public ValidatorListener getValidatorListener() {
		return mValidatorListener;
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