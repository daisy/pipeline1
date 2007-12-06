package int_daisy_recorder2dtb.read;

import int_daisy_recorder2dtb.InputType;

import java.net.URL;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.dtb.ncxonly.model.Model;

/**
 * Abstract base for concrete readers of playlists or marker files.
 * @author Markus Gylling
 */
public abstract class Reader {

	protected URL mInputURL = null;
	protected TransformerDelegateListener mTransformer = null;
		
	/**
	 * Constructor used during factory discovery.
	 */
	public Reader() {
		
	}
	
	/**
	 * Primary constructor.
	 * @param file
	 * @param tdl
	 */
	public Reader(URL file, TransformerDelegateListener tdl) {
		mInputURL = file;
		mTransformer = tdl;
	}
	
	/**
	 * Parse the input resource that was set in constructor, create and return a Model instance. 
	 */
	public abstract Model createModel() throws TransformerRunException;
		
	/**
	 * Factory qeury: is inparam resource of a type that this Reader supports?
	 */
	public abstract boolean supports(URL u);
	
	/**
	 * Get the type of playlist/marker file that this Reader supports.
	 */
	public abstract InputType getSupportedInputType();
	
	
}
