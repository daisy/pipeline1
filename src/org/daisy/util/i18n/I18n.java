/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.i18n;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * @author Linus Ericson
 */
public class I18n {

    private static ResourceBundle defaultBundle = null;
    private Stack<ResourceBundle> bundles = new Stack<ResourceBundle>();

    public I18n() {
    }

    public I18n(ResourceBundle bundle) {
	bundles.push(bundle);
    }

    public static void setDefaultBundle(ResourceBundle bundle) {
	defaultBundle = bundle;
    }

    public void addBundle(ResourceBundle bundle) {
	bundles.push(bundle);
    }

    public String format(String msgId, Object[] params) {
	try {
	    for (Iterator<ResourceBundle> it = bundles.iterator(); it.hasNext();) {
		try {
		    ResourceBundle bundle = it.next();
		    String msg = bundle.getString(msgId);
		    return MessageFormat.format(msg, params);
		} catch (MissingResourceException e) {
		    // Nothing
		}
	    }
	    if (defaultBundle != null) {
		try {
		    String msg = defaultBundle.getString(msgId);
		    return MessageFormat.format(msg, params);
		} catch (MissingResourceException e) {
		    // Nothing
		}
	    }
	} catch (IllegalArgumentException e) {
	    return "<wrong format of resource in bundle> " + msgId;
	}
	return "<missing resource> " + msgId;
    }

    public String format(String msgId) {
	return format(msgId, null);
    }

    public String format(String msgId, Object param) {
	return format(msgId, new Object[] { param });
    }

    public String format(String msgId, Object param1, Object param2) {
	return format(msgId, new Object[] { param1, param2 });
    }

    public String format(String msgId, Object param1, Object param2,
	    Object param3) {
	return format(msgId, new Object[] { param1, param2, param3 });
    }
}
