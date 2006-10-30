package org.daisy.util.fileset.validation.delegate;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

/**
 * An inferface for performing delegated fileset validation tasks.
 * @author Markus Gylling
 */
public interface ValidatorDelegate {
	
	/**
	 * Register the Validator that owns this delegate.
	 */
	public void setValidator(Validator validator);
	
	/**
	 * Execute the task delegated by the Validator. The delegate
	 * will use the Fileset instance registered with the Validator as
	 * the content to execute the task on, and it will use the ValidatorListener
	 * registered with the Validator to report any findings. 
	 */
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException;
	
	/**
	 * Is inparam fileset supported by this ValidatorDelegate?
	 */
	public boolean isFilesetTypeSupported(FilesetType type);
}
