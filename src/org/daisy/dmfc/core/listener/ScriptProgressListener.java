package org.daisy.dmfc.core.listener;

import java.util.EventListener;

import org.daisy.dmfc.core.script.Script;

/**
 * An implementor of this interface will recieve events on script 
 * start and script end. <p>TransformerProgressListener is a sister
 * interface to this interface; callbacks to TransformerProgressListener are made
 * <em>after</em> a scriptStart event and <em>until</em> a scriptEnd event.</p>
 * @author Markus Gylling
 */
public interface ScriptProgressListener extends EventListener {

	/**
	 * Recieve a notification on script start.
	 * @param script the URL of the script that is starting 
	 */	
    public void scriptStart(Script script);

    
	/**
	 * Recieve a notification on script end.
	 * If script execution fails, a ScriptException will be thrown, and this method
	 * will not be called.
	 * @param script the URL of the script that is ending
	 */	
    public void scriptEnd(Script script);
        
    
}
