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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Linus Ericson
 */
public class I18n {
	
	private ResourceBundle bundle;
	
	public I18n(ResourceBundle a_bundle) {
		bundle = a_bundle;
	}
	
	public String format(String a_msgId, Object[] a_params) {		
		try {
			if (bundle == null) {
				return "<resource bundle missing> " + a_msgId;
			}
			String _msg = bundle.getString(a_msgId);
			return MessageFormat.format(_msg, a_params);
		} catch (MissingResourceException e) {
			return "<missing resource in bundle> " + a_msgId;
		}
	}
	
	public String format(String a_msgId) {		
		return format(a_msgId, null);
	}
}
