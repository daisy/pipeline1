package org.daisy.util.fileset.validation;

import java.net.URI;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.delegate.impl.InnerDocURICheckerDelegate;
import org.daisy.util.fileset.validation.delegate.impl.InterDocURICheckerDelegate;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

/**
 * A Fileset Validator for XHTML docs; only adds URI checking atm.
 * @author Markus Gylling
 */
public class ValidatorImplXhtml10 extends ValidatorImplAbstract implements Validator {

	ValidatorImplXhtml10() {
		super(FilesetType.XHTML_DOCUMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources();
		super.validate(fileset);
		validate();
	}


	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources();
		super.validate(manifest);
		validate();
	}
	
	/**
	 * Perform additional validation other than that done through the call 
	 * to super.validate (which executes any schemas and delegates registered on super)
	 */
	private void validate() throws ValidatorException, ValidatorNotSupportedException {

	}
	
	/**
	 * Set validation resources (schemas, delegates) that are hardcoded for this Fileset type.
	 */
	private void setStaticResources() throws ValidatorException{
		try{			
			//delegates:								
			setDelegate(new InterDocURICheckerDelegate());									
			setDelegate(new InnerDocURICheckerDelegate());
			
		}catch (Exception e) {
			throw new ValidatorException(e.getMessage(),e);
		}
	}
	
}
