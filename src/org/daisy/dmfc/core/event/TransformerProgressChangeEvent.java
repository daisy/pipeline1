package org.daisy.dmfc.core.event;

import org.daisy.dmfc.core.transformer.Transformer;

/**
 * An event raised when a Transformer within the Pipeline changes its progress.
 * @author Markus Gylling
 */
public class TransformerProgressChangeEvent extends ProgressChangeEvent {

	public TransformerProgressChangeEvent(Transformer source, double progress) {
		super(source, progress);
	}

}
