/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.core.event;

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