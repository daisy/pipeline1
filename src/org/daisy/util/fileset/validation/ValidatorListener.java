package org.daisy.util.fileset.validation;

import org.daisy.util.fileset.validation.message.ValidatorMessage;

/**
 * Handler interface for recieving messages from a {@link org.daisy.util.fileset.validation.Validator}.
 * @author Markus Gylling
 */
public interface ValidatorListener {

	/**
	 * Receive messages issued by the Validator regarding encountered invalid states in the fileset being validated.
	 * <p>Message nature is determined by the particular subclass of ValidatorMessage that is received;
	 * messages are always instances of {@link org.daisy.util.fileset.validation.message.ValidatorMessage} but are typically 
	 * subclassed to {@link org.daisy.util.fileset.validation.message.ValidatorWarningMessage}, 
	 * {@link org.daisy.util.fileset.validation.message.ValidatorErrorMessage} or 
	 * {@link org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage}.
	 * </p>
	 * <p>Messages on failures-to-validate states are not issued here but in the <code>exception</code> method. 
	 * The <code>report</code> method issues only messages that are unambigous indicators of an invalid state in a fileset.</p>
	 * @param validator The Validator that sends this message.
	 * @param message The message sent.
	 */
	public void report(Validator validator, ValidatorMessage message);
	
	/**
	 * Receive non-critical informational messages issued by the Validator. 
	 * <p>These messages are decorative in nature and do not contain any information on the validity status 
	 * of the fileset being validated, nor information
	 * on failures-to-validate. A typical use of this method is to give surrounding information to a human user.</p> 
	 */
	public void inform(Validator validator, String information);	
	
	/**
	 * Receive information on exceptions that the Validator caught during its execution. 
	 * <p>A Validator only uses this method when it catches an exception and decides to
	 * try to continue the validation process. In the circumstance when a Validator decides
	 * to abort the Validation process due to an exception, it will <strong>not</strong> invoke this method before
	 * abortion, but directly throw an Exception through the .validate() method on which it was invoked.</p>
	 */
	public void exception(Validator validator, Exception e);	
	
	/**
	 * Receive a progress indicator from the Validator.
	 * <p>Values are always between 0 and 1.</p>
	 * <p>Note - it is neither guaranteed that Validators 
	 * will emit this information, nor that its reliable
	 * if emitted.</p>
	 */
	public void progress(Validator validator, double progress);
	
}
