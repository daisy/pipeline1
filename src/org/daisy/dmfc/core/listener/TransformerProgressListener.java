package org.daisy.dmfc.core.listener;

import java.util.EventListener;

import org.daisy.dmfc.core.transformer.Transformer;

/** 
 * An implementor of this interface will recieve events 
 * on transformer start, end, and progress. 
 * <p>Since transformers are wrapped within scripts, these callbacks are only
 * made within the boundaries of a ScriptProgressListener.scriptStart and a
 * ScriptProgressListener.scriptEnd callback.</p>
 *  
 * @author Markus Gylling
 */
public interface TransformerProgressListener extends EventListener {

	/**
	 * Recieve a notification on transformer start.
	 * @param transformer the Transformer object that is starting 
	 */	
    public void transformerStart(Transformer transformer);

	/**
	 * Recieve a notification on transformer end.
	 * @param transformer the Transformer object that is ending 
	 */	
    public void transformerEnd(Transformer transformer);
    
	/**
	 * Recieve a notification on transformer progress.
	 * @param progress a value between 0 and 1
	 * @param transformer the Transformer emitting the progress value 
	 */	    
    public void transformerProgress(double progress, Transformer transformer);

	
}
