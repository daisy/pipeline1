package org.daisy.util.fileset.validation.delegate;

import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;

/**
 * An abstract base for ValidatorDelegate that a concrete impl may choose to extend for convenience.
 * @author Markus Gylling
 */
public abstract class ValidatorDelegateImplAbstract implements ValidatorDelegate {

	protected Validator mValidator = null; //the registered Validator
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#setValidator(org.daisy.util.fileset.validation.Validator)
	 */
	public void setValidator(Validator validator) {
		mValidator = validator;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#execute()
	 */
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {
	  //not much generic stuff that can be done here. An overrider will survive without
	  //calling super, but is recommended to do so in case we extend this layer.			
	  if(null==mValidator) throw new ValidatorException("no registered validator");			  
	}
	
	protected final void report(ValidatorMessage message) {
		ValidatorListener listener = mValidator.getListener();
		listener.report(mValidator, message);
	}
		
}
