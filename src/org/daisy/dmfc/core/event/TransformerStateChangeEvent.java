package org.daisy.dmfc.core.event;

import org.daisy.dmfc.core.transformer.Transformer;

/**
 * An event raised when a {@link org.daisy.dmfc.core.transformer.Transformer} object within the Pipeline has started or stopped.
 * @author Markus Gylling
 */
public class TransformerStateChangeEvent extends StateChangeEvent {

	public TransformerStateChangeEvent(Transformer source, Status state) {
		super(source, state);
	}

	private static final long serialVersionUID = 3683114276276735596L;
	
}
