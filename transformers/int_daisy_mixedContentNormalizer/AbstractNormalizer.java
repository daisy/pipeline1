package int_daisy_mixedContentNormalizer;

import java.util.EventObject;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.event.BusListener;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.UserAbortEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 *
 * @author Markus Gylling
 */
public abstract class AbstractNormalizer implements BusListener {
		
	protected TransformerDelegateListener mTransformer  = null;
	protected int mModCount = 0;
	protected boolean mUserAbort = false;
	
	public AbstractNormalizer(TransformerDelegateListener pl) {				
		mTransformer = pl;
		EventBus.getInstance().subscribe(this, UserAbortEvent.class);
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
			
	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.event.BusListener#received(java.util.EventObject)
	 */
	public void received(EventObject event) {
		if(event instanceof UserAbortEvent)
			mUserAbort = true;		
	}
}
