package int_daisy_mixedContentNormalizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 *
 * @author Markus Gylling
 */
public abstract class AbstractNormalizer {
		
	protected TransformerDelegateListener mTransformer  = null;
	protected int mModCount = 0;
	
	
	public AbstractNormalizer(TransformerDelegateListener tdl) {				
		mTransformer = tdl;		
	}

	/**
	 * Perform mixed content normalization on source, and return the result.
	 */
	public abstract Result normalize(Source source) throws TransformerRunException;
	    
	/**
	 * @return the number of modifications (element inserts) that was performed during the normalization.
	 */
	public int getNumberOfModifications() {
		return mModCount;
	}
				
}
