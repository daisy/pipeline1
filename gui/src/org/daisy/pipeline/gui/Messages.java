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
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String message_coreReload;
    public static String menu_edit;
    public static String menu_file;
    public static String menu_help;
    public static String menu_new;
    public static String menu_window;
    public static String menu_window_navigation;
    public static String menu_window_openPerspective;
    public static String menu_window_showView;
    public static String pref_lamePath_label;
    public static String pref_lamePath_tooltip;
    public static String pref_pythonPath_label;
    public static String pref_pythonPath_tooltip;
    public static String pref_tempDirPath_label;
    public static String pref_tempDirPath_tooltip;
    public static String window_title;
}
