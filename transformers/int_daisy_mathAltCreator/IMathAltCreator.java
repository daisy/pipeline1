package int_daisy_mathAltCreator;

import java.io.File;
import java.util.Map;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;

/**
 * Interface for MathML alttext and altimg providers. 
 * @author Markus Gylling
 */
public interface IMathAltCreator {

	/**
	 * Retrieve a nicename for this service provider, 
	 * for display to users.
	 */
	public String getNiceName();
	
	/**
	 * Configure the service. 
	 * @param input Input File. A DTBook document with unescaped MathML islands. May not be null.
	 * @param output Output File, where altimg and alttext attributes have been added to all MathML islands. May not be null.
	 * @param parameters May be null.
	 * @throws IllegalStateException if the service cannot be run after configuration.
	 */
	public void configure(File input, File output, Map<String, Object> parameters);
	
	/**
	 * Execute the service.
	 * @throws Exception If execution fails.
	 */
	public void execute() throws Exception;
	
	/**
	 * Register a TransformerDelegateListener with this instance
	 */
	public void addListener(TransformerDelegateListener listener);
	
	/**
	 * Remove a TransformerDelegateListener from this instance
	 */
	public void removeListener(TransformerDelegateListener listener);
}
