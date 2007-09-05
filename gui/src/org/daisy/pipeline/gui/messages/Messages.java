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
package org.daisy.pipeline.gui.messages;

import org.daisy.pipeline.core.event.MessageEvent.Cause;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.messages.messages"; //$NON-NLS-1$
	/** The title of the clear messages action */
	public static String action_clearMessages;
	/** The title of the export messages action */
	public static String action_export;
	/** The confirmation question of the export action */
	public static String action_export_confirm;
	/** The title of the action that brings the filter configuration dialog */
	public static String action_filter;
	/** The title of the scroll lock action */
	public static String action_scrollLock;
	/** The label of the 'input' message cause */
	public static String cause_input;
	/** The title of the button to filter 'input' cause */
	public static String cause_input_filter;
	/** The label of the 'system' message cause */
	public static String cause_system;
	/** The title of the button to filter 'system' cause */
	public static String cause_system_filter;
	/** The label of the 'deselect all' button */
	public static String dialog_filter_button_deselectAll;
	/** The label of the 'select all' button */
	public static String dialog_filter_button_selectAll;
	/** The label of the 'severity' group in the filter dialog */
	public static String dialog_filter_severityGroup;
	/** The title of the filter dialog */
	public static String dialog_filter_title;
	/** The label of the 'types' group in the filter dialog */
	public static String dialog_filter_typesGroup;
	/** The label of the 'core' group-by category */
	public static String groupBy_category_core;
	/** The label of the 'cause' group-by category */
	public static String groupBy_categorySet_cause;
	/** The label of the 'job' group-by category */
	public static String groupBy_categorySet_job;
	/** The label of the 'type' group-by category */
	public static String groupBy_categorySet_type;
	/** The header of the message column */
	public static String heading_message;
	/** The header of the type column */
	public static String heading_type;
	/** The string used to display extend location information */
	public static String location_extended;
	/** The string used to display location information about file only */
	public static String location_file;
	/** The string used to display location information about file and line */
	public static String location_fileAndLine;
	/** The string used to display location information about file, line, column */
	public static String location_fileAndColumnAndLine;
	/** The name of the group-by sub menu */
	public static String menu_groupBy;
	/** The label of the 'debug' message type */
	public static String type_debug;
	/** The name of the button to filter 'debug' message type */
	public static String type_debug_filter;
	/** The label of the 'error' message type */
	public static String type_error;
	/** The name of the button to filter 'error' message type */
	public static String type_error_filter;
	/** The label of the 'info' message type */
	public static String type_info;
	/** The name of the button to filter 'info' message type */
	public static String type_info_filter;
	/** The label of the 'warning' message type */
	public static String type_warning;
	/** The name of the button to filter 'warning' message type */
	public static String type_warning_filter;
	/** The name of the UI job used to refresh the message table */
	public static String uiJob_addMessage_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Returns the localized tooltip to use in the filter dialog for the given
	 * message cause.
	 * 
	 * @param cause
	 *            A message cause
	 * @return the tooltip to use in the filter dialog for the given message
	 *         cause
	 */
	public static String getFilterTip(Cause cause) {
		switch (cause) {
		case INPUT:
			return cause_input_filter;
		case SYSTEM:
			return cause_system_filter;
		default:
			return "!err!"; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the localized tooltip to use in the filter dialog for the given
	 * message type.
	 * 
	 * @param type
	 *            A message type
	 * @return the tooltip to use in the filter dialog for the given message
	 *         type
	 */
	public static String getFilterTip(Type type) {
		switch (type) {
		case DEBUG:
			return type_debug_filter;
		case ERROR:
			return type_error_filter;
		case INFO:
			return type_info_filter;
		case WARNING:
			return type_warning_filter;
		default:
			return "!err!"; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the localized name of the given message cause.
	 * 
	 * @param cause
	 *            A message cause
	 * @return the localized name of the given message cause
	 */
	public static String getName(Cause cause) {
		switch (cause) {
		case INPUT:
			return cause_input;
		case SYSTEM:
			return cause_system;
		default:
			return "!err!"; //$NON-NLS-1$
		}
	}

	/**
	 * Returns the localized name of the given message type.
	 * 
	 * @param type
	 *            A message type
	 * @return the localized name of the given message type
	 */
	public static String getName(Type type) {
		switch (type) {
		case DEBUG:
			return type_debug;
		case ERROR:
			return type_error;
		case INFO:
			return type_info;
		case WARNING:
			return type_warning;
		default:
			return "!err!"; //$NON-NLS-1$
		}
	}

	private Messages() {
	}
}
