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
package org.daisy.pipeline.gui.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.messages.messages"; //$NON-NLS-1$
    public static String action_clearMessages;
    public static String action_export;
    public static String action_export_confirm;
    public static String action_filter;
    public static String action_scrollLock;
    public static String dialog_filter_button_deselectAll;
    public static String dialog_filter_button_selectAll;
    public static String dialog_filter_severityGroup;
    public static String dialog_filter_title;
    public static String dialog_filter_typesGroup;
    public static String groupBy_category_core;
    public static String groupBy_categorySet_cause;
    public static String groupBy_categorySet_job;
    public static String groupBy_categorySet_type;
    public static String heading_message;
    public static String heading_type;
    public static String location_file;
    public static String location_fileAndLine;
    public static String location_fileAndColumnAndLine;
    public static String menu_groupBy;
    public static String uiJob_addMessage_name;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
