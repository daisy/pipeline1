/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
    private Stack bundles = new Stack();
	
    public I18n() {
	}
    
	public I18n(ResourceBundle a_bundle) {
	    bundles.push(a_bundle);
	}
	
	public static void setDefaultBundle(ResourceBundle a_bundle) {
	    defaultBundle = a_bundle;
	}
	
	public void addBundle(ResourceBundle a_bundle) {
	    bundles.push(a_bundle);
	}
	
	public String format(String a_msgId, Object[] a_params) {
	    try {
		    for (Iterator _iter = bundles.iterator(); _iter.hasNext(); ) {
		        try {
			        ResourceBundle _bundle = (ResourceBundle)_iter.next();
			        String _msg = _bundle.getString(a_msgId);
			        return MessageFormat.format(_msg, a_params);
		        } catch (MissingResourceException e) {
		            // Nothing
		        }
		    }
		    if (defaultBundle != null) {
		        try {		        
			        String _msg = defaultBundle.getString(a_msgId);
			        return MessageFormat.format(_msg, a_params);
		        } catch (MissingResourceException e) {
		            // Nothing
		        }
		    }
	    } catch (IllegalArgumentException e) {
	        return "<wrong format of resource in bundle> " + a_msgId;
	    }	    
	    return "<missing resource> " + a_msgId;	    
	}
	
	public String format(String a_msgId) {		
		return format(a_msgId, null);
	}
}
