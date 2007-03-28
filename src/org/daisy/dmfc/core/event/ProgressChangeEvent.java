package org.daisy.dmfc.core.event;

/**
 * An event raised when an object within the Pipeline changes its progress.
 * @author Markus Gylling
 */
public class ProgressChangeEvent extends SystemEvent {
	private double mProgress;
	
	/**
	 * @param progress A value between 0 and 1 inclusive.
	 * @throws IllegalArgumentException if progress is not between 0 and 1 inclusive. 
	 */
	public ProgressChangeEvent(Object source, double progress) {
		super(source);
		if(progress<0.0||progress>1.0) {
			throw new IllegalArgumentException(
					Double.toString(progress) + " must be a value between 0 and 1");
		}
		mProgress = progress;
	}
	
	/**
	 * @return a value between 0 and 1 inclusive.
	 */
	public double getProgress() {
		return mProgress;
	}

	private static final long serialVersionUID = -5413716769871123121L;		
	
}