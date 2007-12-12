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

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.messages"; //$NON-NLS-1$
	/** The accessible name of the view drop-down menu */
	public static String accessibleName_viewMenu;
	/** Message warning the the core is being reloaded */
	public static String message_reloadCore;
	/** The title of the edit menu */
	public static String menu_edit;
	/** The title of the file menu */
	public static String menu_file;
	/** The title of the help menu */
	public static String menu_help;
	/** The title of the new sub-menu */
	public static String menu_new;
	/** The title of the window menu */
	public static String menu_window;
	/** The title of the navigation sub-menu */
	public static String menu_window_navigation;
	/** The title of the open perspective sub-menu */
	public static String menu_window_openPerspective;
	/** The title of the open show view sub-menu */
	public static String menu_window_showView;
	/** The label presenting the preference for the path to ImageMagick */
	public static String pref_imageMagickPath_label;
	/** The tooltip for the preference for the path to ImageMagick */
	public static String pref_imageMagickPath_tooltip;
	/** The label presenting the preference for the path to Lame */
	public static String pref_lamePath_label;
	/** The tooltip for the preference for the path to Lame */
	public static String pref_lamePath_tooltip;
	/** The label presenting the preference for the path to Python */
	public static String pref_pythonPath_label;
	/** The tooltip for the preference for the path to Python */
	public static String pref_pythonPath_tooltip;
	/** The label presenting the preference for the path to SoX */
	public static String pref_soxPath_label;
	/** The tooltip for the preference for the path to SoX */
	public static String pref_soxPath_tooltip;
	/**
	 * The label presenting the preference for the path to the temporary
	 * directory
	 */
	public static String pref_tempDirPath_label;
	/** The tooltip for the preference for the path to the temporary directory */
	public static String pref_tempDirPath_tooltip;
	/** The title of the Pipeline GUI main window */
	public static String window_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
