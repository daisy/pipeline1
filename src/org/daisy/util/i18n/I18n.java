/*
 * Created on 2005-mar-23
 */
package org.daisy.util.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author LINUSE
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
