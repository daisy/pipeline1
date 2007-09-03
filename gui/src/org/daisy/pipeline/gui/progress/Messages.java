/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.pipeline.gui.progress;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.progress.messages"; //$NON-NLS-1$
	/**
	 * The tooltip of the button to cancel the job displayed in the progress
	 * view
	 */
	public static String button_cancel_tooltip;
	/**
	 * The text to display in the progress view when no job is selected.
	 */
	public static String label_noJob;
	/** The label for the "running" state */
	public static String label_state_running;
	/** The label for the "done" state */
	public static String label_state_done;
	/** The label for the timing information when the job is finished */
	public static String state_timeDone;
	/** The label for the timing information when the job is running */
	public static String state_timeRunning;
	/** The title of the progress refresh thread */
	public static String uiJob_progressUpdate;
	/** The title of the task refresh thread */
	public static String uiJob_taskUpdate_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
