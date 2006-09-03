package org.daisy.util.fileset.validation;

import java.net.URI;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

/**
 * A bridge for the ZedVal library to realize a Z39.86 {@link org.daisy.util.fileset.validation.Validator}
 * @author Markus Gylling
 */
class ValidatorImplZedVal extends ValidatorImplAbstract implements Validator  {

	/**
	 * Constructor.
	 */
	ValidatorImplZedVal(){
		super(FilesetType.Z3986);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		super.validate(fileset);
		validate();	
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		super.validate(manifest);
		validate();		
	}

	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		throw new ValidatorNotSupportedException("not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		super.reset();
		//TODO reset local member vars
	}
}