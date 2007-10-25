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
public abstract class AbstractSyncPointLocator implements BusListener {
		
	protected TransformerDelegateListener mTransformer  = null;
	protected int mSyncPointCount = 0;
	protected boolean mUserAbort = false;
	
	public AbstractSyncPointLocator(TransformerDelegateListener pl) {				
		mTransformer = pl;
		EventBus.getInstance().subscribe(this, UserAbortEvent.class);
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
				
	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.event.BusListener#received(java.util.EventObject)
	 */
	public void received(EventObject event) {
		if(event instanceof UserAbortEvent)
			mUserAbort = true;		
	}
}
