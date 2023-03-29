/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui;

/**
 * Defines constants for the paths to the icons used in the Pipeline GUI.
 * 
 * @author Romain Deltour
 */
public interface IIconsKeys {

	/** The path to the icons directory (relative to the plugin home) */
	public static final String ICON_DIR = "icons/"; //$NON-NLS-1$
	/** The path to the "close" icon (relative to the plugin home) */
	public static final String ACTION_CLOSE = ICON_DIR + "action-close.gif"; //$NON-NLS-1$
	/** The path to the "stop" icon (relative to the plugin home) */
	public static final String ACTION_STOP = ICON_DIR + "action-stop.gif"; //$NON-NLS-1$
	/** The path to the "clear" icon (relative to the plugin home) */
	public static final String CLEAR_FINISHED = ICON_DIR
			+ "action-clear-finished.gif"; //$NON-NLS-1$
	/** The path to the "synchronize ToC" icon (relative to the plugin home) */
	public static final String HELP_SYNCHRONIZE = ICON_DIR
			+ "help-synchronize.gif"; //$NON-NLS-1$
	/** The path to the "ToC item" icon (relative to the plugin home) */
	public static final String HELP_TOC_ITEM = ICON_DIR + "help-toc-item.gif"; //$NON-NLS-1$
	/** The path to the "ToC section" icon (relative to the plugin home) */
	public static final String HELP_TOC_SECTION = ICON_DIR
			+ "help-toc-section.gif"; //$NON-NLS-1$
	/** The path to the "ToC subsection" icon (relative to the plugin home) */
	public static final String HELP_TOC_SUBSECTION = ICON_DIR
			+ "help-toc-subsection.gif"; //$NON-NLS-1$
	/** The path to the "clear message" icon (relative to the plugin home) */
	public static final String MESSAGE_CLEAR = ICON_DIR + "message-clear.gif"; //$NON-NLS-1$
	/** The path to the "debug message" icon (relative to the plugin home) */
	public static final String MESSAGE_DEBUG = ICON_DIR + "message-debug.gif"; //$NON-NLS-1$
	/** The path to the "error message" icon (relative to the plugin home) */
	public static final String MESSAGE_ERROR = ICON_DIR + "message-error.gif"; //$NON-NLS-1$
	/** The path to the "export messages" icon (relative to the plugin home) */
	public static final String MESSAGE_EXPORT = ICON_DIR + "message-export.gif"; //$NON-NLS-1$
	/** The path to the "filter debug" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_DEBUG = ICON_DIR
			+ "message-filter-debug.gif"; //$NON-NLS-1$
	/** The path to the "filter error" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_ERROR = ICON_DIR
			+ "message-filter-error.gif"; //$NON-NLS-1$
	/** The path to the "filter input" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_INPUT = ICON_DIR
			+ "message-filter-input.gif"; //$NON-NLS-1$
	/** The path to the "filter info" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_INFO = ICON_DIR
			+ "message-filter-info.gif"; //$NON-NLS-1$
	/** The path to the "filter info finer" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_INFO_FINER = ICON_DIR
			+ "message-filter-info-finer.gif"; //$NON-NLS-1$;
	/** The path to the "close" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_SYSTEM = ICON_DIR
			+ "message-filter-system.gif"; //$NON-NLS-1$
	/** The path to the "filter warning" icon (relative to the plugin home) */
	public static final String MESSAGE_FILTER_WARNING = ICON_DIR
			+ "message-filter-warning.gif"; //$NON-NLS-1$
	/** The path to the "info message" icon (relative to the plugin home) */
	public static final String MESSAGE_INFO = ICON_DIR + "message-info.gif"; //$NON-NLS-1$
	/** The path to the "info finer message" icon (relative to the plugin home) */
	public static final String MESSAGE_INFO_FINER = ICON_DIR
			+ "message-info-finer.gif"; //$NON-NLS-1$
	/** The path to the "scroll lock" icon (relative to the plugin home) */
	public static final String MESSAGE_SCROLL_LOCK = ICON_DIR
			+ "message-scroll-lock.gif"; //$NON-NLS-1$
	/** The path to the "warning message" icon (relative to the plugin home) */
	public static final String MESSAGE_WARNING = ICON_DIR
			+ "message-warning.gif"; //$NON-NLS-1$
	/** The path to the "move to bottom" icon (relative to the plugin home) */
	public static final String MOVE_BOTTOM = ICON_DIR + "move-bottom.gif"; //$NON-NLS-1$
	/** The path to the "move down" icon (relative to the plugin home) */
	public static final String MOVE_DOWN = ICON_DIR + "move-down.gif"; //$NON-NLS-1$
	/** The path to the "move to top" icon (relative to the plugin home) */
	public static final String MOVE_TOP = ICON_DIR + "move-top.gif"; //$NON-NLS-1$
	/** The path to the "move up" icon (relative to the plugin home) */
	public static final String MOVE_UP = ICON_DIR + "move-up.gif"; //$NON-NLS-1$
	/** The path to the "canceled" icon (relative to the plugin home) */
	public static final String STATE_CANCELED = ICON_DIR + "state-canceled.gif"; //$NON-NLS-1$
	/** The path to the "failed" icon (relative to the plugin home) */
	public static final String STATE_FAILED = ICON_DIR + "state-failed.gif"; //$NON-NLS-1$
	/** The path to the "finished" icon (relative to the plugin home) */
	public static final String STATE_FINISHED = ICON_DIR + "state-finished.gif"; //$NON-NLS-1$
	/** The path to the "idle" icon (relative to the plugin home) */
	public static final String STATE_IDLE = ICON_DIR + "state-idle.gif"; //$NON-NLS-1$
	/** The path to the "running" icon (relative to the plugin home) */
	public static final String STATE_RUNNING = ICON_DIR + "state-running.gif"; //$NON-NLS-1$
	/** The path to the "waiting" icon (relative to the plugin home) */
	public static final String STATE_WAITING = ICON_DIR + "state-waiting.gif"; //$NON-NLS-1$
	/** The path to the "category" icon (relative to the plugin home) */
	public static final String TREE_CATEGORY = ICON_DIR + "tree-category.gif"; //$NON-NLS-1$
	/** The path to the "collapse all" icon (relative to the plugin home) */
	public static final String TREE_COLLAPSE_ALL = ICON_DIR
			+ "tree-collapse-all.gif"; //$NON-NLS-1$
	/** The path to the "expand all" icon (relative to the plugin home) */
	public static final String TREE_EXPAND_ALL = ICON_DIR
			+ "tree-expand-all.gif"; //$NON-NLS-1$
	/** The path to the "filter tree" icon (relative to the plugin home) */
	public static final String TREE_FILTER = ICON_DIR + "tree-filter.gif"; //$NON-NLS-1$
	/** The path to the "new job wizard" icon (relative to the plugin home) */
	public static final String WIZ_NEW_JOB = ICON_DIR + "wizard-new-job.png"; //$NON-NLS-1$
	/** The path to the "update wizard" icon (relative to the plugin home) */
	public static final String WIZ_UPDATE = ICON_DIR + "wizard-update.gif"; //$NON-NLS-1$

}
