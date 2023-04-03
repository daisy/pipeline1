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
package org.daisy.pipeline.gui;

/**
 * Stores IDs of commands, command parameters and command categories as used in
 * plugin.xml.
 * 
 * @author Romain Deltour
 * 
 */
public interface ICommandConstants {
	/* Categories */
	/** The category for commands related to running jobs */
	public static final String RUN_CATEGORY = "org.daisy.pipeline.gui.command.category.run";

	/* Commands */
	/** The command to cancel jobs */
	public static final String CANCEL_CMD = "org.daisy.pipeline.gui.command.cancel";
	/** The command to cancel all jobs */
	public static final String CANCEL_ALL_CMD = "org.daisy.pipeline.gui.command.cancelAll";
	/** The command to clear finished jobs */
	public static final String CLEAR_FINISHED_CMD = "org.daisy.pipeline.gui.command.clearFinished";
	/** The command to move down jobs in the queue */
	public static final String MOVE_DOWN_CMD = "org.daisy.pipeline.gui.command.moveDown";
	/** The command to move jobs to the bottom of the queue */
	public static final String MOVE_TO_BOTTOM_CMD = "org.daisy.pipeline.gui.command.moveToBottom";
	/** The command to move jobs to the top of the queue */
	public static final String MOVE_TO_TOP_CMD = "org.daisy.pipeline.gui.command.moveToTop";
	/** The command to move up jobs in the queue */
	public static final String MOVE_UP_CMD = "org.daisy.pipeline.gui.command.moveUp";
	/** The command to create a new item (such as a job) */
	public static final String NEW_ITEM_CMD = "org.daisy.pipeline.gui.command.newItem";
	/** The command to run the selected job */
	public static final String RUN_CMD = "org.daisy.pipeline.gui.command.run";
	/** The command to run all jobs */
	public static final String RUN_ALL_CMD = "org.daisy.pipeline.gui.command.runAll";
	/** The command to toggle to the browser widget */
	public static final String TOGGLE_BROWSER_CMD = "org.daisy.pipeline.gui.command.toggleBrowser";
	/** The command to show the documentation perspective */
	public static final String SHOW_DOC_CMD = "org.daisy.pipeline.gui.command.showDoc";
	/** The parameter of the show doc command to handle warning dialog display */
	public static final String SHOW_DOC_PARAM_WARNING = "org.daisy.pipeline.gui.doc.showDoc.handleWarning"; //$NON-NLS-1$
	/** The command to call the software update wizard */
	public static final String UPDATE_CMD = "org.daisy.pipeline.gui.command.update";

}
