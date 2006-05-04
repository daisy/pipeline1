package org.daisy.dmfc.gui.transformerlist;


import org.daisy.dmfc.core.transformer.TransformerHandler;

public interface ITransformerListViewer {

	/**
	 * Update the view to show that a transformer was added 
	 * to the list.  Not used in this app.
	 * 
	 * @param TransformerHandler
	 */
	public void addTransformer(TransformerHandler th);
	
	/**
	 * Update the view to show that a transformer was removed 
	 * from the list.  Not done in this application.
	 * 
	 * @param transformerHandler
	 */
	public void removeTransformer(TransformerHandler th);
	
	/**
	 * Update the view to reflect the fact that a transformer has finished running.
	 * 
	 * @param task
	 */
	public void updateTransformer(TransformerHandler th);
}
