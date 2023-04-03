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
package org.daisy.pipeline.gui.doc;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.doc.messages"; //$NON-NLS-1$
	/** The name of the sync ToC action */
	public static String action_synchronize;
	/** The tooltip of the sync ToC action */
	public static String action_synchronize_tooltip;
	/** The message of the confirm dialog to show the doc perspective */
	public static String dialog_showDoc_message;
	/** The title of the confirm dialog to show the doc perspective */
	public static String dialog_showDoc_title;
	/** The toggle message of the confirm dialog to show the doc perspective */
	public static String dialog_showDoc_toggle;
	/** The error message to display when the browser couldn't be created */
	public static String error_noBrowser;
	/** the title of the user guide ToC tab */
	public static String tab_help;
	/** the tooltip of the user guide ToC tab */
	public static String tab_help_tooltip;
	/** the title of the script ToC tab */
	public static String tab_script;
	/** the tooltip of the script ToC tab */
	public static String tab_script_tooltip;
	/** the title of the transformers ToC tab */
	public static String tab_transformers;
	/** the tooltip of the transformers ToC tab */
	public static String tab_transformers_tooltip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
