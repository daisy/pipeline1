package org_pef_dtbook2pef.system.tasks.layout.flow;

import java.io.OutputStream;

/**
 * Perform layout of a document to the specified OutputStream
 * @author joha
 *
 */
public interface LayoutPerformer {
	
	/**
	 * Write 
	 * @param output
	 * @throws LayoutPerformerException
	 */
	public void layout(OutputStream output) throws LayoutPerformerException;

}
