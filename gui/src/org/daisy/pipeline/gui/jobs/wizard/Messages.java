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
package org.daisy.pipeline.gui.jobs.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.jobs.wizard.messages"; //$NON-NLS-1$
	/** Label of the dialog help tray close button */
	public static String helpTray_close;
	/** Tooltip of the dialog help tray close button */
	public static String helpTray_close_tooltip;
	/** Description of the parameter configuration wizard page */
	public static String page_param_description;
	/** Error message displayed when a script parameter is invalid */
	public static String page_param_error_invalid;
	/** Label of the group of optional script parameters */
	public static String page_param_optionalGroup;
	/** Label of the group of required script parameters */
	public static String page_param_requiredGroup;
	/** Title of the parameter configuration wizard page */
	public static String page_param_title;
	/** Description of the script selection wizard page */
	public static String page_script_description;
	/** Error message when a script has not been properly loaded */
	public static String page_script_error_unhandledScript;
	/** Title of the script selection wizard page */
	public static String page_script_title;
	/** Title of the new job wizard */
	public static String wizard_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
