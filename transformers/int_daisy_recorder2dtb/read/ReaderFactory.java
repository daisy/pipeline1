package int_daisy_recorder2dtb.read;

import int_daisy_recorder2dtb.read.audacity.AudacityAupReader;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Retrieve a reader for a certain playlist/marker type
 * @author Markus Gylling
 */

public class ReaderFactory {
	
	private TransformerDelegateListener mTransformer = null;
	private Set<Class <?extends Reader>> registry = null;
	
	private ReaderFactory(TransformerDelegateListener tdl) {
		mTransformer = tdl;
		//note: runtime registration TODO, this is temporary
		registry = new HashSet<Class <?extends Reader>>();
		registry.add(AudacityAupReader.class);
	}
	
	public static ReaderFactory newInstance(TransformerDelegateListener tdl) {
		return new ReaderFactory(tdl);		
	}
	
	/**
	 * Retrieve a reader for inparam playlist/marker resource
	 * @throws TransformerRunException if no reader could be allocated.
	 */
	public Reader get(URL url) throws TransformerRunException {
		
		for(Class <?extends Reader> cl : registry) {
			try {				
				Reader instance = cl.newInstance();
				if(instance.supports(url)) {
					Constructor constr = cl.getDeclaredConstructor(new Class[] {URL.class, TransformerDelegateListener.class});
					return  (Reader) constr.newInstance(new Object[] {url,mTransformer});					
				}							
			} catch (Exception e) {
				mTransformer.delegateMessage(this, e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
			} 
		}
		throw new TransformerRunException(mTransformer.delegateLocalize("TYPE_NOT_SUPPORTED", new String[]{url.getPath()}));
	}
	
}
