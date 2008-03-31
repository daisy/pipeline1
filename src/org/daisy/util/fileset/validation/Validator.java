package org.daisy.util.fileset.validation;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.stream.events.Attribute;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegate;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotRecognizedException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

public interface Validator {

	/**
	 * Is validation of the inparam fileset supported by this Validator?
	 */
	public boolean isFilesetTypeSupported(FilesetType type);
	
	/**
	 * Retrieve a list of fileset types that this Validator supports validation of.
	 */
	public List getSupportedFilesetTypes();
	
	/**
	 * Register a ValidatorListener with this validator.	 
	 */
	public void setListener(ValidatorListener listener);
	
	/**
	 * @return a registered ValidatorListener, or null if none is registered.
	 */
	public ValidatorListener getListener();
	
	/**
	 * Validate a fileset.
	 * <p>This method is typically used when a fileset instance has already been created.</p>
	 * <p>Any user of this method should make sure to reroute errors from FilesetErrorHandler during 
	 * fileset instantiation (prior to the validator doing its own
	 * job) to the ValidatorListener.</p>
	 * @param fileset Fileset to validate
	 * @throws ValidatorException if the validation did not complete due to a nonrecoverable problem
	 * @throws ValidatorNotSupportedException if this validator does not support validation of the inparam fileset type
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException;
	
	/**
	 * Validate a fileset.
	 * <p>This method is typically used when a fileset instance has not yet been created.</p>
	 * <p>Errors reported during fileset instantiation (prior to the validator doing its own
	 * job) will be rerouted to the ValidatorListener.</p>
	 * @param manifest URI to main file of the fileset to validate
	 * @throws ValidatorException if the validation did not complete due to a nonrecoverable problem
	 * @throws ValidatorNotSupportedException if this validator does not support validation of the inparam fileset type
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException;
	
	/**
	 * Reset this Validator to its initial state. A validator that
	 * has been reset can be reused.
	 */
	public void reset();

	/**
	 * Retrieve a handle to the fileset currently registered with this Validator.
	 */
	public Fileset getFileset();
	
	public void setFeature(String name, boolean value) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;
	
	public boolean getFeature(String name) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;
    	
	public void setProperty(String name, Object object) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;

	public Object getProperty(String name) throws ValidatorNotRecognizedException, ValidatorNotSupportedException;

	/**
	 * Register a {@link org.daisy.util.fileset.validation.delegate.ValidatorDelegate} instance with this Validator. 
	 * <p>This method can be called several times to register multiple delegates.</p> 
	 * <p>Note: a Fileset must be registered with this Validator before this method is called, or a ValidatorException is thrown.</p>
	 * <p>Note: at {@link #reset()}, the list of registered delegates is also reset.</p>
	 */
	public void setDelegate(ValidatorDelegate delegate) throws ValidatorException, ValidatorNotSupportedException;
	
	/**
	 * Register a {@link org.daisy.util.fileset.validation.delegate.ValidatorDelegate} instance with this Validator by reffering to its qualified name. 
	 * <p>This method can be called several times to register multiple delegates.</p> 
	 * <p>Note: a Fileset must be registered with this Validator before this method is called, or a ValidatorException is thrown.</p>
	 * <p>Note: at {@link #reset()}, the list of registered delegates is also reset.</p>
	 */
	public void setDelegate(String delegateClassName) throws ValidatorException, ValidatorNotSupportedException;

	/**
	 * Add a Schema Source (RelaxNG, Schematron, WXS, compound) to apply to certain members of the Fileset.
	 * @param schema the URL of the schema
	 * @param filesetFileType the particular FilesetFile subclass to apply the Schema on, expressed as a fully qualified class name.  
	 * @see #setSchema(URL, String, Set)
	 */
	public void setSchema(URL schema, String filesetFileType) throws ValidatorException, ValidatorNotSupportedException;
	
	/**
	 * Add a Schema Source (RelaxNG, Schematron, WXS, compound) to apply to certain members of the Fileset.
	 * <p>This three-param method is used for example to apply different schemas to different versions of the same document type.</p>
	 * <p>All Attribute objects in the rootAttributes set must occur on the instance; but not vice versa.</p>
	 * @param schema the URL of the schema
	 * @param filesetFileType the particular FilesetFile subclass to apply the Schema on, expressed as a fully qualified class name.  
	 * @param rootAttributes a set of attributes that must occur on the root element of the validated instance. May be null. 
	 * @see #setSchema(URL, String)
	 *  
	 */
	public void setSchema(URL schema, String filesetFileType, Set<Attribute> rootAttributes) throws ValidatorException, ValidatorNotSupportedException;

}
