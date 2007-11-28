package int_daisy_mixedContentNormalizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 *
 * @author Markus Gylling
 */
public abstract class AbstractSyncPointLocator {
		
	protected TransformerDelegateListener mTransformer  = null;
	protected int mSyncPointCount = 0;
		
	public AbstractSyncPointLocator(TransformerDelegateListener tdl) {				
		mTransformer = tdl;		
	}

	/**
	 * Locate sync points in inparam source, and return the result.
	 * <p>Sync points are identified through an attribute identified in config, typically smil:sync="true"</p>
	 */
	public abstract Result locate(Source source) throws TransformerRunException;
	    
	/**
	 * Retrieve the number of sync points that were identified.
	 */
	public int getNumberOfSyncPoints() {
		return mSyncPointCount;
	}
					
}
